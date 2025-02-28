/**
 * 
 */
package ubu.gii.dass.c01;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Test cases for the ReusablePool class
 * @author Claude
 *
 */
public class ReusablePoolTest {
	
	private static ReusablePool pool;
	
	@BeforeAll
	public static void setUp() {
		// Get the singleton instance for use in tests
		pool = ReusablePool.getInstance();
	}
	
	@AfterAll
	public static void tearDown() throws Exception {
		// Release all objects back to the pool to reset its state
		try {
			// We need to reacquire and release objects to ensure the pool is in a known state
			Reusable r1 = pool.acquireReusable();
			Reusable r2 = pool.acquireReusable();
			
			pool.releaseReusable(r1);
			pool.releaseReusable(r2);
		} catch (Exception e) {
			// If an exception occurs, the pool may already be in a good state
		}
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#getInstance()}.
	 * Tests that getInstance always returns the same instance (singleton pattern)
	 */
	@Test
	@DisplayName("Test getInstance returns the same instance")
	public void testGetInstance() {
		// Get two references to the ReusablePool
		ReusablePool instance1 = ReusablePool.getInstance();
		ReusablePool instance2 = ReusablePool.getInstance();
		
		// Verify they are the same object
		assertSame(instance1, instance2, "getInstance should always return the same instance");
		assertNotNull(instance1, "getInstance should not return null");
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	 * Tests acquiring objects from the pool successfully
	 */
	@Test
	@DisplayName("Test acquiring Reusable objects successfully")
	public void testAcquireReusableSuccess() throws Exception {
		// First, ensure the pool has all objects available
		try {
			pool.releaseReusable(pool.acquireReusable());
			pool.releaseReusable(pool.acquireReusable());
		} catch (Exception e) {
			// If an exception occurs, the pool may already be in a good state
		}
		
		// Now acquire two objects
		Reusable r1 = pool.acquireReusable();
		Reusable r2 = pool.acquireReusable();
		
		// Verify the objects are not null and are different
		assertNotNull(r1, "First acquired object should not be null");
		assertNotNull(r2, "Second acquired object should not be null");
		assertNotEquals(r1, r2, "Different acquired objects should not be equal");
		
		// Clean up
		pool.releaseReusable(r1);
		pool.releaseReusable(r2);
	}
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	 * Tests that NotFreeInstanceException is thrown when pool is empty
	 */
	@Test
	@DisplayName("Test NotFreeInstanceException when pool is empty")
	public void testAcquireReusableWhenEmpty() throws Exception {
		// First, ensure the pool has all objects available
		try {
			pool.releaseReusable(pool.acquireReusable());
			pool.releaseReusable(pool.acquireReusable());
		} catch (Exception e) {
			// If an exception occurs, the pool may already be in a good state
		}
		
		// Acquire all objects to empty the pool
		Reusable r1 = pool.acquireReusable();
		Reusable r2 = pool.acquireReusable();
		
		// Verify that NotFreeInstanceException is thrown when trying to acquire from empty pool
		assertThrows(NotFreeInstanceException.class, () -> {
			pool.acquireReusable();
		}, "NotFreeInstanceException should be thrown when pool is empty");
		
		// Clean up
		pool.releaseReusable(r1);
		pool.releaseReusable(r2);
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 * Tests releasing objects back to the pool successfully
	 */
	@Test
	@DisplayName("Test releasing Reusable objects successfully")
	public void testReleaseReusableSuccess() throws Exception {
		// Acquire an object
		Reusable r = pool.acquireReusable();
		
		// Release it back
		pool.releaseReusable(r);
		
		// Verify we can acquire it again
		Reusable r2 = pool.acquireReusable();
		assertNotNull(r2, "Should be able to acquire an object after releasing");
		
		// Clean up
		pool.releaseReusable(r2);
	}
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 * Tests that DuplicatedInstanceException is thrown when releasing the same object twice
	 */
	@Test
	@DisplayName("Test DuplicatedInstanceException when releasing same object twice")
	public void testReleaseReusableDuplicated() throws Exception {
		// Acquire an object
		Reusable r = pool.acquireReusable();
		
		// Release it back
		pool.releaseReusable(r);
		
		// Verify that DuplicatedInstanceException is thrown when releasing the same object again
		assertThrows(DuplicatedInstanceException.class, () -> {
			pool.releaseReusable(r);
		}, "DuplicatedInstanceException should be thrown when releasing same object twice");
		
		// Clean up - reacquire the object
		Reusable r2 = pool.acquireReusable();
		// Don't need to release as the test is complete
	}
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.Reusable#util()}.
	 * Tests the util method of the Reusable class
	 */
	@Test
	@DisplayName("Test Reusable util method")
	public void testReusableUtil() throws Exception {
		// Acquire an object
		Reusable r = pool.acquireReusable();
		
		// Test the util method
		String result = r.util();
		assertNotNull(result, "util method should not return null");
		assertTrue(result.contains("Uso del objeto Reutilizable"), 
				"util method should return expected message");
		assertTrue(result.contains(Integer.toString(r.hashCode())), 
				"util method should include the hashCode");
		
		// Clean up
		pool.releaseReusable(r);
	}
	
	/**
	 * Test method for exception message validation
	 */
	@Test
	@DisplayName("Test exception messages")
	public void testExceptionMessages() {
		// Test NotFreeInstanceException message
		NotFreeInstanceException notFreeEx = new NotFreeInstanceException();
		assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", 
				notFreeEx.getMessage(), "NotFreeInstanceException message is incorrect");
		
		// Test DuplicatedInstanceException message
		DuplicatedInstanceException dupEx = new DuplicatedInstanceException();
		assertEquals("Ya existe esa instancia en el pool.", 
				dupEx.getMessage(), "DuplicatedInstanceException message is incorrect");
	}
}