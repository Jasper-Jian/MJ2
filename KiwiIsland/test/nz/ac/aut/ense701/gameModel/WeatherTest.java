/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class WeatherTest {
    
    public WeatherTest() {
    } 
    //Remain Sunny at the first status
    @BeforeClass
    public static void setUpClass() {        
        System.out.println("Testing the weather at the start");         
        String expResult = "Sunny";        
        String result = Weather.getSingleTon().getWeatherStr();
        System.out.println(result);
        assertEquals(expResult, result);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testWeatherChangeAfter4Steps(){      
      System.out.println("Testing the weather after 4 steps");      
      StepCounter step = StepCounter.getSingleTon();      
      step.setStep(4);
      if(step.getStep()==4){
          Weather.getSingleTon().creatWeather();
      }      
      boolean result = false;
      String checkStatus = Weather.getSingleTon().getWeatherStr();
      if(checkStatus == "Sunny"){
          result = true;
      }else{
          result = false;
      }      
      System.out.println(result);
      assertFalse("true", result);
    }
}
