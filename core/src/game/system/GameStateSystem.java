package game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import game.component.GameStateComponent;

/**
 * System cập nhật trạng thái game (timer, multiplier, etc.)
 */
public class GameStateSystem extends IteratingSystem {

    private ComponentMapper<GameStateComponent> gameStateMapper = ComponentMapper.getFor(GameStateComponent.class);

    public GameStateSystem() {
        super(Family.all(GameStateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GameStateComponent gameState = gameStateMapper.get(entity);

        if (gameState != null) {
            gameState.update(deltaTime);

            // In ra khi còn 3 giây
            if (gameState.doubleScoreTimeLeft > 0 && gameState.doubleScoreTimeLeft <= 3) {
                System.out.println("Double Score còn: " + (int) gameState.doubleScoreTimeLeft + "s");
            }
        }
    }
}
