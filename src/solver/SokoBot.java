package solver;
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
      Set<State> visitedStates = new HashSet<>();



      // Add the initial state to the open set
      State initialState = initializeState();// Define how to initialize the initial state.



        System.out.println("InState Map");
        for(int x = 0; x < initialState.getItemsData().length; x++) {
            for (int y = 0; y < initialState.getItemsData().length; y++)
                System.out.print(initialState.getItemsData()[x][y]);
            System.out.println();
        }

        openSet.add(initialState);
        actions.put(initialState, new ArrayList<>());

        System.out.println("+++++++");
      while (!openSet.isEmpty())
      {
        State currentState = openSet.remove();

        visitedStates.add(initialState);

        System.out.println("+++++++");
          if(currentState.actions != null)
          {
              System.out.println(currentState.actions);
          }

        if (currentState.isGoalState()) {
            return currentState.actions;
        }

        //int currentCost = cost(actions.get(currentState));

        System.out.println("CurrState");
        for(int x = 0; x < currentState.stateData.length; x++) {
          for (int y = 0; y < currentState.stateData[x].length; y++)
              System.out.print(currentState.stateData[x][y]);
          System.out.println();
        }

        List<State> successors = generateSuccessors(currentState);

        for (State successor : successors)
        {
            System.out.println(!visitedStates.contains(successor));

            if (!visitedStates.contains(successor)) {
                successor.setHeuristic(getCost(successor, successor.goals));
                openSet.add(successor);

          }
        }
      }

      return "lrrlrlrlrllrrlrlrlrlrlrlrr";
    }

    private State initializeState() throws Exception {
      // Implement how to initialize the initial state based on the mapData and itemsData.
      // ...
        for(int  z= 0; z< mapData.length; z++) {
            for (int j = 0; j < mapData[z].length; j++)
                System.out.print(mapData[z][j]);
            System.out.println();
        }
        System.out.println("x");
        for(int  z= 0; z< this.itemsData.length; z++) {
            for (int j = 0; j < this.itemsData[z].length; j++)
                System.out.print(this.itemsData[z][j]);
            System.out.println();
        }
        System.out.println("x");
        return new State(this.itemsData, this.mapData);
    }

    private ArrayList<State> generateSuccessors(State state) throws Exception {
        ArrayList<State> newStates = new ArrayList<>();
        Position positions = new Position();
        State origState = new State(state);
        State currState = new State(state);
        char[][] newItemData;
        Position playerPosition = new Position();


        System.out.println("===Reference Map====");
        for(int x = 0; x < state.getItemsData().length; x++) {
            for (int y = 0; y < state.getItemsData()[x].length; y++)
                System.out.print(state.getItemsData()[x][y]);
            System.out.println();
        }

        for (int y = 0; y < currState.getItemsData().length; y++)
        {
            for (int x = 0; x < currState.getItemsData()[y].length; x++)
            {
                char c = currState.getItemsData()[y][x];
                if (c == '@' )
                    playerPosition = new Position(x, y);
            }
        }

        int playerX = playerPosition.getX();
        int playerY = playerPosition.getY();

        ArrayList<Position> boxes = new ArrayList<>();

        for(int i = 0; i< currState.getItemsData().length;i++){
            for(int j = 0; j<currState.getItemsData()[0].length;j++){
                if (currState.getItemsData()[i][j] == '$'){
                    boxes.add(new Position(i,j));
                }
            }
        }

        // Define possible moves (up, down, left, right)
        int[] moveX = {0, 0, -1, 1};
        int[] moveY = {-1, 1, 0, 0};
        String[] moveActions = {"d", "u", "r", "l"};

        for (int i = 0; i < moveX.length; i++) {
            int newX = playerX + moveX[i];
            int newY = playerY + moveY[i];
            positions = new Position(newX,newY);

            currState.setItemsData(origState.getItemsData());

            int boxIndex = 0;
            for (Position box : boxes)
            {
                System.out.println(box.getX() + " "+ box.getY() + " "+ newX+ " "+ newY);
                if (box.getX() == newX && box.getY() == newY)
                {
                    if (!currState.isWall(newX+ moveX[i], newY+ moveY[i]) && currState.getItemsData()[newY+ moveY[i]][newX+ moveX[i]] != '$') {
                        newItemData = currState.setNewPosition(boxIndex, positions, box, playerPosition, moveX[i], moveY[i]);
                        newStates.add(new State(currState, currState.getActions() + moveActions[i], newItemData));
                        System.out.println(boxIndex);
                        currState.setItemsData(origState.getItemsData());
                    }
                }
                boxIndex++;
            }

            if (!currState.isWall(newX, newY) && currState.getItemsData()[newY][newX] != '$') {
                System.out.println(newX+"Not a box"+ newY);
                newItemData = currState.setNewPosition(0, positions, playerPosition, playerPosition,0, 0);
                newStates.add(new State(currState, currState.getActions() + moveActions[i], newItemData));
            }

        }

        System.out.println("+++++++");


        return newStates;
    }

    private int cost(List<String> actions) {
      // A cost function to calculate the cost based on the number of lowercase letters in the actions.
      return (int) actions.stream().filter(action -> Character.isLowerCase(action.charAt(0))).count();
    }

    private int getCost(State currState, ArrayList<Position> goals){

        int cost=0;
        ArrayList<Position> boxes = new ArrayList<>();

        for(int i = 0; i< currState.getItemsData().length;i++){
            for(int j = 0; j<currState.getItemsData()[0].length;j++){
                if (currState.getItemsData()[i][j] == '$'){
                    boxes.add(new Position(i,j));
                }
            }
        }

        for (Position boxPos : boxes) {
            int minDistance = Integer.MAX_VALUE;

            // Calculate the Manhattan distance from the box to the closest goal
            for (Position goalPos : goals) {
                int distance = Math.abs(boxPos.x - goalPos.x) + Math.abs(boxPos.y - goalPos.y);
                minDistance = Math.min(minDistance, distance);
            }

            cost += minDistance;
        }

        return cost;

    }


}
