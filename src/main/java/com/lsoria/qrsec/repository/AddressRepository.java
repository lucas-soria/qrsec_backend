package com.lsoria.qrsec.repository;

import java.util.Optional;

import com.lsoria.qrsec.domain.model.Address;
import com.lsoria.qrsec.domain.model.House;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<Address, String> {

    Optional<Address> findByHouse(House house);

}
