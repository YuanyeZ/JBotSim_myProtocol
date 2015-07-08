package jbotexample;

import jbotsim.Link;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.event.ConnectivityListener;
import jbotsim.event.TopologyListener;
import jbotsim.ui.JViewer;

public class LonerCentralized implements TopologyListener, 
                                         ConnectivityListener{
    public LonerCentralized(Topology tp){
        tp.addTopologyListener(this);
        tp.addConnectivityListener(this);
    }
    public void nodeAdded(Node node) { // TopologyListener
        node.setColor(node.hasNeighbors()?"red":"green");
    }
    public void nodeRemoved(Node node) { // TopologyListener
    }
    public void linkAdded(Link link) { // ConnectivityListener
        for (Node node : link.endpoints())
            node.setColor("red");
    }
    public void linkRemoved(Link link) { // ConnectivityListener
        for (Node node : link.endpoints())
            if (!node.hasNeighbors())
                node.setColor("green");
    }
    public static void main(String[] args) {
        Topology tp = new Topology();
        new LonerCentralized(tp);
        new JViewer(tp);
    }    
}    
