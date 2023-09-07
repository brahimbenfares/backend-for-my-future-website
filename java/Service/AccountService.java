package Service;

import DAO.AccountDAO;
import Model.Account;
import java.net.URL;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
// remove import com.google.api.services.storage.Storage.BucketAccessControls.List;
import com.google.cloud.storage.BlobInfo;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AccountService {
    private AccountDAO accountDAO;
    private Storage storage;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);


    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
    public Account createNewUserAccount(Account account) {
        if (!isStrongPassword(account.getPassword())) {
            return null; // Password doesn't meet the strength criteria
        }
    
        if (doesUsernameExist(account.getUsername())) {
            return null; // Username already exists
        }
    
        if (doesEmailAddressExist(account.getEmail())) {
            return null; // Email address already exists
        }
    
         Account createdAccount = accountDAO.createNewUserAccount(account);
        if (createdAccount != null) {
            return createdAccount;
        }
        return null;
    }
    

    public Account getUserAccountLoginByUsername(String username, String password) {
        return accountDAO.getUserAccountByLogin(username, password);
    }

    public Account getUserAccountLoginByEmail(String email, String password) {
        return accountDAO.getUserAccountLoginByEmail(email, password);
    }



public Account getUserAccountByUsername(String username) {
    return accountDAO.getUserAccountByUsername(username);
}





    public boolean doesUsernameExist(String username) {
        return accountDAO.doesUsernameExist(username);
    }

    public boolean doesEmailAddressExist(String email) {
        return accountDAO.doesEmailAddressExist(email);
    }

    private boolean isStrongPassword(String password) {
        // Implement your password strength validation logic here
        // Check for at least 8 characters, uppercase, lowercase, and special characters
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(pattern);
    }

public int getAccountIdFromToken(String verificationCode) {
    return accountDAO.getAccountIdFromToken(verificationCode);
}

    public boolean resetPassword(int accountId, String verificationCode, String newPassword) {
        return accountDAO.resetPassword(accountId, verificationCode, newPassword);
    }


public Account findAccountByEmail(String email) {
    return accountDAO.findAccountByEmail(email);
}


public String generatePasswordResetToken(Account account) {
    logger.info("Generating password reset token for account: " + account);  // new log statement
    String token = accountDAO.generatePasswordResetToken(account);
    if (token == null) {
        logger.warn("Failed to generate password reset token for account: " + account);  // new log statement
    }
    return token;
}




    public void updateUserAccount(Account userAccount) {
        accountDAO.updateUserAccount(userAccount);
    }
    


    public String uploadProfilePicture(String username, byte[] profilePicture) {
    String bucketName = "bucketname";
    String objectName = "name of your folder/" + username;

    try {
        // Upload the profile picture to private storage
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
        storage.create(blobInfo, profilePicture);

        // Generate a signed URL with very long (100 years) temporary access for the profile picture
        URL signedUrl = storage.signUrl(blobInfo, 365 * 24 * 100, TimeUnit.HOURS); // 100 years

        // Return the signed URL of the uploaded profile picture
        return signedUrl.toString();
    } catch (StorageException e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to upload profile picture.");
    }
}


}



