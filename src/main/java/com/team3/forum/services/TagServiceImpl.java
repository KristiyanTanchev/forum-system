package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Tag;
import com.team3.forum.models.User;
import com.team3.forum.repositories.TagRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {
    public static final String ADMIN_AUTHORIZATION_ERROR = "Only administrators can manage tags";
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Tag createTag(String name, int userId) {
        User requester = userRepository.findById(userId);
        if (!requester.isAdmin()) {
            throw new AuthorizationException(ADMIN_AUTHORIZATION_ERROR);
        }
        String normalizedName = name.toLowerCase().trim();
        if (tagRepository.findAll().stream().anyMatch(t -> t.getName().equals(normalizedName))) {
            throw new DuplicateEntityException("Tag with this name already exists");
        }
        Tag tag = new Tag();
        tag.setName(normalizedName);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(int id, String name, int userId) {
        User requester = userRepository.findById(userId);
        if (!requester.isAdmin()) {
            throw new AuthorizationException(ADMIN_AUTHORIZATION_ERROR);
        }
        Tag existing = tagRepository.findById(id);
        existing.setName(name.toLowerCase().trim());
        return tagRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findById(int id) {
        return tagRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(int id, int userId) {
        User requester = userRepository.findById(userId);
        if (!requester.isAdmin()) {
            throw new AuthorizationException(ADMIN_AUTHORIZATION_ERROR);
        }
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag", id);
        }
        tagRepository.deleteById(id);
    }
}