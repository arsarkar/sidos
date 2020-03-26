package Painter;

/* 
 * Code by NomNom (24 Aug 2010)
 * modified by Bruceoutdoors(2 Aug 2012)
 * modified by Arkopaul (20 March 2013)
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.swing.*;

import algorithms.Algorithm;
import algorithms.PolygonDOF;

public class PolygonPainter implements Runnable
{
	PolygonDOF algorithm;

	public PolygonPainter(PolygonDOF algorithm){
		this.algorithm = algorithm;
	}



	public static void main(String[] args)
	{
		PaintWindow frame = new PaintWindow(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}



	public void run() {
		// TODO Auto-generated method stub
		PaintWindow frame = new PaintWindow(algorithm);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

}

class PaintWindow extends JFrame
{ 
	
	public PaintWindow(final PolygonDOF algorithm)
	{
		setTitle("Polygon Painter");
		setSize(525, 400);
		
		panel = new JPanel();
		drawPad = new PadDraw();
		
		panel.setPreferredSize(new Dimension(100, 68));

		//Creates a new container
		Container content = this.getContentPane();
		content.setLayout(new BorderLayout());

		//sets the panel to the left, padDraw in the center
		content.add(panel, BorderLayout.WEST);
		content.add(drawPad, BorderLayout.CENTER);

		//add the color buttons:
		makeColorButton(Color.black);
		makeColorButton(Color.blue);

		//creates the clear button
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				drawPad.clear();
				drawPad.started = false;
			}
		});
		panel.add(clearButton);

		//create Finish Button 
		JButton finishButton = new JButton("Finish");
		finishButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Point2D endPoint = drawPad.points.getFirst();
				drawPad.graphics2D.draw(new Line2D.Double(drawPad.startX, drawPad.startY, endPoint.getX(), endPoint.getY()));
				drawPad.partSegments.add(new Line2D.Double(drawPad.startX, drawPad.startY, endPoint.getX(), endPoint.getY()));
				drawPad.points.clear();
				drawPad.repaint();
			}
		});
		panel.add(finishButton);

		//create Export Button 
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				algorithm.generatePolygons(drawPad.partASegments, 1);
				algorithm.generatePolygons(drawPad.partBSegments, 2);
				closePainter();
			}
		});
		panel.add(exportButton);
	}
	
	public void closePainter(){
		setVisible(false);
		dispose();
	}
	
	/*
	 * makes a button that changes the color
	 * @param color the color used for the button
	 */
	public void makeColorButton(final Color color)
	{
		String iconName = "";
		if(color.equals(Color.black))
			iconName = "black.gif";
		else if(color.equals(Color.blue))
			iconName = "blue.gif";
		Icon icon = new ImageIcon(this.getClass().getResource(iconName));
		JButton tempButton = new JButton();
		tempButton.setIcon(icon);
		tempButton.setBackground(color);
		tempButton.setPreferredSize(new Dimension(16, 16));
		panel.add(tempButton);
		tempButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				drawPad.changeColor(color);
			}
		});
	}

	private JPanel panel;
	private PadDraw drawPad;
}

class PadDraw extends JComponent
{
	//this gonna be your image that you draw on
	Image image;
	//this is what we'll be using to draw on
	public Graphics2D graphics2D;
	//these gonna hold our mouse coordinates
	public LinkedList<Point2D> points = new LinkedList<Point2D>();
	public ArrayList<Line2D> partASegments =  new ArrayList<Line2D>();
	public ArrayList<Line2D> partBSegments =  new ArrayList<Line2D>();
	public ArrayList<Line2D> partSegments;
	public double endX, endY, startX, startY;
	public boolean started = false;


	public PadDraw()
	{
		setDoubleBuffered(false);



		addMouseListener(new MouseAdapter()
		{
			//if the mouse is pressed it sets the oldX & oldY
			//coordinates as the mouses x & y coordinates
			public void mousePressed(MouseEvent e)
			{
				if(started){
					endX = e.getX();
					endY = e.getY();
					points.add(new Point2D.Double(startX, startY));
					graphics2D.draw(new Line2D.Double(startX, startY, endX, endY));
					partSegments.add(new Line2D.Double(startX, startY, endX, endY));
					repaint();
					startX = endX;
					startY = endY;
				}
				else{
					startX = e.getX();
					startY = e.getY();
					points.add(new Point2D.Double(startX, startY));
					started = true;
				}
				System.out.println("("+e.getX()+","+e.getY()+")");
			}
		});

		//		addMouseMotionListener(new MouseMotionAdapter()
		//		{
		//			//while the mouse is dragged it sets currentX & currentY as the mouses x and y
		//			//then it draws a line at the coordinates
		//			//it repaints it and sets oldX and oldY as currentX and currentY
		//			public void mouseDragged(MouseEvent e)
		//			{
		//				endX = e.getX();
		//				endY = e.getY();
		//
		//				graphics2D.draw(new Line2D.Double(startX, startY, endX, endY));
		//				repaint();
		//
		//				startX = endX;
		//				startY = endY;
		//			}
		//		});
	}

	//this is the painting bit
	//if it has nothing on it then
	//it creates an image the size of the window
	//sets the value of Graphics as the image
	//sets the rendering
	//runs the clear() method
	//then it draws the image
	public void paintComponent(Graphics g)
	{
		if(image == null)
		{
			image = createImage(getSize().width, getSize().height);
			graphics2D = (Graphics2D)image.getGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			clear();
		}

		g.drawImage(image, 0, 0, null);
	}

	//this is the clear
	//it sets the colors as white
	//then it fills the window with white
	//thin it sets the color back to black
	public void clear()
	{
		graphics2D.setPaint(Color.white);
		graphics2D.fillRect(0, 0, getSize().width, getSize().height);
		graphics2D.setPaint(Color.black);
		repaint();
	}

	public void changeColor(Color theColor)
	{

		//Add gridline
		for(int i=0; i<=525; i=i+5){
			if(i%25==0){
				graphics2D.setPaint(Color.GRAY);
			}
			else{
				graphics2D.setPaint(Color.LIGHT_GRAY);
			}
			repaint();
			graphics2D.draw(new Line2D.Double(i, 0, i, 400));
			repaint();
		}
		for(int i=0; i<=400; i=i+5){
			if(i%25==0){
				graphics2D.setPaint(Color.GRAY);
			}
			else{
				graphics2D.setPaint(Color.LIGHT_GRAY);
			}
			repaint();
			graphics2D.draw(new Line2D.Double(0, i, 525, i));
			repaint();
		}

		if(theColor.equals(Color.black))
			partSegments = partASegments;
		else if(theColor.equals(Color.blue))
			partSegments = partBSegments;
		started = false;

		graphics2D.setPaint(theColor);
		repaint();
	}
} 
