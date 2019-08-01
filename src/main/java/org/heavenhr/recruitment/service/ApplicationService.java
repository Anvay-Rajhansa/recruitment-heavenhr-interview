package org.heavenhr.recruitment.service;

import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.request.UpdateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationCountResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsListResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;

public interface ApplicationService {

    ApplicationDetailsResponse createApplication(CreateApplicationRequest request);

    ApplicationDetailsResponse getApplicationByOfferIdAndApplicationId(Long offerId, Long applicationId);

    ApplicationDetailsListResponse getAllApplicationsByOfferId(Long offerId);

    ApplicationDetailsResponse updateApplication(Long applicationId, UpdateApplicationRequest request);

    ApplicationCountResponse getCountOfApplicationByOfferId(Long offerId);
}
