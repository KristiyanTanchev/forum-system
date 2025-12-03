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
        return em.createQuery("from Comment c where c.isDeleted = false and c.id = :id", Comment.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
    }

    @Override
    public boolean existsById(int id) {
        return em.createQuery("select count(c) from Comment c where c.isDeleted = false and c.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public List<Comment> findAll() {
        return em.createQuery("from Comment c where c.isDeleted = false", Comment.class).getResultList();
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
                        "SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public Comment findByIdIncludeDeleted(int id) {
        Comment result = em.find(Comment.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Comment", id);
        }
        return result;
    }

    @Override
    public int getCommentCount() {
        return em.createQuery("select count(c) from Comment c where c.isDeleted = false", Long.class)
                .getSingleResult().intValue();
    }
}