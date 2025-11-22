package com.leavems.leave_management_system.service;

import com.leavems.leave_management_system.model.Role;
import com.leavems.leave_management_system.model.User;
import com.leavems.leave_management_system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User u) {
        if (u.getPasswordHash() != null) {
            u.setPasswordHash(passwordEncoder.encode(u.getPasswordHash()));
        }
        return userRepository.save(u);
    }

    @PostConstruct
    public void seedDefaultUsers() {
        // idempotent seed: create sample student, instructor, admin if not exist
        if (userRepository.findByEmail("student1@example.com").isEmpty()) {
            userRepository.save(User.builder()
                    .username("student1")
                    .email("student1@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.STUDENT)
                    .build());
        }
        if (userRepository.findByEmail("instructor1@example.com").isEmpty()) {
            userRepository.save(User.builder()
                    .username("instructor1")
                    .email("instructor1@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.INSTRUCTOR)
                    .build());
        }
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .passwordHash(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .build());
        }
    }
}
