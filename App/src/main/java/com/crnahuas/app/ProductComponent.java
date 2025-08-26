package com.crnahuas.app;

/**
 * Producto concreto (sin descuentos). Implementa Component.
 */
public class ProductComponent implements Component {

    private final String nombre;
    private final String categoria;
    private final double precioBase;

    /**
     * @param nombre nombre del producto (no vacío)
     * @param categoria categoría del producto (no vacía)
     * @param precioBase precio base (>= 0)
     */
    public ProductComponent(String nombre, String categoria, double precioBase) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre requerido");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("Categoria requerida");
        }
        if (precioBase < 0) {
            throw new IllegalArgumentException("Precio invalido");
        }
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioBase = precioBase;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getCategoria() {
        return categoria;
    }

    @Override
    public double getPrecio() {
        return precioBase;
    }
}
