package app;

// Import all Swing classes (like JFrame, JButton)
import javax.swing.*;

public class Main{
	

	    public static void main(String[] args) {

	        // Create the main window (JFrame) with a title
	        JFrame frame = new JFrame("main");

	        // Set the size of the window: width = 300, height = 200 pixels
	        frame.setSize(300, 200);

	        // Set what happens when the user closes the window
	        // EXIT_ON_CLOSE means the program will stop running when you close the window
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        // Create a button with the label "Click me!"
	        JButton button = new JButton("Click me!");

	        // Add the button to the frame (main window)
	        // By default, it goes to the center if no layout is specified
	        frame.add(button);

	        // Make the window visible on the screen
	        frame.setVisible(true);

	        // Add functionality to the button
	        // This code will run when the button is clicked
	        button.addActionListener(e -> {
	
	    
	 // This message will be printed in the console when button is clicked
        System.out.println("Button was clicked!");
    });
}
}