/**
 * 
 */
package ubu.gii.dass.c01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;



/**
 * @author Sofía Calavia
 * @author Andrés Puentes
 * @author Mario Cea
 * @author Alejandro García
 *
 */
public class ReusablePoolTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public static void setUp() throws Exception{
	}

	
	@AfterAll
	public static void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#getInstance()}.
	 */
        @Test
        @DisplayName("testGetInstance")
	public void testGetInstance() {
		ReusablePool instance1 = ReusablePool.getInstance();
      		assertNotNull (instance1,"La instancia adquirida no debe ser nula");

      		ReusablePool instance2 = ReusablePool.getInstance();
      		assertSame(instance1, instance2);		
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	 */
	@Test
        @DisplayName("testAcquireReusable")

	public void testAcquireReusable() {
		ReusablePool pool = ReusablePool.getInstance();

		try {
			Reusable reusable = pool.acquireReusable();

			assertNotNull(reusable,"El objeto reusable adquirido no debería ser nulo");
		} catch (NotFreeInstanceException e) {
			fail("No se lanzaría una excepción si hemos adquirirido un objeto reusable del pool");
        }
		
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
		 * @throws NotFreeInstanceException 
		 */
		 @Test
		 @DisplayName("testReleaseReusable")
		 public void testReleaseReusable() throws DuplicatedInstanceException, NotFreeInstanceException {
	 ReusablePool pool = ReusablePool.getInstance();
		 Reusable reusable = new Reusable();

		 try{
			 pool.releaseReusable(reusable);

			 Reusable acqReusable = pool.acquireReusable();

			 assertEquals(reusable, acqReusable, "El objeto que ha sido adquirido debe ser el mismo que el liberado");
			 assertNotNull(pool.acquireReusable(), "El pool debería tener al menos un objeto reusable después de liberarlo");
		 } catch (DuplicatedInstanceException e) {
			fail("No debería saltar una excepcion al liberar un objeto reusable");

		 } catch (NotFreeInstanceException e) {
			fail("No se debe lanzar una excepción al adquirir un objeto reusable");

		 }
	 }

	 @Test
    @DisplayName("testConcurrentAccess")
    public void testConcurrentAccess() throws InterruptedException {
		ReusablePool pool = ReusablePool.getInstance();
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Reusable> acquiredObjects = new HashSet<>();

        Runnable task = () -> {
            try {
                Reusable reusable = pool.acquireReusable();
                synchronized (acquiredObjects) {
                    acquiredObjects.add(reusable);
                }
                pool.releaseReusable(reusable);
            } catch (NotFreeInstanceException | DuplicatedInstanceException e) {
                fail("No debería lanzarse una excepción en acceso concurrente");
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            new Thread(task).start();
        }

        latch.await(); // Esperar a que todos los hilos terminen

        assertFalse(acquiredObjects.isEmpty(), "Se deben haber adquirido objetos en concurrencia");
    }

}