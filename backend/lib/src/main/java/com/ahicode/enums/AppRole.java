package com.ahicode.enums;

import org.springframework.security.core.GrantedAuthority;

public enum AppRole implements GrantedAuthority {
    ADMIN_ROLE,
    USER_ROLE;


    @Override
    public String getAuthority() {
        return name();
    }
}
