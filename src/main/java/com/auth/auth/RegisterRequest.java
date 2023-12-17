package com.auth.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public boolean haveAllFields() {
        return (firstname != null && lastname != null && email != null && password != null);
    }
}
