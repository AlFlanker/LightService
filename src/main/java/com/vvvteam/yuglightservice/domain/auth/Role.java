package com.vvvteam.yuglightservice.domain.auth;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
    USER(3),SuperUser(2),SuperUserOwner(1),ADMIN(0);
    private int rang;

    @Override
    public String getAuthority() {
        return name();
    }

    Role(int rang) {
        this.rang = rang;
    }

    public int getRang(){return rang;}
}
