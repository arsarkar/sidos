package newTestBed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D.Double;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.java.swing.Painter;

import Painter.PolygonPainter;
import algorithms.Algorithm;
import algorithms.LineSweep;
import algorithms.PolygonDOF;
import newTestBed.TestBedv2;


import sweepAlgorithm.linesweep.SweepLine;

public class NewToolBoxPanel extends JPanel {

	public enum TestType{
		LineIntersection,
		PolygonDOF;
	}

	private static final long serialVersionUID = -4034304543659671886L;
	private static final int fillerHeight = 1;
	private boolean gamePaused = false;
	JPanel toolBox;
	Algorithm algorithm;
	TestBedv2 testBed;
	boolean inDrawingMode = false;


	public NewToolBoxPanel(TestBedv2 testBed) {
		this.testBed = testBed;
		setLayout(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Gap Generator");
		title.setTitleJustification(TitledBorder.LEFT);
		setBorder(title);
		addComponents();
	}

	private void addComponents() {

		final JPanel mainNorth = new JPanel();
		mainNorth.setLayout(new BoxLayout(mainNorth,BoxLayout.Y_AXIS));
		add(mainNorth, BorderLayout.NORTH);

		//add basic controls
		JPanel mainSouth = new JPanel();
		mainSouth.setLayout(new BoxLayout(mainSouth,BoxLayout.Y_AXIS));
		add(mainSouth, BorderLayout.SOUTH);
		final JButton executeButton = new JButton("Start Execution");
		executeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(algorithm !=null)
					algorithm.execute();
			}
		});
		mainSouth.add(createFields("", executeButton));
		final JButton drawFreeHand = new JButton("Start Drawing");
		drawFreeHand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(inDrawingMode){
					testBed.startGame();
					drawFreeHand.setText("Start Drawing");
					inDrawingMode = false;
					testBed.inDrawingMode = false;
				}
				else{
					testBed.stopGame();
					drawFreeHand.setText("Finish Drawing");
					inDrawingMode = true;
					testBed.inDrawingMode = true;
				}
			}
		});
		mainSouth.add(createFields("", drawFreeHand));

		JComboBox testType = new JComboBox();
		testType.setModel(new DefaultComboBoxModel(TestType.values()));
		testType.setSelectedItem(null);
		mainNorth.add(createFields("", new JLabel("Select Test")));
		mainNorth.add(createFields("", testType));
		//set different toolbox in mainNorth panel according to test type
		testType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				TestType tt = (TestType) cb.getSelectedItem();
				TestBedv2.writeConsole(tt.toString()+" selected");
				switch (tt) {
				case LineIntersection:
					TestBedv2.writeConsole("Sweep Line(blue) created for line sweep algorithm");
					algorithm = new LineSweep(new SweepLine(10, false));
					toolBox = new ToolBox1();
					break;
				case PolygonDOF:
					TestBedv2.writeConsole("Sweep Line(blue) created for Polygon DOF algorithm");
					algorithm = new PolygonDOF(new SweepLine(10, false));
					testBed.setAlgorithm(algorithm);
					toolBox = new ToolBox2();
					break;
				default:
					toolBox = new JPanel();

				}
				mainNorth.add(toolBox);
				mainNorth.revalidate();
				mainNorth.repaint();
			}
		});

	}

	private JPanel createFields(String name, JComponent component){

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel(name));
		panel.add(component);
		return panel;
	}

	public class ToolBox1 extends JPanel{

		LineSweep lineSweep;
		public ToolBox1() {
			super();
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			createComponents();
		}

		public void createComponents(){
			final JTextField numSegment = new JTextField("3",3);
			add(createFields("Number of Segments", numSegment));
			JButton generateSegment = new JButton("Generate segments");
			generateSegment.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					lineSweep = (LineSweep) algorithm;
					TestBedv2.writeConsole("Segments(black) created");
					lineSweep.generateSegments(Integer.parseInt(numSegment.getText()));
					testBed.setAlgorithm(algorithm);
				}
			});
			add(createFields("", generateSegment));
			JButton saveSegments = new JButton("Save segments");
			saveSegments.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					lineSweep = (LineSweep) algorithm;
					lineSweep.setSaveSegment(true);
				}
			});
			add(createFields("", saveSegments));
		}
	}

	public class ToolBox2 extends JPanel{

		PolygonDOF polyDOF;
		ArrayList<Line2D> linesA = new ArrayList<Line2D>();
		ArrayList<Line2D> linesB = new ArrayList<Line2D>();
		JTextField xTrans;
		JTextField yTrans;
		public ToolBox2() {
			super();
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			createComponents();
		}

		public void createComponents(){
			JButton generateSegment = new JButton("Create Polygons in paint console");
			generateSegment.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					polyDOF = (PolygonDOF) algorithm;
					TestBedv2.writeConsole("Opening painter");
					PolygonPainter painter = new PolygonPainter(polyDOF);
					Thread painterThread = new Thread(painter);
					painterThread.start();
				}
			});

			add(createFields("", generateSegment));

			//New File upload 
			xTrans = new JTextField("100",3);
			add(createFields("X translate", xTrans));
			yTrans = new JTextField("100",3);
			add(createFields("Y translate", yTrans));
			JButton uploadCSV = new JButton("Upload CSV");
			uploadCSV.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					polyDOF = (PolygonDOF) algorithm;
					uploadCSV();
				}
			});
			add(createFields("", uploadCSV));

			
			JButton translate = new JButton("Translate");
			translate.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					polyDOF.translateLines(java.lang.Double.parseDouble(xTrans.getText()), 
											java.lang.Double.parseDouble(yTrans.getText()));
				}
			});
			add(createFields("", translate));
		}

		private void uploadCSV(){

			File file;
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("C:\\Users\\sarkara1\\Dropbox\\Research\\Semantic integration of database modelling\\Development\\data\\"));
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				//This is where a real application would open the file.
				TestBedv2.writeConsole("Opening: " + file.getName() + ".");
				try {
					BufferedReader br = new BufferedReader( new FileReader(file));
					String line;
					while ((line = br.readLine()) != null) {

						// use comma as separator
						String[] tokens = line.split(",");
						double x1 = java.lang.Double.parseDouble(tokens[1]);
						if(tokens[0].equals("A")){
							linesA.add(new Line2D.Double(new Point2D.Double(java.lang.Double.parseDouble(tokens[1])+java.lang.Double.parseDouble(xTrans.getText()), 
																			java.lang.Double.parseDouble(tokens[2])+java.lang.Double.parseDouble(yTrans.getText())),
														 new Point2D.Double(java.lang.Double.parseDouble(tokens[3])+java.lang.Double.parseDouble(xTrans.getText()), 
																			java.lang.Double.parseDouble(tokens[4])+java.lang.Double.parseDouble(yTrans.getText()))));						}
						else if(tokens[0].equals("B")){
							linesB.add(new Line2D.Double(new Point2D.Double(java.lang.Double.parseDouble(tokens[1])+java.lang.Double.parseDouble(xTrans.getText()), 
																			java.lang.Double.parseDouble(tokens[2])+java.lang.Double.parseDouble(yTrans.getText())),
														 new Point2D.Double(java.lang.Double.parseDouble(tokens[3])+java.lang.Double.parseDouble(xTrans.getText()), 
																			java.lang.Double.parseDouble(tokens[4])+java.lang.Double.parseDouble(yTrans.getText()))));	
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				polyDOF.generatePolygons(linesA, 1);
				polyDOF.generatePolygons(linesB, 2);
			} else {
				TestBedv2.writeConsole("Open command cancelled by user.");
			}
		}
	}


}
