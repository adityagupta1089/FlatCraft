package world.single;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import java.util.List;

import hud.InventoryItem;
import manager.ResourcesManager;
import object.monster.FlyingMonster;
import object.monster.WalkingMonster;
import object.player.SurvivalPlayer;
import object.tile.Tile;
import spritesheet.TileSpritesheet;
import world.World;

public class SurvivalWorld extends World implements world.constants.CreativeConstants {

    private static final float GRAVITY_X = 0;
    private static final float GRAVITY_Y = -17f;

    private static final float PLAYER_STOP_EPSILON = 1f;


    public SurvivalWorld(BoundCamera camera) {
        super(camera);
        camera.setBounds(0, 0, GRID_WIDTH * Tile.TILE_EDGE, GRID_HEIGHT * Tile.TILE_EDGE);
        camera.setBoundsEnabled(true);
        ((SurvivalPlayer) player).setStopEpsilon(PLAYER_STOP_EPSILON);
        physicsWorld.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
                    if (x1.getBody().getUserData().equals("player") || x2.getBody().getUserData().equals("player")) {
                        ((SurvivalPlayer) player).increaseFootContacts();
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
                    if (x1.getBody().getUserData().equals("player") || x2.getBody().getUserData().equals("player")) {
                        ((SurvivalPlayer) player).decreaseFootContacts();
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    @Override
    public Vector2 getGravity() {
        return new Vector2(GRAVITY_X, GRAVITY_Y);
    }

    @Override
    public void createPlayer(Camera camera) {
        player = new SurvivalPlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE, (DIRT_WIDTH + 1) * Tile.TILE_EDGE, physicsWorld);
        player.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
        camera.setCenter(player.getX(), player.getY());
        camera.setChaseEntity(player);
        this.attachChild(player);
        entities.add(player);

        WalkingMonster walkingMonster = new WalkingMonster(Tile.TILE_EDGE, (DIRT_WIDTH + 1) * Tile.TILE_EDGE);
        walkingMonster.setPosition(walkingMonster.getX() + walkingMonster.getWidth() / 2, walkingMonster.getY() +
                walkingMonster.getHeight() / 2);
        this.attachChild(walkingMonster);
        entities.add(walkingMonster);

        FlyingMonster flyingMonster = new FlyingMonster(Tile.TILE_EDGE, (DIRT_WIDTH + 4) * Tile.TILE_EDGE);
        flyingMonster.setPosition(flyingMonster.getX() + flyingMonster.getWidth() / 2, flyingMonster.getY() +
                flyingMonster.getHeight() / 2);
        this.attachChild(flyingMonster);
        entities.add(flyingMonster);
    }

    @Override
    public void createBackground() {
        setBackground(new Background(Color.WHITE));
        int separationLayer = BACKGROUND_GRID_HEIGHT / 2;
        for (int i = 0; i < BACKGROUND_GRID_HEIGHT + 1; i++) {
            for (int j = 0; j < BACKGROUND_GRID_WIDTH + 1; j++) {
                TextureRegion temp = null;
                if (i < separationLayer) {
                    temp = ResourcesManager.skyBoxBottomRegion;
                } else if (i > separationLayer) {
                    temp = ResourcesManager.skyBoxTopRegion;
                } else {
                    temp = ResourcesManager.skyBoxSideHillsRegion;
                }
                Sprite bgtile = new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, i * BACKGROUND_TILE_EDGE +
                        BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE, BACKGROUND_TILE_EDGE, temp, ResourcesManager
                        .vertexBufferObjectManager);
                attachChild(bgtile);
                entities.add(bgtile);
            }
        }
    }

    @Override
    public void createForeground() {
        int i = 0;
        /* Layers of dirt */
        for (; i < DIRT_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                createTile(j, i, TileSpritesheet.DIRT_ID);

            }
        }
        /* One layer of grass */
        for (int j = 0; j < GRID_WIDTH; j++) {
            createTile(j, i, TileSpritesheet.DIRT_GRASS_ID);
        }

		/* Border */
        for (i = 0; i < GRID_WIDTH; i++) {
            createTile(i, -1, TileSpritesheet.DIRT_ID);
            createTile(i, GRID_HEIGHT, TileSpritesheet.DIRT_ID);
        }
        for (i = 0; i < GRID_HEIGHT; i++) {
            createTile(-1, i, TileSpritesheet.DIRT_ID);
            createTile(GRID_WIDTH, i, TileSpritesheet.DIRT_ID);
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (ResourcesManager.hud.inventorySceneShown) return false;
        if (pSceneTouchEvent.isActionUp()) {
            int blockX = ((int) pSceneTouchEvent.getX()) / Tile.TILE_EDGE;
            int blockY = ((int) pSceneTouchEvent.getY()) / Tile.TILE_EDGE;
            if (blockX != ((int) player.getX()) / Tile.TILE_EDGE || blockY != ((int) player.getY()) / Tile.TILE_EDGE) {
                if (placeMode == MODE_DELETE_TILES && grid.indexOfKey(position(blockX, blockY)) > 0) {
                    ResourcesManager.hud.currItem.give();
                    deleteTile(blockX, blockY);
                    return true;
                } else if (placeMode == MODE_PLACE_TILES && grid.indexOfKey(position(blockX, blockY)) < 0) {
                    if (ResourcesManager.hud.currItem.take()) {
                        createTile(blockX, blockY, ResourcesManager.hud.currItem.mTileType);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onPopulateQuickAccess(List<InventoryItem> qa) {
        for (int i = TileSpritesheet.MIN_INDEX; i < TileSpritesheet.MAX_INDEX; i++) {
            qa.add(new InventoryItem(i, 100));
        }
    }

}
