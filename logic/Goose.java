package logic;

public class Goose extends Piece {

    public Goose(int r, int c) {
        super(r, c);
    }

    @Override
    public char getSymbol() {
        return 'G';
    }
}