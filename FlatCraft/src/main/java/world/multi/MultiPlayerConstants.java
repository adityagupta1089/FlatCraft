package world.multi;

import android.util.SparseArray;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.multiplayer.adt.message.IMessage;
import org.andengine.extension.multiplayer.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.server.SocketServer;
import org.andengine.extension.multiplayer.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.shared.SocketConnection;
import org.andengine.extension.multiplayer.util.MessagePool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import java.util.List;

import hud.InventoryItem;
import manager.ResourcesManager;
import object.player.CreativePlayer;
import object.player.Player;
import object.tile.Tile;
import spritesheet.TileSpritesheet;
import world.World;
import world.constants.CreativeConstants;

public class MultiPlayerConstants extends World implements CreativeConstants {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final int SERVER_PORT = 4444;

    private static final short FLAG_MESSAGE_SERVER_ADD_SPRITE = 1;
    private static final short FLAG_MESSAGE_SERVER_MOVE_SPRITE = FLAG_MESSAGE_SERVER_ADD_SPRITE + 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;
    private static final int DIALOG_SHOW_SERVER_IP_ID = DIALOG_ENTER_SERVER_IP_ID + 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private int mSpriteIDCounter;
    private final SparseArray<Player> mPlayers = new SparseArray<>();

    private String mServerIP = LOCALHOST_IP;
    private SocketServer<SocketConnectionClientConnector> mSocketServer;
    private ServerConnector<SocketConnection> mServerConnector;

    private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();

    // ===========================================================
    // Constructor
    // ===========================================================
    public MultiPlayerConstants(BoundCamera camera) {
        super(camera);
        camera.setBounds(0, 0, GRID_WIDTH * Tile.TILE_EDGE, GRID_HEIGHT * Tile.TILE_EDGE);
        camera.setBoundsEnabled(true);
        player.setLinearDamping(PLAYER_DAMPING);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void createPlayer(Camera camera) {
        player = new CreativePlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE, (DIRT_WIDTH + 1) * Tile.TILE_EDGE, physicsWorld);
        player.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
        camera.setCenter(player.getX(), player.getY());
        camera.setChaseEntity(player);
        this.attachChild(player);
        entities.add(player);
    }

    @Override
    public Vector2 getGravity() {
        return new Vector2(GRAVITY_X, GRAVITY_Y);
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
                Sprite bgtile = new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, i * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE, BACKGROUND_TILE_EDGE, temp, ResourcesManager.vertexBufferObjectManager);
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
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onPopulateQuickAccess(List<InventoryItem> qa) {
        // TODO add more inventory items
        for (int i = TileSpritesheet.MIN_INDEX; i < TileSpritesheet.MAX_INDEX; i++) {
            qa.add(new InventoryItem(i, 100));
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
