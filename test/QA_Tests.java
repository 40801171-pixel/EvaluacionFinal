import modules.Inventory;

import modules.Reports;
import modules.Sales;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;

public class QA_Tests {

    // Variables para simular la consola (Teclado y Pantalla)
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    // Método auxiliar que escribe automáticamente por el usuario
    private void simulateInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    // --- CASOS FUNCIONALES: MÓDULO DE INVENTARIO ---

    @Test
    public void CF01_RegistroNuevo() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        assertEquals(10, inv.getStock("Teclado"), "Fallo CF-01: El producto no se registró correctamente.");
    }

    @Test
    public void CF02_IngresoExistente() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        inv.updateStock("Teclado", 5); // Simulamos otro ingreso
        // FALLO ESPERADO: El sistema sobrescribe (da 5) en lugar de sumar o validar (15 o 10).
        assertEquals(15, inv.getStock("Teclado"), "Fallo CF-02: El sistema sobrescribe en lugar de sumar.");
    }

    @Test
    public void CF03_InventarioVacio() {
        Inventory inv = new Inventory();
        inv.printInventory();
        assertTrue(outContent.toString().contains("vacío"), "Fallo CF-03: No muestra mensaje de inventario vacío.");
    }

    // --- CASOS FUNCIONALES: MÓDULO DE VENTAS ---

    @Test
    public void CF04_VentaSuficiente() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        Sales sales = new Sales(inv);
        simulateInput("Teclado\n2\n"); // Simula escribir "Teclado", Enter, "2", Enter
        sales.registerSale();
        assertEquals(8, inv.getStock("Teclado"), "Fallo CF-04: El stock no se redujo correctamente.");
    }

    @Test
    public void CF05_VentaInsuficiente() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        Sales sales = new Sales(inv);
        simulateInput("Teclado\n15\n");
        sales.registerSale();
        assertTrue(outContent.toString().contains("Stock insuficiente"), "Fallo CF-05: Permitió vender más del stock.");
    }

    @Test
    public void CF06_VentaNoRegistrado() {
        Inventory inv = new Inventory();
        Sales sales = new Sales(inv);
        simulateInput("Inexistente\n1\n");
        // FALLO ESPERADO (Crítico): Lanzará NullPointerException porque el docente no validó si existe.
        sales.registerSale();
    }

    @Test
    public void CF07_VentaNegativa() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        Sales sales = new Sales(inv);
        simulateInput("Teclado\n-5\n");
        sales.registerSale();
        // FALLO ESPERADO: No hay validación. 10 - (-5) = 15. Esperamos que bloquee y se quede en 10.
        assertEquals(10, inv.getStock("Teclado"), "Fallo CF-07: Aceptó cantidad negativa y aumentó el stock.");
    }

    @Test
    public void CF08_VentaCero() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        Sales sales = new Sales(inv);
        simulateInput("Teclado\n0\n");
        sales.registerSale();
        assertEquals(10, inv.getStock("Teclado"), "Fallo CF-08: El stock se alteró al vender 0.");
    }

    // --- CASOS FUNCIONALES: MÓDULO DE REPORTES ---

    @Test
    public void CF09_ReporteExistencias() {
        Inventory inv = new Inventory();
        inv.updateStock("Raton", 5);
        Reports reports = new Reports(inv, new Sales(inv));
        reports.generateReport();
        assertTrue(outContent.toString().contains("Raton: 5"), "Fallo CF-09: El reporte no muestra existencias.");
    }

    @Test
    public void CF10_MetricasNegocio() {
        Inventory inv = new Inventory();
        inv.updateStock("Teclado", 10);
        Sales sales = new Sales(inv);
        //Hacemos una venta real para ver si el reporte la registra
        simulateInput("Teclado\n2\n");
        sales.registerSale();

        Reports reports = new Reports(inv, sales);
        reports.generateReport();
        assertTrue(outContent.toString().toLowerCase().contains("transacciones"), "Fallo CF-10: El reporte es incompleto. Solo muestra stock y omite el historial de transacciones.");
    }

    // --- CASOS NO FUNCIONALES ---

    @Test
    public void CNF01_Rendimiento() {
        Inventory inv = new Inventory();
        inv.updateStock("Monitor", 20);
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            inv.getStock("Monitor");
        }, "Fallo CNF-01: El tiempo de respuesta es excesivo.");
    }

    @Test
    public void CNF02_Accesibilidad() {
        Inventory inv = new Inventory();
        Sales sales = new Sales(inv);
        simulateInput("Teclado\ncinco\n"); // Simula escribir letras en lugar de números
        // FALLO ESPERADO: Lanzará NumberFormatException por falta de try-catch.
        sales.registerSale();
    }

    @Test
    public void CNF03_UsoRecursos() {
        Inventory inv = new Inventory();
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10000; i++) {
                inv.updateStock("Item" + i, 1);
            }
        }, "Fallo CNF-03: Fuga de memoria en uso continuo.");
    }
}