package object.player;

import org.andengine.extension.physics.box2d.PhysicsWorld;

public class SurvivalPlayer extends Player {

	private static final int VELOCITY_X = 10;
	private static final int VELOCITY_Y = 15;
	private static final float MOVEMENT_THRESHOLD = 0.4f;

	private float STOP_EPSILON;

	public SurvivalPlayer(float pX, float pY, PhysicsWorld physicsWorld) {
		super(pX, pY, physicsWorld);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setVelocityDirection(float f, float g) {
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

}
