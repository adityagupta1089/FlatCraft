package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import base.BaseScene;
import manager.ResourcesManager;

public class SplashScene extends BaseScene {

	private Sprite splash;

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		splash = new Sprite(0, 0, ResourcesManager.splash_region, vertexBufferObjectManager) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.5f);
		splash.setPosition(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2);
		attachChild(splash);

	}

	@Override
	public void onBackKeyPressed() {

	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

}
