package org.heavenhr.recruitment.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.heavenhr.recruitment.entity.ApplicationStatus;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationRequest {

    @NotNull
    private ApplicationStatus status;
}
