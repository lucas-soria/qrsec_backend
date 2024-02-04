package com.lsoria.qrsec.service;

import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.GuestRepository;
import com.lsoria.qrsec.service.exception.NotFoundException;
import com.lsoria.qrsec.service.exception.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class GuestService {

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    UserService userService; // Move all uses of userIsAuthorized to handler.

    public List<Guest> findAll(String username, Role role) throws Exception {

        if (userService.userIsAuthorized(username, role)) {

            return guestRepository.findAll();

        }

        throw new UnauthorizedException("User is not Authorized to use this endpoint");

    }

    public List<Guest> findAllMyGuests(String username, Role role) throws Exception {

        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        if (!userService.userIsAuthorized(username, role)) {

            throw new UnauthorizedException("User is not Authorized to use this endpoint");

        }

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User not found");

        }

        return guestRepository.findByOwner(currentUser.get());

    }

    public Optional<Guest> findOne(String id) {

        return guestRepository.findById(id);

    }

    public Guest save(Guest guest, String username) throws Exception {

        Optional<Guest> existentGuest = guestRepository.findByDni(guest.getDni());
        if (existentGuest.isPresent()) {
            guest = existentGuest.get();
        }
        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User not found");

        }

        guest.getOwner().add(currentUser.get());
        guest = guestRepository.save(guest);

        return guest;

    }

}
