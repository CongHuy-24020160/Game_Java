package game.LoadAssets;

import com.badlogic.gdx.physics.box2d.*;

public class BodyFactory {
    public enum BlockType {ONE_HIT, TWO_HIT, UNBREAKABLE, PASSTHROUGH}

    ;

    public enum Material {PLASTIC, HARDENED, STEEL, PUFF}

    private World world;
    // singleton
    private static BodyFactory thisInstance;

    public static BodyFactory getInstance(World world) {
        if (thisInstance == null) {
            thisInstance = new BodyFactory(world);
        } else {
            //CẬP NHẬT LẠI WORLD MỚI (Sửa lỗi nhỏ)
            thisInstance.world = world;
        }
        return thisInstance;
    }


    private BodyFactory(World world) {
        this.world = world;
    }

    public static void destroyInstance() {
        thisInstance = null;
    }

    // material refers to how we want our fixture to behave
    private static FixtureDef makeFixture(Material material, Shape shape, boolean isSensor) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        // make fixture a sensor
        if (isSensor) fixtureDef.isSensor = true;

        if (material != null) {
            switch (material) {
                case PLASTIC:
                case HARDENED:
                case STEEL:
                    fixtureDef.density = 1f;
                    fixtureDef.friction = 0.3f; // Giữ nguyên


                    fixtureDef.restitution = 1.0f; // Thay 0.1f thành 1.0f

                    break;
                case PUFF:
                    fixtureDef.density = 1f;
                    fixtureDef.friction = 0f;
                    fixtureDef.restitution = 0.01f;
                    break;
            }
        }

        return fixtureDef;
    }

    // make a box box2d body
    public Body makeBoxPolyBody(float posx, float posy, float width, float height, Material material,
                                BodyDef.BodyType bodyType, boolean fixedRotation, boolean isSensor) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx + (width / 2);
        boxBodyDef.position.y = posy + (height / 2);
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material, poly, isSensor));
        poly.dispose();

        return boxBody;
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, Material material,
                                   BodyDef.BodyType bodyType, boolean fixedRotation, boolean isSensor) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius / 2);
        boxBody.createFixture(makeFixture(material, circleShape, isSensor));
        circleShape.dispose();
        return boxBody;
    }
}
