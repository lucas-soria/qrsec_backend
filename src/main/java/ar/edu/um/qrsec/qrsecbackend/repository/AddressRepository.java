package ar.edu.um.qrsec.qrsecbackend.repository;


import ar.edu.um.qrsec.qrsecbackend.domain.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<Address, String> {}
