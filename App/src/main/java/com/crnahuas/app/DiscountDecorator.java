package com.crnahuas.app;

/**
 * Decorador abstracto del patrón Decorator. Envuelve un Component y por defecto
 * delega sus métodos.
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
    } // por defecto, pasa-through
}

/**
 * Decorador concreto: 10% OFF a cualquier producto.
 */
class TenPercentDecorator extends DiscountDecorator {

    public TenPercentDecorator(Component c) {
        super(c);
    }

    @Override
    public double getPrecio() {
        return componente.getPrecio() * 0.90;
    }
}

/**
 * Decorador concreto: 20% OFF si la categoría coincide (case-insensitive).
 */
class CategoryTwentyDecorator extends DiscountDecorator {

    private final String categoriaObjetivo;

    /**
     * @param c componente a decorar
     * @param categoriaObjetivo categoría elegible para el 20% (no vacía)
     */
    public CategoryTwentyDecorator(Component c, String categoriaObjetivo) {
        super(c);
        if (categoriaObjetivo == null || categoriaObjetivo.isBlank()) {
            throw new IllegalArgumentException("Categoria objetivo requerida");
        }
        this.categoriaObjetivo = categoriaObjetivo;
    }

    @Override
    public double getPrecio() {
        double base = componente.getPrecio();
        return componente.getCategoria().equalsIgnoreCase(categoriaObjetivo) ? base * 0.80 : base;
    }
}

/**
 * Decorador concreto: resta un monto fijo (nunca deja negativo).
 */
class FlatAmountDecorator extends DiscountDecorator {

    private final double monto;

    /**
     * @param c componente a decorar
     * @param monto monto a restar (>= 0)
     */
    public FlatAmountDecorator(Component c, double monto) {
        super(c);
        if (monto < 0) {
            throw new IllegalArgumentException("Monto invalido");
        }
        this.monto = monto;
    }

    @Override
    public double getPrecio() {
        double r = componente.getPrecio() - monto;
        return (r < 0) ? 0 : r;
    }
}
