package object.monster;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.Constants;

import java.util.Random;

import manager.ResourcesManager;
import object.player.Player;
import object.tile.Tile;

public class WalkingMonster extends Monster {

    private static final Random r = new Random();

    public WalkingMonster(float pX, float pY, PhysicsWorld physicsWorld, Player mp) {
        super(pX, pY, ResourcesManager.enemy_walking, mp);
        this.animate(new long[]{200, 200, 200, 200, 200}, new int[]{0, 1, 2, 1, 0}, true);
        final float[] sceneCenterCoordinates = this.getSceneCenterCoordinates();
        final float centerX = sceneCenterCoordinates[Constants.VERTEX_INDEX_X];
        final float centerY = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y];
        monsterBody = PhysicsFactory.createBoxBody(physicsWorld, centerX, centerY, this.getWidthScaled() * 0.75f, this
                .getHeightScaled() * 0.75f, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1, 0f, 0.2f));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, monsterBody, true, false));
        p = mp;
        this.registerUpdateHandler(new TimerHandler(2f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                WalkingMonster.this.monsterBody.setLinearVelocity(new Vector2((r.nextBoolean() ? 8 : -8), 0));
            }
        }));
        monsterBody.setUserData("monster");
    }

    private static final float THRESHOLD = 0.5f;

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        if (Math.abs(this.getX() - p.getX()) < 3 * Tile.TILE_EDGE) {
            this.monsterBody.setLinearVelocity(new Vector2(Math.signum(p.getX() - this.getX()) * 4, 0));
        }
        if (this.monsterBody.getLinearVelocity().x < -THRESHOLD) this.setFlippedHorizontal(true);
        else if (this.monsterBody.getLinearVelocity().x > THRESHOLD) this.setFlippedHorizontal(false);
        else this.setCurrentTileIndex(0);
        super.onManagedUpdate(pSecondsElapsed);
    }
}
