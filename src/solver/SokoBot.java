package solver;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * This class will be the main solver class that will use A* search
 * to solve the Sokoban puzzles
 */

public class SokoBot {

    private int width;
    private int height;
    private char[][] mapData;
    private char[][] itemsData;
    private int[] moveX = {0, 0, -1, 1};
    private int[] moveY = {-1, 1, 0, 0};
    private String[] moveActions = {"u", "d", "l", "r"};
    private ArrayList<Position> goals;

    /**
     * This is the method called by BotThread in order to activate the sokobot
     * @param width width of the map
     * @param height height of the map
     * @param mapData the array that contains information about the walls and goals location
     * @param itemsData the array that contains information about the initial position of the boxes and character
     * @return the string containing the solution
     */
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData){
        this.width = width;
        this.height = height;
        this.mapData = mapData;
        this.itemsData = itemsData;


        try {
            return solveSokobanAStar();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "lrlrllrlrlrllr";

    }

    /**
     * This is the A* method that will use A* algorithm
     * @return the string solution
     * @throws Exception
     */
    public String solveSokobanAStar() throws Exception {

        //gets the goal positions
        this.goals = new ArrayList<>();
        findGoals(mapData);

        //priority set for the available states
        PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(State::getHeuristic));

        //to stop visiting previously visited states
        HashSet<State> visitedStates = new HashSet<State>();

        // to add the initial state to the open set
        State initialState = new State(this.itemsData, this.mapData, "");
        initialState.setHeuristic(getCost(initialState));
        openSet.add(initialState);

        while (!openSet.isEmpty())
        {
            State currentState = openSet.poll();

            //checks if the curr state is visited
            if(visitedStates.contains(currentState))
                continue;


            if (currentState.isGoalState()) {
                return currentState.actions;
            }



            int succNo = 0;
            for (int move =0; move < 4; move++)
            {
                State successor = generateSuccessors(currentState, move);

                if (successor != null)
                {

                    if (!visitedStates.contains(successor)) {
                        successor.setHeuristic(getCost(successor));
                        openSet.add(successor);
                        succNo++;

                    }
                }
            }

            //adds it to visited states only if it is not a dead end
            if (succNo > 0)
                visitedStates.add(currentState);
        }

        return "lrrlrlrlrllrrlrlrlrlrlrlrr";
    }


    /**
     * This method will generate a new successor
     * @param state the parent state
     * @param i the move (up/down/left/right)
     * @return the new generated state or null
     * @throws Exception
     */
    private State generateSuccessors(State state, int i) throws Exception {

        char[][] board = state.cloneItems(state.getItemsData());
        char[][] newItemData;

        //gets player position
        int playerX = state.player.getX();
        int playerY = state.player.getY();

        //creates a new possible position
        int newX = playerX + moveX[i];
        int newY = playerY + moveY[i];
        Position positions = new Position(newX,newY);

        //if it doesn't involve using a box
        if (this.mapData[newY][newX]!= '#' && board[newY][newX] != '$') {
            newItemData = state.setNewPosition(board,0, positions, state.player,0, 0);
            if(newItemData!=null)
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }
        //if involves using a box
        else if (board[newY][newX] == '$'&& this.mapData[newY+moveY[i]][newX+moveX[i]] != '#' && board[newY+ moveY[i]][newX+ moveX[i]] != '$')
        {
            Position box = new Position(newX+ moveX[i], newY+ moveY[i]);
            newItemData = state.setNewPosition(board, 1, positions, box, moveX[i], moveY[i]);
            if(newItemData!=null)
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }

        //if no successor was generated
        return null;
    }

    /**
     * This method find the goals
     * @param board the mapData that contains information about the goal positions
     * @throws Exception
     */
    private void findGoals(char[][] board) throws Exception
    {
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (c == '.' )
                    this.goals.add(new Position(x, y));
            }
        }
    }


    /**
     * This method computes for the cost (heuristic for each state)
     * @param currState the state we are looking into
     * @return the cost (heuristic for each state)
     */
    private int getCost(State currState){

        int cost= 0;

        ArrayList<Position> boxes = new ArrayList<>();
        int obstacles = 0;

        //only gets from boxes NOT IN GOAL POSITION and adds on if there is a wall or box around it
        for(int i = 0; i< currState.getItemsData().length;i++){
            for(int j = 0; j< currState.getItemsData()[i].length;j++){
                if (currState.getItemsData()[i][j] == '$' && this.mapData[i][j] != '.'){
                    boxes.add(new Position(i,j));
                    if (currState.getItemsData()[i+1][j] == '$' || this.mapData[i+1][j] == '#')
                        obstacles++;
                    if (currState.getItemsData()[i+1][j-1] == '$' || this.mapData[i+1][j-1] == '#')
                        obstacles++;
                    if (currState.getItemsData()[i+1][j+1] == '$' || this.mapData[i+1][j+1]  == '#')
                        obstacles++;
                    if (currState.getItemsData()[i-1][j] == '$' || this.mapData[i-1][j] == '#')
                        obstacles++;
                    if (currState.getItemsData()[i-1][j-1] == '$' || this.mapData[i-1][j-1]  == '#')
                        obstacles++;
                    if (currState.getItemsData()[i-1][j+1] == '$' || this.mapData[i-1][j+1] == '#')
                        obstacles++;
                    if (currState.getItemsData()[i][j+1] == '$' || this.mapData[i][j+1]== '#')
                        obstacles++;
                    if (currState.getItemsData()[i][j-1] == '$' || this.mapData[i][j-1] == '#')
                        obstacles++;
                }
            }
        }

        for (Position boxPos : boxes) {
            int minDistance = 1000;
            // calculates the manhattan distance from each box to the closest goal
            for (Position goalPos : this.goals) {
                int distance = Math.abs(boxPos.x - goalPos.x) + Math.abs(boxPos.y - goalPos.y);
                if (distance < minDistance)
                    minDistance = distance;
            }

            cost += minDistance;
        }

        return cost+obstacles;
    }


}