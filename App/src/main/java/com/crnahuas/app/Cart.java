package com.crnahuas.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Carrito con múltiples líneas (ítems) y operaciones básicas. Cada línea
 * mantiene su componente "actual" (posiblemente decorado) y su cantidad.
 */
public class Cart {

    private final List<CartItem> items = new ArrayList<>();

    /* --- Helpers internos --- */
    private String norm(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }

    /**
     * Agrega un producto al carrito. Si ya existe por nombre, acumula la
     * cantidad.
     *
     * @param base producto base (no nulo)
     * @param qty cantidad (> 0)
     */
    public void add(Component base, int qty) {
        if (base == null) {
            throw new IllegalArgumentException("base nula");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("qty > 0");
        }
        String target = norm(base.getNombre());
        for (CartItem ci : items) {
            if (norm(ci.getBase().getNombre()).equals(target)) {
                ci.addQuantity(qty);
                return;
            }
        }
        items.add(new CartItem(base, qty));
    }

    /**
     * Elimina la línea por nombre (normalizado).
     */
    public boolean removeByProductName(String nombre) {
        String target = norm(nombre);
        for (Iterator<CartItem> it = items.iterator(); it.hasNext();) {
            CartItem ci = it.next();
            if (norm(ci.getBase().getNombre()).equals(target)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Disminuye cantidad por nombre (normalizado); si llega a 0, elimina la
     * línea.
     */
    public boolean decreaseQuantityByProductName(String nombre, int amount) {
        String target = norm(nombre);
        for (Iterator<CartItem> it = items.iterator(); it.hasNext();) {
            CartItem ci = it.next();
            if (norm(ci.getBase().getNombre()).equals(target)) {
                ci.decreaseQuantity(amount);
                if (ci.getQuantity() <= 0) {
                    it.remove();
                }
                return true;
            }
        }
        return false;
    }

    /* --------- NUEVO: operaciones por índice (más seguras) --------- */
    /**
     * Elimina por índice (1-based).
     */
    public boolean removeAtIndex(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= items.size()) {
            return false;
        }
        items.remove(idx);
        return true;
    }

    /**
     * Disminuye cantidad por índice (1-based). Si queda en 0, elimina la línea.
     */
    public boolean decreaseAtIndex(int oneBasedIndex, int amount) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= items.size()) {
            return false;
        }
        CartItem ci = items.get(idx);
        ci.decreaseQuantity(amount);
        if (ci.getQuantity() <= 0) {
            items.remove(idx);
        }
        return true;
    }

    /** Vista de solo lectura de las líneas del carrito (no API pública). */
    List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getItemCount() {
        return items.size();
    }

    /**
     * Restaura todas las líneas a su producto base (limpia descuentos).
     */
    public void resetAllDecorators() {
        for (CartItem ci : items) {
            ci.resetDecorators();
        }
    }

    /**
     * Subtotal del carrito = suma (precio actual decorado * cantidad) por
     * línea.
     *
     * @return total con 2 decimales
     */
    public double total() {
        double sum = 0;
        DiscountManager dm = DiscountManager.getInstance();
        for (CartItem ci : items) {
            double unit = dm.calcularPrecio(ci.getActual());
            sum += unit * ci.getQuantity();
        }
        return Math.round(sum * 100.0) / 100.0;
    }
}

/**
 * Línea del carrito: mantiene producto base, versión decorada y cantidad.
 */
class CartItem {

    private final Component base;
    private Component actual;
    private int quantity;

    /**
     * @param base producto base (no nulo)
     * @param quantity cantidad (> 0)
     */
    public CartItem(Component base, int quantity) {
        if (base == null) {
            throw new IllegalArgumentException("base nula");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity > 0");
        }
        this.base = base;
        this.actual = base; // al inicio sin decoradores
        this.quantity = quantity;
    }

    /**
     * @return producto base (sin descuentos)
     */
    public Component getBase() {
        return base;
    }

    /**
     * @return componente actual (ya decorado si se aplicaron descuentos)
     */
    public Component getActual() {
        return actual;
    }

    /**
     * Establece un nuevo componente decorado para la línea.
     */
    public void setActual(Component nuevo) {
        this.actual = nuevo;
    }

    /**
     * @return cantidad actual de la línea
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Incrementa cantidad (must > 0).
     */
    public void addQuantity(int extra) {
        if (extra <= 0) {
            throw new IllegalArgumentException("extra > 0");
        }
        this.quantity += extra;
    }

    /**
     * Disminuye cantidad; si baja de 0, se corrige a 0 (el llamador puede
     * eliminar la línea).
     */
    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount > 0");
        }
        this.quantity -= amount;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    /**
     * Limpia todos los decoradores y vuelve al producto base.
     */
    public void resetDecorators() {
        this.actual = base;
    }
}
