package agents;

import jade.core.Agent;

/**
 * This Agent is responsible for managing schema...Each agent 
 * can own multiple schema but can work on only one active schema at a time
 * @author as888211
 *
 */
public class SchemaAgent extends Agent {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 5739849209532690240L;

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		System.out.println("SchemaAgent is active");
	}

}
