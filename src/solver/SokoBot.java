package solver;
import java.util.*;
import java.util.Map;


public class SokoBot {

    private int width;
    private int height;
    private char[][] mapData;
    private char[][] itemsData;
    public Map<State, Integer> estimatedFValue;
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

        PriorityQueue<State> openSet = new PriorityQueue<>(new Comparator<State>() {
            @Override
            public int compare(State state1, State state2) {
                // Compare states based on their estimated F-values
                return Integer.compare(state1.getFValue(), state2.getFValue());
            }
        });
      Map<State, Integer> costToReach = new HashMap<State, Integer>();
      this.estimatedFValue = new HashMap<State, Integer>();
      Map<State, List<String>> actions = new HashMap<State, List<String>> ();


      // Add the initial state to the open set
      State initialState = initializeState();// Define how to initialize the initial state.
        System.out.println("InState Map");
        for(int x = 0; x < initialState.getMap().getItemsData().length; x++) {
            for (int y = 0; y < initialState.getMap().getItemsData().length; y++)
                System.out.print(initialState.getMap().getItemsData()[x][y]);
            System.out.println();
        }

      ArrayList<State> goalStates = generateGoalStates(initialState); // Define how to generate goal states.
        System.out.println("+++++++");
        for (int s = 0; s < goalStates.size(); s++)
        {
            for(int x = 0; x < goalStates.get(s).stateData.length; x++) {
                for (int y = 0; y < goalStates.get(s).stateData[x].length; y++)
                    System.out.print(goalStates.get(s).stateData[x][y]);
                System.out.println();
            }
        }

        for(int x = 0; x < initialState.stateData.length; x++) {
            for (int y = 0; y < initialState.stateData[x].length; y++)
                System.out.print(initialState.stateData[x][y]);
            System.out.println();
        }

        System.out.println("InState Map");
        for(int x = 0; x < initialState.map.getItemsData().length; x++) {
            for (int y = 0; y < initialState.map.getItemsData().length; y++)
                System.out.print(initialState.map.getItemsData()[x][y]);
            System.out.println();
        }

      openSet.add(initialState);
      costToReach.put(initialState, 0);
      estimatedFValue.put(initialState, initialState.getHeuristic());
      actions.put(initialState, new ArrayList<>());

        System.out.println("+++++++");
      while (!openSet.isEmpty()) {
        State currentState = openSet.poll();
          System.out.println("+++++++");

        if (goalStates.contains(currentState)) {
            System.out.println(actions.size());
          return String.join("", actions.get(currentState));
        }

        int currentCost = cost(actions.get(currentState));

        System.out.println("InState");
          for(int x = 0; x < currentState.stateData.length; x++) {
              for (int y = 0; y < currentState.stateData[x].length; y++)
                  System.out.print(currentState.stateData[x][y]);
              System.out.println();
          }

        List<State> successors = generateSuccessors(currentState.getMap(),currentState);

        for (State successor : successors) {
            tentativeCost = currentCost + 1;
          int fValue = successor.setFValue(tentativeCost);
          System.out.println(fValue + " " + tentativeCost);

          if (!costToReach.containsKey(successor) || tentativeCost < costToReach.get(successor)) {
            costToReach.put(successor, tentativeCost);
            estimatedFValue.put(successor, fValue);

            List<String> successorActions = new ArrayList<>(actions.get(currentState));
            successorActions.add(successor.getLastAction());
            actions.put(successor, successorActions);

            openSet.add(successor);

              for (String actionsSSS : successorActions)
                  System.out.println(actionsSSS);
          }
        }
      }

      return "lrrlrlrlrllrrlrlrlrlrlrlrr";
    }

    private ArrayList<State> generateGoalStates(State initialState) throws Exception {
      // Implement how to generate goal states based on the itemsData.
      // ...
        ArrayList<State> generateGoals = new ArrayList<>();
        State currState = new State(initialState.map, this.itemsData, this.mapData);


        generateGoals.addAll(currState.setUltGoalState(currState.map, this.itemsData, this.mapData, width, height));

        System.out.println("StateData In after Goal");
        for(int  z= 0; z< initialState.stateData.length; z++) {
            for (int j = 0; j < initialState.stateData.length; j++)
                System.out.print(initialState.stateData[z][j]);
            System.out.println();
        }

      return generateGoals;
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
        PuzzleMap map = new PuzzleMap(this.mapData, this.itemsData, width, height);
        for(int  z= 0; z< map.getItemsData().length; z++) {
            for (int j = 0; j < map.getItemsData()[z].length; j++)
                System.out.print(map.getItemsData()[z][j]);
            System.out.println();
        }
        System.out.println("x");
        return new State(map, this.itemsData, this.mapData);
    }

    private ArrayList<State> generateSuccessors(PuzzleMap stateMap, State state) throws Exception {
        ArrayList<State> newStates = new ArrayList<>();
        Position positions = new Position();
        State currState = new State(state);
        PuzzleMap currItemData = new PuzzleMap(this.mapData, state.stateData, this.width, this.height);


        System.out.println("Reference Map");
        for(int x = 0; x < currItemData.getItemsData().length; x++) {
            for (int y = 0; y < currItemData.getItemsData()[x].length; y++)
                System.out.print(currItemData.getItemsData()[x][y]);
            System.out.println();
        }

        Position playerPosition = currState.player;
        int playerX = playerPosition.getX();
        int playerY = playerPosition.getY();

        // Define possible moves (up, down, left, right)
        int[] moveX = {0, 0, -1, 1};
        int[] moveY = {-1, 1, 0, 0};
        String[] moveActions = {"u", "d", "l", "r"};

        for(int z = 0; z < currItemData.getMapMatrix().length; z++) {
            for (int j = 0; j < currItemData.getMapMatrix()[z].length; j++)
                System.out.print(currItemData.getMapMatrix()[z][j]);
            System.out.println();
        }

        for (int i = 0; i < moveX.length; i++) {
            int newX = playerX + moveX[i];
            int newY = playerY + moveY[i];

            currItemData = new PuzzleMap(this.mapData, state.stateData, this.width, this.height);

            int currIndex = 0;
            for (Position box : currState.boxes)
            {
                currItemData = new PuzzleMap(this.mapData, state.stateData, this.width, this.height);
                System.out.println(box.getX() + " "+ box.getY() + " "+ newX+ " "+ newY);
                if (box.equals(new Position(newX, newY)))
                {
                    if (!currItemData.isWall(newX+ moveX[i], newY+ moveY[i])) {
                        positions = new Position(newX,newY);
                        currItemData = currState.setNewPosition(currItemData, currIndex, positions, box, playerPosition, moveX[i], moveY[i]);
                        PuzzleMap map = new PuzzleMap(currItemData);
                        newStates.add(new State(map, currState, moveActions[i], currItemData.getItemsData()));
                        System.out.println(currIndex);
                    }
                }
                currIndex++;
            }

            if (!currItemData.isWall(newX, newY) && currItemData.getItemsData()[newY][newX] != '$') {
                positions = new Position(newX,newY);
                currItemData = currState.setNewPosition(currItemData, 0, positions, playerPosition, playerPosition,0, 0);
                PuzzleMap map = new PuzzleMap(currItemData);
                newStates.add(new State(map, currState, moveActions[i], currItemData.getItemsData()));
            }

        }

        System.out.println("+++++++");




        return newStates;
    }

    private int cost(List<String> actions) {
      // A cost function to calculate the cost based on the number of lowercase letters in the actions.
      return (int) actions.stream().filter(action -> Character.isLowerCase(action.charAt(0))).count();
    }


}
