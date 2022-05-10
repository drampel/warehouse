import java.util.Scanner;

public class WarehouseMenu {
    public static void warehouseMenu() {
        boolean isExit = false;
        while (!isExit) {
            System.out.print("""
                                        
                    Warehouse Menu: 1 - addProduct
                                    2 - updateProduct
                                    3 - deleteProduct
                                    4 - printProfit
                                    5 - exit
                    Choose an action:\040""");
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine().strip();
            switch (action) {
                case "1" -> WorkWithWarehouseStorage.addProduct();
                case "2" -> WorkWithWarehouseStorage.updateProduct();
                case "3" -> WorkWithWarehouseStorage.deleteProduct();
                case "4" -> WorkWithWarehouseStorage.printProfit();
                case "5" -> isExit = true;
                default -> System.out.println("Choose the right action!\n");
            }
        }
    }
}