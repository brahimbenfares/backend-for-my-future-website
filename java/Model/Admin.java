package Model;

public class Admin {
    private int id;
    private String username;
    private String password;
    private boolean isManager;
    private String permissions;
    
    public Admin() {
        // default constructor
    }
    // The constructor to match AdminDAO
    public Admin(int id, String username, String password, boolean isManager, String permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isManager = isManager;
        this.permissions = permissions;
    }
    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getisManager() {
        return isManager;
    }

    public void setManager(boolean isManager) {
        this.isManager = isManager;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
