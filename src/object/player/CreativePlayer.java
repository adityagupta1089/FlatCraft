package object.player;

import org.andengine.extension.physics.box2d.PhysicsWorld;

public class CreativePlayer extends Player {

	CreativePlayer player;
	
	public CreativePlayer(float pX, float pY, PhysicsWorld physicsWorld) {
		super(pX, pY, physicsWorld);
	}

	@Override
	public void setVelocityDirection(float f, float g) {
		playerBody.setLinearVelocity(15 * f, 15 * g);
	}

}
