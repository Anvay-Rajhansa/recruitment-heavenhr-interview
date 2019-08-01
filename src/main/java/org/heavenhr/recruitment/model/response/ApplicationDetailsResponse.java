package org.heavenhr.recruitment.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetailsResponse {

    private Long id;

    private String email;

    private String resumeText;

    private String status;

    private OfferDetailsResponse offer;
}
