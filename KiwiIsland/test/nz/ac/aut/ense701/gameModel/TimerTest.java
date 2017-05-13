/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;
import static junit.framework.TestCase.assertFalse;
import nz.ac.aut.ense701.gui.KiwiCountUI;
import nz.ac.aut.ense701.gameModel.Timer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class TimerTest {
    
    KiwiCountUI kiwiCountUI;
    Timer timer = new Timer(kiwiCountUI);
    public TimerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {  
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * If the game has not start the time will remain the same
     */
    @Test
    public void testGetProgramStart() {
        System.out.println("Test Time when game started");  
        long first, second;
        first= timer.getProgramStart();    
        second = timer.getProgramStart();
        System.out.println(first+"            "+second);   
        assertEquals(first, second);
    }    
    
    @Test
    public void testTimeDifferent() {
        System.out.println("Test Time after game started");  
        long CurrentTime = timer.getProgramStart();   
        long AfterGameStart = System.currentTimeMillis();       
        System.out.println(CurrentTime+"            "+AfterGameStart);        
        assertNotEquals(CurrentTime, AfterGameStart);
    }

    
}
