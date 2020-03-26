package SchemaTranslation.stepReader.dataStructure;

import java.util.ArrayList;

import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;
import SchemaTranslation.*;

public class Node<T extends DefaultTreeElement> {
	
	
	//Each node should  have one root Node and pointer to the children Node and one pointer to the parent
	//this can also be extended for multiple parent
	T element;
	Node<T> parent;
	ArrayList<Tree<T>> children;
	private Boolean isAssigned = false;
	private int position = 0;
	/**
	 * generic constructor
	 */
	public Node() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor
	 * @param element
	 */
	public Node(T element) {
		super();
		this.element = element;
		
	}
	
	public Boolean hasChild(){
		if(children==null)
			return false;
		else
			return true;
	}
	
	public Node<T> getParent(){
		return parent;
	}
	
	//always override this 
	public Boolean isEqualTo(Node<T> node) throws SdaiException{
		if(node.element.equals(element))  //compareValuesBoolean
			return true;
		else
			return false;
	}
	
	public void assignPosition(int position, boolean isCommon){
		this.position = position;
		this.isAssigned = true;
	}
	
	public Boolean isAssigned(){
		return isAssigned;
	}
	
	public int getPosition(){
		return position;
	}
	
	public String toString(){
		return element.toString();
	}

	public T getElement() {
		return element;
	}
}
