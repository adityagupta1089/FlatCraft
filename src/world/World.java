package world;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import object.player.Player;
import object.tile.Tile;

public abstract class World extends Scene {

	private static final float SOLID_OBJECT_DENSITY = 1;
	private static final float SOLID_OBJECT_ELASTICITY = 0;
	private static final float SOLID_OBJECT_FRICTION = 0.5f;

	protected Tile[][] grid;
	public Player player;

	protected PhysicsWorld physicsWorld;

	protected static final FixtureDef fixedSolidObjectFixtureDef = PhysicsFactory
			.createFixtureDef(SOLID_OBJECT_DENSITY, SOLID_OBJECT_ELASTICITY, SOLID_OBJECT_FRICTION);

	public World(Camera camera) {
		createPhysics();
		createBackground();
		createForeground();
		createPlayer(camera);
	}

	public abstract void createPlayer(Camera camera);

	public void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, getGravity(), false);
		registerUpdateHandler(physicsWorld);
	}

	public abstract Vector2 getGravity();

	public abstract void createBackground();

	public abstract void createForeground();
}
