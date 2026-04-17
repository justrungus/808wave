package com.wave808.server.services;

import org.springframework.stereotype.Service;
import com.wave808.server.models.User;
import com.wave808.server.dto.UserDTO;
import com.wave808.server.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public UserDTO register(String username, String email, String password) throws RuntimeException{
        if (userRepository.existsByUsername(username)){
            throw new RuntimeException("Username already in use");
        }
        if (userRepository.existsByEmail(email)){
            throw new RuntimeException("email already in use");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password); //TODO BCrypt
        User savedUser = userRepository.save(newUser);
        return new UserDTO(savedUser.getUserId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public UserDTO login(String username, String password) throws RuntimeException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPasswordHash().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }

}
