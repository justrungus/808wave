package com.wave808.server.services;

import org.springframework.stereotype.Service;

import com.wave808.server.models.User;
import com.wave808.server.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User registerUser(String username, String email, String rawPassword){
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);

        newUser.setPasswordHash(rawPassword);

        return userRepository.save(newUser);
    }

}
