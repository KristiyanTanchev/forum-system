package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Comment save(Comment entity) {
        if (entity.getId() == 0) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public Comment findById(int id) {
        Comment result = em.find(Comment.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Comment", id);
        }
        return result;
    }

    @Override
    public boolean existsById(int id) {
        return em.find(Comment.class, id) != null;
    }

    @Override
    public List<Comment> findAll() {
        return em.createQuery("from Comment", Comment.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        Comment comment = findById(id);
        em.remove(comment);
    }

    @Override
    public void delete(Comment entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public List<Comment> findByPostId(int postId) {
        return em.createQuery(
                        "SELECT c FROM Comment c WHERE c.post.id = :postId", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}