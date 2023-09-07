package Service;

import Model.Product;
import Model.ProductSale;
import Model.ProductView;
import Model.ShippingAddress;
import Model.Customer;
import Model.Order;
import DAO.ProductRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
//import DAO.AccountDAO;
//import Model.Account;
import com.google.cloud.storage.Storage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ProductService {

    private ProductRepository productRepository;
    private Storage storage;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

   // ProductService.java
   public void addProduct(Product product) {
    String name = product.getName();
    String description = product.getDescription();
    float price = product.getPrice();
    int quantity = product.getQuantity();
    int age = product.getAge();
    String category = product.getCategory();
    List<String> pictureUrls = product.getImageUrls();
    

    // Create a new Product instance
    Product newProduct = new Product(name, description, price, quantity, age, category, pictureUrls);
    productRepository.save(newProduct);
  
}

    

   


    public List<String> uploadProductPictures(String productName, MultipartFile[] productPictures) {
        String bucketName = "bucketname";
    List<String> imageUrls = new ArrayList<>();

    for (MultipartFile picture : productPictures) {
        String objectName = "your folder name" + productName + "/" + picture.getOriginalFilename();

        try {
            // Upload the picture to private storage
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
            storage.create(blobInfo, picture.getBytes());

            // Generate a signed URL with very long (100 years) temporary access for the picture
            URL signedUrl = storage.signUrl(blobInfo, 365 * 24 * 100, TimeUnit.HOURS); // 100 years
            imageUrls.add(signedUrl.toString());

        } catch (StorageException e) {
            System.err.println("Storage Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload product picture.", e);
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload product picture.", e);
        }
        

    // Return the signed URLs of the uploaded product pictures as a comma-separated string
   
}

 return imageUrls;
}





public void deleteProduct(int productId) {
    productRepository.deleteProduct(productId);
}

public void updateProduct(Product product) {
    productRepository.updateProduct(product);
}



public String processSale(List<ProductSale> sales, Customer customer, ShippingAddress address) {
    return productRepository.processSale(sales, customer, address);
}


public String confirmOrder(int orderId) {
    return productRepository.confirmOrder(orderId);
}


public String cancelOrder(int orderId) {
    try {
        productRepository.cancelOrder(orderId); 
        return "Order cancelled successfully";
    } catch (SQLException e) {
        e.printStackTrace();
        return "Failed to cancel order due to a database error.";
    }
}



public String manualSale(List<ProductSale> sales) {
    return productRepository.manualSale(sales);
}


    public List<Product> getAllProducts() {
        return productRepository.findAll();
     }


    public Product getProductById(int productId) {
    return productRepository.findById(productId);
    }


    public List<Map<String, Object>> getMostSoldProducts() {
        try {
            return productRepository.getMostSoldProducts();
        } catch (SQLException e) {
            e.printStackTrace();  
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> getWeeklySales() {
        try {
            return productRepository.getWeeklySales();
        } catch (SQLException e) {
            e.printStackTrace(); 
            return Collections.emptyList();
        }
    }
    
    public List<Map<String, Object>> getDailyRevenue() {
        try {
            return productRepository.getDailyRevenue();
        } catch (SQLException e) {
            e.printStackTrace(); 
            return Collections.emptyList();
        }
    }



// Add this method in your ProductService
public void removeUnreadOrder(int orderId) {
    try {
        productRepository.removeUnreadOrder(orderId);
    } catch (SQLException e) {
        throw new RuntimeException("Failed to remove unread order", e);
    }
}



public int getUnreadOrdersCount() {
    return this.productRepository.getUnreadOrdersCount();
}

    // Add this method in your ProductService
public void addUnreadOrder(int orderId) {
    try {
        productRepository.addUnreadOrder(orderId);
    } catch (SQLException e) {
        throw new RuntimeException("Failed to add unread order", e);
    }
}

public List<Order> getAllOrders() throws SQLException {
    return productRepository.getAllOrders();
}



public Order getOrderById(int orderId) {
    try {
        return productRepository.getOrderById(orderId); // Assuming that you have a getOrderById method in your ProductRepository
    } catch (SQLException e) {
        throw new RuntimeException("Failed to retrieve order with ID " + orderId, e);
    }
}

public List<Integer> getUnreadOrderIds() { 
       try {
        return productRepository.getUnreadOrderIds();
    } catch (SQLException e) {
        throw new RuntimeException("Failed to retrieve unread order IDs", e);
    }
}

public void incrementProductView(int productId) throws SQLException {
    productRepository.incrementProductView(productId);
}

public List<ProductView> getAllProductViews() throws SQLException {
    return productRepository.getAllProductViews();
}



}