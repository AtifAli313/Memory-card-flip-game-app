package com.test3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<MemoryCard> cards;
    private MemoryAdapter adapter;
    private int indexOfSingleSelectedCard = -1;
    private int movesCount = 0;
    private int difficulty = 4; // Default Easy 4x4

    private TextView movesText, winMovesText;
    private TextView timerText, winTimeText;
    private View menuLayout;
    private View gameLayout;
    private View winLayout;
    private TextView easyMode, mediumMode, hardMode;
    private TextView gameEasy, gameMedium, gameHard;

    private long startTime = 0;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            String time = String.format("%02d:%02d", minutes, seconds);
            timerText.setText(time);
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movesText = findViewById(R.id.movesText);
        timerText = findViewById(R.id.timerText);
        winMovesText = findViewById(R.id.winMovesText);
        winTimeText = findViewById(R.id.winTimeText);
        
        menuLayout = findViewById(R.id.menuLayout);
        gameLayout = findViewById(R.id.gameLayout);
        winLayout = findViewById(R.id.winLayout);
        
        easyMode = findViewById(R.id.easyMode);
        mediumMode = findViewById(R.id.mediumMode);
        hardMode = findViewById(R.id.hardMode);

        gameEasy = findViewById(R.id.gameEasy);
        gameMedium = findViewById(R.id.gameMedium);
        gameHard = findViewById(R.id.gameHard);

        setupDifficultySelectors();

        findViewById(R.id.playNowButton).setOnClickListener(v -> startGame());
        findViewById(R.id.restartButton).setOnClickListener(v -> setupGame());
        
        findViewById(R.id.navHome).setOnClickListener(v -> showMenu());
        findViewById(R.id.navPlay).setOnClickListener(v -> {
            if (menuLayout.getVisibility() == View.VISIBLE) {
                startGame();
            }
        });

        findViewById(R.id.playAgainButton).setOnClickListener(v -> {
            winLayout.setVisibility(View.GONE);
            setupGame();
        });

        findViewById(R.id.winHomeButton).setOnClickListener(v -> {
            winLayout.setVisibility(View.GONE);
            showMenu();
        });
    }

    private void showMenu() {
        stopTimer();
        gameLayout.setVisibility(View.GONE);
        winLayout.setVisibility(View.GONE);
        menuLayout.setVisibility(View.VISIBLE);
    }

    private void startGame() {
        menuLayout.setVisibility(View.GONE);
        winLayout.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);
        setupGame();
    }

    private void setupDifficultySelectors() {
        View.OnClickListener listener = v -> {
            int size = 4;
            if (v.getId() == R.id.mediumMode || v.getId() == R.id.gameMedium) size = 6;
            else if (v.getId() == R.id.hardMode || v.getId() == R.id.gameHard) size = 8;
            updateDifficulty(size);
            if (gameLayout.getVisibility() == View.VISIBLE) {
                setupGame();
            }
        };

        easyMode.setOnClickListener(listener);
        mediumMode.setOnClickListener(listener);
        hardMode.setOnClickListener(listener);
        gameEasy.setOnClickListener(listener);
        gameMedium.setOnClickListener(listener);
        gameHard.setOnClickListener(listener);
    }

    private void updateDifficulty(int size) {
        difficulty = size;
        
        // Update Menu UI
        resetDifficultyUI(easyMode, mediumMode, hardMode);
        if (size == 4) setDifficultyActive(easyMode);
        else if (size == 6) setDifficultyActive(mediumMode);
        else setDifficultyActive(hardMode);

        // Update Game UI
        resetGameDifficultyUI();
        if (size == 4) setGameDifficultyActive(gameEasy);
        else if (size == 6) setGameDifficultyActive(gameMedium);
        else setGameDifficultyActive(gameHard);
    }

    private void resetDifficultyUI(TextView... views) {
        for (TextView v : views) {
            v.setBackground(null);
            v.setTextColor(getResources().getColor(R.color.on_surface_variant));
        }
    }

    private void setDifficultyActive(TextView v) {
        v.setBackgroundResource(R.drawable.bg_difficulty_selected);
        v.setTextColor(getResources().getColor(R.color.on_tertiary_container));
    }

    private void resetGameDifficultyUI() {
        gameEasy.setBackgroundResource(R.drawable.bg_difficulty_pill);
        gameMedium.setBackgroundResource(R.drawable.bg_difficulty_pill);
        gameHard.setBackgroundResource(R.drawable.bg_difficulty_pill);
        gameEasy.setTextColor(getResources().getColor(R.color.on_surface_variant));
        gameMedium.setTextColor(getResources().getColor(R.color.on_surface_variant));
        gameHard.setTextColor(getResources().getColor(R.color.on_surface_variant));
    }

    private void setGameDifficultyActive(TextView v) {
        v.setBackgroundResource(R.drawable.bg_difficulty_pill_active);
        v.setTextColor(getResources().getColor(R.color.on_tertiary_container));
    }

    private void setupGame() {
        movesCount = 0;
        updateStatus();
        startTimer();
        
        int totalCards = difficulty * difficulty;
        
        List<Integer> pool = Arrays.asList(
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera,
                android.R.drawable.ic_menu_send, android.R.drawable.ic_menu_manage,
                android.R.drawable.ic_menu_call, android.R.drawable.ic_menu_agenda,
                android.R.drawable.ic_menu_directions, android.R.drawable.ic_menu_compass,
                android.R.drawable.ic_menu_add, android.R.drawable.ic_menu_delete,
                android.R.drawable.ic_menu_edit, android.R.drawable.ic_menu_search,
                android.R.drawable.ic_menu_view, android.R.drawable.ic_menu_share,
                android.R.drawable.ic_menu_save, android.R.drawable.ic_menu_upload
        );
        
        List<Integer> images = new ArrayList<>();
        int pairsNeeded = totalCards / 2;
        for(int i=0; i < pairsNeeded; i++) {
            int img = pool.get(i % pool.size());
            images.add(img);
            images.add(img);
        }
        Collections.shuffle(images);

        cards = new ArrayList<>();
        for (int img : images) {
            cards.add(new MemoryCard(img));
        }

        RecyclerView rv = findViewById(R.id.recyclerView);
        adapter = new MemoryAdapter(cards, position -> {
            updateGameWithFlip(position);
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, difficulty));
    }

    private void startTimer() {
        stopTimer();
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void updateGameWithFlip(int position) {
        MemoryCard card = cards.get(position);
        if (card.isFlipped() || card.isMatched()) return;

        if (indexOfSingleSelectedCard == -1) {
            restoreCards();
            indexOfSingleSelectedCard = position;
        } else {
            movesCount++;
            updateStatus();
            checkForMatch(indexOfSingleSelectedCard, position);
            indexOfSingleSelectedCard = -1;
        }
        
        card.setFlipped(true);
        adapter.notifyDataSetChanged();
        checkWinCondition();
    }

    private void checkForMatch(int pos1, int pos2) {
        if (cards.get(pos1).getIdentifier() == cards.get(pos2).getIdentifier()) {
            cards.get(pos1).setMatched(true);
            cards.get(pos2).setMatched(true);
        }
    }

    private void restoreCards() {
        for (MemoryCard card : cards) {
            if (!card.isMatched()) card.setFlipped(false);
        }
    }

    private void updateStatus() {
        movesText.setText(String.valueOf(movesCount));
    }

    private void checkWinCondition() {
        for (MemoryCard card : cards) {
            if (!card.isMatched()) return;
        }
        stopTimer();
        showWinScreen();
    }

    private void showWinScreen() {
        winMovesText.setText(String.valueOf(movesCount));
        winTimeText.setText(timerText.getText());
        winLayout.setVisibility(View.VISIBLE);
    }
}
