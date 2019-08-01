package org.heavenhr.recruitment.service.Impl;

import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.exception.RecruitmentBusinessException;
import org.heavenhr.recruitment.mapper.OfferMapper;
import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.OfferDetailsListResponse;
import org.heavenhr.recruitment.model.response.OfferDetailsResponse;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OfferServiceImpl implements OfferService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferServiceImpl.class);

    private final OfferRepository offerRepository;

    public OfferServiceImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public OfferDetailsResponse CreateOffer(CreateOfferRequest request) {

        checkIfDateIsValid(request);

        Offer offer = OfferMapper.mapCreateOfferRequestToOffer(request);

        try {
            return OfferMapper.mapOfferToResponseObject(offerRepository.save(offer));
        } catch (DataIntegrityViolationException ex) {
            LOG.error("DataIntegrityViolationException occurred while creating new offer", ex);
            throw new RecruitmentBusinessException("Offer with given title already exists.");
        }
    }

    @Override
    public OfferDetailsResponse getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RecruitmentBusinessException("Offer with given id does not exists."));

        return OfferMapper.mapOfferToResponseObject(offer);
    }

    @Override
    public OfferDetailsListResponse getAllOffers() {
        return OfferMapper.mapOfferListToResponseObject(offerRepository.findAll());
    }

    private void checkIfDateIsValid(CreateOfferRequest request) {
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RecruitmentBusinessException("Please select futuristic date.");
        }
    }
}
