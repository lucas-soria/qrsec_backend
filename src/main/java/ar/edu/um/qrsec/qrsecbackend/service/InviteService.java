package ar.edu.um.qrsec.qrsecbackend.service;

import ar.edu.um.qrsec.qrsecbackend.domain.model.Invite;
import ar.edu.um.qrsec.qrsecbackend.repository.InviteRepository;
import ar.edu.um.qrsec.qrsecbackend.repository.UserRepository;
import ar.edu.um.qrsec.qrsecbackend.security.utils.SecurityContextUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class InviteService {

    @Autowired
    InviteRepository inviteRepository;

    @Autowired
    UserRepository userRepository;

    public Invite save(Invite invite) {
        log.debug("Request to save Invite : {}", invite);
        String currentlyLoggedInUsername = new SecurityContextUserInfo().getUsername();
        invite.setOwner(userRepository.findByUsername(currentlyLoggedInUsername).get());
        invite.setCreated(LocalDateTime.now());
        invite.setModified(invite.getCreated());
        invite = inviteRepository.save(invite);
        return invite;
    }

    public Optional<Invite> findOne(String id) {
        log.debug("Request to find Invite : {}", id);
        return inviteRepository.findById(id);
    }

    public List<Invite> findAll() {
        log.debug("Request to find all Invite");
        return inviteRepository.findAll();
    }

}
