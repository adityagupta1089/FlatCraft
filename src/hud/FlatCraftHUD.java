package hud;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;
import world.World;

public class FlatCraftHUD extends HUD {

	private List<InventoryItem> quickAccess;
	private List<InventoryItem> remainingTiles;

	public InventoryItem currItem;

	public FlatCraftHUD(World world) {
		quickAccess = new LinkedList<InventoryItem>();
		remainingTiles = new LinkedList<InventoryItem>();
		world.onPopulateQuickAccess(quickAccess);
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
			if (cnt++ < 8) {
				ie.setListener(this);
				ie.setPosition(X0 + (2 * cnt - 1) * deltaX + deltaX / 2, pY);
				attachChild(ie);
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
