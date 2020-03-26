package SchemaTranslation.stepReader.dataStructure;

import java.util.ArrayList;
import java.util.TreeMap;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;

public class Tree<T extends DefaultTreeElement> {

	Node<T> rootElement;

	public Tree(){
		
	}
	
	public Tree(T rootValue) {
		super();
		rootElement = new Node<T>(rootValue);
	}

	public Tree(Node<T> root){
		super();
		rootElement = root;
	}
	
	public void addRoot(T rootValue){
		rootElement = new Node<T>(rootValue);
	}

	public Node<T> getRootElement() {
		return rootElement;
	}
	
	@Deprecated
	public void addElement(T parent, T element) throws SdaiException{
		Node<T> parentNode = getElement(parent);
		if(parentNode != null){
			Node<T> n = new Node<T>(element);
			n.parent = parentNode;
			Tree<T> newChild = new Tree<T>(n);
			if(parentNode.children == null){
				parentNode.children = new ArrayList<Tree<T>>();
			}
			parentNode.children.add(newChild);
		}
	}
	
	public void addElement(Node<T> parent, Node<T> element) throws SdaiException{
		if(parent != null){
			element.parent = parent;
			Tree<T> newChild = new Tree<T>(element);
			if(parent.children == null){
				parent.children = new ArrayList<Tree<T>>();
			}
			parent.children.add(newChild);
		}
	}
	

	
	public Node<T> getElement(T elementValue) throws SdaiException{
		Node<T> root = rootElement;
		Node<T> element = null;
		if(root.isEqualTo(new Node<T>(elementValue))){
			element =  root;
		}
		else{
			if(root.hasChild()){
				for(Object t:root.children){
					@SuppressWarnings("unchecked")
					Tree<T> tree = (Tree<T>)t;
					element = tree.getElement(elementValue);
					if(element!=null)
						break;
				}
			}
		}
		return element;
	}

	@Deprecated
	public ArrayList<Node<T>> getAllElements(){
		ArrayList<Node<T>> jobList = new ArrayList<Node<T>>();
		Node<T> root = rootElement;
		jobList.add(root);
		if(root.hasChild()){
			for(Tree<T> t:root.children){
				Tree<T> tree = (Tree<T>)t;
				jobList.addAll(tree.getAllElements());
			}
		}
		return jobList;
	}
	
	public ArrayList<Tree<T>> getAllChildren(){
		Node<T> root = rootElement;
		if(root.hasChild()){
			return root.children;
		}
		return null;
	}
	
	public String toString(String indentation){
		Node<T> root = rootElement;
		String prefix = "";
		String s="";
		if(indentation==null){
			indentation = "\t";
		}
		if(root.element!=null)
			s=s+indentation+root.element.toString()+"\n";
		else
			s=s+"\n";
		if(root.hasChild()){
			for(Tree t:root.children){
				s= s+ t.toString(indentation+"\t");
			}
		}
		return s;
	}

	class DefaultNode extends Node<T>{
		
	}


}
