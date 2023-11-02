package ju.example.training_management_system.util;

import ju.example.training_management_system.model.users.Role;

public final class Utils {

    private Utils() {
    }

    public static String getRequiredDashboard(Role role) {
        return switch (role) {
            case ADMIN -> "admin-dashboard";
            case COMPANY -> "company-dashboard";
            case STUDENT -> "student-dashboard";
        };
    }
}
