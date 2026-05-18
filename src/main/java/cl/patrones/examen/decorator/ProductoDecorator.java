package cl.patrones.examen.decorator;

import cl.patrones.examen.productos.domain.Producto;
import cl.patrones.examen.productos.domain.Categoria;

public abstract class ProductoDecorator implements Producto {
    protected Producto productoOriginal;

    public ProductoDecorator(Producto productoOriginal) {
        this.productoOriginal = productoOriginal;
    }

    @Override
    public String getSku() {
        return this.productoOriginal.getSku();
    }

    @Override
    public String getNombre() {
        return this.productoOriginal.getNombre();
    }

    @Override
    public String getImagen() {
        return this.productoOriginal.getImagen();
    }

    @Override
    public Long getCosto() {
        return this.productoOriginal.getCosto();
    }

    @Override
    public Long getPrecioLista() {
        return this.productoOriginal.getPrecioLista();
    }

    @Override
    public Long getDescuento() {
        return this.productoOriginal.getDescuento();
    }

    @Override
    public Categoria getCategoria() {
        return this.productoOriginal.getCategoria();
    }

    @Override
    public abstract Long getPrecioFinal();
}