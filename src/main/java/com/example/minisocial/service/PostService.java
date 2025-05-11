package com.example.minisocial.service;

import com.example.minisocial.model.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;

@Stateless
public class PostService {

    @PersistenceContext
    private EntityManager em;

    /* ---------- CRUD ---------- */
    public Post create(Long authorId, String content, String image, String link) {
        User author = em.find(User.class, authorId);
        if (author == null) return null;

        Post p = new Post();
        p.setAuthor(author);
        p.setContent(content);
        p.setImageUrl(image);
        p.setLinkUrl(link);

        em.persist(p);
        em.flush();                    // populate ID immediately
        return p;
    }

    public Post update(Long postId, String content, String image, String link) {
        Post p = em.find(Post.class, postId);
        if (p == null) return null;

        if (content != null) p.setContent(content);
        if (image   != null) p.setImageUrl(image);
        if (link    != null) p.setLinkUrl(link);
        return p;
    }

    public boolean delete(Long postId) {
        Post p = em.find(Post.class, postId);
        if (p == null) return false;
        em.remove(p);
        return true;
    }

    public Post find(Long id) { return em.find(Post.class, id); }

    /* ---------- feed ---------- */
    public List<Post> timeline(Long userId, int max) {
        return em.createQuery("""
            SELECT p FROM Post p
            WHERE p.author.id = :uid
               OR p.author.id IN (
                    SELECT CASE
                           WHEN f.user.id = :uid THEN f.friend.id
                           ELSE f.user.id END
                    FROM Friendship f
                    WHERE (f.user.id = :uid OR f.friend.id = :uid)
                      AND f.status = com.example.minisocial.model.Friendship$Status.ACCEPTED
               )
            ORDER BY p.createdAt DESC
            """, Post.class)
                .setParameter("uid", userId)
                .setMaxResults(max)
                .getResultList();
    }

    /* ---------- likes ---------- */
    public boolean toggleLike(Long postId, Long userId) {
        Post p  = em.find(Post.class, postId);
        User u  = em.find(User.class, userId);
        if (p == null || u == null) return false;

        TypedQuery<PostLike> q = em.createQuery("""
            SELECT l FROM PostLike l
            WHERE l.post.id = :pid AND l.user.id = :uid
            """, PostLike.class)
                .setParameter("pid", postId)
                .setParameter("uid", userId);

        List<PostLike> existing = q.getResultList();

        if (existing.isEmpty()) {
            PostLike like = new PostLike();
            like.setPost(p);
            like.setUser(u);
            em.persist(like);
        } else {
            em.remove(existing.get(0));
        }
        return true;
    }

    /* ---------- comments ---------- */
    public PostComment addComment(Long postId, Long authorId, String text) {
        Post p = em.find(Post.class, postId);
        User a = em.find(User.class, authorId);
        if (p == null || a == null) return null;

        PostComment c = new PostComment();
        c.setPost(p);
        c.setAuthor(a);
        c.setText(text);
        em.persist(c);
        em.flush();
        return c;
    }

    public boolean deleteComment(Long commentId, Long requesterId) {
        PostComment c = em.find(PostComment.class, commentId);
        if (c == null) return false;
        if (!c.getAuthor().getId().equals(requesterId)) return false;
        em.remove(c);
        return true;
    }
}
