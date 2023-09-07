package Model;

public class ProductSale {
    private int productId;
    private int quantitySold; // For current uses
    private int quantity; // For the new payload
    private String orderType;

    // Getter and Setter for productId
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Getter and Setter for quantitySold
    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    // Getter and Setter for orderType
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // This method helps consolidate the two fields, giving preference to quantity if it's set
    public int getEffectiveQuantity() {
        return quantity != 0 ? quantity : quantitySold;
    }
}
