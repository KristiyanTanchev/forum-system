package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.enums.PostSortField;
import com.team3.forum.models.enums.SortDirection;
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
    public Post findById(int id) {
        return em.createQuery("from Post p where p.isDeleted = false and p.id = :id", Post.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    public boolean existsById(int id) {
        return em.createQuery("select count(p) from Post p where p.isDeleted = false and p.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public List<Post> findAll() {
        return em.createQuery("from Post p where p.isDeleted = false", Post.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        Post result = em.find(Post.class, id);
        if (result == null) {
            throw new EntityNotFoundException("Post", id);
        }
        em.remove(result);
    }

    @Override
    public void delete(Post entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public Post findByAndIsDeleted(int id) {
        return em.createQuery("from Post p where p.isDeleted = true and p.id = :id", Post.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    public List<Post> findPostsInFolderPaginated(int page,
                                                 int size,
                                                 Folder parent,
                                                 PostSortField orderBy,
                                                 SortDirection direction) {
        String query = "from Post p " +
                "where p.isDeleted = false and p.folder = :parent " +
                "order by " + orderBy.getJpqlField() + " " + direction.name();

        return em.createQuery(query, Post.class)
                .setParameter("parent", parent)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }
}
