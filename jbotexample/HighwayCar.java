package jbotexample;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.ui.JViewer;
import jbotsim.Topology;

public class HighwayCar extends Node implements ClockListener{
    double speed = 3 + Math.random() * 2.0; // Random speed between 3 and 5
    
    public HighwayCar(){
    	
    	int dir = (int) (Math.random() * 4 % 4);
    	
        setDirection(dir * Math.PI / 2); 
        
        setProperty("icon", "/robot.png");
        setProperty("size", 20);
        Clock.addClockListener(this, 2);
    }
    public void onClock() {
        move(speed);
        wrapLocation();
    }
    public static void main(String[] args) {
        Node.setModel("car", new HighwayCar());        
        Node.setModel("tower", new HighwayTower());
        Node.setModel("server", new HighwayServer());
        new JViewer(new Topology());
    }    
}    