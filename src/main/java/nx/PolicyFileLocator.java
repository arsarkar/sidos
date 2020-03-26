package nx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class PolicyFileLocator {

	

	public String getLocationOfPolicyFile(boolean forClient) {
		File tempFile;
		String fileName = "";
		try {
			if(forClient){
				tempFile = File.createTempFile("rmi-nx_client", ".policy");
				fileName = "ClientPolicy.policy";
			}
			else{
				tempFile = File.createTempFile("rmi-nx_server", ".policy");
				fileName = "ServerPolicy.policy";
			}
			InputStream is = PolicyFileLocator.class.getResourceAsStream(fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			int read = 0;
			while((read = is.read()) != -1) {
				writer.write(read);
			}
			writer.close();
			tempFile.deleteOnExit();
			return tempFile.getAbsolutePath();
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}
