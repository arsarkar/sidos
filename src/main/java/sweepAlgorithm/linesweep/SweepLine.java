package sweepAlgorithm.linesweep;

import gui.AnimationFrame;
import newTestBed.*;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

public class SweepLine extends Drawable{

	Line2D line;
	double position;                 //current position either x or y depends on orientation
	public boolean isVertical = true;
	
	public SweepLine() {
		this(0);
	}

	public SweepLine(double startPosition) {
		this.position = startPosition;
	}
	
	public SweepLine(double startPosition, boolean isVertical){
		this.position = startPosition;
		this.isVertical = isVertical;
		if(isVertical)
			line = new Line2D.Double(position, TestBedv2.MinY, position, TestBedv2.MaxY);
		else
			line = new Line2D.Double(TestBedv2.MinX, position, TestBedv2.MaxX, position);
	}
	
	public Line2D getLine(){
		return line;
	}
	
	/**
	 * Moves the sweep line to the specified event
	 * movement direction depends on the orientation
	 * @param event
	 */
	public void moveToEvent(Event event){
		if(isVertical){
			line.setLine(event.getPoint().getX(), line.getY1(), event.getPoint().getX(), line.getY2());
			position = event.getPoint().getX();
		}
		else{
			line.setLine(line.getX1(), event.getPoint().getY(), line.getX2(), event.getPoint().getY());
			position = event.getPoint().getY();
		}
	}

	public void increment(double position){
		if(isVertical){
			line.setLine(line.getX1()+position, line.getY1(), line.getX2()+position, line.getY2());
			position = line.getX1()+position;
		}
		else{
			line.setLine(line.getX1(), line.getY1()+position, line.getX2(), line.getY2()+position);
			position = line.getY1()+position;
		}
	}
	
	public SweepLine cloneLine(){
		// TODO Auto-generated method stub
		return new SweepLine(this.position, this.isVertical);
	}

	public boolean isCovered(Event intersection) {
		// TODO Auto-generated method stub
		if(isVertical){
			if(intersection.getPoint().getX()<=position)
				return true;
		}
		else{
			if(intersection.getPoint().getY()<=position)
				return true;
		}
		return false;
	}

	public Line2D getSplit(Event point, boolean isPositive) {
		if(isPositive){
			return new Line2D.Double(point.getPoint().getX(), point.getPoint().getY(), line.getX2(), line.getY2());
		}
		else{
			return new Line2D.Double(line.getX1(), line.getY1(), point.getPoint().getX(), point.getPoint().getY());
		}
	}
}
