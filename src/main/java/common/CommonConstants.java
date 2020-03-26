/**
 * 
 */
package common;

import java.util.HashMap;

/**
 * @author as888211
 * This singleton class holds all constants required for SIDOS
 */
public final class CommonConstants {
	
	private static volatile CommonConstants instance = null;
	private HashMap<String, Object> constants = new HashMap<String, Object>();
	
	/**
	 * Constructor should not be called directly
	 */
	private CommonConstants() {
		constants.put("JSDAI_REPOSITORY_DIRECTORY_NAME", "repositories");
		constants.put("JSDAI_REPOSITORY_DIRECTORY_PATH", "C:\\Repositories");
		constants.put("JSDAI_REPOSITORY_PREFIX", "REPO_");
		constants.put("NXSERVER_NAME", "NXServer");
		constants.put("NXSERVER_PORT", "1099");
	}
	
	
	public static CommonConstants getCommonConstants(){
		if(instance==null){
			synchronized (CommonConstants.class) {
				instance = new CommonConstants();
			}
		}
		return instance;
	}
	
	public Object getConstant(String key){
		return constants.get(key);
	}
	
}
