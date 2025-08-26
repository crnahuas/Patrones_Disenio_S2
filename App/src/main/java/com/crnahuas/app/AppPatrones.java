package com.crnahuas.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Aplicación de consola para gestionar carrito y descuentos. - Usa
 * BufferedReader (sin Scanner). - Formatea columnas con printf para que todo se
 * vea ordenado. - Integra Singleton + Decorator + Command y se aplicaron mejoras sugeridas
 * en la entrega anterior.
 */
public class AppPatrones {

    private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));

    /* ===================== Utilidades ===================== */
    /**
     * Pausa simple para lectura.
     */
    private static void pause() {
        System.out.println("Presiona ENTER para continuar...");
        try {
            IN.readLine();
        } catch (IOException ignored) {
        }
    }

    /**
     * Lee una línea (retorna "" si hay error).
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return IN.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Lee entero válido mediante reintento.
     */
    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt).trim());
            } catch (Exception e) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    /**
     * Lee double válido mediante reintento.
     */
    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt).trim());
            } catch (Exception e) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }

    /* ===================== Impresiones con printf ===================== */
    /**
     * Muestra el catálogo en forma tabular.
     */
    private static void printCatalog(Component[] catalog) {
        System.out.println("\n=== Catálogo de productos ===");
        System.out.printf("%-3s | %-18s | %-10s | %8s%n", "#", "Producto", "Categoria", "Precio");
        System.out.println("-------------------------------------------------------");
        for (int i = 0; i < catalog.length; i++) {
            System.out.printf("%3d | %-18s | %-10s | %8.2f%n",
                    i + 1, catalog[i].getNombre(), catalog[i].getCategoria(), catalog[i].getPrecio());
        }
    }

    /**
     * Muestra el carrito en forma tabular.
     */
    private static void printCart(Cart cart) {
        System.out.println("\n=== Carrito de compras===");
        if (cart.getItems().isEmpty()) {
            System.out.println("(vacío)");
            return;
        }

        System.out.printf("%-3s | %-18s | %-10s | %8s | %8s | %3s | %8s%n",
                "#", "Producto", "Categoria", "Precio", "Precio Desc.", "Cant", "Total");
        System.out.println("--------------------------------------------------------------------------------");

        DiscountManager dm = DiscountManager.getInstance();
        int i = 1;
        for (CartItem ci : cart.getItems()) {
            double base = ci.getBase().getPrecio();
            double desc = dm.calcularPrecio(ci.getActual());
            double linea = Math.round(desc * ci.getQuantity() * 100.0) / 100.0;

            System.out.printf("%3d | %-18s | %-10s | %8.2f | %8.2f | %3d | %8.2f%n",
                    i++, ci.getBase().getNombre(), ci.getBase().getCategoria(),
                    base, desc, ci.getQuantity(), linea);
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Subtotal Carrito: %8.2f%n", cart.total());
    }

    /* ===================== Menú ===================== */
    private static void menu() {
        System.out.println("\n===== MENÚ =====");
        System.out.println("1) Listar catálogo");
        System.out.println("2) Agregar al carrito");
        System.out.println("3) Eliminar producto");
        System.out.println("4) Disminuir cantidad de un producto");
        System.out.println("5) Ver carrito");
        System.out.println("6) Aplicar 10% a TODO el carrito");
        System.out.println("7) Aplicar 20% por CATEGORÍA a TODO el carrito");
        System.out.println("8) Aplicar MONTO FIJO por línea a TODO el carrito");
        System.out.println("9) Limpiar DESCUENTOS del carrito");
        System.out.println("0) Salir");
    }

    /* ===================== Main ===================== */
    /**
     * Main de la app de consola. - Catálogo fijo para simplificar la demo. -
     * Commands para mutar el carrito y aplicar decoradores a todas las líneas.
     */
    public static void main(String[] args) {
        // Catálogo base (nivel estudiante)
        Component[] catalog = new Component[]{
            new ProductComponent("Polera básica", "BASICO", 14990),
            new ProductComponent("Zapatilla running", "CALZADO", 49990),
            new ProductComponent("Jeans slim", "BASICO", 29990),
            new ProductComponent("Chaqueta liviana", "ABRIGO", 39990)
        };

        Cart cart = new Cart();
        CartContext cartCtx = new CartContext(cart);
        Invoker inv = new Invoker();

        int op;
        do {
            menu();
            op = readInt("Opción: ");
            switch (op) {
                case 1 -> { // Listar catálogo
                    printCatalog(catalog);
                    pause();
                }
                case 2 -> { // Agregar (por índice de catálogo)
                    printCatalog(catalog);
                    int idx = readInt("Número de producto a agregar: ");
                    if (idx < 1 || idx > catalog.length) {
                        System.out.println("Índice inválido");
                        break;
                    }
                    int qty = readInt("Cantidad: ");
                    inv.agregar(new AddProductToCartCommand(cartCtx, catalog[idx - 1], qty));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 3 -> { // Eliminar por ÍNDICE de LÍNEA (robusto)
                    if (cart.getItems().isEmpty()) {
                        System.out.println("Carrito vacío");
                        break;
                    }
                    printCart(cart);
                    int line = readInt("Número de línea a eliminar (columna #): ");
                    inv.agregar(new RemoveProductAtIndexCommand(cartCtx, line));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 4 -> { // Disminuir por ÍNDICE de LÍNEA (robusto)
                    if (cart.getItems().isEmpty()) {
                        System.out.println("Carrito vacío");
                        break;
                    }
                    printCart(cart);
                    int line = readInt("Número de línea a disminuir (columna #): ");
                    int qty = readInt("Cantidad a disminuir: ");
                    inv.agregar(new DecreaseProductQtyAtIndexCommand(cartCtx, line, qty));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 5 -> { // Ver carrito
                    printCart(cart);
                    pause();
                }
                case 6 -> { // 10% global (se apila sobre lo actual)
                    inv.agregar(new ApplyTenPercentToAllCommand(cartCtx));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 7 -> { // 20% por categoría (se apila)
                    String cat = readLine("Categoría objetivo (ej. BASICO): ");
                    inv.agregar(new ApplyCategoryTwentyToAllCommand(cartCtx, cat));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 8 -> { // Monto fijo por línea (se apila)
                    double monto = readDouble("Monto a restar por línea: ");
                    inv.agregar(new ApplyFlatAmountToAllCommand(cartCtx, monto));
                    inv.ejecutarTodo();
                    printCart(cart);
                    pause();
                }
                case 9 -> { // Limpiar descuentos (idóneo antes de aplicar un nuevo “plan”)
                    inv.agregar(new ResetDiscountsAllCommand(cartCtx)); // ahora también como Command
                    inv.ejecutarTodo();
                    System.out.println("Descuentos limpiados (cada línea volvió a su precio base).");
                    printCart(cart);
                    pause();
                }
                case 0 ->
                    System.out.println("Saliendo...");
                default ->
                    System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }
}
