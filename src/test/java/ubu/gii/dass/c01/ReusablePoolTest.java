/**
 * 
 */
package ubu.gii.dass.c01;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Test cases for the ReusablePool class
 * @author Andrés Puentes
 * @author Mario Cea
 * @author Alejandro García
 * @author Sofía Calavia
 */
public class ReusablePoolTest {
	
	private ReusablePool pool;
	
	@BeforeEach
	public void setUp() {
		// Get the singleton instance for use in tests
		pool = ReusablePool.getInstance();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		// We need to try to restore the pool to its initial state
		try {
			// First acquire any remaining objects
			while (true) {
				pool.acquireReusable();
			}
		} catch (NotFreeInstanceException e) {
			// Pool is empty, which is what we want
		}
		
		// Now release two objects to restore the pool to its initial state
		Reusable r1 = new Reusable();
		Reusable r2 = new Reusable();
		
		try {
			pool.releaseReusable(r1);
			pool.releaseReusable(r2);
		} catch (DuplicatedInstanceException e) {
			// Ignore if objects are already in the pool
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
	public void testAcquireReusable() throws Exception {
		// First make sure we can acquire both objects
		Reusable r1 = null;
		Reusable r2 = null;
		
		try {
			r1 = pool.acquireReusable();
			assertNotNull(r1, "First acquired object should not be null");
			
			r2 = pool.acquireReusable();
			assertNotNull(r2, "Second acquired object should not be null");
			
			// Verify they are different objects
			assertNotEquals(r1, r2, "Different acquired objects should not be equal");
			
			// Verify they have different hashcodes
			assertNotEquals(r1.hashCode(), r2.hashCode(), "Different acquired objects should have different hashcodes");
			
			// Test that acquiring a third object throws NotFreeInstanceException
			assertThrows(NotFreeInstanceException.class, () -> {
				pool.acquireReusable();
			}, "NotFreeInstanceException should be thrown when pool is empty");
		} finally {
			// Clean up - release the objects back to the pool
			if (r1 != null) {
				pool.releaseReusable(r1);
			}
			if (r2 != null) {
				pool.releaseReusable(r2);
			}
		}
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 * Tests releasing objects back to the pool successfully and handling duplicated instances
	 */
	@Test
	@DisplayName("Test releasing Reusable objects and handling duplicates")
	public void testReleaseReusable() throws Exception {
		// First acquire an object
		Reusable r = pool.acquireReusable();
		
		// Release it back
		pool.releaseReusable(r);
		
		// Verify that DuplicatedInstanceException is thrown when releasing the same object again
		assertThrows(DuplicatedInstanceException.class, () -> {
			pool.releaseReusable(r);
		}, "DuplicatedInstanceException should be thrown when releasing same object twice");
		
		// Now test that we can acquire and release multiple objects
		Reusable r1 = pool.acquireReusable();
		Reusable r2 = pool.acquireReusable();
		
		// Verify both objects were acquired
		assertNotNull(r1, "Should be able to acquire first object");
		assertNotNull(r2, "Should be able to acquire second object");
		
		// Release them back
		pool.releaseReusable(r1);
		pool.releaseReusable(r2);
		
		// Verify we can acquire them again
		r1 = pool.acquireReusable();
		r2 = pool.acquireReusable();
		
		assertNotNull(r1, "Should be able to acquire first object again");
		assertNotNull(r2, "Should be able to acquire second object again");
		
		// Clean up
		pool.releaseReusable(r1);
		pool.releaseReusable(r2);
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
		
		try {
			// Test the util method
			String result = r.util();
			assertNotNull(result, "util method should not return null");
			assertTrue(result.contains("Uso del objeto Reutilizable"), 
					"util method should return expected message");
			assertTrue(result.contains(Integer.toString(r.hashCode())), 
					"util method should include the hashCode");
		} finally {
			// Clean up
			pool.releaseReusable(r);
		}
	}
}