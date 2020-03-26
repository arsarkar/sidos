package gui;

import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.net.UnknownHostException;

public class AnimationFrame extends JFrame implements WindowListener{

	private static final long serialVersionUID = -3108480733715691970L;
	public static final int DEFAULT_UPF = 2;
	public static final double MIN_X = 10;
	public static final double MAX_X = 690;
	public static final double MIN_Y = 10;
	public static final double MAX_Y = 490;
	
	static AnimationPanel animePanel;
	static JTextArea console; 
	
	public AnimationFrame(int fps){
		//Create basic layout
		animePanel = new AnimationPanel(fps, DEFAULT_UPF, 700, 500); 
		JPanel consolePanel = new JPanel(new BorderLayout());
		console = new JTextArea();
		console.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(console);
		console.setBackground(new Color(247, 213, 244));
		consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
		console.append("Please Select a test.");
		JSplitPane leftPanel =  new JSplitPane(JSplitPane.VERTICAL_SPLIT, animePanel, consolePanel);
		ToolBoxPanel rightPanel = new ToolBoxPanel(animePanel, this);
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		getContentPane().add(mainPanel);
		
		//Set the right panel toolbox
		
		
		//Set windows property
		setResizable(false);
		setTitle("Testbed for geometric computation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

	public AnimationPanel getAnimePanel(){
		return animePanel;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.stopGame();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.stopGame();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.pauseGame();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.resumeGame();
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.resumeGame();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		animePanel.pauseGame();
	}
	
	public static void main(String[] args){
		// TODO Auto-generated method stub
		AnimationFrame frame = new AnimationFrame(55);
		frame.setSize(1000, 700);
		frame.setVisible(true);
	}
	
	public static class Utility{
		public static void writeConsole(String message){
			console.append("\n"+message);
			console.setCaretPosition(console.getDocument().getLength());
		}
		public static void changeUPF(int upf){
			animePanel.setUPF(upf);
		}
	}
}