package nz.ac.aut.ense701.gameModel;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This is the class that knows the Kiwi Island game rules and state
 * and enforces those rules.
 *
 * @author AS
 * @version 1.0 - created
 * Maintenance History
 * August 2011 Extended for stage 2. AS
 */

public class Game
{
    //Constants shared with UI to provide player data
    public static final int STAMINA_INDEX = 0;
    public static final int MAXSTAMINA_INDEX = 1;
    public static final int MAXWEIGHT_INDEX = 2;
    public static final int WEIGHT_INDEX = 3;
    public static final int MAXSIZE_INDEX = 4;
    public static final int SIZE_INDEX = 5;
    
    /**
     * A new instance of Kiwi island that reads data from "IslandData.txt".
     */
    public Game() 
    {   
        eventListeners = new HashSet<GameEventListener>();
        this.autoConnectKiwiIslandDB();
        createNewGame();
    }
    
    
    /**
     * Starts a new game.
     * At this stage data is being read from a text file
     */
    public void createNewGame()
    {
        totalPredators = 0;
        totalKiwis = 0;
        predatorsTrapped = 0;
        kiwiCount = 0;
	//Initial the island data from a xml file
        initialiseIslandFromFile("xml/IslandData.xml");
        drawIsland();
        state = GameState.PLAYING;
        winMessage = "";
        loseMessage = "";
        playerMessage = "";
        notifyGameEventListeners();
    }

    /***********************************************************************************************************************
     * Accessor methods for game data
    ************************************************************************************************************************/
    
    /**
     * Get number of rows on island
     * @return number of rows.
     */
    public int getNumRows()
    {
        return island.getNumRows();
    }
    
    /**
     * Get number of columns on island
     * @return number of columns.
     */
    public int getNumColumns()
    {
        return island.getNumColumns();
    }
    
    /**
     * Gets the current state of the game.
     * 
     * @return the current state of the game
     */
    public GameState getState()
    {
        return state;
    }    
 
    /**
     * Provide a description of occupant
     * @param whichOccupant
     * @return description if whichOccuoant is an instance of occupant, empty string otherwise
     */
    public String getOccupantDescription(Object whichOccupant)
    {
       String description = "";
        if(whichOccupant !=null && whichOccupant instanceof Occupant)
        {
            Occupant occupant = (Occupant) whichOccupant;
            description = occupant.getDescription();
        }
        return description;
    }
 
        /**
     * Gets the player object.
     * @return the player object
     */
    public Player getPlayer()
    {
        return player;
    }
    
    /**
     * Checks if possible to move the player in the specified direction.
     * 
     * @param direction the direction to move
     * @return true if the move was successful, false if it was an invalid move
     */
    public boolean isPlayerMovePossible(MoveDirection direction)
    {
        boolean isMovePossible = false;
        // what position is the player moving to?
        Position newPosition = player.getPosition().getNewPosition(direction);
        // is that a valid position?
        if ( (newPosition != null) && newPosition.isOnIsland() )
        {
            // what is the terrain at that new position?
            Terrain newTerrain = island.getTerrain(newPosition);
            // can the playuer do it?
            isMovePossible = player.hasStaminaToMove(newTerrain) && 
                             player.isAlive();
        }
        return isMovePossible;
    }
    
      /**
     * Get terrain for position
     * @param row
     * @param column
     * @return Terrain at position row, column
     */
    public Terrain getTerrain(int row, int column) {
        return island.getTerrain(new Position(island, row, column));
    }

    /**
     * Is this position visible?
     * @param row
     * @param column
     * @return true if position row, column is visible
     */
    public boolean isVisible(int row, int column) {
        return island.isVisible(new Position(island, row, column));

    }
   
    /**
    * Is this position explored?
    * @param row
    * @param column
    * @return true if position row, column is explored.
    */
    public boolean isExplored(int row, int column) {
        return island.isExplored(new Position(island, row, column));
    }

    /**
     * Get occupants for player's position
     * @return occupants at player's position
     */
    public Occupant[] getOccupantsPlayerPosition()
    {
        return island.getOccupants(player.getPosition());
    }
    
    /**
     * Get string for occupants of this position
     * @param row
     * @param column
     * @return occupant string for this position row, column
     */
    public String getOccupantStringRepresentation(int row, int column) {
        return island.getOccupantStringRepresentation(new Position(island, row, column));
    }
    /**
     * Get the name of the occupant according the player's positions
     * @param row
     * @param column
     * @return 
     */
    public String getOccupantName(int row,int column){
    return island.getOccupantNameString(new Position(island, row, column));
    }
    
    /**
     * Get values from player for GUI display
     * @return player values related to stamina and backpack.
     */
    public int[] getPlayerValues()
    {
        int[] playerValues = new int[6];
        playerValues[STAMINA_INDEX ]= (int) player.getStaminaLevel();
        playerValues[MAXSTAMINA_INDEX]= (int) player.getMaximumStaminaLevel();
        playerValues[MAXWEIGHT_INDEX ]= (int) player.getMaximumBackpackWeight();
        playerValues[WEIGHT_INDEX]= (int) player.getCurrentBackpackWeight();
        playerValues[MAXSIZE_INDEX ]= (int) player.getMaximumBackpackSize();
        playerValues[SIZE_INDEX]= (int) player.getCurrentBackpackSize();
            
        return playerValues;
        
    }
    
    /**
     * How many kiwis have been counted?
     * @return count
     */
    public int getKiwiCount()
    {
        return kiwiCount;
    }
    
    /**
     * How many predators are left?
     * @return number remaining
     */
    public int getPredatorsRemaining()
    {
        return totalPredators - predatorsTrapped;
    }
    
    /**
     * Get contents of player backpack
     * @return objects in backpack
     */
    public Object[] getPlayerInventory()
            {
              return  player.getInventory().toArray();
            }
    
    /**
     * Get player name
     * @return player name
     */
    public String getPlayerName()
    {
        return player.getName();
    }

    /**
     * Is player in this position?
     * @param row
     * @param column
     * @return true if player is at row, column
     */
    public boolean hasPlayer(int row, int column) 
    {
        return island.hasPlayer(new Position(island, row, column));
    }
    
    /**
     * Only exists for use of unit tests
     * @return island
     */
    public Island getIsland()
    {
        return island;
    }
    
    /**
     * Draws the island grid to standard output.
     */
    public void drawIsland()
    {  
          island.draw();
    }
    
     /**
     * Is this object collectable
     * @param itemToCollect
     * @return true if is an item that can be collected.
     */
    public boolean canCollect(Object itemToCollect)
    {
        boolean result = (itemToCollect != null)&&(itemToCollect instanceof Item);
        if(result)
        {
            Item item = (Item) itemToCollect;
            result = item.isOkToCarry();
        }
        return result;
    }
    
    /**
     * Is this object a countable kiwi
     * @param itemToCount
     * @return true if is an item is a kiwi.
     */
    public boolean canCount(Object itemToCount)
    {
        boolean result = (itemToCount != null)&&(itemToCount instanceof Kiwi);
        if(result)
        {
            Kiwi kiwi = (Kiwi) itemToCount;
            result = !kiwi.counted();
        }
        return result;
    }
    /**
     * Is this object usable
     * @param itemToUse
     * @return true if is an item that can be collected.
     */
    public boolean canUse(Object itemToUse)
    {
        boolean result = (itemToUse != null)&&(itemToUse instanceof Item);
        if(result)
        {
            //Food can always be used (though may be wasted)
            // so no need to change result

            if(itemToUse instanceof Tool)
            {
                Tool tool = (Tool)itemToUse;
                //Traps can only be used if there is a predator to catch
                if(tool.isTrap())
                {
                    result = island.hasPredator(player.getPosition());
                }
                //Screwdriver can only be used if player has a broken trap
                else if (tool.isScrewdriver() && player.hasTrap())
                {
                    result = player.getTrap().isBroken();
                }
                else
                {
                    result = false;
                }
            }            
        }
        return result;
    }
    
        
    /**
     * Details of why player won
     * @return winMessage
     */
    public String getWinMessage()
    {
        return winMessage;
    }
    
    /**
     * Details of why player lost
     * @return loseMessage
     */
    public String getLoseMessage()
    {
        return loseMessage;
    }
    
    /**
     * Details of information for player
     * @return playerMessage
     */
    public String getPlayerMessage()
    {
        String message = playerMessage;
        playerMessage = ""; // Already told player.
        return message;
    }
    
    /**
     * Is there a message for player?
     * @return true if player message available
     */
    public boolean messageForPlayer() {
        return !("".equals(playerMessage));
    }
    
    /***************************************************************************************************************
     * Mutator Methods
    ****************************************************************************************************************/
    
   
    
    /**
     * Picks up an item at the current position of the player
     * Ignores any objects that are not items as they cannot be picked up
     * @param item the item to pick up
     * @return true if item was picked up, false if not
     */
    public boolean collectItem(Object item)
    {
        boolean success = (item instanceof Item) && (player.collect((Item)item));
        if(success)
        {
            // player has picked up an item: remove from grid square
            island.removeOccupant(player.getPosition(), (Item)item);
            
            
            // everybody has to know about the change
            notifyGameEventListeners();
        }      
        return success;
    } 

    
    /**
     * Drops what from the player's backpack.
     *
     * @param what  to drop
     * @return true if what was dropped, false if not
     */
    public boolean dropItem(Object what)
    {
        boolean success = player.drop((Item)what);
        if ( success )
        {
            // player has dropped an what: try to add to grid square
            Item item = (Item)what;
            success = island.addOccupant(player.getPosition(), item);
            if ( success )
            {
                // drop successful: everybody has to know that
                notifyGameEventListeners();
            }
            else
            {
                // grid square is full: player has to take what back
                player.collect(item);                     
            }
        }
        return success;
    } 
      
    
    /**
     * Uses an item in the player's inventory.
     * This can  be food or tool items.
     * @param item to use
     * @return true if the item has been used, false if not
     */
    public boolean useItem(Object item)
    {  
        boolean success = false;
        if ( item instanceof Food && player.hasItem((Food) item) )
        //Player east food to increase stamina
        {
            Food food = (Food) item;
            if("Mushroom".equals(food.getName())){
                            Random rand = new Random();
                            //Determine whether the mushroom is toxic
                            boolean flag = rand.nextBoolean();
                            if(flag){
                             //Reduce stamina if the mushroom is toxic
                             player.reduceStamina(food.getEnergy());
                             this.playerMessage="The mushroom is toxic!";
                              // player has consumed the food: remove from inventory
                             player.drop(food);
                            }else{
                              //gets energy if the mushroom is not toxic
                              player.increaseStamina(food.getEnergy());
                              // player has consumed the food: remove from inventory
                              player.drop(food);
                            }
                        }else{
            // player gets energy boost from food
            player.increaseStamina(food.getEnergy());
            // player has consumed the food: remove from inventory
            player.drop(food);
            }
            // use successful: everybody has to know that
            notifyGameEventListeners();
        }
        else if (item instanceof Tool)
        {
            Tool tool = (Tool) item;
            if (tool.isTrap()&& !tool.isBroken())
            {
                 success = trapPredator(); 
            }
            else if(tool.isScrewdriver())// Use screwdriver (to fix trap)
            {
                if(player.hasTrap())
                    {
                        Tool trap = player.getTrap();
                        trap.fix();
                    }
            }
        }
        updateGameState();
        return success;
    }
    
    /**
     * Count any kiwis in this position
     */
    public void countKiwi() 
    {
        //check if there are any kiwis here
        for (Occupant occupant : island.getOccupants(player.getPosition())) {
            if (occupant instanceof Kiwi) {
                Kiwi kiwi = (Kiwi) occupant;
                if (!kiwi.counted()) {
                    kiwi.count();
                    kiwiCount++;
                    //add the scores if the kiwi have been counted
                    this.player.setScores(this.player.getScores()+10);
                }
            }
        }
        updateGameState();
    }
       
    /**
     * Attempts to move the player in the specified direction.
     * 
     * @param direction the direction to move
     * @return true if the move was successful, false if it was an invalid move
     */
    public boolean playerMove(MoveDirection direction)
    {
        // what terrain is the player moving on currently
        boolean successfulMove = false;
        if ( isPlayerMovePossible(direction) )
        {
            Position newPosition = player.getPosition().getNewPosition(direction);
            Terrain  terrain     = island.getTerrain(newPosition);

            // move the player to new position
            player.moveToPosition(newPosition, terrain);
            island.updatePlayerPosition(player);
            successfulMove = true;
                    
            // Is there a hazard?
            checkForHazard();
            
            updateGameState();            
        }
        return successfulMove;
    }
    
    
    
    /**
     * Adds a game event listener.
     * @param listener the listener to add
     */
    public void addGameEventListener(GameEventListener listener)
    {
        eventListeners.add(listener);
    }
    
    
    /**
     * Removes a game event listener.
     * @param listener the listener to remove
     */
    public void removeGameEventListener(GameEventListener listener)
    {
        eventListeners.remove(listener);
    }
   
    
    /*********************************************************************************************************************************
     *  Private methods
     *********************************************************************************************************************************/
    
    /**
     * Used after player actions to update game state.
     * Applies the Win/Lose rules.
     */
    private void updateGameState()
    {
         String message = "";
        if ( !player.isAlive() )
        {
            state = GameState.LOST;
            message = "Sorry, you have lost the game. " + this.getLoseMessage();
            this.player.setScores(this.player.getScores()-StepCounter.getSingleTon().getStep());
            this.setLoseMessage(message);
        }
        else if (!playerCanMove() )
        {
            state = GameState.LOST;
            message = "Sorry, you have lost the game. You do not have sufficient stamina to move.";
            this.player.setScores(this.player.getScores()-StepCounter.getSingleTon().getStep());
            this.setLoseMessage(message);
        }
        else if(predatorsTrapped == totalPredators)
        {
            state = GameState.WON;
            message = "You win! You have done an excellent job and trapped all the predators.";
            this.setWinMessage(message);
            //if all the predators are trapped, the player will get 550 scores
            this.player.setScores(this.player.getScores()+550-(StepCounter.getSingleTon().getStep()*2));
        }
        else if(kiwiCount == totalKiwis)
        {   
            if(predatorsTrapped >= totalPredators * MIN_REQUIRED_CATCH)
            {
                state = GameState.WON;
                message = "You win! You have counted all the kiwi and trapped at least 80% of the predators.";
                this.setWinMessage(message);
                  //if all the kiwis have been counted and the trapped predators over the minimum required catch number, get 500 scores
                this.player.setScores(this.player.getScores()+500-(StepCounter.getSingleTon().getStep()*2));
            }
        }
        // notify listeners about changes
            notifyGameEventListeners();
    }
    
       
    /**
     * Sets details about players win
     * @param message 
     */
    private void setWinMessage(String message)
    {
        winMessage = message;
    }
    
    /**
     * Sets details of why player lost
     * @param message 
     */
    private void setLoseMessage(String message)
    {
        loseMessage = message;
    }
    
    /**
     * Set a message for the player
     * @param message 
     */
    private void setPlayerMessage(String message) 
    {
        playerMessage = message;
        
    }
    
    /**
     * Get the current player's name
     * @return 
     */
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
    
    /**
     * Set the current player's name
     * @param currentPlayerName 
     */
    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }
    
    
    
    /**
     * Check if player able to move
     * @return true if player can move
     */
    private boolean playerCanMove() 
    {
        return ( isPlayerMovePossible(MoveDirection.NORTH)|| isPlayerMovePossible(MoveDirection.SOUTH)
                || isPlayerMovePossible(MoveDirection.EAST) || isPlayerMovePossible(MoveDirection.WEST));

    }
        
    /**
     * Trap a predator in this position
     * @return true if predator trapped
     */
    private boolean trapPredator()
    {
        Position current= player.getPosition();
        boolean hadPredator = island.hasPredator(current);
        if(hadPredator) //can trap it
        {
            Occupant occupant = island.getPredator(current);
            //Predator has been trapped so remove
            island.removeOccupant(current, occupant); 
            predatorsTrapped++;
            //add the scores if the preadtor has been trapped
            this.player.setScores(this.player.getScores()+10);
        }
        
        return hadPredator;
    }
    
    /**
     * Checks if the player has met a hazard and applies hazard impact.
     * Fatal hazards kill player and end game.
     */
    private void checkForHazard()
    {
        //check if there are hazards
        for ( Occupant occupant : island.getOccupants(player.getPosition())  )
        {
            if ( occupant instanceof Hazard )
            {
               handleHazard((Hazard)occupant) ;
            }
        }
    }
    
    /**
     * Apply impact of hazard
     * @param hazard to handle
     */
    private void handleHazard(Hazard hazard) {
        if (hazard.isFatal()) 
        {
            
            player.kill();
            this.setLoseMessage(hazard.getDescription() + " has killed you.");
        } 
        else if (hazard.isBreakTrap()) 
        {
            Tool trap = player.getTrap();
            if (trap != null) {
                trap.setBroken();
                this.setPlayerMessage("Sorry your predator trap is broken. You will need to find tools to fix it before you can use it again.");
            }
        } 
        else // hazard reduces player's stamina
        {   //Tornadoes the player will be blown to a new location
            if("Tornadoes".equals(hazard.getName())){
                 Random rand = new Random();
                 //New position's row
                 int row = rand.nextInt(10);
                 //New position's column
                 int column = rand.nextInt(10);
                 //New Position of the player
                 Position newPosition = new Position(island, row, column);
                 Terrain  terrain     = island.getTerrain(newPosition);
                // move the player to new position
                 player.moveToPosition(newPosition, terrain);
                 island.updatePlayerPosition(player);

            }
            double impact = hazard.getImpact();
            // Impact is a reduction in players energy by this % of Max Stamina
            double reduction = player.getMaximumStaminaLevel() * impact;
            player.reduceStamina(reduction);
            // if stamina drops to zero: player is dead
            if (player.getStaminaLevel() <= 0.0) {
                player.kill();
                this.setLoseMessage(" You have run out of stamina");
            }
            else // Let player know what happened
            {
                this.setPlayerMessage(hazard.getDescription() + " has reduced your stamina.");
            }
        }
    }
    
    
    /**
     * Notifies all game event listeners about a change.
     */
    private void notifyGameEventListeners()
    {
        for ( GameEventListener listener : eventListeners ) 
        {
            listener.gameStateChanged();
        }
    }

    
   /**
     * Loads terrain and occupant data from a xml.
     * At this stage this method assumes that the data file is correct and just
     * throws an exception or ignores it if it is not.
     * 
     * @param xmlName xml name of the data file
     */
    private void initialiseIslandFromFile(String xmlName) 
    {
        try
        {
            SAXReader reader = new SAXReader();
            File file = new File(xmlName);
            Document doc = reader.read(file);
            Element root = doc.getRootElement();
            Element islandE = root.element("island");
            //Get the number of island's row
            int rowNum = Integer.parseInt(islandE.element("numRows").getTextTrim());
            //Get the column of island's column
            int colNum = Integer.parseInt(islandE.element("numColumns").getTextTrim());
            this.island = new Island(rowNum, colNum);
            //Set up the player information
            setUpPlayer(root);
            //Set up the level of the island
            
            //Set up the terrain for each grid
            setUpTerrain(root);
            //Set up the occupant for each grid
            setUpOccupants(root);
        }
         catch (DocumentException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads terrain data and creates the terrain.
     * 
     * @param root data from the xml file
     */
    private void setUpTerrain(Element root) 
    {
            //Set the terrain for each grid suqare
            //Get the terrain from xml file
            String terrainStr = root.element("terrain").getTextTrim();
            String[] terrainArr = terrainStr.split(",");
            for(int row=0;row<terrainArr.length;row++){
                String terrainRow = terrainArr[row];
                for(int col=0;col<terrainArr.length-1;col++){
                    Position pos = new Position(island, row, col);
                    String   terrainString = terrainRow.trim().substring(col, col+1);
                    Terrain terrain = Terrain.getTerrainFromStringRepresentation(terrainString);
                    island.setTerrain(pos, terrain);
                }
            }
    }

    /**
     * Reads player data and creates the player.
     * @param root data from the xml file
     */
    
    private void setUpPlayer(Element root) 
    {   
            //Get Player Informaiton
            Element playerE = root.element("player");
            //Get the name of player
            String playerName = playerE.attributeValue("name");
            //Get the player's initial position
            //Get the row of the positon
            int playerPosRow = Integer.parseInt(playerE.element("posRow").getTextTrim());
            //Get the column of the position
            int playerPosCol = Integer.parseInt(playerE.element("posCol").getTextTrim());
            //Get the max statmina of the player
            double playerMaxStamina = Double.parseDouble(playerE.element("maxStamina").getTextTrim());
            //Get the max backpack weight of the player
            double playerMaxBackpackWeight = Double.parseDouble(playerE.element("maxBackpackWeight").getTextTrim());
            //Get the max backpack size of the player
            double playerMaxBackpackSize = Double.parseDouble(playerE.element("maxBackpackSize").getTextTrim());

             Position pos = new Position(island, playerPosRow, playerPosCol);
             player = new Player(pos,playerName ,playerMaxStamina, 
             playerMaxBackpackWeight, playerMaxBackpackSize);
             
             island.updatePlayerPosition(player);
    }

    /**
     * Creates occupants listed in the file and adds them to the island.
     * @param root data from the xml file
     */
    private void setUpOccupants(Element root) 
    {     
            Occupant occupant = null;
            //Get the number of total items
            int itemNum = Integer.parseInt(root.element("occupants").element("itemNum").getTextTrim());
            String occupants = root.element("occupants").getTextTrim();
            //Split the string according to the ";" symbol
            String[] occArr = occupants.split(";");
            for(int i=0;i<occArr.length;i++){
                //Split the string according to the "," symbol
                String[] tempArr = occArr[i].split(",");
                for(int j=0;j<tempArr.length;j++){
                        //If the occupant type is "T"
                    if(tempArr[j].trim().equals("T")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        double weight = Double.parseDouble(tempArr[j+5].trim());
                        double size = Double.parseDouble(tempArr[j+6].trim());
                        occupant = new Tool(occPos, occName, occDesc, weight, size);
                        island.addOccupant(occPos, occupant);
                    }   //If the occupant type is "E"
                        else if(tempArr[j].trim().equals("E")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        double weight = Double.parseDouble(tempArr[j+5].trim());
                        double size = Double.parseDouble(tempArr[j+6].trim());
                        double energy = Double.parseDouble(tempArr[j+7].trim());
                        occupant = new Food(occPos, occName, occDesc, weight, size, energy);
                        island.addOccupant(occPos, occupant);
                    }   //If the occupant type is "H"
                        else if(tempArr[j].trim().equals("H")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        double impact = Double.parseDouble(tempArr[j+5].trim());
                        occupant = new Hazard(occPos, occName, occDesc,impact);
                        island.addOccupant(occPos, occupant);
                    }   //If the occupant type is "K"
                        else if(tempArr[j].trim().equals("K")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        occupant = new Kiwi(occPos, occName, occDesc);
                        island.addOccupant(occPos, occupant);
                        totalKiwis++;
                    }   //If the occupant type is "P"
                        else if(tempArr[j].trim().equals("P")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        occupant = new Predator(occPos, occName, occDesc);
                        island.addOccupant(occPos, occupant);
                        totalPredators++;
                    }   //If the occupant type is "F"
                        else if(tempArr[j].trim().equals("F")){
                        String occName = tempArr[j+1].trim();
                        String occDesc = tempArr[j+2].trim();
                        int occRow = Integer.parseInt(tempArr[j+3].trim());
                        int occCol = Integer.parseInt(tempArr[j+4].trim());
                        Position occPos = new Position(island, occRow, occCol);
                        occupant = new Fauna(occPos, occName, occDesc);
                        island.addOccupant(occPos, occupant);
                    }
                    
                }
            }

    }  
    /**
     * Log in
     * @param userName
     * @param password
     * @return 
     */
     public String login(String userName,String password){
         String messageTxt = "";
         try {
            Statement statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM PLAYER WHERE PLAYERNAME = '"+userName+"'");
            if(rs.next()){
                if(rs.getString("PASSWORD").equals(password)){
                    messageTxt = "Successful Login!! Welcome to kiwiisland, "+userName;
                }else{
                    messageTxt = "Sorry, you have input wrong password";
                }
            }else{
                    messageTxt = "The user name is not existed";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
         return messageTxt;
    }
    /**
     * Register a new account
     * @param userName
     * @param password
     * @return 
     */
    public String register(String userName,String password){
        Boolean accountExist = null;
        ResultSet rs = null;
        String messageString ="";
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM PLAYER WHERE PLAYERNAME = '" + userName + "'");
             if (rs.next()) {
                accountExist = true;
            } else {
                statement.executeUpdate("INSERT INTO PLAYER (PLAYERNAME,PASSWORD) VALUES ('" + userName + "','" + password + "')");
                accountExist = false;
            }
            if(accountExist){
                messageString = "Sorry the user name is already existed.";
            }else{
                messageString = "Congratulations! The register operation is successful.";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        return messageString;
    }
    
    /**
     * Auto connect to the kiwi island database
     */
    public void autoConnectKiwiIslandDB(){
        try {
            conn = DriverManager.getConnection(url, username, password);
            statement = conn.createStatement();
            if(checkTableExisting("PLAYER")){
            statement.executeUpdate("CREATE TABLE PLAYER(PLAYERID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),PLAYERNAME VARCHAR(30) NOT NULL,PASSWORD VARCHAR(20) NOT NULL)");//Create the player table
            }
            if(checkTableExisting("RANKBOARD")){
            statement.executeUpdate("CREATE TABLE RANKBOARD(RANKID INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),PLAYERNAME VARCHAR(30) NOT NULL, SCORES INT NOT NULL)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Check whether the table is already existed
     *
     * @param newTableName
     */
    public Boolean checkTableExisting(String newTableName) {
        Boolean flag = true;
        try {
            System.out.println("Check Existing Tables.... ");
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rsDBMeta = dbmd.getTables(null, null, null, null);//types);
            Statement dropStatement = null;
            while (rsDBMeta.next()) {
                String tableName = rsDBMeta.getString("TABLE_NAME");
                if (tableName.compareToIgnoreCase(newTableName) == 0) {
                    flag = false;
                }
            }
            if (rsDBMeta != null) {
                rsDBMeta.close();
            }
            if (dropStatement != null) {
                dropStatement.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
           finally{
            return flag;
        }
    }
    /**
     * Save the scores
     */
    public void saveScores(){
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("INSERT INTO RANKBOARD (PLAYERNAME,SCORES) VALUES ('"+this.getCurrentPlayerName()+"',"+this.player.getScores()+")");
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Read the rank data
     */
    public ArrayList rankScores(){
        ArrayList<String> rankList = new ArrayList<String>();
        ArrayList<String> resultList = new ArrayList<String>(10);
        try {
            Statement statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM RANKBOARD");
            while(rs.next()){
                rankList.add(rs.getString("PLAYERNAME")+";"+rs.getInt("SCORES"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } 
        /* Sort the rank list */
        for (int i = 0; i < rankList.size(); i++) {
            for (int j = 0; j < rankList.size()-1; j++) {
               if(Integer.parseInt(rankList.get(j).split(";")[1])<Integer.parseInt(rankList.get(j+1).split(";")[1])){
                   String temp = rankList.get(j);
                   rankList.set(j,rankList.get(j+1));
                   rankList.set(j+1,temp);
               }
            } 
        }
        /* Put the values in the result list */
        for (int i = 0; i < (rankList.size()>10 ? 10 : rankList.size()); i++) {
            resultList.add("     No:"+(i+1)+"     Player:"+rankList.get(i).split(";")[0]+"      Scores:"+rankList.get(i).split(";")[1]);            
        }
        return resultList;
    }

    private Island island;
    private Player player;
    private GameState state;
    private int kiwiCount;
    private int totalPredators;
    private int totalKiwis;
    private int predatorsTrapped;
    private Set<GameEventListener> eventListeners;
    private final double MIN_REQUIRED_CATCH = 0.8;
    private String winMessage = "";
    private String loseMessage  = "";
    private String playerMessage  = "";   
    

    private Connection conn = null;
    private String url = "jdbc:derby:kiwiIslandDB;create=true";//url of the DB host
    private String username = "groupmj2";  //your DB username
    private String password = "123123";   //your DB password
    private Statement statement;
    private ResultSet rs;
    private String currentPlayerName;


}
