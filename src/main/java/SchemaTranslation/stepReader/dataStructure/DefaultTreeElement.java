package SchemaTranslation.stepReader.dataStructure;

import java.util.HashMap;

public interface DefaultTreeElement<T> {
	
//	public T unitStorage;
//	public Class handler;
	
//	public DefaultTreeElement(T unitStorage, Class handler) {
//		super();
//		this.unitStorage = unitStorage;
//		this.handler = handler;
//	}

	public boolean isEqualTo(T element);
	
	public String toString();
	
	public HashMap<String, Object> getAttributeValue() throws Exception;
	
	public Object getHandler();
}
