package com.ecommerce.flashsale_platform.modules.auth.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String role;
}
