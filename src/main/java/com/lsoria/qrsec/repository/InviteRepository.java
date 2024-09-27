package com.lsoria.qrsec.repository;

import java.util.List;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.domain.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface InviteRepository extends MongoRepository<Invite, String> {

    public List<Invite> findByOwner(User owner);

}
