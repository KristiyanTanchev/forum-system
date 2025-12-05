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

    public int getUsersCount() {
        return em.createQuery("select count(u) from User u where u.isDeleted = false", Long.class)
                .getSingleResult().intValue();
    }

    @Override
    public List<User> findAllWithFilterPaginated(int page, int size, String searchQuery,
                                                 String statusFilter, String sortBy, String direction) {

        StringBuilder queryString = new StringBuilder("from User u ");
        queryString.append(buildUserWhereClause(searchQuery, statusFilter));

        String orderBy = buildOrderByClause(sortBy, direction);
        queryString.append(" ").append(orderBy);

        var query = em.createQuery(queryString.toString(), User.class);

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.toLowerCase() + "%");
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int countUsersWithFilters(String searchQuery, String statusFilter) {
        StringBuilder queryString = new StringBuilder(" SELECT COUNT(u) from User u ");
        queryString.append(buildUserWhereClause(searchQuery, statusFilter));

        var query = em.createQuery(queryString.toString(), Long.class);

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("search", "%" + searchQuery.toLowerCase() + "%");
        }
        return query.getSingleResult().intValue();
    }

    @Override
    public int getBlockedUsersCount() {
        return em.createQuery("select count(u) from User u " +
                        "where u.isDeleted = false and u.isBlocked = true", Long.class)
                .getSingleResult().intValue();
    }

    private String buildOrderByClause(String sortBy, String direction) {
        String dir = "desc".equalsIgnoreCase(direction) ? "desc" : "asc";

        return switch (sortBy != null ? sortBy : "username") {
            case "email" -> "order by u.email " + dir;
            case "firstName" -> "order by u.firstName " + dir;
            default -> "order by u.username " + dir;
        };
    }

    private StringBuilder buildUserWhereClause(String searchQuery, String statusFilter) {
        StringBuilder whereClause = new StringBuilder("where u.isDeleted = false ");

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            whereClause.append("and (lower(u.username) like :search ")
                    .append("or lower(u.email) like :search ")
                    .append("or lower(u.firstName) like :search) ");
        }

        if (statusFilter != null && !statusFilter.isEmpty()) {
            switch (statusFilter) {
                case "active":
                    whereClause.append("and u.isBlocked = false ");
                    break;
                case "blocked":
                    whereClause.append("and u.isBlocked = true ");
                    break;
                case "admin":
                    whereClause.append("and u.role = 'ADMIN' ");
                    break;
                case "moderator":
                    whereClause.append("and u.role = 'MODERATOR' ");
                    break;
                case "user":
                    whereClause.append("and u.role = 'USER' ");
                    break;
            }
        }
        return whereClause;
    }
}
