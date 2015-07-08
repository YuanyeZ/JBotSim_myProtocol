package jbotexample;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;
import jbotsim.ui.JViewer;
import jbotsim.Topology;

public class LonerMessageBased extends Node implements ClockListener, 
                                                     MessageListener{
    boolean isAlone = true;    
    
    public LonerMessageBased(){
        Clock.addClockListener(this, 30);
        addMessageListener(this);
        setColor("green");
    }
    public void onClock(){
        send(null, null);
        setColor(isAlone?"green":"red");
        isAlone = true;
    }
    public void onMessage(Message msg) {
        isAlone = false;
    }
    
    public static void main(String[] args) {
        Node.setModel("default", new LonerMessageBased());
        new JViewer (new Topology());
    }    
}    