package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Post save(Post entity) {
        if (entity.getId() == 0) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public Post findById(int id){
        Post result = em.find(Post.class, id);
        if (result == null){
            throw new EntityNotFoundException("Post", "id", String.valueOf(id));
        }
        return result;
    }

    @Override
    public boolean existsById(int id) {
        Post post = em.find(Post.class, id);
        return post != null;
    }

    @Override
    public List<Post> findAll() {
        return em.createQuery("from Post", Post.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        Post result = em.find(Post.class, id);
        if (result == null){
            throw new EntityNotFoundException("Post", "id", String.valueOf(id));
        }
        em.remove(result);
    }

    @Override
    public void delete(Post entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
