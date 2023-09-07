package Model;

public class Customer {
    private int id;
    private String name;
    private String phoneNumber;
    private String orderMethod; // Pickup or Delivery


public int getId(){
    return id;
}
public void setId(int id){
    this.id=id;
}

public String getname(){
    return name;
}
public void setName(String name){
    this.name=name;
}
public String getphoneNumbe(){
    return phoneNumber;
}
public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
}
public String getorderMethod(){
    return orderMethod;
}
public void setOrderMethod(String orderMethod) {
    this.orderMethod = orderMethod;
}

}