package solver;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;


public class SokoBot {

    private int width;
    private int height;
    private char[][] mapData;
    private char[][] itemsData;
    private int tentativeCost;
    private Position lastPlayerPos;

    private ArrayList<Position> goals;

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        this.width = width;
        this.height = height;
        this.mapData = mapData;
        this.itemsData = itemsData;


        try {
            return solveSokobanAStar();
            // Process the solution here
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception or print the stack trace
            // You may want to log the exception or take appropriate action here
        }


        return "lrlrllrlrlrllr";

    }

    public String solveSokobanAStar() throws Exception {
        // Initialize data structures and use width, height, mapData, and itemsData as needed.
        // ...

        PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(State::getHeuristic));

        Map<State, List<String>> actions = new HashMap<State, List<String>> ();
        ArrayList<State> visitedStates = new ArrayList<>();
        String vistedActions = "";

        // Add the initial state to the open set
        State initialState = new State(this.itemsData, this.mapData, "");// Define how to initialize the initial state.

      /*

        System.out.println("InState Map");
        for(int x = 0; x < initialState.getItemsData().length; x++) {
            for (int y = 0; y < initialState.getItemsData()[x].length; y++)
                System.out.print(initialState.getItemsData()[x][y]);
            System.out.println();
        }*/

        initialState.setHeuristic(getCost(initialState));
        openSet.add(initialState);
        int logPrevData = 0;


        //System.out.println("+++++++");
        while (!openSet.isEmpty())
        {
            State currentState = openSet.remove();

            if(visitedStates.contains(currentState))
                continue;

            visitedStates.add(currentState);

        /*System.out.println("+++++++");
          if(currentState.actions != null)
          {
              System.out.println(currentState.actions);
          }*/

            if (currentState.isGoalState()) {
                System.out.println("Done");
                return currentState.actions;
            }

            //int currentCost = cost(actions.get(currentState));

        /*
        System.out.println("CurrState " + currentState.getHeuristic());
        for(int x = 0; x < currentState.getItemsData().length; x++) {
          for (int y = 0; y < currentState.getItemsData()[x].length; y++)
              System.out.print(currentState.getItemsData()[x][y]);
          System.out.println();
        }
        */


            for (int move =0; move < 4; move++)
            {
                State successor;
                if (logPrevData < 2)
                    successor = generateSuccessors(currentState, move, this.itemsData, 0);
                else
                    successor = generateSuccessors(currentState, move, visitedStates.get(visitedStates.size()-2).getItemsData(), 1);

                if (successor != null)
                {
                    //System.out.println(!visitedStates.contains(successor));

                    if (!visitedStates.contains(successor)) {
                        successor.setHeuristic(getCost(successor));
                        openSet.add(successor);

                     /*
                     for(int x = 0; x < successor.getItemsData().length; x++) {
                         for (int y = 0; y < successor.getItemsData()[x].length; y++)
                             System.out.print(successor.getItemsData()[x][y]);
                         System.out.println();
                     }*/
                    }
                }
            }

            logPrevData++;
        }

        return "lrrlrlrlrllrrlrlrlrlrlrlrr";
    }


    private State generateSuccessors(State state, int i, char[][] prevData, int logPrevAvail) throws Exception {

        char[][] board = state.cloneItems(state.getItemsData());
        char[][] newItemData;

        /*
        System.out.println("===Reference Map====");
        for(int x = 0; x < currState.getItemsData().length; x++) {
            for (int y = 0; y < currState.getItemsData()[x].length; y++)
                System.out.print(currState.getItemsData()[x][y]);
            System.out.println();
        }*/

        Position playerPosition = state.findPlayer(state.getItemsData());
        int playerX = playerPosition.getX();
        int playerY = playerPosition.getY();


        // Define possible moves (up, down, left, right)
        int[] moveX = {0, 0, -1, 1};
        int[] moveY = {-1, 1, 0, 0};
        String[] moveActions = {"u", "d", "l", "r"};

        int newX = playerX + moveX[i];
        int newY = playerY + moveY[i];
        Position positions = new Position(newX,newY);

        if (this.mapData[newY][newX]!= '#' && board[newY][newX] != '$') {
            //System.out.println(newX+"Not a box"+ newY);
            newItemData = state.setNewPosition(board,0, positions, playerPosition, playerPosition,0, 0);
            if(newItemData!=null && (!newItemData.equals(prevData) || logPrevAvail == 0))
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }
        else if (board[newY][newX] == '$'&& this.mapData[newY+moveY[i]][newX+moveX[i]] != '#' && board[newY+ moveY[i]][newX+ moveX[i]] != '$')
        {
            Position box = new Position(newX+ moveX[i], newY+ moveY[i]);
            newItemData = state.setNewPosition(board, 1, positions, box, playerPosition, moveX[i], moveY[i]);
            if(newItemData!=null && (!newItemData.equals(prevData) || logPrevAvail == 0))
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }

        //System.out.println("+++++++");

        return null;
    }


    private int getCost(State currState){

        int cost= 0;
        /*
        ArrayList<Position> boxes = new ArrayList<>();

        for(int i = 0; i< currState.getItemsData().length;i++){
            for(int j = 0; j< currState.getItemsData()[i].length;j++){
                if (currState.getItemsData()[i][j] == '$'){
                    boxes.add(new Position(i,j));
                }
            }
        }

        for (Position boxPos : boxes) {
            int minDistance = Integer.MAX_VALUE;

            // Calculate the Manhattan distance from the box to the closest goal
            for (Position goalPos : this.goals) {
                int distance = Math.abs(boxPos.x - goalPos.x) + Math.abs(boxPos.y - goalPos.y);
                if (distance < minDistance)
                    minDistance = distance;
            }

            cost += minDistance;
        }*/
        for(int i = 0; i< currState.getItemsData().length;i++){
            for(int j = 0; j< currState.getItemsData()[i].length;j++){
                if (currState.getItemsData()[i][j] == '$' && this.mapData[i][j] != '.'){
                    cost++;
                }
            }
        }


        //System.out.println(cost);
        return cost;

    }


}