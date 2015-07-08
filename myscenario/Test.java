package myscenario;

import java.util.ArrayList;
import java.util.List;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;


public class Test{
	/**
	 * 
	 */
	public final static int LONG = 620;
	public final static int WIDE = 620;
	
	public static List<MeshRobot> neighborRobot = new ArrayList<MeshRobot>();
	
	public static void main(String[] args)
	{
		Node.setModel("MeshRobot",new MeshRobot());
		Node.setModel("MeshSensor",new MeshSensor());
		Node.setModel("Event",new Event());
				
		//set new topology
		Topology tp = new Topology(LONG, WIDE);
		//set sensor array
		for (int i = 0; i < 20; i++){
			for (int j = 0; j < 20; j++)
			tp.addNode(30 * i + 18 + Math.random() * 4, 
					30 * j + 18 + Math.random() * 4, new MeshSensor());
		}
		
		for (int i = 0; i < Integer.valueOf(args[0]).intValue(); i++){
			//tp.addNode(310, 310, new MeshRobot(i+1));
			tp.addNode(Math.random() * 560 + 30, Math.random() * 560 + 30, new MeshRobot(i+1));
		}
		
		//System.out.println("hi, add evnet~~~~~~~~~~~~~~~");
		tp.addNode(1000, 1000, new Event(2));
		//tp.addNode(1200, 1200, new Event(4));

		new JViewer(tp);	
	}

}
	
