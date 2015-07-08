package myscenario;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;


import jbotsim.Node;

public class msgPacket implements Cloneable{
	
	//Common variable
	
	//iMeshCst, iMeshRet, iMeshLook, ILSR, Event
	public _msgType type;
	//noDir, East, West, North, South
	public _pktDir direction;
	//source node, last node, dest node, sp node
	public Node source, last, dest;
	
	//decide whether next hop forward this pkg or not. 
	//public boolean isForward;
	
	//iMesh variable
	public MeshRobot M_SP;
	Set<MeshRobot> neighbourRobot;
	//public boolean isface;
//	public Node M_turnFace = null;
//	public boolean M_gfg_isGreedy=true;//greedy gfg_isg=true, face gfg_isg=0;
//	public boolean M_isLookup=false;
//	public double 	M_distance=10000;//?
//	public boolean M_isRetrieve=false;
//	public String M_retrieveDirection;
//	public Node M_retrieveSP;
//	public Node M_lookup_SP;
//	public boolean M_islookup_forward=false;
//	public boolean M_isFindingsp=false;//on the way getting there(the found sp)
	
	public Point2D.Double event;
	/**************************************************************/
	
	//ILSR variable
	/**************************************************************/
	//location of dest node
	public Point2D.Double I_endpoint;
	public double I_movingSpeed;
	
	//public NodeLocation I_eventLocation;
	
	
	public msgPacket() 
	{
		type = null;
		direction = _pktDir.noDir;
		source = null;
		last = null;
		//dest node
		dest = null;
		M_SP = null;
		//isface = false;
		
	}

	public msgPacket(_msgType t, Node r) 
	{
		type = t;
		direction = _pktDir.noDir;
		source = r;
		last = r;
		dest = null;
		M_SP = null;		
		//updateTime = r.meshUpdateTime;
		
		
		//msg type is iMesh
		if(t == _msgType.iMesh){
			M_SP = (MeshRobot) r;	
		}
		
		//msg type is look up mesh
		//**********************************************************************************
		else if(t == _msgType.iMeshLook){
			
		}
				
		//msg type is ILSRLockUp, send current position, endpoint, mesh update time, moving speed
		//**********************************************************************************
		else if(t == _msgType.ILSRLocUp){
			//updateTime = r.timer;
			//location of dest node
			I_endpoint = new Point2D.Double();
			I_endpoint = ((MeshRobot)r).endpoint;
			
		}
		
		//msg type is ILSRLockUp, send current position, endpoint, mesh update time, moving speed
		//**********************************************************************************
		else if(t == _msgType.ILSRReLocUp){
			//updateTime = r.timer;
			I_endpoint = new Point2D.Double();
			I_endpoint = ((MeshRobot)r).endpoint;
			
		}
		
		//msg type is event
		//**********************************************************************************
		else if(t == _msgType.Event){
//			M_isLookup = true;
			this.event = new Point2D.Double();
		}
		
		//**********************************************************************************
		else if(t == _msgType.eventReport){
			this.event = new Point2D.Double();
			
			//this.M_isFindingsp = true;
		}
		
		//**********************************************************************************
		else if(t == _msgType.robotLocation){
			neighbourRobot = new HashSet<MeshRobot>();
			neighbourRobot = ((MeshSensor)r).neighbouringRobot;
		}
			
		
	}
	public msgPacket(_msgType t, Node r, _pktDir d) {
		this(t, r);
		this.direction = d;
	}
	public msgPacket(_msgType t, Node r, _pktDir d, Node dest) {
		this(t, r);
		this.direction = d;
		this.dest = dest;
	}
	
	public Object clone(){
		msgPacket temp = null;
		try{
			temp = (msgPacket)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace(); 
		}
		return temp;
	}
	
//	public int compareTo(msgPacket c)
//	{
//		return this.M_distance>c.M_distance?1:(this.M_distance==c.M_distance?0:-1);
//	}
//	public String toString()
//	{
//		return this.M_distance+":"+"("+source.getX()+","+source.getY()+")";
//	}

	
}
