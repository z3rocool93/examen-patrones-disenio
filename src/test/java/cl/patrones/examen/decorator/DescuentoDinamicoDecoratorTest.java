package cl.patrones.examen.decorator;

import cl.patrones.examen.productos.domain.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DescuentoDinamicoDecoratorTest {

    @Mock
    private Producto productoMock;

    @Mock
    private Authentication authMock;

    @Mock
    private SecurityContext securityContextMock;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(securityContextMock.getAuthentication()).thenReturn(authMock);
        SecurityContextHolder.setContext(securityContextMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // TÉCNICA 1: PARTICIÓN DE EQUIVALENCIA (Clases válidas)
    // ========================================================================

    @Test
    @DisplayName("Partición 1: Cliente normal sin promos de día ni categoría (0%)")
    void testClienteNormalSinPromo() {
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));

        when(productoMock.getNombre()).thenReturn("Martillo");
        when(productoMock.getPrecioLista()).thenReturn(10000L);

        DescuentoDinamicoDecorator decorador = new DescuentoDinamicoDecorator(productoMock);
        LocalDate juevesSinPromo = LocalDate.of(2023, 10, 26);

        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(juevesSinPromo);

            assertEquals(0L, decorador.getDescuento(), "El descuento debe ser 0%");
            assertEquals(10000L, decorador.getPrecioFinal(), "El precio final debe ser igual al de lista");
        }
    }

    @Test
    @DisplayName("Partición 2: Cliente compra Compresor un Lunes (6%)")
    void testClienteConPromoDelDia() {
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));

        when(productoMock.getNombre()).thenReturn("Compresor 2HP");
        when(productoMock.getPrecioLista()).thenReturn(100000L);

        DescuentoDinamicoDecorator decorador = new DescuentoDinamicoDecorator(productoMock);
        LocalDate lunesDePromo = LocalDate.of(2023, 10, 23);

        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(lunesDePromo);

            assertEquals(6L, decorador.getDescuento(), "Debe aplicar 6% por ser Lunes y Compresor");
            assertEquals(94000L, decorador.getPrecioFinal(), "El precio final debe reflejar el 6% de descuento");
        }
    }

    @Test
    @DisplayName("Partición 3: Empleado en un día sin promos (5%)")
    void testEmpleadoSinPromoDelDia() {
        when(authMock.isAuthenticated()).thenReturn(true);
        // Usamos ROLE_EMPLEADO
        when(authMock.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")));

        when(productoMock.getNombre()).thenReturn("Martillo");
        when(productoMock.getPrecioLista()).thenReturn(10000L);

        DescuentoDinamicoDecorator decorador = new DescuentoDinamicoDecorator(productoMock);
        LocalDate juevesSinPromo = LocalDate.of(2023, 10, 26);

        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(juevesSinPromo);

            assertEquals(5L, decorador.getDescuento(), "Debe aplicar 5% exclusivo de empleado");
            assertEquals(9500L, decorador.getPrecioFinal(), "El precio debe reflejar el descuento de empleado");
        }
    }

    // ========================================================================
    // TÉCNICA 2: ANÁLISIS DE VALORES LÍMITE (Choque de reglas)
    // ========================================================================

    @Test
    @DisplayName("Límite: Empleado (5%) compra Esmeril un Martes (8%). Se aplica el mayor (8%), no se suman.")
    void testAnalisisDeLimitesChoqueDeDescuentos() {
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")));

        when(productoMock.getNombre()).thenReturn("Esmeril Angular");
        when(productoMock.getPrecioLista()).thenReturn(50000L);

        DescuentoDinamicoDecorator decorador = new DescuentoDinamicoDecorator(productoMock);
        LocalDate martesDePromo = LocalDate.of(2023, 10, 24);

        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(martesDePromo);

            assertEquals(8L, decorador.getDescuento(), "Límite superado: Debe aplicar 8%, descartando el 5%");
            assertEquals(46000L, decorador.getPrecioFinal(), "El precio final se calcula con el 8%");
        }
    }
}