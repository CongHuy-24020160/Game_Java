package game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.component.PhysicsBodyComponent;
import game.component.TextureComponent;

import com.badlogic.gdx.math.Vector2;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RenderingSystemTest {

    private RenderingSystem renderingSystem;
    private SpriteBatch batch;
    private OrthographicCamera cam;

    private Entity entity;
    private TextureComponent texture;
    private PhysicsBodyComponent physicsBody;

    @Before
    public void setUp() {
        batch = mock(SpriteBatch.class);
        cam = mock(OrthographicCamera.class);
        renderingSystem = new RenderingSystem(batch, cam);

        entity = mock(Entity.class);

        texture = mock(TextureComponent.class);
        physicsBody = mock(PhysicsBodyComponent.class);

        TextureRegion region = mock(TextureRegion.class);
        when(region.getRegionWidth()).thenReturn(100);
        when(region.getRegionHeight()).thenReturn(50);
        texture.currImage = region;

        physicsBody.body = mock(com.badlogic.gdx.physics.box2d.Body.class);
        when(physicsBody.body.getPosition()).thenReturn(new Vector2(10f, 20f));
        physicsBody.isDead = false;

        when(entity.getComponent(TextureComponent.class)).thenReturn(texture);
        when(entity.getComponent(PhysicsBodyComponent.class)).thenReturn(physicsBody);
    }

    @Test
    public void testProcessEntity_drawCalled() {
        // Tạo entity thật
        Entity entity = new Entity();
        TextureComponent texture = new TextureComponent();
        PhysicsBodyComponent physicsBody = new PhysicsBodyComponent();

        // Mock body
        physicsBody.body = mock(com.badlogic.gdx.physics.box2d.Body.class);
        physicsBody.isDead = false;

        // Trả về Vector2 thực cho position
        when(physicsBody.body.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(10f, 20f));

        // Mock texture region
        TextureRegion region = mock(TextureRegion.class);
        when(region.getRegionWidth()).thenReturn(100);
        when(region.getRegionHeight()).thenReturn(50);
        texture.currImage = region;

        // Thêm component vào entity
        entity.add(texture);
        entity.add(physicsBody);

        // Thực thi processEntity
        renderingSystem.processEntity(entity, 0f);

        // Verify batch.draw được gọi
        verify(batch).draw(eq(texture.currImage), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }


}
