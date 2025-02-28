package ubu.gii.dass.c01;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReusablePoolTest {
    private static final int INSTANCE_COUNT = 2;

    @AfterEach
    public void cleanUp() {
        ReusablePool pool = ReusablePool.getInstance();
        System.out.println("Initial pool state: " + pool);
        try {
            while (true) {
                pool.acquireReusable();
            }
        } catch (NotFreeInstanceException e) {
            try {
                System.out.println("Middle pool state: " + pool);

                for (int i = 0; i < INSTANCE_COUNT; i++) {
                    pool.releaseReusable(new Reusable());
                }
            } catch (Exception ignored) {
            }
        }
        System.out.println("Final pool state: " + pool);
    }

    @AfterAll
    public static void tearDown() {
        // No operation
    }

    @Test
    @DisplayName("Test Singleton Instance")
    public void testGetInstance() {
        ReusablePool pool1 = ReusablePool.getInstance();
        ReusablePool pool2 = ReusablePool.getInstance();
        assertNotNull(pool1);
        assertNotNull(pool2);
        assertSame(pool1, pool2);
        assertEquals(pool1, pool2);
    }

    @Test
    @DisplayName("Test Acquire Reusable")
    public void testAcquireReusable() {
        ReusablePool pool = ReusablePool.getInstance();
        System.out.println(pool);
        try {
            for (int i = 0; i < INSTANCE_COUNT; i++) {
                pool.acquireReusable();
            }
        } catch (NotFreeInstanceException e) {
            fail("Exception should not be thrown");
        }
        assertThrows(NotFreeInstanceException.class, pool::acquireReusable);
    }

    @Test
    @DisplayName("Test Release Reusable")
    public void testReleaseReusable() {
        ReusablePool pool = ReusablePool.getInstance();
        Reusable reusable = null;
        try {
            reusable = pool.acquireReusable();
        } catch (NotFreeInstanceException e) {
            fail("Exception should not be thrown");
        }
        try {
            pool.releaseReusable(reusable);
        } catch (DuplicatedInstanceException e) {
            fail("Exception should not be thrown");
        }
        Reusable finalReusable = reusable;
        assertThrows(DuplicatedInstanceException.class, () -> pool.releaseReusable(finalReusable));
    }

    @Test
    @DisplayName("Test Reusable Util")
    public void testReusableUtil() {
        assertNotEquals((new Reusable()).util(), (new Reusable()).util());
    }

    @Test
    @DisplayName("Test Client Main")
    public void testClient() {
        assertNotNull(new Client());
        assertDoesNotThrow(() -> Client.main(null));
    }
}
