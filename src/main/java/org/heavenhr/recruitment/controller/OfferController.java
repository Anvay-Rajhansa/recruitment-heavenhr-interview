package org.heavenhr.recruitment.controller;

import org.heavenhr.recruitment.model.request.CreateOfferRequest;
import org.heavenhr.recruitment.service.ApplicationService;
import org.heavenhr.recruitment.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("api/v1/offers")
public class OfferController {

    private final OfferService offerService;
    private final ApplicationService applicationService;

    public OfferController(OfferService offerService, ApplicationService applicationService) {
        this.offerService = offerService;
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity createOffer(@Valid @RequestBody CreateOfferRequest offerRequest) {
        return new ResponseEntity(offerService.CreateOffer(offerRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity getOfferById(@PathVariable("id") @NotNull Long id) {
        return new ResponseEntity(offerService.getOfferById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllOffers() {
        return new ResponseEntity(offerService.getAllOffers(), HttpStatus.OK);
    }

    @GetMapping("/{offerId}/applications/{applicationId}")
    public ResponseEntity getApplicationByOfferIdAndApplicationId(@PathVariable("offerId") @NotNull Long offerId,
                                                                  @PathVariable("applicationId") @NotNull Long applicationId) {
        return new ResponseEntity(applicationService.getApplicationByOfferIdAndApplicationId(offerId, applicationId),
                HttpStatus.OK);
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity getAllApplicationsByOfferId(@PathVariable("id") @NotNull Long id) {
        return new ResponseEntity(applicationService.getAllApplicationsByOfferId(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/applications/count")
    public ResponseEntity getCountOfApplicationsByOfferId(@PathVariable("id") @NotNull Long id) {
        return new ResponseEntity(applicationService.getCountOfApplicationByOfferId(id), HttpStatus.OK);
    }
}