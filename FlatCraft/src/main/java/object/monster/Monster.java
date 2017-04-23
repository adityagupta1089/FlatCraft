package object.monster;

import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import manager.ResourcesManager;
import object.player.Player;
import object.tile.Tile;

public class Monster extends AnimatedSprite {

    protected Body monsterBody;
    protected Player p;

    public Monster(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, Player mp) {
        super(pX, pY, Tile.TILE_EDGE, Tile.TILE_EDGE, pTiledTextureRegion, ResourcesManager.vertexBufferObjectManager);
    }
}