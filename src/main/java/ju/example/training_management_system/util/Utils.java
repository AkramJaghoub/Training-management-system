package ju.example.training_management_system.util;

import ju.example.training_management_system.model.Role;

public final class Utils {

    private Utils() {
    }

    public static String getRequiredDashboard(Role role) {
        return switch (role) {
            case ADMIN -> "admin_dashboard";
            case COMPANY -> "company_dashboard";
            case STUDENT -> "student_dashboard";
        };
    }
}
