package org.heavenhr.recruitment.repository;

import org.heavenhr.recruitment.entity.Application;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends CrudRepository<Application, Long> {

    @Query("SELECT a FROM application a WHERE a.offer.id = :offerId")
    public Optional<List<Application>> findAllByOfferId(@Param("offerId") Long offerId);

    @Query("SELECT a FROM application a WHERE a.offer.id = :offerId AND a.id = :applicationId")
    public Optional<Application> findByOfferIdAndApplicationId(@Param("offerId") Long offerId,
                                                               @Param("applicationId") Long applicationId);

    @Query("SELECT COUNT(a) FROM application a WHERE a.offer.id = :offerId")
    public Optional<Integer> getCountByOfferId(@Param("offerId") Long offerId);
}
