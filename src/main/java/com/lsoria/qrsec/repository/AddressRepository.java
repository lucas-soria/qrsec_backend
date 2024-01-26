package com.lsoria.qrsec.repository;


import com.lsoria.qrsec.domain.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<Address, String> {}
