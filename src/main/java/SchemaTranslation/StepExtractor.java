package SchemaTranslation;

import java.io.File;
import java.util.ArrayList;

import jsdai.SAic_advanced_brep.AAdvanced_brep_shape_representation;
import jsdai.SAic_advanced_brep.EAdvanced_brep_shape_representation;
import jsdai.SAic_topologically_bounded_surface.EAdvanced_face;
import jsdai.SGeometric_model_schema.EManifold_solid_brep;
import jsdai.SGeometry_schema.EAxis2_placement_3d;
import jsdai.SGeometry_schema.ECurve;
import jsdai.SGeometry_schema.EElementary_surface;
import jsdai.SGeometry_schema.ESurface;
import jsdai.SRepresentation_schema.ARepresentation_item;
import jsdai.SRepresentation_schema.ERepresentation_item;
import jsdai.STopology_schema.AFace;
import jsdai.STopology_schema.AFace_bound;
import jsdai.STopology_schema.AOriented_edge;
import jsdai.STopology_schema.EClosed_shell;
import jsdai.STopology_schema.EEdge;
import jsdai.STopology_schema.EEdge_curve;
import jsdai.STopology_schema.EEdge_loop;
import jsdai.STopology_schema.EFace_bound;
import jsdai.STopology_schema.ELoop;
import jsdai.STopology_schema.EOriented_edge;
import jsdai.lang.ASdaiModel;
import jsdai.lang.ASdaiRepository;
import jsdai.lang.SdaiException;
import jsdai.lang.SdaiIterator;
import jsdai.lang.SdaiModel;
import jsdai.lang.SdaiRepository;
import jsdai.lang.SdaiSession;
import jsdai.lang.SdaiTransaction;
import common.CommonConstants;
import SchemaTranslation.stepReader.dataStructure.*;
import SchemaTranslation.entityHandlers.*;
/**
 * This class reads a step file of automotive design(STEP214). This class provides methods
 * for extracting information from the STEP file.
 * This file is open for extension as for now only SHAPE_REPRESENTATION entity is explored for 
 * extracting geometrical data from STEP File
 * 
 * @see This Class operates on the STEP214 exported from UGS NX software. It is not guaranteed 
 * that it will work on other STEP. Please test and extend the class accordingly 
 * for accommodating other type of STEPS 
 * @author as888211
 *
 */
public class StepExtractor implements SchemaExtractor{

	//SDAI session and transaction
	SdaiSession session = null;
	SdaiTransaction transaction = null;

	/**
	 * Will store the current active repository
	 */
	SdaiRepository currentRepository;
	/**
	 * parameterless constructor for instantiating the class without the file name
	 * must use setStepFile method to add the step file 
	 * @throws SdaiException 
	 */
	public StepExtractor() throws SdaiException{

		//Create Properties
		java.util.Properties properties = new java.util.Properties();

		//Set working directory for storing repository
		properties.setProperty(CommonConstants.getCommonConstants().getConstant("JSDAI_REPOSITORY_DIRECTORY_NAME").toString(),
				CommonConstants.getCommonConstants().getConstant("JSDAI_REPOSITORY_DIRECTORY_PATH").toString());

		//Set property to SDAI session
		SdaiSession.setSessionProperties(properties);

		//open Session
		session = SdaiSession.openSession();

		//start read/write transaction
		transaction = session.startTransactionReadWriteAccess();
	}

	/**
	 * Creates a new repository and upload the STEP file
	 * @param stepFile
	 * @return
	 * @throws Exception 
	 * @throws SdaiException
	 */
	public void loadSchema(String schemaName, File stepFile) throws Exception{
		if(schemaName==null){
			schemaName = CommonConstants.getCommonConstants().getConstant("JSDAI_REPOSITORY_PREFIX").toString()+
					session.getActiveServers().getMemberCount()+1;
		}
		if(getRepository(schemaName)==null){
			if(stepFile!=null){
				//Create a new Repository and stores it in current repository
				try {
					currentRepository = session.importClearTextEncoding(schemaName, stepFile.getPath(), null);
				} catch (SdaiException e) {
					throw e;
				}

				//Validate file with the schema I don't know how?
				//		ASchemaInstance schemas = repository.getSchemas();
				//		SdaiIterator it = schemas.createIterator();
				//		boolean sameSchema = false;
				//		while(it.next()){
				//			if(schemas.getCurrentMember(it).getNativeSchema(). .equals(schemaDefinition)){
				//				sameSchema = true;
				//			}
				//		}
				//		if(!sameSchema){
				//			repository.deleteRepository();
				//			throw new Exception("Schema doesn't match!");
				//		}

				//commit imported data
				transaction.commit();
			}
		}
		else{
			throw new Exception("Repository already exists!");
		}

	}

	/**
	 * Get an Existing repository and open it
	 * Returns null if no repository is found matching
	 * @param repositoryName
	 * @return
	 * @throws SdaiException
	 */
	public SdaiRepository getRepository(String repositoryName) throws SdaiException{

		ASdaiRepository repositories = session.getKnownServers();
		SdaiIterator it = repositories.createIterator();
		while(it.next()){
			if(repositories.getCurrentMember(it).getName().equals(repositoryName)){
				return repositories.getCurrentMember(it);
			}
		}
		return null;
	}

	/**
	 * Get First model of a repository and start read only access on it
	 * @param repository
	 * @return
	 * @throws SdaiException
	 */
	public SdaiModel getModel() throws SdaiException{
		//get all models
		ASdaiModel models = currentRepository.getModels();

		//choose the first model
		SdaiModel model = models.getByIndex(1);

		//start read only access on the model
		if(model.getMode() == SdaiModel.NO_ACCESS){
			model.startReadOnlyAccess();
		}
		return model;
	}

	/**
	 * Extracts instances of entity advanced_brep_shape_representation  
	 * @see <a href="http://www.steptools.com/support/stdev_docs/express/ap203e2/html/t_advan-02.html">advanced_brep_shape_representation</a>
	 * @param model
	 * @return ArrayList<EAdvanced_brep_shape_representation>
	 * @throws Exception 
	 */
	public ArrayList<Node> getShapes() throws Exception{
		ArrayList<Node> shapeList =  new ArrayList<Node>();
		SdaiModel model = getModel();
		AAdvanced_brep_shape_representation brepShapes = 
				(AAdvanced_brep_shape_representation) model.getInstances(EAdvanced_brep_shape_representation.class);

		SdaiIterator it1 = brepShapes.createIterator();
		while(it1.next()){
			EAdvanced_brep_shape_representation brepShape = brepShapes.getCurrentMember(it1);
			shapeList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(brepShape, new ShapeHandler(brepShape))));
		}

		return shapeList;
	}

	/**
	 * Extracts instances of entity advanced_brep_shape_representation  
	 * @see <a href="http://www.steptools.com/support/stdev_docs/express/ap203e2/html/t_repre-01.html">advanced_brep_shape_representation</a>
	 * @param shape
	 * @return ArrayList<ERepresentation_item>
	 * @throws Exception
	 */
	public ArrayList<Node> getFaces(DefaultTreeElement shape) throws Exception{
		ArrayList<Node> faceList =  new ArrayList<Node>();
		SdaiTreeElement sdaiShape = (SdaiTreeElement) shape;
		EAdvanced_brep_shape_representation brepShape = (EAdvanced_brep_shape_representation) sdaiShape.unitStorage;
		ARepresentation_item items = 
				(ARepresentation_item) brepShape.getItems(null);

		SdaiIterator it1 = items.createIterator();
		while(it1.next()){
			ERepresentation_item item = items.getCurrentMember(it1);
			//supply condition for identifying different subtypes of Representation_item
			if(item instanceof EManifold_solid_brep){
				//For Manifold Solid
				EManifold_solid_brep manifoldSolid = (EManifold_solid_brep) item;
				//get outer attribute
				EClosed_shell shell = manifoldSolid.getOuter(null);
				//Get cfs_faces attribute
				AFace faces = shell.getCfs_faces(null);
				SdaiIterator it2 = faces.createIterator();
				while(it2.next()){
					//supply condition for identifying different subtypes of Face
					if(faces.getCurrentMember(it2) instanceof EAdvanced_face){
						//For Advanced_Face
						EAdvanced_face face = (EAdvanced_face) faces.getCurrentMember(it2);
						faceList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(face, new FaceHandler(face))));
					}
				}
			}
		}
		return faceList;
	}

	/**
	 * Extracts position attribute from instances of entity EAdvanced_face.
	 * @param face
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getSurfaceProperty(DefaultTreeElement face) throws Exception{
		ArrayList<Node> faceGeoList =  new ArrayList<Node>();
		SdaiTreeElement sdaiface = (SdaiTreeElement) face;
		EAdvanced_face advancedFace = (EAdvanced_face) sdaiface.unitStorage;
		//Get Face_geometry
		ESurface surface = advancedFace.getFace_geometry(null);
		//Only three type of subtypes defines all types surfaces. i.e Bounded_Surface, Elementary_surface, Swept_surface
		//position attribute is only defined in elementary_surface
		if(surface instanceof EElementary_surface){
			//get Position
			EAxis2_placement_3d position = ((EElementary_surface) surface).getPosition(null);
			faceGeoList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(position, new DefaultEntityHandler(position))));
		}
		return faceGeoList;
	}

	/**
	 * Extracts the faceBounds creating the facebounds 
	 * @param face
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getFaceBounds(DefaultTreeElement face) throws Exception{
		ArrayList<Node> faceBoundList =  new ArrayList<Node>();
		SdaiTreeElement sdaiface = (SdaiTreeElement) face;
		EAdvanced_face advancedFace = (EAdvanced_face) sdaiface.unitStorage;
		//Get list of face_bounds
		AFace_bound faceBounds = advancedFace.getBounds(null);
		SdaiIterator it1 = faceBounds.createIterator();
		while(it1.next()){
			EFace_bound faceBound = (EFace_bound) faceBounds.getCurrentMember(it1);
			//get the loop
			//			ELoop loop = faceBound.getBound(null);
			faceBoundList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(faceBound, new DefaultEntityHandler(faceBound))));
		}
		return faceBoundList;
	}

	/**
	 * Extracts list of edges baking a face_bound
	 * @param faceBound
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getEdgeLoops(DefaultTreeElement faceBound) throws Exception{
		ArrayList<Node> edgeLoopList =  new ArrayList<Node>();
		SdaiTreeElement sdaiFaceBound = (SdaiTreeElement) faceBound;
		EFace_bound faceOuterBound = (EFace_bound) sdaiFaceBound.unitStorage;
		//get the loop
		ELoop loop = faceOuterBound.getBound(null);
		if(loop instanceof EEdge_loop){
			EEdge_loop edgeLoop = (EEdge_loop) loop;
			//get edge_List
			AOriented_edge orientedEdges = edgeLoop.getEdge_list(null);
			SdaiIterator it1 = orientedEdges.createIterator();
			while(it1.next()){
				EOriented_edge orientedEdge = orientedEdges.getCurrentMember(it1);
				edgeLoopList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(orientedEdge, new DefaultEntityHandler(orientedEdge))));
			}
		}
		return edgeLoopList;

	}
	
	/**
	 * Extract the edge elemnent geometry from edgeLOop
	 * @param edgeLoop
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Node> getEdgeGeometry(DefaultTreeElement edgeLoop) throws Exception{
		ArrayList<Node> edgeGeometryList =  new ArrayList<Node>();
		SdaiTreeElement sdaiEdgeLoop = (SdaiTreeElement) edgeLoop;
		EOriented_edge orientedEdge = (EOriented_edge) sdaiEdgeLoop.unitStorage;
		//get the edge_element
		EEdge edge = orientedEdge.getEdge_element(null);
		if(edge instanceof EEdge_curve){
			EEdge_curve edgeCurve = (EEdge_curve) edge;
			//get Edge_geometry
			ECurve curve = edgeCurve.getEdge_geometry(null);
			edgeGeometryList.add(new Node<SdaiTreeElement>(new SdaiTreeElement(curve, new DefaultEntityHandler(curve))));
		}
		return edgeGeometryList;
	}
	
	public String getCurrentSchema() throws Exception {
		// TODO Auto-generated method stub
		return currentRepository.getName();

	}

	public Node getCurrentModel() throws Exception {
		// TODO Auto-generated method stub

		return new Node<SdaiTreeElement>(new SdaiTreeElement(getModel()));
	}

	public void changeCurrentSchema(String schemaName) throws SdaiException {
		// TODO Auto-generated method stub
		SdaiRepository currRepos = getRepository(currentRepository.getName());
		currRepos.closeRepository();
		SdaiRepository newRepos = getRepository(schemaName);
		newRepos.openRepository();
		currentRepository = newRepos;
	}


}
