package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import algorithms.Algorithm;
import algorithms.LineSweep;

import newTestBed.TestBedv2;


import sweepAlgorithm.linesweep.SweepLine;

public class ToolBoxPanel extends JPanel {

	public enum TestType{
		LineIntersection,
		StaticPosition;
	}

	private static final long serialVersionUID = -4034304543659671886L;
	private static final int fillerHeight = 1;
	private AnimationPanel animePanel;
	private boolean gamePaused = false;
	AnimationFrame frame;
	JPanel toolBox;
	Algorithm algorithm;
	TestBedv2 testBed;
	

	
	
	public ToolBoxPanel(TestBedv2 testBed) {
		this.testBed = testBed;
		setLayout(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Gap Generator");
		title.setTitleJustification(TitledBorder.LEFT);
		setBorder(title);
		addComponents();
	}

	public ToolBoxPanel(AnimationPanel animePanel, AnimationFrame animationFrame) {
		super();
		this.animePanel = animePanel;
		this.frame = animationFrame;
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
				animePanel.resumeGame();
				executeButton.setEnabled(false);
			}
		});
		mainSouth.add(createFields("", executeButton));

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
				AnimationFrame.Utility.writeConsole(tt.toString()+" selected");
				switch (tt) {
				case LineIntersection:
					AnimationFrame.Utility.writeConsole("Sweep Line(blue) created for line sweep algorithm");
					algorithm = new LineSweep(new SweepLine(10, false));
					toolBox = new ToolBox1();
					break;

				default:
					toolBox = new JPanel();
					
				}
				mainNorth.add(toolBox);
				mainNorth.revalidate();
				frame.validate();
				frame.repaint();
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
					AnimationFrame.Utility.writeConsole("Segments(black) created");
					lineSweep.generateSegments(Integer.parseInt(numSegment.getText()));
					animePanel.setAlgorithm(algorithm);
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
}
