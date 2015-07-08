package jbotexample;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.TopologyListener;

public class SensorNode extends Node implements TopologyListener {

	public SensorNode(){
        setCommunicationRange(0);
        setProperty("icon", "/robot.png");
        setProperty("size", 20);
	}
	public void onTopologyAttachment(Topology tp){
        tp.addTopologyListener(this);
    }
    public void onTopologyDetachment(Topology tp){
        tp.removeTopologyListener(this);
    }
    public void nodeAdded(Node node) { // TopologyListener
        if (node.getClass()==HighwayTower.class)
            connect(this.getTopology(), this, node);
    }
    public void nodeRemoved(Node node) { // TopologyListener
    }
    protected static void connect(Topology tp, Node n1, Node n2){
        tp.addLink(new Link(n1, n2, Link.Type.UNDIRECTED, Link.Mode.WIRED));
    }

}
