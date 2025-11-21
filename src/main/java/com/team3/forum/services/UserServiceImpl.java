package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
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
    public static final String UPDATE_AUTHORIZATION_ERROR = "You are not authorized to update this user.";
    public static final String ALREADY_BLOCKED_ERROR = "User is already blocked.";
    public static final String NOT_BLOCKED_ERROR = "User is not blocked.";
    public static final String ALREADY_ADMIN_ERROR = "User is already an admin.";
    public static final String BLOCKED_USER_ERROR = "Cannot perform this action on a blocked user.";
    public static final String DELETED_USER_ERROR = "Cannot perform this action on a deleted user.";

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
    public User updateUser(int id, UserUpdateDto dto, int requesterId) {
        User existingUser = userRepository.findById(id);
        User requester = userRepository.findById(requesterId);

        if (existingUser == null) {
            throw new EntityNotFoundException("User", id);
        }

        if (requester == null) {
            throw new EntityNotFoundException("User", requesterId);
        }

        if (requester.getId() != id && !requester.isAdmin()) {
            throw new AuthorizationException(UPDATE_AUTHORIZATION_ERROR);
        }

        if (existingUser.isBlocked()) {
            throw new EntityUpdateConflictException(BLOCKED_USER_ERROR);
        }

        if (existingUser.isDeleted()) {
            throw new EntityUpdateConflictException(DELETED_USER_ERROR);
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
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        if (user.isBlocked()) {
            throw new EntityUpdateConflictException(ALREADY_BLOCKED_ERROR);
        }
        if (user.isDeleted()) {
            throw new EntityUpdateConflictException(DELETED_USER_ERROR);
        }
        user.setBlocked(true);
        return userRepository.save(user);
    }

    @Override
    public User unblockUser(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        if (!user.isBlocked()) {
            throw new EntityUpdateConflictException(NOT_BLOCKED_ERROR);
        }
        if (user.isDeleted()) {
            throw new EntityUpdateConflictException(DELETED_USER_ERROR);
        }
        user.setBlocked(false);
        return userRepository.save(user);
    }

    @Override
    public User promoteToAdmin(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        if (user.isAdmin()) {
            throw new EntityUpdateConflictException(ALREADY_ADMIN_ERROR);
        }
        if (user.isBlocked()) {
            throw new EntityUpdateConflictException(BLOCKED_USER_ERROR);
        }
        if (user.isDeleted()) {
            throw new EntityUpdateConflictException(DELETED_USER_ERROR);
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
