package com.lsoria.qrsec.repository;

import java.util.Optional;

import com.lsoria.qrsec.domain.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

}
