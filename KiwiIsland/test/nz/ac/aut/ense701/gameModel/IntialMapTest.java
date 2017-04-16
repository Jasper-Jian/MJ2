/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.ac.aut.ense701.gameModel;

import java.io.File;
import nz.ac.aut.ense701.gameModel.Fauna;
import nz.ac.aut.ense701.gameModel.Food;
import nz.ac.aut.ense701.gameModel.Hazard;
import nz.ac.aut.ense701.gameModel.Island;
import nz.ac.aut.ense701.gameModel.Kiwi;
import nz.ac.aut.ense701.gameModel.Occupant;
import nz.ac.aut.ense701.gameModel.Position;
import nz.ac.aut.ense701.gameModel.Predator;
import nz.ac.aut.ense701.gameModel.Terrain;
import nz.ac.aut.ense701.gameModel.Tool;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Minghao Yang
 */
public class IntialMapTest {
    
    public static void main(String[] args) throws DocumentException{
        Occupant occupant = null;
        SAXReader reader = new SAXReader();
        File file = new File("xml/testXml.xml");
        Document doc = reader.read(file);
        Element root = doc.getRootElement();
        Element island = root.element("island");
	//Get the number of island's row
        int rowNum = Integer.parseInt(island.element("numRows").getTextTrim());
        //Get the column of island's column
        int colNum = Integer.parseInt(island.element("numColumns").getTextTrim());
        Island island1 = new Island(rowNum, colNum);
        System.out.println(island1);
        //Get Player Informaiton
        Element player = root.element("player");
        //Get the name of player
        String playName = player.attributeValue("name");
        //Get the player's initial position
        //Get the row of the positon
        int posRow = Integer.parseInt(player.element("posRow").getTextTrim());
        //Get the column of the position
        int posCol = Integer.parseInt(player.element("posCol").getTextTrim());
        //Get the max statmina of the player
        double maxStamina = Double.parseDouble(player.element("maxStamina").getTextTrim());
        //Get the max backpack weight of the player  
        double maxBackpackWeight = Double.parseDouble(player.element("maxBackpackWeight").getTextTrim());
        //Get the max backpack size of the player
        double maxBackpackSize = Double.parseDouble(player.element("maxBackpackSize").getTextTrim());
        //Set the terrain for each grid suqare
        //Get the terrain from xml file
         String terrain = root.element("terrain").getTextTrim();
         String[] terrainArr = terrain.split(",");
         for(int row=0;row<terrainArr.length;row++){
             String terrainRow = terrainArr[row];
             for(int col=0;col<terrainArr.length;col++){
                 String   terrainString = terrainRow.substring(col, col+1);
                 Terrain terr = Terrain.getTerrainFromStringRepresentation(terrainString);
                 System.out.println(terr);
                
             }
         }
        int itemNum = Integer.parseInt(root.element("occupants").element("itemNum").getTextTrim());
        System.out.println(itemNum);
        String occupants = root.element("occupants").getTextTrim();
        String[] occArr = occupants.split(";");
        for(int i=0;i<occArr.length;i++){
        String[] tempArr = occArr[i].split(",");
            for(int j=0;j<tempArr.length;j++){ 
                   //If the occupant type is "T"
                   if(tempArr[j].trim().equals("T")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       double weight = Double.parseDouble(tempArr[j+5].trim());
                       double size = Double.parseDouble(tempArr[j+6].trim());
                       occupant = new Tool(occPos, occName, occDesc, weight, size);
                       island1.addOccupant(occPos, occupant);
                   }else if(tempArr[j].trim().equals("E")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       double weight = Double.parseDouble(tempArr[j+5].trim());
                       double size = Double.parseDouble(tempArr[j+6].trim());
                       double energy = Double.parseDouble(tempArr[j+7].trim());
                       occupant = new Food(occPos, occName, occDesc, weight, size, energy);
                       island1.addOccupant(occPos, occupant);
                   }else if(tempArr[j].trim().equals("H")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       double impact = Double.parseDouble(tempArr[j+5].trim());
                       occupant = new Hazard(occPos, occName, occDesc,impact);
                       island1.addOccupant(occPos, occupant);
                   }else if(tempArr[j].trim().equals("K")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       occupant = new Kiwi(occPos, occName, occDesc);
                       island1.addOccupant(occPos, occupant);
                   }else if(tempArr[j].trim().equals("P")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       occupant = new Predator(occPos, occName, occDesc);
                       island1.addOccupant(occPos, occupant);
                   }else if(tempArr[j].trim().equals("F")){
                       String occName = tempArr[j+1].trim();
                       String occDesc = tempArr[j+2].trim();
                       int occRow = Integer.parseInt(tempArr[j+3].trim());
                       int occCol = Integer.parseInt(tempArr[j+4].trim());
                       Position occPos = new Position(island1, occRow, occCol);
                       occupant = new Fauna(occPos, occName, occDesc);
                       island1.addOccupant(occPos, occupant);
                   }
                
            }
        }
    }
}
