package nz.ac.aut.ense701.gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import nz.ac.aut.ense701.gameModel.Game;
import nz.ac.aut.ense701.gameModel.GameEventListener;
import nz.ac.aut.ense701.gameModel.GameState;
import nz.ac.aut.ense701.gameModel.MoveDirection;
import nz.ac.aut.ense701.gameModel.StepCounter;
import nz.ac.aut.ense701.gameModel.Timer;
import nz.ac.aut.ense701.gameModel.Weather;

/*
 * User interface form for Kiwi Island.
 * 
 * @author AS
 * @version Apirl 2017
 */
public class KiwiCountUI
        extends javax.swing.JFrame
        implements GameEventListener {

    private StepCounter stepCounter = StepCounter.getSingleTon();
    private Weather weather = Weather.getSingleTon();

    /**
     * Creates a GUI for the KiwiIsland game.
     *
     * @param game the game object to represent with this GUI.
     */
    public KiwiCountUI(Game game) {
        assert game != null : "Make sure game object is created before UI";
        this.game = game;
        createHeartBeat();
        setAsGameListener();
        initComponents();
        timer = new Timer(this);
        timer.start();
        initIslandGrid();
        CustomKeyListener();
        update();
    }

    /**
     * This method is called by the game model every time something changes.
     * Trigger an update.
     */
    @Override
    public void gameStateChanged() {
        //Get the changed weather string
        weather.getWeatherChageStr();
        //count step
        stepCounter.addStep();
        update();
        //if the player's stamina is too low, user will hear the heart beat warning
        lowStaminaWarn();
        //change the player's stamina color according to the level of the stamina
        changeStaminaColor();
        // check for "game over" or "game won"
        if (game.getState() == GameState.LOST) {
            JOptionPane.showMessageDialog(
                    this,
                    game.getLoseMessage(), "Game over!",
                    JOptionPane.INFORMATION_MESSAGE);
            //initialize the step of stepcounter
            stepCounter.setStep(-1);
            weather.setWeatherStr("Sunny");
            JOptionPane.showMessageDialog(
                    this,
                    "You have got " + this.game.getPlayer().getScores() + " scores.", "SCORES",
                    JOptionPane.INFORMATION_MESSAGE);
            this.game.saveScores();
            //Return zero to the seconds jlabel
            seconds.setText("00m:00s");
            //Reset the timer
            timer.setProgramStart(System.currentTimeMillis());
            game.createNewGame();
        } else if (game.getState() == GameState.WON) {
            int minute = Integer.parseInt(this.seconds.getText().substring(0, 2));
            //Decrease the scores according to the time
            this.game.getPlayer().setScores(this.game.getPlayer().getScores()-minute*50);
            JOptionPane.showMessageDialog(
                    this,
                    "You have got " + this.game.getPlayer().getScores() + " scores.", "SCORES",
                    JOptionPane.INFORMATION_MESSAGE);
            this.game.saveScores();
            //initialize the step of stepcounter
            stepCounter.setStep(-1);
            weather.setWeatherStr("Sunny");
            JOptionPane.showMessageDialog(
                    this,
                    game.getWinMessage(), "You have got " + this.game.getPlayer().getScores() + " scores.",
                    JOptionPane.INFORMATION_MESSAGE);
            //Return zero to the seconds jlabel
            seconds.setText("00m:00s");
            //Reset the timer
            timer.setProgramStart(System.currentTimeMillis());
            game.createNewGame();
        } else if (game.messageForPlayer()) {
            JOptionPane.showMessageDialog(
                    this,
                    game.getPlayerMessage(), "Important Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setAsGameListener() {
        game.addGameEventListener(this);
    }

    /**
     * Updates the state of the UI based on the state of the game.
     */
    private void update() {
        // update the grid square panels
        Component[] components = pnlIsland.getComponents();
        for (Component c : components) {
            // all components in the panel are GridSquarePanels,
            // so we can safely cast
            GridSquarePanel gsp = (GridSquarePanel) c;
            gsp.update();
        }

        // update player information
        int[] playerValues = game.getPlayerValues();
        txtPlayerName.setText(game.getCurrentPlayerName());
        progPlayerStamina.setMaximum(playerValues[Game.MAXSTAMINA_INDEX]);
        progPlayerStamina.setValue(playerValues[Game.STAMINA_INDEX]);
        progBackpackWeight.setMaximum(playerValues[Game.MAXWEIGHT_INDEX]);
        progBackpackWeight.setValue(playerValues[Game.WEIGHT_INDEX]);
        progBackpackSize.setMaximum(playerValues[Game.MAXSIZE_INDEX]);
        progBackpackSize.setValue(playerValues[Game.SIZE_INDEX]);

        //Update Kiwi and Predator information
        txtKiwisCounted.setText(Integer.toString(game.getKiwiCount()));
        txtPredatorsLeft.setText(Integer.toString(game.getPredatorsRemaining()));

        //Update Weather and Step Counter information
        jLabel1.setText(Integer.toString(stepCounter.getStep()));
        jLabel4.setText(weather.getWeatherStr());

        // update inventory list
        listInventory.setListData(game.getPlayerInventory());
        listInventory.clearSelection();
        listInventory.setToolTipText(null);
        btnUse.setEnabled(false);
        btnDrop.setEnabled(false);

        // update list of visible objects
        listObjects.setListData(game.getOccupantsPlayerPosition());
        listObjects.clearSelection();
        listObjects.setToolTipText(null);
        btnCollect.setEnabled(false);
        btnCount.setEnabled(false);

        // update movement buttons
        btnMoveNorth.setEnabled(game.isPlayerMovePossible(MoveDirection.NORTH));
        btnMoveEast.setEnabled(game.isPlayerMovePossible(MoveDirection.EAST));
        btnMoveSouth.setEnabled(game.isPlayerMovePossible(MoveDirection.SOUTH));
        btnMoveWest.setEnabled(game.isPlayerMovePossible(MoveDirection.WEST));
    }

    //Combine the KeyListener to different pannel
    public void CustomKeyListener() {
        setKeyListener(this);
        pnlIsland.setFocusable(true);

        //Add space_bar key and X,C key listener for Use,collect, Drop and count 
        setKeyListener(pnlIsland);
        setKeyListener(btnCollect);
        setKeyListener(btnCount);
        setKeyListener(btnDrop);
        setKeyListener(btnUse);

        //Add arrow key and ASWD key listener use for key movement 
        setKeyListener(btnMoveEast);
        setKeyListener(btnMoveNorth);
        setKeyListener(btnMoveSouth);
        setKeyListener(btnMoveWest);

        //Add the key listener in different panel to enable the listener ability
        setKeyListener(listInventory);
        setKeyListener(listObjects);
        setKeyListener(lblKiwisCounted);
        setKeyListener(lblPredators);

    }

    //implement key event by using KeyListener
    public void setKeyListener(Component component) {
        component.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int KeyScan = e.getKeyCode();
                switch (KeyScan) {
                    //↑ and W is moving North
                    case KeyEvent.VK_UP:
                        btnMoveNorth.doClick();
                        break;
                    case KeyEvent.VK_W:
                        btnMoveNorth.doClick();
                        break;
                    //↓ and S for South
                    case KeyEvent.VK_DOWN:
                        btnMoveSouth.doClick();
                        break;
                    case KeyEvent.VK_S:
                        btnMoveSouth.doClick();
                        break;
                    //← and A for West
                    case KeyEvent.VK_LEFT:
                        btnMoveWest.doClick();
                        break;
                    case KeyEvent.VK_A:
                        btnMoveWest.doClick();
                        break;
                    //→ and D for East
                    case KeyEvent.VK_RIGHT:
                        btnMoveEast.doClick();
                        break;
                    case KeyEvent.VK_D:
                        btnMoveEast.doClick();
                        break;
                    //Space_Bar for Use,Collect and Count
                    case KeyEvent.VK_SPACE:
                        btnUse.doClick();
                        btnCollect.doClick();
                        btnCount.doClick();
                        break;
                    //C for Collect Item
                    case KeyEvent.VK_C:
                        btnCollect.doClick();
                        break;
                    //X for Drop Item
                    case KeyEvent.VK_X:
                        btnDrop.doClick();
                        break;
                    //ESC key for quit the game
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        MainMenu menu = new MainMenu(game);
                        menu.setVisible(true);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel pnlContent = new javax.swing.JPanel();
        pnlIsland = new javax.swing.JPanel();
        javax.swing.JPanel pnlControls = new javax.swing.JPanel();
        javax.swing.JPanel pnlPlayer = new javax.swing.JPanel();
        javax.swing.JPanel pnlPlayerData = new javax.swing.JPanel();
        javax.swing.JLabel lblPlayerName = new javax.swing.JLabel();
        txtPlayerName = new javax.swing.JLabel();
        javax.swing.JLabel lblPlayerStamina = new javax.swing.JLabel();
        progPlayerStamina = new javax.swing.JProgressBar();
        javax.swing.JLabel lblBackpackWeight = new javax.swing.JLabel();
        progBackpackWeight = new javax.swing.JProgressBar();
        javax.swing.JLabel lblBackpackSize = new javax.swing.JLabel();
        progBackpackSize = new javax.swing.JProgressBar();
        lblPredators = new javax.swing.JLabel();
        lblKiwisCounted = new javax.swing.JLabel();
        txtKiwisCounted = new javax.swing.JLabel();
        txtPredatorsLeft = new javax.swing.JLabel();
        javax.swing.JPanel pnlMovement = new javax.swing.JPanel();
        btnMoveNorth = new javax.swing.JButton();
        btnMoveSouth = new javax.swing.JButton();
        btnMoveEast = new javax.swing.JButton();
        btnMoveWest = new javax.swing.JButton();
        javax.swing.JPanel pnlInventory = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlInventory = new javax.swing.JScrollPane();
        listInventory = new javax.swing.JList();
        btnDrop = new javax.swing.JButton();
        btnUse = new javax.swing.JButton();
        javax.swing.JPanel pnlObjects = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlObjects = new javax.swing.JScrollPane();
        listObjects = new javax.swing.JList();
        btnCollect = new javax.swing.JButton();
        btnCount = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        timerJpanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        seconds = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Kiwi Count");

        pnlContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlContent.setLayout(new java.awt.BorderLayout(10, 0));

        javax.swing.GroupLayout pnlIslandLayout = new javax.swing.GroupLayout(pnlIsland);
        pnlIsland.setLayout(pnlIslandLayout);
        pnlIslandLayout.setHorizontalGroup(
            pnlIslandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 614, Short.MAX_VALUE)
        );
        pnlIslandLayout.setVerticalGroup(
            pnlIslandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 566, Short.MAX_VALUE)
        );

        pnlContent.add(pnlIsland, java.awt.BorderLayout.CENTER);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        pnlPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder("Player"));
        pnlPlayer.setLayout(new java.awt.BorderLayout());

        pnlPlayerData.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlPlayerData.setLayout(new java.awt.GridBagLayout());

        lblPlayerName.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblPlayerName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        pnlPlayerData.add(lblPlayerName, gridBagConstraints);

        txtPlayerName.setText("Player Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlPlayerData.add(txtPlayerName, gridBagConstraints);

        lblPlayerStamina.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblPlayerStamina.setText("Stamina:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlPlayerData.add(lblPlayerStamina, gridBagConstraints);

        progPlayerStamina.setForeground(new java.awt.Color(0, 153, 51));
        progPlayerStamina.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlPlayerData.add(progPlayerStamina, gridBagConstraints);

        lblBackpackWeight.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblBackpackWeight.setText("Backpack Weight:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlPlayerData.add(lblBackpackWeight, gridBagConstraints);

        progBackpackWeight.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlPlayerData.add(progBackpackWeight, gridBagConstraints);

        lblBackpackSize.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblBackpackSize.setText("Backpack Size:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlPlayerData.add(lblBackpackSize, gridBagConstraints);

        progBackpackSize.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlPlayerData.add(progBackpackSize, gridBagConstraints);

        lblPredators.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblPredators.setText("Predators Left:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPlayerData.add(lblPredators, gridBagConstraints);

        lblKiwisCounted.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        lblKiwisCounted.setText("Kiwis Counted :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPlayerData.add(lblKiwisCounted, gridBagConstraints);

        txtKiwisCounted.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtKiwisCounted.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPlayerData.add(txtKiwisCounted, gridBagConstraints);

        txtPredatorsLeft.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        txtPredatorsLeft.setText("P");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlPlayerData.add(txtPredatorsLeft, gridBagConstraints);

        pnlPlayer.add(pnlPlayerData, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        pnlControls.add(pnlPlayer, gridBagConstraints);

        pnlMovement.setBorder(javax.swing.BorderFactory.createTitledBorder("Movement"));
        pnlMovement.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        pnlMovement.setLayout(new java.awt.GridBagLayout());

        btnMoveNorth.setBackground(new java.awt.Color(176, 255, 176));
        btnMoveNorth.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        btnMoveNorth.setText("N");
        btnMoveNorth.setFocusable(false);
        btnMoveNorth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveNorthActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMovement.add(btnMoveNorth, gridBagConstraints);

        btnMoveSouth.setBackground(new java.awt.Color(176, 255, 176));
        btnMoveSouth.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        btnMoveSouth.setText("S");
        btnMoveSouth.setFocusable(false);
        btnMoveSouth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveSouthActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMovement.add(btnMoveSouth, gridBagConstraints);

        btnMoveEast.setBackground(new java.awt.Color(176, 255, 176));
        btnMoveEast.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        btnMoveEast.setText("E");
        btnMoveEast.setFocusable(false);
        btnMoveEast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveEastActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMovement.add(btnMoveEast, gridBagConstraints);

        btnMoveWest.setBackground(new java.awt.Color(176, 255, 176));
        btnMoveWest.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        btnMoveWest.setText("W");
        btnMoveWest.setFocusable(false);
        btnMoveWest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveWestActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMovement.add(btnMoveWest, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        pnlControls.add(pnlMovement, gridBagConstraints);

        pnlInventory.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory"));
        pnlInventory.setLayout(new java.awt.GridBagLayout());

        listInventory.setBackground(new java.awt.Color(153, 200, 247));
        listInventory.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        listInventory.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listInventory.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listInventory.setVisibleRowCount(3);
        listInventory.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listInventoryValueChanged(evt);
            }
        });
        scrlInventory.setViewportView(listInventory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInventory.add(scrlInventory, gridBagConstraints);

        btnDrop.setBackground(new java.awt.Color(176, 255, 176));
        btnDrop.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnDrop.setText("Drop");
        btnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDropActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInventory.add(btnDrop, gridBagConstraints);

        btnUse.setBackground(new java.awt.Color(176, 255, 176));
        btnUse.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnUse.setText("Use");
        btnUse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInventory.add(btnUse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlControls.add(pnlInventory, gridBagConstraints);

        pnlObjects.setBorder(javax.swing.BorderFactory.createTitledBorder("Objects"));
        java.awt.GridBagLayout pnlObjectsLayout = new java.awt.GridBagLayout();
        pnlObjectsLayout.columnWidths = new int[] {0, 5, 0};
        pnlObjectsLayout.rowHeights = new int[] {0, 5, 0};
        pnlObjects.setLayout(pnlObjectsLayout);

        listObjects.setBackground(new java.awt.Color(153, 200, 247));
        listObjects.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        listObjects.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listObjects.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listObjects.setVisibleRowCount(3);
        listObjects.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listObjectsValueChanged(evt);
            }
        });
        scrlObjects.setViewportView(listObjects);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlObjects.add(scrlObjects, gridBagConstraints);

        btnCollect.setBackground(new java.awt.Color(176, 255, 176));
        btnCollect.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnCollect.setText("Collect");
        btnCollect.setToolTipText("");
        btnCollect.setMaximumSize(new java.awt.Dimension(61, 23));
        btnCollect.setMinimumSize(new java.awt.Dimension(61, 23));
        btnCollect.setPreferredSize(new java.awt.Dimension(61, 23));
        btnCollect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCollectActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlObjects.add(btnCollect, gridBagConstraints);

        btnCount.setBackground(new java.awt.Color(176, 255, 176));
        btnCount.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnCount.setText("Count");
        btnCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCountActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlObjects.add(btnCount, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlControls.add(pnlObjects, gridBagConstraints);

        pnlContent.add(pnlControls, java.awt.BorderLayout.EAST);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Current Weather:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("Sunny");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(159, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4))
        );

        jLabel4.getAccessibleContext().setAccessibleName("");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Step Counter:");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("0");
        jLabel1.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(jLabel1)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleParent(pnlPlayerData);

        timerJpanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Timer:");

        seconds.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        seconds.setText("00m:00s");
        seconds.setToolTipText("");

        javax.swing.GroupLayout timerJpanelLayout = new javax.swing.GroupLayout(timerJpanel);
        timerJpanel.setLayout(timerJpanelLayout);
        timerJpanelLayout.setHorizontalGroup(
            timerJpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerJpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(168, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timerJpanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(seconds)
                .addGap(75, 75, 75))
        );
        timerJpanelLayout.setVerticalGroup(
            timerJpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timerJpanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(seconds)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(timerJpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timerJpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContent.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(pnlContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMoveEastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveEastActionPerformed
        game.playerMove(MoveDirection.EAST);
    }//GEN-LAST:event_btnMoveEastActionPerformed

    private void btnMoveNorthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveNorthActionPerformed
        game.playerMove(MoveDirection.NORTH);
    }//GEN-LAST:event_btnMoveNorthActionPerformed

    private void btnMoveSouthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveSouthActionPerformed
        game.playerMove(MoveDirection.SOUTH);
    }//GEN-LAST:event_btnMoveSouthActionPerformed

    private void btnMoveWestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveWestActionPerformed
        game.playerMove(MoveDirection.WEST);
    }//GEN-LAST:event_btnMoveWestActionPerformed

    private void btnCollectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCollectActionPerformed
        Object obj = listObjects.getSelectedValue();
        game.collectItem(obj);
    }//GEN-LAST:event_btnCollectActionPerformed

    private void btnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDropActionPerformed
        game.dropItem(listInventory.getSelectedValue());
    }//GEN-LAST:event_btnDropActionPerformed

    private void listObjectsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listObjectsValueChanged
        Object occ = listObjects.getSelectedValue();
        if (occ != null) {
            btnCollect.setEnabled(game.canCollect(occ));
            btnCount.setEnabled(game.canCount(occ));
            listObjects.setToolTipText(game.getOccupantDescription(occ));
        }
    }//GEN-LAST:event_listObjectsValueChanged

    private void btnUseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseActionPerformed
        game.useItem(listInventory.getSelectedValue());

    }//GEN-LAST:event_btnUseActionPerformed

    private void listInventoryValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listInventoryValueChanged
        Object item = listInventory.getSelectedValue();
        btnDrop.setEnabled(true);
        if (item != null) {
            btnUse.setEnabled(game.canUse(item));
            listInventory.setToolTipText(game.getOccupantDescription(item));
        }
    }//GEN-LAST:event_listInventoryValueChanged

    private void btnCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCountActionPerformed
        game.countKiwi();
    }//GEN-LAST:event_btnCountActionPerformed

    /**
     * Creates and initialises the island grid.
     */
    private void initIslandGrid() {
        // Add the grid
        int rows = game.getNumRows();
        int columns = game.getNumColumns();
        // set up the layout manager for the island grid panel
        pnlIsland.setLayout(new GridLayout(rows, columns));
        // create all the grid square panels and add them to the panel
        // the layout manager of the panel takes care of assigning them to the
        // the right position
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                pnlIsland.add(new GridSquarePanel(game, row, col));
            }
        }
    }

    /**
     * Low Stamina Warning
     *
     * @author Minghao Yang
     */
    private void lowStaminaWarn() {
        //Get the current stamina
        double playerCurrentStamina = game.getPlayer().getStaminaLevel();
        if (playerCurrentStamina <= 50) {
            heatBeatAudio.loop();
        } else {
            if (heatBeatAudio != null) {
                heatBeatAudio.stop();
            }
        }
    }
    
    

    /**
     * Change the progPlayerStamina label's foreground color according to the
     * player's stamina
     *
     * @author Minghao Yang
     */
    private void changeStaminaColor() {
        //Get the current stamina
        double playerCurrentStamina = game.getPlayer().getStaminaLevel();
        if (playerCurrentStamina <= 100 && playerCurrentStamina >= 70) {
            progPlayerStamina.setForeground(new Color(0, 153, 51));
        } else if (playerCurrentStamina >= 40 && playerCurrentStamina < 70) {
            progPlayerStamina.setForeground(new Color(255, 153, 51));
        } else {
            progPlayerStamina.setForeground(Color.RED);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCollect;
    private javax.swing.JButton btnCount;
    private javax.swing.JButton btnDrop;
    private javax.swing.JButton btnMoveEast;
    private javax.swing.JButton btnMoveNorth;
    private javax.swing.JButton btnMoveSouth;
    private javax.swing.JButton btnMoveWest;
    private javax.swing.JButton btnUse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblKiwisCounted;
    private javax.swing.JLabel lblPredators;
    private javax.swing.JList listInventory;
    private javax.swing.JList listObjects;
    private javax.swing.JPanel pnlIsland;
    private javax.swing.JProgressBar progBackpackSize;
    private javax.swing.JProgressBar progBackpackWeight;
    private javax.swing.JProgressBar progPlayerStamina;
    private javax.swing.JLabel seconds;
    private javax.swing.JPanel timerJpanel;
    private javax.swing.JLabel txtKiwisCounted;
    private javax.swing.JLabel txtPlayerName;
    private javax.swing.JLabel txtPredatorsLeft;
    // End of variables declaration//GEN-END:variables
   /**
     * Set the heart beat sound
     */
    public void createHeartBeat() {
        URL heartBeatUrl = null;
        URI heartBeatUri;
        heartBeatUri = heartBeatFile.toURI();
        try {
            heartBeatUrl = heartBeatUri.toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(KiwiCountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        heatBeatAudio = Applet.newAudioClip(heartBeatUrl);
    }

    /**
     * Set the timer label
     *
     * @param text
     */
    public void setTime(String text) {
        this.seconds.setText(text);
    }

    
    private final static File heartBeatFile = new File("sound/heartbeat.wav");
    private AudioClip heatBeatAudio;
    private Timer timer;
    public Game game;

}
