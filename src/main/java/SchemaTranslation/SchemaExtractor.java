/**
 * 
 */
package SchemaTranslation;

import java.io.File;
import java.util.ArrayList;

import SchemaTranslation.stepReader.dataStructure.*;


/**
 * @author as888211
 *
 */
public interface SchemaExtractor{
	
	/**
	 * Method for loading schema/part file
	 * @param file
	 * @throws Exception 
	 */
	public void loadSchema(String schemaName, File file) throws Exception;

	/**
	 * Returns the current active schema
	 * @return
	 * @throws Exception 
	 */
	public String getCurrentSchema() throws Exception;
	
	/**
	 * Return current active model
	 * @return
	 * @throws Exception
	 */
	public Node<?> getCurrentModel() throws Exception;
	
	/**
	 * Change current schema to one of the available schemas
	 * @param schemaName
	 * @throws SdaiException
	 */
	public void changeCurrentSchema(String schemaName) throws Exception;
	
	/**
	 * Get 3D shapes from schema
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getShapes() throws Exception;
	
	/**
	 * Get items for 
	 * @param shape
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getFaces(DefaultTreeElement<?> shape) throws Exception;
	
	public ArrayList<Node> getSurfaceProperty(DefaultTreeElement<?> face) throws Exception;
	
	public ArrayList<Node> getFaceBounds(DefaultTreeElement<?> face) throws Exception;
	
	public ArrayList<Node> getEdgeLoops(DefaultTreeElement<?> faceBound) throws Exception;
	
	public ArrayList<Node> getEdgeGeometry(DefaultTreeElement<?> edgeLoop) throws Exception;
	

}
