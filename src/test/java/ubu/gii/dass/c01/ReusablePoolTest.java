package ubu.gii.dass.c01;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ReusablePoolTest {

    @BeforeAll
    public static void setUp() throws Exception {
    }

    @AfterAll
    public static void tearDown() throws Exception {
    }

    @Test
    @DisplayName("Prueba obtener instancia del pool")
    public void testObtenerInstancia() {
        ReusablePool instancia1 = ReusablePool.getInstance();
        assertNotNull(instancia1, "La instancia obtenida no debe ser nula");
        ReusablePool instancia2 = ReusablePool.getInstance();
        assertSame(instancia1, instancia2, "Debe ser la misma instancia del pool");
    }

    @Test
    @DisplayName("Prueba adquirir un objeto reusable")
    public void testAdquirirReusable() {
        ReusablePool pool = ReusablePool.getInstance();
        try {
            Reusable reusable = pool.acquireReusable();
            assertNotNull(reusable, "El objeto reusable adquirido no debería ser nulo");
            assertTrue(reusable instanceof Reusable, "El objeto adquirido debe ser una instancia de Reusable");
        } catch (NotFreeInstanceException e) {
            fail("No debería lanzarse una excepción si se ha adquirido un objeto reusable del pool");
        }
    }

    @Test
    @DisplayName("Prueba liberar un objeto reusable")
    public void testLiberarReusable() throws DuplicatedInstanceException, NotFreeInstanceException {
        ReusablePool pool = ReusablePool.getInstance();
        Reusable reusable = new Reusable();
        try {
            pool.releaseReusable(reusable);
            Reusable acqReusable = pool.acquireReusable();
            assertEquals(reusable, acqReusable, "El objeto adquirido debe ser el mismo que el liberado");
            assertNotNull(pool.acquireReusable(), "El pool debería contener al menos un objeto reusable después de liberarlo");
        } catch (DuplicatedInstanceException e) {
            fail("No debería lanzarse una excepción al liberar un objeto reusable");
        } catch (NotFreeInstanceException e) {
            fail("No debería lanzarse una excepción al adquirir un objeto reusable");
        }
    }

    @Test
    @DisplayName("Prueba adquirir reusable cuando no hay instancias libres")
    public void testAdquirirReusableSinInstanciasLibres() {
        ReusablePool pool = ReusablePool.getInstance();
        try {
            Reusable r1 = pool.acquireReusable();
            Reusable r2 = pool.acquireReusable();
            assertNotNull(r1, "El primer reusable no debería ser nulo");
            assertNotNull(r2, "El segundo reusable no debería ser nulo");
            try {
                pool.acquireReusable();
                fail("Se debería haber lanzado NotFreeInstanceException");
            } catch (NotFreeInstanceException e) {
                assertEquals("No hay más instancias reutilizables disponibles. Inténtalo más tarde", e.getMessage());
            }
            pool.releaseReusable(r1);
            pool.releaseReusable(r2);
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Prueba liberar un objeto duplicado")
    public void testLiberarReusableDuplicado() {
        ReusablePool pool = ReusablePool.getInstance();
        try {
            Reusable reusable = pool.acquireReusable();
            pool.releaseReusable(reusable);
            try {
                pool.releaseReusable(reusable);
                fail("Se debería haber lanzado DuplicatedInstanceException");
            } catch (DuplicatedInstanceException e) {
                assertEquals("Ya existe esa instancia en el pool.", e.getMessage());
            }
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Prueba de ciclo de operaciones en el pool")
    public void testCicloOperacionesPool() {
        ReusablePool pool = ReusablePool.getInstance();
        try {
            Reusable r1 = pool.acquireReusable();
            Reusable r2 = pool.acquireReusable();
            assertNotNull(r1, "El primer reusable no debería ser nulo");
            assertNotNull(r2, "El segundo reusable no debería ser nulo");
            assertTrue(r1 != r2, "Deben ser objetos diferentes");
            pool.releaseReusable(r2);
            pool.releaseReusable(r1);
            Reusable r3 = pool.acquireReusable();
            Reusable r4 = pool.acquireReusable();
            assertSame(r1, r3, "El primer objeto liberado debería ser el primero adquirido");
            assertSame(r2, r4, "El segundo objeto liberado debería ser el segundo adquirido");
            pool.releaseReusable(r3);
            pool.releaseReusable(r4);
        } catch (Exception e) {
            fail("Excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Prueba de funcionalidad del método util() en Reusable")
    public void testUtilReusable() {
        Reusable reusable = new Reusable();
        String utilString = reusable.util();
        assertTrue(utilString.contains(String.valueOf(reusable.hashCode())), "La cadena de utilidad debe contener el hashcode del objeto");
        assertTrue(utilString.contains(":Uso del objeto Reutilizable"), "La cadena de utilidad debe contener el mensaje esperado");
    }

    @Test
    @DisplayName("Prueba de mensajes de excepción")
    public void testMensajesExcepciones() {
        NotFreeInstanceException notFreeEx = new NotFreeInstanceException();
        assertEquals("No hay más instancias reutilizables disponibles. Inténtalo más tarde", notFreeEx.getMessage(), "NotFreeInstanceException debe contener el mensaje correcto");
        DuplicatedInstanceException dupEx = new DuplicatedInstanceException();
        assertEquals("Ya existe esa instancia en el pool.", dupEx.getMessage(), "DuplicatedInstanceException debe contener el mensaje correcto");
    }
}
