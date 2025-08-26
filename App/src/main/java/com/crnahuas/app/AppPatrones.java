package com.crnahuas.app;

public class AppPatrones {

    private static void imprimirCarrito(String titulo, Cart cart) {
        System.out.println("=== " + titulo + " ===");
        System.out.printf("%-3s | %-18s | %-10s | Base: %8s | Desc: %8s | Qty: %3s | Línea: %8s%n",
                "#", "Producto", "Categoria", "Precio", "Precio", "Cant", "Total");
        System.out.println("--------------------------------------------------------------------------");

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
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("Subtotal Carrito: %8.2f%n", cart.total());
    }

    public static void main(String[] args) {
        // Productos base
        Component polera = new ProductComponent("Polera básica", "BASICO", 14990);
        Component zapas = new ProductComponent("Zapatilla running", "CALZADO", 49990);
        Component jeans = new ProductComponent("Jeans slim", "BASICO", 29990);

        // Carrito y contexto
        Cart cart = new Cart();
        CartContext cartCtx = new CartContext(cart);
        Invoker inv = new Invoker();

        // 1) Agregar productos al carrito
        inv.agregar(new AddProductToCartCommand(cartCtx, polera, 2)); // 2 poleras
        inv.agregar(new AddProductToCartCommand(cartCtx, zapas, 1)); // 1 zapatilla
        inv.agregar(new AddProductToCartCommand(cartCtx, jeans, 1)); // 1 jeans
        inv.ejecutarTodo();
        imprimirCarrito("Carrito inicial", cart);

        // 2) Aplicar descuentos globales al carrito: 10% a todo + 20% a BASICO
        inv.agregar(new ApplyTenPercentToAllCommand(cartCtx));
        inv.agregar(new ApplyCategoryTwentyToAllCommand(cartCtx, "BASICO"));
        inv.ejecutarTodo();
        imprimirCarrito("Tras 10% global + 20% BASICO", cart);

        // 3) Aplicar monto fijo de 2000 a todas las líneas
        inv.agregar(new ApplyFlatAmountToAllCommand(cartCtx, 2000));
        inv.ejecutarTodo();
        imprimirCarrito("Tras -2000 por línea", cart);

        // 4) Eliminar un producto por nombre (ej.: Jeans slim)
        inv.agregar(new RemoveProductFromCartCommand(cartCtx, "Jeans slim"));
        inv.ejecutarTodo();
        imprimirCarrito("Tras eliminar 'Jeans slim'", cart);
    }

}
