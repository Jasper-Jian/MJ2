/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;

import java.util.Random;

/**
 *
 * @author Administrator
 */
public class Weather {
    public String creatWeather(){
        Random rand = new Random();
        int weatherChange = rand.nextInt(4);
        String weatherStr = null;
        switch(weatherChange){
            case 0:
                weatherStr = "Sunny";
            break;
            case 1:
                weatherStr = "Rainy";
            break;
            case 2:
                weatherStr = "Windy";
            break;
            case 3:
                weatherStr = "Stormy";
            break;
            default:
                weatherStr = "Unkown";
            break;
        }
        return weatherStr;
    }
}
