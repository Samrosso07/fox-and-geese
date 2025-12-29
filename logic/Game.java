package logic;

import java.util.Stack;

public class Game {

    private Board board;
    private boolean foxTurn;
    
    // Clase interna para guardar el estado completo
    private class GameState {
        char[][] boardSnap;
        boolean turn;
        public GameState(char[][] b, boolean t) {
            this.boardSnap = b;
            this.turn = t;
        }
    }
    
    private Stack<GameState> history;

    public enum MoveResult {
        ILLEGAL,
        MOVED_TURN_CHANGED,
        MOVED_STAY,
        GAME_OVER
    }

    public Game() {
        board = new Board();
        history = new Stack<>();
        reset();
    }

    public void reset() {
        board.initialize();
        foxTurn = false;
        history.clear();
    }

    public Board getBoard(){ return this.board; }
    public boolean isFoxTurn() { return foxTurn; }

    public void undo() {
        if (!history.isEmpty()) {
            GameState previous = history.pop();
            board.restoreFromSnapshot(previous.boardSnap);
            this.foxTurn = previous.turn;
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public MoveResult tryMove(int fr, int fc, int tr, int tc) {
        Piece p = board.getPiece(fr, fc);
        if (p == null) return MoveResult.ILLEGAL;
        if (p instanceof Fox && !foxTurn) return MoveResult.ILLEGAL;
        if (p instanceof Goose && foxTurn) return MoveResult.ILLEGAL;

        // GUARDAR ESTADO PARA UNDO
        char[][] currentSnap = board.getSnapshot();
        
        // Detectar si va a ser una captura (ANTES de mover)
        boolean isCaptureMove = false;
        if (p instanceof Fox) {
            // Si la distancia es 2, es un intento de salto
            if (Math.abs(fr - tr) == 2 || Math.abs(fc - tc) == 2) {
                isCaptureMove = true;
            }
        }

        // INTENTAR MOVER
        if (!board.tryMove(fr, fc, tr, tc, foxTurn)) {
            return MoveResult.ILLEGAL;
        }

        // --- SONIDOS ---
        if (isCaptureMove) {
            SoundManager.playSFX("assets/eat.wav");   // Sonido de comer
        } else {
            SoundManager.playSFX("assets/move.wav");  // Sonido de mover
        }
        // -----------------------

        // Guardar en historial
        history.push(new GameState(currentSnap, foxTurn));

        if (board.isFoxTrapped()) {
            return MoveResult.GAME_OVER;
        }
        
        if (!board.geeseHaveMoves() || board.getGeeseCount() < 4) {
            return MoveResult.GAME_OVER;
        }

        // Verificar saltos múltiples
        if (isCaptureMove && p instanceof Fox) {
            if (board.foxCanCaptureAgain(tr, tc)) {
                return MoveResult.MOVED_STAY;
            }
        }

        foxTurn = !foxTurn;
        return MoveResult.MOVED_TURN_CHANGED;
    }
}