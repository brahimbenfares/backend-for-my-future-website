package Model;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Order {
    private int id;
    private Date date;
    private Time time;
    private String type; // Pickup or Delivery
    private String status;
    private List<Product> products;
    private Customer customer;
    private ShippingAddress shippingAddress;
    private String combinedDatetime;
    private String orderStatus; // or you can use an Enum, but let's use String for simplicity

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getCombinedDatetime() {
        return combinedDatetime;
    }
    public void setCombinedDatetime(String combinedDatetime) {
        this.combinedDatetime = combinedDatetime;
    }
    // Getters
    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
