package nz.ac.aut.ense701.main;


import nz.ac.aut.ense701.gameModel.Game;
import nz.ac.aut.ense701.gui.MainMenu;

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
        final MainMenu menu = new MainMenu(game);
         
        // create the GUI for the game
        // final KiwiCountUI gui = new KiwiCountUI(game);
        // make the GUI visible
        
            java.awt.EventQueue.invokeLater(new Runnable() 
        {
           
            public void run() 
            {
               menu.setVisible(true);
              
            }

         
        }
            );
     
    
    }


}
