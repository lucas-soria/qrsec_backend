package ar.edu.um.qrsec.qrsecbackend.repository;

import ar.edu.um.qrsec.qrsecbackend.domain.model.Guest;
import ar.edu.um.qrsec.qrsecbackend.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

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
