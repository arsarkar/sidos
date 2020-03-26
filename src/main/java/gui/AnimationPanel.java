package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JPanel;

import algorithms.Algorithm;
import algorithms.LineSweep;

import sweepAlgorithm.linesweep.Drawable;
import sweepAlgorithm.linesweep.LineSegment;
import sweepAlgorithm.linesweep.SweepLine;

public class AnimationPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -8129997977595498612L;

	private int PWIDTH;   // size of panel
	private int PHEIGHT;
	private static int fps;
	private static int upf;
	private Thread animator;            // for the animation
	/**
	 * A volatile modifier will prevent this variable to be copied to the local
	 * memory of the thread. Thus while we change the value of the variable
	 * it will reflect in the thread. 
	 * Without volatile modifier changing the global value will not affect the local copy 
	 * of the variable
	 */
	private volatile boolean running = false;    // stops the animation
	private volatile Boolean inDriveMode = true;   //For changing the state of the car
	private volatile static Boolean paused = true;

	//Variable for double buffering
	private Graphics2D dbg;
	private BufferedImage dbImage = null;
	private Image carImage;

	//Game Components
	private volatile Algorithm algorithm;

	public AnimationPanel(int fps, int upf, int width, int height){
		setBackground(Color.WHITE);
		this.fps = fps;
		setPreferredSize(new Dimension(width, height));
		this.PWIDTH = width;
		this.PHEIGHT = height;
	}

	public void setUPF(int upf){
		this.upf = upf;
	}

	@Override
	public void addNotify()
	/* Wait for the JPanel to be added to the
	     JFrame/JApplet before starting. */
	{
		super.addNotify();   // creates the peer
		startGame();         // start the thread
	}

	private void startGame()
	// initialize and start the thread
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} 

	/**
	 * THis method needs to be called from peer on close event or other stopping activity
	 */
	public void stopGame(){
		// called by the user to stop execution
		running = false;   
	}

	/**
	 * This method will be called from peer for pausing the game
	 */
	public void pauseGame(){
		paused = true;
		AnimationFrame.Utility.writeConsole("Pausing Execution temporarily");
	}

	/**
	 * This method will be called from peer for resuming paused game
	 */
	public void resumeGame(){
		if(paused){
			paused = false;
			AnimationFrame.Utility.writeConsole("Starting Execution...");
		}
	}

	public void setAlgorithm(Algorithm a){
		algorithm = a;
	}

	@Override
	public void run()
	/* Repeatedly update, render, sleep */
	{
		long beforeTime, timeDiff, sleepTime;
		beforeTime = System.currentTimeMillis();

		running = true;
		while(running) {
			gameUpdate();   // game state is updated
			gameRender();   // render to a buffer
			redraw();      // paint with the buffer

			timeDiff = System.currentTimeMillis() - beforeTime;
			sleepTime = 1000/fps - timeDiff;   // time left in this loop
			if (sleepTime <= 0)  // update/render took longer than period
				sleepTime = 5;    // sleep a bit anyway
			try {
				Thread.sleep(sleepTime);  // in ms
			}
			catch(InterruptedException ex){}
			beforeTime = System.currentTimeMillis();

		}
		System.exit(0);   // so enclosing JFrame/JApplet exits
	} // end of run()

	private void gameUpdate()
	{ 
		if(!paused){
			if(algorithm!=null){
				algorithm.execute();
				//Give time to execute
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void gameRender() {
		if (dbImage == null){  // create the buffer
			dbImage = new BufferedImage(PWIDTH, PHEIGHT, BufferedImage.TYPE_INT_ARGB);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else
				dbg = (Graphics2D) dbImage.getGraphics();
		}

		// clear the background
		dbg.setColor(Color.white);
		dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
		// draw game elements

		if(algorithm!=null){
			for(Drawable shape:algorithm.getTestShapes()){
				if(shape instanceof LineSegment){
					LineSegment line = (LineSegment) shape;
					Point2D start = line.getLine().getP1();
					dbg.setColor(Color.LIGHT_GRAY);
					dbg.drawString(line.getTag(), (float)start.getX()+5, (float)start.getY()-5);
					dbg.setColor(Color.BLACK);
					dbg.draw(line.getLine());
				}
				else if(shape instanceof SweepLine){
					SweepLine line = (SweepLine) shape;
					dbg.setColor(Color.BLUE);
					dbg.draw(line.getLine());
				}
			}
		}
	}

	private void redraw() {
		// TODO Auto-generated method stub
		Graphics g;
		try {
			g = this.getGraphics();  // get the panel's graphic context
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			/**
			 * sync the display on some systems like Linux, Which 
			 * has no default flush graphics option
			 */
			Toolkit.getDefaultToolkit().sync(); 
			g.dispose();
		}
		catch (Exception e)
		{ 
			System.out.println("Graphics context error: " + e);  
		}
	}

	public static class Utility{
		public static void controlCar(boolean startCar){
			paused = !startCar;
			if(startCar)
				AnimationFrame.Utility.writeConsole("Starting Car...");
			else
				AnimationFrame.Utility.writeConsole("Stopping Car...");
		}

		public static void setFPS(int fps1){
			fps = fps1;
		}
	}

}
