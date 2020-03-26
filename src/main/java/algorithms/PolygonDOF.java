package algorithms;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import newTestBed.TestBedv2;

import sweepAlgorithm.linesweep.Drawable;
import sweepAlgorithm.linesweep.Event;
import sweepAlgorithm.linesweep.LineSegment;
import sweepAlgorithm.linesweep.SweepLine;

public class PolygonDOF implements Algorithm {

	//This two array list stores line segments from two different parts
	ArrayList<LineSegment> partA = new ArrayList<LineSegment>();
	ArrayList<LineSegment> partB = new ArrayList<LineSegment>();
	ArrayList<LineSegment> coveredSegments = new ArrayList<LineSegment>();
	ArrayList<Double> positiveDOF = new ArrayList<Double>();
	ArrayList<Double> negativeDOF = new ArrayList<Double>();
	SweepLine sweep = null;

	public PolygonDOF(SweepLine sweep) {
		this.sweep = sweep;
	}

	public void generatePolygons(ArrayList<Line2D> lines, int partType){
		TestBedv2.writeConsole("Importing shapes for part"+partType+" from painter");
		ArrayList<LineSegment> part = null;
		String lineTag = null;
		int i = 0;
		switch (partType) {
		case 1:
			part = partA;
			lineTag = "A";
			break;
		case 2:
			part = partB;
			lineTag = "B";
			break;
		default:
			break;
		}
		for(Line2D line:lines){
			TestBedv2.writeConsole(line.getX1()+"\t"+line.getX2()+"\t"+line.getY1()+"\t"+line.getY2());
			part.add(new LineSegment(line, sweep, lineTag+(i++)));
		}
	}
	
	public void translateLines(double x, double y){
		Line2D line = null;
		for(LineSegment l:partA){
			line = l.getLine();
			line = new Line2D.Double(new Point2D.Double(line.getX1()+x, line.getY1()+y), 
									new Point2D.Double(line.getX2()+x, line.getY2()+y));
		}
		for(LineSegment l:partB){
			line = l.getLine();
			line = new Line2D.Double(new Point2D.Double(line.getX1()+x, line.getY1()+y), 
									new Point2D.Double(line.getX2()+x, line.getY2()+y));
		}
	}

	@Override
	public Drawable[] getTestShapes() {
		// TODO Auto-generated method stub
		ArrayList<Drawable> d  = new ArrayList<Drawable>();
		Drawable dArray[];
		int count = 0;
		for(LineSegment l:partA){
			l.setTag("partA"); 
			d.add(l);
			count++;
		}
		for(LineSegment l:partB){
			l.setTag("partB"); 
			d.add(l);
			count++;
		}
		if(sweep!=null)
			d.add(sweep);
		dArray = new Drawable[d.size()];
		for(int i=0; i<d.size(); i++){
			dArray[i]=d.get(i);
		}
		return dArray;
	}

	@Override
	public void execute() {
		String direction;
		if(sweep.isVertical)
			direction = "Y";
		else
			direction = "X";
		boolean partADone = false;
		boolean partBDone = false;

		//Start with inner part sweep
		TestBedv2.writeConsole("-"+direction+" Clearance      partA           +"+direction+" Clearance");
		TestBedv2.writeConsole("-----------------------------------------");
//		while(!partADone){
			partADone = sweepPart(partA, partB);
//			if(partADone)
				//Reset sweep line
//				sweep = new SweepLine(10, false);
//		}

		coveredSegments.clear();

//		TestBedv2.writeConsole("-"+direction+" Clearance        partB         +"+direction+" Clearance");
//		TestBedv2.writeConsole("-----------------------------------------");
//
//		//Start Sweeping outer part
//		while(!partBDone){
//			partBDone = sweepPart(partB, partA);
//		}
	}

	private boolean sweepPart(ArrayList<LineSegment> sweepPart, ArrayList<LineSegment> intersectPart){
		double minDistance = Double.MAX_VALUE;
		LineSegment nearestLine = null;
		Event vertex = null;

		//get all vertex of sweep part in the Priority Queue
		Queue<Event> vertices = new PriorityQueue<Event>();
		for(LineSegment line:sweepPart){
			if(!vertices.contains(line.getStart())){
				vertices.add(line.getStart());
			}
//			if(!vertices.contains(line.getEnd())){
//				vertices.add(line.getEnd());
//			}
		}


		/*		This is another method but prefered the other way		
		//Calculate the closest vertex of the PartA polygon from sweep
		//the segment whose one end point is covered can be skipped because it's other end point 
		//should belong to another segment
		for(LineSegment line:sweepPart){
			if(!coveredSegments.contains(line)){
				double distance = Math.min(line.getStartDistance(), line.getEndDistance());
				if(distance<minDistance){
					minDistance = distance;
					nearestLine = line;
				}
			}
		}

		if(nearestLine==null)
			return true;
		 */
		//Move sweep line to the nearest vertex this is the main iteration block
		while(vertices.peek()!=null){
			vertex  = vertices.poll();
			sweep.moveToEvent(vertex);
			calculateIntersections(vertex, intersectPart);
		}

		return true;
	}

	private void calculateIntersections(Event point, ArrayList<LineSegment> intersectPart){
		boolean positive = true;
		boolean negative = true;
		for(LineSegment line:intersectPart){
			if (line.isIntersecting(point, true))
				positive = false;
			if (line.isIntersecting(point, false))
				negative = false;
		}
		TestBedv2.writeConsole(negative+"----------------"+positive);
		
//		double minPos = 99999.0;
//		double minNeg = -99999.0;
//		double zeroValue = 0.0;
//		//Calculate all intersection points with partB
//		for(LineSegment line:intersectPart){
//			double intercept = line.getIntersection();
//			if(intercept!=Double.POSITIVE_INFINITY){
//				intercept = getDistance(intercept, point);
//				if(intercept>= zeroValue){
//					if(intercept<minPos){
//						minPos = intercept;
//					}
//				}
//				else if(intercept<= zeroValue){
//					if(intercept>minNeg){
//						minNeg = intercept;
//					}
//				}
//			}
//		}
//		positiveDOF.add(minPos);
//		negativeDOF.add(minNeg);
//
//		//Report to console
//		DecimalFormat df = new DecimalFormat("#.##");
//		String negative;
//		String positive;
//		if(minNeg==-Double.MAX_VALUE)
//			negative = "clear";
//		else
//			negative = df.format(minNeg);
//		if(minPos==Double.MAX_VALUE)
//			positive = "clear";
//		else
//			positive = df.format(minPos);
//		TestBedv2.writeConsole(negative+"----------------"+positive);
	}

	private double getDistance(double intercept, Event point){
		if(sweep.isVertical){
			return intercept-point.getPoint().getY();
		}
		else{
			return intercept-point.getPoint().getX();
		}
	}

}
