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
    public String actions;
    public State parent = null;
    private int heuristic = 1000;

    private char[][] itemsData;

    private char[][] mapData;
    private int fValue = 1000;


    public State( State parent, String newAction, char[][] stateData) throws Exception//More parameters for player and boxes
    {
        this.player = findPlayer(stateData);
        this.boxes = parent.boxes;
        this.goals = parent.goals;
        this.actions = newAction;

        this.itemsData = new char[stateData.length][stateData[0].length];
        for (int i = 0; i < stateData.length; i++)
            for (int j = 0; j < stateData[i].length; j++)
                this.itemsData[i][j] = stateData[i][j];

        this.mapData = new char[parent.getMapData().length][parent.getMapData()[0].length];
        for (int i = 0; i < parent.getMapData().length; i++)
            for (int j = 0; j < parent.getMapData()[i].length; j++)
                this.mapData[i][j] = parent.getMapData()[i][j];

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
        this.itemsData = new char[itemsData.length][itemsData[0].length];
        for (int i = 0; i < itemsData.length; i++)
            for (int j = 0; j < itemsData[i].length; j++)
                this.itemsData[i][j] = itemsData[i][j];

        this.mapData = new char[mapData.length][mapData[0].length];
        for (int i = 0; i < mapData.length; i++)
            for (int j = 0; j < mapData[i].length; j++)
                this.mapData[i][j] = mapData[i][j];

        this.actions = actions;
        this.player = findPlayer(this.itemsData);
        this.goals = findGoals(this.mapData);
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

    public ArrayList<Position> findGoals(char[][] board)
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

    public char[][] setNewPosition(int isBox, Position newPos, Position oldPos, Position addPos, int boxX, int boxY){

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

        for (int y = 0; y < this.itemsData.length; y++)
        {
            for (int x = 0; x < this.itemsData[y].length; x++)
            {
                if (keyX == x && keyY == y)
                {
                    if(isBox == 1)
                    {
                        System.out.println("isBox");

                        this.itemsData[y+boxY][x+boxX] = '$';
                        this.itemsData[keyY][keyX] = '@';
                        this.itemsData[addPos.getY()][addPos.getX()] = ' ';
                        System.out.println(addPos.getY()+" "+addPos.getX());
                        //this.player = newPos;

                        /*
                        for(int z = 0; z < this.itemsData.length; z++) {
                            for (int j = 0; j < this.itemsData[z].length; j++)
                                System.out.print(this.itemsData[z][j]);
                            System.out.println();
                        }*/
                        System.out.println("end.");
                    }
                    else
                    {

                        this.itemsData[keyY][keyX] = '@';
                        this.itemsData[oldY][oldX] = ' ';
                        //this.player = newPos;

                        /*
                        for(int z = 0; z < this.itemsData.length; z++) {
                            for (int j = 0; j < this.itemsData[z].length; j++)
                                System.out.print(this.itemsData[z][j]);
                            System.out.println();
                        }*/

                    }

                }
            }
        }

        return this.itemsData;

    }

    public void setItemsData(char[][] stateData) {
        this.itemsData = new char[stateData.length][stateData[0].length];
        for (int i = 0; i < stateData.length; i++)
            for (int j = 0; j < stateData[i].length; j++)
                this.itemsData[i][j] = stateData[i][j];
    }


    public boolean isGoalState(){
        int count=0;

        for(int i = 0; i < this.mapData.length; i++)
            for (int j = 0; j < this.mapData[i].length; j++)
                if(this.mapData[i][j] == '.' && this.itemsData[i][j] == '$')
                    count++;
        if (count == goals.size())
            return true;
        else
            return false;

    }



    /**
     *
     * @param x
     * @param y
     * @return Whether the tile is free to move onto or not
     */
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
            return this.player == other.player &&
                    java.util.Arrays.deepEquals(this.itemsData, other.itemsData);
        }
        return false;
    }

    /*
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

    }*/

}