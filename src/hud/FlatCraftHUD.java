package hud;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class FlatCraftHUD extends HUD {

	public FlatCraftHUD() {
		//@formatter:off
		attachChild(new Sprite(ResourcesManager.WIDTH / 2, 100 + ResourcesManager.inventoryBaseRegion.getHeight() / 2, ResourcesManager.WIDTH/3, 131, ResourcesManager.inventoryBaseRegion,  ResourcesManager.vertexBufferObjectManager));
		//@formatter:on
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

}
