package org.heavenhr.recruitment.service;

import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.model.response.OfferDetailsListResponse;
import org.heavenhr.recruitment.model.response.OfferDetailsResponse;

public interface OfferService {

    OfferDetailsResponse CreateOffer(CreateOfferRequest request);

    OfferDetailsResponse getOfferById(Long id);

    OfferDetailsListResponse getAllOffers();
}
