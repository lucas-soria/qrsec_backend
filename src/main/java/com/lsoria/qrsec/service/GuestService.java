package com.lsoria.qrsec.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.GuestRepository;
import com.lsoria.qrsec.service.exception.NotFoundException;

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
    UserService userService;

    private final Role adminRole = new Role(Role.ADMIN);
    private final Role guardRole = new Role(Role.GUARD);
    private final Role ownerRole = new Role(Role.OWNER);

    public List<Guest> findGuestsByCurrentUser(String username) throws Exception {

        // TODO: String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);

        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        User user = currentUser.get();
        Set<Guest> guests = new HashSet<>();

        if (user.getAuthorities().contains(adminRole)) {
            guests.addAll(findAll());
        }
        if (user.getAuthorities().contains(ownerRole)) {
            guests.addAll(findAllMyGuests(username));
        }

        return new ArrayList<>(guests);

    }

    public List<Guest> findAll() {

        return guestRepository.findAll();

    }

    public List<Guest> findAllMyGuests(String username) throws Exception {

        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        return guestRepository.findByOwnersContaining(currentUser.get());

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

            throw new NotFoundException("User " + username + " not found");

        }
        guest.getOwners().add(currentUser.get());

        return guestRepository.insert(guest);

    }

    public Guest update(Guest oldGuest, Guest updatedGuest) {

        oldGuest.setFirstName(updatedGuest.getFirstName());
        oldGuest.setLastName(updatedGuest.getLastName());
        oldGuest.setPhone(updatedGuest.getPhone());
        oldGuest.setOwners(updatedGuest.getOwners());

        return guestRepository.save(oldGuest);

    }

    public void delete(String id, String username) throws Exception {

        Optional<Guest> foundGuest = this.findOne(id);
        if (foundGuest.isEmpty()) {

            throw new NotFoundException("Guest " + id + " not found");

        }

        Optional<User> foundUser = userService.findByUsername(username);
        if (foundUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        Guest guestToDelete = foundGuest.get();
        User user = foundUser.get();
        Set<User> owners = guestToDelete.getOwners();
        if (owners.size() == 1) {

            guestRepository.deleteById(id);

            return;

        }
        if (!owners.remove(user)) {

            throw new NotFoundException("Guest " + id + " not found");

        }

        guestToDelete.setOwners(owners);
        guestRepository.save(guestToDelete);

    }

}
