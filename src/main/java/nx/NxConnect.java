package nx;

import java.net.InetAddress;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import nxopen.Arc;
import nxopen.BasePart;
import nxopen.Body;
import nxopen.BodyCollection;
import nxopen.Curve;
import nxopen.Edge;
import nxopen.Ellipse;
import nxopen.Face;
import nxopen.IBaseCurve;
import nxopen.INXObject;
import nxopen.NXException;
import nxopen.NXObject;
import nxopen.PartSaveStatus;
import nxopen.Plane;
import nxopen.PlaneTypes;
import nxopen.Point3d;
import nxopen.SmartObject;
import nxopen.Vector3d;
import nxopen.NXObject.AttributeInformation;
import nxopen.NXObject.AttributeType;
import nxopen.Part;
import nxopen.PartCollection;
import nxopen.PartLoadStatus;
import nxopen.Session;
import nxopen.SessionFactory;
import nxopen.assemblies.Component;
import nxopen.assemblies.ComponentAssembly;
import nxopen.display.DynamicSectionBuilder;
import nxopen.features.FeatureCollection;
import nxopen.features.IntersectionCurveBuilder;

public class NxConnect {

	private Session session;
	private static int BINDTIMEOUT = 0;
	

	public NxConnect() throws Exception {
		session = (Session) SessionFactory.get("Session");	

	}

	public NxConnect(String serverName, String host, int port) throws Exception{
		session = lookupServer(serverName, host, port).session();
		PolicyFileLocator policyFile = new PolicyFileLocator();
		System.setProperty("java.security.policy", policyFile.getLocationOfPolicyFile(true));
	}

	public Session getSession() {
		return session;
	}


	/** Looks up the server in the RMI registry */
	private static NXRemoteServer lookupServer(String serverName, String host, int port) throws Exception
	{
		NXRemoteServer server = null;
		Registry r = LocateRegistry.getRegistry(host);	
		String name =  "//" +
				host +//":"+ port +
				"/"+serverName;
		System.out.println("Looking up name of server");
		int time = 0;
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager(){

				@Override
				public void checkConnect(String arg0, int arg1, Object arg2) {
					
				}

				@Override
				public void checkConnect(String arg0, int arg1) {
				
				}
            	
            });
        }
		// Look up the server.  Keep trying until it is found or
		// the amount of time we have tried exceeds the amount specified
		// in the property nxexamples.remoting.rmilookuptimeout
		do
		{   
			try
			{
				server = (NXRemoteServer)Naming.lookup(name);
			}
			catch ( NotBoundException e )
			{
				time += 1000;
				if ( time > BINDTIMEOUT )
					throw e;
				Thread.sleep(1000);
			}
			catch ( ConnectException e )
			{
				time += 1000;
				if ( time > BINDTIMEOUT )
					throw e;
				Thread.sleep(1000);
			}

		}
		while(server == null);
		System.out.println("Name of server found");
		return server;        
	}


	public static void main(String[] args) {

		ArrayList<Face> faceCol = new ArrayList<Face>();
		
		try {
			NxConnect connect = new NxConnect();



			//Get Part
			Session session = connect.getSession();
			session.parts().openBaseDisplay("C://Users//arko//workspace//SIDOSProject//parts//toycar_body.prt");
			Part workPart = session.parts().work();
			System.out.println("Working Part "+ workPart);

			//get Assembly
//			ComponentAssembly assembly = workPart.componentAssembly();
//			if(assembly.rootComponent()!=null){
//				Component[] children = assembly.rootComponent().getChildren();
//				for(Component c:children){
//					System.out.println(c.displayName());
//					workPart = (Part) c.prototype();
//					System.out.println("Part : "+workPart);
					
					BodyCollection bodyList = workPart.bodies();
					BodyCollection.Iterator itr;
					for (itr = bodyList.iterator(); itr.hasNext();)
					{ 
						
						Body body = (Body) itr.next();
						System.out.println("\tBody: "+body);
						Face faceArray[] = body.getFaces();
						for (int inx=0; inx <(int)faceArray.length; ++inx)
						{
							Face face = faceArray[inx];
							faceCol.add(face);
							System.out.println("\t\tFace :"+face);
							System.out.println("\t\tFace Type :"+face.solidFaceType());
							
								
								Edge[] edgeArray = face.getEdges();
								for(int inx1=0; inx1 <edgeArray.length; inx1++){
									Edge edge = edgeArray[inx1];
									
									System.out.println("\t\t\tEdge :"+edge);
									System.out.println("\t\t\tEdge Type :"+edge.solidEdgeType());
									Edge.VerticesData vertices = edge.getVertices();
									System.out.println("\t\t\t\tP1 :"+vertices.vertex1.x+","+
											vertices.vertex1.y+","+
											vertices.vertex1.z);
									System.out.println("\t\t\t\tP2 :"+vertices.vertex2.x+","+
											vertices.vertex2.y+","+
											vertices.vertex2.z);
									
									IBaseCurve curve = (IBaseCurve) edge.prototype();
//									if(edge.solidEdgeType().equals(Edge.EdgeType.CIRCULAR)){
//										Arc a = (Arc) curve;
//										System.out.println("\t\t\tArc Radius :"+a.radius()+" "+a.startAngle());
//									}
									
								}
							
//							AttributeInformation[] properties  = face.getAttributeTitlesByType();
//							for(AttributeInformation att:properties){
//								System.out.println("\t\t\tAttribute: "+att.toString());
//							}
						}
					}
//				}
//			}
			
			
			//start building intersectionbuilder
			Face[] faces = new Face[faceCol.size()];
			for(int i=0; i<faceCol.size(); i++){
				faces[i] = faceCol.get(i);
			}
			DynamicSectionBuilder sectionBuilder = workPart.dynamicSections().createSectionBuilder(workPart.modelingViews().workView());
//			IntersectionCurveBuilder curveBuilder = workPart.features().createIntersectionCurveBuilder(null);
//			curveBuilder.firstSet().add(faces);
			//set up section builder
			sectionBuilder.setSeriesSpacing(0.01);
			sectionBuilder.setOffset(20.88);
			NXObject object = sectionBuilder.commit();
//			NXObject[] commitedObjects = sectionBuilder.getCommittedObjects();
			sectionBuilder.destroy();
			PartSaveStatus status = workPart.saveAs("C://Users//arko//Desktop//toycar_body_mod1");
			System.out.println(status);
			status.dispose();
			
//			Point3d origin1 = new Point3d(0.0, 2.0, 0.0);
//			Vector3d normal1 = new Vector3d(0.0, 0.0, 1.0);
//			Plane plane1 = workPart.planes().createPlane(origin1, normal1, SmartObject.UpdateOption.WITHIN_MODELING);
//			plane1.setMethod(PlaneTypes.MethodType.FIXED_Y);
//			curveBuilder.setSecondPlane(plane1);
//			NXObject object = curveBuilder.commit();
//			NXObject[] resultObjects = curveBuilder.getCommittedObjects(); 

//			for(NXObject o:resultObjects){
//				System.out.println("curve "+o.toString());
//			}
			
//			//Get all bodies
//			BodyCollection bodyList = workPart.bodies();
//			BodyCollection.Iterator itr;
//			for (itr = bodyList.iterator(); itr.hasNext();)
//			{ 
//				
//				Body body = (Body) itr.next();
//				System.out.println(body);
//				Face faceArray[] = body.getFaces();
//				for (int inx=0; inx <(int)faceArray.length; ++inx)
//				{
//					Face face = faceArray[inx];
//					System.out.println("\t\tface"+face);
//					AttributeInformation[] properties  = face.getUserAttributes();
//					for(AttributeInformation att:properties){
//						System.out.println("\t\t\tAttribute: "+att.toString());
//					}
//				}
//			} 
			
			
		} catch (NXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
