package logic;

public class Fox extends Piece {

    public Fox(int r, int c) {
        super(r, c);
    }

    @Override
    public char getSymbol() {
        return 'F';
    }
}