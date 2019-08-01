package org.heavenhr.recruitment.service.Impl;

import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.event.ApplicationStatusEvent;
import org.heavenhr.recruitment.exception.RecruitmentBusinessException;
import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.request.UpdateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationCountResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsListResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;
import org.heavenhr.recruitment.repository.ApplicationRepository;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.service.ApplicationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;

public class ApplicationServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ApplicationService applicationService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        applicationService = new ApplicationServiceImpl(applicationRepository, offerRepository,
                applicationEventPublisher);
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_for_invalid_offer_id_while_creating_application() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long offerId = 1L;

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .offerId(offerId)
                .email(email)
                .resumeText(resumeText)
                .build();

        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        //when
        applicationService.createApplication(request);

        //then
        //RecruitmentBusinessException is expected.
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_user_with_email_already_applied_for_job_while_creating_application() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long offerId = 1L;

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .offerId(offerId)
                .email(email)
                .resumeText(resumeText)
                .build();

        Offer offer = Offer.builder().id(offerId).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();
        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        BDDMockito.when(applicationRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate email"));

        //when
        applicationService.createApplication(request);

        //then
        //RecruitmentBusinessException is expected.
    }

    @Test
    public void should_create_application() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long offerId = 1L;

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .offerId(offerId)
                .email(email)
                .resumeText(resumeText)
                .build();

        Offer offer = Offer.builder().id(offerId).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();
        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        Application expectedApplication = Application.builder()
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(ApplicationStatus.APPLIED)
                .build();

        ArgumentCaptor<Application> createApplicationCaptor = ArgumentCaptor.forClass(Application.class);
        BDDMockito.when(applicationRepository.save(any())).thenReturn(expectedApplication);

        //when
        ApplicationDetailsResponse response = applicationService.createApplication(request);

        //then
        Assert.assertEquals(email, response.getEmail());
        Assert.assertEquals(offerId, response.getOffer().getId());
        Assert.assertEquals(resumeText, response.getResumeText());

        BDDMockito.verify(applicationRepository).save(createApplicationCaptor.capture());
        Application application = createApplicationCaptor.getValue();
        Assert.assertEquals(email, application.getEmail());
        Assert.assertEquals(offerId, application.getOffer().getId());
        Assert.assertEquals(resumeText, application.getResumeText());
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_for_invalid_application_id_while_updating_application() {
        //given
        Long invalidId = 123L;
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        BDDMockito.when(applicationRepository.findById(invalidId)).thenReturn(Optional.empty());

        //when
        applicationService.updateApplication(invalidId, request);


        //then
        //RecruitmentBusinessException is expected.
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_try_to_update_same_status_of_application() {
        //given
        Long applicationId = 1L;

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        Offer offer = Offer.builder().id(1L).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application expectedApplication = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email("abc@test.com")
                .resumeText("text")
                .status(ApplicationStatus.APPLIED)
                .build();

        BDDMockito.when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

        //when
        applicationService.updateApplication(applicationId, request);


        //then
        //RecruitmentBusinessException is expected.
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_try_to_update_status_from_INVITED_to_APPLIED_of_application() {
        //given
        Long applicationId = 1L;

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        Offer offer = Offer.builder().id(1L).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application expectedApplication = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email("abc@test.com")
                .resumeText("text")
                .status(ApplicationStatus.INVITED)
                .build();

        BDDMockito.when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

        //when
        applicationService.updateApplication(applicationId, request);


        //then
        //RecruitmentBusinessException is expected.
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_try_to_update_status_of_HIRED_application() {
        //given
        Long applicationId = 1L;

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        Offer offer = Offer.builder().id(1L).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application expectedApplication = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email("abc@test.com")
                .resumeText("text")
                .status(ApplicationStatus.HIRED)
                .build();

        BDDMockito.when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(expectedApplication));

        //when
        applicationService.updateApplication(applicationId, request);


        //then
        //RecruitmentBusinessException is expected.
    }

    @Test
    public void should_update_application_status() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long applicationId = 1L;
        ApplicationStatus status = ApplicationStatus.INVITED;

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .status(status)
                .build();

        Offer offer = Offer.builder().id(1L).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application expectedApplication = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application updatedApplication = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(status)
                .build();

        ArgumentCaptor<Application> updateApplicationCaptor = ArgumentCaptor.forClass(Application.class);
        BDDMockito.when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(expectedApplication));

        BDDMockito.when(applicationRepository.save(any())).thenReturn(updatedApplication);

        ArgumentCaptor<ApplicationStatusEvent> eventCaptor = ArgumentCaptor.forClass(ApplicationStatusEvent.class);

        //when
        ApplicationDetailsResponse response = applicationService.updateApplication(applicationId, request);

        //then
        Assert.assertEquals(email, response.getEmail());
        Assert.assertEquals(resumeText, response.getResumeText());
        Assert.assertEquals(status.name(), response.getStatus());

        BDDMockito.verify(applicationRepository).save(updateApplicationCaptor.capture());
        Application application = updateApplicationCaptor.getValue();
        Assert.assertEquals(email, application.getEmail());
        Assert.assertEquals(resumeText, application.getResumeText());
        Assert.assertEquals(status, application.getStatus());

        BDDMockito.verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        ApplicationStatusEvent event = eventCaptor.getValue();
        Assert.assertEquals(ApplicationStatus.APPLIED, event.getOldStatus());
        Assert.assertEquals(status, event.getNewStatus());
    }

    @Test
    public void should_return_application_by_offer_id_and_application_id() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long offerId = 1L;
        Long applicationId = 1L;

        Offer offer = Offer.builder().id(offerId).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application application = Application.builder()
                .id(applicationId)
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(ApplicationStatus.APPLIED)
                .build();

        BDDMockito.when(applicationRepository.findByOfferIdAndApplicationId(offerId, applicationId)).thenReturn(Optional.of(application));

        //when
        ApplicationDetailsResponse response = applicationService.getApplicationByOfferIdAndApplicationId(offerId, applicationId);

        //then
        Assert.assertEquals(email, response.getEmail());
        Assert.assertEquals(offerId, response.getOffer().getId());
        Assert.assertEquals(resumeText, response.getResumeText());
    }

    @Test
    public void should_return_all_application_by_offer_id() {
        //given
        String email = "abc@test.com";
        String resumeText = "xyz";
        Long offerId = 1L;

        Offer offer = Offer.builder().id(offerId).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();

        Application application = Application.builder()
                .id(1L)
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(ApplicationStatus.APPLIED)
                .build();
        Application application1 = Application.builder()
                .id(2L)
                .offer(offer)
                .email(email)
                .resumeText(resumeText)
                .status(ApplicationStatus.APPLIED)
                .build();

        BDDMockito.when(applicationRepository.findAllByOfferId(offerId)).thenReturn(Optional.of(Arrays.asList(application, application1)));

        //when
        ApplicationDetailsListResponse response = applicationService.getAllApplicationsByOfferId(offerId);

        //then
        Assert.assertEquals(2, response.getApplications().size());
        Assert.assertThat(response.getApplications(),
                hasItems(anyOf(
                        hasProperty("id", is(application.getId())),
                        hasProperty("id", is(application1.getId())))));
    }

    @Test
    public void should_return_application_count_by_offer_id() {
        //given
        Long offerId = 1L;
        int expectedCount = 10;

        Offer offer = Offer.builder().id(offerId).jobTitle("test").startDate(Date.valueOf(LocalDate.now())).build();
        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        BDDMockito.when(applicationRepository.getCountByOfferId(offerId)).thenReturn(Optional.of(expectedCount));

        //when
        ApplicationCountResponse response = applicationService.getCountOfApplicationByOfferId(offerId);

        //then
        Assert.assertEquals(expectedCount, response.getNoOfApplications());
    }

}