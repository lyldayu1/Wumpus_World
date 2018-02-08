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
	Action lastAction;
	HashSet<String> unvisited;
	HashSet<String> safe;
	HashSet<String> uncertain;
	HashMap<String,ArrayList<Integer>> map;
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
		
		return Action.CLIMB;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================


	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}