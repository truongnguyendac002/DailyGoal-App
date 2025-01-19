package com.projectn.projectn.service;


import com.projectn.projectn.config.MessageBuilder;
import com.projectn.projectn.model.User;
import com.projectn.projectn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addPoints(User user, int points) {
        user.setWallet(user.getWallet() + points);
        userRepository.save(user);
    }

}

