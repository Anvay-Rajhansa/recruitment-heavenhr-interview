package org.heavenhr.recruitment.mapper;

import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.OfferDetailsListResponse;
import org.heavenhr.recruitment.model.response.OfferDetailsResponse;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class OfferMapper {

    public static Offer mapCreateOfferRequestToOffer(CreateOfferRequest request) {
        return Offer.builder()
                .jobTitle(request.getJobTitle())
                .startDate(Date.valueOf(request.getStartDate()))
                .build();
    }

    public static OfferDetailsResponse mapOfferToResponseObject(Offer offer) {
        return OfferDetailsResponse.builder()
                .id(offer.getId())
                .jobTitle(offer.getJobTitle())
                .startDate(offer.getStartDate().toLocalDate())
                .build();
    }

    public static OfferDetailsListResponse mapOfferListToResponseObject(Iterable<Offer> offers) {
        List<OfferDetailsResponse> responseList = new ArrayList<>();
        offers.forEach(offer -> {
            responseList.add(OfferMapper.mapOfferToResponseObject(offer));
        });
        return OfferDetailsListResponse.builder().offers(responseList).build();
    }
}
