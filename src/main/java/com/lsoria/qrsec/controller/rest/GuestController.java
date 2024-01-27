package com.lsoria.qrsec.controller.rest;

import com.lsoria.qrsec.domain.model.Guest;
import com.lsoria.qrsec.service.GuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.path}")
@Slf4j
public class GuestController {

    @Autowired
    GuestService guestService;

    @PostMapping("${api.path.guest}")
    public Guest createGuest(@RequestBody Guest guest) {
        log.debug("REST request to save Guest : {}", guest);
        return guestService.save(guest);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all/${api.path.guest}")
    public List<Guest> getGuests() {
        log.debug("REST request to get all Guest");
        return guestService.findAll();
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("${api.path.guest}")
    public List<Guest> getMyGuests() {
        log.debug("REST request to get all Guest of currently logged user");
        return guestService.findAllMyGuests();
    }

    @GetMapping("${api.path.guest}/{id}")
    public ResponseEntity<Guest> getGuest(@PathVariable String id) {
        log.debug("REST request to get Invite : {}", id);
        return ResponseEntity.of(guestService.findOne(id));
    }

}
