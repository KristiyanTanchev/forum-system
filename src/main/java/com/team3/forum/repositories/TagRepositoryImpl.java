package com.team3.forum.repositories;

import com.team3.forum.models.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository{
    @Override
    public Tag save(Tag entity) {
        return null;
    }

    @Override
    public Tag findById(int id) {
        return null;
    }

    @Override
    public boolean existsById(int id) {
        return false;
    }

    @Override
    public List<Tag> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void delete(Tag entity) {

    }
}
