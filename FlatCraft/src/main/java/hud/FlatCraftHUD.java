package hud;

import java.util.ArrayList;
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
		super();
		entities = new ArrayList<>();
		quickAccess = new ArrayList<>();
		ResourcesManager.world.onPopulateQuickAccess(quickAccess);
	}

	public void initTiles() {
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

		ResourcesManager.selector = new Sprite(X0 + deltaX, pY, 2 * deltaX, base.getHeight(),
				ResourcesManager.selectorRegion, ResourcesManager.vertexBufferObjectManager);

		for (InventoryItem ie : quickAccess) {
			if (cnt == 0) currItem = ie;
			if (cnt++ < 8) {
				ie.setListener(this);
				this.attachChild(ie);
				ie.setPosition(X0 + (2 * cnt - 1) * deltaX, pY);
				this.registerTouchArea(ie);
			} else break;
		}

		this.attachChild(ResourcesManager.selector);
	}

	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException {
		super.attachChild(pEntity);
		entities.add(pEntity);
	}

	private void cleanEntities() {
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
