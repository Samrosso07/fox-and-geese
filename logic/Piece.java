package logic;

public abstract class Piece {
    protected int row, col;

    public Piece(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public void setPosition(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public abstract char getSymbol();
}