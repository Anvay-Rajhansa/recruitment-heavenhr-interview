package org.heavenhr.recruitment.controller;

import org.heavenhr.recruitment.model.request.CreateApplicationRequest;
import org.heavenhr.recruitment.model.request.UpdateApplicationRequest;
import org.heavenhr.recruitment.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        return new ResponseEntity(applicationService.createApplication(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateApplication(@PathVariable("id") @NotNull Long id,
                                            @Valid @RequestBody UpdateApplicationRequest updateApplicationRequest) {
        return new ResponseEntity(applicationService.updateApplication(id, updateApplicationRequest), HttpStatus.OK);
    }
}
