/*****************************************************************
	* This driver is where the program runs from. It implements
   * the panel (Conway's Game of Life) to complete the task.
   *	 
	* @author Grace Lu & Minna Kuriakose
	* @version 6/03/15
	****************************************************************/
 
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.swing.*;
 
public class ConwaysGameOfLifeDriver  
{
    public static void main(String[] args) {
    
        // Creates the frame, sets it's size & location, allows it to close, 
        // add content, and allows the user to view it
        JFrame game = new ConwaysGameOfLife();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setTitle("Conway's Game of Life");
        game.setSize(800, 600);
        game.setLocation(460,200);
        game.setVisible(true);
    }
}