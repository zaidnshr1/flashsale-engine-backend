package com.ecommerce.flashsale_platform.modules.user.application.dto.request;

import com.ecommerce.flashsale_platform.modules.auth.domain.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;
}