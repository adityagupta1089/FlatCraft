package hud;

import org.andengine.engine.camera.hud.HUD;

public class FlatCraftHUD extends HUD {

	public FlatCraftHUD() {
		this.attachChild(new InventoryScene());
	}

}
