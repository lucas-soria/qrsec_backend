package com.lsoria.qrsec.controller.rest;

import com.lsoria.qrsec.domain.dto.UserDTO;
import com.lsoria.qrsec.domain.dto.mapper.UserMapper;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.service.UserService;
import com.lsoria.qrsec.service.exception.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("${api.path}")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @PostMapping("${api.path.users}")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {

        try {
            User createdUser = userService.save(user);

            return ResponseEntity.ok(userMapper.userToUserDTO(createdUser));
        } catch (ConflictException conflictException) {
            log.error("Couldn't create USER.\nMessage: {}.", conflictException.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception exception) {
            log.error("Couldn't create USER.\nMessage: {}.\nStackTrace:\n{}", exception.getMessage(), exception.getStackTrace());
        }

        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("${api.path.users}")
    public List<UserDTO> getUsers() {
        List<User> users = userService.findAll();

        return users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
    }

    @GetMapping("${api.path.users}/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        Optional<User> user = userService.findOne(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDTO userDTO = userMapper.userToUserDTO(user.get());

        return ResponseEntity.ok(userDTO);
    }

}