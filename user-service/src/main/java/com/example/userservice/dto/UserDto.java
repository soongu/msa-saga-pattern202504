package com.example.userservice.dto;

import com.example.userservice.entity.User;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {

    public static UserDto from(User user) {
        return UserDto.builder()
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .id(user.getId())
                .build()
                ;
    }
}
