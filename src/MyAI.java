// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================
import java.util.*;
public class MyAI extends Agent
{
	boolean isreturn;
	int point=0;
	Action lastAction;
	Stack<String> stack;
	HashSet<String> visited;
	HashSet<String> safe;
	HashSet<String> uncertain;
	HashMap<String,String> map;
    boolean	goldLooted;		// True if gold was successfuly looted
	boolean	hasArrow;		// True if the agent can shoot
	boolean	bump;			// Bump percept flag
	boolean	scream;			// Scream percept flag
	int		agentDir;		// The direction the agent is facing: 0 - right, 1 - down, 2 - left, 3 - up
	int		agentX;			// The column where the agent is located ( x-coord = col-coord )
	int		agentY;			// The row where the agent is located ( y-coord = row-coord )
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		goldLooted   = false;
		hasArrow     = true;
		bump         = false;
		scream       = false;
		agentDir     = 0;
		agentX       = 0;
		agentY       = 0;
		lastAction   = Action.CLIMB;
		
		isreturn=false;
		stack=new Stack<String>();
		visited=new HashSet<String>();
		visited.add("00");
		safe=new HashSet<String>();
		safe.add("00");
		uncertain=new HashSet<String>();
		map=new HashMap<String,String>();
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	public Action getAction
	(
		boolean stench,
		boolean breeze,
		boolean glitter,
		boolean bump,
		boolean scream
	)
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		
		//put the current position<key,value>to map
		String key=String.valueOf(agentX)+String.valueOf(agentY);
		String cur_value= (stench==false?String.valueOf(0):String.valueOf(1))+(breeze==false?String.valueOf(0):String.valueOf(1))
				+ (glitter==false?String.valueOf(0):String.valueOf(1))+ (bump==false?String.valueOf(0):String.valueOf(1))
				+(scream==false?String.valueOf(0):String.valueOf(1));
		map.put(key, cur_value);
		//back to init point. and if user back to point over 2 times, it will climb. 
		if(agentX==0&&agentY==0) {
			//System.out.println("1");
			point++;
			if(point>=2)
				return Action.CLIMB;
		}
		//climb when user back to init point after getting the gold
		if(isreturn) {     //when the gold is catched
			//System.out.println("2");
			if(agentX==0&&agentY==0)
				return Action.CLIMB;
			return return_action();
		}
		
		if(glitter) {
			//System.out.println("3");
			isreturn=true;
			return Action.GRAB;
		}
		
		if(bump) {
			visited.add(key);
			String last=stack.pop();
			agentX=last.charAt(0)-'0';
			agentY=last.charAt(1)-'0';
			if(!visited.contains(String.valueOf(agentX+1)+String.valueOf(agentY))){
				return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
			}else if(!visited.contains(String.valueOf(agentX)+String.valueOf(agentY+1))) {
				return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
			}else if(!visited.contains(String.valueOf(agentX-1)+String.valueOf(agentY))) {
				return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
			}else if(!visited.contains(String.valueOf(agentX)+String.valueOf(agentY-1))) {
				return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
			}else {
				return return_action();
			}
		}
		if((stench==false)&&(breeze==false)) {     //safe
			//System.out.println("5");
			safe.add(String.valueOf(agentX+1)+String.valueOf(agentY));
			safe.add(String.valueOf(agentX)+String.valueOf(agentY+1));
			safe.add(String.valueOf(agentX-1)+String.valueOf(agentY));
			safe.add(String.valueOf(agentX)+String.valueOf(agentY-1));
			if(!visited.contains(String.valueOf(agentX+1)+String.valueOf(agentY))){
				return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
			}else if(!visited.contains(String.valueOf(agentX)+String.valueOf(agentY+1))) {
				return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
			}else if(!visited.contains(String.valueOf(agentX-1)+String.valueOf(agentY))) {
				return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
			}else if(!visited.contains(String.valueOf(agentX)+String.valueOf(agentY-1))) {
				return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
			}else {
				return return_action();
			}
		}
		if(stench==true||breeze==true) {
			//logic judge   ignore
			//System.out.println("6");
			if(!safe.contains(String.valueOf(agentX)+String.valueOf(agentY+1))) {
				uncertain.add(String.valueOf(agentX)+String.valueOf(agentY+1));
			}
			if(!safe.contains(String.valueOf(agentX)+String.valueOf(agentY-1))) {
				uncertain.add(String.valueOf(agentX)+String.valueOf(agentY-1));
			}
			if(!safe.contains(String.valueOf(agentX-1)+String.valueOf(agentY))) {
				uncertain.add(String.valueOf(agentX-1)+String.valueOf(agentY));
			}
			if(!safe.contains(String.valueOf(agentX+1)+String.valueOf(agentY))) {
				uncertain.add(String.valueOf(agentX+1)+String.valueOf(agentY));
			}
			return  return_action();
		}else {
			//System.out.println("7");
			return return_action();
		}
		
		

		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	public Action return_action() {
		if(stack.isEmpty())return Action.CLIMB;
		String last_cur=stack.peek();
		if((last_cur.charAt(0)-'0'-agentX)==-1) {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				agentDir=2;
				return Action.TURN_RIGHT;
			}else if(agentDir==2) {
				stack.pop();
				agentX--;
				return Action.FORWARD;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}else if((last_cur.charAt(0)-'0'-agentX)==1) {
			if(agentDir==0) {
				stack.pop();
				agentX++;
				return Action.FORWARD;
			}else if(agentDir==1) {
				agentDir=0;
				return Action.TURN_LEFT;
			}else if(agentDir==2) {
				agentDir=3;
				return Action.TURN_RIGHT;
			}else {
				agentDir=0;
				return Action.TURN_RIGHT;
			}
		}else if((last_cur.charAt(1)-'0'-agentY)==1) {
			if(agentDir==0) {
				agentDir=3;
				return Action.TURN_LEFT;
			}else if(agentDir==1) {
				agentDir=2;
				return Action.TURN_RIGHT;
			}else if(agentDir==2) {
				agentDir=3;
				return Action.TURN_RIGHT;
			}else {
				stack.pop();
				agentY++;
				return Action.FORWARD;
			}
		}else {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				stack.pop();
				agentY--;
				return Action.FORWARD;
			}else if(agentDir==2) {
				agentDir=1;
				return Action.TURN_LEFT;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}
	}
	public Action forward_action(String next_cur,String cur) {
		if((next_cur.charAt(0)-'0'-agentX)==-1) {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				agentDir=2;
				return Action.TURN_RIGHT;
			}else if(agentDir==2) {
				stack.push(cur);
				agentX--;
				visited.add(next_cur);
				safe.add(next_cur);
				return Action.FORWARD;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}else if((next_cur.charAt(0)-'0'-agentX)==1) {
			if(agentDir==0) {
				stack.push(cur);
				agentX++;
				visited.add(next_cur);
				safe.add(next_cur);
				return Action.FORWARD;
			}else if(agentDir==1) {
				agentDir=0;
				return Action.TURN_LEFT;
			}else if(agentDir==2) {
				agentDir=3;
				return Action.TURN_RIGHT;
			}else {
				agentDir=0;
				return Action.TURN_RIGHT;
			}
		}else if((next_cur.charAt(1)-'0'-agentY)==1) {
			if(agentDir==0) {
				agentDir=3;
				return Action.TURN_LEFT;
			}else if(agentDir==1) {
				agentDir=2;
				return Action.TURN_RIGHT;
			}else if(agentDir==2) {
				agentDir=3;
				return Action.TURN_RIGHT;
			}else {
				stack.push(cur);
				agentY++;
				visited.add(next_cur);
				safe.add(next_cur);
				return Action.FORWARD;
			}
		}else {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				stack.push(cur);
				agentY--;
				visited.add(next_cur);
				safe.add(next_cur);
				return Action.FORWARD;
			}else if(agentDir==2) {
				agentDir=1;
				return Action.TURN_LEFT;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================


	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}