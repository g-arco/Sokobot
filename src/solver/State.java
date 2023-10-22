package solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This will be the class that will handle the state representations
 */
public class State{
    public String actions;
    public Position player;
    public State parent = null;
    private int heuristic = 1000;

    private char[][] itemsData;

    private char[][] mapData;
    private int fValue = 1000;


    /**
     * This constructor is for the successors
     * @param parent the previous state
     * @param newAction the previous actions plus the new move
     * @param stateData the new STATE REPRESENTATION
     * @throws Exception
     */
    public State(State parent, String newAction, char[][] stateData) throws Exception//More parameters for player and boxes
    {
        this.actions = newAction;
        this.itemsData = cloneItems(stateData);
        this.mapData = parent.mapData;
        this.parent = parent;
        this.player = findPlayer(stateData);

    }

    /**
     * This constructor is for the initial state
     * @param itemsData the STATE REPRESENTATION
     * @param mapData the one to reference the walls and goal position
     * @param actions the string to be used to track the actions
     * @throws Exception
     */
    public State(char[][] itemsData, char[][] mapData, String actions) throws Exception
    {
        this.itemsData = cloneItems(itemsData);
        this.mapData = mapData;
        this.actions = actions;

        this.player = findPlayer(itemsData);

    }


    /**
     * This method finds the player in the board
     * @param board the itemsData with information about the player position
     * @return the Position (of the player)
     * @throws Exception
     */
    public Position findPlayer(char[][] board) throws Exception
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

    /**
     * This method is used for generating a new successor by moving the position of player and a box
     * @param board the itemsData of reference state
     * @param isBox if it involves moving a box
     * @param newPos the new player position
     * @param oldPos the old player position
     * @param boxX to move the box in X position
     * @param boxY to move the box in Y position
     * @return
     */
    public char[][] setNewPosition(char[][] board, int isBox, Position newPos, Position oldPos, int boxX, int boxY){

        int keyX = newPos.getX();
        int keyY = newPos.getY();
        int oldX = oldPos.getX();
        int oldY = oldPos.getY();

        //repositions old data
        if(isBox == 1)
        {
            if(cornerDeadlock(keyY+boxY, keyX+boxX) && this.mapData[keyY+boxY][keyX+boxX] != '.')
                return null;

            board[keyY+boxY][keyX+boxX] = '$';
            board[keyY][keyX] = '@';
            board[player.getY()][player.getX()] = ' ';
        }
        else
        {

            board[keyY][keyX] = '@';
            board[oldY][oldX] = ' ';
        }


        return board;

    }


    /**
     * This checks if the state is considered as a goal state
     * @return if it is goal state or not
     */
    public boolean isGoalState(){
        for(int i = 0; i < this.mapData.length; i++)
            for (int j = 0; j < this.mapData[i].length; j++)
                if(this.mapData[i][j] == '.' && this.itemsData[i][j] != '$')
                    return false;

        return true;
    }

    /**
     * This method deep clones itemsData
     * @param board the itemsData you want to clone
     * @return
     */
    public char[][] cloneItems(char[][] board){
        // get the row length and get the column length
        char[][] clone = new char[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            System.arraycopy(board[i],0,clone[i],0,board[0].length);
        }
        return clone;
    }

    /**
     * This method checks if the state is in a corner deadlock (means it is a dead end)
     * @param y the Y position
     * @param x the X position
     * @return if it is a deadlock or not
     */
    public boolean cornerDeadlock(int y, int x) {

        boolean upBlock = (this.mapData[y-1][x] == '#');
        boolean downBlock = (this.mapData[y+1][x] == '#' );
        boolean leftBlock = (this.mapData[y][x-1] == '#' );
        boolean rightBlock = (this.mapData[y][x+1] == '#');

        return (upBlock || downBlock) && (leftBlock || rightBlock);
    }


    /**
     * Sets the state's heuristic
     * @param heuristic
     */
    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Gets the state's heuristic
     * @return
     */
    public int getHeuristic() {
        return heuristic;
    }


    /**
     * Gets the state's itemsData
     * @return itemsData
     */
    public char[][] getItemsData() {
        return itemsData;
    }


    /**
     * Gets the state's actions recorded
     * @return
     */
    public String getActions() {
        return actions;
    }


    //in order to prevent state duplicates
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State other) {
            return this.heuristic == other.heuristic && java.util.Arrays.deepEquals(this.itemsData, ((State) obj).itemsData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.deepHashCode(itemsData)+this.heuristic;
    }


}