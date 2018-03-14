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
	public class Clause
	{
		ArrayList<String> singles;
		Clause()
		{
			this.singles = new ArrayList<String>();
		}
		void add(String literal)
		{
			singles.add(literal);
		}
	}
	ArrayList<Clause> Pit_Clauses = new ArrayList<Clause>();
	ArrayList<Clause> Wum_Clauses = new ArrayList<Clause>();

	boolean isreturn;
	int point=0;
	Action lastAction;
	Stack<String> stack;
	HashSet<String> visited;
	HashSet<String> safe;
	HashSet<String> uncertain;
	HashMap<String,String> map;
	HashSet<String> certain_pit;
	String wumposition;
    boolean	goldLooted;		// True if gold was successfuly looted
	boolean	hasArrow;		// True if the agent can shoot
	boolean	bump;			// Bump percept flag
	boolean	scream;			// Scream percept flag
	int		agentDir;		// The direction the agent is facing: 0 - right, 1 - down, 2 - left, 3 - up
	int		agentX;			// The column where the agent is located ( x-coord = col-coord )
	int		agentY;			// The row where the agent is located ( y-coord = row-coord )
	int     maxX;
	int     maxY;
	int     isup;
	// ======================================================================
	// My precious code below
	HashMap<String, Integer> KB;
	
	// ======================================================================
	
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
		maxX         =0;
		maxY         =0;
		
		isup=4;
		isreturn=false;
		stack=new Stack<String>();
		visited=new HashSet<String>();
		certain_pit=new HashSet<String>();
		wumposition="";
		visited.add("00");
		visited.add("e");
		safe=new HashSet<String>();
		safe.add("00");
		safe.add("e");
		uncertain=new HashSet<String>();
		map=new HashMap<String,String>();

		//add the B P to the knowledge base
		ArrayList<String> neighs=new ArrayList<String>();
		neighs.add("01");
		neighs.add("10");
		addPitClause("00",neighs);
		Clause clause=new Clause();
		clause.add("-P00");
		Pit_Clauses.add(clause);
		
		//add the S W to the knowledge base
		neighs=new ArrayList<String>();
		neighs.add("01");
		neighs.add("10");
		addWumClause("00",neighs);
		clause=new Clause();
		clause.add("-W00");
		Wum_Clauses.add(clause);
		
		
		// ======================================================================
		// My precious code below
		KB = new HashMap<String, Integer>();
		// ======================================================================
	}
	// ======================================================================
	// My precious code
	public boolean ValidCoord(int x, int y)
	{
		if(maxX!=0&&maxY!=0) {
			if(x >=0 && x<=maxX && y>=0&&y<=maxY)
				return true;
			return false;
		}
		if(maxY!=0) {
			if(x >=0 && y>=0 && y<=maxY)
				return true;
		}
		else if(maxX!=0) {
			if(x>=0&&x<=maxX&&y>=0)
				return true;
			return false;
		}else {
			if(x>=0&&y>=0)
				return true;
			return false;
		}
		return false;
	}
	public String Coord(int x, int y)
	{
		if(ValidCoord(x,y))
			return String.valueOf(x) + String.valueOf(y);
		else
			return "e";
	}
	public int Details
	(	
		boolean stench,
		boolean breeze,
		boolean glitter,
		boolean bump,
		boolean scream
	)
	{
		//scream     bump    glitter    breeze    stench
		//  1		  1			 1		   1        1
		int details = 0;
		if(stench)
			details = details + 1;
		if(breeze)
			details = details + (1 << 1);
		if(glitter)
			details = details + (1 << 2);
		if(bump)
			details = details + (1 << 3);
		if(scream)
			details = details + (1 << 4);
		return details;
	}
	// ======================================================================


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
		// ======================================================================
		// My precious code below
		KB.put(Coord(agentX,agentY), Details(stench,breeze,glitter,bump,scream));
		
		// ======================================================================
		if(scream)
			this.scream=true;
		//back to init point. and if user back to point over 2 times, it will climb. 
		if(agentX==0&&agentY==0) {
			//System.out.println("1");
			if(stench&&hasArrow) {
				hasArrow=false;
				return Action.SHOOT;
			}
			if(stench&&!hasArrow) {
				this.scream=true;
				visited.add("01");
				wumposition="01";
				isup=2;
			}
			point++;
			if(point>=isup)
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
			Clause clause=new Clause();
			clause.add("-P"+key);
			Pit_Clauses.add(clause);
			
			clause=new Clause();
			clause.add("-W"+key);
			Wum_Clauses.add(clause);
			
			
			visited.add(key);
			String last=stack.pop();
			agentX=last.charAt(0)-'0';
			agentY=last.charAt(1)-'0';
			if(agentDir==0)
				maxX=agentX;
			if(agentDir==3)
				maxY=agentY;
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
			safe.add(Coord(agentX+1, agentY));
			safe.add(Coord(agentX,agentY+1));
			safe.add(Coord(agentX-1,agentY));
			safe.add(Coord(agentX,agentY-1));
			
			ArrayList<String> neighs=new ArrayList<String>();
			if(Coord(agentX+1, agentY)!="e") {
				neighs.add(Coord(agentX+1, agentY));
			}
			if(Coord(agentX,agentY+1)!="e") {
				neighs.add(Coord(agentX,agentY+1));
			}
			if(Coord(agentX-1,agentY)!="e") {
				neighs.add(Coord(agentX-1,agentY));
			}
			if(Coord(agentX,agentY-1)!="e") {
				neighs.add(Coord(agentX,agentY-1));
			}
			addPitClause(key,neighs);
			Clause clause=new Clause();
			clause.add("-P"+key);
			Pit_Clauses.add(clause);
			addWumClause(key,neighs);
			clause=new Clause();
			clause.add("-S"+key);
			Wum_Clauses.add(clause);
			
			
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
			if(this.scream==true&&breeze==false) { //equal to safe node
				//System.out.println("1");
				safe.add(Coord(agentX+1, agentY));
				safe.add(Coord(agentX,agentY+1));
				safe.add(Coord(agentX-1,agentY));
				safe.add(Coord(agentX,agentY-1));
				
				ArrayList<String> neighs=new ArrayList<String>();
				if(Coord(agentX+1, agentY)!="e") {
					neighs.add(Coord(agentX+1, agentY));
				}
				if(Coord(agentX,agentY+1)!="e") {
					neighs.add(Coord(agentX,agentY+1));
				}
				if(Coord(agentX-1,agentY)!="e") {
					neighs.add(Coord(agentX-1,agentY));
				}
				if(Coord(agentX,agentY-1)!="e") {
					neighs.add(Coord(agentX,agentY-1));
				}
				addPitClause(key,neighs);
				Clause clause=new Clause();
				clause.add("-B"+key);
				Pit_Clauses.add(clause);
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
			}else if((stench==false||this.scream==true)&&breeze==true) {
				//System.out.println("2");
				if(safe.contains(Coord(agentX+1,agentY))&&(!visited.contains(Coord(agentX+1,agentY)))) {
					return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
				}
				if(safe.contains(Coord(agentX,agentY+1))&&(!visited.contains(Coord(agentX,agentY+1)))) {
					return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
				}
				if(safe.contains(Coord(agentX-1,agentY))&&(!visited.contains(Coord(agentX-1,agentY)))) {
					return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
				}
				if(safe.contains(Coord(agentX,agentY-1))&&(!visited.contains(Coord(agentX,agentY-1)))) {
					return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
				}
				ArrayList<String> neighs=new ArrayList<String>();
				if(Coord(agentX+1, agentY)!="e") {
					neighs.add(Coord(agentX+1, agentY));
				}
				if(Coord(agentX,agentY+1)!="e") {
					neighs.add(Coord(agentX,agentY+1));
				}
				if(Coord(agentX-1,agentY)!="e") {
					neighs.add(Coord(agentX-1,agentY));
				}
				if(Coord(agentX,agentY-1)!="e") {
					neighs.add(Coord(agentX,agentY-1));
				}
				addPitClause(key,neighs);
				Clause clause=new Clause();
				clause.add("B"+key);
				Pit_Clauses.add(clause);
				if((!safe.contains(Coord(agentX+1,agentY)))&&(!certain_pit.contains(Coord(agentX+1,agentY)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX+1,agentY));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX+1,agentY));
					if(isPit==false) {
						certain_pit.add(Coord(agentX+1,agentY));
						clause=new Clause();
						clause.add("P"+Coord(agentX+1,agentY));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX+1,agentY));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX+1,agentY));
						if(isPit==false) {
							//safe.add(Coord(agentX+1,agentY));
							clause=new Clause();
							clause.add("-P"+Coord(agentX+1,agentY));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX,agentY+1)))&&(!certain_pit.contains(Coord(agentX,agentY+1)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX,agentY+1));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX,agentY+1));
					if(isPit==false) {
						certain_pit.add(Coord(agentX,agentY+1));
						clause=new Clause();
						clause.add("P"+Coord(agentX,agentY+1));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX,agentY+1));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX,agentY+1));
						if(isPit==false) {
							//safe.add(Coord(agentX,agentY+1));
							clause=new Clause();
							clause.add("-P"+Coord(agentX,agentY+1));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX-1, agentY)))&&(!certain_pit.contains(Coord(agentX-1, agentY)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX-1, agentY));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX-1, agentY));
					if(isPit==false) {
						certain_pit.add(Coord(agentX-1, agentY));
						clause=new Clause();
						clause.add("P"+Coord(agentX-1, agentY));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX-1, agentY));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX-1, agentY));
						if(isPit==false) {
							//safe.add(Coord(agentX-1, agentY));
							clause=new Clause();
							clause.add("-P"+Coord(agentX-1, agentY));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX, agentY-1)))&&(!certain_pit.contains(Coord(agentX, agentY-1)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX, agentY-1));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX, agentY-1));
					if(isPit==false) {
						certain_pit.add(Coord(agentX, agentY-1));
						clause=new Clause();
						clause.add("P"+Coord(agentX, agentY-1));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX, agentY-1));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX, agentY-1));
						if(isPit==false) {
							//safe.add(Coord(agentX, agentY-1));
							clause=new Clause();
							clause.add("-P"+Coord(agentX, agentY-1));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
						}
					}
				}
				return return_action();
			}else if(stench==true&&breeze==false){
				//System.out.println("3");
				ArrayList<String> neighs=new ArrayList<String>();
				if(Coord(agentX+1, agentY)!="e") {
					neighs.add(Coord(agentX+1, agentY));
				}
				if(Coord(agentX,agentY+1)!="e") {
					neighs.add(Coord(agentX,agentY+1));
				}
				if(Coord(agentX-1,agentY)!="e") {
					neighs.add(Coord(agentX-1,agentY));
				}
				if(Coord(agentX,agentY-1)!="e") {
					neighs.add(Coord(agentX,agentY-1));
				}
				addPitClause(key,neighs);
				Clause clause=new Clause();
				clause.add("-B"+key);
				Pit_Clauses.add(clause);
				if(Coord(agentX+1,agentY)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX+1,agentY),key);
				}
				if(Coord(agentX,agentY+1)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX,agentY+1),key);
				}
				if(Coord(agentX-1,agentY)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX-1,agentY),key);
				}
				if(Coord(agentX,agentY-1)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX,agentY-1),key);
				}
				wumposition=WumpusMurder(agentX,agentY);
				if(wumposition!="") {
					//System.out.println(wumposition);
					if(hasArrow!=false)
					return shot_action(wumposition,key);
				}
				//call little brother's code
				return return_action();
			}else {
				//System.out.println("4");
				if(Coord(agentX+1,agentY)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX+1,agentY),key);
				}
				if(Coord(agentX,agentY+1)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX,agentY+1),key);
				}
				if(Coord(agentX-1,agentY)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX-1,agentY),key);
				}
				if(Coord(agentX,agentY-1)==wumposition) {
					if(hasArrow!=false)
					return shot_action(Coord(agentX,agentY-1),key);
				}
				wumposition=WumpusMurder(agentX,agentY); //what if we dont shoot.if we find the wumpus location. set a location dont go. shoot is -10
				
				if(wumposition!="") {
					//System.out.println(wumposition);
					if(hasArrow!=false)
					return shot_action(wumposition,key);
				}
				if(safe.contains(Coord(agentX+1,agentY))&&(!visited.contains(Coord(agentX+1,agentY)))) {
					return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
				}
				if(safe.contains(Coord(agentX,agentY+1))&&(!visited.contains(Coord(agentX,agentY+1)))) {
					return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
				}
				if(safe.contains(Coord(agentX-1,agentY))&&(!visited.contains(Coord(agentX-1,agentY)))) {
					return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
				}
				if(safe.contains(Coord(agentX,agentY-1))&&(!visited.contains(Coord(agentX,agentY-1)))) {
					return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
				}
				ArrayList<String> neighs=new ArrayList<String>();
				if(Coord(agentX+1, agentY)!="e") {
					neighs.add(Coord(agentX+1, agentY));
				}
				if(Coord(agentX,agentY+1)!="e") {
					neighs.add(Coord(agentX,agentY+1));
				}
				if(Coord(agentX-1,agentY)!="e") {
					neighs.add(Coord(agentX-1,agentY));
				}
				if(Coord(agentX,agentY-1)!="e") {
					neighs.add(Coord(agentX,agentY-1));
				}
				addPitClause(key,neighs);
				Clause clause=new Clause();
				clause.add("B"+key);
				Pit_Clauses.add(clause);
				if((!safe.contains(Coord(agentX+1,agentY)))&&(!certain_pit.contains(Coord(agentX+1,agentY)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX+1,agentY));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX+1,agentY));
					if(isPit==false) {
						certain_pit.add(Coord(agentX+1,agentY));
						clause=new Clause();
						clause.add("P"+Coord(agentX+1,agentY));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX+1,agentY));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX+1,agentY));
						if(isPit==false) {
							//safe.add(Coord(agentX+1,agentY));
							clause=new Clause();
							clause.add("-P"+Coord(agentX+1,agentY));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX+1)+String.valueOf(agentY),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX,agentY+1)))&&(!certain_pit.contains(Coord(agentX,agentY+1)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX,agentY+1));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX,agentY+1));
					if(isPit==false) {
						certain_pit.add(Coord(agentX,agentY+1));
						clause=new Clause();
						clause.add("P"+Coord(agentX,agentY+1));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX,agentY+1));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX,agentY+1));
						if(isPit==false) {
							//safe.add(Coord(agentX,agentY+1));
							clause=new Clause();
							clause.add("-P"+Coord(agentX,agentY+1));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX)+String.valueOf(agentY+1),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX-1, agentY)))&&(!certain_pit.contains(Coord(agentX-1, agentY)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX-1, agentY));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX-1, agentY));
					if(isPit==false) {
						certain_pit.add(Coord(agentX-1, agentY));
						clause=new Clause();
						clause.add("P"+Coord(agentX-1, agentY));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX-1, agentY));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX-1, agentY));
						if(isPit==false) {
							//safe.add(Coord(agentX-1, agentY));
							clause=new Clause();
							clause.add("-P"+Coord(agentX-1, agentY));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX-1)+String.valueOf(agentY),key);
						}
					}
				}
				if((!safe.contains(Coord(agentX, agentY-1)))&&(!certain_pit.contains(Coord(agentX, agentY-1)))) {
					ArrayList<Clause> Pit_Clauses_copy=copy_list(Pit_Clauses);
					clause=new Clause();
					clause.add("-P"+Coord(agentX, agentY-1));
					Pit_Clauses_copy.add(clause);
					boolean isPit=Logic(Pit_Clauses_copy,"-P"+Coord(agentX, agentY-1));
					if(isPit==false) {
						certain_pit.add(Coord(agentX, agentY-1));
						clause=new Clause();
						clause.add("P"+Coord(agentX, agentY-1));
						Pit_Clauses.add(clause);
					}else {
						ArrayList<Clause> Pit_Clauses_copy1=copy_list(Pit_Clauses);
						clause=new Clause();
						clause.add("P"+Coord(agentX, agentY-1));
						Pit_Clauses_copy1.add(clause);
						isPit=Logic(Pit_Clauses_copy1,"P"+Coord(agentX, agentY-1));
						if(isPit==false) {
							//safe.add(Coord(agentX, agentY-1));
							clause=new Clause();
							clause.add("-P"+Coord(agentX, agentY-1));
							Pit_Clauses.add(clause);
							return forward_action(String.valueOf(agentX)+String.valueOf(agentY-1),key);
						}
					}
				}
				
				return  return_action();
			}
		}else {
			//System.out.println("5");
			return return_action();
		}
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	public Action return_action() {
		if(stack.isEmpty())
			return Action.CLIMB;
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
	public Action shot_action(String next_cur,String cur) {
		if((next_cur.charAt(0)-'0'-agentX)==-1) {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				agentDir=2;
				return Action.TURN_RIGHT;
			}else if(agentDir==2) {
				return Action.SHOOT;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}else if((next_cur.charAt(0)-'0'-agentX)==1) {
			if(agentDir==0) {
				return Action.SHOOT;
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
				return Action.SHOOT;
			}
		}else {
			if(agentDir==0) {
				agentDir=1;
				return Action.TURN_RIGHT;
			}else if(agentDir==1) {
				return Action.SHOOT;
			}else if(agentDir==2) {
				agentDir=1;
				return Action.TURN_LEFT;
			}else {
				agentDir=2;
				return Action.TURN_LEFT;
			}
		}
	}
	public void addPitClause(String cur,ArrayList<String> neighs) {
		Clause pit_clause=new Clause();
		pit_clause.add("-B"+cur);
		for(int i=0;i<neighs.size();i++) {
			pit_clause.add("P"+neighs.get(i));
		}
		Pit_Clauses.add(pit_clause);
		
		for(int i=0;i<neighs.size();i++) {
			pit_clause=new Clause();
			pit_clause.add("-P"+neighs.get(i));
			pit_clause.add("B"+cur);
			Pit_Clauses.add(pit_clause);
		}
	}
	public void addWumClause(String cur,ArrayList<String> neighs) {
		Clause wum_clause=new Clause();
		wum_clause.add("-S"+cur);
		for(int i=0;i<neighs.size();i++) {
			wum_clause.add("W"+neighs.get(i));
		}
		Wum_Clauses.add(wum_clause);
		
		for(int i=0;i<neighs.size();i++) {
			wum_clause=new Clause();
			wum_clause.add("-W"+neighs.get(i));
			wum_clause.add("S"+cur);
			Wum_Clauses.add(wum_clause);
		}
	}
	public ArrayList<Clause> copy_list(ArrayList<Clause> ori){
		ArrayList<Clause> copy=new ArrayList<Clause>();
		for(Clause c: ori)
		{
			Clause c1 = new Clause();
			for(String s: c.singles)
			{
				c1.add(s);
			}
			copy.add(c1);
		}
		return copy;
	}
	boolean hasFalsehood(ArrayList<Clause> Clauses)
	{
		ArrayList<String> singleLiterals = new ArrayList<String>();
		for(Clause c: Clauses)
		{
			if(c.singles.size() == 1)
			{
				singleLiterals.add(c.singles.get(0));
			}
		}
		for(String sl : singleLiterals)
		{
			String sl_opposite;
			if(sl.startsWith("-")) sl_opposite = sl.substring(1);
			else sl_opposite = "-"+sl;
			for(Clause c: Clauses)
			{
				if(c.singles.size() == 1)
				{
					if(c.singles.get(0).equals(sl_opposite))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	void cutClauses(String literal,ArrayList<Clause> Clauses)
	{
		String cutLiteral;
		if(literal.startsWith("-")) cutLiteral = literal.substring(1);
		else cutLiteral = "-"+literal;
		for(Clause c: Clauses)
		{
			c.singles.remove(cutLiteral);
		}
	}
	void removeClauses(String literal,ArrayList<Clause> Clauses)
	{
		ArrayList<Clause> clausesToRemove = new ArrayList<Clause>();
		for(Clause c: Clauses)
		{
			for(String l: c.singles)
			{
				if(l.equals(literal))
				{
					clausesToRemove.add(c);
				}	
			}
		}
		for(Clause c : clausesToRemove)
		{
			Clauses.remove(c);
		}	
	}
	String searchSingleLiteral(ArrayList<Clause> Clauses)
	{
		String literalToRemove = "NotFoundYet";
		for(Clause c: Clauses)
		{
			if(c.singles.size() == 1)
			{
				literalToRemove = c.singles.get(0);
				break;
			}
		}
		return literalToRemove;
	}
	boolean Logic(ArrayList<Clause> Clauses,String out)
	{
		
		removeClauses(out,Clauses);
		cutClauses(out,Clauses);
		if(Clauses.size() == 0) 
		{
			//System.out.println("All clauses removed. Returning true.");
			return true;
		}
		if(hasFalsehood(Clauses)) 
		{
			//System.out.println("Falsehood detected. Returning false.");
			return false;
		}
		else if(hasEmptyClause(Clauses))
		{
			//System.out.println("Empty clause detected. Returning false.");
			return false;
		}
		while(true)
		{	
			String out1 = searchSingleLiteral(Clauses);
			if(!out1.equals("NotFoundYet"))
			{
				removeClauses(out1,Clauses);
				cutClauses(out1,Clauses);
				if(Clauses.size() == 0) 
				{
					//System.out.println("All clauses removed. Returning true.");
					return true;
				}
				if(hasFalsehood(Clauses)) 
				{
					//System.out.println("Falsehood detected. Returning false.");
					return false;
				}
				else if(hasEmptyClause(Clauses))
				{
					//System.out.println("Empty clause detected. Returning false.");
					return false;
				}
			}
			else
				break;
		}
		return true;
	}
	boolean hasEmptyClause(ArrayList<Clause> Clauses)
	{
		for(Clause c: Clauses)
		{
			if(c.singles.size() == 0)
			{
				return true;
			}
		}
		return false;
	}
	public String WumpusMurder
	(int x, int y) 
	{
		ArrayList<String> Ws = new ArrayList<String>();
		boolean s1=false,s2=false,s3=false,s4=false,s5=false,s6=false,s7=false,s8=false;
		for(int i = 0; i < 2; i++)
		{
			String foo = Coord((x-1)+i*2,y);
			String bar = Coord(x, (y-1)+i*2);
			if( (foo != "e") && !safe.contains(foo) )
				Ws.add(foo);
			if( (bar != "e") && !safe.contains(bar) )
				Ws.add(bar);
		}
		
		for(int i = 0; i < Ws.size(); i++)
		{
			String temp = Ws.get(i);
			int Wx=temp.charAt(0)-'0';
			int Wy=temp.charAt(1)-'0';
			if(Wx < x) 
			{
				String S1 = Coord(Wx-1 , Wy);
				int S1V = KB.containsKey(S1)? KB.get(S1):0;
				String S2 = Coord(Wx , Wy+1);
				int S2V = KB.containsKey(S2)? KB.get(S2):0;
				String S8 = Coord(Wx , Wy-1);
				int S8V = KB.containsKey(S8)? KB.get(S8):0;				
				
				if( ((S1V & 1) == 1) || ( ((S2V & 1) == 1) && ((S8V & 1) == 1) ) )
					return Ws.get(i);	
				else if ((S2V & 1) == 1)
					s2 = true;
				else if ((S8V & 1) == 1)
					s8 = true;
			}
			if(Wx > x) 
			{
				String S5 = Coord(Wx+1 , Wy);
				int S5V = KB.containsKey(S5)? KB.get(S5):0;
				String S4 = Coord(Wx , Wy+1);
				int S4V = KB.containsKey(S4)? KB.get(S4):0;
				String S6 = Coord(Wx , Wy-1);
				int S6V = KB.containsKey(S6)? KB.get(S6):0;				
				
				if( ((S5V & 1) == 1) || ( ((S4V & 1) == 1) && ((S6V & 1) == 1) ) )
					return Ws.get(i);	
				else if ((S4V & 1) == 1)
					s4 = true;
				else if ((S6V & 1) == 1)
					s6 = true;
			}
			if(Wy < y) 
			{
				String S7 = Coord(Wx , Wy-1);
				int S7V = KB.containsKey(S7)? KB.get(S7):0;
				String S8 = Coord(Wx-1 , Wy);
				int S8V = KB.containsKey(S8)? KB.get(S8):0;
				String S6 = Coord(Wx+1 , Wy);
				int S6V = KB.containsKey(S6)? KB.get(S6):0;				
				
				if( ((S7V & 1) == 1) || ( ((S6V & 1) == 1) && ((S8V & 1) == 1) ) )
					return Ws.get(i);
				else if ((S6V & 1) == 1)
					s6 = true;
				else if ((S8V & 1) == 1)
					s8 = true;
			}
			if(Wy > y) 
			{
				String S3 = Coord(Wx , Wy+1);
				int S3V = KB.containsKey(S3)? KB.get(S3):0;
				String S2 = Coord(Wx-1 , Wy);
				int S2V = KB.containsKey(S2)? KB.get(S2):0;
				String S4 = Coord(Wx+1 , Wy);
				int S4V = KB.containsKey(S4)? KB.get(S4):0;				
				
				if( ((S3V & 1) == 1) || ( ((S2V & 1) == 1) && ((S4V & 1) == 1) ) )
					return Ws.get(i);		
				else if ((S2V & 1) == 1)
					s2 = true;
				else if ((S4V & 1) == 1)
					s4 = true;
			}
			if(s6 )
			{
				String a="";
				if(safe.contains(Coord(x,y-1)))
					a =  Coord(x+1,y);
				if(safe.contains(Coord(x+1,y)))
					a =  Coord(x,y-1);
				if(a != "e")
					return a;
			}
			if(s8 )
			{
				String a="";
				if(safe.contains(Coord(x-1,y)))
					a =  Coord(x,y-1);
				if(safe.contains(Coord(x,y-1)))
					a =  Coord(x-1,y);
				if(a != "e")
					return a;
			}
			if(s2 )
			{
				String a="";
				if(safe.contains(Coord(x-1,y)))
					a =  Coord(x,y+1);
				if(safe.contains(Coord(x,y+1)))
					a =  Coord(x-1,y);
				if(a != "e")
					return a;
			}
			if(s4 )
			{
				String a="";
				if(safe.contains(Coord(x,y+1)))
					a =  Coord(x+1,y);
				if(safe.contains(Coord(x+1,y)))
					a =  Coord(x,y+1);
				if(a != "e")
					return a;
			}

		}
		return "";
	
	}


	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================


	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}