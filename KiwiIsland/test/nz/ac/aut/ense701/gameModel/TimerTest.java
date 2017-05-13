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
public class TimerTest {
    
    public TimerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getProgramStart method, of class Timer.
     */
    @Test
    public void testGetProgramStart() {
        System.out.println("getProgramStart");
        Timer instance = null;
        long expResult = 0L;
        long result = instance.getProgramStart();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProgramStart method, of class Timer.
     */
    @Test
    public void testSetProgramStart() {
        System.out.println("setProgramStart");
        long programStart = 0L;
        Timer instance = null;
        instance.setProgramStart(programStart);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class Timer.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        Timer instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
