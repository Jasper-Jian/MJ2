package nz.ac.aut.ense701.main;


import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.*;

import java.awt.event.WindowEvent;
import nz.ac.aut.ense701.gameModel.Game;
import nz.ac.aut.ense701.gui.KiwiCountUI;
import nz.ac.aut.ense701.gui.Instruction;

import nz.ac.aut.ense701.gameModel.Game;
 import nz.ac.aut.ense701.gui.KiwiCountUI;
import nz.ac.aut.ense701.gameModel.GameEventListener;
/**
 * Kiwi Count Project
 * 
 * @author AS
 * @version 2011
 */
public class Main 
{
   
   
    public static void main(String[] args) 
    {
  
        // create the game object
        final Game game = new Game();
         final Instruction gui = new Instruction(game);
         
        // create the GUI for the game
        // final KiwiCountUI gui = new KiwiCountUI(game);
        // make the GUI visible
        
            java.awt.EventQueue.invokeLater(new Runnable() 
        {
           
            public void run() 
            {
               
                gui.setVisible(true);
              
            }

         
        }
            );
     
    
    }


}
