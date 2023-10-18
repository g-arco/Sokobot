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
    private int[] moveX = {0, 0, -1, 1};
    private int[] moveY = {-1, 1, 0, 0};
    private String[] moveActions = {"u", "d", "l", "r"};

    private ArrayList<Position> goals;

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData){
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

        this.goals = new ArrayList<>();
        findGoals(mapData);

        PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(State::getHeuristic));

        ArrayList<State> visitedStates = new ArrayList<State>();

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
            State currentState = openSet.poll();

            if(visitedStates.contains(currentState))
                continue;

            visitedStates.add(currentState);

        System.out.println("****");
          if(currentState.actions != null)
          {
              System.out.println(currentState.actions);
          }

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
        }*/



            for (int move =0; move < 4; move++)
            {
                State successor = generateSuccessors(currentState, move);

                if (successor != null)
                {
                    System.out.println("****" + openSet.size());
                    System.out.println(!visitedStates.contains(successor));

                    if (!visitedStates.contains(logPrevData)) {
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


    private State generateSuccessors(State state, int i) throws Exception {

        char[][] board = state.cloneItems(state.getItemsData());
        char[][] newItemData;

        /*
        System.out.println("===Reference Map====");
        for(int x = 0; x < currState.getItemsData().length; x++) {
            for (int y = 0; y < currState.getItemsData()[x].length; y++)
                System.out.print(currState.getItemsData()[x][y]);
            System.out.println();
        }*/

        int playerX = state.player.getX();
        int playerY = state.player.getY();


        // Define possible moves (up, down, left, right)

        int newX = playerX + moveX[i];
        int newY = playerY + moveY[i];
        Position positions = new Position(newX,newY);

        if (this.mapData[newY][newX]!= '#' && board[newY][newX] != '$') {
            //System.out.println(newX+"Not a box"+ newY);
            newItemData = state.setNewPosition(board,0, positions, state.player,0, 0);
            if(newItemData!=null)
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }
        else if (board[newY][newX] == '$'&& this.mapData[newY+moveY[i]][newX+moveX[i]] != '#' && board[newY+ moveY[i]][newX+ moveX[i]] != '$')
        {
            Position box = new Position(newX+ moveX[i], newY+ moveY[i]);
            newItemData = state.setNewPosition(board, 1, positions, box, moveX[i], moveY[i]);
            if(newItemData!=null)
                return new State(state, state.getActions() + moveActions[i], newItemData);
        }

        System.out.println("+++++++");

        return null;
    }

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


    private int getCost(State currState){

        int cost= 0;


        ArrayList<Position> boxes = new ArrayList<>();
        int obstacles = 0;

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
            int minDistance = Integer.MAX_VALUE;

            // Calculate the Manhattan distance from the box to the closest goal
            for (Position goalPos : this.goals) {
                int distance = Math.abs(boxPos.x - goalPos.x) + Math.abs(boxPos.y - goalPos.y);
                if (distance < minDistance)
                    minDistance = distance;
            }

            cost += minDistance;
        }
        /*
        for (Position goalPos : this.goals) {
            if (currState.getItemsData()[goalPos.getY()][goalPos.getY()] != '$' ) {
                cost++;
            }
        }*/



        //System.out.println(cost);
        return cost+obstacles;

    }


}