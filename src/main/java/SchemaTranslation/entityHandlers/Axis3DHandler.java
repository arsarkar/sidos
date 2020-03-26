package SchemaTranslation.entityHandlers;

import jsdai.SAic_topologically_bounded_surface.EAdvanced_face;
import jsdai.SGeometry_schema.EAxis2_placement_3d;
import jsdai.SGeometry_schema.ECartesian_point;
import jsdai.SGeometry_schema.EDirection;
import jsdai.SGeometry_schema.ESurface;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;

public class Axis3DHandler extends DefaultEntityHandler {

	public Axis3DHandler(EEntity entity) {
		super(entity);
		// TODO Auto-generated constructor stub
	}

	public String getLocation(){
		EAxis2_placement_3d position = (EAxis2_placement_3d) this.entity;
		try {
			ECartesian_point points3D = position.getLocation(null);
			return points3D.toString();
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getAxis(){
		EAxis2_placement_3d position = (EAxis2_placement_3d) this.entity;
		try {
			EDirection points3D = position.getAxis(null);
			return points3D.getDirection_ratios(null).toString();
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getRef_dir(){
		EAxis2_placement_3d position = (EAxis2_placement_3d) this.entity;
		try {
			EDirection points3D = position.getRef_direction(null);
			return points3D.getDirection_ratios(null).toString();
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
