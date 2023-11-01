package ju.example.training_management_system.model;

import ju.example.training_management_system.exception.NoRoleFoundException;

import java.util.Locale;

public enum Role {
    ADMIN,
    COMPANY,
    STUDENT;

    public static Role toRole(String role){
        return switch (role.toUpperCase()){
            case "STUDENT" ->  Role.STUDENT;
            case "COMPANY" -> Role.COMPANY;
            case "ADMIN" -> Role.ADMIN;
            default -> throw new NoRoleFoundException();
        };
    }
}