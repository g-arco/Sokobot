package solver;

/**
 * This class helps in computing for the manhattan distance
 */
public  class Position {
    public int x, y;

    /**
     * The constructor class for the box/goal position
     * @param x x position
     * @param y y position
     */
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;

    }


    /**
     * Gets the X position
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y position
     * @return y
     */
    public int getY() {
        return y;
    }

}