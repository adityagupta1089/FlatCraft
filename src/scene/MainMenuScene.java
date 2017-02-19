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
	private final int MENU_CREDITS = 2;
	private final int MENU_HELP = 3;
	private final int MENU_EXIT = 4;
	private static final int SPACING = 125;

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, -110);

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_PLAY, ResourcesManager.play_region, vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_OPTIONS, ResourcesManager.options_region, vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem creditsMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_OPTIONS, ResourcesManager.credits_region, vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem helpMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_OPTIONS, ResourcesManager.help_region, vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem exitMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(MENU_OPTIONS, ResourcesManager.exit_region, vertexBufferObjectManager), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(optionsMenuItem);
		menuChildScene.addMenuItem(creditsMenuItem);
		menuChildScene.addMenuItem(helpMenuItem);
		menuChildScene.addMenuItem(exitMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + SPACING);
		optionsMenuItem.setPosition(optionsMenuItem.getX(), playMenuItem.getY() - SPACING);
		creditsMenuItem.setPosition(optionsMenuItem.getX(), optionsMenuItem.getY() - SPACING);
		helpMenuItem.setPosition(optionsMenuItem.getX(), creditsMenuItem.getY() - SPACING);
		exitMenuItem.setPosition(optionsMenuItem.getX(), helpMenuItem.getY() - SPACING);

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
		case MENU_CREDITS:
			return true;
		case MENU_HELP:
			return true;
		case MENU_EXIT:
			System.exit(0);
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
