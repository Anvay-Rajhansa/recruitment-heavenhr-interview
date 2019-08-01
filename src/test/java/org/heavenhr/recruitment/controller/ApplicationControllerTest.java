package org.heavenhr.recruitment.controller;

import org.heavenhr.recruitment.TestBase;
import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.request.UpdateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;
import org.heavenhr.recruitment.repository.ApplicationRepository;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationControllerTest extends TestBase {

    private static Offer offer = null;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Before
    public void init() {
        if (offer == null) {
            offer = Offer.builder()
                    .jobTitle("application test title")
                    .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                    .build();
            offer = offerRepository.save(offer);
        }
    }

    @Test
    public void should_return_request_validation_error_while_creating_application() throws Exception {
        //given
        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .offerId(null)
                .email(null)
                .resumeText(null)
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createApplicationRequest)))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());
        List<String> errorMessages = (List<String>) error.get("error");

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("offerId : must not be null");
        expectedErrors.add("email : must not be null");
        expectedErrors.add("email : must not be empty");
        expectedErrors.add("resumeText : must not be null");
        expectedErrors.add("resumeText : must not be empty");

        Assert.assertThat(expectedErrors, containsInAnyOrder(errorMessages.toArray()));
    }

    @Test
    public void should_return_error_when_offerId_is_invalid_while_creating_application() throws Exception {
        //given
        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .offerId(123456L)
                .email("abc@test.com")
                .resumeText("aafsaf")
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Offer with given id does not exists.", error.get("error"));
    }

    @Test
    public void should_return_error_when_email_already_applied_for_given_offer_while_creating_offer() throws Exception {
        //given
        String givenEmail = "alreadyExists@test.com";

        Application application = Application.builder()
                .email("alreadyExists@test.com")
                .resumeText("axy")
                .offer(offer)
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);

        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .offerId(offer.getId())
                .email(givenEmail)
                .resumeText("aafsaf")
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("User with given email is already applied for this offer.", error.get("error"));
    }

    @Test
    public void should_create_application() throws Exception {
        //given
        String givenEmail = "abc@test.com";
        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .offerId(offer.getId())
                .email(givenEmail)
                .resumeText("adadad")
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createApplicationRequest)))
                .andExpect(status().isCreated()).andReturn();

        //then
        ApplicationDetailsResponse response = JsonUtils.parseJsonStringInObject(result.getResponse().getContentAsString(),
                ApplicationDetailsResponse.class);
        Assert.assertEquals(givenEmail, response.getEmail());
        Assert.assertEquals(ApplicationStatus.APPLIED.name(), response.getStatus());
        Assert.assertEquals(offer.getId(), response.getOffer().getId());
    }

    @Test
    public void should_return_request_validation_error_while_updating_application() throws Exception {
        //given
        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(null)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());
        List<String> errorMessages = (List<String>) error.get("error");

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("status : must not be null");

        Assert.assertThat(expectedErrors, containsInAnyOrder(errorMessages.toArray()));
    }

    @Test
    public void should_return_error_when_application_id_is_invalid_while_updating_application() throws Exception {
        //given
        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.INVITED)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Application does not exists.", error.get("error"));
    }

    @Test
    public void should_return_error_when_try_to_update_same_status() throws Exception {
        //given
        Application application = Application.builder()
                .email("applicationSameStatusUpdate@test.com")
                .resumeText("axy")
                .offer(offer)
                .status(ApplicationStatus.APPLIED)
                .build();
        application = applicationRepository.save(application);

        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/" + application.getId()
        )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Please select different status to update.", error.get("error"));
    }

    @Test
    public void should_return_error_when_try_to_update_status_from_INVITED_to_APPLIED() throws Exception {
        //given
        Application application = Application.builder()
                .email("applicationInvitedStatusUpdate@test.com")
                .resumeText("axy")
                .offer(offer)
                .status(ApplicationStatus.INVITED)
                .build();
        application = applicationRepository.save(application);

        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/" + application.getId()
        )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Please select different status to update as candidate is invited.", error.get("error"));
    }

    @Test
    public void should_return_error_when_try_to_update_status_from_HIRED_to_APPLIED() throws Exception {
        //given
        Application application = Application.builder()
                .email("applicationHiredStatusUpdate@test.com")
                .resumeText("axy")
                .offer(offer)
                .status(ApplicationStatus.HIRED)
                .build();
        application = applicationRepository.save(application);

        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.APPLIED)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/" + application.getId()
        )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Status update is not allowed as recruitment process is finished for this candidate.", error.get("error"));
    }

    @Test
    public void should_update_application_status() throws Exception {
        //given
        Application application = Application.builder()
                .email("applicationUpdate@test.com")
                .resumeText("axy")
                .offer(offer)
                .status(ApplicationStatus.APPLIED)
                .build();
        application = applicationRepository.save(application);

        UpdateApplicationRequest updateApplicationRequest = UpdateApplicationRequest.builder()
                .status(ApplicationStatus.INVITED)
                .build();

        //when
        MvcResult result = mockMvc.perform(put("/api/v1/applications/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(updateApplicationRequest)))
                .andExpect(status().isOk()).andReturn();

        //then
        ApplicationDetailsResponse response = JsonUtils.parseJsonStringInObject(result.getResponse().getContentAsString(),
                ApplicationDetailsResponse.class);
        Assert.assertEquals(updateApplicationRequest.getStatus().name(), response.getStatus());
        Assert.assertEquals(offer.getId(), response.getOffer().getId());
    }
}