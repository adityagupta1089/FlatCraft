package scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import base.BaseScene;
import manager.ResourcesManager;
import manager.SceneManager;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private MenuScene creditsChildScene;
	private MenuScene helpChildScene;

	private final int MENU_PLAY = 0;
	private final int MENU_CREDITS = 1;
	private final int MENU_HELP = 2;
	private final int MENU_EXIT = 3;

	private final int HELP_BACK = 4;

	private final int CREDITS_BACK = 5;

	private static final int SPACING = 150;

	private WebView helpView;
	private WebView creditsView;

	private static final int VIEW_LEFT_MARGIN = 125;
	private static final int VIEW_RIGHT_MARGIN = 125;
	private static final int VIEW_TOP_MARGIN = 250;
	private static final int VIEW_BOTTOM_MARGIN = 180;

	private List<IEntity> entities;

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, -110);

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(MENU_PLAY,
				ResourcesManager.caviarDreams, "PLAY", vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem creditsMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(MENU_CREDITS,
				ResourcesManager.caviarDreams, "CREDITS", vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem helpMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(MENU_HELP,
				ResourcesManager.caviarDreams, "HELP", vertexBufferObjectManager), 1.2f, 1);
		final IMenuItem exitMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(MENU_EXIT,
				ResourcesManager.caviarDreams, "EXIT", vertexBufferObjectManager), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(creditsMenuItem);
		menuChildScene.addMenuItem(helpMenuItem);
		menuChildScene.addMenuItem(exitMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 100);
		creditsMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() - SPACING);
		helpMenuItem.setPosition(playMenuItem.getX(), creditsMenuItem.getY() - SPACING);
		exitMenuItem.setPosition(playMenuItem.getX(), helpMenuItem.getY() - SPACING);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	private void createHelpMenuScene() {
		helpChildScene = new MenuScene(camera);
		helpChildScene.setPosition(0, 0);
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(HELP_BACK,
				ResourcesManager.caviarDreams, "Back", vertexBufferObjectManager), 1.2f, 1);
		ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				helpView = new WebView(ResourcesManager.gameActivity);
				helpView.loadUrl("file:///android_asset/gfx/menu/help.html");
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						ResourcesManager.WIDTH - VIEW_LEFT_MARGIN - VIEW_RIGHT_MARGIN,
						ResourcesManager.HEIGHT - VIEW_TOP_MARGIN - VIEW_BOTTOM_MARGIN);
				params.setMargins(VIEW_LEFT_MARGIN, VIEW_TOP_MARGIN, VIEW_RIGHT_MARGIN,
						VIEW_BOTTOM_MARGIN);
				ResourcesManager.gameActivity.addContentView(helpView, params);
			}
		});

		helpChildScene.addMenuItem(backMenuItem);

		helpChildScene.setPosition(helpChildScene.getX(), helpChildScene.getY() - 400);

		helpChildScene.buildAnimations();
		helpChildScene.setBackgroundEnabled(false);

		helpChildScene.setOnMenuItemClickListener(this);

		detachChild(menuChildScene);
		setChildScene(helpChildScene);
	}

	private void createCreditsMenuScene() {
		creditsChildScene = new MenuScene(camera);
		creditsChildScene.setPosition(0, 0);
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(CREDITS_BACK,
				ResourcesManager.caviarDreams, "Back", vertexBufferObjectManager), 1.2f, 1);
		ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				creditsView = new WebView(ResourcesManager.gameActivity);
				creditsView.loadUrl("file:///android_asset/gfx/menu/credits.html");
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						ResourcesManager.WIDTH - VIEW_LEFT_MARGIN - VIEW_RIGHT_MARGIN,
						ResourcesManager.HEIGHT - VIEW_TOP_MARGIN - VIEW_BOTTOM_MARGIN);
				params.setMargins(VIEW_LEFT_MARGIN, VIEW_TOP_MARGIN, VIEW_RIGHT_MARGIN,
						VIEW_BOTTOM_MARGIN);
				ResourcesManager.gameActivity.addContentView(creditsView, params);
			}
		});

		creditsChildScene.addMenuItem(backMenuItem);

		creditsChildScene.setPosition(creditsChildScene.getX(), creditsChildScene.getY() - 400);

		creditsChildScene.buildAnimations();
		creditsChildScene.setBackgroundEnabled(false);

		creditsChildScene.setOnMenuItemClickListener(this);

		detachChild(menuChildScene);
		setChildScene(creditsChildScene);
	}

	@Override
	public void createScene() {
		entities = new ArrayList<IEntity>();
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void attachChild(IEntity pEntity) throws IllegalStateException {
		entities.add(pEntity);
		super.attachChild(pEntity);
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

	public void clearScene() {
		engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				cleanEntities();
				clearTouchAreas();
				clearUpdateHandlers();
				System.gc();
			}
		});
	}

	@Override
	public void disposeScene() {
		if (ResourcesManager.menuMusic.isPlaying()) ResourcesManager.menuMusic.pause();
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setCenter(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2);
		clearScene();
	}

	private void createBackground() {
		attachChild(new Sprite(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2,
				ResourcesManager.menuBackgroundRegion, vertexBufferObjectManager) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				ResourcesManager.buttonClickSound.play();
				if (ResourcesManager.menuMusic.isPlaying()) ResourcesManager.menuMusic.pause();
				SceneManager.loadGameScene(engine);
				return true;
			case MENU_CREDITS:
				ResourcesManager.buttonClickSound.play();
				createCreditsMenuScene();
				return true;
			case MENU_HELP:
				ResourcesManager.buttonClickSound.play();
				createHelpMenuScene();
				return true;
			case MENU_EXIT:
				ResourcesManager.buttonClickSound.play();
				if (ResourcesManager.menuMusic.isPlaying()) ResourcesManager.menuMusic.pause();
				System.exit(0);
				return true;
			case HELP_BACK:
				ResourcesManager.buttonClickSound.play();
				helpChildScene.dispose();
				helpChildScene.detachSelf();
				ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((ViewGroup) helpView.getParent()).removeView(helpView);
					}
				});
				setChildScene(menuChildScene);
				return true;
			case CREDITS_BACK:
				ResourcesManager.buttonClickSound.play();
				creditsChildScene.dispose();
				creditsChildScene.detachSelf();
				ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((ViewGroup) creditsView.getParent()).removeView(creditsView);
					}
				});
				setChildScene(menuChildScene);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void onBackKeyPressed() {
		ResourcesManager.buttonClickSound.play();
		if (ResourcesManager.menuMusic.isPlaying()) ResourcesManager.menuMusic.pause();
		System.exit(0);
	}
}
