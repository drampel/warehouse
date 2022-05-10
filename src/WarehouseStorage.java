import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class WarehouseStorage {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/warehouse";
    private static final String USER = "postgres";
    private static final String PWD = "user";

    public static void warehouseDatabaseConnection() {
        String createType = "DO $$ BEGIN " +
                "CREATE TYPE product_status AS ENUM('ACTIVE','DELETED'); " +
                "EXCEPTION " +
                "WHEN duplicate_object THEN null;" +
                "END $$;";
        String createTable = "CREATE TABLE IF NOT EXISTS warehouse_storage" +
                "(id BIGINT GENERATED ALWAYS AS IDENTITY, " +
                "product_name VARCHAR(32) NOT NULL, " +
                "buy_price DECIMAL DEFAULT NULL, " +
                "sale_price DECIMAL DEFAULT NULL, " +
                "count BIGINT DEFAULT NULL," +
                "status product_status NOT NULL DEFAULT 'ACTIVE')";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);
             Statement stmt = connection.createStatement()) {
            stmt.execute(createType);
            stmt.execute(createTable);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}