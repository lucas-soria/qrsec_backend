package com.lsoria.qrsec.controller.rest;

import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.path}")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("${api.path.user}")
    public User createInvite(@RequestBody User user) {
        log.debug("REST request to save User : {}", user);
        return userService.save(user);
    }

    @GetMapping("${api.path.user}")
    public List<User> getGuests() {
        log.debug("REST request to get all User");
        return userService.findAll();
    }

    @GetMapping("${api.path.user}/{id}")
    public ResponseEntity<User> getGuest(@PathVariable String id) {
        log.debug("REST request to get User : {}", id);
        return ResponseEntity.of(userService.findOne(id));
    }

}