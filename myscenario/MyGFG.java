package myscenario;

import java.util.ArrayList;
import java.util.List;

import jbotsim.Node;


public class MyGFG {
	
	public static void GFGsend(Node source, msgPacket msg, _pktDir d){
		/**
		 * In gfg we first try greedy send our pkt according to direction
		 * if we can find a node closer to our dest, we send it the pkt
		 * if not, turn facing
		 * in facing, if we counter clockwise find the node based on the pkt direction
		 */
		Node nexthop = new Node();
		nexthop = null;
		
		List<Node> neighborlist = new ArrayList<Node>();
		neighborlist = source.getNeighbors();
		neighborlist.remove(msg.last);
		
		//
			
		if(d == _pktDir.noDir){
			//find the node closest to dest in neighborlist
			double temp = source.distance(msg.dest);
			for(Node node : neighborlist){
				//find the minimum distance to dest.
				if(node.distance(msg.dest) < temp){
					if(!(node instanceof Event)){
						nexthop = node;
						temp = node.distance(msg.dest);
					}
				}
			}
			if(temp < source.distance(msg.dest) ){
				msg.last = source;
				source.send(nexthop, msg);
				//mark it next hop in case that need retrieve
				if (nexthop instanceof MeshSensor && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = (MeshSensor)nexthop;
				else if (nexthop instanceof MeshRobot && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = new MeshSensor ();
			}
			else
				//face the msg
				face(source, msg, d); 
					
		}

		//send north
		else if (d == _pktDir.North){
			double temp = source.getY();
			for(Node node : neighborlist){
				//find the most northern node.
				if(node.getY() < temp - 10){
					if(!(node instanceof Event)){
						nexthop = node;
						temp = node.getY();
					}
				}
			}
			
			if(temp < source.getY()){
				msg.last = source;
				source.send(nexthop, msg);
				//mark it next hop in case that need retrieve
				if (nexthop instanceof MeshSensor && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = (MeshSensor)nexthop;
				else if (nexthop instanceof MeshRobot && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = new MeshSensor ();
			}
			else
				//face the msg
				face(source, msg, d); 
		}
		//send south
		else if (d == _pktDir.South){
			double temp = source.getY();
			for(Node node : neighborlist){
				//find the most southern node.
				if(node.getY() > temp + 10){
					if(!(node instanceof Event)){
						nexthop = node;
						temp = node.getY();
					}
				}
			}
			if(temp > source.getY()){
				msg.last = source;
				source.send(nexthop, msg);
				//mark it next hop in case that need retrieve
				if (nexthop instanceof MeshSensor && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = (MeshSensor)nexthop;
				else if (nexthop instanceof MeshRobot && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = new MeshSensor ();
			}
			else
				//face the msg
				face(source, msg, d); 
		}
		//send east
		else if (d == _pktDir.East){
			//the most eastern node.
			double temp = source.getX();
			for(Node node : neighborlist){
				//find the most southern node.
				if(node.getX() > temp + 10){
					if(!(node instanceof Event)){
						nexthop = node;
						temp = node.getX();
					}
				}
			}
			if(temp > source.getX()){
				msg.last = source;
				source.send(nexthop, msg);
				//mark it next hop in case that need retrieve
				if (nexthop instanceof MeshSensor && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = (MeshSensor)nexthop;
				else if (nexthop instanceof MeshRobot && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = new MeshSensor ();
			}
			else
				//face the msg
				face(source, msg, d); 
		}
		//send west
		else if (d == _pktDir.West){
			double temp = source.getX();
			for(Node node : neighborlist){
				//find the most west node.
				if(node.getX() < temp - 10){
					if(!(node instanceof Event)){
						nexthop = node;
						temp = node.getX();
					}
				}
			}
			if(temp < source.getX()){
				msg.last = source;
				source.send(nexthop, msg);
				//mark it next hop in case that need retrieve
				if (nexthop instanceof MeshSensor && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = (MeshSensor)nexthop;
				else if (nexthop instanceof MeshRobot && source instanceof MeshSensor)
					((MeshSensor)source).nextHop = new MeshSensor ();
			}
			else
				//face the msg
				face(source, msg, d); 
		}
	
	}
	
	public static void face(Node source, msgPacket msg, _pktDir d){
				
		Node dest = new Node();
		Node fnexthop = new Node();
		dest = msg.dest;
		fnexthop = null;
		double baseAngle = 0;
		double temp = Math.PI * 2;
		List<Node> neighborlist = new ArrayList<Node>();
		neighborlist = source.getNeighbors();
		neighborlist.remove(msg.last);
		
		if(source instanceof MeshSensor)
			((MeshSensor)source).isFace =true;
				
		if(d == _pktDir.noDir){
			baseAngle = Math.atan2(-(dest.getY() - source.getY()), dest.getX() - source.getX());
		} 
		else if(d == _pktDir.North){
			//90
			baseAngle = Math.PI / 2; 
		}
		else if(d == _pktDir.South){
			//-90
			baseAngle = - Math.PI / 2;
		}
		else if(d == _pktDir.East){
			//0
			baseAngle = 0;
		}
		else if (d == _pktDir.West){
			//180
			baseAngle = Math.PI;
		}
		//counter clockwise select node from the baseAngle
				
		for(Node n : neighborlist){
			//List<Object> angle = new ArrayList<Object>();
			if(!(n instanceof Event)){
				double nodeAngle = Math.atan2( -(n.getY() - source.getY()), n.getX() - source.getX()) - baseAngle;
				//change angle from -PI ~ PI to 0 ~ 2 * PI
			
				if(nodeAngle < 0){
					nodeAngle += Math.PI * 2;
				}
			//find the node with smallest angle
				if(nodeAngle < temp){
					temp = nodeAngle;
					fnexthop = n;
				}
			}
			
		}
		if (fnexthop instanceof MeshSensor && source instanceof MeshSensor)
			((MeshSensor)source).nextHop = (MeshSensor)fnexthop;
		else if (fnexthop instanceof MeshRobot && source instanceof MeshSensor)
			((MeshSensor)source).nextHop = new MeshSensor ();
		msg.last = source;
		source.send(fnexthop, msg);

	}
	
}
