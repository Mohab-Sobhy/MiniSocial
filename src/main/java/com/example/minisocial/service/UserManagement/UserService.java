package com.example.minisocial.service.UserManagement;
import com.example.minisocial.model.User;
import com.example.minisocial.util.JwtUtil;
import com.example.minisocial.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    JwtUtil jwtUtil;

    @Transactional
    public User register(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        // تشفير كلمة المرور قبل الحفظ
        String hashedPassword = PasswordUtil.encryptPassword(user.getHashedPassword());
        user.setHashedPassword(hashedPassword);

        entityManager.persist(user);
        return user;
    }

    public String login(String email, String password) {
        User user = findByEmail(email);
        if (user == null || !PasswordUtil.matches(password, user.getHashedPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return jwtUtil.generateToken(user.getId());
    }

    public User findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public User updateProfile(Long id, User updatedUser) {
        User existingUser = findById(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getBio() != null) {
            existingUser.setBio(updatedUser.getBio());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (findByEmail(updatedUser.getEmail()) != null) {
                throw new RuntimeException("Email already in use");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getHashedPassword() != null) {
            String hashedPassword = PasswordUtil.encryptPassword(updatedUser.getHashedPassword());
            existingUser.setHashedPassword(hashedPassword);
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        return entityManager.merge(existingUser);
    }

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }
}