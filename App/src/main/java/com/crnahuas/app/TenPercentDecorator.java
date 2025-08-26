package com.crnahuas.app;

/**
 * Aplica 10% de descuento a cualquier producto.
 */
public class TenPercentDecorator extends DiscountDecorator {

    public TenPercentDecorator(Component componente) {
        super(componente);
    }

    @Override
    public double getPrecio() {
        return componente.getPrecio() * 0.90;
    }
}
