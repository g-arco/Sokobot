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
    public String actions;
    public Position player;
    public State parent = null;
    private int heuristic = 1000;

    private char[][] itemsData;

    private char[][] mapData;
    private int fValue = 1000;


    public State(State parent, String newAction, char[][] stateData) throws Exception//More parameters for player and boxes
    {
        this.actions = newAction;
        this.itemsData = cloneItems(stateData);
        this.mapData = parent.mapData;
        this.parent = parent;
        this.player = findPlayer(stateData);

        //cacheHeuristic();
        //this.fValue = heuristic;

        /*
        for(int  z= 0; z< map.getMapMatrix().length; z++) {
            for (int j = 0; j < map.getMapMatrix()[z].length; j++)
                System.out.print(map.getMapMatrix()[z][j]);
            System.out.println();
        }*/
    }

    public State(char[][] itemsData, char[][] mapData, String actions) throws Exception
    {
        //Read in the initial position of the boxes and the player.
        this.itemsData = cloneItems(itemsData);
        this.mapData = mapData;
        this.actions = actions;

        this.player = findPlayer(itemsData);

        //cacheHeuristic();
        //this.fValue = heuristic;
    }



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


    public char[][] setNewPosition(char[][] board, int isBox, Position newPos, Position oldPos, int boxX, int boxY){

        int keyX = newPos.getX();
        int keyY = newPos.getY();
        int oldX = oldPos.getX();
        int oldY = oldPos.getY();


        /*
        System.out.println("before");
        for(int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++)
                System.out.print(board[x][y]);
            System.out.println();
        }*/


        if(isBox == 1)
        {
            //System.out.println("isBox");
            if(cornerDeadlock(keyY+boxY, keyX+boxX) && this.mapData[keyY+boxY][keyX+boxX] != '.')
                return null;

            board[keyY+boxY][keyX+boxX] = '$';
            board[keyY][keyX] = '@';
            board[player.getY()][player.getX()] = ' ';
            //System.out.println(addPos.getY()+" "+addPos.getX());
            //this.player = newPos;

            /*
            for(int z = 0; z < this.itemsData.length; z++) {
                for (int j = 0; j < this.itemsData[z].length; j++)
                    System.out.print(this.itemsData[z][j]);
                System.out.println();
            }*/
            //System.out.println("end.");
        }
        else
        {

            board[keyY][keyX] = '@';
            board[oldY][oldX] = ' ';
            //this.player = newPos;

            /*
            for(int z = 0; z < this.itemsData.length; z++) {
                for (int j = 0; j < this.itemsData[z].length; j++)
                    System.out.print(this.itemsData[z][j]);
                System.out.println();
            }*/

        }


        return board;

    }


    public boolean isGoalState(){
        for(int i = 0; i < this.mapData.length; i++)
            for (int j = 0; j < this.mapData[i].length; j++)
                if(this.mapData[i][j] == '.' && this.itemsData[i][j] != '$')
                    return false;

        return true;
    }

    public char[][] cloneItems(char[][] board){
        // get the row length and get the column length
        char[][] clone = new char[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            System.arraycopy(board[i],0,clone[i],0,board[0].length);
        }
        return clone;
    }

    public boolean cornerDeadlock(int y, int x) {
        // Checks if box is corner_deadlock, BUT NOT AT GOAL STATE!!, using both the dimensions of map and the obstacles

        boolean upBlock = (this.mapData[y-1][x] == '#');
        boolean downBlock = (this.mapData[y+1][x] == '#' );
        boolean leftBlock = (this.mapData[y][x-1] == '#' );
        boolean rightBlock = (this.mapData[y][x+1] == '#');

        //System.out.println(upBlock + " " + downBlock + " " + leftBlock + " " + rightBlock);
        return (upBlock || downBlock) && (leftBlock || rightBlock);
    }

    /*
    public boolean edgeDeadlock(int y, int x) {
        // Checks if there is a deadlock due to map walls on a box and a possible storage point

        // Check if box is either at the leftmost or rightmost wall, and check if storage is not along that wall
        for (char sideWall : this.mapData[y])
        if ((box[0] == 0 || box[0] == state.getWidth() - 1) && (box[0] - storage[0] != 0)) {
            return true;
        }
        // Check if box is either at the topmost or bottommost wall, and check if storage is not along that wall
        else if ((box[1] == state.getHeight() - 1 || box[1] == 0) && (box[1] - storage[1] != 0)) {
            return true;
        }
        return false;
    }*/


    public int getfValue() {
        return fValue;
    }

    public void setFValue(int fValue) {
        this.fValue = fValue;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public int getHeuristic() {
        return heuristic;
    }


    public char[][] getItemsData() {
        return itemsData;
    }

    public char[][] getMapData() {
        return mapData;
    }

    public String getActions() {
        return actions;
    }



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