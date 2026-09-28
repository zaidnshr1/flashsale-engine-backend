package com.ecommerce.flashsale_platform.modules.auth.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Operations", description = "Protected endpoints restricted to ROLE_ADMIN")
public class AdminTestController {

    @GetMapping("/ping")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Health check endpoint strictly for Admins")
    public ResponseEntity<ApiResponse<String>> pingAdmin() {
        return ResponseEntity.ok(ApiResponse.ok("Access granted: Welcome to Admin Area", "PONG"));
    }
}
