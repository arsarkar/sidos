package newTestBed;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.geom.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import algorithms.Algorithm;
import edu.ohiou.mfgresearch.labimp.draw.DrawWFApplet;
import edu.ohiou.mfgresearch.labimp.draw.DrawWFPanel;
import edu.ohiou.mfgresearch.labimp.draw.ImpObject;
import sweepAlgorithm.linesweep.Drawable;
import sweepAlgorithm.linesweep.LineSegment;
import sweepAlgorithm.linesweep.SweepLine;


public class TestBedv2 extends ImpObject implements Runnable{

	private volatile boolean running = false;    // stops the animation
	private volatile static Boolean paused = true;
	public volatile boolean inDrawingMode = false;
	private static int fps = 55;
	private static Thread thread;
	private static final long serialVersionUID = 2928262569408628430L;
	static JTextArea console; 
	//Variable for double buffering
	private Graphics2D dbg;
	private BufferedImage dbImage = null;
	int start  = 10;
	//Game Components
	private volatile Algorithm algorithm;
	public static double MaxX = 0;
	public static double MaxY = 0;
	public static double MinX = 0;
	public static double MinY = 0;
	
	//For drawing
	double startX = 0,
			startY = 0,
			endX = 0,
			endY = 0;
	ArrayList<Line2D> segments = new ArrayList<Line2D>();
	boolean lineStarted = false;
	Graphics g;		

	public TestBedv2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		panel  = new NewToolBoxPanel(this);
		panelOrientation = JSplitPane.HORIZONTAL_SPLIT;
		panel.setMinimumSize(new Dimension(300, 0));
		
	}

	
	public void startGame()
	{
		running = true;
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
		//		AnimationFrame.Utility.writeConsole("Pausing Execution temporarily");
	}

	/**
	 * This method will be called from peer for resuming paused game
	 */
	public void resumeGame(){
		if(paused){
			paused = false;
			//			AnimationFrame.Utility.writeConsole("Starting Execution...");
		}
	}

	public void setAlgorithm(Algorithm a){
		algorithm = a;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestBedv2 testBed  = new TestBedv2();
		DrawWFApplet applet = new DrawWFApplet(testBed){
			private static final long serialVersionUID = 2703039980933874051L;

			@Override
			public void start() {
				// TODO Auto-generated method stub
				JSplitPane contentPane = new JSplitPane();
				contentPane.setDividerLocation(600);
				contentPane.setDividerSize(5);
				if (target != null) {
					//        addPanel();
					contentPane.setOrientation(target.getPanelOrientation());
					contentPane.setRightComponent(target.geettPanel());
				} else {
					contentPane.setRightComponent(new JLabel(
						"This applet does not have target",
						SwingConstants.CENTER));
					contentPane.setOrientation(SwingConstants.HORIZONTAL);
				}
				console = new JTextArea();
				console.setBackground(Color.WHITE);
				console.setEditable(false);
				console.append("Please select a test type");
				JScrollPane consoleScroll = new JScrollPane(console);
				JSplitPane topPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,canvas,consoleScroll);
				topPanel.setDividerLocation(500);
				topPanel.setDividerSize(5);
				contentPane.setLeftComponent(topPanel);
				setContentPane(contentPane);
			}
			
		};
		thread = new Thread(testBed);
		thread.start();
		testBed.startGame();
		testBed.resumeGame();
		applet.display(JFrame.EXIT_ON_CLOSE);

		//Just set it manually for 2D drawing
		MaxX = 525;
		MaxY = 400;
	}
	
	public static void writeConsole(String message){
		console.append("\n"+message);
		console.setCaretPosition(console.getDocument().getLength());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long beforeTime, timeDiff, sleepTime;
		beforeTime = System.currentTimeMillis();
		while(running){
//			timeDiff = System.currentTimeMillis() - beforeTime;
//			sleepTime = 1000/fps - timeDiff;   // time left in this loop
//			if (sleepTime <= 35)  // update/render took longer than period
//				sleepTime = 35;    // sleep a bit anyway
			try {
				Thread.sleep(100);  // in ms
			}
			catch(InterruptedException ex){}
			beforeTime = System.currentTimeMillis();
			//Repaint the canvas...
			if(!paused)
				canvas.repaint();
		}
	}

//	@Override
//	public LinkedList<Shape> getShapeList(DrawWFPanel canvas) {
//		// TODO Auto-generated method stub
//		LinkedList<Shape> shapes = new LinkedList<Shape>();
//		if(algorithm!=null){
//			for(Drawable shape:algorithm.getTestShapes()){
//				if(shape instanceof LineSegment){
//					LineSegment line = (LineSegment) shape;
//					shapes.add(line.getLine());
//				}
//				else if(shape instanceof SweepLine){
//					SweepLine line = (SweepLine) shape;
//					dbg.setColor(Color.BLUE);
//					shapes.add(line.getLine());
//				}
//			}
//		}
//		shapes.add(new Line2D.Double(50,start,200,start));
//		return shapes;
//	}

	@Override
	public void makeShapeSets(DrawWFPanel canvas) {
		// TODO Auto-generated method stub
		if(algorithm!=null){
			for(Drawable shape:algorithm.getTestShapes()){
				if(shape instanceof LineSegment){
					LineSegment line = (LineSegment) shape;
					if(line.getTag().equals("partA"))
						canvas.addDrawShape(Color.RED, line.getLine());
					else if(line.getTag().equals("partB"))
						canvas.addDrawShape(Color.BLUE, line.getLine());
				}
				else if(shape instanceof SweepLine){
					SweepLine line = (SweepLine) shape;
					canvas.addDrawShape(Color.BLACK,line.getLine());
				}
			}
		}
		
	}

	
	
}
