package com.lsoria.qrsec.repository;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends MongoRepository<Guest, String> {

    public Optional<Guest> findByDni(String dni);

    public List<Guest> findByOwner(User owner);

    /*
    @Query(value="{\aca query de mongo\}")
    Ej: { id_usuario: { $in: seguidos } }
    */

}
