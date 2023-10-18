package solver;

public  class Position {
    public int x, y;

    public Position()
    {
        this.x = this.y = 0;
    }

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;

    }


    public boolean equals(Position p)
    {
        return x == p.x && y == p.y;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}