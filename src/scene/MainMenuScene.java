package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import base.BaseScene;
import manager.ResourcesManager;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_PLAY, ResourcesManager.play_region, vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_OPTIONS, ResourcesManager.options_region, vertexBufferObjectManager), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(optionsMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
		optionsMenuItem.setPosition(optionsMenuItem.getX(), optionsMenuItem.getY() - 110);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}

	private void createBackground() {
		attachChild(new Sprite(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2,
				ResourcesManager.menu_background_region, vertexBufferObjectManager) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX,
			float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case MENU_PLAY:
			return true;
		case MENU_OPTIONS:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}
}
