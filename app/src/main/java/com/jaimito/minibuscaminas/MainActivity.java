package com.jaimito.minibuscaminas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final int BOMBS = 3;
    Button[] gridButtons = new Button[25];
    int[] buttonIds = {
            R.id.btn00, R.id.btn01, R.id.btn02, R.id.btn03, R.id.btn04,
            R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13, R.id.btn14,
            R.id.btn20, R.id.btn21, R.id.btn22, R.id.btn23, R.id.btn24,
            R.id.btn30, R.id.btn31, R.id.btn32, R.id.btn33, R.id.btn34,
            R.id.btn40, R.id.btn41, R.id.btn42, R.id.btn43, R.id.btn44
    };
    TextView tvScore, tvStatus;
    Button btnNew, btnSolve;
    Random random = new Random();
    int[][] board = new int[5][5];
    int totalPoints = 0;
    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < gridButtons.length; i++) {
            gridButtons[i] = findViewById(buttonIds[i]);
            int finalI = i;
            gridButtons[i].setOnClickListener(v -> onCellClick(finalI));
        }

        tvScore = findViewById(R.id.tvScore);
        tvStatus = findViewById(R.id.tvStatus);
        btnNew = findViewById(R.id.btnNew);
        btnSolve = findViewById(R.id.btnSolve);

        btnNew.setOnClickListener(v -> startNewGame());
        btnSolve.setOnClickListener(v -> revealBombs());

        startNewGame();
    }

    private void startNewGame() {
        resetBoard();
        placeBombs();
        calculateBombsNear();
        totalPoints = 0;
        gameOver = false;
        tvStatus.setText(getString(R.string.playing));
        tvScore.setText(getString(R.string.points) + totalPoints);
    }

    private void resetBoard() {
        for (int i = 0; i < 25; i++) {
            board[i / 5][i % 5] = 0;
            gridButtons[i].setText("");
            gridButtons[i].setBackgroundColor(getResources().getColor(R.color.bcTile));
            gridButtons[i].setEnabled(true);
            gridButtons[i].setVisibility(View.VISIBLE);
        }
    }

    private void placeBombs() {
        int bombsPlaced = 0;
        while (bombsPlaced < BOMBS) {
            int row = random.nextInt(5);
            int col = random.nextInt(5);
            if (board[row][col] == 0) {
                board[row][col] = -1; // Bomb
                bombsPlaced++;
            }
        }
    }

    private void calculateBombsNear() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (board[row][col] != -1) {
                    board[row][col] = countBombsNear(row, col);
                }
            }
        }
    }

    private int countBombsNear(int row, int col) {
        int bombsNear = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < 5 && j >= 0 && j < 5 && board[i][j] == -1) {
                    bombsNear++;
                }
            }
        }
        return bombsNear;
    }

    private void onCellClick(int index) {
        if (gameOver) return;

        int row = index / 5;
        int col = index % 5;

        if (board[row][col] == -1) {
            // Bomb clicked, Game Over
            gridButtons[index].setText("💣");
            tvStatus.setText(getString(R.string.game_over));
            gameOver = true;
            revealBombs();
        } else {
            // Show bombs near
            int bombsNear = board[row][col];
            gridButtons[index].setText(String.valueOf(bombsNear));
            gridButtons[index].setEnabled(false);

            // Update points
            totalPoints += getPoints(bombsNear);
            tvScore.setText(getString(R.string.points) + totalPoints);

            if (bombsNear == 0) {
                gridButtons[index].setVisibility(View.INVISIBLE); // Hide if no bombs nearby
            }

            if (checkWin()) {
                tvStatus.setText(getString(R.string.solved));
                gameOver = true;
            }
        }
    }

    private int getPoints(int bombsNear) {
        switch (bombsNear) {
            case 0: return 100;
            case 1: return 10;
            case 2: return 20;
            case 3: return 30;
            default: return 0;
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 25; i++) {
            if (gridButtons[i].isEnabled() && board[i / 5][i % 5] != -1) {
                return false;
            }
        }
        return true;
    }

    private void revealBombs() {
        for (int i = 0; i < 25; i++) {
            if (board[i / 5][i % 5] == -1) {
                gridButtons[i].setText("💣");
                gridButtons[i].setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            gridButtons[i].setEnabled(false);
        }
    }
}
