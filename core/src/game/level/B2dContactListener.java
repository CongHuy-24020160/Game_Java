package game.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import game.component.ColliderComponent;

public class B2dContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

//        System.out.println(fa.getBody().getType()+" has hit "+ fb.getBody().getType());
        if(fa.getBody().getUserData() instanceof Entity){
            Entity ent = (Entity) fa.getBody().getUserData();
            entityCollision(ent, fb);
        }else if(fb.getBody().getUserData() instanceof Entity){
            Entity ent = (Entity) fb.getBody().getUserData();
            entityCollision(ent, fb);
        }
    }

    // update the collision component entity property
    private void entityCollision(Entity ent, Fixture fb){
        if(fb.getBody().getUserData() instanceof Entity){
            Entity colEnt = (Entity) fb.getBody().getUserData();

            ColliderComponent col = ent.getComponent(ColliderComponent.class);
            ColliderComponent colb = colEnt.getComponent(ColliderComponent.class);

            if(col != null){
                col.tagertEntity = colEnt;
            }

            if(colb != null){
                colb.tagertEntity = ent;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getBody().getUserData() instanceof Entity){
            Entity ent = (Entity) fa.getBody().getUserData();
            removeCollision(ent, fb);
        }else if(fb.getBody().getUserData() instanceof Entity){
            Entity ent = (Entity) fb.getBody().getUserData();
            removeCollision(ent, fb);
        }
    }

    // remove entity once it has been removed
    private void removeCollision(Entity ent, Fixture fb){
        if(fb.getBody().getUserData() instanceof Entity){
            Entity colEnt = (Entity) fb.getBody().getUserData();

            ColliderComponent col = ent.getComponent(ColliderComponent.class);
            ColliderComponent colb = colEnt.getComponent(ColliderComponent.class);

            col.tagertEntity = null;
            colb.tagertEntity = null;

            // reset can collide flags
            col.isActive = true;
            colb.isActive = true;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
