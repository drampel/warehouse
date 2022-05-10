import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class WorkWithWarehouseStorage {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/warehouse";
    private static final String USER = "postgres";
    private static final String PWD = "user";

    public static void addProduct() {
        boolean isExistProductName = true;
        ResultSet rs = null;
        String insert = "INSERT INTO warehouse_storage (product_name, buy_price, sale_price, count)" +
                " VALUES (?,?,?,?)";
        String select = "SELECT * FROM warehouse_storage WHERE status = 'ACTIVE' ORDER BY id";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
             PreparedStatement pstmt = connection.prepareStatement(insert);
             PreparedStatement pstmtSelect = connection.prepareStatement(select)) {
            System.out.print("\nEnter value 'product_name': ");
            Scanner scanner = new Scanner(System.in);
            while (isExistProductName) {
                String addedProductName = scanner.nextLine().strip();
                String existingProductName = null;
                rs = pstmtSelect.executeQuery();
                if (!addedProductName.equals("")) {
                    while (rs.next()) {
                        String productName = rs.getString(2);
                        if (addedProductName.equals(productName)) {
                            existingProductName = addedProductName;
                            System.out.print("'product_name' already exists with the same name. Try again: ");
                        }
                    }
                    if (existingProductName == null) {
                        pstmt.setString(1, addedProductName);
                        isExistProductName = false;
                    }
                } else System.out.print("'product_name' value cannot be empty. Try again: ");
            }
            WorkWithWarehouseStorage.checkInputPriceAndCount(pstmt, 2, "buy_price");
            WorkWithWarehouseStorage.checkInputPriceAndCount(pstmt, 3, "sale_price");
            WorkWithWarehouseStorage.checkInputPriceAndCount(pstmt, 4, "count");
            pstmt.executeUpdate();
            System.out.println("Product added!\n");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateProduct() {
        boolean isExit = false;
        while (!isExit) {
            System.out.print("""
                                        
                    Product update by criterion:
                    1 - id
                    2 - product_name
                    or
                    3 - exit
                    Choose an action:\040""");
            Scanner scanner = new Scanner(System.in);
            String criterionNumber = scanner.nextLine().strip();
            switch (criterionNumber) {
                case "1", "2" -> {
                    String productCriterion = switch (criterionNumber) {
                        case "1" -> "id";
                        case "2" -> "product_name";
                        default -> null;
                    };
                    boolean isExistProduct = false;
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    String updateByProductName = null;
                    int updateById = 0;
                    String select = "SELECT * FROM warehouse_storage WHERE status = 'ACTIVE' ORDER BY id";
                    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
                         PreparedStatement pstmtSelect = connection.prepareStatement(select)) {
                        System.out.printf("Enter the '%s' value according to which the update will be performed: ",
                                productCriterion);
                        while (!isExistProduct) {
                            scanner = new Scanner(System.in);
                            rs = pstmtSelect.executeQuery();
                            try {
                                switch (criterionNumber) {
                                    case "1" -> {
                                        updateById = scanner.nextInt();
                                        if (updateById > 0) {
                                            int existingProductId = 0;
                                            while (rs.next()) {
                                                int id = rs.getInt(1);
                                                if (updateById == id) existingProductId = id;
                                            }
                                            if (existingProductId != 0) isExistProduct = true;
                                            else System.out.printf("Product by criterion '%s' with '%s' value does " +
                                                    "not exist. Try again: ", productCriterion, updateById);
                                        } else System.out.printf("'%s' value cannot be null or negative. " +
                                                "Try again: ", productCriterion);
                                    }
                                    case "2" -> {
                                        updateByProductName = scanner.nextLine().strip();
                                        if (!updateByProductName.equals("")) {
                                            String existingProductName = null;
                                            while (rs.next()) {
                                                String productName = rs.getString(2);
                                                if (updateByProductName.equals(productName)) existingProductName = productName;
                                            }
                                            if (existingProductName != null) isExistProduct = true;
                                            else System.out.printf("Product by criterion '%s' with '%s' value does " +
                                                    "not exist. Try again: ", productCriterion, updateByProductName);
                                        } else System.out.printf("'%s' value cannot be empty. Try again: ", productCriterion);
                                    }
                                }
                            } catch (InputMismatchException ex) {
                                System.out.print("Incorrect data entry. Try again: ");
                            }
                        }
                        while (!isExit) {
                            System.out.print("""
                                                                        
                                    Updating a product column value:
                                    1 - product_name
                                    2 - buy_price
                                    3 - sale_price
                                    4 - count
                                    or
                                    5 - exit
                                    Choose an action:\040""");
                            scanner = new Scanner(System.in);
                            String columnNumber = scanner.nextLine().strip();
                            switch (columnNumber) {
                                case "1", "2", "3", "4" -> {
                                    isExit = true;
                                    String productColumn = switch (columnNumber) {
                                        case "1" -> "product_name";
                                        case "2" -> "buy_price";
                                        case "3" -> "sale_price";
                                        case "4" -> "count";
                                        default -> null;
                                    };
                                    String update = "UPDATE warehouse_storage SET " + productColumn + " = ? " +
                                            "WHERE status = 'ACTIVE' AND " + productCriterion + " = ?";
                                    pstmt = connection.prepareStatement(update);
                                    switch (criterionNumber) {
                                        case "1" -> pstmt.setInt(2, updateById);
                                        case "2" -> pstmt.setString(2, updateByProductName);
                                    }
                                    switch (columnNumber) {
                                        case "1" -> {
                                            boolean isExistProductName = true;
                                            String productName;
                                            System.out.print("Enter value 'product_name': ");
                                            while (isExistProductName) {
                                                String addedProductName = scanner.nextLine().strip();
                                                String existingProductName = null;
                                                rs = pstmtSelect.executeQuery();
                                                if (!addedProductName.equals("")) {
                                                    switch (criterionNumber) {
                                                        case "1" -> {
                                                            while (rs.next()) {
                                                                int id = rs.getInt(1);
                                                                productName = rs.getString(2);
                                                                if (addedProductName.equals(productName) && updateById != id) {
                                                                    existingProductName = addedProductName;
                                                                    System.out.print("'product_name' already exists " +
                                                                            "with the same name. Try again: ");
                                                                } else if (addedProductName.equals(productName))
                                                                    System.out.print("'product_name' remains the same. ");
                                                            }
                                                        }
                                                        case "2" -> {
                                                            while (rs.next()) {
                                                                productName = rs.getString(2);
                                                                if (addedProductName.equals(productName) &&
                                                                        !updateByProductName.equals(productName)) {
                                                                    existingProductName = addedProductName;
                                                                    System.out.print("'product_name' already exists" +
                                                                            " with the same name. Try again: ");
                                                                } else if (addedProductName.equals(productName))
                                                                    System.out.print("'product_name' remains the same. ");
                                                            }
                                                        }
                                                    }
                                                    if (existingProductName == null) {
                                                        pstmt.setString(1, addedProductName);
                                                        isExistProductName = false;
                                                    }
                                                } else System.out.printf("'%s' value cannot be empty. Try again: ", productColumn);
                                            }
                                        }
                                        case "2", "3", "4" -> WorkWithWarehouseStorage.checkInputPriceAndCount(pstmt,
                                                1, productColumn);
                                    }
                                    pstmt.executeUpdate();
                                    System.out.println("Operation completed!\n");
                                }
                                case "5" -> isExit = true;
                                default -> System.out.println("Enter the product column name correctly!");
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (pstmt != null) rs.close();
                            if (rs != null) rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                case "3" -> isExit = true;
                default -> System.out.println("Enter the product column name correctly!");
            }
        }
    }

    public static void deleteProduct() {
        boolean isExit = false;
        while (!isExit) {
            System.out.print("""
                                                
                    Deleting a product by a criterion:
                    1 - id
                    2 - product_name
                    or
                    3 - exit
                    Choose an action:\040""");
            Scanner scanner = new Scanner(System.in);
            String criterionNumber = scanner.nextLine().strip();
            switch (criterionNumber) {
                case "1", "2" -> {
                    isExit = true;
                    String productCriterion = switch (criterionNumber) {
                        case "1" -> "id";
                        case "2" -> "product_name";
                        default -> null;
                    };
                    boolean isExistProduct = false;
                    ResultSet rs = null;
                    String delete = "UPDATE warehouse_storage SET status = 'DELETED' WHERE " + productCriterion + " = ?";
                    String select = "SELECT * FROM warehouse_storage WHERE status = 'ACTIVE' ORDER BY id";
                    try (Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
                         PreparedStatement pstmt = connection.prepareStatement(delete);
                         PreparedStatement pstmtSelect = connection.prepareStatement(select)) {
                        System.out.printf("Enter the '%s' under which the deletion will be performed: ", productCriterion);
                        while (!isExistProduct) {
                            scanner = new Scanner(System.in);
                            rs = pstmtSelect.executeQuery();
                            try {
                                switch (criterionNumber) {
                                    case "1" -> {
                                        int deleteById = scanner.nextInt();
                                        if (deleteById > 0) {
                                            int existingProductId = 0;
                                            while (rs.next()) {
                                                int id = rs.getInt(1);
                                                if (deleteById == id) existingProductId = id;
                                            }
                                            if (existingProductId != 0) {
                                                pstmt.setInt(1, deleteById);
                                                isExistProduct = true;
                                            } else System.out.printf("Product by criterion '%s' with '%s' value does " +
                                                    "not exist. Try again: ", productCriterion, deleteById);
                                        } else System.out.printf("'%s' value cannot be null or negative. Try again: ", productCriterion);
                                    }
                                    case "2" -> {
                                        String deleteByProductName = scanner.nextLine().strip();
                                        if (!deleteByProductName.equals("")) {
                                            String existingProductName = null;
                                            while (rs.next()) {
                                                String productName = rs.getString(2);
                                                if (deleteByProductName.equals(productName))
                                                    existingProductName = productName;
                                            }
                                            if (existingProductName != null) {
                                                pstmt.setString(1, deleteByProductName);
                                                isExistProduct = true;
                                            } else
                                                System.out.printf("Product by criterion '%s' with '%s' value does " +
                                                        "not exist. Try again: ", productCriterion, deleteByProductName);
                                        } else System.out.printf("'%s' value cannot be empty. Try again: ", productCriterion);
                                    }
                                }
                            } catch (InputMismatchException ex) {
                                System.out.print("Incorrect data entry. Try again: ");
                            }
                        }
                        pstmt.executeUpdate();
                        System.out.println("Product deleted!\n");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (rs != null) rs.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                case "3" -> isExit = true;
                default -> System.out.println("Enter the product column name correctly!");
            }
        }
    }

    public static void printProfit() {
        ResultSet rs = null;
        String select = "SELECT * FROM warehouse_storage WHERE status = 'ACTIVE' ORDER BY id";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
             PreparedStatement pstmtSelect = connection.prepareStatement(select)) {
            rs = pstmtSelect.executeQuery();
            BigDecimal totalProfit = BigDecimal.ZERO;
            while (rs.next()) {
                int id = rs.getInt("id");
                String productName = rs.getString("product_name");
                BigDecimal buyPrice = rs.getBigDecimal("buy_price");
                BigDecimal salePrice = rs.getBigDecimal("sale_price");
                int count = rs.getInt("count");
                BigDecimal productProfit = (salePrice.subtract(buyPrice)).multiply(BigDecimal.valueOf(count));
                totalProfit = totalProfit.add(productProfit);
                System.out.printf("{id = %d, product_name = %s, buy_price = %,.2f, sale_price = %,.2f, count = %d, " +
                        "income = %,.2f}%n", id, productName, buyPrice, salePrice, count, productProfit);
            }
            System.out.printf("Total income: %,.2f%n", totalProfit);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void checkInputPriceAndCount(PreparedStatement pstmt,
                                                int parameterIndex,
                                                String productColumn) throws SQLException {
        boolean isAboveZero = false;
        System.out.printf("Enter value '%s': ", productColumn);
        while (!isAboveZero) {
            Scanner scanner = new Scanner(System.in);
            try {
                switch (productColumn) {
                    case "buy_price", "sale_price" -> {
                        BigDecimal price = scanner.nextBigDecimal();
                        if (price.compareTo(BigDecimal.ZERO) >= 0) {
                            pstmt.setBigDecimal(parameterIndex, price);
                            isAboveZero = true;
                        } else System.out.printf("'%s' value cannot be negative. Try again: ", productColumn);
                    }
                    case "count" -> {
                        int count = scanner.nextInt();
                        if (count >= 0) {
                            pstmt.setInt(parameterIndex, count);
                            isAboveZero = true;
                        } else System.out.printf("'%s' value cannot be negative. Try again: ", productColumn);
                    }
                }
            } catch (InputMismatchException ex) {
                System.out.print("Incorrect data entry. Try again: ");
            }
        }
    }
}