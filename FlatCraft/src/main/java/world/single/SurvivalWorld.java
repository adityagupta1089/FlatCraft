package world.single;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import java.util.List;
import java.util.Random;

import hud.InventoryItem;
import manager.ResourcesManager;
import manager.SceneManager;
import object.monster.FlyingMonster;
import object.monster.Monster;
import object.monster.WalkingMonster;
import object.player.SurvivalPlayer;
import object.tile.Tile;
import spritesheet.TileSpritesheet;
import world.World;

public class SurvivalWorld extends World implements world.constants.CreativeConstants {

    private static final float GRAVITY_X = 0;
    private static final float GRAVITY_Y = -17f;

    private static final float PLAYER_STOP_EPSILON = 1f;

    private long startTime;

    public SurvivalWorld(BoundCamera camera) {
        super(camera);
        startTime = SystemClock.elapsedRealtime();
        ResourcesManager.gameRunning = true;
        camera.setBounds(0, 0, GRID_WIDTH * Tile.TILE_EDGE, GRID_HEIGHT * Tile.TILE_EDGE);
        camera.setBoundsEnabled(true);
        ((SurvivalPlayer) player).setStopEpsilon(PLAYER_STOP_EPSILON);
        physicsWorld.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();
                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
                    if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("tile") || x2.getBody()
                            .getUserData().equals("player") && x1.getBody().getUserData().equals("tile")) {
                        ((SurvivalPlayer) player).increaseFootContacts();
                    }
                    if (x1.getBody().getUserData().equals("player") && x2.getBody().getUserData().equals("monster") || x2
                            .getBody()
                            .getUserData().equals("player") && x1.getBody().getUserData().equals("monster")) {
                        SurvivalWorld.this.playerDied();
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

        this.registerUpdateHandler(new TimerHandler(15f, true, new ITimerCallback() {

            private Random r = new Random();

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                Monster m;
                int x = 0, y = DIRT_WIDTH + 1;
                int tries = 0;
                while (tries < 100 && (Math.abs(x - player.getX()) / Tile.TILE_EDGE < 2 || Math.abs(y - player.getY()) / Tile
                        .TILE_EDGE < 2 || grid.indexOfKey(position(x, y)) < 0)) {
                    x = r.nextInt(GRID_WIDTH);
                    y = DIRT_WIDTH + 1 + r.nextInt(GRID_HEIGHT - (DIRT_WIDTH + 1));
                    tries++;
                }
                if (false/*r.nextBoolean()*/) {
                    m = new WalkingMonster((x + 1) * Tile.TILE_EDGE, (y + 1) * Tile.TILE_EDGE, physicsWorld, player);
                } else {
                    m = new FlyingMonster((x + 1) * Tile.TILE_EDGE, (y + 1) * Tile.TILE_EDGE, physicsWorld, player);
                }
                m.setPosition(m.getX() + m.getWidth() / 2, m.getY() +
                        m.getHeight() / 2);
                SurvivalWorld.this.attachChild(m);
            }
        }));
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
            createTile(i, -1, TileSpritesheet.DIRT_ID, false);
            createTile(i, GRID_HEIGHT, TileSpritesheet.DIRT_ID, false);
        }
        for (i = 0; i < GRID_HEIGHT; i++) {
            createTile(-1, i, TileSpritesheet.DIRT_ID, false);
            createTile(GRID_WIDTH, i, TileSpritesheet.DIRT_ID, false);
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (!ResourcesManager.gameRunning) return false;
        if (ResourcesManager.hud.inventorySceneShown) return false;
        if (pSceneTouchEvent.isActionUp()) {
            int blockX = ((int) pSceneTouchEvent.getX()) / Tile.TILE_EDGE;
            int blockY = ((int) pSceneTouchEvent.getY()) / Tile.TILE_EDGE;
            int playerx = ((int) player.getX()) / Tile.TILE_EDGE;
            int playery = ((int) player.getY()) / Tile.TILE_EDGE;
            if (Math.abs(blockX - playerx) + Math.abs(blockY - playery) > 8) return false;
            if (blockX != playerx || blockY != playery) {
                if (placeMode == MODE_DELETE_TILES && grid.indexOfKey(position(blockX, blockY)) > 0) {
                    ResourcesManager.hud.currItem.give();
                    deleteTile(blockX, blockY);
                    return true;
                } else if (placeMode == MODE_PLACE_TILES && grid.indexOfKey(position(blockX, blockY)) < 0) {
                    if (ResourcesManager.hud.currItem.take()) {
                        createTile(blockX, blockY, ResourcesManager.hud.currItem.mTileType, true, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static int TIME = 20;

    @Override
    public void onPopulateQuickAccess(List<InventoryItem> qa) {
        for (int i = TileSpritesheet.MIN_INDEX; i < new Random().nextInt(TileSpritesheet.MAX_INDEX - 8) + 8; i++) {
            qa.add(new InventoryItem(i, 5, true));
        }
        ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                while (ResourcesManager.hud == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final Text regenerateText = new Text(0, 900, ResourcesManager.caviarDreamsGame, "RG Time: 00", ResourcesManager
                        .vertexBufferObjectManager);
                regenerateText.setColor(Color.BLACK);
                regenerateText.setScale(1.5f);
                regenerateText.registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        if (TIME > 0) {
                            TIME--;
                        } else {
                            ResourcesManager.buttonClickSound.play();
                            TIME = 20;
                        }
                        regenerateText.setText("RG Time: " + TIME);
                    }
                }));
                regenerateText.setPosition(100 + regenerateText.getWidth() / 2, regenerateText.getY() -
                        regenerateText.getHeight() / 2);
                ResourcesManager.hud.attachChild(regenerateText);
            }
        });
    }

    public void playerDied() {
        final long milliseconds = SystemClock.elapsedRealtime() - startTime;
        ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context = ResourcesManager.gameActivity;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResourcesManager.buttonClickSound.play();
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                SceneManager.getCurrentScene().onBackKeyPressed();
                                break;
                        }
                    }
                };
                int seconds = (int) (milliseconds / 1000) % 60;
                int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
                int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
                String time = "";
                if (hours > 0) time += hours + " hours";
                if (minutes > 0) time += minutes + " minutes";
                if (seconds > 0) time += seconds + " seconds!";
                builder.setTitle("Yay you survived for " + time).setCancelable(false).setPositiveButton("Go Back!",
                        dialogClickListener);
                builder.show();
                ResourcesManager.gameRunning = false;
                SurvivalWorld.this.setIgnoreUpdate(true);
            }
        });
    }
}
