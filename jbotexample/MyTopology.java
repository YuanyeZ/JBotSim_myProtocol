package jbotexample;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;

public class MyTopology implements ClockListener{
	Topology tp;
	public MyTopology (Topology tp){
		this.tp = tp;
		Clock.addClockListener(this, 4);
	}
	public void onClock(){
		for (Node n : tp.getNodes()){
			n.move(0);

		}
	}

	
/*	public static void main(String[] args) {
		Topology tp = new Topology (860, 660);
		
		for (int i = 0; i < 20; i ++){
			for (int j = 0; j < 20; j++)
				tp.addNode(40 * i + 30, 30 * j + 30);
		}
		new MyTopology(tp);
		new JViewer(tp);

	}
*/
}
