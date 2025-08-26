package com.crnahuas.app;

/**
 * Componente base del patrón Decorator. Expone la API mínima que verán tanto el
 * producto base como los decoradores.
 */
public interface Component {

    /**
     * @return nombre del producto (para mostrar y buscar)
     */
    String getNombre();

    /**
     * @return categoría del producto (para reglas por categoría)
     */
    String getCategoria();

    /**
     * @return precio "actual" del componente (base o decorado)
     */
    double getPrecio();
}
