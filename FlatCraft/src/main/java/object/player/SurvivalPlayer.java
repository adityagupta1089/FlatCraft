package object.player;

import org.andengine.extension.physics.box2d.PhysicsWorld;

public class SurvivalPlayer extends Player {

    private static final int VELOCITY_X = 10;
    private static final int VELOCITY_Y = 15;
    private static final float MOVEMENT_THRESHOLD = 0.4f;

    private float STOP_EPSILON;

    private boolean footContacts = false;

    public SurvivalPlayer(float pX, float pY, PhysicsWorld physicsWorld) {
        super(pX, pY, physicsWorld);
        this.playerBody.setUserData("player");
    }

    @Override
    public void setVelocityDirection(float f, float g) {
        if (!footContacts) g = 0;
        float vx = playerBody.getLinearVelocity().x, vy = playerBody.getLinearVelocity().y;
        if (Math.abs(f) > MOVEMENT_THRESHOLD) {
            vx = Math.signum(f) * VELOCITY_X;
        }
        if (Math.abs(vy / VELOCITY_Y) < STOP_EPSILON && Math.abs(g) > MOVEMENT_THRESHOLD) {
            vy = Math.signum(g) * VELOCITY_Y;
        }
        playerBody.setLinearVelocity(vx, vy);
    }

    public void setStopEpsilon(float playerStopEpsilon) {
        this.STOP_EPSILON = playerStopEpsilon;
    }

    public void increaseFootContacts() {
        footContacts = true;
    }

    public void decreaseFootContacts() {
        footContacts = false;
    }

}
