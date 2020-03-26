package sweepAlgorithm.linesweep;

import java.awt.geom.Point2D;

import algorithms.LineSweep;


/**
 * This is a point event of type start / end / intersection
 * @author Arko
 *
 */
public class Event implements Comparable<Event>{

	public static enum EventType{
		start, 
		end,
		intersection;
	}

	private Point2D point;
	private EventType type;
	private LineSegment line1;
	private LineSegment line2;

	public Event(EventType type, Point2D point, LineSegment line) {
		this.type = type;
		this.point = point;
		this.line1 = line;
	}

	public Event(EventType type, Point2D point, LineSegment line1, LineSegment line2) {
		this.type = type;
		this.point = point;
		this.line1 = line1;
		this.line2 = line2;
	}

	public Point2D getPoint() {
		return point;
	}

	public EventType getType() {
		return type;
	}

	public LineSegment getSegment1(){
		return line1;
	}

	public LineSegment getSegment2(){
		return line2;
	}

	/**
	 * compared lexicographically 
	 * First it compares them by status deleted>inactive>active then
	 * for vertical sweep first by x and then y
	 * for horizontal sweep first by y and then x
	 */
	public int compareTo(Event o) {
		// TODO Auto-generated method stub

		if(LineSweep.isVertical){
			if(point.getX()>o.point.getX())
				return 1;
			else if(point.getX()<o.point.getX())
				return -1;
			else if(point.getX()==o.point.getX()){
//				if(point.getY()>o.point.getY())
//					return 1;
//				else if(point.getY()<o.point.getY())
//					return -1;
				return 0;
			}
		}
		else{
			if(point.getY()>o.point.getY())
				return 1;
			else if(point.getY()<o.point.getY())
				return -1;
			else if(point.getY()==o.point.getY()){
//				if(point.getX()>o.point.getX())
//					return 1;
//				else if(point.getX()<o.point.getX())
//					return -1;
				return 0;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new String("("+point.getX()+","+point.getY()+") of type "+type.toString());
	}
	
	public String getTag(){
		if(type.equals(EventType.intersection))
			return new String("I"+line1.getTag()+line2.getTag());
		else if(type.equals(EventType.start))
			return new String("S"+line1.getTag());
		else if(type.equals(EventType.end))
			return new String("E"+line1.getTag());
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Event e = (Event) obj;
		if(this.point.equals(e.getPoint())&&
				this.type.equals(e.getType()))
			return true;
		else
			return false;
	}

}
