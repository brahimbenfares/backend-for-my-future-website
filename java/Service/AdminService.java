package Service;

import Model.Admin;

import java.util.List;

import DAO.AdminDAO;
import Model.JwtUtil;

public class AdminService {
    private AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    public Admin getAdminLogin(String username, String password) {
        return adminDAO.getAdminByLogin(username, password);
    }

    public boolean doesAdminExist(String username) {
        return adminDAO.doesAdminExist(username);
    }

    public boolean createNewAdmin(Admin admin) {
        return adminDAO.createNewAdmin(admin);
    }

    public boolean updateAdminPermissions(String username, String permissions) {
        return adminDAO.updateAdminPermissions(username, permissions);
    }

    public boolean deleteAdmin(String username) {
        return adminDAO.deleteAdmin(username);
    }

    public String generateAdminToken(String username) {
        return JwtUtil.generateToken(username);
    }

    public boolean validateToken(String token) {
        // Assume JwtUtil.validateToken() would return null for invalid tokens
        // and non-null for valid tokens
        return JwtUtil.validateToken(token) != null;
    }
    
    public List<Admin> getAllAdmins() {
        return adminDAO.getAllAdmins();
    }
}
