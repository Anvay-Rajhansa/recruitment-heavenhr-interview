package org.heavenhr.recruitment.mapper;

import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationCountResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsListResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

public class ApplicationMapperTest {

    @Test
    public void mapCreateApplicationRequest() {
        //given
        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .offerId(1L)
                .email("abc@test.com")
                .resumeText("text")
                .build();

        Offer offer = Offer.builder().build();

        //when
        Application application = ApplicationMapper.mapCreateApplicationRequest(request, offer);

        //then
        Assert.assertEquals(request.getEmail(), application.getEmail());
        Assert.assertEquals(request.getResumeText(), application.getResumeText());
        Assert.assertEquals(ApplicationStatus.APPLIED, application.getStatus());
        Assert.assertEquals(offer, application.getOffer());
    }


    @Test
    public void mapApplicationToResponse() {
        //given
        Offer offer = Offer.builder()
                .id(1L)
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .jobTitle("test")
                .build();

        Application application = Application.builder()
                .id(1L)
                .email("abc@test.com")
                .resumeText("xyz")
                .status(ApplicationStatus.INVITED)
                .offer(offer)
                .build();

        //when
        ApplicationDetailsResponse response = ApplicationMapper.mapApplicationToResponse(application);

        //then
        Assert.assertEquals(application.getId(), response.getId());
        Assert.assertEquals(application.getEmail(), response.getEmail());
        Assert.assertEquals(application.getStatus().name(), response.getStatus());
        Assert.assertEquals(application.getResumeText(), response.getResumeText());
        Assert.assertEquals(application.getOffer().getId(), response.getOffer().getId());
        Assert.assertEquals(application.getOffer().getJobTitle(), response.getOffer().getJobTitle());
    }

    @Test
    public void mapApplicationListToResponse() {
        //given
        Offer offer = Offer.builder()
                .id(1L)
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .jobTitle("test")
                .build();

        Application application = Application.builder()
                .id(1L)
                .email("abc@test.com")
                .resumeText("xyz")
                .status(ApplicationStatus.INVITED)
                .offer(offer)
                .build();

        //when
        ApplicationDetailsListResponse responseList =
                ApplicationMapper.mapApplicationListToResponse(Arrays.asList(application));

        //then
        Assert.assertEquals(1, responseList.getApplications().size());

        ApplicationDetailsResponse response = responseList.getApplications().get(0);
        Assert.assertEquals(application.getId(), response.getId());
        Assert.assertEquals(application.getEmail(), response.getEmail());
        Assert.assertEquals(application.getStatus().name(), response.getStatus());
        Assert.assertEquals(application.getResumeText(), response.getResumeText());
        Assert.assertEquals(application.getOffer().getId(), response.getOffer().getId());
        Assert.assertEquals(application.getOffer().getJobTitle(), response.getOffer().getJobTitle());
    }

    @Test
    public void mapApplicationCountResponse() {
        //given
        int count = 10;

        //when
        ApplicationCountResponse response = ApplicationMapper.mapApplicationCountResponse(count);

        //then
        Assert.assertEquals(count, response.getNoOfApplications());
    }
}