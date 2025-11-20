package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        try {
            return em.createQuery(
                            "from User u where u.id = :id and u.isDeleted = false",
                            User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User", id);
        }
    }

    @Override
    public boolean existsById(int id) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.id = :id and u.isDeleted = false",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("from User u where u.isDeleted = false", User.class).getResultList();
    }

    @Override
    public void softDeleteById(int id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        user.setDeleted(true);
        em.merge(user);
    }

    @Override
    public void restoreById(int id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User", id);
        }
        user.setDeleted(false);
        em.merge(user);
    }

    @Override
    public User findByUsername(String username) {
        try {
            return em.createQuery("from User u where u.username=:username and u.isDeleted = false", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User", "username", username);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                        "select count(u) from User u where u.email = :email and u.isDeleted = false",
                        Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return em.createQuery("from User u where u.email=:email and u.isDeleted = false", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User", "email", email);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "select count(u) from User u where u.username = :username and u.isDeleted = false",
                        Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        return em.createQuery("from User u where u.isDeleted = false and (" +
                        " u.username like :search" +
                        " or u.email like :search " +
                        "or u.firstName like :search) ", User.class)
                .setParameter("search", searchPattern)
                .getResultList();
    }
}
