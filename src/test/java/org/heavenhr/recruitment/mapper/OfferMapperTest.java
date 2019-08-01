package org.heavenhr.recruitment.mapper;

import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.OfferDetailsListResponse;
import org.heavenhr.recruitment.model.response.OfferDetailsResponse;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

public class OfferMapperTest {

    @Test
    public void mapCreateOfferRequestToOffer() {
        //given
        CreateOfferRequest request = CreateOfferRequest.builder()
                .jobTitle("test")
                .startDate(LocalDate.now().plusMonths(1))
                .build();

        //when
        Offer offer = OfferMapper.mapCreateOfferRequestToOffer(request);

        //then
        Assert.assertEquals(request.getJobTitle(), offer.getJobTitle());
        Assert.assertEquals(request.getStartDate(), offer.getStartDate().toLocalDate());
    }

    @Test
    public void mapOfferToResponseObject() {
        //given
        Offer offer = Offer.builder()
                .jobTitle("test")
                .id(1L)
                .startDate(Date.valueOf(LocalDate.now()))
                .build();

        //when
        OfferDetailsResponse response = OfferMapper.mapOfferToResponseObject(offer);

        //then
        Assert.assertEquals(offer.getId(), response.getId());
        Assert.assertEquals(offer.getJobTitle(), response.getJobTitle());
        Assert.assertEquals(offer.getStartDate().toLocalDate(), response.getStartDate());
    }

    @Test
    public void mapOfferListToResponseObject() {
        //given
        Offer offer = Offer.builder()
                .jobTitle("test")
                .id(1L)
                .startDate(Date.valueOf(LocalDate.now()))
                .build();

        //when
        OfferDetailsListResponse responseList = OfferMapper.mapOfferListToResponseObject(Arrays.asList(offer));

        //then
        Assert.assertEquals(1, responseList.getOffers().size());

        OfferDetailsResponse response = responseList.getOffers().get(0);

        Assert.assertEquals(offer.getId(), response.getId());
        Assert.assertEquals(offer.getJobTitle(), response.getJobTitle());
        Assert.assertEquals(offer.getStartDate().toLocalDate(), response.getStartDate());

    }
}