package ar.edu.um.qrsec.qrsecbackend.controller.rest;

import ar.edu.um.qrsec.qrsecbackend.domain.model.Invite;
import ar.edu.um.qrsec.qrsecbackend.service.InviteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.path}")
@Slf4j
public class InviteController {

    @Autowired
    InviteService inviteService;

    @PostMapping("${api.path.invite}")
    public Invite createInvite(@RequestBody Invite invite) {
        log.debug("REST request to save Invite : {}", invite);
        return inviteService.save(invite);
    }

    @GetMapping("${api.path.invite}")
    public List<Invite> getInvites() {
        log.debug("REST request to get all Invite");
        return inviteService.findAll();
    }

    @GetMapping("${api.path.invite}/{id}")
    public ResponseEntity<Invite> getInvite(@PathVariable String id) {
        log.debug("REST request to get Invite : {}", id);
        return ResponseEntity.of(inviteService.findOne(id));
    }

}
