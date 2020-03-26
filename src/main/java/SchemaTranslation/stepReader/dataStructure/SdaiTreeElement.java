package SchemaTranslation.stepReader.dataStructure;

import java.util.HashMap;

import jsdai.dictionary.EAttribute;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;
import jsdai.lang.SdaiModel;
import SchemaTranslation.stepReader.dataStructure.*;
import SchemaTranslation.entityHandlers.*;

public class SdaiTreeElement implements DefaultTreeElement<EEntity> {

	

	public EEntity unitStorage;
	public SdaiModel model; // to be removed later
	public SdaiHandler handler;
	
	public SdaiTreeElement(EEntity unitStorage, SdaiHandler handler) {
		super();
		this.unitStorage = unitStorage;
		this.handler = handler;
	}
	
	public SdaiTreeElement(SdaiModel model) {
		super();
		this.model = model;
	}
	
	@Override
	public String toString() {
		if(model==null)
			return unitStorage.toString();
		else
			return model.toString();
	}

	public boolean isEqualTo(EEntity element) {
		// TODO Auto-generated method stub
		try {
			if(unitStorage.compareValuesBoolean(element))
				return true;
			else
				return false;
		} catch (SdaiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public HashMap<String, Object> getAttributeValue() throws Exception {
		// TODO Auto-generated method stub
		HashMap<EAttribute, Object> attributeValues = handler.explicitAttribute();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for(EAttribute att:attributeValues.keySet()){
			attributes.put(att.getName(null), attributeValues.get(att));
		}
		return attributes;
	}

	public Object getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}
}
