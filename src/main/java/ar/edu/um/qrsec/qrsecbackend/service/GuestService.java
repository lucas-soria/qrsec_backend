package ar.edu.um.qrsec.qrsecbackend.service;

import ar.edu.um.qrsec.qrsecbackend.domain.model.Guest;
import ar.edu.um.qrsec.qrsecbackend.repository.GuestRepository;
import ar.edu.um.qrsec.qrsecbackend.repository.UserRepository;
import ar.edu.um.qrsec.qrsecbackend.security.utils.SecurityContextUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class GuestService {

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    UserRepository userRepository;

    public Guest save(Guest guest) {
        log.debug("Request to save Guest : {}", guest);
        Optional<Guest> optionalGuest = guestRepository.findByDni(guest.getDni());
        if (optionalGuest.isPresent()) {
            guest = optionalGuest.get();
        }
        String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();
        guest.getOwner().add(userRepository.findByUsername(currentlyLoggedInUsername).get());
        guest = guestRepository.save(guest);
        return guest;
    }

    public Optional<Guest> findOne(String id) {
        log.debug("Request to find Guest : {}", id);
        return guestRepository.findById(id);
    }

    public List<Guest> findAll() {
        log.debug("Request to find all Guest");
        return guestRepository.findAll();
    }

    public List<Guest> findAllMyGuests() {
        log.debug("Request to find all Guest of currently logged user");
        String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();
        return guestRepository.findByOwner(userRepository.findByUsername(currentlyLoggedInUsername).get());
    }

}
