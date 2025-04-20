package com.example.userservice.dto;

import lombok.Builder;

@Builder
public record ResponseDto(
        DepartmentDto department,
        UserDto user
) {
}
