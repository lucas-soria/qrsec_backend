package com.lsoria.qrsec.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.lsoria.qrsec.domain.model.Role;
import com.lsoria.qrsec.domain.model.User;
import com.lsoria.qrsec.repository.UserRepository;
import com.lsoria.qrsec.service.exception.ConflictException;
import com.lsoria.qrsec.service.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

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

        if (userRepository.count() == 0) {
            user.setEnabled(true);
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(Role.ADMIN));
            user.setAuthorities(roles);
        } else {
            user.setEnabled(false);
            Set<Role> roles = new HashSet<>();
            user.setAuthorities(roles);
        }

        try {

            return userRepository.insert(user);

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
        oldUser.setPhone(updatedUser.getPhone());
        oldUser.setUsername(updatedUser.getUsername());

        return userRepository.save(oldUser);

    }

    public void delete(String id) throws Exception {

        Optional<User> userFound = this.findOne(id);
        if (userFound.isEmpty()) {

            throw new NotFoundException("User " + id + " not found");

        }

        User user = userFound.get();
        user.setEnabled(false);

        userRepository.save(user);

    }

    public boolean userIsAuthorized(String username, Role role) throws Exception {

        Optional<User> currentUser = this.findByUsername(username);
        if (currentUser.isEmpty()) {

            throw new NotFoundException("User " + username + " not found");

        }

        User user = currentUser.get();

        return user.getAuthorities().contains(role);

    }

}
