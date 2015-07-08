package myscenario;

import java.util.ArrayList;
import java.util.List;

import jbotsim.Clock;
import jbotsim.Node;
import jbotsim.event.ClockListener;

public class Event extends Node implements ClockListener {
	
	public boolean detected;
	public msgPacket EventMsg;
	public boolean isDealed;
	
	public boolean showup;
	public boolean setLocation;
	public int round;
	public int showTime;
	
	public Event(int x){
		setProperty("icon", "/event.png");
		setProperty("size", 15);
		//this.setColor("red");
		Clock.addClockListener(this,10);
		
		detected = false;
		EventMsg = new msgPacket(_msgType.Event, this);
		isDealed = false;
		showup = false;
		round = 0;
		if(x == 2)
			showTime = 200;
		if(x == 4)
			showTime = 800;
	}
	public Event(){
		this(4);
	}

	public void onClock(){        //send message every clock
//		if(round == 160)
//			Clock.pause();
//		
		if(Clock.currentTime() % 1000 == 0 ){
			round++;
			//System.out.println("This is the round " + round);
			
			Clock.reset();
		}
		// && 
//		if(Clock.currentTime() == showTime && round >0 && round % 10 == 7){
//			showup = true;
//			detected = false;
//			isDealed = false;
//		}
		
//		if(showup == true){ 
//			showup = false;
//			this.setLocation(Math.random() * 560 + 30, (Math.random() * 560 + 30));
//		}
		// 
		if(detected == false ) {// && round >0 && round % 10 == 7){
			Node dest = getDestRobot();
			//dest.setColor("red");
			EventMsg.event.x = this.getX();
			EventMsg.event.y = this.getY();
			send(dest, EventMsg);
			detected = true;
			//System.out.println("detected~!!!!!!!!!!!!!~~~~~~~~~~~~~~~~~~~~~~~~~~"); 
		}
		
		if(isDealed == true){
			//System.out.println("dealed~~~~~~~~~~~~~~~~~~~~~~~~~~~"); 
			this.setLocation(1000, 1000);
			detected = false;
			showup = false;
			isDealed = false;
		}
	}
	
	//get the closest node to send the event
	public Node getDestRobot(){  //choose next destination
		Node dst = new MeshSensor();
		List<Node> EventNbr = new ArrayList<Node>();
		EventNbr = this.getNeighbors();
		double minDist = 10000000;
		if(EventNbr.size() != 0){
			minDist = this.distance(EventNbr.get(0));
			int indexOfNode = 0;
			for(int i = 0; i < EventNbr.size(); i++){
				if (this.distance(EventNbr.get(i)) < minDist){
					minDist = this.distance(EventNbr.get(i));
					indexOfNode = i;
				}
			}
			dst = EventNbr.get(indexOfNode);
		}
		return dst;
	}
	
}