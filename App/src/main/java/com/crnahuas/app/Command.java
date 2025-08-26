package com.crnahuas.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz del patrón Command. La pauta pedía un método 'Ejecutar' (usamos
 * 'ejecutar' por convención Java).
 */
public interface Command {

    void ejecutar();

    String nombre();
}

/**
 * Invoker: acumula y ejecuta comandos en orden FIFO.
 */
class Invoker {

    private final List<Command> cola = new ArrayList<>();

    void agregar(Command c) {
        cola.add(c);
    }

    void ejecutarTodo() {
        for (Command c : cola) {
            c.ejecutar();
        }
        cola.clear();
    }
}

/**
 * Contexto de carrito para comandos a nivel de carro.
 */
class CartContext {

    private final Cart cart;

    CartContext(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("cart nulo");
        }
        this.cart = cart;
    }

    Cart getCart() {
        return cart;
    }
}

/* ===================== Comandos de CARRITO ===================== */
/**
 * Agrega un producto (con cantidad) al carrito.
 */
class AddProductToCartCommand implements Command {

    private final CartContext ctx;
    private final Component base;
    private final int qty;

    AddProductToCartCommand(CartContext ctx, Component base, int qty) {
        this.ctx = ctx;
        this.base = base;
        this.qty = qty;
    }

    @Override
    public void ejecutar() {
        ctx.getCart().add(base, qty);
    }

    @Override
    public String nombre() {
        return "Agregar " + qty + "x " + base.getNombre();
    }
}

/* --- NUEVO: comandos por índice (más robustos) --- */
class RemoveProductAtIndexCommand implements Command {

    private final CartContext ctx;
    private final int index1;

    RemoveProductAtIndexCommand(CartContext ctx, int oneBasedIndex) {
        this.ctx = ctx;
        this.index1 = oneBasedIndex;
    }

    @Override
    public void ejecutar() {
        ctx.getCart().removeAtIndex(index1);
    }

    @Override
    public String nombre() {
        return "Eliminar línea #" + index1;
    }
}

class DecreaseProductQtyAtIndexCommand implements Command {

    private final CartContext ctx;
    private final int index1;
    private final int amount;

    DecreaseProductQtyAtIndexCommand(CartContext ctx, int oneBasedIndex, int amount) {
        this.ctx = ctx;
        this.index1 = oneBasedIndex;
        this.amount = amount;
    }

    @Override
    public void ejecutar() {
        ctx.getCart().decreaseAtIndex(index1, amount);
    }

    @Override
    public String nombre() {
        return "Disminuir " + amount + " de línea #" + index1;
    }
}

/* ===================== Descuentos a TODO el carrito ===================== */
class ApplyTenPercentToAllCommand implements Command {

    private final CartContext ctx;

    ApplyTenPercentToAllCommand(CartContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void ejecutar() {
        for (CartItem ci : ctx.getCart().getItems()) {
            ci.setActual(new TenPercentDecorator(ci.getActual())); // se apila sobre lo actual
        }
    }

    @Override
    public String nombre() {
        return "10% carrito";
    }
}

class ApplyCategoryTwentyToAllCommand implements Command {

    private final CartContext ctx;
    private final String categoria;

    ApplyCategoryTwentyToAllCommand(CartContext ctx, String categoria) {
        this.ctx = ctx;
        this.categoria = categoria;
    }

    @Override
    public void ejecutar() {
        for (CartItem ci : ctx.getCart().getItems()) {
            ci.setActual(new CategoryTwentyDecorator(ci.getActual(), categoria)); // se apila
        }
    }

    @Override
    public String nombre() {
        return "20% cat=" + categoria;
    }
}

class ApplyFlatAmountToAllCommand implements Command {

    private final CartContext ctx;
    private final double monto;

    ApplyFlatAmountToAllCommand(CartContext ctx, double monto) {
        this.ctx = ctx;
        this.monto = monto;
    }

    @Override
    public void ejecutar() {
        for (CartItem ci : ctx.getCart().getItems()) {
            ci.setActual(new FlatAmountDecorator(ci.getActual(), monto)); // se apila
        }
    }

    @Override
    public String nombre() {
        return "-" + monto + " por línea";
    }
}

/* --- NUEVO: reset de descuentos vía comando (consistencia con Command) --- */
class ResetDiscountsAllCommand implements Command {

    private final CartContext ctx;

    ResetDiscountsAllCommand(CartContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void ejecutar() {
        ctx.getCart().resetAllDecorators();
    }

    @Override
    public String nombre() {
        return "Reset descuentos (todas las líneas)";
    }
}
