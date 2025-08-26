package com.crnahuas.app;

/**
 * Componente básico que expone el precio "actual" del producto. Los decoradores
 * envolverán este componente para modificar su precio.
 */
public interface Component {

    /**
     * @return nombre del producto
     */
    String getNombre();

    /**
     * @return categoría del producto (para reglas por categoría)
     */
    String getCategoria();

    /**
     * @return precio actual (con o sin decoradores aplicados)
     */
    double getPrecio();
}
