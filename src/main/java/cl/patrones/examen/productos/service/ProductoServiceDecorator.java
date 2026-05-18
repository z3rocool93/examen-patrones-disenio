package cl.patrones.examen.productos.service;

import cl.patrones.examen.productos.domain.Producto;
import cl.patrones.examen.decorator.DescuentoDinamicoDecorator;

import java.util.ArrayList;
import java.util.List;

public class ProductoServiceDecorator implements ProductoService {

    private final ProductoService servicioOriginal;

    public ProductoServiceDecorator(ProductoService servicioOriginal) {
        this.servicioOriginal = servicioOriginal;
    }

    @Override
    public List<? extends Producto> getProductos() {
        List<? extends Producto> originales = servicioOriginal.getProductos();
        List<Producto> decorados = new ArrayList<>();

        for (Producto p : originales) {
            decorados.add(new DescuentoDinamicoDecorator(p));
        }

        return decorados;
    }
}