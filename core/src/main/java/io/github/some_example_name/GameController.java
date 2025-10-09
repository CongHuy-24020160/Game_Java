package io.github.some_example_name;

import java.util.ArrayList;
import java.util.List;

class Ball {
    private float x, y, dx, dy;

    public Ball(float x, float y) {
        this.x = x;
        this.y = y;
        this.dx = 2; // speed
        this.dy = -2;
    }

    public void update(float deltaTime) {
        x += dx * deltaTime; //depends on fps
        y += dy * deltaTime;
    }

    public void bounce() {
        dy = -dy;
    }

    public boolean collidesWith(Paddle paddle) {
        return x >= paddle.getX() && x <= paddle.getX() + paddle.getWidth()
            && y >= paddle.getY() && y <= paddle.getY() + paddle.getHeight();
    }

    public boolean collidesWith(Brick brick) {
        return x >= brick.getX() && x <= brick.getX() + brick.getWidth()
            && y >= brick.getY() && y <= brick.getY() + brick.getHeight();
    }
}

class Paddle {
    private float x, y;
    private float width = 80, height = 10;
    private float speed = 5;

    public Paddle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float deltaTime) {
        // controlled by player
    }

    public void moveLeft() {
        x -= speed;
    }

    public void moveRight() {
        x += speed;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
}

class Brick {
    private float x, y, width = 40, height = 20;
    private boolean destroyed = false;

    public Brick(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
    }
}

class BrickManager {
    private List<Brick> bricks = new ArrayList<>();

    public BrickManager() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 3; j++)
                bricks.add(new Brick(50 + i * 50, 50 + j * 30));
    }

    public List<Brick> getAll() {
        return bricks;
    }
}

public class GameController {
    private Ball ball;
    private Paddle paddle;
    private BrickManager bricks;

    public GameController() {
        ball = new Ball(100, 200);
        paddle = new Paddle(150, 350);
        bricks = new BrickManager();
    }

    public void update(float deltaTime) {
        ball.update(deltaTime);
        checkCollisions();
    }

    private void checkCollisions() {
        if (ball.collidesWith(paddle)) {
            ball.bounce();
        }
        for (Brick b : bricks.getAll()) {
            if (!b.isDestroyed() && ball.collidesWith(b)) {
                ball.bounce();
                b.destroy();
            }
        }
    }

    public void onKeyPressed(String key) {
        if (key.equals("LEFT")) {
            paddle.moveLeft();
        }
        else if (key.equals("RIGHT")) {
            paddle.moveRight();
        }
    }
}
