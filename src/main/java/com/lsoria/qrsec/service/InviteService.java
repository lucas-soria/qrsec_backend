package com.lsoria.qrsec.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.InviteRepository;
import com.lsoria.qrsec.service.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class InviteService {

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    UserService userService;

    public List<Invite> findAll() {

        return inviteRepository.findAll();

    }

    public List<Invite> findAllMyInvites(String username) throws Exception {

        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        return inviteRepository.findByOwner(currentUser.get());

    }

    public Optional<Invite> findOne(String id) {

        return inviteRepository.findById(id);

    }

    public Invite save(Invite invite, String username) throws Exception {

        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();

        Optional<User> currentUser = userService.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        User user = currentUser.get();
        invite.setOwner(user);
        invite.setGuests(invite.getGuests());

        invite.setDays(invite.getDays());
        invite.setHours(invite.getHours());
        invite.setMaxTimeAllowed(invite.getMaxTimeAllowed());

        invite.setDropsTrueGuest(invite.getDropsTrueGuest());
        invite.setNumberOfPassengers(invite.getNumberOfPassengers());

        LocalDateTime time = LocalDateTime.now();
        invite.setCreatedAt(time);
        invite.setLastModifiedAt(time);

        return inviteRepository.insert(invite);

    }

    public Invite update(Invite oldInvite, Invite updatedInvite) {

        oldInvite.setGuests(updatedInvite.getGuests());

        oldInvite.setDays(updatedInvite.getDays());
        oldInvite.setHours(updatedInvite.getHours());
        oldInvite.setMaxTimeAllowed(updatedInvite.getMaxTimeAllowed());

        oldInvite.setDropsTrueGuest(updatedInvite.getDropsTrueGuest());
        oldInvite.setNumberOfPassengers(updatedInvite.getNumberOfPassengers());

        oldInvite.setLastModifiedAt(LocalDateTime.now());

        return inviteRepository.save(oldInvite);

    }

    public Boolean delete(String id) throws Exception {

        Optional<Invite> foundInvite = this.findOne(id);
        if (foundInvite.isEmpty()) {

            throw new NotFoundException("Invite " + id + " not found");

        }

        Invite inviteToDelete = foundInvite.get();
        if (!inviteToDelete.getEnabled()) {

            inviteRepository.deleteById(id);

            return true;

        }

        inviteToDelete.setEnabled(false);
        inviteRepository.save(inviteToDelete);

        return false;

    }

}
