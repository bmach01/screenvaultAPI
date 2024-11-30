package com.screenvault.screenvaultAPI.user;

public enum UserRole {
    ADMIN(0),
    USER(1),
    ANONYMOUS(2);

    public final int grade;

    UserRole(int grade) {
        this.grade = grade;
    }
}
