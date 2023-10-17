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
    public State parent = null;
    private int heuristic = 1000;

    private char[][] itemsData;

    private char[][] mapData;
    private int fValue = 1000;


    public State( State parent, String newAction, char[][] stateData) throws Exception//More parameters for player and boxes
    {
        this.actions = newAction;

        this.itemsData = cloneItems(stateData);

        this.mapData = parent.mapData;

        this.parent = parent;
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


    public char[][] setNewPosition(char[][] board, int isBox, Position newPos, Position oldPos, Position addPos, int boxX, int boxY){

        int keyX = newPos.getX();
        int keyY = newPos.getY();
        int oldX = oldPos.getX();
        int oldY = oldPos.getY();

        /*
        System.out.println("before");
        for(int x = 0; x < this.itemsData.length; x++) {
            for (int y = 0; y < this.itemsData[x].length; y++)
                System.out.print(this.itemsData[x][y]);
            System.out.println();
        }*/


        if(isBox == 1)
        {
            //System.out.println("isBox");
            if((this.mapData[keyY+boxY][keyX+boxX+1] == '#' || this.mapData[keyY+boxY][keyX+boxX+1] == '$') &&
                    (this.mapData[keyY+boxY+1][keyX+boxX] == '#'|| this.mapData[keyY+boxY-1][keyX+boxX] == '#')  &&
                    this.mapData[keyY+boxY][keyX+boxX] != '.')
                return null;
            if((this.mapData[keyY+boxY][keyX+boxX-1] == '#' || this.mapData[keyY+boxY][keyX+boxX+1] == '$')&&
                    (this.mapData[keyY+boxY+1][keyX+boxX] == '#' ||  this.mapData[keyY+boxY-1][keyX+boxX] == '#') &&
                    this.mapData[keyY+boxY][keyX+boxX] != '.')
                return null;

            board[keyY+boxY][keyX+boxX] = '$';
            board[keyY][keyX] = '@';
            board[addPos.getY()][addPos.getX()] = ' ';
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




    /*
    public boolean isFree(int x, int y)
    {

        return (!this.isWall(x, y) && !this.isBox(x, y));
    }*/

    /*
    is* functions for convenience in this class which only forwards

    public boolean isBox(int x, int y)
    {
        for (int ii = 0; ii < boxes.size(); ++ii)
        {
            Position pos = boxes.get(ii);
            if (x == pos.x & y == pos.y)
                return true;
        }
        return false;
    }*/
    public char[][] cloneItems(char[][]arrSRC){
        // get the row length and get the column length
        char[][] clone = new char[arrSRC.length][arrSRC[0].length];
        for(int i = 0; i < arrSRC.length; i++){
            System.arraycopy(arrSRC[i],0,clone[i],0,arrSRC[0].length);
        }
        return clone;
    }

    public boolean isDeadEnd(char[][] stateData){
        // get the row length and get the column length
        int deadEnd = 0;

        for(int x = 0; x < stateData.length; x++) {
            for (int y = 0; y < stateData[x].length; y++)
            {
                if (stateData[x][y] == '$' && this.mapData[x][y] != '.')
                {
                    if(this.mapData[x+1][y] == '$' && this.mapData[x+2][y] == '#')
                        return true;
                    if(this.mapData[x-1][y] == '$' && this.mapData[x-2][y] == '#')
                        return true;
                    if(this.mapData[x][y+1] == '$' && this.mapData[x][y+2] == '#')
                        return true;
                    if(this.mapData[x][y-1] == '$' && this.mapData[x][y-2] == '#')
                        return true;
                    if((this.mapData[x][y+1] == '#' && this.mapData[x+1][y] == '#') || (this.mapData[x][y+1] == '#' && this.mapData[x-1][y] == '#'))
                        return true;
                    if((this.mapData[x][y-1] == '#' && this.mapData[x+1][y] == '#') || (this.mapData[x][y-1] == '#' && this.mapData[x-1][y] == '#'))
                        return true;
                }
            }
        }

        return false;
    }

    public boolean isWall(int x, int y)
    {
        return (this.mapData[y][x] == '#');
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State other) {
            return java.util.Arrays.deepEquals(this.itemsData, other.itemsData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.deepHashCode(itemsData);
    }


}