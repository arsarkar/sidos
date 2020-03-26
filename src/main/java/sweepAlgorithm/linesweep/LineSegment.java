package sweepAlgorithm.linesweep;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import algorithms.LineSweep;

import sweepAlgorithm.linesweep.Event.EventType;

/**
 * This class stores straight line with pointers for storing element in the status structure and 
 * event structure
 * @author Arko
 *
 */
public class LineSegment extends Drawable implements Comparable{

	private String tag;
	private Event start;            //Starting point of line
	private Event end;              //Ending point of line 
	private Line2D line;
	private LineEquation equation;
	private SweepLine sweep;

	public LineSegment(Line2D line, SweepLine sweep, String tag){
		this.start = new Event(EventType.start, line.getP1(), this);
		this.end =	new Event(EventType.end, line.getP2(), this);
		this.line = line;
		createEqution();
		this.sweep = sweep;
		this.tag = tag;
	}

	/**
	 * Stores start and end point in the order of the x axis and creates the line
	 * @param start
	 * @param end
	 */
	public LineSegment(Point2D start, Point2D end, SweepLine sweep, String tag) {
		if(LineSweep.isVertical){
			if(start.getX()>end.getX()){
				this.start = new Event(Event.EventType.start, end, this);
				this.end = new Event(Event.EventType.end, start, this);
			}
			else{
				this.start  = new Event(Event.EventType.start, start, this);
				this.end = new Event(Event.EventType.end, end, this);
			}
		}
		else{
			if(start.getY()>end.getY()){
				this.start = new Event(Event.EventType.start, end, this);
				this.end = new Event(Event.EventType.end, start, this);
			}
			else{
				this.start  = new Event(Event.EventType.start, start, this);
				this.end = new Event(Event.EventType.end, end, this);
			}
		}
		this.line = new Line2D.Double(start, end);
		createEqution();
		this.sweep = sweep;
		this.tag = tag;
	}

	public void createEqution(){
		double x1 = this.start.getPoint().getX();
		double y1 = this.start.getPoint().getY();
		double x2 = this.end.getPoint().getX();
		double y2 = this.end.getPoint().getY();
		double slope = (y2-y1)/(x2-x1);
		double yIntercept = (x2*y1-x1*y2)/(x2-x1);
		equation = new LineEquation(slope, yIntercept);
	}

	/**
	 * Static method to create a line segment randomly
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @return
	 */
	public static LineSegment createLineSegment(double minX, double maxX, double minY, double maxY, SweepLine sweep, String tag){
		double x1 = minX+Math.random()*maxX;
		double y1 = minY+Math.random()*maxY;
		double x2 = minX+Math.random()*maxX;
		double y2 = minY+Math.random()*maxY;
		return new LineSegment(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), sweep, tag);
	}

	/**
	 * Get Y intercept with the current sweep line 
	 * Should be called when it is absolutely sure that the line segment is in the range of the sweep line
	 * @param sweepLine
	 * @return
	 */
	private double getIntercept(){
		if(sweep.isVertical)
			return equation.getY(sweep.position);
		else
			return equation.getX(sweep.position);
	}


	public double getCorrectIntercept(){
		if(sweep.isVertical){
			if((start.getPoint().getX()<sweep.position &&
					end.getPoint().getX()>sweep.position) ||
					(start.getPoint().getX()>sweep.position &&
							end.getPoint().getX()<sweep.position))
				return equation.getY(sweep.position);
			else
				return java.lang.Double.POSITIVE_INFINITY;
		}
		else{
			if((start.getPoint().getY()<sweep.position &&
					end.getPoint().getY()>sweep.position) ||
					(start.getPoint().getY()>sweep.position &&
							end.getPoint().getY()<sweep.position))
				return equation.getX(sweep.position);
			else
				return java.lang.Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * Identify and create an event with the intersection point with line segment line
	 * @param line
	 * @return
	 */
	public Event getIntersection(LineSegment line){
		double m1 = this.equation.getSlope();
		double c1 = this.equation.getyIntercept();
		double m2 = line.getEquation().getSlope();
		double c2 = line.getEquation().getyIntercept();
		if(this.line.intersectsLine(line.getLine())){
			double x = (c2-c1)/(m1-m2);
			double y = (m1*c2-c1*m2)/(m1-m2);
			return new Event(Event.EventType.intersection, new Point2D.Double(x, y), this, line);
		}
		else
			return null;
	}

	public double getIntersection() {
		// TODO Auto-generated method stub
		double x1 = line.getX1();
		double x2 = line.getX2();
		double x3 = sweep.getLine().getX1();
		double x4 = sweep.getLine().getX2();
		double y1 = line.getY1();
		double y2 = line.getY2();
		double y3 = sweep.getLine().getY1();
		double y4  = sweep.getLine().getY2();
		double x12 = x1 - x2;
		double x34 = x3 - x4;
		double y12 = y1 - y2;
		double y34 = y3 - y4;

		double c = x12 * y34 - y12 * x34;

		if (Math.abs(c) < 0.01)
		{
			// No intersection
			return java.lang.Double.POSITIVE_INFINITY;
		}
		else
		{
			// Intersection
			double a = x1 * y2 - y1 * x2;
			double b = x3 * y4 - y3 * x4;

			double x = (a * x34 - b * x12) / c;
			double y = (a * y34 - b * y12) / c;

			return x;
		}
	}

	public boolean isIntersecting(Event point, boolean isPositive) {
//		Line2D spittedSweep = sweep.getSplit(point, isPositive);
		if (sweep.isVertical){
			if(line.getX1()<=point.getPoint().getX() && line.getX2()>point.getPoint().getX()){
				return true;
			}
			else
				return false;
		}
		else{
			if(line.getY1()<=point.getPoint().getY() && line.getY2()>point.getPoint().getY()){
				return true;
			}
			else
				return false;
		}
	}

	/**
	 * Get the closest one of two endpoints from sweep line
	 * @return
	 */
	public Event getNearestEndPoint(){
		double minDistance = Math.min(getStartDistance(), getEndDistance());
		if(minDistance == getStartDistance())
			return start;
		else
			return end;
	}

	/**
	 * Returns the distance of this line segment from sweep
	 * Average of distance of two end points of this line segment from sweep
	 * @return
	 */
	public double getSegmentDistance(){
		return ((getStartDistance()+getEndDistance())/2);
	}

	/**
	 * Returns the distance of start point from sweep line
	 * @return
	 */
	public double getStartDistance(){
		return sweep.getLine().ptLineDist(this.start.getPoint());
	}

	/**
	 * Returns the distance of end point from sweep line
	 * @return
	 */
	public double getEndDistance(){
		return sweep.getLine().ptLineDist(this.end.getPoint());
	}

	//Standard getters
	public Event getStart() {
		return start;
	}

	public Event getEnd() {
		return end;
	}

	public Line2D getLine() {
		return line;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public LineEquation getEquation(){
		return equation;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		LineSegment e = (LineSegment) obj;
		return this.line.equals(e.line);
	}

	/**
	 * Stores the eqation of the line in y=mx+c format
	 * @author Arko
	 *
	 */
	class LineEquation{

		private double slope;
		private double yIntercept;

		public LineEquation(double slope, double yIntercept) {
			super();	
			this.slope = slope;
			this.yIntercept = yIntercept;
		}

		public double getY(double x){
			return slope*x+yIntercept;
		}

		public double getX(double y){
			return (y-yIntercept)/slope;
		}

		public double getSlope() {
			return slope;
		}

		public double getyIntercept() {
			return yIntercept;
		}
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		LineSegment l = (LineSegment) o;
		if(this.getIntercept()>l.getIntercept())
			return 1;
		else if(this.getIntercept()<l.getIntercept())
			return -1;
		else 
			return 0;
	}
}
