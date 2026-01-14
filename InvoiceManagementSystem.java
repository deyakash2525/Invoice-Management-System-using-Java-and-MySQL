import java.sql.*;
import java.util.Scanner;
public class InvoiceManagementSystem {

    static final String DB_URL = "jdbc:mysql://localhost:3306/invoice"; 
    static final String USER = "root";
    static final String PASS = "";
    

    static Connection conn = null;
    static Scanner scanner = new Scanner(System.in); 
    public static void create_customers(String NAME, String ADDRESS, String PHONE_NO) throws SQLException {
        String query = "INSERT INTO CUSTOMERS (NAME, ADDRESS, PHONE_NO) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, NAME);
        pstmt.setString(2, ADDRESS);
        pstmt.setString(3, PHONE_NO);
        pstmt.executeUpdate();
        System.out.println("✅ Customer created successfully.");
    }
    public static void read_customers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMERS");
        boolean found = false;
        System.out.println("\n--- LIST OF CUSTOMERS ---");
        System.out.printf("%-5s | %-20s | %-20s | %-15s%n", "ID", "NAME", "ADDRESS", "PHONE");
        System.out.println("-------------------------------------------------------------------");
        while (rs.next()) {
            found = true;
            System.out.printf("%-5d | %-20s | %-20s | %-15s%n",
                    rs.getInt("ID"), rs.getString("NAME"), rs.getString("ADDRESS"), rs.getString("PHONE_NO"));
        }
        if (!found) System.out.println("No customers found.");
    }

    public static void update_customers(int ID) {
        try {
            System.out.println("1. UPDATE NAME");
            System.out.println("2. UPDATE ADDRESS");
            System.out.println("3. UPDATE PHONE NUMBER");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine(); 
            String query = "";
            String inputVal = "";            
            if (choice.equals("1")) {
                System.out.print("Enter New Name: ");
                inputVal = scanner.nextLine();
                query = "UPDATE CUSTOMERS SET NAME=? WHERE ID=?";
            } else if (choice.equals("2")) {
                System.out.print("Enter New Address: ");
                inputVal = scanner.nextLine();
                query = "UPDATE CUSTOMERS SET ADDRESS=? WHERE ID=?";
            } else if (choice.equals("3")) {
                System.out.print("Enter New Phone: ");
                inputVal = scanner.nextLine();
                query = "UPDATE CUSTOMERS SET PHONE_NO=? WHERE ID=?";
            } else {
                System.out.println("❌ Invalid Choice");
                return;
            }
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, inputVal);
            pstmt.setInt(2, ID);            
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("✅ Customer updated successfully.");
            else System.out.println("⚠️ No customer found with ID: " + ID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete_customers(int ID) {
        try {
            String query = "DELETE FROM CUSTOMERS WHERE ID=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, ID);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Customer deleted successfully.");
            } else {
                System.out.println("⚠️ No customer found with ID: " + ID);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ ERROR: Cannot delete this Customer.");
            System.out.println("   Reason: This customer has existing Invoices.");
            System.out.println("   Solution: Delete their invoices first.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void create_products(String NAME, double PRICE, int STOCK) throws SQLException {
        String query = "INSERT INTO PRODUCTS (NAME, PRICE, STOCK) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, NAME);
        pstmt.setDouble(2, PRICE);
        pstmt.setInt(3, STOCK);
        pstmt.executeUpdate();
        System.out.println("✅ Product created successfully.");
    }

    public static void read_products() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCTS");
        System.out.println("\n--- LIST OF PRODUCTS ---");
        System.out.printf("%-5s | %-20s | %-10s | %-5s%n", "ID", "NAME", "PRICE", "STOCK");
        System.out.println("----------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-5d | %-20s | %-10.2f | %-5d%n",
                    rs.getInt("ID"), rs.getString("NAME"), rs.getDouble("PRICE"), rs.getInt("STOCK"));
        }
    }

    public static void update_products(int ID) {
        try {
            System.out.println("1. UPDATE NAME");
            System.out.println("2. UPDATE PRICE");
            System.out.println("3. UPDATE STOCK QUANTITY");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine(); 
            String query = "";
            if (choice.equals("1")) {
                System.out.print("Enter New Name: ");
                String name = scanner.nextLine();
                query = "UPDATE PRODUCTS SET NAME=? WHERE ID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setInt(2, ID);
                if(pstmt.executeUpdate() > 0) System.out.println("✅ Product Name updated.");
                else System.out.println("⚠️ Product ID not found.");

            } else if (choice.equals("2")) {
                System.out.print("Enter New Price: ");
                double price = Double.parseDouble(scanner.nextLine());
                query = "UPDATE PRODUCTS SET PRICE=? WHERE ID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setDouble(1, price);
                pstmt.setInt(2, ID);
                if(pstmt.executeUpdate() > 0) System.out.println("✅ Product Price updated.");
                else System.out.println("⚠️ Product ID not found.");

            } else if (choice.equals("3")) {
                System.out.print("Enter New Stock Quantity: ");
                int stock = Integer.parseInt(scanner.nextLine());
                query = "UPDATE PRODUCTS SET STOCK=? WHERE ID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, stock);
                pstmt.setInt(2, ID);
                if(pstmt.executeUpdate() > 0) System.out.println("✅ Product Stock updated.");
                else System.out.println("⚠️ Product ID not found.");

            } else {
                System.out.println("❌ Invalid Choice");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Invalid number format entered.");
        }
    }
    
    public static void create_invoice_header(int CUSTOMER_ID) {
        try {
            String query = "INSERT INTO INVOICES (CUSTOMER_ID, TOTAL_AMOUNT) VALUES (?, 0.00)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, CUSTOMER_ID);
            pstmt.executeUpdate();
            System.out.println("✅ New Invoice created.");
        } catch (SQLException e) {
            System.out.println("❌ Error: Customer ID not found or Database error.");
        }
    }

    public static void read_invoices() {
        try {
            String query = "SELECT I.ID AS inv_id, C.NAME AS cust_name, I.INVOICE_DATE, I.TOTAL_AMOUNT " +
                           "FROM INVOICES I " +
                           "JOIN CUSTOMERS C ON I.CUSTOMER_ID = C.ID";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            System.out.println("\n--- LIST OF INVOICES ---");
            System.out.printf("%-5s | %-20s | %-25s | %-10s%n", "ID", "CUSTOMER", "DATE", "TOTAL");
            System.out.println("---------------------------------------------------------------------");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("%-5d | %-20s | %-25s | %-10.2f%n",
                        rs.getInt("inv_id"), 
                        rs.getString("cust_name"), 
                        rs.getString("INVOICE_DATE"), 
                        rs.getDouble("TOTAL_AMOUNT"));
            }
            if (!hasData) System.out.println("⚠️ No invoices found.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void add_invoice_item(int INVOICE_ID, int PRODUCT_ID, int QUANTITY) throws SQLException {
        String priceQuery = "SELECT PRICE FROM PRODUCTS WHERE ID = ?";
        PreparedStatement pStmt = conn.prepareStatement(priceQuery);
        pStmt.setInt(1, PRODUCT_ID);
        ResultSet rs = pStmt.executeQuery();
        
        if(rs.next()) {
            double price = rs.getDouble("PRICE");
            double subtotal = price * QUANTITY;
            String insertQuery = "INSERT INTO INVOICE_ITEMS (INVOICE_ID, PRODUCT_ID, QUANTITY, SUBTOTAL) VALUES (?, ?, ?, ?)";
            PreparedStatement iStmt = conn.prepareStatement(insertQuery);
            iStmt.setInt(1, INVOICE_ID);
            iStmt.setInt(2, PRODUCT_ID);
            iStmt.setInt(3, QUANTITY);
            iStmt.setDouble(4, subtotal);
            iStmt.executeUpdate();
            String updateInv = "UPDATE INVOICES SET TOTAL_AMOUNT = TOTAL_AMOUNT + ? WHERE ID = ?";
            PreparedStatement uStmt = conn.prepareStatement(updateInv);
            uStmt.setDouble(1, subtotal);
            uStmt.setInt(2, INVOICE_ID);
            uStmt.executeUpdate();

            System.out.println("✅ Item added. Invoice Total updated.");
        } else {
            System.out.println("⚠️ Product not found.");
        }
    }

    public static void read_invoice_items(int INVOICE_ID) throws SQLException {
        String query = "SELECT P.NAME, II.QUANTITY, II.SUBTOTAL FROM INVOICE_ITEMS II JOIN PRODUCTS P ON II.PRODUCT_ID = P.ID WHERE II.INVOICE_ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, INVOICE_ID);
        ResultSet rs = pstmt.executeQuery();
        System.out.println("--- Items for Invoice " + INVOICE_ID + " ---");
        boolean hasItems = false;
        while(rs.next()) {
            hasItems = true;
            System.out.printf("ITEM: %s, QTY: %d, SUBTOTAL: %.2f%n", rs.getString("NAME"), rs.getInt("QUANTITY"), rs.getDouble("SUBTOTAL"));
        }
        if(!hasItems) System.out.println("No items found in this invoice.");
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("✅ Connected to database successfully.");

            while (true) {
                System.out.println("\n--- INVOICE MANAGEMENT SYSTEM ---");
                System.out.println("1. CUSTOMER RELATED DATA");
                System.out.println("2. PRODUCT RELATED DATA");
                System.out.println("3. INVOICE GENERATION RELATED DATA");
                System.out.println("4. INVOICE ITEMS (ADD PRODUCTS) RELATED DATA");
                System.out.println("5. EXIT");

                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    while (true) {
                        System.out.println("\n--- CUSTOMER MENU ---");
                        System.out.println("1. ADD NEW CUSTOMER");
                        System.out.println("2. DISPLAY CUSTOMERS");
                        System.out.println("3. UPDATE CUSTOMER");
                        System.out.println("4. DELETE CUSTOMER");
                        System.out.println("5. EXIT FROM CUSTOMER RELATED DATA");

                        System.out.print("Enter Choice: ");
                        String press = scanner.nextLine();

                        if (press.equals("1")) {
                            System.out.print("Enter Name: "); String name = scanner.nextLine();
                            System.out.print("Enter Address: "); String addr = scanner.nextLine();
                            System.out.print("Enter Phone: "); String phone = scanner.nextLine();
                            create_customers(name, addr, phone);

                        } else if (press.equals("2")) {
                            read_customers();

                        } else if (press.equals("3")) {
                            System.out.print("Enter Customer ID to update: ");
                            int id = Integer.parseInt(scanner.nextLine());
                            update_customers(id);

                        } else if (press.equals("4")) {
                            System.out.print("Enter Customer ID to delete: ");
                            int id = Integer.parseInt(scanner.nextLine());
                            delete_customers(id);

                        } else if (press.equals("5")) {
                            break;
                        } else {
                            System.out.println("Invalid Choice.");
                        }
                    }

                } else if (choice.equals("2")) {
                    while (true) {
                        System.out.println("\n--- PRODUCT MENU ---");
                        System.out.println("1. ADD NEW PRODUCT");
                        System.out.println("2. DISPLAY PRODUCTS");
                        System.out.println("3. UPDATE PRODUCT"); // New Menu Option
                        System.out.println("4. EXIT FROM PRODUCT RELATED DATA");

                        System.out.print("Enter Choice: ");
                        String press = scanner.nextLine();

                        if (press.equals("1")) {
                            System.out.print("Enter Name: "); String name = scanner.nextLine();
                            System.out.print("Enter Price: "); double price = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter Stock: "); int stock = Integer.parseInt(scanner.nextLine());
                            create_products(name, price, stock);

                        } else if (press.equals("2")) {
                            read_products();

                        } else if (press.equals("3")) {
                            System.out.print("Enter Product ID to update: ");
                            int id = Integer.parseInt(scanner.nextLine());
                            update_products(id);

                        } else if (press.equals("4")) {
                            break;
                        } else {
                            System.out.println("Invalid Choice.");
                        }
                    }

                } else if (choice.equals("3")) {
                    while (true) {
                        System.out.println("\n--- INVOICE GENERATION MENU ---");
                        System.out.println("1. CREATE NEW INVOICE");
                        System.out.println("2. DISPLAY ALL INVOICES");
                        System.out.println("3. EXIT FROM INVOICE RELATED DATA");

                        System.out.print("Enter Choice: ");
                        String press = scanner.nextLine();

                        if (press.equals("1")) {
                            System.out.print("Enter Customer ID: "); 
                            int cid = Integer.parseInt(scanner.nextLine());
                            create_invoice_header(cid);

                        } else if (press.equals("2")) {
                            read_invoices();

                        } else if (press.equals("3")) {
                            break;
                        } else {
                            System.out.println("Invalid Choice.");
                        }
                    }

                } else if (choice.equals("4")) {
                    while (true) {
                        System.out.println("\n--- INVOICE ITEMS MENU ---");
                        System.out.println("1. ADD ITEM TO INVOICE");
                        System.out.println("2. DISPLAY ITEMS IN INVOICE");
                        System.out.println("3. EXIT FROM INVOICE ITEMS DATA");

                        System.out.print("Enter Choice: ");
                        String press = scanner.nextLine();

                        if (press.equals("1")) {
                            System.out.print("Enter Invoice ID: "); int invId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter Product ID: "); int prodId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter Quantity: "); int qty = Integer.parseInt(scanner.nextLine());
                            add_invoice_item(invId, prodId, qty);

                        } else if (press.equals("2")) {
                            System.out.print("Enter Invoice ID to View: "); 
                            int invId = Integer.parseInt(scanner.nextLine());
                            read_invoice_items(invId);

                        } else if (press.equals("3")) {
                            break;
                        } else {
                            System.out.println("Invalid Choice.");
                        }
                    }

                } else if (choice.equals("5")) {
                    System.out.println("EXITING FROM INVOICE MANAGEMENT SYSTEM....");
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }

            conn.close();
            scanner.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Invalid input! Please enter a number.");
        }
    }
}