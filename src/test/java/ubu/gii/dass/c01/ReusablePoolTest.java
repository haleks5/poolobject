/**
 * 
 */
package ubu.gii.dass.c01;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.Vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    
    private static ReusablePool originalInstance;
    
    /**
     * Save the original instance and reset for clean testing environment
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Save the original instance
        originalInstance = ReusablePool.getInstance();
        
        // Reset the instance to null using reflection
        Field instanceField = ReusablePool.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
    
    /**
     * Restore the original instance after each test
     */
    @AfterEach
    public void tearDown() throws Exception {
        // Restore the original instance using reflection
        Field instanceField = ReusablePool.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, originalInstance);
    }
    
    /**
     * Test method for {@link ubu.gii.dass.c01.ReusablePool#getInstance()}.
     */
    @Test
    @DisplayName("testGetInstance")
    public void testGetInstance() {
        // Test getInstance creates a new instance when null
        ReusablePool instance1 = ReusablePool.getInstance();
        assertNotNull(instance1, "La instancia adquirida no debe ser nula");
        
        // Test getInstance returns the same instance when called again
        ReusablePool instance2 = ReusablePool.getInstance();
        assertSame(instance1, instance2, "getInstance debe devolver la misma instancia");
        
        // Additional verification of internal state
        try {
            // Use reflection to verify the pool size is 2
            Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
            reusablesField.setAccessible(true);
            Vector<?> reusables = (Vector<?>) reusablesField.get(instance1);
            assertEquals(2, reusables.size(), "El tamaño inicial del pool debe ser 2");
        } catch (Exception e) {
            fail("Error accessing private fields: " + e.getMessage());
        }
    }
    
    /**
     * Test method for {@link ubu.gii.dass.c01.ReusablePool#acquireReusable()}.
     */
    @Test
    @DisplayName("testAcquireReusable")
    public void testAcquireReusable() {
        // Get a fresh instance
        ReusablePool pool = ReusablePool.getInstance();
        
        try {
            // Acquire first reusable
            Reusable r1 = pool.acquireReusable();
            assertNotNull(r1, "El objeto reusable adquirido no debería ser nulo");
            assertTrue(r1 instanceof Reusable, "El objeto adquirido debe ser una instancia de Reusable");
            
            // Verify the pool size decreased
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(1, reusables.size(), "El tamaño del pool debe ser 1 después de adquirir un objeto");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Acquire second reusable
            Reusable r2 = pool.acquireReusable();
            assertNotNull(r2, "El segundo objeto reusable adquirido no debería ser nulo");
            assertTrue(r1 != r2, "Los objetos adquiridos deben ser diferentes");
            
            // Verify pool is now empty
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(0, reusables.size(), "El pool debe estar vacío después de adquirir ambos objetos");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Clean up
            pool.releaseReusable(r1);
            pool.releaseReusable(r2);
            
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * Test method for {@link ubu.gii.dass.c01.ReusablePool#releaseReusable(ubu.gii.dass.c01.Reusable)}.
     */
    @Test
    @DisplayName("testReleaseReusable")
    public void testReleaseReusable() {
        // Get a fresh instance
        ReusablePool pool = ReusablePool.getInstance();
        
        try {
            // Empty the pool first
            Reusable originalR1 = pool.acquireReusable();
            Reusable originalR2 = pool.acquireReusable();
            
            // Create a new reusable object
            Reusable newReusable = new Reusable();
            
            // Release the new object to the pool
            pool.releaseReusable(newReusable);
            
            // Verify pool size increased
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(1, reusables.size(), "El tamaño del pool debe ser 1 después de liberar un objeto");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Acquire the object back
            Reusable acquiredReusable = pool.acquireReusable();
            
            // Verify it's the same object we released
            assertSame(newReusable, acquiredReusable, "El objeto adquirido debe ser el mismo que liberamos");
            
            // Clean up
            pool.releaseReusable(originalR1);
            pool.releaseReusable(originalR2);
            
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * Test method for exception when no free instances are available.
     */
    @Test
    @DisplayName("testAcquireReusableWhenNoFreeInstances")
    public void testAcquireReusableWhenNoFreeInstances() {
        // Get a fresh instance
        ReusablePool pool = ReusablePool.getInstance();
        
        try {
            // Acquire all available reusables
            Reusable r1 = pool.acquireReusable();
            Reusable r2 = pool.acquireReusable();
            
            // Verify both objects were acquired
            assertNotNull(r1, "First reusable should not be null");
            assertNotNull(r2, "Second reusable should not be null");
            
            // Check pool is empty
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(0, reusables.size(), "El pool debe estar vacío");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Try to acquire a third reusable
            try {
                pool.acquireReusable();
                fail("Should have thrown NotFreeInstanceException");
            } catch (NotFreeInstanceException e) {
                // Expected exception
                assertEquals("No hay más instancias reutilizables disponibles. Reintentalo más tarde", e.getMessage());
            }
            
            // Clean up
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
        // Get a fresh instance
        ReusablePool pool = ReusablePool.getInstance();
        
        try {
            // Acquire a reusable
            Reusable reusable = pool.acquireReusable();
            
            // Release it back to the pool
            pool.releaseReusable(reusable);
            
            // Verify the reusable is in the pool
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertTrue(reusables.contains(reusable), "El pool debe contener el objeto liberado");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Try to release it again
            try {
                pool.releaseReusable(reusable);
                fail("Should have thrown DuplicatedInstanceException");
            } catch (DuplicatedInstanceException e) {
                // Expected exception
                assertEquals("Ya existe esa instancia en el pool.", e.getMessage());
            }
            
            // Reacquire to clean up
            pool.acquireReusable();
            
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * Test for complete pool lifecycle with multiple operations.
     */
    @Test
    @DisplayName("testCompletePoolLifecycle")
    public void testCompletePoolLifecycle() {
        // Get a fresh instance
        ReusablePool pool = ReusablePool.getInstance();
        
        try {
            // Step 1: Check initial state
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(2, reusables.size(), "El tamaño inicial del pool debe ser 2");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Step 2: Acquire objects
            Reusable r1 = pool.acquireReusable();
            Reusable r2 = pool.acquireReusable();
            
            // Step 3: Pool should be empty
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(0, reusables.size(), "El pool debe estar vacío después de adquirir ambos objetos");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Step 4: Create a new object and release it
            Reusable r3 = new Reusable();
            pool.releaseReusable(r3);
            
            // Step 5: Release one of the original objects
            pool.releaseReusable(r1);
            
            // Step 6: Pool should have 2 objects now
            try {
                Field reusablesField = ReusablePool.class.getDeclaredField("reusables");
                reusablesField.setAccessible(true);
                Vector<?> reusables = (Vector<?>) reusablesField.get(pool);
                assertEquals(2, reusables.size(), "El pool debe tener 2 objetos");
                assertTrue(reusables.contains(r1), "El pool debe contener r1");
                assertTrue(reusables.contains(r3), "El pool debe contener r3");
            } catch (Exception e) {
                fail("Error accessing private fields: " + e.getMessage());
            }
            
            // Step 7: Reacquire objects and verify LIFO order
            Reusable rA = pool.acquireReusable();
            Reusable rB = pool.acquireReusable();
            
            // The last object released should be the first acquired (LIFO)
            assertSame(r1, rA, "El primer objeto adquirido debe ser r1 (último liberado)");
            assertSame(r3, rB, "El segundo objeto adquirido debe ser r3 (liberado antes que r1)");
            
            // Clean up
            pool.releaseReusable(r2);
            pool.releaseReusable(rA);
            pool.releaseReusable(rB);
            
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
        
        // Verify it matches the expected format exactly
        String expected = reusable.hashCode() + "  :Uso del objeto Reutilizable";
        assertEquals(expected, utilString, "El método util() debe devolver el formato exacto esperado");
    }
    
    /**
     * Test method for serialVersionUID in NotFreeInstanceException.
     */
    @Test
    @DisplayName("testNotFreeInstanceExceptionSerialVersionUID")
    public void testNotFreeInstanceExceptionSerialVersionUID() {
        try {
            Field serialVersionUIDField = NotFreeInstanceException.class.getDeclaredField("serialVersionUID");
            serialVersionUIDField.setAccessible(true);
            long serialVersionUID = serialVersionUIDField.getLong(null);
            assertEquals(1L, serialVersionUID, "serialVersionUID debe ser 1L");
        } catch (Exception e) {
            fail("Error accessing serialVersionUID: " + e.getMessage());
        }
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
                    "NotFreeInstanceException debe tener el mensaje correcto");
        
        // Test DuplicatedInstanceException
        DuplicatedInstanceException dupEx = new DuplicatedInstanceException();
        assertEquals("Ya existe esa instancia en el pool.", 
                    dupEx.getMessage(), 
                    "DuplicatedInstanceException debe tener el mensaje correcto");
    }
    
    /**
     * Test creating additional instances of exceptions.
     */
    @Test
    @DisplayName("testCreateExceptionInstances")
    public void testCreateExceptionInstances() {
        // Create multiple instances to ensure constructors are fully covered
        NotFreeInstanceException notFreeEx1 = new NotFreeInstanceException();
        NotFreeInstanceException notFreeEx2 = new NotFreeInstanceException();
        
        DuplicatedInstanceException dupEx1 = new DuplicatedInstanceException();
        DuplicatedInstanceException dupEx2 = new DuplicatedInstanceException();
        
        // Verify they're different instances but with same messages
        assertTrue(notFreeEx1 != notFreeEx2, "Deben ser instancias diferentes");
        assertEquals(notFreeEx1.getMessage(), notFreeEx2.getMessage(), "Los mensajes deben ser iguales");
        
        assertTrue(dupEx1 != dupEx2, "Deben ser instancias diferentes");
        assertEquals(dupEx1.getMessage(), dupEx2.getMessage(), "Los mensajes deben ser iguales");
    }
}