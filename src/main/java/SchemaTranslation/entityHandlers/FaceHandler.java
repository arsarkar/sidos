package SchemaTranslation.entityHandlers;

import jsdai.SAic_topologically_bounded_surface.EAdvanced_face;
import jsdai.SGeometry_schema.ESurface;
import jsdai.STopology_schema.EFace;
import jsdai.lang.SdaiException;

public class FaceHandler extends DefaultEntityHandler{
	
	public FaceHandler(EFace shape) {
		super(shape);
		// TODO Auto-generated constructor stub
	}
	
	public String getFaceType(){
		EAdvanced_face face = (EAdvanced_face) this.entity;
		try {
			ESurface surface = face.getFace_geometry(null);
			return surface.getName(null);
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
