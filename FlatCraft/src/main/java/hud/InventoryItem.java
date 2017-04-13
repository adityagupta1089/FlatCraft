package hud;

import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import manager.ResourcesManager;
import object.tile.Tile;

public class InventoryItem extends Tile {

	private FlatCraftHUD mHUD;
	private int mCnt;
	private Text mText;

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

	public void setListener(FlatCraftHUD flatCraftHUD) {
		mHUD = flatCraftHUD;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if (pSceneTouchEvent.isActionUp()) {
			ResourcesManager.buttonClickSound.play();
			mHUD.currItem = this;
			ResourcesManager.selector.setPosition(this);
			return true;
		} else return false;
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
}
