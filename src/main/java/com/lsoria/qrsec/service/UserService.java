package com.lsoria.qrsec.service;

import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User save(User user) {
        log.debug("Request to save User : {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;
    }

    public Optional<User> findOne(String id) {
        log.debug("Request to find User : {}", id);
        Optional<User> user = userRepository.findById(id);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        log.debug("Request to find User by username : {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        return user;
    }

    public List<User> findAll() {
        log.debug("Request to find all User");
        return userRepository.findAll();
    }

}
