package hud;

import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class FlatCraftHUD extends HUD {

	private List<InventoryItem> quickAccess;

	public InventoryItem currItem;

	private List<IEntity> entities;

	public FlatCraftHUD() {
		entities = new LinkedList<IEntity>();
		quickAccess = new LinkedList<InventoryItem>();
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
			}
		}
	}

	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException {
		super.attachChild(pEntity);
		entities.add(pEntity);
	}

	public void cleanEntities() {
		for (IEntity entity : entities) {
			entity.clearEntityModifiers();
			entity.clearUpdateHandlers();
			entity.detachSelf();

			if (!entity.isDisposed()) {
				entity.dispose();
			}
		}

		entities.clear();
		entities = null;
	}

	@Override
	public void dispose() {
		ResourcesManager.engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				cleanEntities();
				clearTouchAreas();
				clearUpdateHandlers();
				System.gc();
			}
		});
	}

}
