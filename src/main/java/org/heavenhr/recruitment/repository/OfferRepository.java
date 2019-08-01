package org.heavenhr.recruitment.repository;

import org.heavenhr.recruitment.entity.Offer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OfferRepository extends PagingAndSortingRepository<Offer, Long> {
}
