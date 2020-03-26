package SchemaTranslation.entityHandlers;

import java.util.HashMap;

import jsdai.dictionary.AExplicit_attribute;
import jsdai.dictionary.EAttribute;
import jsdai.dictionary.EEntity_definition;
import jsdai.dictionary.EExplicit_attribute;
import jsdai.lang.CEntity;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;
import jsdai.lang.SdaiIterator;

public class DefaultEntityHandler implements SdaiHandler{
	
	EEntity entity;
	
	public DefaultEntityHandler(EEntity entity) {
		super();
		this.entity  = entity;
		// TODO Auto-generated constructor stub
	}

	public String getEntityType() {
		// TODO Auto-generated method stub
		String s = entity.toString();
		int i = s.indexOf("=");
		int j = s.indexOf("(");
		return s.substring(i, j);
	}

	public String getInstanceNumber() {
		// TODO Auto-generated method stub
		String s = entity.toString();
		int i = s.indexOf("=");
		return s.substring(0, i);
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		try {
			return entity.getPersistentLabel();
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<EAttribute, Object> explicitAttribute() throws SdaiException{
		HashMap<EAttribute, Object> attributes = new HashMap<EAttribute, Object>();
		EEntity_definition entityDefinition = entity.getInstanceType(); 	
		AExplicit_attribute explicit_attributes = entityDefinition.getExplicit_attributes(null);
		SdaiIterator it = explicit_attributes.createIterator();
		while(it.next()){
			EExplicit_attribute explicitAttribute= explicit_attributes.getCurrentMember(it);
			attributes.put(explicitAttribute, entity.get_object(explicitAttribute));
		}
		return attributes;
	}

	public Object getAttributeValue(EAttribute attribute) throws SdaiException{
		return  entity.get_object(attribute);
	}
	
}
