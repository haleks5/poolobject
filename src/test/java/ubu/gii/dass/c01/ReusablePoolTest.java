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
            fail("No debería lanzarse una excepción si hay objetos disponibles en el pool");
        }
    }

    @Test
    @DisplayName("Prueba liberar un objeto reusable")
    public void testLiberarReusable() throws DuplicatedInstanceException, NotFreeInstanceException {
        ReusablePool pool = ReusablePool.getInstance();
        Reusable reusable = pool.acquireReusable();
        try {
            pool.releaseReusable(reusable);
            Reusable acqReusable = pool.acquireReusable();
            assertEquals(reusable, acqReusable, "El objeto adquirido debe ser el mismo que el liberado");
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
}