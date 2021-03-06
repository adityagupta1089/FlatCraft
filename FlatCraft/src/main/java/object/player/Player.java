package object.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;

import manager.ResourcesManager;
import object.tile.Tile;

public abstract class Player extends Sprite {

    Body playerBody;

    private static final int PLAYER_BORDER = 5;

    public Player(float pX, float pY, PhysicsWorld physicsWorld) {
        super(pX, pY, Tile.TILE_EDGE - PLAYER_BORDER, Tile.TILE_EDGE - PLAYER_BORDER,
                ResourcesManager.playerRegion, ResourcesManager.vertexBufferObjectManager);
        playerBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody,
                PhysicsFactory.createFixtureDef(1, 0f, 0.5f));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, playerBody, true, false));
    }

    public Player(float pX, float pY, PhysicsWorld physicsWorld, int mID) {
        super(pX, pY, Tile.TILE_EDGE - PLAYER_BORDER, Tile.TILE_EDGE - PLAYER_BORDER,
                getRegion(mID), ResourcesManager.vertexBufferObjectManager);
        playerBody = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody,
                PhysicsFactory.createFixtureDef(1, 0f, 0.5f));
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, playerBody, true, false));
    }

    private static ITextureRegion getRegion(int mID) {
        switch (mID % 4) {
            case 1:
                return ResourcesManager.player2Region;
            case 2:
                return ResourcesManager.player3Region;
            case 3:
                return ResourcesManager.player4Region;
            default:
                return ResourcesManager.playerRegion;
        }
    }

    public abstract void setVelocityDirection(float f, float g);

    public void setLinearDamping(float f) {
        playerBody.setLinearDamping(f);
    }

    public Body getBody() {
        return playerBody;
    }
}
