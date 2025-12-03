package com.recipes.recipe_backend.gestion_utilisateur.service;

import com.recipes.recipe_backend.dto.ChangePasswordRequest;
import com.recipes.recipe_backend.dto.UpdateProfileRequest;
import com.recipes.recipe_backend.dto.UserDTO;
import com.recipes.recipe_backend.gestion_utilisateur.entity.User;
import com.recipes.recipe_backend.gestion_utilisateur.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ✅ CACHE : Met en cache la liste complète
    @Cacheable(value = "users", key = "'all'")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ✅ CACHE : Met en cache par ID
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToDTO(user);
    }
    
    // ✅ CACHE : Met en cache par username
    @Cacheable(value = "users", key = "'username_' + #username")
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToDTO(user);
    }
    
    // ✅ CACHE : Met en cache par email
    @Cacheable(value = "users", key = "'email_' + #email")
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToDTO(user);
    }

    // ✅ CACHE : Invalide plusieurs caches lors de la mise à jour du profil
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "users", key = "'all'"),
        @CacheEvict(value = "userDetails", key = "@userRepository.findById(#id).orElse(null)?.email")
    })
    public UserDTO updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        String oldEmail = user.getEmail();
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        
        userRepository.save(user);
        
        // Invalider aussi l'ancien et le nouvel email si changé
        if (!oldEmail.equals(user.getEmail())) {
            evictUserCaches(oldEmail, user.getEmail(), user.getUsername());
        }
        
        return convertToDTO(user);
    }
    
    // ✅ CACHE : Invalide les caches lors du changement de mot de passe
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "userDetails", key = "@userRepository.findById(#id).orElse(null)?.email")
    })
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }
        
        // Vérifier que les nouveaux mots de passe correspondent
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Les nouveaux mots de passe ne correspondent pas");
        }
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    // ✅ CACHE : Invalide tous les caches lors de la suppression
    @Caching(evict = {
        @CacheEvict(value = "users", allEntries = true),
        @CacheEvict(value = "userDetails", key = "@userRepository.findById(#id).orElse(null)?.email")
    })
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }
    
    // Méthode helper pour invalider les caches d'un utilisateur
    @Caching(evict = {
        @CacheEvict(value = "users", key = "'email_' + #oldEmail"),
        @CacheEvict(value = "users", key = "'email_' + #newEmail"),
        @CacheEvict(value = "users", key = "'username_' + #username"),
        @CacheEvict(value = "userDetails", key = "#oldEmail"),
        @CacheEvict(value = "userDetails", key = "#newEmail")
    })
    private void evictUserCaches(String oldEmail, String newEmail, String username) {
        // Cette méthode sert uniquement à invalider les caches
    }
    
    // Convertir User en UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setRole(user.getRole().name());
        return dto;
    }
}