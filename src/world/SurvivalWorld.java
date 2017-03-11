package world;

import com.badlogic.gdx.math.Vector2;

public class SurvivalWorld extends World {

	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = -17f;

	public SurvivalWorld() {
		super();
	}

	@Override
	public Vector2 getGravity() {
		return new Vector2(GRAVITY_X, GRAVITY_Y);
	}

}
