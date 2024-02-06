package com.lsoria.qrsec.repository;

import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuestRepository extends MongoRepository<Guest, String> {

    public Optional<Guest> findByDni(String dni);

    public List<Guest> findByOwnersContaining(User owner);

    /*
    @Query(value="{\aca query de mongo\}")
    Ej: { id_usuario: { $in: seguidos } }
    */

}
