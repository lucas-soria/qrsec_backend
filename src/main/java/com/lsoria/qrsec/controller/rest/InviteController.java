package com.lsoria.qrsec.controller.rest;

import java.util.List;

import com.lsoria.qrsec.domain.model.Invite;
import com.lsoria.qrsec.service.InviteService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}")
@Slf4j
public class InviteController {

    @Autowired
    InviteService inviteService;

    @PostMapping("${api.path.invites}")
    public Invite createInvite(@RequestBody Invite invite) {
        log.info("REST request to save Invite : {}", invite);
        return inviteService.save(invite);
    }

    @GetMapping("${api.path.invites}")
    public List<Invite> getInvites() {
        log.info("REST request to get all Invite");
        return inviteService.findAll();
    }

    @GetMapping("${api.path.invites}/{id}")
    public ResponseEntity<Invite> getInvite(@PathVariable String id) {
        log.info("REST request to get Invite : {}", id);
        return ResponseEntity.of(inviteService.findOne(id));
    }

}
