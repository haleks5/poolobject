/**
 * 
 */
package ubu.gii.dass.c01;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;



/**
 * @author Sofía Calavia
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
	 */
	/**
 	* @author Andres Puentes
	 *
 	*/
	@Test
   	@DisplayName("testReleaseReusable")
   	public void testReleaseReusable() {
        	Reusable reusable = pool.acquireReusable();
        	assertNotNull(reusable, "El objeto adquirido no debe ser nulo");

       	 	pool.releaseReusable(reusable);
        
        	// Comprobar que el objeto está disponible después de la liberación
        	assertTrue(pool.isAvailable(reusable), "El objeto debería estar disponible después de ser liberado");
    	}	

}
