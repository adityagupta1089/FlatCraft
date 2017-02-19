package manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import base.BaseScene;
import scene.GameScene;
import scene.LoadingScene;
import scene.MainMenuScene;
import scene.SplashScene;

public class SceneManager {
	// --------------------------------------------------------------//
	// Scenes
	// --------------------------------------------------------------//
	public static BaseScene currentScene;
	public static GameScene gameScene;
	public static SplashScene splashScene;
	public static MainMenuScene menuScene;
	public static LoadingScene loadingScene;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	public static void setScene(BaseScene scene) {
		ResourcesManager.engine.setScene(scene);
		currentScene = scene;
	}

	// --------------------------------------------------------------//
	// Splash Scene
	// --------------------------------------------------------------//
	public static void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private static void disposeSplashScene() {
		ResourcesManager.unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}

	// --------------------------------------------------------------//
	// Main Menu Scene
	// --------------------------------------------------------------//
	public static void createMenuScene() {
		ResourcesManager.loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		setScene(menuScene);
		disposeSplashScene();
	}

	// --------------------------------------------------------------//
	// Menu Scene <-> Game Scene
	// --------------------------------------------------------------//
	public static void loadGameScene(final Engine mEngine) {
		setScene(loadingScene);
		ResourcesManager.unloadMenuTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}

	public static void loadMenuScene(final Engine mEngine) {
		setScene(loadingScene);
		gameScene.disposeScene();
		ResourcesManager.unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.loadMenuTextures();
				setScene(menuScene);
			}
		}));
	}

	// --------------------------------------------------------------//
	// Getters
	// --------------------------------------------------------------//
	public static BaseScene getCurrentScene() {
		return currentScene;
	}

}
