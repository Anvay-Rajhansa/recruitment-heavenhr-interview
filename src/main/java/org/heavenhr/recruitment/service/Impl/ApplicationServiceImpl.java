package org.heavenhr.recruitment.service.Impl;

import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.event.ApplicationStatusEvent;
import org.heavenhr.recruitment.exception.RecruitmentBusinessException;
import org.heavenhr.recruitment.mapper.ApplicationMapper;
import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.request.UpdateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationCountResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsListResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;
import org.heavenhr.recruitment.repository.ApplicationRepository;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.heavenhr.recruitment.entity.ApplicationStatus.APPLIED;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final OfferRepository offerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, OfferRepository offerRepository,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.applicationRepository = applicationRepository;
        this.offerRepository = offerRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public ApplicationDetailsResponse createApplication(CreateApplicationRequest request) {

        Offer offer = offerRepository.findById(request.getOfferId())
                .orElseThrow(() -> new RecruitmentBusinessException("Offer with given id does not exists."));

        Application application = ApplicationMapper.mapCreateApplicationRequest(request, offer);

        try {
            return ApplicationMapper.mapApplicationToResponse(applicationRepository.save(application));
        } catch (DataIntegrityViolationException ex) {
            LOG.error("DataIntegrityViolationException occurred while creating new application", ex);
            throw new RecruitmentBusinessException("User with given email is already applied for this offer.");
        }
    }


    @Override
    public ApplicationDetailsResponse getApplicationByOfferIdAndApplicationId(Long offerId, Long applicationId) {

        Application application = applicationRepository.findByOfferIdAndApplicationId(offerId, applicationId)
                .orElseThrow(() -> new RecruitmentBusinessException("Application does not exists."));
        return ApplicationMapper.mapApplicationToResponse(application);
    }

    @Override
    public ApplicationDetailsListResponse getAllApplicationsByOfferId(Long offerId) {

        List<Application> applications = applicationRepository.findAllByOfferId(offerId).orElse(Collections.emptyList());
        return ApplicationMapper.mapApplicationListToResponse(applications);
    }

    @Override
    public ApplicationDetailsResponse updateApplication(Long applicationId, UpdateApplicationRequest request) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RecruitmentBusinessException("Application does not exists."));
        ApplicationStatus oldStatus = application.getStatus();

        checkIfStatusUpdateIsAllowed(oldStatus, request.getStatus());

        application.setStatus(request.getStatus());

        Application updatedApplication = applicationRepository.save(application);

        publishStatusChangeNotification(application, oldStatus, request.getStatus());

        return ApplicationMapper.mapApplicationToResponse(updatedApplication);
    }

    @Override
    public ApplicationCountResponse getCountOfApplicationByOfferId(Long offerId) {
        offerRepository.findById(offerId)
                .orElseThrow(() -> new RecruitmentBusinessException("Offer with given id does not exists."));
        int count = applicationRepository.getCountByOfferId(offerId).orElse(0);
        return ApplicationMapper.mapApplicationCountResponse(count);
    }

    private void checkIfStatusUpdateIsAllowed(ApplicationStatus oldStatus, ApplicationStatus newStatus) {
        if (oldStatus == newStatus) {
            throw new RecruitmentBusinessException("Please select different status to update.");
        }

        switch (oldStatus) {
            case APPLIED:
                break;
            case INVITED:
                if (newStatus == APPLIED) {
                    throw new RecruitmentBusinessException("Please select different status to update as candidate is invited.");
                }
                break;
            case REJECTED:
            case HIRED:
                throw new RecruitmentBusinessException("Status update is not allowed as recruitment process is finished for this candidate.");
        }
    }

    private void publishStatusChangeNotification(Application application, ApplicationStatus oldStatus,
                                                 ApplicationStatus newStatus) {
        ApplicationStatusEvent event = new ApplicationStatusEvent(this, application, oldStatus,
                newStatus);
        applicationEventPublisher.publishEvent(event);
    }
}
