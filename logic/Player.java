package logic;

public class Player {

    private String name;
    private String pieceType; // "F" o "G"

    public Player(String name, String pieceType) {
        this.name = name;
        this.pieceType = pieceType;
    }

    public String getName() { return name; }
    public String getPieceType() { return pieceType; }
}