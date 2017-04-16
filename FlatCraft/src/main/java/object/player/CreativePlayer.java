package object.player;

import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.extension.physics.box2d.PhysicsWorld;

public class CreativePlayer extends Player {

    public CreativePlayer(float pX, float pY, PhysicsWorld physicsWorld) {
        super(pX, pY, physicsWorld);
    }

    public CreativePlayer(float mX, float mY, PhysicsWorld physicsWorld, int mID) {
        super(mX, mY, physicsWorld, mID);
    }

    @Override
    public void setVelocityDirection(float f, float g) {
        if (f < 0) this.setFlippedHorizontal(true);
        else this.setFlippedHorizontal(false);
        playerBody.setLinearVelocity(15 * f, 15 * g);
    }

}
