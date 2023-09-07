package Controller;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;

import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import Model.Account;
import Model.Customer;
import Model.JwtUtil;
import Service.AccountService;
import Service.ProductService;  // New import
import Model.ResetPasswordRequest;
import Model.ShippingAddress;
import Model.UnreadOrdersResponse;
import Model.Product;
import Model.ProductSale;
import Model.ProductView;

import java.util.List;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.ArrayList;
import java.util.HashMap;

//import java.util.HashMap;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileUploadBase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Model.Order;
// for admin:
import Model.Admin;
import Model.AdminDTO;
import Service.AdminService;



import DAO.ProductRepository;
import io.jsonwebtoken.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

//import io.javalin.http.UnauthorizedResponse;

public class SocialMediaController {
    private AdminService adminService;
    private AccountService accountService;
    private ProductService productService;  // New field

    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);

    private Credentials loadCredentials() throws IOException {
        System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        FileInputStream credentialsFile = new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
                return GoogleCredentials.fromStream(credentialsFile);
    }
    
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.productService = new ProductService(new ProductRepository());  // Initialize productService
    }

    public io.javalin.Javalin startAPI() throws IOException {
        this.adminService = new AdminService();  // Initialize AdminService
        try {
            // Load credentials from the JSON key file
            Credentials credentials = loadCredentials();

            // Create a client or connection object for Google Cloud Storage
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            // Pass the 'storage' object to your AccountService or any other relevant classes
            accountService.setStorage(storage);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        try {
            System.out.println("Current Path: " + new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        // In your Javalin initialization
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("C:/Users/br3mt/OneDrive/Desktop/brahim/start/start/src/main/webapp", null);
            config.enableCorsForAllOrigins();
        }).start(8082);
        


        //Admin 
                app.post("/admin/login", this::adminLogin);
                app.post("/admin/create", this::createAdmin);
                app.put("/admin/update", this::updatePermissions);
                app.delete("/admin/delete", this::deleteAdmin);
                app.get("/admins", this::getAllAdminsHandler);  

app.get("/protected-api/some-resource", ctx -> {
    // In this example, if the user is authenticated, a simple JSON response is sent back
    ctx.json(new HashMap<String, Object>() {{
        put("message", "You are authorized!");
        put("status", "success");
        put("code", 200);
    }});
});



                //user
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::userAccountLogin);
        app.post("/reset-password", this::resetPasswordHandler);
        app.post("/reset-password-request", this::resetPasswordRequestHandler); // Add this line
        app.get("/protected-endpoint", this::someProtectedEndpoint); // Add this line for the protected endpoint
        app.get("/user", this::getUserInformationHandler);
        app.post("/upload-profile-picture", this::uploadProfilePictureHandler);
        app.delete("/products-deletion/{productId}", this::deleteproducthandler);
        app.put("product-update/{productId}",this::updateproducthandler);
        app.post("/add-product", this::addProductHandler);




        app.get("/products", this::getProductsHandler);
        app.get("/products/{productId}", this::getProductByIdHandler);



        app.post("/process-sale", this::processSale);
        app.post("/orders/{orderId}/confirm", this::confirmOrder);
        app.post("/orders/{orderId}/cancel",this::cancelOrder);

        app.post("/manual-sale", this::manualSaleHandler);


        app.get("/most-sold-products", this::getMostSoldProductsHandler);
        app.get("/weekly-sales", this::getWeeklySalesHandler);
        app.get("/daily-revenue", this::getDailyRevenueHandler);


        app.delete("/unread-orders/{orderId}", this::removeUnreadOrderHandler);
        app.get("/unread-orders/count", this::getUnreadOrdersCountHandler);

        app.post("/unread-orders/{orderId}", this::addUnreadOrderHandler);
        app.get("/unread-orders/orders", this::getUnreadOrdersHandler);
        
        app.get("/orders", this::getAllOrdersHandler);
        app.get("/orders/{orderId}", this::getOrderByIdHandler);

                // Add to your initEndpoints method:
        // Change these lines in your initEndpoints method:
        app.post("/incrementView/{productId}", this::incrementProductViewHandler);
        app.get("/allProductViews", this::getAllProductViewsHandler);

        return app;
    }







    private void createAccountHandler(Context ctx) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Account account = mapper.readValue(ctx.body(), Account.class);

            // Check if username already exists
            if (accountService.doesUsernameExist(account.getUsername())) {
                ctx.status(400).result("Username already exists. Please choose another one.");
                return;
            }

            // Check if email address already exists
            if (accountService.doesEmailAddressExist(account.getEmail())) {
                ctx.status(400).result("Email address already exists. Please use a different one or reset your password.");
                return;
            }

            Account createdAccount = accountService.createNewUserAccount(account);
            if (createdAccount != null && createdAccount.getUsername() != null && createdAccount.getPassword() != null
                    && !createdAccount.getUsername().isEmpty() && createdAccount.getPassword().length() >= 4) {
                ctx.json(createdAccount); // Send the created account as JSON response
            } else {
                ctx.status(400).result("Failed to create account. Please try again.");
            }
        } catch (JsonProcessingException | NullPointerException e) {
            ctx.status(400).result("Invalid request. Please provide valid account details.");
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }






private void someProtectedEndpoint(Context ctx) {
    String authHeader = ctx.header("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        try {
          //  String token = authHeader.substring(7);
           // String username = JwtUtil.validateToken(token);
            // Perform additional authorization or logic here
            ctx.result("Access granted"); // Example response for a successful protected endpoint access
        } catch (io.jsonwebtoken.JwtException e) {
            ctx.status(401).result("Invalid token. Please log in again.");
        }
    } else {
        ctx.status(401).result("Missing token. Please log in.");
    }
}




private void userAccountLogin(Context ctx) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        Account loggedInAccount;

        if(account.getLoginIdentifier().contains("@")) {
            loggedInAccount = accountService.getUserAccountLoginByEmail(account.getLoginIdentifier(), account.getPassword());
        } else {
            loggedInAccount = accountService.getUserAccountLoginByUsername(account.getLoginIdentifier(), account.getPassword());
        }

        if (loggedInAccount != null) {
            String token = JwtUtil.generateToken(loggedInAccount.getUsername());
            ctx.json(Map.of("token", token)); // Return the token as JSON
        } else {
            ctx.status(401).result("Invalid username or password. Please try again.");
        }
    } catch (Exception e) {
        System.err.println("Error in userAccountLogin: " + e.getMessage());
        e.printStackTrace();
        ctx.status(500).result(e.getMessage());
    }
}





  public void resetPasswordHandler(Context ctx) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        ResetPasswordRequest resetRequest = mapper.readValue(ctx.body(), ResetPasswordRequest.class);

        String verificationCode = resetRequest.getVerificationCode();
        String newPassword = resetRequest.getNewPassword();
        String confirmPassword = resetRequest.getConfirmPassword();
        System.out.println("Verification Code: " + verificationCode);

        int accountId = accountService.getAccountIdFromToken(verificationCode);
        if (accountId != -1 && newPassword.equals(confirmPassword)) {
            System.out.println("Executing SQL query to reset password...");
            if (accountService.resetPassword(accountId, verificationCode, newPassword)) {

                System.out.println("Sending response: " + ctx.body()); // Add this line

                ctx.status(200).json(Map.of("status", 200, "message", "Password reset successful"));
            } else {
                System.out.println("Sending response: " + ctx.body()); // Add this line

                ctx.status(400).json(Map.of("status", 400, "message", "Failed to reset password"));
            }
        } else {
            System.out.println("Sending response: " + ctx.body()); // Add this line

            ctx.status(400).json(Map.of("status", 400, "message", "Invalid verification code or passwords do not match"));
        }
    } catch (Exception e) {
        System.out.println("Sending response: " + ctx.body()); // Add this line

        ctx.status(500).json(Map.of("status", 500, "message", "Server error"));
    }
}






private void resetPasswordRequestHandler(Context ctx) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ctx.body());
        String email = jsonNode.get("email").asText();
        logger.info("Processing password reset request for email: " + email);  // new log statement
        Account account = accountService.findAccountByEmail(email);

        if (account != null) {
            logger.info("Account found for email: " + email);  // new log statement
            String token = accountService.generatePasswordResetToken(account);

            if (token != null) {
                ctx.status(200).result("Password reset request processed successfully");
            } else {
                ctx.status(500).result("Failed to generate password reset token");
            }
        } else {
            logger.warn("Account not found for email: " + email);  // new log statement
            ctx.status(400).result("Account not found");
        }
    } catch (Exception e) {
        logger.error("Failed to process password reset request", e);
        ctx.status(500).result("Failed to process password reset request");
    }
}





private void getUserInformationHandler(Context ctx) {
    try {
        String token = ctx.header("Authorization");

        // Wrap the token validation in a try-catch block
        try {
            String username = JwtUtil.getUsernameFromToken(token);
            System.out.println("Username from token: " + username);  // Log the username

            Account userAccount = accountService.getUserAccountByUsername(username);

            System.out.println("User account from service: " + userAccount);  // Log the user account

            if (userAccount != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(userAccount);

                System.out.println("Final JSON result: " + json);  // Log the final JSON result

                ctx.result(json);
                ctx.contentType("application/json");
            } else {
                ctx.status(404).result("User account not found.");
            }
        } catch (SignatureException e) {
            ctx.status(401).result("Invalid token.");
        }

    } catch (Exception e) {
        System.err.println("Error in getUserInformationHandler: " + e.getMessage());
        e.printStackTrace();
        ctx.status(500).result(e.getMessage());
    }
}






private void uploadProfilePictureHandler(Context ctx) {
    try {
        String token = ctx.header("Authorization");
        String username = JwtUtil.getUsernameFromToken(token);
        System.out.println("Username: " + username); // Added debug log

        UploadedFile uploadedFile = ctx.uploadedFile("profilePicture");
        InputStream inputStream = uploadedFile.getContent();

        // Read the input stream into a byte array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] profilePicture = buffer.toByteArray();
        System.out.println("Uploaded file size: " + profilePicture.length); // Added debug log

        // Upload the profile picture and get the signed URL
        String pictureUrl = accountService.uploadProfilePicture(username, profilePicture);
        System.out.println("Returned picture URL: " + pictureUrl); // Added debug log

        // Update the profile picture URL in the user account
        Account userAccount = accountService.getUserAccountByUsername(username);
        userAccount.setProfilePictureUrl(pictureUrl);
        accountService.updateUserAccount(userAccount);

        ctx.result("{\"message\": \"Profile picture uploaded successfully.\", \"profilePictureUrl\": \"" + pictureUrl + "\"}");
        ctx.contentType("application/json");
    } catch (IOException e) {
        e.printStackTrace();
        ctx.status(500).result("Failed to upload profile picture.");
    }
}



private void addProductHandler(Context ctx) {
    try {
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(ctx.req);

        Product product = new Product();
        List<MultipartFile> productPictures = new ArrayList<>();

        if (isMultipart) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            List<FileItem> items = upload.parseRequest(ctx.req);

            // Process the uploaded items
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field
                    String name = item.getFieldName();
                    String value = item.getString();

                    switch (name) {
                        case "name":
                            product.setName(value);
                            break;
                        case "description":
                            product.setDescription(value);
                            break;
                        case "price":
                            product.setPrice(Float.parseFloat(value));
                            break;
                        case "quantity":
                            product.setQuantity(Integer.parseInt(value));
                            break;

                        case "age":
                            product.setAge(Integer.parseInt(value));
                            break; 
                        case "category":
                            product.setCategory(value);
                            break;
                           
                    }
                } else {
                    // Process uploaded file
                    String fieldName = item.getFieldName();
                    String fileName = item.getName();
                    InputStream fileContent = item.getInputStream();

                    MultipartFile picture = new MockMultipartFile(fieldName, fileName,
                        ContentType.APPLICATION_OCTET_STREAM.toString(), fileContent);
                    productPictures.add(picture);
                }
            }

             // Call your service method to upload product pictures and set the returned imageUrls to the product
             List<String> imageUrls = productService.uploadProductPictures(product.getName(), productPictures.toArray(new MultipartFile[0]));
             product.setImageUrls(imageUrls);
 
             // Now call your service method to add the product to the database
             productService.addProduct(product);
             ctx.status(201); // Status: Created
             ctx.json(product); // Send the product as JSON
         } else {
             // Handle error - not a file upload request
             ctx.status(400).result("No file upload request");
         }
     } catch (Exception e) {
         // Handle error
         ctx.status(500).result("Server error: " + e.getMessage());
     }
 }
 


 



private void deleteproducthandler(Context ctx) {
    try {
        int productId = Integer.parseInt(ctx.pathParam("productId"));

        // Call delete product service
        productService.deleteProduct(productId);

        // Send success response
        ctx.status(200).result("Product successfully deleted");

    } catch (NumberFormatException e) {
        logger.error("Invalid product ID format", e);
        ctx.status(400).result("Invalid product ID format");
    } catch (Exception e) {
        logger.error("Error deleting product", e);
        ctx.status(500).result("Error deleting product");
    }
}




private void updateproducthandler(Context ctx) {
    try {
        // Parse request body to product object
        Product productToUpdate = ctx.bodyAsClass(Product.class);
        
        // Call updateProduct method from ProductService
        productService.updateProduct(productToUpdate);
        
        // Send response
        ctx.status(200).result("Product successfully updated.");
        
    } catch (Exception e) {
        ctx.status(500).result("An error occurred while updating the product: " + e.getMessage());
    }
}


public void processSale(Context ctx) {
    // Create a new Gson object
    Gson gson = new Gson();

    // Deserialize the request payload to extract different objects
    JsonObject requestBody = gson.fromJson(ctx.body(), JsonObject.class);
    
    // Deserialize ProductSale list
    List<ProductSale> sales = gson.fromJson(requestBody.get("sales"), new TypeToken<List<ProductSale>>(){}.getType());

    // Deserialize Customer
    Customer customer = gson.fromJson(requestBody.get("customer"), Customer.class);

    // Deserialize ShippingAddress
    ShippingAddress address = gson.fromJson(requestBody.get("address"), ShippingAddress.class);
    System.out.println("Received sales data: " + requestBody.toString());

    String message = productService.processSale(sales, customer, address);
    ctx.result(message);
}


public void confirmOrder(Context ctx) {
    int orderId;
    try {
        orderId = Integer.parseInt(ctx.pathParam("orderId"));
    } catch (NumberFormatException e) {
        ctx.status(400).result("Invalid order ID");
        return;
    }

    String message = productService.confirmOrder(orderId);

    // You can decide how to structure the response based on the message
    if ("Order confirmed successfully".equals(message)) {
        ctx.status(200);
        ctx.result("{\"success\": true}");
    } else {
        ctx.status(500).result("{\"success\": false, \"message\":\"" + message + "\"}");
    }
}


public void cancelOrder(Context ctx) {
    int orderId;
    try {
        orderId = Integer.parseInt(ctx.pathParam("orderId"));
    } catch (NumberFormatException e) {
        ctx.status(400).result("Invalid order ID");
        return;
    }

    String message = productService.cancelOrder(orderId);

    if ("Order cancelled successfully".equals(message)) {
        ctx.status(200);
        ctx.result("{\"success\": true}");
    } else {
        ctx.status(500).result("{\"success\": false, \"message\":\"" + message + "\"}");
    }
}







public void manualSaleHandler(Context ctx) {
    Gson gson = new Gson();
    String requestBody = ctx.body();

    // Log the raw request body to check what's being sent
    System.out.println("Raw Request Body: " + requestBody);

    // Deserialize the request payload to a list of product sales
    List<ProductSale> sales = gson.fromJson(requestBody, new TypeToken<List<ProductSale>>(){}.getType());

    for (ProductSale sale : sales) {
        System.out.println("Deserialized Product ID: " + sale.getProductId());
        System.out.println("Deserialized Quantity: " + sale.getQuantitySold());
    }

    String message = productService.manualSale(sales);
    ctx.result(message);
}





private void getProductsHandler(Context ctx) {
    try {
        List<Product> products = productService.getAllProducts();
        ctx.json(products);
    } catch (Exception e) {
        ctx.status(500).result("Error retrieving products: " + e.getMessage());
    }
}

public void getProductByIdHandler(Context ctx) {
    String productIdString = ctx.pathParam("productId"); // Use "productId" instead of "id"
    int productId = Integer.parseInt(productIdString);
    Product product = productService.getProductById(productId);
    if (product != null) {
        ctx.json(product);
    } else {
        ctx.status(404).result("Product not found");
    }
}


private void getMostSoldProductsHandler(Context ctx) {
    List<Map<String, Object>> result = productService.getMostSoldProducts();
    ctx.json(result);
}

private void getWeeklySalesHandler(Context ctx) {
    List<Map<String, Object>> result = productService.getWeeklySales();
    ctx.json(result);
}

private void getDailyRevenueHandler(Context ctx) {
    List<Map<String, Object>> result = productService.getDailyRevenue();
    ctx.json(result);
}

public void removeUnreadOrderHandler(Context ctx) {
    int orderId = Integer.parseInt(ctx.pathParam("orderId"));
    productService.removeUnreadOrder(orderId);
    ctx.status(204); // 204 No Content means the request was successful but there's no representation to return (i.e. the response is intentionally empty)
}


public void addUnreadOrderHandler(Context ctx) {
    int orderId = Integer.parseInt(ctx.pathParam("orderId"));
    productService.addUnreadOrder(orderId);
    ctx.status(204); // 204 No Content means the request was successful but there's no representation to return (i.e. the response is intentionally empty)
}



private void getUnreadOrdersCountHandler(Context ctx) throws SQLException {
    // Get both the count and unread order IDs
    int count = productService.getUnreadOrdersCount();
    List<Integer> unreadOrderIds = productService.getUnreadOrderIds();
    
    // Create the UnreadOrdersResponse with both count and unreadOrderIds
    UnreadOrdersResponse response = new UnreadOrdersResponse(count, unreadOrderIds);
    
    ctx.json(response);
}

private void getUnreadOrdersHandler(Context ctx) {
    List<Integer> unreadOrderIds = productService.getUnreadOrderIds();
    int count = unreadOrderIds.size();
    ctx.json(new UnreadOrdersResponse(count, unreadOrderIds));
}


public void getAllOrdersHandler(Context ctx) {
    try {
        List<Order> orders = productService.getAllOrders();
        ctx.json(orders);
    } catch (SQLException e) {
        // Handle exception appropriately: e.g., log the exception and send an error response
        ctx.status(500).result("Error retrieving orders from database");
        e.printStackTrace();
    }
}




public void getOrderByIdHandler(Context ctx) throws SQLException {
    String orderIdStr = ctx.pathParam("orderId");

    // Check for null, empty, or "null" strings
    if (orderIdStr == null || orderIdStr.isEmpty() || "null".equals(orderIdStr)) {
        ctx.status(400).result("Invalid order ID provided.");
        return;
    }

    try {
        int orderId = Integer.parseInt(orderIdStr);

        // Fetch the order
        Order order = productService.getOrderById(orderId);
        
        // Handle potential null order
        if (order == null) {
            ctx.status(404).result("Order not found");
        } else {
            ctx.json(order);
        }

    } catch (NumberFormatException e) {
        ctx.status(400).result("Invalid order ID format.");
    }
}

public void incrementProductViewHandler(Context ctx) throws SQLException {
    String productIdStr = ctx.pathParam("productId");

    if (productIdStr == null || productIdStr.isEmpty() || "null".equals(productIdStr)) {
        ctx.status(400).result("Invalid product ID provided.");
        return;
    }

    try {
        int productId = Integer.parseInt(productIdStr);
        productService.incrementProductView(productId);
        ctx.status(204);
    } catch (NumberFormatException e) {
        ctx.status(400).result("Invalid product ID format.");
    }
}

public void getAllProductViewsHandler(Context ctx) throws SQLException {
    List<ProductView> productViews = productService.getAllProductViews();
    ctx.json(productViews);
}


public void adminLogin(Context ctx) {
    try {
        String username = ctx.formParam("username");  
        String password = ctx.formParam("password");  
        Admin admin = adminService.getAdminLogin(username, password);
        
        if (admin != null) {
            // Generate the token
            String token = adminService.generateAdminToken(username);
            logger.debug("Generated Token: " + token);  // Log token to console
            
            
            // Prepare the admin data to send back
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setUsername(admin.getUsername());
            adminDTO.setIsManager(admin.getisManager());
            adminDTO.setPermissions(admin.getPermissions());
            
            // Set the Authorization header with the generated token
            ctx.header("Authorization", "Bearer " + token);
            ctx.header("Access-Control-Expose-Headers", "Authorization");  // Add this line
  
            
            // Send the admin data and status as JSON
            ctx.json(adminDTO).status(200);
        } else {
            ctx.status(401).result("Unauthorized");
        }
    } catch (Exception e) {
        logger.error("Error during admin login", e);
        ctx.status(500).result("Internal Server Error");
    }
}


public void createAdmin(Context ctx) {
    try {
        Admin admin = ctx.bodyAsClass(Admin.class);
        if (adminService.createNewAdmin(admin)) {
            ctx.status(201).json(Map.of("message", "Created")); // Changed to JSON
        } else {
            ctx.status(400).json(Map.of("message", "Bad Request")); // Changed to JSON
        }
    } catch (Exception e) {
        logger.error("Error during admin creation", e);
        ctx.status(500).json(Map.of("message", "Internal Server Error")); // Changed to JSON
    }
}


public void updatePermissions(Context ctx) {
    try {
        String username = ctx.queryParam("username");
        String permissions = ctx.queryParam("permissions");
        if (adminService.updateAdminPermissions(username, permissions)) {
            ctx.status(200).json(Map.of("message", "Updated"));  // Changed to JSON
        } else {
            ctx.status(400).json(Map.of("message", "Bad Request"));  // Changed to JSON
        }
    } catch (Exception e) {
        logger.error("Error during permission update", e);
        ctx.status(500).json(Map.of("message", "Internal Server Error"));  // Changed to JSON
    }
}


public void deleteAdmin(Context ctx) {
    try {
        String username = ctx.queryParam("username");
        if (adminService.deleteAdmin(username)) {
            ctx.status(200).json(Map.of("message", "Deleted"));  // Changed to JSON
        } else {
            ctx.status(400).json(Map.of("message", "Bad Request"));  // Changed to JSON
        }
    } catch (Exception e) {
        logger.error("Error during admin deletion", e);
        ctx.status(500).json(Map.of("message", "Internal Server Error"));  // Changed to JSON
    }
}



public void getAllAdminsHandler(Context ctx) throws SQLException {
    List<Admin> admins = adminService.getAllAdmins();  // Fetch the list of admins from the AdminService
    ctx.json(admins);  // Respond with the list of admins
}



public void requireAuthentication(Context ctx) {
    String authHeader = ctx.header("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        boolean isValid = adminService.validateToken(token);

        if (!isValid) {
            ctx.status(401).result("Unauthorized");
            redirectToLogin(ctx);
            return;
        }
    }
}

private void redirectToLogin(Context ctx) {
    ctx.redirect("http://127.0.0.1:8080/start/start/src/main/webapp/admin_login.html");
}



}