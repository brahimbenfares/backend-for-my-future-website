package Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Account {
    private int account_id;
    private String username;
    private String password;
    //@JsonProperty("email")
    @JsonProperty("loginIdentifier")
    private String email_address; 
    private String phoneNumber;
    private String confirmPassword;
    private String loginIdentifier;
    private byte[] profilePicture; // Add this field
    private String profilePictureUrl; // Add this field


    public Account() {
        // Default constructor
        this.profilePictureUrl = "default_url";  // Set a default URL or an empty string
    }
 
    public Account(String username, String password, String email_address, String phoneNumber,String profilePictureUrl) {
        this.username = username;
        this.password = password;
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl=profilePictureUrl;
    }   

    public Account(int account_id, String username, String password, String email_address, String phoneNumber,String profilePictureUrl) {
        this.account_id = account_id;
        this.username = username;
        this.password = password;
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl=profilePictureUrl;
    }  
    
    
    public Account(int account_id, String username, String email_address, String phoneNumber, String profilePictureUrl) {
        this.account_id = account_id;
        this.username = username;
       
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
    }
    

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
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

    public String getEmail() {
        return email_address;
    }

    public void setEmail(String email) {
        this.email_address = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

  

     public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

  public Account(int account_id, String username, String email_address, String phoneNumber) {
    this.account_id = account_id;
    this.username = username;
    this.email_address = email_address;
    this.phoneNumber = phoneNumber;
    
}
public byte[] getProfilePicture() {
    return profilePicture;
}

public void setProfilePicture(byte[] profilePicture) {
    this.profilePicture = profilePicture;
}

public String getProfilePictureUrl() {
    return profilePictureUrl;
}

public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
}

 @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return Objects.equals(loginIdentifier, account.loginIdentifier) &&
           Objects.equals(password, account.password);
}


    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + account_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email_address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
               '}';
    }

@JsonCreator
public static Account create(@JsonProperty("username") String username, 
                             @JsonProperty("email_address") String email_address,
                             @JsonProperty("phoneNumber") String phoneNumber,
                             @JsonProperty("profilePictureUrl") String profilePictureUrl) {
    Account account = new Account();
    account.setUsername(username);
    account.setEmail(email_address);
    account.setPhoneNumber(phoneNumber);
    if (profilePictureUrl == null) {
        account.setProfilePictureUrl("default_url");
    } else {
        account.setProfilePictureUrl(profilePictureUrl);
    }
    return account;
}
}