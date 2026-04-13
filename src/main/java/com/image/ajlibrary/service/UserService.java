package com.image.ajlibrary.service;

import com.image.ajlibrary.dto.LoginRequest;
import com.image.ajlibrary.dto.RegisterRequest;
import com.image.ajlibrary.entity.User;
import com.image.ajlibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Register a new user.
     * NOTE: Password stored as plain text for simplicity.
     * For production, encode with BCryptPasswordEncoder.
     */
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // TODO: BCrypt encode in production
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        return userRepository.save(user);
    }

    /**
     * Authenticate a user by username and password.
     * Returns the User object on success; throws on failure.
     */
    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(
                        () -> new IllegalArgumentException("User not found with username: " + request.getUsername()));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
