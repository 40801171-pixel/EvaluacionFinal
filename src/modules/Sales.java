package modules;

import java.util.Scanner;

public class Sales {
    private Inventory inventory;

    public Sales(Inventory inventory) {
        this.inventory = inventory;
    }

    public void registerSale() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Registro de Venta ---");
        System.out.print("Ingrese el nombre del producto: ");
        String productName = scanner.nextLine();
        System.out.print("Ingrese la cantidad vendida: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        // Error: No se valida si el producto existe en el inventario.
        int currentStock = inventory.getStock(productName);
        if (currentStock < quantity) {
            System.out.println("Error: Stock insuficiente.");
            return;
        }
        inventory.updateStock(productName, currentStock - quantity);
        System.out.println("Venta registrada correctamente.");

    }



}
