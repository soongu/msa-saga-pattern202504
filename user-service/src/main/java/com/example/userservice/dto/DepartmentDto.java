package com.example.userservice.dto;

import lombok.Builder;

@Builder
public record DepartmentDto(
        Long id,
        String departmentName,
        String departmentAddress,
        String departmentCode
) {

}
