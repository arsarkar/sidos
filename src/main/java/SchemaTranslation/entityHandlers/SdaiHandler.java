package SchemaTranslation.entityHandlers;

import java.util.HashMap;

import jsdai.dictionary.EAttribute;
import jsdai.lang.SdaiException;

/**
 * This interface guides the creation of handler for a particular Entity
 * @author as888211
 *
 */
public interface SdaiHandler {
	
	public String getEntityType();
	
	public String getInstanceNumber();
	
	public String getLabel();
	
	public HashMap<EAttribute, Object> explicitAttribute() throws SdaiException;
	
	public Object getAttributeValue(EAttribute attribute) throws SdaiException;

}
