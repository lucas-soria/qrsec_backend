package com.lsoria.qrsec.service;

import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.Address;
import com.lsoria.qrsec.repository.AddressRepository;
import com.lsoria.qrsec.service.exception.ConflictException;
import com.lsoria.qrsec.service.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    public List<Address> findAll() {

        return addressRepository.findAll();

    }

    public Optional<Address> findOne(String id) {

        return addressRepository.findById(id);

    }

    public Address save(Address address) throws Exception {

        try {

            return addressRepository.insert(address);

        } catch (DuplicateKeyException duplicateKeyException) {

            throw new ConflictException(duplicateKeyException.getMessage());

        }

    }

    public Address update(Address oldAddress, Address updatedAddress) {

        oldAddress.setLocation(updatedAddress.getLocation());

        return addressRepository.save(oldAddress);

    }

    public void delete(String id) throws Exception {

        if (!addressRepository.existsById(id)) {

            throw new NotFoundException("Address not found");

        }

        addressRepository.deleteById(id);

    }

}
