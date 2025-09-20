package com.jobtracker.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
}
