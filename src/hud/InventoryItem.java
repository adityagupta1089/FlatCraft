package hud;

import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import manager.ResourcesManager;
import object.tile.Tile;

public class InventoryItem extends Tile {

	private FlatCraftHUD mHUD;
	private int mCnt;
	private Text mText;

	public InventoryItem(String pTileType, int cnt) {
		super(0, 0, pTileType);
		setScale(0.75f);
		mCnt = cnt;
		mText = new Text(this.getX(), this.getY(), ResourcesManager.caviarDreamsGame, "00000",
				ResourcesManager.vertexBufferObjectManager);
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
			mHUD.currItem = this;
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
