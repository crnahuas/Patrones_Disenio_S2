package com.crnahuas.app;

/**
 * DiscountManager (Singleton). Punto único de acceso para utilidades comunes de
 * descuentos (p. ej. redondeo).
 */
public final class DiscountManager {

    /**
     * Instancia única (creación ansiosa; thread-safe por carga de clase).
     */
    private static final DiscountManager INSTANCE = new DiscountManager();

    /**
     * Constructor privado: evita instanciación externa.
     */
    private DiscountManager() {
    }

    /**
     * Punto de acceso global a la instancia única.
     *
     * @return la única instancia de DiscountManager
     */
    public static DiscountManager getInstance() {
        return INSTANCE;
    }

    /**
     * Normaliza el precio del componente actual (decorado o no) a 2 decimales.
     *
     * @param comp componente del cual leer el precio
     * @return precio con 2 decimales
     * @throws IllegalArgumentException si comp es nulo
     */
    public double calcularPrecio(Component comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Component nulo");
        }
        return round2(comp.getPrecio());
    }

    /**
     * Redondeo simple a 2 decimales (nivel estudiante).
     */
    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
