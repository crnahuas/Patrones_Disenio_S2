package com.crnahuas.app;

/**
 * Decorador abstracto que delega en el componente envuelto. Las subclases
 * sobreescriben getPrecio() para aplicar reglas.
 */
public abstract class DiscountDecorator implements Component {

    protected final Component componente;

    /**
     * @param componente componente a envolver (no nulo)
     */
    protected DiscountDecorator(Component componente) {
        if (componente == null) {
            throw new IllegalArgumentException("Componente nulo");
        }
        this.componente = componente;
    }

    @Override
    public String getNombre() {
        return componente.getNombre();
    }

    @Override
    public String getCategoria() {
        return componente.getCategoria();
    }

    @Override
    public double getPrecio() {
        return componente.getPrecio();
    }
}
