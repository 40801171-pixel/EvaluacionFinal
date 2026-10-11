package main;
import modules.Inventory;
import modules.Sales;
import modules.Reports;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Sales sales = new Sales(inventory);
        Reports reports = new Reports(inventory, sales);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Sistema de Gestión Simplificada de MYPEs ===");
            System.out.println("1. Gestionar Inventario");
            System.out.println("2. Registrar Venta");
            System.out.println("3. Generar Reporte");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    inventory.manageInventory();
                    break;
                case "2":
                    sales.registerSale();
                    break;
                case "3":
                    reports.generateReport();
                    break;
                case "4":
                    System.out.println("Saliendo del sistema...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
}
