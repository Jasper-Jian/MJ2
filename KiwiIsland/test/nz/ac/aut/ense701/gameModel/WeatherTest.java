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
public class WeatherTest {
    
    public WeatherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getSingleTon method, of class Weather.
     */
    @Test
    public void testGetSingleTon() {
        System.out.println("getSingleTon");
        Weather expResult = null;
        Weather result = Weather.getSingleTon();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDifficulty method, of class Weather.
     */
    @Test
    public void testGetDifficulty() {
        System.out.println("getDifficulty");
        Weather instance = null;
        double expResult = 0.0;
        double result = instance.getDifficulty();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWeatherStr method, of class Weather.
     */
    @Test
    public void testGetWeatherStr() {
        System.out.println("getWeatherStr");
        Weather instance = null;
        String expResult = "";
        String result = instance.getWeatherStr();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWeatherStr method, of class Weather.
     */
    @Test
    public void testSetWeatherStr() {
        System.out.println("setWeatherStr");
        String weatherStr = "";
        Weather instance = null;
        instance.setWeatherStr(weatherStr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Weather.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Weather instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Weather.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        Weather instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWeatherChageStr method, of class Weather.
     */
    @Test
    public void testGetWeatherChageStr() {
        System.out.println("getWeatherChageStr");
        Weather instance = null;
        instance.getWeatherChageStr();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDifficulty method, of class Weather.
     */
    @Test
    public void testSetDifficulty() {
        System.out.println("setDifficulty");
        Weather instance = null;
        instance.setDifficulty();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of creatWeather method, of class Weather.
     */
    @Test
    public void testCreatWeather() {
        System.out.println("creatWeather");
        Weather instance = null;
        instance.creatWeather();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
