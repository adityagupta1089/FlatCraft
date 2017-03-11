package object;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import manager.ResourcesManager;
import object.tile.Tile;

public class Player extends Sprite {
	private Body playerBody;

	private static final int VELOCITY_X = 10;
	private static final int VELOCITY_Y = 15;
	private static final float MOVEMENT_THRESHOLD = 0.4f;

	private float STOP_EPSILON;

	public Player(float pX, float pY, PhysicsWorld physicsWorld) {
		super(pX, pY, Tile.TILE_EDGE, Tile.TILE_EDGE, ResourcesManager.playerRegion,
				ResourcesManager.vertexBufferObjectManager);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(1, 0f, 0.5f));
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, playerBody, true, false));
	}

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

	public void setLinearDamping(float f) {
		playerBody.setLinearDamping(f);
	}

	public void setStopEpsilon(float playerStopEpsilon) {
		this.STOP_EPSILON = playerStopEpsilon;
	}

}
