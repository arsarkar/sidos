package nx;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Policy;

import nxopen.*;

/**
 * Starts and runs a remote NXSERVER on the host machine...
 * uses JAVA RMI with remote protocols defined by NXOpen
 * @author arko
 *
 */
public class NXRemoteServerImpl extends UnicastRemoteObject implements NXRemoteServer
{
	public static String SERVERNAME = "NXServer";
	private static int SERVERPORT = 1099;
    private Session theSession = null;
    private boolean isShutdownAllowed;
    private Registry registry;
    
    public NXRemoteServerImpl(boolean isShutDownAllowed) throws RemoteException
    {
        super();
        isShutdownAllowed = isShutDownAllowed;
        //Policy file
        PolicyFileLocator policyFile = new PolicyFileLocator();
        System.setProperty("java.security.policy", policyFile.getLocationOfPolicyFile(false));
        //Double checking the policy file
        System.out.println("policy : "+Policy.getPolicy());
    }
    
    /** Starts the server and binds it with the RMI registry */
    public void startServer() throws Exception
    {
        System.out.println("Starting Local registry");
        LocateRegistry.createRegistry(SERVERPORT);
        registry = LocateRegistry.getRegistry();
        System.out.println("Starting Server");
        NXRemotableObject.RemotingProtocol remotingProtocol = NXRemotableObject.RemotingProtocol.create();
        theSession = (Session)SessionFactory.get("Session", remotingProtocol);
        System.out.println("Got Session");
        theSession.listingWindow().open();
        theSession.listingWindow().writeLine("Binding Session");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
    	}
        registry.rebind(SERVERNAME, this);
        theSession.listingWindow().writeLine("Session bound");
        
        System.out.println("Server ready at host:port "+InetAddress.getLocalHost().getHostAddress()+":"+registry.REGISTRY_PORT);
    }
    
    public Session session() throws RemoteException, NXException { return theSession; }
  
    public boolean isShutdownAllowed() throws RemoteException { return isShutdownAllowed; }
    
    /**
     * Thread class to be used after object is unbound from registry
     * @author arko
     *
     */
    private class ShutdownThread extends Thread
    {
        public void run()
        {
            try
            {
                Thread.sleep(250);                
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                System.exit(0);
            }
        }
    }
    
    public void shutdown() throws RemoteException
    {
        if ( !isShutdownAllowed )
            throw new RemoteException("Shutdown not allowed");
        
        try
        {
            registry.unbind(SERVERNAME);
        }
        catch (Exception e)
        {
            throw new RemoteException("Exception during unbind", e);
        }
        finally
        {
            // We need to shut down the server after this method
            // has returned.  If we shut down before this method has
            // returned, the client will receive an exception.
            // So, we create a separate thread that will wait 
            // briefly and then shut down the server.
            (new ShutdownThread()).start();
        }
    }    
    
    public static void main(String[] args) throws Exception
    {
        NXRemoteServerImpl self = new NXRemoteServerImpl(true);
        self.startServer();
    }
}
