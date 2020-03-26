package algorithms;

import gui.AnimationFrame;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.geom.*;
import newTestBed.*;

import sweepAlgorithm.linesweep.Drawable;
import sweepAlgorithm.linesweep.Event;
import sweepAlgorithm.linesweep.LineSegment;
import sweepAlgorithm.linesweep.SweepLine;

public class LineSweep implements Algorithm{

	ArrayList<LineSegment> segments = new ArrayList<LineSegment>();
	ArrayList<Event> removedEvents = new ArrayList<Event>();
	TreeSet<LineSegment> status = new TreeSet<LineSegment>(); //status structure
	PriorityQueue<Event> events = new PriorityQueue<Event>();
	SweepLine sweep;
	public static boolean isVertical; // to check whether it is a vertical or horizontal sweep algorithm
	private boolean saveSegment = false;

	public LineSweep(SweepLine sweep) {
		LineSweep.isVertical = sweep.isVertical;
		this.sweep = sweep;
	}

	public void setSaveSegment(boolean saveSegment){
		this.saveSegment=saveSegment;
	}

	/**
	 * Generate line segments and populate event structure with start and end events
	 * @param numberOfSegments
	 */
	public void generateSegments(int numberOfSegments){
		if(!saveSegment){
			if(!segments.isEmpty())
				segments.clear();
			segments.add(new LineSegment(new Point2D.Double(20, 0), new Point2D.Double(20, 400), sweep, "L"));
			// pass the reference of sweep line while creating segments
//			while(numberOfSegments>0){
//				segments.add(LineSegment.createLineSegment(TestBedv2.MinX+10, TestBedv2.MaxX-10, TestBedv2.MinY+10, TestBedv2.MaxX-10, sweep, "L"+numberOfSegments)); // Create randomly
//				numberOfSegments--;
//			}
		}

		//Initialize events with all segments
		if(!events.isEmpty())
			events.clear();
		for(LineSegment l:segments){
			events.add(l.getStart());
			events.add(l.getEnd());
		}
	}

	@Override
	public void execute() {
		LineSegment line;
		LineSegment leftSegment;
		LineSegment rightSegment;
		//Start Moving sweep to events till there is no active event
		if(!events.isEmpty()){
			//move to next active event in the priority queue
			//The head event is active because event structure is sorted by status 
			removedEvents.add(events.peek());
			Event event = events.poll();
			sweep.moveToEvent(event);
//			AnimationFrame.Utility.writeConsole("Sweep line moved to point "+event.toString());
			switch (event.getType()) {
			case start:
				
				//get the status reordered 
				LineSegment dummy = new LineSegment(new Point2D.Double(0,0), new Point2D.Double(5,5), sweep, "");
				status.add(dummy);
				status.remove(dummy);
				//insert segment in the status
				line = event.getSegment1();
				status.add(line);
				//make s and segment left of s as neighbors, calculate and insert intersection of them in event queue
				leftSegment = status.lower(line);
				if(leftSegment!=null){
					Event newIntersection = leftSegment.getIntersection(line);
					if(newIntersection!=null)
						events.add(newIntersection);
				}
				//make s and segment right of s as neighbors, calculate and insert intersection of them in event queue
				rightSegment = status.higher(line);
				if(rightSegment!=null){
					Event newIntersection = line.getIntersection(rightSegment);
					if(newIntersection!=null)
						events.add(newIntersection);
				}
				//delete the intersection of segments left and right of s from Q and add it to removed Event
				if(leftSegment!=null&&rightSegment!=null){
					Event removedEvent = leftSegment.getIntersection(rightSegment);
					if(removedEvent!=null){
						events.remove(removedEvent);
						removedEvents.add(removedEvent);
					}
				}
//				AnimationFrame.Utility.writeConsole(printEvents());
//				AnimationFrame.Utility.writeConsole(printStatus());
				break;
			case end:
				
				//get the status reordered 
				LineSegment dummy1 = new LineSegment(new Point2D.Double(0,0), new Point2D.Double(5,5), sweep, "");
				status.add(dummy1);
				status.remove(dummy1);
				//delete line segment from status
				line = event.getSegment1();
				status.remove(line);
				//calculate and insert intersection of segments left and right of s in Q
				leftSegment = status.lower(line);
//				AnimationFrame.Utility.writeConsole("leftSegment for 1 "+leftSegment.getTag());
				rightSegment = status.higher(line);
//				AnimationFrame.Utility.writeConsole("rightSegment for 1 "+rightSegment.getTag());
				if(leftSegment!=null&&rightSegment!=null){
					Event newIntersection = leftSegment.getIntersection(rightSegment);
					if(newIntersection!=null){
						if(!removedEvents.contains(newIntersection))
							events.add(newIntersection);
					}
				}
//				AnimationFrame.Utility.writeConsole(printEvents());
//				AnimationFrame.Utility.writeConsole(printStatus());
				break;
			case intersection:
//				AnimationFrame.Utility.writeConsole(printStatus());
				//Output the point as intersection
//				AnimationFrame.Utility.writeConsole("Intersection detected at "+event.toString());
				LineSegment line1 = event.getSegment1();
//				AnimationFrame.Utility.writeConsole("Line1 "+line1.getTag());
				LineSegment line2 = event.getSegment2();
//				AnimationFrame.Utility.writeConsole("Line2 "+line2.getTag());
				//make left and right segments of s1 as neighbors, calculate and insert intersection of them in Q
				leftSegment = status.lower(line1);
//				AnimationFrame.Utility.writeConsole("leftSegment for 1 "+leftSegment.getTag());
				rightSegment = status.higher(line1);
//				AnimationFrame.Utility.writeConsole("rightSegment for 1 "+rightSegment.getTag());
				if(leftSegment!=null&&rightSegment!=null){
					Event removedEvent = leftSegment.getIntersection(rightSegment);
					if(removedEvent!=null){
						if(!removedEvents.contains(removedEvent)){
							events.remove(removedEvent);
							removedEvents.add(removedEvent);
						}
					}
				}
				//make left and right segments of s2 as neighbors, calculate and insert intersection of them in Q
				leftSegment = status.lower(line2);
//				AnimationFrame.Utility.writeConsole("leftSegment for 2 "+leftSegment.getTag());
				rightSegment = status.higher(line2);
//				AnimationFrame.Utility.writeConsole("rightSegment for 2 "+rightSegment.getTag());
				if(leftSegment!=null&&rightSegment!=null){
					Event removedEvent = leftSegment.getIntersection(rightSegment);
					if(removedEvent!=null){
						if(!removedEvents.contains(removedEvent)){
							events.remove(removedEvent);
							removedEvents.add(removedEvent);
						}
					}
				}
				//delete the intersection of segments left of s1 and s1 from Q
				leftSegment = status.lower(line1);
				if(leftSegment!=null){
					Event newIntersection = leftSegment.getIntersection(line1);
					if(newIntersection!=null){
						if(!removedEvents.contains(newIntersection))
							events.add(newIntersection);
					}
				}
				//delete the intersection of segments right of s2 and s2 from Q
				rightSegment = status.higher(line2);
				if(rightSegment!=null){
					Event newIntersection = line2.getIntersection(rightSegment);
					if(newIntersection!=null){
						if(!removedEvents.contains(newIntersection))
							events.add(newIntersection);
					}
				}
//				AnimationFrame.Utility.writeConsole(printEvents());
//				AnimationFrame.Utility.writeConsole(printStatus());
				break;
			default:
				break;
			}
		}
	}

	private String printEvents() {
		// TODO Auto-generated method stub
		String s="";
		Iterator<Event> iter = events.iterator();
		while(iter.hasNext()){
			s=s+iter.next().getTag()+"\t";
		}
		return s;
	}

	private String printStatus(){
		String s="";
		for(LineSegment l:status){
			s=s+l.getTag()+"\t";
		}
		return s;
	}
	
	@Override
	public Drawable[] getTestShapes() {
		// TODO Auto-generated method stub
		Drawable[] d  = new Drawable[segments.size()+1];
		int count = 0;
		for(LineSegment l:segments){
			d[count] = l;
			count++;
		}
		d[count] = sweep;
		return d;
	}

}
