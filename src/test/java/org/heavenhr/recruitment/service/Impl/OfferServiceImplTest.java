package org.heavenhr.recruitment.service.Impl;

import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.exception.RecruitmentBusinessException;
import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.OfferDetailsListResponse;
import org.heavenhr.recruitment.model.response.OfferDetailsResponse;
import org.heavenhr.recruitment.repository.OfferRepository;
import org.heavenhr.recruitment.service.OfferService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;

public class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    private OfferService offerService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        offerService = new OfferServiceImpl(offerRepository);
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_for_invalid_date() {
        //given
        String givenTitle = "test";
        LocalDate givenDate = LocalDate.now().minusDays(1);

        CreateOfferRequest request = CreateOfferRequest.builder()
                .jobTitle(givenTitle)
                .startDate(givenDate)
                .build();

        //when
        offerService.CreateOffer(request);

        //then
        //RecruitmentBusinessException is expected.
    }

    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_offer_with_same_title_already_exists() {
        //given
        String givenTitle = "test";
        LocalDate givenDate = LocalDate.now().plusMonths(1);

        CreateOfferRequest request = CreateOfferRequest.builder()
                .jobTitle(givenTitle)
                .startDate(givenDate)
                .build();

        BDDMockito.when(offerRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate key"));

        //when
        offerService.CreateOffer(request);

        //then
        //RecruitmentBusinessException is expected.
    }

    @Test
    public void should_create_offer() {
        //given
        String givenTitle = "test";
        LocalDate givenDate = LocalDate.now().plusMonths(1);

        CreateOfferRequest request = CreateOfferRequest.builder()
                .jobTitle(givenTitle)
                .startDate(givenDate)
                .build();

        Offer expectedOffer = Offer.builder()
                .id(1L)
                .jobTitle(givenTitle)
                .startDate(Date.valueOf(givenDate))
                .build();

        ArgumentCaptor<Offer> createOfferCaptor = ArgumentCaptor.forClass(Offer.class);
        BDDMockito.when(offerRepository.save(any())).thenReturn(expectedOffer);

        //when
        OfferDetailsResponse response = offerService.CreateOffer(request);

        //then
        Assert.assertEquals(expectedOffer.getId(), response.getId());
        Assert.assertEquals(expectedOffer.getJobTitle(), response.getJobTitle());

        BDDMockito.verify(offerRepository).save(createOfferCaptor.capture());
        Offer offer = createOfferCaptor.getAllValues().get(0);
        Assert.assertEquals(expectedOffer.getJobTitle(), offer.getJobTitle());
    }


    @Test(expected = RecruitmentBusinessException.class)
    public void should_throw_error_when_offer_id_is_invalid_while_getting_offer_by_id() {
        //given
        Long offerId = 1L;

        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        //when
        offerService.getOfferById(offerId);

        //then
        //RecruitmentBusinessException is expected.
    }

    @Test
    public void should_return_offer_by_id() {
        //given
        Long offerId = 1L;

        Offer expectedOffer = Offer.builder()
                .id(1L)
                .jobTitle("test")
                .startDate(Date.valueOf(LocalDate.now()))
                .build();

        BDDMockito.when(offerRepository.findById(offerId)).thenReturn(Optional.of(expectedOffer));

        //when
        OfferDetailsResponse response = offerService.getOfferById(offerId);

        //then
        Assert.assertEquals(offerId, response.getId());
    }

    @Test
    public void should_return_all_offers() {
        //given

        Offer expectedOffer = Offer.builder()
                .id(1L)
                .jobTitle("test")
                .startDate(Date.valueOf(LocalDate.now()))
                .build();
        Offer expectedOffer1 = Offer.builder()
                .id(2L)
                .jobTitle("test1")
                .startDate(Date.valueOf(LocalDate.now()))
                .build();

        BDDMockito.when(offerRepository.findAll()).thenReturn(Arrays.asList(expectedOffer, expectedOffer1));

        //when
        OfferDetailsListResponse response = offerService.getAllOffers();

        //then
        Assert.assertEquals(2, response.getOffers().size());
        Assert.assertThat(response.getOffers(),
                hasItems(anyOf(
                        hasProperty("id", is(expectedOffer.getId())),
                        hasProperty("id", is(expectedOffer1.getId())))));
    }
}