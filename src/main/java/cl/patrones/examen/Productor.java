package cl.patrones.examen;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary; // Agregamos Primary

import cl.patrones.examen.productos.service.ProductoServiceImpl;
import cl.patrones.examen.productos.service.ProductoServiceDecorator; // Importamos nuestro decorador

@Configuration
public class Productor {

	// 1. Spring crea el bean original de la librería
	@Bean
	ProductoServiceImpl productoServiceImpl() {
		return new ProductoServiceImpl();
	}

	// 2. Spring inyecta el original aquí, crea el decorador, y lo define como el oficial
	@Bean
	@Primary
	ProductoServiceDecorator productoServiceDecorator(ProductoServiceImpl original) {
		return new ProductoServiceDecorator(original);
	}
}