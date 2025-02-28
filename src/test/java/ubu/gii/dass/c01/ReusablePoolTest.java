/**
 * 
 */
package ubu.gii.dass.c01;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Vector;

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
	
	/**
	 * Helper method to reset the singleton instance between tests
	 * This ensures complete test isolation
	 */
	private void resetSingleton() throws Exception {
		Field instance = ReusablePool.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, null);
	}
	
	@BeforeEach
	public void setUp() throws Exception {
		// Reset the singleton to ensure tests are isolated
		resetSingleton();
		// Get a fresh instance
		pool = ReusablePool.getInstance();
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		// Reset the pool state and the singleton instance
		try {
			// First acquire any remaining objects to empty the pool
			while (true) {
				pool.acquireReusable();
			}
		} catch (NotFreeInstanceException e) {
			// Pool is empty, which is what we want
		}
		
		// Create two new Reusable objects
		Reusable r1 = new Reusable();
		Reusable r2 = new Reusable();
		
		// Release the objects back to the pool
		try {
			pool.releaseReusable(r1);
			pool.releaseReusable(r2);
		} catch (DuplicatedInstanceException e) {
			// Ignore if objects are already in the pool
		}
		
		// Reset the singleton for the next test
		resetSingleton();
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
		
		// Test that the singleton initializes with exactly 2 reusable objects
		try {
			// Access the private field 'reusables' to verify its size
			Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
			reusablesField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Vector<Reusable> reusables = (Vector<Reusable>) reusablesField.get(instance1);
			
			assertEquals(2, reusables.size(), "Pool should be initialized with 2 reusable objects");
			
			// Verify each object in the pool is a Reusable instance
			for (Object obj : reusables) {
				assertTrue(obj instanceof Reusable, "Each object in the pool should be a Reusable instance");
			}
		} catch (Exception e) {
			fail("Exception should not occur when accessing private fields: " + e.getMessage());
		}
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
	 * Tests acquiring objects from the pool successfully and when empty
	 */
	@Test
	@DisplayName("Test acquiring Reusable objects")
	public void testAcquireReusable() throws Exception {
		// Test acquiring all objects from the pool
		Reusable r1 = pool.acquireReusable();
		Reusable r2 = pool.acquireReusable();
		
		// Verify objects were acquired
		assertNotNull(r1, "First acquired object should not be null");
		assertNotNull(r2, "Second acquired object should not be null");
		assertNotEquals(r1, r2, "Different acquired objects should not be equal");
		
		// Test that the pool is now empty
		assertThrows(NotFreeInstanceException.class, () -> {
			pool.acquireReusable();
		}, "NotFreeInstanceException should be thrown when pool is empty");
		
		// Test the exception message
		try {
			pool.acquireReusable();
			fail("Should have thrown NotFreeInstanceException");
		} catch (NotFreeInstanceException e) {
			assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", 
					e.getMessage(), "NotFreeInstanceException should have the correct message");
		}
		
		// Release objects back to the pool
		pool.releaseReusable(r1);
		pool.releaseReusable(r2);
	}

	/**
	 * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
	 * Tests releasing objects back to the pool successfully and handling duplicated instances
	 */
	@Test
	@DisplayName("Test releasing Reusable objects")
	public void testReleaseReusable() throws Exception {
		// Acquire objects to work with
		Reusable r1 = pool.acquireReusable();
		Reusable r2 = pool.acquireReusable();
		
		// Test the pool is empty
		assertThrows(NotFreeInstanceException.class, () -> {
			pool.acquireReusable();
		}, "Pool should be empty after acquiring two objects");
		
		// Release an object back to the pool
		pool.releaseReusable(r1);
		
		// Test we can acquire it again
		Reusable r3 = pool.acquireReusable();
		assertNotNull(r3, "Should be able to acquire after releasing an object");
		
		// Test releasing the same object twice (should throw DuplicatedInstanceException)
		assertThrows(DuplicatedInstanceException.class, () -> {
			pool.releaseReusable(r1);
		}, "DuplicatedInstanceException should be thrown when releasing same object twice");
		
		// Test the exception message
		try {
			pool.releaseReusable(r1);
			fail("Should have thrown DuplicatedInstanceException");
		} catch (DuplicatedInstanceException e) {
			assertEquals("Ya existe esa instancia en el pool.", 
					e.getMessage(), "DuplicatedInstanceException should have the correct message");
		}
		
		// Clean up - release all objects back to the pool
		try {
			pool.releaseReusable(r2);
			pool.releaseReusable(r3);
		} catch (DuplicatedInstanceException e) {
			// Ignore
		}
	}
	
	/**
	 * Test method for {@link ubu.gii.dass.c01.Reusable#util()}.
	 * Tests the util method of the Reusable class
	 */
	@Test
	@DisplayName("Test Reusable util method")
	public void testReusableUtil() throws Exception {
		// Acquire an object to test
		Reusable r = pool.acquireReusable();
		
		// Test the util method
		String result = r.util();
		assertNotNull(result, "util method should not return null");
		assertTrue(result.contains("Uso del objeto Reutilizable"), 
				"util method should contain the expected message");
		assertTrue(result.contains(String.valueOf(r.hashCode())), 
				"util method should contain the object's hashCode");
		
		// Verify the exact format
		assertEquals(r.hashCode() + "  :Uso del objeto Reutilizable", result,
				"util method should return the hashCode followed by the message");
		
		// Clean up
		pool.releaseReusable(r);
	}
	
	/**
	 * Test method for exception classes
	 * Tests the creation and message content of both custom exceptions
	 */
	@Test
	@DisplayName("Test exception classes")
	public void testExceptions() {
		// Test NotFreeInstanceException
		NotFreeInstanceException nfie = new NotFreeInstanceException();
		assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", 
				nfie.getMessage(), "NotFreeInstanceException should have the correct message");
		
		// Test DuplicatedInstanceException
		DuplicatedInstanceException die = new DuplicatedInstanceException();
		assertEquals("Ya existe esa instancia en el pool.", 
				die.getMessage(), "DuplicatedInstanceException should have the correct message");
		
		// Test that the exceptions extend Exception
		assertTrue(nfie instanceof Exception, "NotFreeInstanceException should extend Exception");
		assertTrue(die instanceof Exception, "DuplicatedInstanceException should extend Exception");
	}
	
	/**
	 * Test for the complete workflow of acquire and release
	 * This test simulates a realistic usage scenario
	 */
	@Test
	@DisplayName("Test complete workflow")
	public void testCompleteWorkflow() throws Exception {
		// 1. Acquire first object
		Reusable r1 = pool.acquireReusable();
		assertNotNull(r1, "Should be able to acquire first object");
		
		// 2. Acquire second object
		Reusable r2 = pool.acquireReusable();
		assertNotNull(r2, "Should be able to acquire second object");
		
		// 3. Try to acquire when pool is empty (should fail)
		assertThrows(NotFreeInstanceException.class, () -> {
			pool.acquireReusable();
		}, "Should throw exception when pool is empty");
		
		// 4. Release the second object
		pool.releaseReusable(r2);
		
		// 5. Acquire an object (should work)
		Reusable r3 = pool.acquireReusable();
		assertNotNull(r3, "Should be able to acquire after releasing one");
		assertSame(r2, r3, "Should get the same object that was just released");
		
		// 6. Try to release the same object twice (should fail)
		assertThrows(DuplicatedInstanceException.class, () -> {
			pool.releaseReusable(r2);
		}, "Should throw exception when releasing same object twice");
		
		// Clean up
		pool.releaseReusable(r1);
		pool.releaseReusable(r3);
	}
	
	/**
	 * Test the Client main method execution
	 * This test ensures the main Client class works correctly
	 */
	@Test
	@DisplayName("Test Client main method")
	public void testClientMain() {
		try {
			// The client.main() method should not throw any exceptions
			Client.main(new String[]{});
			// If we reach here, the test passes (no exceptions were thrown)
		} catch (Exception e) {
			fail("Client.main() threw an unexpected exception: " + e.getMessage());
		}
	}
}