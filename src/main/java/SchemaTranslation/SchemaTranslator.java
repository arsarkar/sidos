package SchemaTranslation;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import SchemaTranslation.stepReader.dataStructure.*;
import SchemaTranslation.entityHandlers.*;

/**
 * This Class is responsible for instantiating a particular type of extractor depending 
 * on the schema type. e.g. StepExtractor for STEP files (In reality right now it can extract only from STEP)
 * This class also stores the extracted data into a tree structure and supports query in it
 * This class also has capability to translate the data into different formats
 * e.g. XML, Text, Table, CSV etc.
 * @author as888211
 *
 */
public class SchemaTranslator implements Serializable{
	
	/**
	 * Version ID
	 */
	private static final long serialVersionUID = 2778614734433227603L;
	
	public enum EXPORT_OPTIONS{CSV, XML, TREE};
	
	/**
	 * This data structure stores all instances extracted from schema
	 */
	HashMap<String, Tree> treeList;
	
	/**
	 * This is the extractor to be used
	 */
	SchemaExtractor extractor;
	
	/**
	 * Can only work on one schema at a time
	 */
	String currentSchema;
	
	/**
	 * Instantiate the class with a correct extractor
	 * @param extractor
	 */
	public SchemaTranslator(SchemaExtractor extractor) {
		this.extractor = extractor;
		treeList = new HashMap<String, Tree>();
		
	}
	
	/**
	 * Loads a new Schema and stores it in the existing schemaList
	 * Then it populates the tree with the current schema
	 * @param schemaName
	 * @param file
	 * @throws Exception
	 */
	public void loadSchema(String schemaName, File file) throws Exception{
		
		// Load schema file 
		extractor.loadSchema(schemaName, file);
		this.currentSchema = schemaName;
		
		//Make entry in the data structure
		Tree tree = new Tree(extractor.getCurrentModel());
		treeList.put(currentSchema, tree);
		
		// Populates the tree
		//Get Shapes
		ArrayList<Node> shapes = extractor.getShapes();
		for(Node shape:shapes){
			tree.addElement(tree.getRootElement(), shape);
			//Get Faces for each shape
			ArrayList<Node> faces = extractor.getFaces(shape.getElement());
			for(Node face:faces){
				tree.addElement(shape, face);
				//Get Surface properties for each face
				ArrayList<Node> surfaces = extractor.getSurfaceProperty(face.getElement());
				for(Node surface:surfaces){
					tree.addElement(face, surface);
				}
				//get Face bounds
				ArrayList<Node> faceBounds = extractor.getFaceBounds(face.getElement());
				for(Node faceBound:faceBounds){
					tree.addElement(face, faceBound);
					//Get EdgeLOops
					ArrayList<Node> edgeLoops = extractor.getEdgeLoops(faceBound.getElement());
					for(Node edgeLoop:edgeLoops){
						tree.addElement(faceBound, edgeLoop);
						//Get edge Elements
						ArrayList<Node> edgeElements = extractor.getEdgeGeometry(edgeLoop.getElement());
						for(Node edgeElement:edgeElements){
							tree.addElement(edgeLoop, edgeElement);
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * Simply return the tree for the current schema
	 * @return
	 */
	public Object getTranslatedSchema(EXPORT_OPTIONS exportFormat){
		switch (exportFormat) {
		case CSV:
			return exportCSV(treeList.get(currentSchema));
		case XML:
			// implement later
			break;	
		case TREE:
			return treeList.get(currentSchema);
		default:
			return null;
		}
		return null;
	}
	
	private String exportCSV(Tree tree){
		String s="";
		ArrayList<Tree> shapes = tree.getAllChildren();
		//Loop though all the Shapes 
		try {
		for(Tree n:shapes){
			DefaultTreeElement d = n.getRootElement().getElement();
			//Name, Text
			s = s+d.getAttributeValue().get("name").toString()+
						","+d.getAttributeValue().get("text").toString()+",";
			//Loop through all the faces 
			ArrayList<Tree> faces = n.getAllChildren();
			for(Tree n1:faces){
				DefaultTreeElement d1 = n1.getRootElement().getElement();
				FaceHandler handler = (FaceHandler) d1.getHandler();
				s = s+handler.getFaceType()+",";
				//Loop through all the suface positions
				ArrayList<Tree> facePositions = n1.getAllChildren();
				for(Tree n2:facePositions){
					DefaultTreeElement d2 = n2.getRootElement().getElement();
					Axis3DHandler handler1 = (Axis3DHandler) d2.getHandler();
					s=s+handler1.getLocation()+","
							+handler1.getAxis()+","
							+handler1.getRef_dir()+",";
					
				}
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Returns all schemas available to this translator
	 * @return
	 */
	public ArrayList<String> getAllSchemas(){
		
		ArrayList<String> schemas = new ArrayList<String>();
		
		for(String key:treeList.keySet()){
			schemas.add(key);
		}
		
		return schemas;
	}
	
	/**
	 * Loads a new Schema from available schemas
	 * @param schemaName
	 * @throws Exception 
	 */
	public void changeSchema(String schemaName) throws Exception{
		extractor.changeCurrentSchema(schemaName);
		this.currentSchema = schemaName;
	}
	
}

