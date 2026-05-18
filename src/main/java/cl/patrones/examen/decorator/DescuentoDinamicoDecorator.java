package cl.patrones.examen.decorator;

import cl.patrones.examen.productos.domain.Producto;
import cl.patrones.examen.productos.domain.Categoria;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;

public class DescuentoDinamicoDecorator extends ProductoDecorator {

    public DescuentoDinamicoDecorator(Producto productoOriginal) {
        super(productoOriginal);
    }

    @Override
    public Long getDescuento() {
        String diaActual = LocalDate.now().getDayOfWeek().toString();
        String nombreProducto = getNombre() != null ? getNombre().toLowerCase() : "";
        double descuentoDiaCategoria = 0.0;

        // Reglas combinadas de día y categoría
        if ("MONDAY".equals(diaActual) && nombreProducto.contains("compresor")) {
            descuentoDiaCategoria = 6.0;
        } else if ("TUESDAY".equals(diaActual) && nombreProducto.contains("esmeril")) {
            descuentoDiaCategoria = 8.0;
        } else if ("WEDNESDAY".equals(diaActual) && nombreProducto.contains("taladro")) {
            descuentoDiaCategoria = 10.0;
        }

        // Regla de empleado
        double descuentoEmpleado = 0.0;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getAuthorities().toString().contains("ROLE_EMPLEADO")) {
            descuentoEmpleado = 5.0;
        }

        // Se aplica el de mayor valor
        double mejorDescuento = Math.max(descuentoDiaCategoria, descuentoEmpleado);
        return Math.round(mejorDescuento);
    }

    @Override
    public Long getPrecioFinal() {
        Long precioListaObj = getPrecioLista();
        if (precioListaObj == null) {
            return 0L;
        }

        double precioLista = precioListaObj.doubleValue();
        double mejorDescuentoPorcentaje = getDescuento().doubleValue();

        // 3. Aplicar la rebaja real sobre el precio base
        double ahorro = precioLista * (mejorDescuentoPorcentaje / 100.0);
        return Math.round(precioLista - ahorro);
    }

    private double calcularDescuentoPorDia() {
        String diaActual = LocalDate.now().getDayOfWeek().toString();
        if ("MONDAY".equals(diaActual) || "WEDNESDAY".equals(diaActual)) {
            return 10.0;
        }
        return 0.0;
    }

    private double calcularDescuentoPorCategoria() {
        String nombreProducto = getNombre();
        if (nombreProducto == null) return 0.0;

        String nombre = nombreProducto.toLowerCase();
        if (nombre.contains("compresor")) {
            return 12.0;
        } else if (nombre.contains("esmeril")) {
            return 15.0;
        } else if (nombre.contains("taladro")) {
            return 8.0;
        }
        return 0.0;
    }

    private double calcularDescuentoPorUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String roles = auth.getAuthorities().toString();
            if (roles.contains("ROLE_EMPLEADO")) {
                return 20.0;
            } else if (roles.contains("ROLE_CLIENTE")) {
                return 5.0;
            }
        }
        return 0.0;
    }
}