package world;

import org.andengine.engine.camera.Camera;

import com.badlogic.gdx.math.Vector2;

import object.player.SurvivalPlayer;

public class SurvivalWorld extends World {

	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = -17f;

	private static final float PLAYER_STOP_EPSILON = 1f;

	SurvivalPlayer player;

	public SurvivalWorld(Camera camera) {
		super(camera);
		player.setStopEpsilon(PLAYER_STOP_EPSILON);
	}

	@Override
	public Vector2 getGravity() {
		return new Vector2(GRAVITY_X, GRAVITY_Y);
	}

	@Override
	public void createPlayer(Camera camera) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createBackground() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createForeground() {
		// TODO Auto-generated method stub

	}

}
