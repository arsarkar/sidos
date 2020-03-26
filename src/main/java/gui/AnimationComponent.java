package gui;

import java.util.LinkedList;

import sweepAlgorithm.linesweep.Drawable;

public class AnimationComponent {

	static LinkedList<Drawable> components = new LinkedList<Drawable>();
	public AnimationComponent() {
		// TODO Auto-generated constructor stub
	}
	
	public void addDrawable(Drawable g) {
		// TODO Auto-generated method stub
		components.add(g);
	}

	public LinkedList<Drawable> getComponents() {
		// TODO Auto-generated method stub
		return components;
	}

}
