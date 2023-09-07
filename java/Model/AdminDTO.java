package Model;


 public class AdminDTO {
    private String username;
    private boolean isManager;
    private String permissions;

    // Default constructor
    public AdminDTO() {
    }

    // Parameterized constructor
    public AdminDTO(String username, boolean isManager, String permissions) {
        this.username = username;
        this.isManager = isManager;
        this.permissions = permissions;
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
