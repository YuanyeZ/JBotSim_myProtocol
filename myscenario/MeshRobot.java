package myscenario;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

public class MeshRobot extends Node implements ClockListener, MessageListener{

	
	public static final int MSG_DELAY = 0;
	public static final int COM_RANGE = 40;
	
	public static final int MESH_UPDATE_TIME = 1000;	
	public static final int MESH_CONST_TIME = 501;
	
	public int round;
		
	private int eventTime;
	
	public int ID;
			
	msgPacket myMsg;
	msgPacket cNorth;
	msgPacket cSouth;
	msgPacket cEast;
	msgPacket cWest; 
	
	public List<Node> neighbourList;
	public Set<MeshRobot> neighbourRobot;
	public List<Node> tempNeighborlist;
	public double robotX;
	public double robotY;
	
	public Point2D.Double event;
	public boolean eventInvolved;
	
	private boolean moveOn;
	private boolean I_destSet;
	private boolean meshConstruct;
	public Point2D.Double endpoint;
	
	public double movingDist;
	
	public MeshRobot(){
		this(0);
	}
	public MeshRobot(int X){
		
		//System.out.print("time unit is "+ Clock.getTimeUnit());
		setCommunicationRange(COM_RANGE);
		Clock.addClockListener(this, 1);
		addMessageListener(this);
		Message.setMessageDelay(MSG_DELAY);
		setProperty("icon", "/robot.png");
		setProperty("size", 14);
		
		round = 0;
		eventTime = 4000;
		
		ID = X;
		
		neighbourList = new ArrayList<Node>();
		neighbourRobot = new HashSet<MeshRobot>();
		tempNeighborlist = new ArrayList<Node>();
		robotX = this.getX();
		robotY = this.getY();
		
		event = new Point2D.Double();
		eventInvolved = false;
		
		moveOn = false;
		I_destSet = false;
		meshConstruct = false;
		endpoint = new Point2D.Double();
		
		movingDist = 0;
	}	


	@Override
	public void onClock() {
		if(Clock.currentTime() % 1000 == 0){
			Clock.reset();
			this.round++;
			//System.out.println("*******************This is the " + this.round + "th round!!!!!!");	
		}
		
		if(round == 160)
			Clock.pause();
		//help to show different SP 
//		if(round > 0 && (round+1) % 5 == 0 && Clock.currentTime() == 450){
//			myMsg = new msgPacket(_msgType.ILSRLocUp, this, _pktDir.noDir);
//			
//			send(null, myMsg);
//		}
		
		//reset robot location  
//		if( round > 0 && round % 10 == 8 && Clock.currentTime() == 0){
//			this.resetRobot();
//			eventTime = 3000;
//			this.eventInvolved = false;
//			this.setLocation(Math.random() * 560 + 30, Math.random() * 560 + 30);
//			//System.out.println("The total distance for robot is " + this.movingDist );
//			//movingDist = 0;
//			
//			//System.out.println("next cycle######################################################################");
//		}
		
		robotX = this.getX();
		robotY = this.getY();
		//get the neighbors and sort based on distance
		this.neighbourList = this.getNeighbors();
		CompareDist nodeDist = new CompareDist();
		Collections.sort(neighbourList, nodeDist);
//		
						
		if(!meshConstruct && !eventInvolved && !moveOn){
			constructMesh();
			this.I_destSet = false;
		}
		
		if(Clock.currentTime() != 0 && Clock.currentTime() % MESH_CONST_TIME == 0 && !this.eventInvolved){
			this.moveOn = true;
			//set the end point to event
			//set voronoi centroid		
			if(!this.I_destSet && !eventInvolved){
				this.setDest();
				//sum the moving distance
//				if(round % 10 > 4 ){
//					movingDist += Math.sqrt( Math.pow( (int)this.endpoint.x - (int)this.getX(), 2 ) 
//							+ Math.pow((int)this.endpoint.y - (int)this.getY(), 2) );
//				}
			}
			
			if(Math.pow( (int)this.endpoint.x - (int)this.getX(), 2 ) + 
					Math.pow((int)this.endpoint.y - (int)this.getY(), 2) < 50){
				//System.out.println("reach dest!!!!!!!!!!1!!!");
				moveOn = false;
			}
			
		}
		
		//start to move!		
		if(moveOn){
			
			//if receive event,  and deal with it
			if(eventInvolved){
				endpoint = event;
			}
						
			this.setDirection(endpoint);
			this.move(1);
			
			tempNeighborlist = this.getNeighbors();
			CompareDist ILSRnodeDist = new CompareDist();
			Collections.sort(tempNeighborlist, ILSRnodeDist);
			
			//run ILSR during move	
			if(this.I_destSet && !eventInvolved){
				//&& Math.sqrt((int)this.endpoint.x - (int)this.getX()) > 1  && Math.sqrt((int)this.endpoint.y - (int)this.getY()) > 1){
				//run ILSR
				//System.out.println("ILSR!!!!!!!!!!!!!");
				robotILSR();
			}
			
			if(Math.pow( (int)this.endpoint.x - (int)this.getX(), 2 ) + 
					Math.pow((int)this.endpoint.y - (int)this.getY(), 2) < 50){
				//System.out.println("reach dest!!!!!!!!!!1!!!");
				moveOn = false;
			}

		}
				
		if(eventInvolved){
					
			if(eventSolved()){
				this.eventInvolved = false;	
				List<Node> eventSearch = new ArrayList<Node>();
				eventSearch = this.getNeighbors();
				for(Node n : eventSearch){
					if(n instanceof Event)
						((Event) n).isDealed = true;
				}
				//this.meshConstruct = true;
			}
		}
		
		if (Clock.currentTime() % MESH_UPDATE_TIME == 0 && !eventInvolved ){
			this.resetRobot();
		}
	}

	public boolean eventSolved() {
		//System.out.println("go for event~~~~~~~~~~~~~~~~");
//		if(Math.sqrt(this.endpoint.x - this.getX()) <= 3  && Math.sqrt(this.endpoint.y - this.getY()) <= 3 ){
			
			//System.out.println("event dealing~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			if(eventTime-- == 0){
				eventTime = 4000;
				event = null;
				eventInvolved = false;
				//System.out.println("------event dealed~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				return true;
			}
		
//		if(round % 10 == 0 )
//			return true;
		return false;
	}

	
	private void constructMesh() {
		cNorth = new msgPacket(_msgType.iMesh, this, _pktDir.North);
		cSouth = new msgPacket(_msgType.iMesh, this, _pktDir.South);
		cEast = new msgPacket(_msgType.iMesh, this, _pktDir.East);
		cWest = new msgPacket(_msgType.iMesh, this, _pktDir.West);
		
		MyGFG.GFGsend(this, cNorth, _pktDir.North);
		//System.out.println( this.round + "th ~~~~~~~~Robot iMesh Message~~~~~~~~~~~~~~~~~~~~");
		MyGFG.GFGsend(this, cSouth, _pktDir.South);
		//System.out.println(this.round + "th ~~~~~~~~Robot iMesh Message~~~~~~~~~~~~~~~~~~~~");
		MyGFG.GFGsend(this, cEast, _pktDir.East);
		//System.out.println(this.round + "th ~~~~~~~~Robot iMesh Message~~~~~~~~~~~~~~~~~~~~");
		MyGFG.GFGsend(this, cWest, _pktDir.West);
		//System.out.println(this.round + "th ~~~~~~~~Robot iMesh Message~~~~~~~~~~~~~~~~~~~~");
		
		meshConstruct = true;
	}

	@Override
	public void onMessage(Message msg) {
		myMsg = (msgPacket) msg.content;
		
		//receive msg from the sensor
		if(myMsg.type == _msgType.eventReport){
			if(myMsg.dest == this){
				this.eventInvolved = true;
				this.moveOn = true;
				this.event = myMsg.event;
				this.endpoint = myMsg.event;
//				System.out.println("event is location at" + this.event.x + " , " + this.event.y);
				//print out the distance to the second event
				if(Clock.currentTime() > 300)
					System.out.print("Distance to event is " + Math.sqrt(Math.pow
							(this.getX() - this.event.x, 2) + (Math.pow(this.getY() - this.event.y, 2))) + "\n");
			}
			else{
				MyGFG.GFGsend(this, myMsg, _pktDir.noDir);
				//System.out.println(this.round + "th ~~~~~~~~Robot Other Message~~~~~~~~~~~~~~~~~~~~");
			}
		}
		
		//got neighboring robot from collision point
		if(myMsg.type == _msgType.robotLocation){
			//System.out.println("receive~!!!!!!!!!!!!!!!!!!!!!!!!!");
			for(MeshRobot r:myMsg.neighbourRobot)
				this.neighbourRobot.add(r);
			
			this.neighbourRobot.remove(this);
			
//			for(MeshRobot r: this.neighbourRobot)
//				System.out.println("this robot at " + this + "has neighbour " + r);	
		}
		
	}
	
	private void setDest() {			
		double sumX = 0;
		double sumY = 0;
		int NumOfNodeLocation = 0;
		
		for(int i = 20; i < Test.LONG -30; i++){
			for (int j = 20; j < Test.WIDE - 30; j++){
				boolean closerPoint = true;
				for (MeshRobot r : neighbourRobot){
					if(this.distance(i, j) > r.distance(i, j)){
						closerPoint = false;
					}
				}
				if(closerPoint == true){	
					sumX += i;
					sumY += j;
					++NumOfNodeLocation;
				}
			}
		}
		this.endpoint.x = sumX / NumOfNodeLocation;
		this.endpoint.y = sumY / NumOfNodeLocation;
		
		I_destSet = true;
		
	}
	
	private void resetRobot() {
					
		moveOn = false;
		meshConstruct = false;
		I_destSet = false;
		
		this.tempNeighborlist.removeAll(tempNeighborlist);
		this.neighbourList.removeAll(neighbourList);
		this.neighbourRobot.removeAll(neighbourRobot);
		
		
	}
	
	public void robotILSR(){						
		//System.out.println("ILSR node~~~");
		//broken node
		if (this.brokenNeighbor()){
			//System.out.println("broken node~~~");
			//send current location, end-point location, mesh update time, moving speed to its neighbors
			myMsg = new msgPacket(_msgType.ILSRLocUp, this, _pktDir.noDir);
			
			send(null, myMsg);
			//System.out.println(this.round + "th ~~~~~~~Robot ILSR Message~~~~~broken~~~~~~~~~~~~~~~");
			//System.out.println("robot ILSR msg sent out!!!");
		}
	
		//new node added 
		else if(newNeighbor()){
			//System.out.println(this.round + "th ~~~~~~~Robot ILSR Message~~~~~new~~~~~~~~~~~~~~~");
		}
		
		//move to event because of higher priority 
		else if (this.eventInvolved == true){
		Node Endpoint = new Node();
		Endpoint.setLocation(endpoint);
		msgPacket ILSRRecover = new msgPacket(_msgType.ILSRReLocUp, this, _pktDir.noDir, Endpoint); 
		MyGFG.GFGsend(this,ILSRRecover, _pktDir.noDir);
		//System.out.println(this.round + "th ~~~~~~~~Robot ILSR Message~~~~~~~~~~~~~~~~~~~~");
		}
	
	}

	private boolean brokenNeighbor() {
		//tempneighbourList is the new, Neighborlist is the old
		
		if (this.neighbourList.size() > this.tempNeighborlist.size()){
			//find which one is broken
			for(int i = 0; i < this.neighbourList.size(); i++){
				if (!this.tempNeighborlist.contains(this.neighbourList.get(i))){
					if(this.neighbourList.get(i) instanceof MeshSensor)
						((MeshSensor)this.neighbourList.get(i)).I_isBrokenOne = true;
				}
			}
			return true;
		}
		else{
			for(int i = 0; i < neighbourList.size(); i++){
				if(!this.tempNeighborlist.contains(this.neighbourList.get(i))){
					if(this.neighbourList.get(i) instanceof MeshSensor)
						((MeshSensor)this.neighbourList.get(i)).I_isBrokenOne = true;
					return true;
				}
			}
			return false;				
		}
		
	}
	
	public boolean newNeighbor(){
		if (this.neighbourList.size() < this.tempNeighborlist.size()){
			return true;
		}
		else{
			for(int i = 0; i < tempNeighborlist.size(); i++){
				if(!this.neighbourList.contains(this.tempNeighborlist.get(i)))
					return true;					
			}				
		}
		return false;
	}

	
	//comparator
	public class CompareDist implements Comparator<Object> {
		public int compare(Object arg0, Object arg1) {
			Node n0 = (Node) arg0;
			Node n1 = (Node) arg1;
			if(n0.distance(robotX, robotY) < n1.distance(robotX, robotY))
				return -1;
			else
				return 1;
		}
	}
	
	public String toString(){
		return this.robotX + " and " + this.robotY;
 	}

}
