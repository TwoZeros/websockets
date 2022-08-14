package com.example.websockets.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    String login;
    String password;
}
