package hud;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseLinear;

import manager.ResourcesManager;
import manager.SceneManager;
import object.tile.Tile;
import scene.GameScene;
import world.World;

public class InventoryItem extends Tile {

    private FlatCraftHUD mHUD;
    private int mCnt;
    private Text mText;

    private static boolean tranferring = false;

    public static final int REGNERATE_TIME = 20;

    private boolean onQuickAccess = false;

    private boolean regenerate = false;

    public InventoryItem(int pTileType, int cnt) {
        super(0, 0, pTileType);
        this.setCullingEnabled(false);
        setScale(0.75f);
        mCnt = cnt;
        mText = new Text(this.getX(), this.getY(), ResourcesManager.caviarDreamsGame, "000",
                ResourcesManager.vertexBufferObjectManager);
        mText.setX(mText.getX() + mText.getWidth() / 2);
        mText.setText(String.valueOf(cnt));
        this.attachChild(mText);
    }

    public InventoryItem(int pTileType, int cnt, boolean pRegenerate) {
        this(pTileType, cnt);
        regenerate = pRegenerate;
        if (regenerate) {
            this.registerUpdateHandler(new TimerHandler(REGNERATE_TIME, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    if (InventoryItem.this.mCnt < 20)
                        InventoryItem.this.give();
                }
            }));
        }
    }

    void setListener(FlatCraftHUD flatCraftHUD) {
        mHUD = flatCraftHUD;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
                                 float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionUp()) {
            GameScene gs = (GameScene) SceneManager.getCurrentScene();
            gs.getWorld().setPlaceMode(World.MODE_PLACE_TILES);
            gs.setTilePlacementMode(World.MODE_PLACE_TILES);
        }
        if (onQuickAccess) {
            if (pSceneTouchEvent.isActionUp()) {
                ResourcesManager.buttonClickSound.play();
                mHUD.currItem = this;
                ResourcesManager.selector.setPosition(this);
                return true;
            }
        } else if (!tranferring) {
            if (pSceneTouchEvent.isActionUp()) {
                tranferring = true;
                ResourcesManager.buttonClickSound.play();

                final float new_x = mHUD.currItem.getX(), new_y = mHUD.currItem.getY();
                final float old_x = this.getX(), old_y = this.getY();

                this.detachSelf();
                mHUD.currItem.detachSelf();

                final float[] new_pos = mHUD.getInventoryScene().convertSceneCoordinatesToLocalCoordinates(new_x, new_y);
                final float[] old_pos = mHUD.getInventoryScene().convertLocalCoordinatesToSceneCoordinates(old_x, old_y);

                this.setPosition(old_pos[0], old_pos[1]);
                mHUD.currItem.setPosition(new_pos[0], new_pos[1]);

                mHUD.getInventoryScene().attachChild(mHUD.currItem);
                mHUD.attachChild(this);

                mHUD.currItem.registerEntityModifier(new MoveModifier(0.5f, mHUD.currItem.getX(), mHUD.currItem.getY(),
                        old_x, old_y, EaseLinear.getInstance()));
                this.registerEntityModifier(new MoveModifier(0.5f, this.getX(), this.getY(), new_x, new_y,
                        EaseLinear.getInstance()));
                this.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        tranferring = false;
                    }
                }));

                mHUD.currItem.setOnQuickAccess(false);
                this.onQuickAccess = true;

                mHUD.swap(mHUD.currItem, this);

                mHUD.currItem = this;
                return true;
            }
        }
        return false;
    }

    public boolean take() {
        if (mCnt > 0) {
            mCnt--;
            mText.setText(String.valueOf(mCnt));
            return true;
        } else {
            return false;
        }
    }

    public void give() {
        mCnt++;
        mText.setText(String.valueOf(mCnt));
    }

    public void setOnQuickAccess(boolean onQuickAccess) {
        this.onQuickAccess = onQuickAccess;
    }
}
