package org.heavenhr.recruitment.mapper;

import org.heavenhr.recruitment.entity.Application;
import org.heavenhr.recruitment.entity.ApplicationStatus;
import org.heavenhr.recruitment.entity.Offer;
import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.response.ApplicationCountResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsListResponse;
import org.heavenhr.recruitment.model.response.ApplicationDetailsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationMapper {

    public static Application mapCreateApplicationRequest(CreateApplicationRequest request, Offer offer) {
        return Application.builder()
                .email(request.getEmail())
                .resumeText(request.getResumeText())
                .offer(offer)
                .status(ApplicationStatus.APPLIED)
                .build();
    }

    public static ApplicationDetailsResponse mapApplicationToResponse(Application application) {
        return ApplicationDetailsResponse.builder()
                .email(application.getEmail())
                .id(application.getId())
                .offer(OfferMapper.mapOfferToResponseObject(application.getOffer()))
                .resumeText(application.getResumeText())
                .status(application.getStatus().name())
                .build();
    }

    public static ApplicationDetailsListResponse mapApplicationListToResponse(List<Application> applications) {
        return ApplicationDetailsListResponse.builder()
                .applications(applications.stream()
                        .map(ApplicationMapper::mapApplicationToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ApplicationCountResponse mapApplicationCountResponse(int count) {
        return ApplicationCountResponse.builder().noOfApplications(count).build();
    }
}
