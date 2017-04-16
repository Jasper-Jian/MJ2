
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;

import java.util.Random;

/**
 * Weather Class
 * @author Minghao Yang
 * @version 1
 * Using the singleton pattern to design the class;
 * Whenever the player moves 4 steps the weather will change randomly.
 * Different weather will return different difficulty.
 */
public class Weather {
    private static final Weather WEATHER = new Weather();
    private double difficulty;
    private String weatherStr = "Sunny";
    private StepCounter stepCounter = StepCounter.getSingleTon();
    private Weather(){
    }
    public static Weather getSingleTon(){
        return WEATHER;
    }
   /**
     * Get the difficulty of the changed weather
     * @return 
     */
    public double getDifficulty() {
		return difficulty;
	}
   /**
     * Get the Weather String
     * @return 
     */
	public String getWeatherStr() {
		return weatherStr;
	}
       /**
         * Set the weather
         * @param weatherStr 
         */
	public void setWeatherStr(String weatherStr) {
		this.weatherStr = weatherStr;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(difficulty);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((stepCounter == null) ? 0 : stepCounter.hashCode());
		result = prime * result + ((weatherStr == null) ? 0 : weatherStr.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Weather other = (Weather) obj;
		if (Double.doubleToLongBits(difficulty) != Double.doubleToLongBits(other.difficulty))
			return false;
		if (stepCounter == null) {
			if (other.stepCounter != null)
				return false;
		} else if (!stepCounter.equals(other.stepCounter))
			return false;
		if (weatherStr == null) {
			if (other.weatherStr != null)
				return false;
		} else if (!weatherStr.equals(other.weatherStr))
			return false;
		return true;
	}
	/**
         * Whenver the player moves 4 steps, the weather will change and get
         * the difficulty according to the weather.
         */
	public void getWeatherChageStr(){
        if(stepCounter.getStep()%4==0){
            this.creatWeather();
            this.setDifficulty();
        }
    }
    
    public void setDifficulty(){
        if("Sunny".equals(this.weatherStr)){
            this.difficulty = 0.0;
        }else if("Windy".equals(this.weatherStr)){
            this.difficulty = 1.0;
        }else if("Snowy".equals(this.weatherStr)){
            this.difficulty = 2.5;
        }else if("Stormy".equals(this.weatherStr)){
            this.difficulty = 3.0;
        }
    }
    /**
     * Set different difficulty to different weather
     */
    public void creatWeather(){
        Random rand = new Random();
        int weatherChange = rand.nextInt(4);
        switch(weatherChange){
            case 0:
                this.weatherStr = "Sunny";
            break;
            case 1:
                this.weatherStr = "Rainy";
            break;
            case 2:
                this.weatherStr = "Windy";
            break;
            case 3:
                this.weatherStr = "Stormy";
            break;
            default:
                this.weatherStr = "Unkown";
            break;
        }
      
    }
}
