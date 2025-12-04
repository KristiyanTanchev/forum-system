package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.enums.PostSortField;
import com.team3.forum.models.enums.SortDirection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Override
    public List<Post> findPostsInFolderPaginated(int page,
                                                 int size,
                                                 Folder parent,
                                                 PostSortField orderBy,
                                                 SortDirection direction) {

        StringBuilder queryString = new StringBuilder("from Post p where p.isDeleted = false");

        if (parent != null) {
            queryString.append(" and p.folder = :parent");
        }

        queryString.append(" order by ")
                .append(orderBy.getJpqlField())
                .append(' ')
                .append(direction.name());

        var query = em.createQuery(queryString.toString(), Post.class);

        if (parent != null) {
            query.setParameter("parent", parent);
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Post> findPostsInFolderWithTagPaginated(int page,
                                                        int size,
                                                        String searchQuery,
                                                        Folder parent,
                                                        PostSortField orderBy,
                                                        SortDirection direction,
                                                        int tagId) {
        StringBuilder queryString = new StringBuilder("select distinct p from Post p left join p.tags t where p.isDeleted = false");

        if (parent != null) {
            queryString.append(" and p.folder = :parent");
        }
        if (tagId != 0) {
            queryString.append(" and t.id = :tagId");
        }
        if (searchQuery != null && !searchQuery.isBlank()) {
            queryString.append(" and lower(p.title) like lower(:search)");
        }

        queryString.append(" order by ")
                .append(orderBy.getJpqlField())
                .append(' ')
                .append(direction.name());

        var query = em.createQuery(queryString.toString(), Post.class);

        if (tagId != 0) {
            query.setParameter("tagId", tagId);
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            query.setParameter("search", "%" + searchQuery.toLowerCase() + "%");
        }

        if (parent != null) {
            query.setParameter("parent", parent);
        }

        return query
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public int countPostsInFolderWithTag(Folder parent, int tagId) {
        StringBuilder queryString = new StringBuilder(
                "select count(distinct p) from Post p left join p.tags t where p.isDeleted = false"
        );

        if (parent != null) {
            queryString.append(" and p.folder = :parent");
        }
        if (tagId != 0) {
            queryString.append(" and t.id = :tagId");
        }

        var query = em.createQuery(queryString.toString(), Long.class);

        if (parent != null) {
            query.setParameter("parent", parent);
        }
        if (tagId != 0) {
            query.setParameter("tagId", tagId);
        }

        return query.getSingleResult().intValue();
    }

    @Override
    public int countPostsInFolderWithTagAndSearch(Folder parent, int tagId, String searchQuery) {
        StringBuilder queryString = new StringBuilder(
                "select count(distinct p) from Post p left join p.tags t where p.isDeleted = false"
        );

        if (parent != null) {
            queryString.append(" and p.folder = :parent");
        }
        if (tagId != 0) {
            queryString.append(" and t.id = :tagId");
        }
        if (searchQuery != null && !searchQuery.isBlank()) {
            queryString.append(" and lower(p.title) like lower(:search)");
        }

        var query = em.createQuery(queryString.toString(), Long.class);

        if (parent != null) {
            query.setParameter("parent", parent);
        }
        if (tagId != 0) {
            query.setParameter("tagId", tagId);
        }
        if (searchQuery != null && !searchQuery.isBlank()) {
            query.setParameter("search", "%" + searchQuery.toLowerCase() + "%");
        }

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Post> findAllSortedByViewsLastDays(int limit, int days) {
        LocalDate since = LocalDate.now().minusDays(days);

        String query = """
                select p
                from Post p
                left join PostView pv
                    on pv.post = p
                    and pv.viewDate >= :since
                where p.isDeleted = false
                group by p
                order by count(pv) desc
                """;

        return em.createQuery(query, Post.class)
                .setParameter("since", since)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public int getPostsCount() {
        return em.createQuery("select count(p) from Post p where p.isDeleted = false", Long.class)
                .getSingleResult().intValue();
    }
}
