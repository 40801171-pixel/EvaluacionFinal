package modules;

public class Reports {
    private Inventory inventory;
    private Sales sales;

    public Reports(Inventory inventory, Sales sales) {
        this.inventory = inventory;
        this.sales = sales;
    }

    public void generateReport() {
        System.out.println("\n--- Generación de Reportes ---");
        // Error: Reporte incompleto, solo muestra los productos.
        System.out.println("Inventario Actual:");
        inventory.printInventory();
    }
}
