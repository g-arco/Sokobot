package solver;

import java.util.ArrayList;
import java.util.Arrays;

public class PuzzleMap {
    private char[][] mapMatrix;
    private int height;
    private int width;
    private char[][] mapData;
    private char[][] itemsData;

    public PuzzleMap(char[][] mapData, char[][] itemsData, int width, int height)
    {
        this.height = height;
        this.width = width;
        this.mapData = mapData;
        this.itemsData = itemsData;


        this.mapMatrix = new char[height][width];
        for (char[] row : this.mapMatrix)
            Arrays.fill(row, ' ');
        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[i].length; j++) {
                char c = itemsData[i][j];

                if ((c == '@' && mapData[i][j] != '.')|| (c == '$' && mapData[i][j] != '.')) {
                    this.mapMatrix[i][j] = itemsData[i][j];
                }
                else if (mapData[i][j] == '.')
                {
                    if (c == '$')
                    {
                        this.mapMatrix[i][j] = '*';
                    }
                    else if (c == '@')
                    {
                        this.mapMatrix[i][j] = '+';
                    }
                    else
                        this.mapMatrix[i][j] = '.';
                }
                else if (mapData[i][j] == '#')
                {
                        this.mapMatrix[i][j] = '#';
                }
            }
        }

        for(int  z= 0; z< this.mapMatrix.length; z++) {
            for (int j = 0; j < this.mapMatrix[z].length; j++)
                System.out.print(this.mapMatrix[z][j]);
            System.out.println();
        }


    }

    public PuzzleMap(PuzzleMap map)
    {
        this.height = map.getHeight();
        this.width = map.getWidth();
        this.mapData = new char[map.getMapData().length][map.getMapData()[0].length];
        for (int i = 0; i < map.getMapData().length; i++)
            for (int j = 0; j < map.getMapData()[i].length; j++)
                this.mapData[i][j] = map.getMapData()[i][j];

        this.itemsData = map.getItemsData();
        this.itemsData = new char[map.getItemsData().length][map.getItemsData()[0].length];
        for (int i = 0; i < map.getItemsData().length; i++)
            for (int j = 0; j < map.getItemsData()[i].length; j++)
                this.itemsData[i][j] = map.getItemsData()[i][j];


        this.mapMatrix = new char[height][width];
        for (char[] row : this.mapMatrix)
            Arrays.fill(row, ' ');
        for (int i = 0; i < itemsData.length; i++) {
            for (int j = 0; j < itemsData[i].length; j++) {
                char c = itemsData[i][j];

                if ((c == '@' && mapData[i][j] != '.')|| (c == '$' && mapData[i][j] != '.')) {
                    this.mapMatrix[i][j] = itemsData[i][j];
                }
                else if (mapData[i][j] == '.')
                {
                    if (c == '$')
                    {
                        this.mapMatrix[i][j] = '*';
                    }
                    else if (c == '@')
                    {
                        this.mapMatrix[i][j] = '+';
                    }
                    else
                        this.mapMatrix[i][j] = '.';
                }
                else if (mapData[i][j] == '#')
                {
                    this.mapMatrix[i][j] = '#';
                }
            }
        }

    }

    public void setMapMatrix(char[][] board) {
        System.out.println("OLD MATRIX");
        for (int i = 0; i < this.mapMatrix.length; i++)
        {
            for (int j = 0; j < this.mapMatrix[i].length; j++)
                System.out.print(this.mapMatrix[i][j]);
            System.out.println();
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                char c = board[i][j];
                //System.out.println(mapMatrix[i][j]);
                if (c == ' ')
                {
                    if (mapMatrix[i][j] == ' ' || mapMatrix[i][j] == '#' || mapMatrix[i][j] == '.')
                        mapMatrix[i][j] = mapMatrix[i][j];
                    else if (mapMatrix[i][j] == '@'|| mapMatrix[i][j] == '$' || mapMatrix[i][j] == '+' || mapMatrix[i][j] == '*') {
                        if (mapMatrix[i][j] == '+' || mapMatrix[i][j] == '*')
                            mapMatrix[i][j] = '.';
                        else
                            mapMatrix[i][j] = ' ';
                    }
                }
                else if (mapMatrix[i][j] != '#' && c != ' ')
                {
                    if ((c == '@' || c == '$') &&  mapMatrix[i][j] != '.') {
                        mapMatrix[i][j] = board[i][j];
                    }
                    else if (c == '$' && mapMatrix[i][j] == '.')
                    {
                        mapMatrix[i][j] = '*';
                    }
                    else if (c == '@' && mapMatrix[i][j] == '.')
                    {
                        mapMatrix[i][j] = '+';
                    }
                    else
                        mapMatrix[i][j] = mapMatrix[i][j];
                }
            }
        }

        System.out.println("NEW MATRIX");
        for (int i = 0; i < this.mapMatrix.length; i++)
        {
            for (int j = 0; j < this.mapMatrix[i].length; j++)
                System.out.print(this.mapMatrix[i][j]);
            System.out.println();
        }

    }

    public void setItemsData(char[][] itemsData) {
        this.itemsData = itemsData;
        this.setMapMatrix(itemsData);
    }

    public int getHeight()
    {
        return this.height;
    }
    public int getWidth()
    {
        return this.width;
    }

    public char[][] getItemsData() {
        return itemsData;
    }

    public char[][] getMapData() {
        return mapData;
    }

    public boolean isEmpty(int x, int y)
    {

        if (x < this.height && y < this.width)
        {
            char c = mapMatrix[y][x];
            return (c == ' ' || c == '$' || c == '@' || c == '.');
        }
        else
            return false;
    }

    public boolean isBlank(int x, int y)
    {

        if (x < this.height && y < this.width)
        {
            char c = mapMatrix[y][x];
            return (c == ' ' || c == '@');
        }
        else
            return false;
    }



    public boolean isWall(int x, int y)
    {
        char c = mapMatrix[y][x];
        return (c == '#');
    }

    public boolean isGoal(int x, int y)
    {
        char c = mapMatrix[y][x];
        return (c == '.' || c == '*');
    }

    public boolean isGoalOnBox(int x, int y)
    {
        char c = mapMatrix[y][x];
        return (c == '*');
    }

    public char[][] getMapMatrix() {
        return mapMatrix;
    }


    /**
     *
     * @return String representing the map

    public String toString()
    {
        String out = new String("");
        for (int i = 0; i < startMap.length; i++) {
            if (i==0) {
                out += startMap[i];
            } else {
                out += System.getProperty("line.separator")+startMap[i];
            }
        }
        return out;
    }
*/
}