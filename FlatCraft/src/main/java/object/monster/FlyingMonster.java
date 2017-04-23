package object.monster;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import java.util.Random;

import manager.ResourcesManager;
import object.player.Player;
import object.tile.Tile;

public class FlyingMonster extends Monster {

    private static final Random nd = new Random();

    public FlyingMonster(float pX, float pY, PhysicsWorld physicsWorld, Player mp) {
        super(pX, pY, ResourcesManager.enemy_flying, mp);
        this.animate(new long[]{50, 50, 50, 50, 50}, new int[]{0, 1, 2, 1, 0}, true);
        monsterBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory
                .createFixtureDef(1, 0.2f, 0.2f));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, monsterBody, true, false));
        p = mp;
        this.registerUpdateHandler(new TimerHandler(2f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                FlyingMonster.this.monsterBody.setLinearVelocity(new Vector2((nd.nextBoolean() ? 8 : -8), nd.nextFloat() * 12));
            }
        }));
        monsterBody.setUserData("monster");
    }

    private static final float THRESHOLD = 0.5f;

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        if (Math.abs(this.getX() - p.getX()) < 3 * Tile.TILE_EDGE) {
            this.monsterBody.setLinearVelocity(new Vector2(Math.signum(p.getX() - this.getX()) * 4, nd.nextFloat() * 12));
        }
        if (this.monsterBody.getLinearVelocity().x < -THRESHOLD) this.setFlippedHorizontal(true);
        else if (this.monsterBody.getLinearVelocity().x > THRESHOLD) this.setFlippedHorizontal(false);
        else this.setCurrentTileIndex(0);
        super.onManagedUpdate(pSecondsElapsed);
    }
}
