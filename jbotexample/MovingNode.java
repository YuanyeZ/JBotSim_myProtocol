package jbotexample;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.ui.JViewer;
import jbotsim.Topology;


//defines the new type of node
public class MovingNode extends Node implements ClockListener{
    public MovingNode(){
    	//implies this node will subsequently have its onClock() 
    	//method called every 2 units of time (the default unit is 10ms)
        Clock.addClockListener(this, 2);
        //Direction is parameter corresponding angle in radian (2*PI)
        setDirection(Math.random()*2*Math.PI);
    }
    public void onClock(){
    	//node advances by 2 units of distance; the number is the speed. 
        move(2);
        //re-evaluates the node's coordinates as if the plane was wrapped.
        wrapLocation(); 
        
    }
    public static void main(String[] args){
        Node.setModel("default", new MovingNode());
        new JViewer (new Topology());
    }   
}    



 
