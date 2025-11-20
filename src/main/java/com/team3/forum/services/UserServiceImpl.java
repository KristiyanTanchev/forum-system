package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import com.team3.forum.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User createUser(UserCreateDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateEntityException("User", "username", dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntityException("User", "email", dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAdmin(false);
        user.setBlocked(false);
        user.setDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(int id, UserUpdateDto dto, String currentUsername) {
        User existingUser = userRepository.findById(id);
        User currentUser = userRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new AuthorizationException("Current user not found");
        }

        if (currentUser.getId() != id && !currentUser.isAdmin()) {
            throw new AuthorizationException("You are not authorized to update this user");
        }

        if (existingUser.isBlocked()) {
            throw new AuthorizationException("Cannot update a blocked user");
        }

        if (existingUser.isDeleted()) {
            throw new AuthorizationException("Cannot update a deleted user");
        }

        if (!existingUser.getEmail().equals(dto.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateEntityException("User", "email", dto.getEmail());
            }
        }
        userMapper.updateEntityFromDto(dto, existingUser);
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User blockUser(int id) {
        User user = userRepository.findById(id);

        if (user.isBlocked()) {
            throw new IllegalStateException("User is already blocked");
        }

        if (user.isDeleted()) {
            throw new IllegalStateException("Cannot block a deleted user");
        }

        user.setBlocked(true);
        return userRepository.save(user);
    }

    @Override
    public User unblockUser(int id) {
        User user = userRepository.findById(id);

        if (!user.isBlocked()) {
            throw new IllegalStateException("User is not blocked");
        }

        if (user.isDeleted()) {
            throw new IllegalStateException("Cannot unblock a deleted user");
        }

        user.setBlocked(false);
        return userRepository.save(user);
    }

    @Override
    public User promoteToAdmin(int id) {
        User user = userRepository.findById(id);

        if (user.isAdmin()) {
            throw new IllegalStateException("User is already an admin");
        }

        if (user.isBlocked()) {
            throw new IllegalStateException("Cannot promote a blocked user to admin");
        }

        if (user.isDeleted()) {
            throw new IllegalStateException("Cannot promote a deleted user to admin");
        }

        user.setAdmin(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Override
    public void softDeleteById(int id) {
        userRepository.softDeleteById(id);
    }

    @Override
    public void restoreById(int id) {
        userRepository.restoreById(id);
    }
}
