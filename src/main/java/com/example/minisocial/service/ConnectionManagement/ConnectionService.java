package com.example.minisocial.service.ConnectionManagement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.example.minisocial.model.User;
import com.example.minisocial.model.Friendship;
import com.example.minisocial.DTO.UserDTO;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConnectionService {

    @PersistenceContext
    EntityManager em;

    private static final String SECRET_KEY = "yourSecretKey"; // Replace with your actual secret

    // 🔓 استخراج Claims من JWT
    private Claims decodeJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(jwt)
                .getBody();
    }

    private User getUserFromToken(String token) {
        String email = decodeJWT(token).getSubject(); // هنا الـ sub هو الإيميل
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<UserDTO> getFriends(String token) {
        User user = getUserFromToken(token);

        return em.createQuery("SELECT f.friend FROM Friendship f WHERE f.user.id = :uid AND f.status = :status", User.class)
                .setParameter("uid", user.getId())
                .setParameter("status", Friendship.Status.ACCEPTED)
                .getResultList()
                .stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setBio(u.getBio());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
