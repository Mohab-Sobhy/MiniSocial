package com.example.minisocial.service.PostManagement;

import com.example.minisocial.model.Post;
import com.example.minisocial.model.PostComment;
import com.example.minisocial.model.PostLike;
import com.example.minisocial.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;

@Stateless
public class PostService {

    @PersistenceContext
    private EntityManager em;

    public Post createPost(Long userId, String content, String imageUrl, String linkUrl) {
        User author = em.find(User.class, userId);
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setLinkUrl(linkUrl);
        em.persist(post);
        return post;
    }

    public List<Post> getTimeline(Long userId) {
        // استعلام 1: الأصدقاء الذين أرسل لهم المستخدم طلبات صداقة
        List<Long> sentFriendIds = em.createQuery(
                        "SELECT f.friend.id FROM Friendship f WHERE f.user.id = :uid", Long.class)
                .setParameter("uid", userId)
                .getResultList();

        // استعلام 2: الأصدقاء الذين أرسلوا له طلبات صداقة
        List<Long> receivedFriendIds = em.createQuery(
                        "SELECT f.user.id FROM Friendship f WHERE f.friend.id = :uid", Long.class)
                .setParameter("uid", userId)
                .getResultList();

        // دمج القوائم بدون تكرار
        Set<Long> friendIds = new java.util.HashSet<>();
        friendIds.addAll(sentFriendIds);
        friendIds.addAll(receivedFriendIds);
        friendIds.add(userId); // تضمين منشورات المستخدم نفسه

        if (friendIds.isEmpty()) {
            return List.of(); // لا داعي لتنفيذ استعلام إذا لا يوجد أي معرفات
        }

        // استعلام المنشورات
        return em.createQuery(
                        "SELECT p FROM Post p WHERE p.author.id IN :friendIds ORDER BY p.createdAt DESC", Post.class)
                .setParameter("friendIds", friendIds)
                .getResultList();
    }


    public Post updatePost(Long postId, Long userId, String newContent, String imageUrl, String linkUrl) {
        Post post = em.find(Post.class, postId);
        if (!post.getAuthor().getId().equals(userId)) throw new SecurityException("Not allowed");
        post.setContent(newContent);
        post.setImageUrl(imageUrl);
        post.setLinkUrl(linkUrl);
        return em.merge(post);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        if (!post.getAuthor().getId().equals(userId)) throw new SecurityException("Not allowed");
        em.remove(post);
    }

    public PostLike likePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        em.persist(like);
        return like;
    }

    public PostComment commentPost(Long postId, Long userId, String text) {
        Post post = em.find(Post.class, postId);
        User user = em.find(User.class, userId);

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setText(text);
        em.persist(comment);
        return comment;
    }
}
