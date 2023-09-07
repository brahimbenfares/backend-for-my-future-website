package Model;
public class ResetPasswordRequest {
    private String verificationCode;
    private String newPassword;
    private String confirmPassword;
     private String email;

    public ResetPasswordRequest() {
        // Default constructor
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

      public String getEmail() {
        return email;
    }
    
}
