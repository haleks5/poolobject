/**
 * 
 */
package ubu.gii.dass.c01;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
		// Crear una instancia del pool
		ReusablePool instance1 = ReusablePool.getInstance();
			//Verifica que la instancia no sea nula
      		assertNotNull (instance1,"La instancia adquirida no debe ser nula");
			//Comprueba si la instancia 1 es igual que la instancia 2
      		ReusablePool instance2 = ReusablePool.getInstance();
      		assertSame(instance1, instance2);		
	}
	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	*/
		
		
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
		 * @throws NotFreeInstanceException 
		*/ 
		 @Test
		 @DisplayName("testReleaseReusable")
		 public void testReleaseReusable() throws DuplicatedInstanceException, NotFreeInstanceException {
			// Crear una instancia del pool
	 		ReusablePool pool = ReusablePool.getInstance();
			//Crea un nuevo objeto reusable
			Reusable reusable = new Reusable();
		 try{
			//Libera este objeto del pool
			 pool.releaseReusable(reusable);
			//Intenta adquirir un nuevo objeto de este
			 Reusable acqReusable = pool.acquireReusable();
			// Verifica si el objeto adquirido es el mismo que el objeto liberado
			 assertEquals(reusable, acqReusable, "El objeto que ha sido adquirido debe ser el mismo que el liberado");
			//Verifica que el poll tiene un objeto antes de liberarlo
			 assertNotNull(pool.acquireReusable(), "El pool debería tener al menos un objeto reusable después de liberarlo");
		 } catch (DuplicatedInstanceException e) {
			//Si se lanza una excepción, imprime un mensaje de error
			fail("No debería saltar una excepcion al liberar un objeto reusable");
		 } catch (NotFreeInstanceException e) {
			//Si se lanza una excepción, imprime un mensaje de error
			fail("No se debe lanzar una excepción al adquirir un objeto reusable");
		 }
	 }

	/**
	 * Test method for exception when no free instances are available.
	 */
	@Test
	@DisplayName("testAcquireReusableWhenNoFreeInstances")
	public void testAcquireReusableWhenNoFreeInstances() {
		// Get the pool instance
		ReusablePool pool = ReusablePool.getInstance();
		
		try {
			// Acquire all available reusables (the pool has a size of 2)
			Reusable r1 = pool.acquireReusable();
			Reusable r2 = pool.acquireReusable();
			
			// Verify both objects were acquired
			assertNotNull(r1, "First reusable should not be null");
			assertNotNull(r2, "Second reusable should not be null");
			
			// Try to acquire a third reusable (should throw NotFreeInstanceException)
			try {
				pool.acquireReusable();
				fail("Should have thrown NotFreeInstanceException");
			} catch (NotFreeInstanceException e) {
				// Expected exception
				assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", e.getMessage());
			}
			
			// Clean up - return the objects to the pool
			pool.releaseReusable(r1);
			pool.releaseReusable(r2);
			
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	/**
	 * Test method for exception when releasing a duplicate instance.
	 */
	@Test
	@DisplayName("testReleaseReusableDuplicated")
	public void testReleaseReusableDuplicated() {
		// Get the pool instance
		ReusablePool pool = ReusablePool.getInstance();
		
		try {
			// Acquire a reusable
			Reusable reusable = pool.acquireReusable();
			
			// Release it back to the pool
			pool.releaseReusable(reusable);
			
			// Try to release it again (should throw DuplicatedInstanceException)
			try {
				pool.releaseReusable(reusable);
				fail("Should have thrown DuplicatedInstanceException");
			} catch (DuplicatedInstanceException e) {
				// Expected exception
				assertEquals("Ya existe esa instancia en el pool.", e.getMessage());
			}
			
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	/**
	 * Test for proper functioning of the pool with multiple operations.
	 */
	@Test
	@DisplayName("testPoolCycleOperations")
	public void testPoolCycleOperations() {
		// Get the pool instance
		ReusablePool pool = ReusablePool.getInstance();
		
		try {
			// Step 1: Acquire all available reusables
			Reusable r1 = pool.acquireReusable();
			Reusable r2 = pool.acquireReusable();
			
			// Step 2: Verify they are different objects
			assertNotNull(r1, "First reusable should not be null");
			assertNotNull(r2, "Second reusable should not be null");
			assertTrue(r1 != r2, "Should be different objects");
			
			// Step 3: Release them in reverse order
			pool.releaseReusable(r2);
			pool.releaseReusable(r1);
			
			// Step 4: Reacquire and verify the order (LIFO - Last In First Out)
			Reusable r3 = pool.acquireReusable();
			Reusable r4 = pool.acquireReusable();
			
			// The pool should return objects in LIFO order
			assertSame(r1, r3, "First released object should be first acquired");
			assertSame(r2, r4, "Second released object should be second acquired");
			
			// Clean up
			pool.releaseReusable(r3);
			pool.releaseReusable(r4);
			
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	/**
	 * Test method for Reusable.util() functionality.
	 */
	@Test
	@DisplayName("testReusableUtil")
	public void testReusableUtil() {
		// Create a reusable object
		Reusable reusable = new Reusable();
		
		// Get the util string
		String utilString = reusable.util();
		
		// Verify it contains the expected format
		assertTrue(utilString.contains(String.valueOf(reusable.hashCode())), 
				   "Util string should contain the object's hashcode");
		assertTrue(utilString.contains(":Uso del objeto Reutilizable"), 
				   "Util string should contain the expected message");
	}

	/**
	 * Test method for exception classes.
	 */
	@Test
	@DisplayName("testExceptionMessages")
	public void testExceptionMessages() {
		// Test NotFreeInstanceException
		NotFreeInstanceException notFreeEx = new NotFreeInstanceException();
		assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", 
					 notFreeEx.getMessage(), 
					 "NotFreeInstanceException should have correct message");
		
		// Test DuplicatedInstanceException
		DuplicatedInstanceException dupEx = new DuplicatedInstanceException();
		assertEquals("Ya existe esa instancia en el pool.", 
					 dupEx.getMessage(), 
					 "DuplicatedInstanceException should have correct message");
	}
}
