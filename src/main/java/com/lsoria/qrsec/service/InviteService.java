package com.lsoria.qrsec.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.repository.InviteRepository;
import com.lsoria.qrsec.repository.UserRepository;

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
    UserRepository userRepository;

    public Invite save(Invite invite) {
        log.info("Request to save Invite : {}", invite);
        // String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();
        String currentlyLoggedInUsername = "username"; // TODO: Fix
        invite.setOwner(userRepository.findByUsername(currentlyLoggedInUsername).get());
        invite.setCreated(LocalDateTime.now());
        invite.setModified(invite.getCreated());
        invite = inviteRepository.save(invite);
        return invite;
    }

    public Optional<Invite> findOne(String id) {
        log.info("Request to find Invite : {}", id);
        return inviteRepository.findById(id);
    }

    public List<Invite> findAll() {
        log.info("Request to find all Invite");
        return inviteRepository.findAll();
    }

}
