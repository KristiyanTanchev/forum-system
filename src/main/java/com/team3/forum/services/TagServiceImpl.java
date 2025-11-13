package com.team3.forum.services;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Tag;
import com.team3.forum.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        // Normalize tag name to lowercase as required
        tag.setName(tag.getName().toLowerCase().trim());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(int id, Tag tag) {
        Tag existing = tagRepository.findById(id);
        existing.setName(tag.getName().toLowerCase().trim());
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
    public void deleteById(int id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag", id);
        }
        tagRepository.deleteById(id);
    }
}