package world;

import com.badlogic.gdx.math.Vector2;

public class CreativeWorld extends World {

	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = 0;

	private static final float PLAYER_DAMPING = 1.5f;
	private static final float PLAYER_STOP_EPSILON = 1f;

	public CreativeWorld() {
		super();
		player.setLinearDamping(PLAYER_DAMPING);
		player.setStopEpsilon(PLAYER_STOP_EPSILON);
	}

	@Override
	public Vector2 getGravity() {
		return new Vector2(GRAVITY_X, GRAVITY_Y);
	}

}
