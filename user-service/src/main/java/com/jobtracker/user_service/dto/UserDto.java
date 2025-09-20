package com.jobtracker.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDto {
    private String username;
    private List<String> roles;
}
