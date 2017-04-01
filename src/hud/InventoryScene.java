package hud;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class InventoryScene extends Scene {

	public InventoryScene() {
		attachChild(new Sprite(640, 100, 100, 100, ResourcesManager.inventoryBaseRegion,
				ResourcesManager.vertexBufferObjectManager));
	}

}
