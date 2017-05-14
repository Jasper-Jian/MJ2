/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nz.ac.aut.ense701.gameModel;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.ac.aut.ense701.gui.KiwiCountUI;

/**
 *
 * @author Administrator
 */
public class Timer extends Thread{
    public KiwiCountUI kiwiCountUI;
    private long programStart;
    
    
    public Timer(KiwiCountUI kiwiCountUI) {
        this.kiwiCountUI = kiwiCountUI;
        this.programStart =  System.currentTimeMillis();
    }
   
    public long getProgramStart() {
        return programStart;
    }

    public void setProgramStart(long programStart) {
        this.programStart = programStart;
    }
    
    private String format(long timeInput){
        int minute,second,milli;
        milli = (int)(timeInput % 1000);
        timeInput  = timeInput / 1000;
        second = (int)(timeInput%60);
        timeInput = timeInput/60;
        minute = (int)(timeInput%60);
        return String.format("%02dm:%02ds",minute,second);
        
    }
   
    
    @Override
    public void run() {
        while (true) {  
            if(this.kiwiCountUI.game.getState()!=GameState.LOST || this.kiwiCountUI.game.getState()!=GameState.WON ){
                //check whether the player is alive
                if(this.kiwiCountUI.game.getPlayer().isAlive()){
                 this.kiwiCountUI.setTime(format(System.currentTimeMillis()-programStart));
                }else{
                this.kiwiCountUI.setTime(format(0));
                } 
            }
            try {
            sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
       }
      
    }
    
}
