package myscenario;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jbotsim.Clock;
import jbotsim.Message;
import jbotsim.Node;
import jbotsim.event.ClockListener;
import jbotsim.event.MessageListener;

public class MeshSensor extends Node implements ClockListener, MessageListener {

	public static final int MSG_DELAY = 0;
	public static final int COM_RANGE = 35;
	
	public static final int MESH_UPDATE_TIME = 1000;
	
	public int round;
	
	public List<Node> neighbourList;
	private msgPacket myMsg;
	public double sensorX;
	public double sensorY;
	
	public MeshRobot SPNode;
	public MeshSensor nextHop;
	public _pktDir meshDir;
	public boolean isMesh;
	public boolean isFace;
	public boolean isRetrive;
	public boolean LookupNode;
	public Set<MeshRobot> neighbouringRobot;
	
	public Point2D.Double event;
	public int potentialSPCounter;
	public List<MeshRobot> potentialSP;
	
	public boolean I_isBrokenOne;
	public MeshRobot I_source;
	public Point2D.Double I_endpoint;
	public boolean I_isAnchor;
		
	public MeshSensor(){
		this.setCommunicationRange(COM_RANGE);
		Clock.addClockListener(this,1);
		Message.setMessageDelay(MSG_DELAY);
		addMessageListener(this);
        setColor("green");
        
        round = 0;
        
        neighbourList = new ArrayList<Node>();
        myMsg = new msgPacket();
    	sensorX = this.getX();
    	sensorY = this.getY();
    	
        SPNode = null;
    	nextHop = null;
    	meshDir = _pktDir.noDir;
    	isMesh = false;
    	isFace = false;
    	isRetrive = false;
    	LookupNode = false;
    	neighbouringRobot = new HashSet<MeshRobot>();
    	
    	event = new Point2D.Double();
    	potentialSPCounter = 0;
    	potentialSP = new ArrayList<MeshRobot>();
    	
    	I_isBrokenOne = false;
    	I_source = null;
    	I_endpoint = null;
	}
	
	@Override
	public void onMessage(Message msg) {
		myMsg = (msgPacket) msg.content;
		
		//construct the mesh
		//*********************************************************************************
		if(myMsg.type == _msgType.iMesh){
			//node not register before
		
			if(this.SPNode == null){
				
				SPNode = myMsg.M_SP;
				this.isMesh = true;
				this.meshDir = myMsg.direction;
				if (myMsg.direction == _pktDir.North)
					this.setColor("white");
				else if (myMsg.direction == _pktDir.South)
					this.setColor("yellow");
				else if(myMsg.direction == _pktDir.East)
					this.setColor("blue");
				else if(myMsg.direction == _pktDir.West)
					this.setColor("pink");

				MyGFG.GFGsend(this, myMsg, myMsg.direction);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				//System.out.println(this + "receive mesh, null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			//receive the same SP construct for different direction 
			else if(myMsg.M_SP.distance(this) == this.SPNode.distance(this) && myMsg.direction != this.meshDir && isFace == false){
				
				MyGFG.GFGsend(this, myMsg, myMsg.direction);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
			}
			//collision point, compare the location of SP
			//collect neighboring robots and sent to each of them
			else if(myMsg.M_SP != this.SPNode){
				
				//add the neighboring Robots to a set
				if(this.distance(myMsg.M_SP) < this.distance(this.SPNode))
					this.SPNode = myMsg.M_SP;
				this.neighbouringRobot.add((MeshRobot) this.SPNode);
				this.neighbouringRobot.add(myMsg.M_SP);	
				
				for(MeshRobot r: neighbouringRobot){
					
					msgPacket robotLcoation = new msgPacket(_msgType.robotLocation, this, _pktDir.noDir, r);
					//System.out.println("dest is " + robotLcoation.dest);
					
					MyGFG.GFGsend(this,robotLcoation, _pktDir.noDir);
					//System.out.println(this.round + "th ~~~~~~~~Sensor Other Message~~~~~~~~~~~~~~~~~~~~");
					//System.out.println("SEND: robot location is " + r);
				}
				//Retrieve...
				//the further SP has been forward yet
				//clean the information in this node
				if( this.nextHop.SPNode != null && this.nextHop.SPNode != myMsg.M_SP 
						&& myMsg.M_SP.distance(this) < this.SPNode.distance(this)){
					MeshSensor temp = new MeshSensor();
					temp = nextHop;
					while(temp.SPNode != null && temp.SPNode == this.SPNode && temp.distance(myMsg.M_SP) < temp.distance(temp.SPNode)){
						temp.clean();
						temp = temp.nextHop;
					}
					
					SPNode = myMsg.M_SP;
					MyGFG.GFGsend(this, myMsg, myMsg.direction);
					//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				}				
			}		
		}
		//detected event
		//*********************************************************************************
		else if(myMsg.type == _msgType.Event){
			//if there is no mobile SP
			if(this.I_source == null){
				this.setColor("black");
				this.event = myMsg.event;
				//System.out.println("the event is located at " + this.event.x + " " + this.event.y + "~~~~~~~`");
				//doing the cross look up with event location in four directions
				msgPacket cNorth = new msgPacket(_msgType.iMeshLook, this, _pktDir.North);
				cNorth.event = myMsg.event;
				msgPacket cSouth = new msgPacket(_msgType.iMeshLook, this, _pktDir.South);
				cSouth.event = myMsg.event;
				msgPacket cEast = new msgPacket(_msgType.iMeshLook, this, _pktDir.East);
				cEast.event = myMsg.event;
				msgPacket cWest = new msgPacket(_msgType.iMeshLook, this, _pktDir.West);
				cWest.event = myMsg.event;
							
				MyGFG.GFGsend(this, cNorth, _pktDir.North);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				MyGFG.GFGsend(this, cSouth, _pktDir.South);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				MyGFG.GFGsend(this, cEast, _pktDir.East);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				MyGFG.GFGsend(this, cWest, _pktDir.West);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
			}
			//report event to mobile SP
			else{
				msgPacket cILSR = new msgPacket(_msgType.eventReport, this, _pktDir.noDir, this.I_source);
				cILSR.event = myMsg.event;
				MyGFG.GFGsend(this, cILSR, _pktDir.noDir);
				//System.out.println(this.round + "th ~~~~~~~~Sensor other Message~~~~~~~~~~~~~~~~~~~~");
			}
		}

		//sensor's request for SP
		//*********************************************************************************
		else if(myMsg.type == _msgType.iMeshLook){
			
			if (this.isMesh != true){
				
				this.setColor("black");
				MyGFG.GFGsend(this, myMsg, myMsg.direction);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
				
			}
			else{
				//System.out.println("eventReport to " + this.SPNode);
				
				msgPacket eventReport = new msgPacket(_msgType.eventReport, this, _pktDir.noDir, myMsg.source);
				eventReport.M_SP = this.SPNode;
				//System.out.println("**************msg dest is " + eventReport.dest);
				MyGFG.GFGsend(this, eventReport, _pktDir.noDir);
				//System.out.println(this.round + "th ~~~~~~~~Sensor event Message~~~~~~~~~~~~~~~~~~~~");
			}
		}
		
		//tell robot about the location of event
		//*********************************************************************************
		else if(myMsg.type == _msgType.eventReport)
			//if this node is not the dest of msg
			
			if(myMsg.dest != this){
				MyGFG.GFGsend(this, myMsg, myMsg.direction);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
			}
			else{
				//if it receive this msg four times
				//it compares the distance between the event and SPs, and choose the minimum one to inform
				MeshRobot SP = new MeshRobot();
				++this.potentialSPCounter;
				this.potentialSP.add(myMsg.M_SP);
								
				if(this.potentialSPCounter == 4){
					
					double minDist = 10000;
					for(MeshRobot r : this.potentialSP){
						//System.out.println(r);
						if(r.distance(this.event.x, this.event.y) < minDist){
							//System.out.println("~~~~~~~~~~~~~~~~~one potential SP is " + r);
							minDist = r.distance(event.x, event.y);
							SP = r;
						}
					}
				}
				//System.out.println("event is discovered, send request to " + SP);
				msgPacket eventReport = new msgPacket(_msgType.eventReport, this, _pktDir.noDir, SP);
				eventReport.event.x = this.event.x;
				eventReport.event.y = this.event.y;
				MyGFG.GFGsend(this, eventReport, eventReport.direction);
				//System.out.println(this.round + "th ~~~~~~~~Sensor iMesh Message~~~~~~~~~~~~~~~~~~~~");
			}
		
		//inform robots their neighboring robots
		//*********************************************************************************
		else if(myMsg.type == _msgType.robotLocation){
			
			MyGFG.GFGsend(this, myMsg, myMsg.direction);
			//System.out.println(this.round + "th ~~~~~~~~Sensor Other Message~~~~~~~~~~~~~~~~~~~~");
		}
		
		//ILSR for mobile node
		//*********************************************************************************
		else if(myMsg.type == _msgType.ILSRLocUp){	
			
			//new node
			if(this.I_source == null){
				this.setColor("yellow");
				this.I_source = (MeshRobot) myMsg.source;
				this.I_endpoint = myMsg.I_endpoint;
				
				myMsg.last = this;
				send(null, myMsg);
				//System.out.println(this.round + "th ~~~~~~~~Sensor ILSR Message~~~~~~~~~~~~~~~~~~~~");
			}
			
			//broken ndoe
			else if(this.I_isBrokenOne == true && myMsg.source.distance(this) < this.I_source.distance(this)){
				//remove the source from the neighborlist
				//neighborlist.remove(neighborlist.indexOf(myMsg.source));
				//re-transmit it
				//neighborlist = this.getNeighbors();
				//System.out.println("broken node, new movingSP~~~");
				this.I_source = (MeshRobot) myMsg.source;
				this.I_endpoint = myMsg.I_endpoint;
				
				myMsg.last = this;
				send(null, myMsg);
				//System.out.println(this.round + "th ~~~~~~~~Sensor ILSR Message~~~~~~~~~~~~~~~~~~~~");
				
			}
			
			//endpoint is not the same
			else if((!myMsg.I_endpoint.equals(this.I_endpoint)) &&
					myMsg.source.distance(this) < this.I_source.distance(this)){
				//System.out.println("endpoint is not the same, msg resend!!!");
				//System.out.println("myMsg: "+ myMsg.source.distance(this));
				//sensorMsg
				//System.out.println("sensorMsg: "+ sensorMsg.source.distance(this));
				//sensorMsg = (msgPacket)myMsg.clone();
				this.I_source = (MeshRobot) myMsg.source;
				this.I_endpoint = myMsg.I_endpoint;
				
				send (null, myMsg);
				//System.out.println(this.round + "th ~~~~~~~~Sensor ILSR Message~~~~~~~~~~~~~~~~~~~~");
			}
		}
		
		//ILSR recovery
		//*********************************************************************************
		else if(myMsg.type == _msgType.ILSRReLocUp){
			
			if(isMsgReceived(myMsg) == true)
				//received this pkt twice; set it as anchor node
				this.I_isAnchor = true;
			else{
				//GFGsend myMsg
				Node temp = new Node();
				temp.setLocation(I_endpoint.x, I_endpoint.y);
				MyGFG.GFGsend(this, myMsg, _pktDir.noDir);
				//System.out.println(this.round + "th ~~~~~~~~Sensor ILSR Message~~~~~~~~~~~~~~~~~~~~");
			}
		}
	}
	
	public boolean isMsgReceived(msgPacket msg){
		if(msg.source == this.myMsg.source){
			if(msg.I_endpoint == this.myMsg.I_endpoint) return true;
			return false;
		}
		else 
			return false;
		
	}

	private void clean() {
		this.setColor("green");
		this.isMesh = false;
		this.SPNode = null;
	}

	@Override
	public void onClock(){
		if(round == 160)
			Clock.pause();
	
		if(Clock.currentTime() % MESH_UPDATE_TIME == 0){
				Clock.reset();
				round++;
				this.resetSensor();
		}
		
		//show different SP in stable deployment 
//		if((round+1) % 5 ==0 && Clock.currentTime() == 550 && this.I_source != null)
//			System.out.println(this.round + "th The SP in sensor is " + this.I_source.ID);
		
			
	}
	
	public void resetSensor(){
		this.setColor("green");
		neighbourList.removeAll(neighbourList);
    	sensorX = this.getX();
    	sensorY = this.getY();
    	
        SPNode = null;
    	nextHop = null;
    	meshDir = _pktDir.noDir;
    	isMesh = false;
    	isFace = false;
    	isRetrive = false;
    	LookupNode = false;
    	neighbouringRobot.removeAll(neighbouringRobot);
    	
    	event = new Point2D.Double();
    	potentialSPCounter = 0;
    	potentialSP.removeAll(potentialSP);
    	
    	I_isBrokenOne = false;
    	I_source = null;
    	I_endpoint = null;
    	I_isAnchor = false;
	}
	
	public String toString(){
		return "sensor located at " +(int)this.getX() + ' ' + (int)this.getY();
	}
	
//	public class CompareDist implements Comparator<Object> {
//		public int compare(Object arg0, Object arg1) {
//			Node n0 = (Node) arg0;
//			Node n1 = (Node) arg1;
//			if(n0.distance(sensorX, sensorY) < n1.distance(sensorX, sensorY))
//				return -1;
//			else
//				return 1;
//		}
//	}


}
