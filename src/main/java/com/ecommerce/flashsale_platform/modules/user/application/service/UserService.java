package com.ecommerce.flashsale_platform.modules.user.application.service;

import com.ecommerce.flashsale_platform.common.exception.ResourceNotFoundException;
import com.ecommerce.flashsale_platform.modules.auth.application.dto.response.UserProfileResponse;
import com.ecommerce.flashsale_platform.modules.auth.domain.model.User;
import com.ecommerce.flashsale_platform.modules.auth.domain.repository.UserRepository;
import com.ecommerce.flashsale_platform.modules.user.application.dto.request.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .toList();
    }

    @Transactional
    public UserProfileResponse updateUserRole(Long userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(request.getRole());
        return mapToProfileResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }
}