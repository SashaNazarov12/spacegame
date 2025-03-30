import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SpaceDodge extends Application {

    private static final int W = 500;
    private static final int H = 700;
    private static final int SHIP_SIZE = 70;
    private static final int MET_SIZE = 50;
    private static final double SPEED = 5;
    private static final int MAX_LIVES = 3;

    private ImageView ship;
    private ArrayList<ImageView> meteors = new ArrayList<>();
    private Random random = new Random();
    private boolean left = false;
    private boolean right = false;
    private int lives = MAX_LIVES;
    private Text livesText = new Text("Жизни: " + MAX_LIVES);
    private Text scoreText = new Text("Очки: 0");
    private int score = 0;
    private boolean gameOver = false;

    private void startGame(Pane root) {
        // Создание корабля
        ship = new ImageView(new Image(getClass().getResourceAsStream("ship.png")));
        ship.setFitWidth(SHIP_SIZE);
        ship.setFitHeight(SHIP_SIZE);
        ship.setX(W / 2 - SHIP_SIZE / 2);
        ship.setY(H - 100);
        root.getChildren().add(ship);

        Scene scene = root.getScene();

        // Управление кораблем
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT: left = true; break;
                case RIGHT: right = true; break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT: left = false; break;
                case RIGHT: right = false; break;
            }
        });

        // Таймер для обновления игры
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    moveShip();
                    updateGame(root);
                }
            }
        };
        timer.start();
    }

    private void loseLife(Pane root) {
        lives -= 1;
        livesText.setText("Жизни: " + lives);
        if (lives <= 0) {
            gameOver = true;
            livesText.setText("Game Over");
            showGameOver(root);
        }
    }

    private void moveShip() {
        double newX = ship.getX();
        if (left) newX -= SPEED;
        if (right) newX += SPEED;
        ship.setX(Math.max(0, Math.min(W - SHIP_SIZE, newX)));
    }

    private void updateGame(Pane root) {
        // Создание новых метеоров
        if (random.nextDouble() < 0.02) {
            ImageView meteor = new ImageView(new Image(getClass().getResourceAsStream("meteor.png")));
            meteor.setFitWidth(MET_SIZE);
            meteor.setFitHeight(MET_SIZE);
            meteor.setX(random.nextInt(W - MET_SIZE));
            meteor.setY(-MET_SIZE);
            meteors.add(meteor);
            root.getChildren().add(meteor);

            // Анимация падения метеора
            TranslateTransition fall = new TranslateTransition(Duration.seconds(5), meteor);
            fall.setByY(H + MET_SIZE);
            fall.setOnFinished(event -> {
                root.getChildren().remove(meteor);
                meteors.remove(meteor);
                score += 10; // Увеличение счета за уклонение
                scoreText.setText("Очки: " + score);
            });
            fall.play();
        }

        // Проверка столкновений
        Iterator<ImageView> iterator = meteors.iterator();
        while (iterator.hasNext()) {
            ImageView meteor = iterator.next();
            if (ship.getBoundsInParent().intersects(meteor.getBoundsInParent())) {
                root.getChildren().remove(meteor);
                iterator.remove();
                loseLife(root);
                break;
            }
        }
    }

    private void showGameOver(Pane root) {
        Text gameOverText = new Text("Game Over\nНажмите R для рестарта");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font(30));
        gameOverText.setX(W / 2 - 150);
        gameOverText.setY(H / 2);
        root.getChildren().add(gameOverText);

        // Обработка рестарта
        root.getScene().setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("R")) {
                root.getChildren().clear();
                resetGame(root);
            }
        });
    }

    private void resetGame(Pane root) {
        lives = MAX_LIVES;
        score = 0;
        gameOver = false;
        meteors.clear();
        livesText.setText("Жизни: " + MAX_LIVES);
        scoreText.setText("Очки: 0");

        // Начальный экран
        Text startText = new Text("Нажмите C для старта");
        startText.setFill(Color.BLACK);
        startText.setFont(Font.font(24));
        startText.setX(W / 2 - 130);
        startText.setY(H / 2);
        root.getChildren().add(startText);
        root.getChildren().add(livesText);
        root.getChildren().add(scoreText);

        // Обработка старта
        root.getScene().setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("C")) {
                root.getChildren().remove(startText);
                startGame(root);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, W, H);

        // Настройка текста жизней и очков
        livesText.setFill(Color.BLACK);
        livesText.setFont(Font.font(24));
        livesText.setX(10);
        livesText.setY(30);

        scoreText.setFill(Color.BLACK);
        scoreText.setFont(Font.font(24));
        scoreText.setX(W - 150);
        scoreText.setY(30);
        //настройка 

        // Начальный экран
        resetGame(root);

        primaryStage.setTitle("Лови камни 2D");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}