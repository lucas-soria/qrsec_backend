package com.lsoria.qrsec.service;

import java.util.List;
import java.util.Optional;

import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.UserRepository;
import com.lsoria.qrsec.service.exception.ConflictException;
import com.lsoria.qrsec.service.exception.NotFoundException;

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

    public List<User> findAll() {

        return userRepository.findAll();

    }

    public Optional<User> findOne(String id) {

        return userRepository.findById(id);

    }

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);

    }

    public User save(User user) throws Exception {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {

            Optional<User> existentUser = userRepository.findByUsername(user.getUsername());
            if (existentUser.isPresent()) {

                throw new ConflictException("User already exists");

            }
            user = userRepository.insert(user);

            return user;

        } catch (DuplicateKeyException duplicateKeyException) {

            throw new ConflictException(duplicateKeyException.getMessage());

        }

    }

    public User update(User oldUser, User updatedUser) {

        oldUser.setAddress(updatedUser.getAddress());
        oldUser.setAuthorities(updatedUser.getAuthorities());
        oldUser.setEnabled(updatedUser.getEnabled());
        oldUser.setFirstName(updatedUser.getFirstName());
        oldUser.setLastName(updatedUser.getLastName());
        oldUser.setPassword(updatedUser.getPassword());
        oldUser.setPhone(updatedUser.getPhone());
        oldUser.setUsername(updatedUser.getUsername());

        userRepository.save(oldUser);

        return new User();

    }

    public void delete(String id) throws Exception {

        if (!userRepository.existsById(id)) {

            throw new NotFoundException("User not found");

        }

        userRepository.deleteById(id);

    }

}
