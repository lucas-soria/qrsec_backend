package com.lsoria.qrsec.repository;

import com.lsoria.qrsec.domain.model.Invite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InviteRepository extends MongoRepository<Invite, String> {}
