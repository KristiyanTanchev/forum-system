package com.team3.forum.repositories;

import com.team3.forum.models.Post;
import com.team3.forum.models.PostView;
import com.team3.forum.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class PostViewRepositoryImpl implements PostViewRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public void registerView(int postId, int userId) {
        Post postRef = em.getReference(Post.class, postId);
        User userRef = em.getReference(User.class, userId);

        PostView view = PostView
                .builder()
                .post(postRef)
                .user(userRef)
                .build();

        em.persist(view);
    }

    @Override
    public long getTotalViewsForPost(int postId) {
        return em.createQuery("""
                        SELECT COUNT(pv)
                        FROM PostView pv
                        WHERE pv.post.id = :postId
                        """, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

    }

    @Override
    public boolean existsForDate(int postId, int userId, LocalDate viewDate) {
        return em.createQuery("""
                        select count(pv) from PostView pv
                            where pv.post.id = :postId
                            and pv.user.id = :userId
                            and pv.viewDate = :viewDate
                        """, Long.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .setParameter("viewDate", viewDate)
                .getSingleResult() > 0;
    }
}
