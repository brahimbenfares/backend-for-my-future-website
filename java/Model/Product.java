package Model;

import java.util.List;

public class Product {
    private int id;
    private int age;
    private String name;
    private String category;
    private float price;
    private int quantity;
    private String description;
    private List<String> imageUrls;

    public Product() {
        // no-argument constructor
    }
    
    // Constructor with ID
    public Product(int id, String name, String description, float price, int quantity, String category, List<String> imageUrls, int age) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imageUrls = imageUrls;
        this.age = age;
    }

    // Constructor without ID (for creating new product before saving to DB)
    public Product(String name, String description, float price, int quantity,  int age,String category, List<String> imageUrls) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imageUrls = imageUrls; 
        this.age = age;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
