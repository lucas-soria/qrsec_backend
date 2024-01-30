package com.lsoria.qrsec.service;

import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.UserRepository;
import com.lsoria.qrsec.service.exception.ConflictException;

import com.mongodb.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User save(User user) throws Exception {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            Optional<User> existentUser = userRepository.findByUsername(user.getUsername());
            if (existentUser.isPresent()) {
                throw new ConflictException("User already exists");
            }

            user = userRepository.save(user); // userRepository.insert()

            return user;
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new ConflictException(duplicateKeyException.getMessage());
        }

    }

    public Optional<User> findOne(String id) {

        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {

        return userRepository.findAll();
    }

}
