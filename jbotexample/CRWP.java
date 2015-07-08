package jbotexample;

import java.awt.geom.Point2D;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ClockListener;
import jbotsim.ui.JViewer;

public class CRWP implements ClockListener{
	Topology tp;
	public CRWP (Topology tp){
		this.tp = tp;
		Clock.addClockListener(this, 4);
	}
	public void onClock(){
		for (Node n : tp.getNodes()){
			Point2D target = (Point2D)n.getProperty("target");
			if (target == null ||
				n.getLocation().distance(target) < 2) {
				target = new Point2D.Double(200, 150);
				n.setProperty("target", target);
				n.setDirection(target);
			}
			n.move(2);

		}
	}

	
	public static void main(String[] args) {
		Topology tp = new Topology (800, 600);
		
		for (int i = 0; i < 20; i ++){
			for (int j = 0; j < 20; j++)
				tp.addNode(40 * i, 30 * j);
		}
		new CRWP(tp);
		new JViewer(tp);

	}

}
