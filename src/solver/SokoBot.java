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
    private Position lastPlayerPos;

    private ArrayList<Position> goals;

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
      this.width = width;
      this.height = height;
      this.mapData = mapData;
      this.itemsData = itemsData;
      this.goals = new ArrayList<>();
      findGoals(this.mapData);


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
      String vistedActions = "";

      // Add the initial state to the open set
      State initialState = initializeState();// Define how to initialize the initial state.


        System.out.println("InState Map");
        for(int x = 0; x < initialState.getItemsData().length; x++) {
            for (int y = 0; y < initialState.getItemsData().length; y++)
                System.out.print(initialState.getItemsData()[x][y]);
            System.out.println();
        }

        initialState.setHeuristic(getCost(initialState));
        openSet.add(initialState);
        actions.put(initialState, new ArrayList<>());


        System.out.println("+++++++");
      while (!openSet.isEmpty())
      {
        State currentState = openSet.remove();

        if(visitedStates.contains(currentState))
            continue;

        visitedStates.add(currentState);
        //vistedActions.concat(currentState.actions);

        System.out.println("+++++++");
          if(currentState.actions != null)
          {
              System.out.println(currentState.actions);
          }

        if (currentState.isGoalState()) {
            return currentState.actions;
        }

        //int currentCost = cost(actions.get(currentState));

        System.out.println("CurrState " + currentState.getHeuristic());
        for(int x = 0; x < currentState.getItemsData().length; x++) {
          for (int y = 0; y < currentState.getItemsData()[x].length; y++)
              System.out.print(currentState.getItemsData()[x][y]);
          System.out.println();
        }

        List<State> successors = generateSuccessors(currentState);

        for (State successor : successors)
        {
            System.out.println(!visitedStates.contains(successor));

            if (!visitedStates.contains(successor)) {
                successor.setHeuristic(getCost(successor));
                openSet.add(successor);

                for(int x = 0; x < successor.getItemsData().length; x++) {
                    for (int y = 0; y < successor.getItemsData()[x].length; y++)
                        System.out.print(successor.getItemsData()[x][y]);
                    System.out.println();
                }

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
        return new State(this.itemsData, this.mapData, "");
    }

    private ArrayList<State> generateSuccessors(State state) throws Exception {
        ArrayList<State> newStates = new ArrayList<>();
        Position positions = new Position();
        State origState = new State(state.getItemsData(), this.mapData, state.actions);
        State currState = new State(state.getItemsData(), this.mapData, state.actions);
        char[][] newItemData;


        System.out.println("===Reference Map====");
        for(int x = 0; x < currState.getItemsData().length; x++) {
            for (int y = 0; y < currState.getItemsData()[x].length; y++)
                System.out.print(currState.getItemsData()[x][y]);
            System.out.println();
        }

        Position playerPosition = currState.findPlayer(currState.getItemsData());
        int playerX = playerPosition.getX();
        int playerY = playerPosition.getY();


        // Define possible moves (up, down, left, right)
        int[] moveX = {0, 0, -1, 1};
        int[] moveY = {-1, 1, 0, 0};
        String[] moveActions = {"u", "d", "l", "r"};



        for (int i = 0; i < moveX.length; i++) {
            int newX = playerX + moveX[i];
            int newY = playerY + moveY[i];
            positions = new Position(newX,newY);

            currState.setItemsData(origState.getItemsData());


            if (this.mapData[newY][newX]!= '#' && currState.getItemsData()[newY][newX] != '$') {
                System.out.println(newX+"Not a box"+ newY);
                newItemData = currState.setNewPosition(0, positions, playerPosition, playerPosition,0, 0);
                newStates.add(new State(currState, currState.getActions() + moveActions[i], newItemData));
            }
            else if (currState.getItemsData()[newY][newX] == '$'&& this.mapData[newY+moveY[i]][newX+moveX[i]] != '#' && currState.getItemsData()[newY+ moveY[i]][newX+ moveX[i]] != '$')
            {
                Position box = new Position(newX+ moveX[i], newY+ moveY[i]);
                newItemData = currState.setNewPosition(1, positions, box, playerPosition, moveX[i], moveY[i]);
                newStates.add(new State(currState, currState.getActions() + moveActions[i], newItemData));
                currState.setItemsData(origState.getItemsData());
            }

        }

        System.out.println("+++++++");


        return newStates;
    }

    private int cost(List<String> actions) {
      // A cost function to calculate the cost based on the number of lowercase letters in the actions.
      return (int) actions.stream().filter(action -> Character.isLowerCase(action.charAt(0))).count();
    }

    private ArrayList<Position> findGoals(char[][] board)
    {

        ArrayList<Position> goals = new ArrayList<Position>();
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (c == '.')
                {
                    this.goals.add(new Position(x, y));
                }
            }
        }
        return goals;
    }

    private int getCost(State currState){

        int cost= currState.actions.length();
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
        }

        System.out.println(cost);
        return cost;

    }


}
