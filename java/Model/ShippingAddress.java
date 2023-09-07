package Model;


public class ShippingAddress {
    private int id;
    private int customerId;
    private String deliveryAddress;
    private String city;
    private String state;
    private String zipCode;

public int getid(){
    return id;
}
public void setId(int id) {
    this.id = id;
}

public int getcustomerId(){
    return customerId;
}
public void setcustomerId(int customerId){
    this.customerId=customerId;
}

public String getdeliveryAddress(){
    return deliveryAddress;
}
public void setDeliveryAddress(String deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
}

public String getcity(){
    return city;
}
public void setCity(String city) {
    this.city = city;
}

public String getstate(){
    return state;
}
public void setState(String state) {
    this.state = state;
}

public String getzipCode(){
    return zipCode;
}
public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
}
}

