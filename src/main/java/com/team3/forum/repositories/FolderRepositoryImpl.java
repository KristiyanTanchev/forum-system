package com.team3.forum.repositories;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Folder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FolderRepositoryImpl implements FolderRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public Folder save(Folder entity) {
        if (entity.getId() == 0) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public Folder findById(int id) {
        Folder folder = em.find(Folder.class, id);
        if (folder == null) {
            throw new EntityNotFoundException("Folder", id);
        }
        return folder;
    }

    @Override
    public boolean existsById(int id) {
        return em.find(Folder.class, id) != null;
    }

    @Override
    public List<Folder> findAll() {
        return em.createQuery("from Folder", Folder.class).getResultList();
    }

    @Override
    public void deleteById(int id) {
        em.remove(findById(id));
    }

    @Override
    public void delete(Folder entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public Folder findBySlug(String slug) {
        return em.createQuery("""
                        from Folder f 
                            where f.slug = :slug
                            order by f.name
                        """, Folder.class)
                .setParameter("slug", slug)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Folder", "slug", slug));
    }

    @Override
    public List<Folder> getFoldersByParentFolder(Folder parentFolder) {
        if (parentFolder == null) {
            return em.createQuery("""
                            from Folder f 
                                where f.parentFolder is null 
                                order by f.name
                            """, Folder.class)
                    .getResultList();
        } else {
            return em.createQuery("""
                            from Folder f 
                                where f.parentFolder = :parentFolder 
                                order by f.name
                            """, Folder.class)
                    .setParameter("parentFolder", parentFolder)
                    .getResultList();
        }
    }

    @Override
    public Folder findByParentFolderAndSlug(Folder parentFolder, String slug) {
        return em.createQuery("""
                                from Folder f 
                                    where f.slug = :slug 
                                        and f.parentFolder = :parentFolder
                                    order by f.name
                                """
                        , Folder.class)
                .setParameter("slug", slug)
                .setParameter("parentFolder", parentFolder)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Folder", "slug", slug));
    }
}
