package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import base.BaseScene;
import manager.ResourcesManager;

public class LoadingScene extends BaseScene {
	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		attachChild(new Text(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2, ResourcesManager.caviarDreams, "Loading...", vertexBufferObjectManager));
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public void disposeScene() {

	}
}
