/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;
import java.util.Random;
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
        System.out.println("At the begining of the Game");
        int expResult = 0;
        int result = StepCounter.getSingleTon().getStep();
        assertEquals(expResult, result);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of player moved one step
     */
    @Test
    public void testAfterMoveOneStep() {
        System.out.println("After Move one Step");
        int expResult = 1;
        StepCounter.getSingleTon().addStep();
        int result = StepCounter.getSingleTon().getStep();
        assertEquals(expResult, result);
    }
     /**
     * Test of player moved N step
     */
    @Test
    public void testAfterMoveNSteps() {
        Random rand = new Random();
        System.out.println("After Move N Steps");
        int expResult = rand.nextInt(100);
        for(int a = 1; a < expResult; a++){
            StepCounter.getSingleTon().addStep();
        }        
        int result = StepCounter.getSingleTon().getStep();
        System.out.println("Steps Moved: "+result);
        assertEquals(expResult, result);
    }

    
}
