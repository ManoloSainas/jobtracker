package com.manolo.jobtracker.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {


    public CustomUserDetails getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        return (CustomUserDetails) authentication.getPrincipal();
    }


    public Long getCurrentUserId() {

        return getCurrentUser()
                .getUser()
                .getId();
    }
}