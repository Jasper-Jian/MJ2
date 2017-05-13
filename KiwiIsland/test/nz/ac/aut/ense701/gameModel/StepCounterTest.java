/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class StepCounterTest {
    
    public StepCounterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getSingleTon method, of class StepCounter.
     */
    @Test
    public void testGetSingleTon() {
        System.out.println("getSingleTon");
        StepCounter expResult = null;
        StepCounter result = StepCounter.getSingleTon();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStep method, of class StepCounter.
     */
    @Test
    public void testSetStep() {
        System.out.println("setStep");
        int step = 0;
        StepCounter instance = null;
        instance.setStep(step);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStep method, of class StepCounter.
     */
    @Test
    public void testGetStep() {
        System.out.println("getStep");
        StepCounter instance = null;
        int expResult = 0;
        int result = instance.getStep();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addStep method, of class StepCounter.
     */
    @Test
    public void testAddStep() {
        System.out.println("addStep");
        StepCounter instance = null;
        instance.addStep();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class StepCounter.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        StepCounter instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class StepCounter.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        StepCounter instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
