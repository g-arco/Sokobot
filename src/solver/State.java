package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class State{

    /**
     * Store boxes and goals in simple list to save memory and allow iteration
     * This will give slightly more expensive isBox/isGoal lookup but the actual runtime of those methods are small anyway
     */
    public ArrayList<Position> boxes;
    public ArrayList<Position> goals;
    public Position player;
    public PuzzleMap map;
    public List<String> actions;
    public State parent = null;
    private int heuristic;
    public char[][] stateData = null;

    private char[][] itemsData;

    private char[][] mapData;
    private int fValue;

    public State(PuzzleMap map, Position player, ArrayList<Position> boxes, List<String> actions, State parent)//More parameters for player and boxes
    {
        this.map = map;
        this.player = player;
        this.boxes = boxes;
        this.actions = actions;
        this.parent = parent;
        this.goals = parent.goals;
        cacheHeuristic();
    }

    public State(PuzzleMap map, State parent, String newAction, char[][] stateData)//More parameters for player and boxes
    {
        this.map = map;
        this.player = parent.player;
        this.boxes = parent.boxes;
        this.goals = parent.goals;
        if (parent.actions == null)
            this.actions = new ArrayList<>();
        else
            this.actions = parent.actions;

        this.actions.add(newAction);
        this.stateData = stateData;
        this.parent = parent;
        cacheHeuristic();
        this.fValue = heuristic;

        for(int  z= 0; z< map.getMapMatrix().length; z++) {
            for (int j = 0; j < map.getMapMatrix()[z].length; j++)
                System.out.print(map.getMapMatrix()[z][j]);
            System.out.println();
        }
    }

    public State(PuzzleMap map, char[][] itemsData, char[][] mapData) throws Exception
    {
        this.map = new PuzzleMap(map);
        //Read in the initial position of the boxes and the player.
        this.itemsData = new char[itemsData.length][itemsData[0].length];

        for (int i = 0; i < itemsData.length; i++)
            for (int j = 0; j < itemsData[i].length; j++)
                this.itemsData[i][j] = itemsData[i][j];

        this.mapData = new char[mapData.length][mapData[0].length];
        for (int i = 0; i < mapData.length; i++)
            for (int j = 0; j < mapData[i].length; j++)
                this.mapData[i][j] = mapData[i][j];

        this.player = findPlayer(this.itemsData);
        this.boxes = findBoxes(this.itemsData);
        this.goals = findGoals(this.mapData);
        this.stateData = this.itemsData;
        cacheHeuristic();
        this.fValue = heuristic;
    }

    public State(State state) throws Exception
    {
        this.map = new PuzzleMap(state.map);
        //Read in the initial position of the boxes and the player.
        this.player = state.player;
        this.boxes = state.boxes;
        this.goals = state.goals;
        this.stateData = state.stateData;
        cacheHeuristic();
        this.fValue = heuristic;
    }

    private Position findPlayer(char[][] board) throws Exception
    {
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (c == '@' )
                    return new Position(x, y);
            }
        }
        throw new Exception("Could not find the player in the board");
    }

    private ArrayList<Position> findBoxes(char[][] board)
    {
        ArrayList<Position> boxes = new ArrayList<Position>();
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (c == '$')
                    boxes.add(new Position(x, y));
            }
        }
        return boxes;
    }

    private ArrayList<Position> findGoals(char[][] board)
    {

        ArrayList<Position> goals = new ArrayList<Position>();
        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (c == '.' || c == '*')
                {
                    goals.add(new Position(x, y));
                }
            }
        }
        return goals;
    }

    /*
    public Set<State>  setGoalState(char[][] mapData, char[][] board, int width, int height, int gx, int gy, int bx, int by, Position oldPlayer) throws Exception {
        Set<State> newStates = new HashSet<>();
        PuzzleMap newMap = new PuzzleMap(mapData, board, width, height);

        board[bx][by] = ' ';
        board[gx][gy] = '*';

        if(this.map.isEmpty(gx+1, gy))
        {
            board[oldPlayer.getX()][oldPlayer.getY()] = ' ';
            board[gx+1][gy] = '@';
            newMap = new PuzzleMap(mapData, board, width, height);
            newStates.add(new State(newMap, board, mapData));
            board[oldPlayer.getX()][oldPlayer.getY()] = '@';
        }
        if(this.map.isEmpty(gx-1, gy))
        {
            board[oldPlayer.getX()][oldPlayer.getY()] = ' ';
            board[gx-1][gy] = '@';
            newMap = new PuzzleMap(mapData, board, width, height);
            newStates.add(new State(newMap, board, mapData));
            board[oldPlayer.getX()][oldPlayer.getY()] = '@';
        }
        if(this.map.isEmpty(gx, gy+1))
        {
            board[oldPlayer.getX()][oldPlayer.getY()] = ' ';
            board[gx][gy+1] = '@';
            newMap = new PuzzleMap(mapData, board, width, height);
            newStates.add(new State(newMap, board, mapData));
            board[oldPlayer.getX()][oldPlayer.getY()] = '@';
        }
        if(this.map.isEmpty(gx, gy-1))
        {
            board[oldPlayer.getX()][oldPlayer.getY()] = ' ';
            board[gx][gy-1] = '@';
            newMap = new PuzzleMap(mapData, board, width, height);
            newStates.add(new State(newMap, board, mapData));
            board[oldPlayer.getX()][oldPlayer.getY()] = '@';
        }

        this.map.setMapMatrix(board);

        return newStates;
    }*/

    public ArrayList<State> setUltGoalState(PuzzleMap pMap,char[][] board, char[][] mapData, int width, int height) throws Exception {

        int i = 0;
        ArrayList<State> newStates = new ArrayList<>();

        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                char c = board[y][x];
                if (mapData[y][x] == '.') {
                    board[y][x] = '*';
                    this.boxes.set(i, new Position(x, y));
                    i++;
                }
                else if (c == '$' && mapData[y][x] == ' ') {
                    board[y][x] = ' ';
                }
            }
        }
        System.out.println("MATRIXMAP");

        for(int z = 0; z < pMap.getMapMatrix().length; z++) {
            for (int j = 0; j < pMap.getMapMatrix()[z].length; j++)
                System.out.print(pMap.getMapMatrix()[z][j]);
            System.out.println();
        }

        for(Position goals : goals)
        {
            System.out.println("+");
            int x = goals.getX();
            int y = goals.getY();
            //System.out.println(i + " "+ board[y][x]);
            if(pMap.isEmpty(x+1, y)){
                board[player.getY()][player.getX()] = ' ';
                board[y][x+1] = '@';
                PuzzleMap newMap = new PuzzleMap(mapData, board, width, height);
                newStates.add(new State(newMap, board, mapData));
                for(int  z= 0; z< board.length; z++) {
                    for (int j = 0; j < board[z].length; j++)
                        System.out.print(board[z][j]);
                    System.out.println();
                }
                board[y][x+1] = ' ';
                board[player.getY()][player.getX()] = '@';

            }
            if(pMap.isEmpty(x-1, y)){
                board[player.getY()][player.getX()] = ' ';
                board[y][x-1] = '@';
                PuzzleMap newMap = new PuzzleMap(mapData, board, width, height);
                newStates.add(new State(newMap, board, mapData));
                for(int z = 0; z < board.length; z++) {
                    for (int j = 0; j < board[z].length; j++)
                        System.out.print(board[z][j]);
                    System.out.println();
                }
                board[y][x-1] = ' ';
                board[player.getY()][player.getX()] = '@';

            }
            if(pMap.isEmpty(x, y+1)){
                board[player.getY()][player.getX()] = ' ';
                board[y+1][x] = '@';
                PuzzleMap newMap = new PuzzleMap(mapData, board, width, height);
                newStates.add(new State(newMap, board, mapData));
                for(int z = 0; z < board.length; z++) {
                    for (int j = 0; j < board[z].length; j++)
                        System.out.print(board[z][j]);
                    System.out.println();
                }
                board[y+1][x] = ' ';
                board[player.getY()][player.getX()] = '@';

            }
            if(pMap.isEmpty(x, y-1)){
                board[player.getY()][player.getX()] = ' ';
                board[y-1][x] = '@';
                PuzzleMap newMap = new PuzzleMap(mapData, board, width, height);
                newStates.add(new State(newMap, board, mapData));
                for(int z = 0; z < board.length; z++) {
                    for (int j = 0; j < board[z].length; j++)
                        System.out.print(board[z][j]);
                    System.out.println();
                }
                board[y-1][x] = ' ';
                board[player.getY()][player.getX()] = '@';

            }
        }

        return newStates;

    }

    public PuzzleMap setNewPosition(PuzzleMap boardMap, int boxIndex, Position newPos, Position oldPos, Position player, int isBox){

        int keyX = newPos.getX();
        int keyY = newPos.getY();
        int oldX = oldPos.getX();
        int oldY = oldPos.getY();
        char[][] board = boardMap.getItemsData();

        System.out.println("before");
        for(int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++)
                System.out.print(board[x][y]);
            System.out.println();
        }

        for (int y = 0; y < board.length; y++)
        {
            for (int x = 0; x < board[y].length; x++)
            {
                if (keyX == x && keyY == y)
                {
                    if(isBox == 1)
                    {
                        System.out.println("isBox");
                        board[y][x] = '$';
                        board[oldY][oldX] = '@';
                        board[player.getY()][player.getX()] = ' ';
                        this.player = player;
                        this.boxes.set(boxIndex, newPos);
                        boardMap.setItemsData(board);

                        for(int z = 0; z < board.length; z++) {
                            for (int j = 0; j < board[z].length; j++)
                                System.out.print(board[z][j]);
                            System.out.println();
                        }
                    }
                    else
                    {
                        board[y][x] = '@';
                        board[oldY][oldX] = ' ';
                        this.player = newPos;

                        for(int z = 0; z < board.length; z++) {
                            for (int j = 0; j < board[z].length; j++)
                                System.out.print(board[z][j]);
                            System.out.println();
                        }

                        boardMap.setItemsData(board);
                    }

                }
            }
        }

        return boardMap;

    }


    private int boxesOnGoal(){
        int numOfBoxOnGoal = 0;
        for (Position boxPos : boxes){
            if(map.isGoal(boxPos.x, boxPos.y)){
                numOfBoxOnGoal++;
            }
        }
        return numOfBoxOnGoal;
    }


    public int getHeight()
    {
        return this.map.getHeight();
    }

    public int getWidth()
    {
        return this.map.getWidth();
    }

    /**
     *
     * @param x
     * @param y
     * @return Whether the tile is free to move onto or not
     */
    public boolean isFree(int x, int y)
    {

        return (!this.isWall(x, y) && !this.isBox(x, y));
    }

    /*
    is* functions for convenience in this class which only forwards
     */
    public boolean isBox(int x, int y)
    {
        for (int ii = 0; ii < boxes.size(); ++ii)
        {
            Position pos = boxes.get(ii);
            if (x == pos.x & y == pos.y)
                return true;
        }
        return false;
    }

    public boolean isEmpty(int x, int y)
    {
        return map.isEmpty(x, y);
    }

    public boolean isGoal(int x, int y)
    {
        return map.isGoal(x, y);
    }

    public boolean isWall(int x, int y)
    {
        return map.isWall(x, y);
    }

    public Position getPlayer()
    {
        return player;
    }

    public String getLastAction() {
        return actions.get(actions.size()-1);
    }

    public int setFValue(int tentativeCost) {
        this.fValue = tentativeCost + heuristic;
        return fValue;
    }

    public int getFValue() {
        return fValue;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public ArrayList<Position> getGoals() {
        return goals;
    }

    public char[][] getStateData() {
        return stateData;
    }

    /**
     *
     * @return A string to be used when debugging the AI

    public String toString()
    {
        String stringOut = "";
        boolean boxBool, playerBool;
        for (int i = 0; i < map.getHeight(); i++) { // For y-coordinates
            for (int j = 0; j < map.getWidth(); j++) { // For x-coordinates
                // See if there is a box with these exact coordinates
                boxBool = false;
                playerBool = false;
                for (Position box : boxes) {
                    if (box.x == j & box.y == i) {
                        boxBool = true;
                    }
                }
                // See if there is a player with these exact coordinates
                if (player.x == j & player.y == i) {
                    playerBool = true;
                }
                // Add to the string
                if (boxBool & map.mapMatrix[i][j] == '.') {
                    stringOut += '*';
                } else if (playerBool & map.mapMatrix[i][j] == '.') {
                    stringOut += '+';
                } else if (playerBool){
                    stringOut += '@';
                } else if (boxBool) {
                    stringOut += '$';
                } else {
                    stringOut += map.mapMatrix[i][j];
                }
            }
            if (i < map.getHeight()-1) { // No new line on last line
                stringOut += System.getProperty("line.separator"); // New line
            }
        }
        return stringOut;
    }
    */


    private void cacheHeuristic(){

        for (Position boxPos : boxes) {
            int minDistance = Integer.MAX_VALUE;

            // Calculate the Manhattan distance from the box to the closest goal
            for (Position goalPos : goals) {
                int distance = Math.abs(boxPos.x - goalPos.x) + Math.abs(boxPos.y - goalPos.y);
                minDistance = Math.min(minDistance, distance);
            }

            heuristic += minDistance;
        }

    }

}