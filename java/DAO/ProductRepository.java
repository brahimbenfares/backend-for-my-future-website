package DAO;

import Model.Customer;
import Model.Order;
import Model.Product;
import Model.ProductSale;
import Model.ProductView;
import Model.ShippingAddress;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ProductRepository {

    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    static {
        // Load DB credentials from config
        Properties properties = new Properties();
        try (InputStream input = ProductRepository.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            properties.load(input);
            DB_URL = properties.getProperty("dbUrl");
            DB_USERNAME = properties.getProperty("dbUsername");
            DB_PASSWORD = properties.getProperty("dbPassword");
            
            // Test the connection
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                System.out.println("Successfully connected to database in ProductRepository.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to the database in ProductRepository.");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read DB credentials from config", ex);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    
    public Product save(Product product) {
        String sql = "INSERT INTO products(name, description, price, quantity, category, image_url, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Product savedProduct = null;
    
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setFloat(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getCategory());
            stmt.setInt(7, product.getAge());

            String joinedImageUrls = String.join(",", product.getImageUrls());
            stmt.setString(6, joinedImageUrls);
    
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    savedProduct = new Product(generatedKeys.getInt(1), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity(), product.getDescription(), product.getImageUrls(), product.getAge());
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return savedProduct;  // return the new product with updated ID
    } 
    

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("category");
                float price = rs.getFloat("price");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("description");
                List<String> imageUrls = Arrays.stream(rs.getString("image_url").split(","))
                                                .map(String::trim)
                                                .collect(Collectors.toList());
                                                int age = rs.getInt("age");

                Product product = new Product(id, name, category, price, quantity, description, imageUrls, age);
                products.add(product);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

// ProductRepository.java

public Product findById(int productId) {
    String sql = "SELECT * FROM products WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, productId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Retrieve product details from the result set and create a Product object
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String description = rs.getString("category");
            float price = rs.getFloat("price");
            int quantity = rs.getInt("quantity");
            String category = rs.getString("description");
            List<String> imageUrls = Arrays.stream(rs.getString("image_url").split(","))
                                            .map(String::trim)
                                            .collect(Collectors.toList());
            int age = rs.getInt("age");

            return new Product(id, name, category, price, quantity, description, imageUrls, age);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return null; // If product is not found, return null
}


    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
    
            preparedStatement.setInt(1, productId);
            preparedStatement.executeUpdate();
    
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }
    
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, quantity = ? WHERE id = ?";
    
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, product.getName());
            stmt.setFloat(3, product.getPrice());
            stmt.setString(2, product.getCategory());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getId());
    
            int affectedRows = stmt.executeUpdate();
    
            if (affectedRows == 0) {
                throw new SQLException("Updating product failed, no rows affected.");
            }
    
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
 /*    private int getCurrentProductQuantity(int productId, Connection conn) throws SQLException {
    String sqlSelectQuantity = "SELECT quantity FROM products WHERE id = ?";
    
    try (PreparedStatement stmt = conn.prepareStatement(sqlSelectQuantity)) {
        stmt.setInt(1, productId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("quantity");
        } else {
            throw new SQLException("Product not found with ID: " + productId);
        }
    }

}  */




public int insertCustomer(Customer customer, Connection conn) throws SQLException {
    String sql = "INSERT INTO customers (name, phone_number, order_method) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, customer.getname());
        stmt.setString(2, customer.getphoneNumbe());
        stmt.setString(3, customer.getorderMethod());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating customer failed, no rows affected.");
        }
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating customer failed, no ID obtained.");
            }
        }
    }
}

public int insertShippingAddress(ShippingAddress address, Connection conn) throws SQLException {
    String sql = "INSERT INTO shippingaddresses (customer_id, delivery_address, city, state, zip_code) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, address.getcustomerId());
        stmt.setString(2, address.getdeliveryAddress());
        stmt.setString(3, address.getcity());
        stmt.setString(4, address.getstate());
        stmt.setString(5, address.getzipCode());
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating shipping address failed, no rows affected.");
        }
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating shipping address failed, no ID obtained.");
            }
        }
    }
}



public int insertOrder(String orderType, Connection conn) throws SQLException {
    String sql = "INSERT INTO orders (order_date, order_time, order_type) VALUES (CURDATE(), CURTIME(), ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, orderType);
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating order failed, no rows affected.");
        }
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }
        }
    }
}


private void insertOrderItem(int orderId, int productId, int quantitySold, Connection conn) throws SQLException {
    String sql = "INSERT INTO order_items(order_id, product_id, quantity) VALUES (?, ?, ?)";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        stmt.setInt(2, productId);
        stmt.setInt(3, quantitySold);
        
        stmt.executeUpdate();
    }
}








public void insertOrderStatus(int orderId, Connection conn) throws SQLException {
    String sql = "INSERT INTO order_status (order_id) VALUES (?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        stmt.executeUpdate();
    }
}

private void updateProductQuantity(int productId, int quantitySold, Connection conn) throws SQLException {
    String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, quantitySold);
        stmt.setInt(2, productId);
        stmt.executeUpdate();  // You missed this line
    }
}

public String confirmOrder(int orderId) {
    String message = "Order confirmed successfully";
    try (Connection conn = this.getConnection()) {
        // First, update the status
        String updateStatusSQL = "UPDATE order_status SET status = 'confirmed' WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateStatusSQL)) {
            stmt.setInt(1, orderId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Order not found with ID: " + orderId);
            }
        }

        // Now, reduce the product stock based on the order items
        List<ProductSale> sales = getOrderItems(orderId, conn);
        for (ProductSale sale : sales) {
            updateProductQuantity(sale.getProductId(), sale.getQuantitySold(), conn);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        message = "Error confirming order.";
    }
    return message;
}

public void cancelOrder(int orderId) throws SQLException {
    try (Connection conn = this.getConnection()) {
        // Update the status to 'cancelled'
        String updateStatusSQL = "UPDATE order_status SET status = 'cancelled' WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateStatusSQL)) {
            stmt.setInt(1, orderId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Order not found with ID: " + orderId);
            }
        }
        
        // If order is cancelled, add back the quantity to the products table
        List<ProductSale> sales = getOrderItems(orderId, conn);
        for (ProductSale sale : sales) {
            int productId = sale.getProductId();
            int quantity = sale.getQuantitySold();

            String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
            }
        }
    }
}




private List<ProductSale> getOrderItems(int orderId, Connection conn) throws SQLException {
    List<ProductSale> sales = new ArrayList<>();

    String sql = "SELECT product_id, quantity FROM order_items WHERE order_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ProductSale sale = new ProductSale();
            sale.setProductId(rs.getInt("product_id"));
            sale.setQuantitySold(rs.getInt("quantity")); // Assuming the method name is setQuantitySold in ProductSale class
            sales.add(sale);
        }
    }
    
    return sales;
}


public String processSale(List<ProductSale> sales, Customer customer, ShippingAddress address) {
    String message = "Sale processed successfully";
    Connection conn = null;

    try {
        // Start a transaction
        conn = this.getConnection();
        conn.setAutoCommit(false);

        // Insert customer and get generated ID
        int customerId = insertCustomer(customer, conn);
        address.setcustomerId(customerId);
        
        // Insert shipping address for this customer
        int addressId = insertShippingAddress(address, conn);

        // Create the order first to get the orderId
        int orderId = insertOrder(sales.get(0).getOrderType(), conn); // Assuming order type is consistent for all items in the list

        // Update Order table with customerId and addressId
        updateOrderWithCustomerAndAddress(orderId, customerId, addressId, conn); 
        
        insertOrderStatus(orderId, conn);

        // Insert order items
        for (ProductSale sale : sales) {
            int productId = sale.getProductId();
            int quantityToProcess = sale.getEffectiveQuantity();
            insertOrderItem(orderId, productId, quantityToProcess, conn);
        }

        // Commit transaction
        conn.commit();
        
        // Add order to UnreadOrders table
        addUnreadOrder(orderId);

    } catch (SQLException ex) {
        ex.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
        message = "Error processing sale.";
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // Reset auto-commit mode
                conn.close(); // Close the connection
            } catch (SQLException commitEx) {
                commitEx.printStackTrace();
            }
        }
    }
    
    return message;
}



/*private void reserveProductQuantity(int orderId, int productId, int quantityToReserve, Connection conn) throws SQLException {
    String sql = "INSERT INTO product_reservations (order_id, product_id, reserved_quantity) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        stmt.setInt(2, productId);
        stmt.setInt(3, quantityToReserve);
        stmt.executeUpdate();
    }

    // Also reduce actual quantity in products table
    String updateProductSQL = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(updateProductSQL)) {
        stmt.setInt(1, quantityToReserve);
        stmt.setInt(2, productId);
        stmt.executeUpdate();
    }
}*/

// New method to update the Order table with customerId and addressId
private void updateOrderWithCustomerAndAddress(int orderId, int customerId, int addressId, Connection conn) throws SQLException {
    String sql = "UPDATE orders SET customer_id = ?, shipping_id = ? WHERE order_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, customerId);
        stmt.setInt(2, addressId);
        stmt.setInt(3, orderId);
        stmt.executeUpdate();
    }
}





public String manualSale(List<ProductSale> sales) {
    String message = "Sale processed successfully";
    Connection conn = null;

    try {
        conn = this.getConnection();
        conn.setAutoCommit(false);

        int orderId = insertOrder(sales.get(0).getOrderType(), conn);

        for (ProductSale sale : sales) {
            int productId = sale.getProductId();
            int quantityToProcess = sale.getEffectiveQuantity();  // Using the effective quantity

            insertOrderItem(orderId, productId, quantityToProcess, conn);
            updateProductQuantity(productId, quantityToProcess, conn);
        }

        conn.commit();

    } catch (SQLException ex) {
        ex.printStackTrace();
        message = "Error processing sale.";
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
                message += " Error rolling back transaction.";
            }
        }
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    return message;
}




// Chart 2: Most Sold Products
public List<Map<String, Object>> getMostSoldProducts() throws SQLException {
    List<Map<String, Object>> result = new ArrayList<>();
    String sql = "SELECT products.name, SUM(order_items.quantity) AS total_quantity_sold " +
                 "FROM order_items " +
                 "INNER JOIN products ON order_items.product_id = products.id " +
                 "GROUP BY products.name " +
                 "ORDER BY total_quantity_sold DESC";
    
                 try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
    
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
    
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>(columnCount);
                    for (int i = 1; i <= columnCount; ++i) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    result.add(row);
                }
            }
    
            return result;
        }

// Chart 3: Weekly Sales
public List<Map<String, Object>> getWeeklySales() throws SQLException {
    List<Map<String, Object>> result = new ArrayList<>();
    String sql = "SELECT WEEKDAY(orders.order_date) as weekday, SUM(order_items.quantity) AS total_quantity_sold " +
                 "FROM order_items " +
                 "JOIN orders ON order_items.order_id = orders.order_id " +
                 "WHERE orders.order_date >= DATE(NOW()) - INTERVAL 7 DAY " +
                 "GROUP BY WEEKDAY(orders.order_date)";
   try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                result.add(row);
            }
        }

        return result;
    }

// Chart 4: Daily Revenue
public List<Map<String, Object>> getDailyRevenue() throws SQLException {
    List<Map<String, Object>> result = new ArrayList<>();
    String sql = "SELECT WEEKDAY(orders.order_date) as weekday, SUM(order_items.quantity * products.price) AS total_revenue " +
                 "FROM order_items " +
                 "JOIN products ON order_items.product_id = products.id " +
                 "JOIN orders ON order_items.order_id = orders.order_id " +
                 "WHERE orders.order_date >= DATE(NOW()) - INTERVAL 7 DAY " +
                 "GROUP BY WEEKDAY(orders.order_date)";
  try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                result.add(row);
            }
        }

        return result;
    }



    /* this is for  unread and read notification and order placement */



// Add this method in your ProductRepository
public void removeUnreadOrder(int orderId) throws SQLException {
    String sql = "DELETE FROM unreadorders WHERE order_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        stmt.executeUpdate();
    }
}


public int getUnreadOrdersCount() {
    try (Connection connection = this.getConnection();
         PreparedStatement stmt = connection.prepareStatement(
                 "SELECT COUNT(*) FROM unreadorders WHERE status = 'unread'"
         )) {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return 0;
}

  // Add this method in your ProductRepository
public void addUnreadOrder(int orderId) throws SQLException {
    String sql = "INSERT INTO unreadorders (order_id, status) VALUES (?, 'unread')";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        stmt.executeUpdate();
    }
}

/* get all orders */
public List<Order> getAllOrders() throws SQLException {
    List<Order> orders = new ArrayList<>();
    Map<Integer, Order> orderMap = new HashMap<>();

    String sql = "SELECT o.order_id, o.order_date, o.order_time, o.order_type, c.customer_id, c.name as customer_name, c.phone_number, c.order_method, " +
    "sa.shipping_id, sa.delivery_address, sa.city, sa.state, sa.zip_code, " +
    "p.name as product_name, oi.quantity, uo.status " +
    "FROM orders o " +
    "LEFT JOIN customers c ON o.customer_id = c.customer_id " + // Changed to LEFT JOIN
    "LEFT JOIN shippingaddresses sa ON o.shipping_id = sa.shipping_id " +
    "JOIN order_items oi ON o.order_id = oi.order_id " +
    "JOIN products p ON oi.product_id = p.id " +
    "LEFT JOIN unreadorders uo ON o.order_id = uo.order_id";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int orderId = rs.getInt("order_id");
            Order order = orderMap.get(orderId);
    
            if (order == null) {
                order = new Order();
                order.setId(orderId);
    
                String orderType = rs.getString("order_type");
                Integer customerId = (Integer) rs.getObject("customer_id");
                Integer shippingId = (Integer) rs.getObject("shipping_id");
    
                // Updated logic to handle NULL values
                if (customerId != null && shippingId != null) {
                    order.setType("Online Sale");
                } else if (orderType != null && !orderType.isEmpty()) {
                    order.setType(orderType);
                } else if (customerId == null && shippingId == null && orderType == null) {
                    order.setType("Walk-In Sale");
                } else {
                    order.setType("Unknown");
                }
                Timestamp timestamp = rs.getTimestamp("order_date");
                order.setCombinedDatetime(timestamp.toString());
                order.setDate(rs.getDate("order_date"));
                order.setTime(rs.getTime("order_time"));
                //order.setType(rs.getString("order_type"));
                order.setStatus(rs.getString("status"));
                order.setProducts(new ArrayList<>());
                
                Customer customer = new Customer();
                customer.setId(rs.getInt("customer_id"));
                customer.setName(rs.getString("customer_name"));
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setOrderMethod(rs.getString("order_method"));
                order.setCustomer(customer);
                
                ShippingAddress shippingAddress = new ShippingAddress();
                shippingAddress.setId(rs.getInt("shipping_id"));
                shippingAddress.setDeliveryAddress(rs.getString("delivery_address"));
                shippingAddress.setCity(rs.getString("city"));
                shippingAddress.setState(rs.getString("state"));
                shippingAddress.setZipCode(rs.getString("zip_code"));
                order.setShippingAddress(shippingAddress);
                
                orderMap.put(orderId, order);
                orders.add(order);
            }
            
            Product product = new Product();
            product.setName(rs.getString("product_name"));
            product.setQuantity(rs.getInt("quantity"));
            order.getProducts().add(product);
        }
    }
    return orders;
}




public List<Integer> getUnreadOrderIds() throws SQLException {
    List<Integer> ids = new ArrayList<>();
    String sql = "SELECT order_id FROM unreadorders WHERE status = 'unread'";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ids.add(rs.getInt("order_id"));
        }
    }

    return ids;
}


public Order getOrderById(int orderId) throws SQLException {
    Order order = null;

    String sql = "SELECT o.order_id, " +
                 "CONCAT(o.order_date, 'T', o.order_time, 'Z') as combined_datetime, " + 
                 "COALESCE(c.order_method, 'Walk-In') as order_type, " + // Use 'Walk-In' if no order_method
                 "c.name as customer_name, c.phone_number as customer_phone, " +
                 "sa.delivery_address, sa.city, sa.state, sa.zip_code, " +
                 "p.name as product_name, p.price as product_price, oi.quantity, uo.status, os.status as order_status " +
                 "FROM orders o " +
                 "LEFT JOIN customers c ON o.customer_id = c.customer_id " + // LEFT JOIN to include walk-ins
                 "LEFT JOIN shippingaddresses sa ON o.shipping_id = sa.shipping_id " +
                 "JOIN order_items oi ON o.order_id = oi.order_id " +
                 "JOIN products p ON oi.product_id = p.id " +
                 "LEFT JOIN unreadorders uo ON o.order_id = uo.order_id " +
                 "LEFT JOIN order_status os ON o.order_id = os.order_id " +
                 "WHERE o.order_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            if (order == null) {
                order = new Order();
                order.setId(rs.getInt("order_id"));
                order.setCombinedDatetime(rs.getString("combined_datetime"));
                order.setType(rs.getString("order_type"));

                order.setStatus(rs.getString("status")); // unreadorders status
                order.setOrderStatus(rs.getString("order_status")); // new order_status status
                order.setProducts(new ArrayList<>());

                // Check for customer information. If available, then it's an online order. Else, it's walk-in.
                String customerName = rs.getString("customer_name");
                if (customerName != null) {  // Online order
                    Customer customer = new Customer();
                    customer.setName(customerName);
                    customer.setPhoneNumber(rs.getString("customer_phone"));
                    order.setCustomer(customer);

                    ShippingAddress shippingAddress = new ShippingAddress();
                    shippingAddress.setDeliveryAddress(rs.getString("delivery_address"));
                    shippingAddress.setCity(rs.getString("city"));
                    shippingAddress.setState(rs.getString("state"));
                    shippingAddress.setZipCode(rs.getString("zip_code"));
                    order.setShippingAddress(shippingAddress);
                }
            }
            
            Product product = new Product();
            product.setName(rs.getString("product_name"));
            product.setPrice(rs.getFloat("product_price"));
            product.setQuantity(rs.getInt("quantity"));
            order.getProducts().add(product);
        }
    }

    if (order == null) {
        throw new SQLException("Order with id " + orderId + " not found.");
    }

    return order;
}




public void incrementProductView(int productId) throws SQLException {
    try(Connection conn = getConnection()) {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO product_views(product_id, views) VALUES (?, 1) ON DUPLICATE KEY UPDATE views = views + 1");
        ps.setInt(1, productId);
        ps.executeUpdate();
    }
}

public List<ProductView> getAllProductViews() throws SQLException {
    List<ProductView> productViews = new ArrayList<>();
    try(Connection conn = getConnection()) {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT products.*, product_views.views " +
            "FROM products LEFT JOIN product_views " +
            "ON products.id = product_views.product_id " +
            "ORDER BY product_views.views DESC"
        );
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ProductView productView = new ProductView(); // Create a new object to store product and view information
            productView.setId(rs.getInt("id"));
            productView.setName(rs.getString("name"));
            productView.setViews(rs.getInt("views"));
            productViews.add(productView);
        }
    }
    return productViews;
}

}

