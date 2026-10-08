package com.ecommerce.flashsale_platform.modules.user.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.modules.auth.application.dto.response.UserProfileResponse;
import com.ecommerce.flashsale_platform.modules.user.application.dto.request.UpdateRoleRequest;
import com.ecommerce.flashsale_platform.modules.user.application.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin endpoints for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", userService.getAllUsers()));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserRole(
            @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("User role updated", userService.updateUserRole(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }
}