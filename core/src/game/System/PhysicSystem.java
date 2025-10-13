package game.System;

import ECS.components.B2BodyComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhysicSystem extends IteratingSystem {

    private static final float TIME_STEP = 1 / 60f; // Khoảng thời gian World mô phỏng
    private static final int VELOCITY_ITERATIONS = 6; //Độ chính xác khi va chạm
    private static final int POSITION_ITERATIONS = 2; //Các vật thể khi va chạm bị chồng chéo

    private final World world; // Thế giới vật lý Box2D
    private final PooledEngine engine; // Engine quản lý ECS
    private final Array<Body> bodiesToRemove;
    private final Array<Entity> entitiesToBeRemoved;

    private final ComponentMapper<B2BodyComponent> b2BodyMapper
        = ComponentMapper.getFor(B2BodyComponent.class);

    public PhysicSystem(World world, PooledEngine engine) {
        super(Family.all(B2BodyComponent.class).get());
        this.world = world;
        this.engine = engine;
        this.bodiesToRemove = new Array<>();
        this.entitiesToBeRemoved = new Array<>();
    }



    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS); //Tính toán vật lý
        performCleanup();
    }

    @Override
    protected void processEntity(Entity entity, float v) {

        final B2BodyComponent b2BodyComponent = b2BodyMapper.get(entity);

        /**
         * Thêm vào danh sách xóa
         */
        if (b2BodyComponent.setToDestroy && !b2BodyComponent.isDead) {
            bodiesToRemove.add(b2BodyComponent.body);
            entitiesToBeRemoved.add(entity);
            b2BodyComponent.isDead = true;
        }
    }

    /**
     * Thực hiện việc dọn dẹp sau khi bước vật lý hoàn tất.
     * Xóa các Body ra khỏi World và các Entity ra khỏi Engine.
     */
    private void performCleanup() {
        // Xóa các Body vật lý
        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        // Xóa các Entity
        for (Entity entity : entitiesToBeRemoved) {
            engine.removeEntity(entity);
        }
        entitiesToBeRemoved.clear();
    }


}
