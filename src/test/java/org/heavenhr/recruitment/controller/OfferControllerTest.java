package org.heavenhr.recruitment.controller;

import org.heavenhr.recruitment.TestBase;
import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.*;
import org.heavenhr.recruitment.repository.ApplicationRepository;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.utils.JsonUtils;
import org.junit.Assert;
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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfferControllerTest extends TestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void should_return_request_validation_error_while_creating_offer() throws Exception {
        //given
        CreateOfferRequest createOfferRequest = CreateOfferRequest.builder()
                .jobTitle(null)
                .startDate(null)
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createOfferRequest)))
                .andExpect(status().isUnprocessableEntity()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());
        List<String> errorMessages = (List<String>) error.get("error");

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("jobTitle : must not be null");
        expectedErrors.add("startDate : must not be null");
        expectedErrors.add("jobTitle : must not be empty");

        Assert.assertThat(expectedErrors, containsInAnyOrder(errorMessages.toArray()));
    }

    @Test
    public void should_return_error_when_start_date_is_not_valid_while_creating_offer() throws Exception {
        //given
        CreateOfferRequest createOfferRequest = CreateOfferRequest.builder()
                .jobTitle("Test")
                .startDate(LocalDate.now().minusDays(1))
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createOfferRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Please select futuristic date.", error.get("error"));
    }

    @Test
    public void should_return_error_when_offer_with_same_title_already_exists_while_creating_offer() throws Exception {
        //given
        String givenTitle = "Test title already exists";

        Offer givenOffer = Offer.builder()
                .jobTitle(givenTitle)
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        offerRepository.save(givenOffer);

        CreateOfferRequest createOfferRequest = CreateOfferRequest.builder()
                .jobTitle(givenTitle)
                .startDate(LocalDate.now().plusMonths(1))
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createOfferRequest)))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Offer with given title already exists.", error.get("error"));
    }

    @Test
    public void should_create_offer() throws Exception {
        //given
        String givenOfferTitle = "Test create title";
        LocalDate givenStartDate = LocalDate.now().plusMonths(2);
        CreateOfferRequest createOfferRequest = CreateOfferRequest.builder()
                .jobTitle(givenOfferTitle)
                .startDate(givenStartDate)
                .build();

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.asJsonString(createOfferRequest)))
                .andExpect(status().isCreated()).andReturn();

        //then
        OfferDetailsResponse response = JsonUtils.parseJsonStringInObject(result.getResponse().getContentAsString(),
                OfferDetailsResponse.class);
        Assert.assertEquals(givenOfferTitle, response.getJobTitle());
        Assert.assertEquals(givenStartDate, response.getStartDate());
    }

    @Test
    public void should_return_error_if_offer_doest_not_exists_with_given_id_while_getting_offer_details() throws Exception {
        //given
        long invalidId = 123456L;

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Offer with given id does not exists.", error.get("error"));
    }

    @Test
    public void should_return_offer_by_id() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title return by id")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + givenOffer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //then
        OfferDetailsResponse response = JsonUtils.parseJsonStringInObject(result.getResponse().getContentAsString(),
                OfferDetailsResponse.class);
        Assert.assertEquals(givenOffer.getId(), response.getId());
        Assert.assertEquals(givenOffer.getJobTitle(), response.getJobTitle());
    }

    @Test
    public void should_return_all_offers() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title get all offers")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        OfferDetailsListResponse response = JsonUtils.parseJsonStringInObject(result.getResponse()
                .getContentAsString(), OfferDetailsListResponse.class);
        Assert.assertThat(response.getOffers(), hasItems(hasProperty("id", is(givenOffer.getId()))));
    }

    @Test
    public void should_return_error_when_application_not_exists_while_getting_application_by_offer_id_and_application_id() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title invalid application")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + givenOffer.getId() + "/applications/" + 1236465L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Application does not exists.", error.get("error"));

    }

    @Test
    public void should_return_application_by_offer_id_and_application_id() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title application")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        Application application = Application.builder()
                .offer(givenOffer)
                .email("applicationswithId@test.com")
                .status(ApplicationStatus.INVITED)
                .resumeText("tetet")
                .build();
        application = applicationRepository.save(application);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + givenOffer.getId() + "/applications/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //then
        ApplicationDetailsResponse response = JsonUtils.parseJsonStringInObject(result.getResponse().getContentAsString(),
                ApplicationDetailsResponse.class);
        Assert.assertEquals(application.getStatus().name(), response.getStatus());
        Assert.assertEquals(givenOffer.getId(), response.getOffer().getId());

    }

    @Test
    public void should_return_all_application_by_offer_id() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title get all applications")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        Application application = Application.builder()
                .offer(givenOffer)
                .email("allapplications@test.com")
                .status(ApplicationStatus.INVITED)
                .resumeText("tetet")
                .build();
        application = applicationRepository.save(application);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + givenOffer.getId() + "/applications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //then
        ApplicationDetailsListResponse response = JsonUtils.parseJsonStringInObject(result.getResponse()
                .getContentAsString(), ApplicationDetailsListResponse.class);
        Assert.assertThat(response.getApplications(), hasItems(hasProperty("id", is(application.getId()))));
    }

    @Test
    public void should_return_error_when_offerId_is_invalid_while_getting_application_count_by_offer_id() throws Exception {
        //given
        long invalidId = 13456L;

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + invalidId + "/applications/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        //then
        Map error = JsonUtils.parseJsonStringInToMap(result.getResponse().getContentAsString());

        Assert.assertEquals("Offer with given id does not exists.", error.get("error"));
    }

    @Test
    public void should_return_application_count_by_offer_id() throws Exception {
        //given
        Offer givenOffer = Offer.builder()
                .jobTitle("Test title applications count")
                .startDate(Date.valueOf(LocalDate.now().plusMonths(1)))
                .build();
        givenOffer = offerRepository.save(givenOffer);

        Application application = Application.builder()
                .offer(givenOffer)
                .email("application.count@test.com")
                .status(ApplicationStatus.INVITED)
                .resumeText("tetet")
                .build();
        application = applicationRepository.save(application);

        //when
        MvcResult result = mockMvc.perform(get("/api/v1/offers/" + givenOffer.getId() + "/applications/count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        //then
        ApplicationCountResponse response = JsonUtils.parseJsonStringInObject(result.getResponse()
                .getContentAsString(), ApplicationCountResponse.class);
        Assert.assertEquals(1, response.getNoOfApplications());
    }
}