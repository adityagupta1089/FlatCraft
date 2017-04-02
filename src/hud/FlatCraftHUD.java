package hud;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class FlatCraftHUD extends HUD {

	private List<InventoryItem> quickAccess;
	private List<InventoryItem> remainingTiles;

	public InventoryItem currItem;

	public FlatCraftHUD() {
		quickAccess = new LinkedList<InventoryItem>();
		remainingTiles = new LinkedList<InventoryItem>();
		ResourcesManager.world.onPopulateQuickAccess(quickAccess);
		Sprite base = new Sprite(ResourcesManager.WIDTH / 2,
				100 + ResourcesManager.inventoryBaseRegion.getHeight() / 2,
				ResourcesManager.WIDTH / 2, 131, ResourcesManager.inventoryBaseRegion,
				ResourcesManager.vertexBufferObjectManager);
		attachChild(base);
		int cnt = 0;
		final float X0 = base.getX() - base.getWidth() / 2;
		final float Xn = base.getX() + base.getWidth() / 2;
		final float deltaX = (Xn - X0) / 16;
		final float pY = base.getY();
		for (InventoryItem ie : quickAccess) {
			if (cnt == 0) currItem = ie;
			if (cnt++ < 8) {
				ie.setListener(this);
				attachChild(ie);
				ie.setPosition(X0 + (2 * cnt - 1) * deltaX, pY);
				registerTouchArea(ie);
			} else {
				remainingTiles.add(ie);
			}
		}
		for (InventoryItem re : remainingTiles) {
			quickAccess.remove(re);
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

}
