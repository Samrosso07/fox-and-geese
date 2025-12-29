package logic;

import java.io.*;

public class Board {

    private Piece[][] grid;

    public Board() {
        grid = new Piece[7][7];
    }

    public void initialize() {
        // Limpiar tablero
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) grid[r][c] = null;
        }

        // --- ZORRO ---
        grid[6][3] = new Fox(6, 3); 

        // --- GANSOS (Configuración correcta para la cruz) ---
        // Fila 0
        grid[0][2] = new Goose(0, 2); grid[0][3] = new Goose(0, 3); grid[0][4] = new Goose(0, 4);
        // Fila 1
        grid[1][2] = new Goose(1, 2); grid[1][3] = new Goose(1, 3); grid[1][4] = new Goose(1, 4);
        // Fila 2
        grid[2][0] = new Goose(2, 0); grid[2][1] = new Goose(2, 1);
        grid[2][2] = new Goose(2, 2); grid[2][3] = new Goose(2, 3); grid[2][4] = new Goose(2, 4);
        grid[2][5] = new Goose(2, 5); grid[2][6] = new Goose(2, 6);
        // Fila 3 (Laterales)
        grid[3][0] = new Goose(3, 0); grid[3][6] = new Goose(3, 6);
    }

    // --- LÓGICA DE VISUALIZACIÓN ---
    public boolean isValidPosition(int r, int c) {
        if (r < 0 || r >= 7 || c < 0 || c >= 7) return false;
        // Esquinas vacías
        if (r < 2 && (c < 2 || c > 4)) return false;
        if (r > 4 && (c < 2 || c > 4)) return false;
        return true;
    }

    public Piece getPiece(int r, int c) {
        if (!isValidPosition(r, c)) return null;
        return grid[r][c];
    }   

    public void movePiece(int fr, int fc, int tr, int tc) {
        grid[tr][tc] = grid[fr][fc];
        grid[fr][fc] = null;
        if (grid[tr][tc] != null) grid[tr][tc].setPosition(tr, tc);
    }

    // --- MÉTODOS DE SNAPSHOT (RECUPERADOS PARA 'DESHACER') ---
    public char[][] getSnapshot() {
        char[][] snap = new char[7][7];
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (!isValidPosition(r, c)) snap[r][c] = 'X';
                else if (grid[r][c] == null) snap[r][c] = '.';
                else snap[r][c] = grid[r][c].getSymbol();
            }
        }
        return snap;
    }

    public void restoreFromSnapshot(char[][] snap) {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                grid[r][c] = null; // Limpiar
                if (isValidPosition(r, c)) {
                    if (snap[r][c] == 'F') grid[r][c] = new Fox(r, c);
                    else if (snap[r][c] == 'G') grid[r][c] = new Goose(r, c);
                }
            }
        }
    }
    // -----------------------------------------------------------

    // --- REGLAS DEL JUEGO ---
    public boolean isLegalMove(int fr, int fc, int tr, int tc, boolean foxTurn) {
        if (!isValidPosition(tr, tc)) return false;
        Piece p = getPiece(fr, fc);
        if (p == null) return false;

        if (foxTurn && !(p instanceof Fox)) return false;
        if (!foxTurn && !(p instanceof Goose)) return false;

        if (p instanceof Goose) {
            return isLegalGooseMove(fr, fc, tr, tc);
        } else {
            return isLegalFoxMove(fr, fc, tr, tc);
        }
    }
    
    private boolean isLegalGooseMove(int fr, int fc, int tr, int tc) {
        int dr = tr - fr;
        int dc = tc - fc;

    
        if (Math.abs(dr) <= 1 && Math.abs(dc) <= 1) {
            return getPiece(tr, tc) == null; // Solo requiere que esté vacío
        }
        return false;
    }

    private boolean isLegalFoxMove(int fr, int fc, int tr, int tc) {
        int dr = tr - fr;
        int dc = tc - fc;
        int absDr = Math.abs(dr);
        int absDc = Math.abs(dc);

        if (absDr <= 1 && absDc <= 1) {
            return getPiece(tr, tc) == null;
        }

        if ((absDr == 2 && absDc == 2) || (absDr == 2 && absDc == 0) || (absDr == 0 && absDc == 2)) {
            int mr = (fr + tr) / 2;
            int mc = (fc + tc) / 2;
            Piece mid = getPiece(mr, mc);
            if (mid instanceof Goose && getPiece(tr, tc) == null) {
                return true;
            }
        }
        return false;
    }   

    public void applyFoxCapture(int fr, int fc, int tr, int tc) {
        if (Math.abs(fr - tr) == 2 || Math.abs(fc - tc) == 2) {
            int mr = (fr + tr) / 2;
            int mc = (fc + tc) / 2;
            grid[mr][mc] = null; 
        }
    }

    public boolean foxCanCaptureAgain(int fr, int fc) {
        int[] dr = {-2, -2, -2, 0, 0, 2, 2, 2};
        int[] dc = {-2, 0, 2, -2, 2, -2, 0, 2};

        for (int i = 0; i < 8; i++) {
            int tr = fr + dr[i];
            int tc = fc + dc[i];
            if (isValidPosition(tr, tc) && isLegalFoxMove(fr, fc, tr, tc)) {
                return true; 
            }
        }
        return false;
    }

    public boolean tryMove(int fr, int fc, int tr, int tc, boolean foxTurn) {
        if (!isLegalMove(fr, fc, tr, tc, foxTurn)) return false;
        Piece p = getPiece(fr, fc);
        if (p instanceof Fox) applyFoxCapture(fr, fc, tr, tc);
        movePiece(fr, fc, tr, tc);
        return true;
    }

    // --- CONDICIONES DE VICTORIA ---
    public boolean isFoxTrapped() {
        Fox fox = null;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (grid[r][c] instanceof Fox) fox = (Fox) grid[r][c];
            }
        }
        if (fox == null) return true; 

        int fr = fox.getRow();
        int fc = fox.getCol();

        for (int r = fr - 2; r <= fr + 2; r++) {
            for (int c = fc - 2; c <= fc + 2; c++) {
                if (isValidPosition(r, c)) {
                    if (isLegalFoxMove(fr, fc, r, c)) return false; 
                }
            }
        }
        return true; 
    }

    // Método para contar gansos vivos
    public int getGeeseCount() {
        int count = 0;
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (grid[r][c] instanceof Goose) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean geeseHaveMoves() {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                if (grid[r][c] instanceof Goose) {
                    int[] dr = {-1, 1, 0, 0};
                    int[] dc = {0, 0, -1, 1};
                    for (int i = 0; i < 4; i++) {
                        int tr = r + dr[i];
                        int tc = c + dc[i];
                        if (isValidPosition(tr, tc)) {
                            if (isLegalGooseMove(r, c, tr, tc)) return true; 
                        }
                    }
                }
            }
        }
        return false;
    }

    // --- PERSISTENCIA ---
    public void saveToFile(String filename, boolean foxTurn) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("FOXTURN " + foxTurn);
            bw.newLine();
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 7; c++) {
                    if (grid[r][c] != null) {
                        char s = grid[r][c].getSymbol();
                        bw.write(r + " " + c + " " + s);
                        bw.newLine();
                    }
                }
            }
        }
    }

    public boolean loadFromFile(String filename) throws IOException {
        initialize(); 
        boolean foxTurn = false;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String first = br.readLine();
            if (first != null && first.startsWith("FOXTURN")) {
                foxTurn = Boolean.parseBoolean(first.split("\\s+")[1]);
            }
            for (int r = 0; r < 7; r++) for (int c = 0; c < 7; c++) grid[r][c] = null;

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length < 3) continue;
                int r = Integer.parseInt(p[0]);
                int c = Integer.parseInt(p[1]);
                char s = p[2].charAt(0);
                if (isValidPosition(r, c)) {
                    if (s == 'F') grid[r][c] = new Fox(r, c);
                    else if (s == 'G') grid[r][c] = new Goose(r, c);
                }
            }
        }
        return foxTurn;
    }
}