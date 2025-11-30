package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Tag save(Tag entity) {
        if (entity.getId() == 0) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public Tag findById(int id) {
        Tag result = em.find(Tag.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Tag", id);
        }
        return result;
    }

    @Override
    public boolean existsById(int id) {
        return em.find(Tag.class, id) != null;
    }

    @Override
    public List<Tag> findAll() {
        return em.createQuery("from Tag", Tag.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        Tag tag = em.find(Tag.class, id);
        if (tag == null) {
            throw new EntityNotFoundException("Tag", id);
        }
        em.remove(tag);
    }

    @Override
    public void delete(Tag entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public List<Tag> findTopByOrderByPostsCountDesc(int limit) {
        return em.createQuery("from Tag t order by size(t.posts) desc", Tag.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
