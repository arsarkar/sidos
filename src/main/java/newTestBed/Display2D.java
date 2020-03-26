package newTestBed;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.LinkedList;

import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import edu.ohiou.mfgresearch.labimp.draw.DrawWFApplet;
import edu.ohiou.mfgresearch.labimp.draw.DrawWFPanel;
import edu.ohiou.mfgresearch.labimp.draw.ImpObject;
import gui.ToolBoxPanel;

public class Display2D extends ImpObject implements Runnable{

	private static final long serialVersionUID = 2928262569408628430L;
	static JTextArea console; 
	int start  = 10;

	public Display2D() {
		// TODO Auto-generated constructor stub

		panelOrientation = JSplitPane.HORIZONTAL_SPLIT;
	}

	@Override
	public void init() {
//		panel  = new NewToolBoxPanel(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Display2D testBed  = new Display2D();
		DrawWFApplet applet = new DrawWFApplet(testBed);
		Thread t  = new Thread(testBed);
		t.start();
		applet.display();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			while(true){
				Thread.sleep(100);
				start++;
				canvas.repaint();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	@Override
	public LinkedList geetShapeList(DrawWFPanel canvas) {
		// TODO Auto-generated method stub
		LinkedList<Shape> shapes = new LinkedList<Shape>();
		shapes.add(new Line2D.Double(50,start,200,start));
		return shapes;
	}

}
