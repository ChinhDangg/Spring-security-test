package com.auth.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;

    public boolean haveAllFields() {
        return (email != null && password != null);
    }
}
