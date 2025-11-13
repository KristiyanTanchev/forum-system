package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User save(User entity) {
        if (entity.getId() == 0) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public User findById(int id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        return user;
    }

    @Override
    public boolean existsById(int id) {
        User user = em.find(User.class, id);
        return user != null;
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("from User", User.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        em.remove(user);
    }

    @Override
    public void delete(User entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public User findByUsername(String username) {
      return em.createQuery("from User u where u.username=:username", User.class).setParameter("username",username).getSingleResult();
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }
}
