/*****************************************************************
   * Panel for Final Project (Conway's Game of Life)
	 
	* @author Grace Lu & Minna Kuriakose
	* @version 6/03/15

	****************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
 

public class ConwaysGameOfLife extends JFrame implements ActionListener {
    private static final int BLOCK_SIZE = 10;
 //menu bar items
    private JMenuBar menu;
    private JMenu filebutton, gamebutton, helpbutton;
    private JMenuItem filebutton_options, filebutton_exit;
    private JMenuItem gamebutton_stop, gamebutton_reset, gamebutton_run, gamebutton_step;
    private JMenuItem helpbutton_instructions, helpbutton_about;
    private int i_movesPerSecond = 3;
    public GameBoard gameboard;
    public GameBoard2 gameboard2;
    public Thread game;
    public Thread game2;
 // setup menu 
    public ConwaysGameOfLife() {

      /************************************************************** 
   	* Creates the menu bar with drop-down options, also 
      * adds action listeners to appropriate buttons such as run and step
   	**************************************************************/
        menu = new JMenuBar();
        setJMenuBar(menu);
        filebutton = new JMenu("File");
        menu.add(filebutton);
        gamebutton = new JMenu("Game");
        menu.add(gamebutton);
        helpbutton = new JMenu("Help");
        menu.add(helpbutton);
        filebutton_options = new JMenuItem("Authors");
        filebutton_options.addActionListener(this);
        filebutton_exit = new JMenuItem("Exit");
        filebutton_exit.addActionListener(this);
        filebutton.add(filebutton_options);
        filebutton.add(new JSeparator());
        filebutton.add(filebutton_exit);
       
        gamebutton_run = new JMenuItem("Run");
        gamebutton_run.addActionListener(this);
        
        gamebutton_step = new JMenuItem("Step");
        gamebutton_step.addActionListener(this);
        
        gamebutton_stop = new JMenuItem("Stop");
        gamebutton_stop.setEnabled(false);
        gamebutton_stop.addActionListener(this);
        gamebutton_reset = new JMenuItem("Reset");
        gamebutton_reset.addActionListener(this);
        gamebutton.add(new JSeparator());
        
        gamebutton.add(gamebutton_step);
        gamebutton.add(gamebutton_run);
        
        gamebutton.add(gamebutton_stop);
        gamebutton.add(gamebutton_reset);
        helpbutton_instructions = new JMenuItem("Instructions");
        helpbutton_instructions.addActionListener(this);
        helpbutton_about = new JMenuItem("History of Game");
        helpbutton_about.addActionListener(this);
        helpbutton.add(helpbutton_instructions);
        helpbutton.add(helpbutton_about);
     
     /************************************************************* 
   	* Sets up the cell grid for the game by instantiating the
      * gameboards that will be used
     **************************************************************/
        gameboard = new GameBoard();//run
        add(gameboard);
        gameboard2 = new GameBoard2();//step
        add(gameboard2);
    }
 
    /************************************************************** 
     * Enable buttons corresponding to the game condition
     * @param isBeingPlayed true/false condition if the game is currently
     *                      in play. Also determines the action, as if
     *                      the game should start or stop based on param
     *                      and the gameboard
     **************************************************************/
    public void setGameBeingPlayed(boolean isBeingPlayed) {
        if (isBeingPlayed) {
             gamebutton_run.setEnabled(false);
             gamebutton_step.setEnabled(true);
             
            gamebutton_stop.setEnabled(true);
            game = new Thread(gameboard);
            game.start();
        } else {
            gamebutton_run.setEnabled(true);
            gamebutton_step.setEnabled(true);
            gamebutton_stop.setEnabled(false);
            game.interrupt();
        }
    }

     /************************************************************** 
     * Enable buttons corresponding the game condition
     * @param isBeingPlayed true/false condition if the game is currently
     *                      in play. Also determines the action, as if
     *                      the game should start or stop based on param
     *                      and the gameboard2 
     **************************************************************/

     public void setGameBeingPlayed2(boolean isBeingPlayed) {
        if (isBeingPlayed) {
            gamebutton_run.setEnabled(true);
            gamebutton_step.setEnabled(true);
            gamebutton_stop.setEnabled(true);

            game2 = new Thread(gameboard2);
            game2.start();

        } else {
            gamebutton_run.setEnabled(true);
            gamebutton_step.setEnabled(true);
            gamebutton_stop.setEnabled(false);
            game2.interrupt();
        }
    }

    /************************************************************** 
     * Signals actions for menu-bar presses for each button available
     * @param ae Information based off a mouse click. Will do different
     *           actions for each button 
     **************************************************************/
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(filebutton_exit)) {
            // Exit the game
            System.exit(0);
        } 
        else if(ae.getSource().equals(filebutton_options)){
            JOptionPane.showMessageDialog(null, "This version of Conway's Game of Life was made by Grace Lu and Minna Kuriakose");
         }else if (ae.getSource().equals(gamebutton_reset)) {
            gameboard.resetBoard();
            gameboard.repaint();
            gameboard2.resetBoard();
            gameboard2.repaint();
        } 
           else if ((ae.getSource().equals(gamebutton_step))){
            setGameBeingPlayed2(true); 
            }
            else if(  (ae.getSource().equals(gamebutton_run)))
            {
               setGameBeingPlayed(true);
               
              
        } else if (ae.getSource().equals(gamebutton_stop)) {
            setGameBeingPlayed(false);
        } 
         else if (ae.getSource().equals(helpbutton_instructions)) {
            JOptionPane.showMessageDialog(null, "Any live cell with less than two living neighbors dies" + "\n" + "Any live cell with two or three living neighbors lives on to the next generation" + "\n" + "Any live cell with more than three living neighbors dies" + "\n" + "Any dead cell with exactly three live neighbours becomes a living cell in the next generation" +
                                                "\n" + "To Play: Draw onto the gameboard and click either Run or Step under the Game button on the Menu" +
                                                "\n" + "(Hint: The Game Board is resizable by dragging the corner of the frame)");
         }else if(ae.getSource().equals(helpbutton_about)){
            JOptionPane.showMessageDialog(null, "This is a game of evolution, involving only a user-input before generating the outcome");
         }
    }
 
      /*****************************************************************
      * Gameboard class for Final Project (Conway's Game of Life)
	 
	   * Gameboard is a JPanel and implements ComponentListener, MouseListener
      * MouseMotionListerner, and Runnable. Is used for the run button to 
      * continuously jump through generations.
	   ****************************************************************/

     class GameBoard extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {
        private Dimension d_gameBoardSize = null; 
        private ArrayList<Point> point = new ArrayList<Point>(0);
 
     /************************************************************** 
     * Constructer for the gameboard, and it adds Component, Mouse,
     * and Mousemotion listeners
     **************************************************************/
        public GameBoard() {
            // Add resizing listener
            addComponentListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
 
     /************************************************************** 
     * Method to make the gameboard resizeable
     **************************************************************/
        private void updateArraySize() {
            ArrayList<Point> removeList = new ArrayList<Point>(0);
            for (Point current : point) {
                if ((current.x > d_gameBoardSize.width-1) || (current.y > d_gameBoardSize.height-1)) {
                    removeList.add(current);
                }
            }
            point.removeAll(removeList);
            repaint();
        }
 
     /************************************************************** 
     * It adds a point to the board using coordinates
     * @param x x-coordinate of point to be added
     * @param y y-coordinate of point to be added
     **************************************************************/
        public void addPoint(int x, int y) {
            if (!point.contains(new Point(x,y))) {
                point.add(new Point(x,y));
            } 
            repaint();
        }
 
     /************************************************************** 
     * Adds a point based on mouse input to the gameboard
     * @param me  location of point to be added
     **************************************************************/
        public void addPoint(MouseEvent me) {
            int x = me.getPoint().x/10-1;
            int y = me.getPoint().y/10-1;
            if ((x >= 0) && (x < d_gameBoardSize.width) && (y >= 0) && (y < d_gameBoardSize.height)) {
                addPoint(x,y);
            }
        }
 
     /************************************************************** 
     * Removes a point on the gameboard
     * @param x  x-coordinate of point to be removed
     * @param y  y-coordinate of point to be removed
     **************************************************************/
        public void removePoint(int x, int y) {
            point.remove(new Point(x,y));
        }
        
     /************************************************************** 
     * Clears the board of all living cells
     **************************************************************/
        public void resetBoard() {
            point.clear();
        }
 
     /************************************************************** 
     * It draws the grid and colors the active cells blue
     * @param g   Graphic of the board
     **************************************************************/
 
        @Override
        public void paintComponent(Graphics g) {
         d_gameBoardSize = new Dimension(getWidth()/8, getHeight()/8);
         updateArraySize();

            super.paintComponent(g);
            try {
                for (Point newPoint : point) {
                    // Draw new point
                    g.setColor(Color.blue); //COLOR
                    g.fillRect(BLOCK_SIZE + (BLOCK_SIZE*newPoint.x), BLOCK_SIZE + (BLOCK_SIZE*newPoint.y), BLOCK_SIZE, BLOCK_SIZE);
                }
            } catch (ConcurrentModificationException cme) {}
            // Setup grid
            g.setColor(Color.BLACK);
            for (int i=0; i<=d_gameBoardSize.width; i++) {
                g.drawLine(((i*BLOCK_SIZE)+BLOCK_SIZE), BLOCK_SIZE, (i*BLOCK_SIZE)+BLOCK_SIZE, BLOCK_SIZE + (BLOCK_SIZE*d_gameBoardSize.height));
            }
            for (int i=0; i<=d_gameBoardSize.height; i++) {
                g.drawLine(BLOCK_SIZE, ((i*BLOCK_SIZE)+BLOCK_SIZE), BLOCK_SIZE*(d_gameBoardSize.width+1), ((i*BLOCK_SIZE)+BLOCK_SIZE));
            }
        }
 
     /************************************************************** 
     * Resizes the gameboard based on user input of dragging corners
     * @param e  Event
     **************************************************************/
        @Override
        public void componentResized(ComponentEvent e) {
            // Setup the game board size with proper boundries
            d_gameBoardSize = new Dimension(getWidth()/BLOCK_SIZE-2, getHeight()/BLOCK_SIZE-2);
            updateArraySize();
        }
     /************************************************************** 
     * Moves the grid on mouse input to the gameboard
     * @param e  location of grid
     **************************************************************/
        @Override
        public void componentMoved(ComponentEvent e) {}
        @Override
     /************************************************************** 
     * Shows the location of the gameboard
     * @param e  location of grid
     **************************************************************/
        public void componentShown(ComponentEvent e) {}
      /************************************************************** 
     * Hides part of the grid
     * @param e  location of grid
     **************************************************************/
        @Override
        public void componentHidden(ComponentEvent e) {}
    /************************************************************** 
     * Registers a mouseclick on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseClicked(MouseEvent e) {}
     /************************************************************** 
     * Registers a mouse press from a mouse click on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mousePressed(MouseEvent e) {}
     /************************************************************** 
     * Registers a mouse release on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseReleased(MouseEvent e) {
            // Mouse was released (user clicked)
            addPoint(e);
        }
     /************************************************************** 
     * Registers a mouse input on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseEntered(MouseEvent e) {}
     /************************************************************** 
     * Un-Registered click of a mouse on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseExited(MouseEvent e) {}
    /************************************************************** 
     * Registers a drag of the mouse for multiple inputs on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseDragged(MouseEvent e) {
            addPoint(e);
        }
      /************************************************************** 
     * Registers a move of the mouse on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
        @Override
        public void mouseMoved(MouseEvent e) {}
     /************************************************************** 
     * Implements the rules of Conway's Algorithm and updates the board
     * and it continues the algorithm until stopped is pressed. 
     **************************************************************/
        @Override
        public void run() {
             d_gameBoardSize = new Dimension(getWidth()/8, getHeight()/8);
         updateArraySize();

            boolean[][] gameBoard;
            gameBoard = new boolean[d_gameBoardSize.width+2][d_gameBoardSize.height+2]; 
            for (Point current : point) {
                gameBoard[current.x+1][current.y+1] = true;
            }
            ArrayList<Point> survivingCells = new ArrayList<Point>(0);
            // Iterate through the array, follow game of life rules
            for (int i=1; i<gameBoard.length-1; i++) {
                for (int j=1; j<gameBoard[0].length-1; j++) {
                    int neighbor = 0;
                    if (gameBoard[i-1][j-1]) 
                    { neighbor++; }
                    if (gameBoard[i-1][j])   
                    { neighbor++; }
                    if (gameBoard[i-1][j+1]) 
                    { neighbor++; }
                    if (gameBoard[i][j-1])   
                    { neighbor++; }
                    if (gameBoard[i][j+1])   
                    { neighbor++; }
                    if (gameBoard[i+1][j-1]) 
                    { neighbor++; }
                    if (gameBoard[i+1][j])   
                    { neighbor++; }
                    if (gameBoard[i+1][j+1]) 
                    { neighbor++; }
                    if (gameBoard[i][j]) {
                        // Cell is alive, Can the cell live (2-3)
                        if ((neighbor == 2) || (neighbor == 3)) {
                            survivingCells.add(new Point(i-1,j-1));
                        } 
                    } else {
                        // Cell is dead, will the cell be given birth (3)
                        if (neighbor == 3) {
                            survivingCells.add(new Point(i-1,j-1));
                        }
                    }
                }
            }
            resetBoard();
            point.addAll(survivingCells);
            repaint();
            setGameBeingPlayed2(true);
            try {
                Thread.sleep(1000/i_movesPerSecond);
                run();
            } catch (InterruptedException ex) {}
            repaint();
        }
    }
    }
    /*****************************************************************
      * Gameboard class for Final Project (Conway's Game of Life)
	 
	   * Gameboard is a JPanel and implements ComponentListener, MouseListener
      * MouseMotionListerner, and Runnable. Is used for the run button to 
      * continuously jump through generations.
	   ****************************************************************/
    class GameBoard2 extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {
      private Dimension d_gameBoardSize = null;
     
      private ArrayList<Point> point = new ArrayList<Point>(0);
   
     /************************************************************** 
     * Constructer for the gameboard, and it adds Component, Mouse,
     * and Mousemotion listeners
     **************************************************************/
      public GameBoard2() {
         // Add resizing listener
         addComponentListener(this);
         addMouseListener(this);
         addMouseMotionListener(this);
      }
      /************************************************************** 
     * Method to make the gameboard resizeable
     **************************************************************/
      private void updateArraySize() {
         d_gameBoardSize = new Dimension(getWidth()/8, getHeight()/8);
       
         ArrayList<Point> removeList = new ArrayList<Point>(0);
         for (Point current : point) {
            if ((current.x > d_gameBoardSize.width-1) || (current.y > d_gameBoardSize.height-1)) {
               removeList.add(current);
            }
         }
         point.removeAll(removeList);
         repaint();
      }
      /************************************************************** 
     * It adds a point to the board using coordinates
     * @param x x-coordinate of point to be added
     * @param y y-coordinate of point to be added
     **************************************************************/

      public void addPoint(int x, int y) {
         if (!point.contains(new Point(x,y))) {
            point.add(new Point(x,y));
         } 
         repaint();
      }
      /************************************************************** 
     * Adds a point based on mouse input to the gameboard
     * @param me  location of point to be added
     **************************************************************/
      public void addPoint(MouseEvent me) {
         int x = me.getPoint().x/10-1;
         int y = me.getPoint().y/10-1;
         if ((x >= 0) && (x < d_gameBoardSize.width) && (y >= 0) && (y < d_gameBoardSize.height)) {
            addPoint(x,y);
         }
      }
       /************************************************************** 
     * Removes a point on the gameboard
     * @param x  x-coordinate of point to be removed
     * @param y  y-coordinate of point to be removed
     **************************************************************/
      public void removePoint(int x, int y) {
         point.remove(new Point(x,y));
      }
      /************************************************************** 
     * Clears the board of all living cells
     **************************************************************/
      public void resetBoard() {
         point.clear();
      }
       /************************************************************** 
     * It draws the grid and colors the active cells blue
     * @param g   Graphic of the board
     **************************************************************/
      @Override
      public void paintComponent(Graphics g) {
       d_gameBoardSize = new Dimension(getWidth()/8, getHeight()/8);
         updateArraySize();

         super.paintComponent(g);
         try {
            for (Point newPoint : point) {
               // Draw new point
               g.setColor(Color.blue);
               g.fillRect(10 + (10*newPoint.x), 10 + (10*newPoint.y), 10, 10);
            }
         } 
         catch (ConcurrentModificationException cme) {}
         // Setup grid
         g.setColor(Color.BLACK);
         for (int i=0; i< d_gameBoardSize.width; i++) {
            g.drawLine(((i*10)+10), 10, (i*10)+10, 10 + (10*d_gameBoardSize.height));
         }
         for (int i=0; i<=d_gameBoardSize.height; i++) {
            g.drawLine(10, ((i*10)+10), 10*(d_gameBoardSize.width+1), ((i*10)+10));
         }
      }
      /************************************************************** 
     * Resizes the gameboard based on user input of dragging corners
     * @param e  Event
     **************************************************************/
      @Override
      public void componentResized(ComponentEvent e) {
         // Setup the game board size with proper boundries
         d_gameBoardSize = new Dimension(getWidth()/8, getHeight()/8);
         updateArraySize();
      }
     /************************************************************** 
     * Moves the grid on mouse input to the gameboard
     * @param e  location of grid
     **************************************************************/
      @Override
      public void componentMoved(ComponentEvent e) {}
     /************************************************************** 
     * Shows the location of the gameboard
     * @param e  location of grid
     **************************************************************/
      @Override
      public void componentShown(ComponentEvent e) {}
       /************************************************************** 
     * Hides part of the grid
     * @param e  location of grid
     **************************************************************/
      @Override
      public void componentHidden(ComponentEvent e) {}
      /************************************************************** 
     * Registers a mouseclick on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mouseClicked(MouseEvent e) {}
       /************************************************************** 
     * Registers a mouse press from a mouse click on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mousePressed(MouseEvent e) {}
      /************************************************************** 
     * Registers a mouse release on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mouseReleased(MouseEvent e) {
         // Mouse was released (user clicked)
         addPoint(e);
      }
      /************************************************************** 
     * Registers a mouse input on the gameboard
     * @param e   Mouseevent 
     **************************************************************/ 
      @Override
      public void mouseEntered(MouseEvent e) {}
      /************************************************************** 
     * Un-Registered click of a mouse on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mouseExited(MouseEvent e) {}
      /************************************************************** 
     * Registers a drag of the mouse for multiple inputs on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mouseDragged(MouseEvent e) {
         // Mouse is being dragged, user wants multiple selections
         addPoint(e);
      }
        /************************************************************** 
     * Registers a move of the mouse on the gameboard
     * @param e   Mouseevent 
     **************************************************************/
      @Override
      public void mouseMoved(MouseEvent e) {}

      /************************************************************** 
     * Implements the rules of Conway's Algorithm and updates the board
     * and steps the generation with each button click. 
     **************************************************************/
      public void run() {
         boolean[][] gameBoard = new boolean[d_gameBoardSize.width+2][d_gameBoardSize.height+2];
         for (Point current : point) {
            gameBoard[current.x+1][current.y+1] = true;
            
         
         }
         ArrayList<Point> survivingCells = new ArrayList<Point>(0);
         // Iterate through the array, follow game of life rules
         for (int i=1; i<gameBoard.length-1; i++) {
            for (int j=1; j<gameBoard[0].length-1; j++) {
               int neighbor = 0;
               if (gameBoard[i-1][j-1]) 
               { neighbor++; }
               if (gameBoard[i-1][j])   
               { neighbor++; }
               if (gameBoard[i-1][j+1]) 
               { neighbor++; }
               if (gameBoard[i][j-1])   
               { neighbor++; }
               if (gameBoard[i][j+1])   
               { neighbor++; }
               if (gameBoard[i+1][j-1]) 
               { neighbor++; }
               if (gameBoard[i+1][j])   
               { neighbor++; }
               if (gameBoard[i+1][j+1]) 
               { neighbor++; }
               if (gameBoard[i][j]) {
                  // Cell is alive, Can the cell live (2-3)
                  if ((neighbor == 2) || (neighbor == 3)) {
                     survivingCells.add(new Point(i-1,j-1));
                  } 
               } 
               else {
                  // Cell is dead, will the cell be given birth(3)
                  if (neighbor == 3) {
                     survivingCells.add(new Point(i-1,j-1));
                  }
               }
            }
         }
         resetBoard();
         point.addAll(survivingCells);
         repaint();
         
       
      
      }    
   }

 