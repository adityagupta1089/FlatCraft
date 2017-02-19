package manager;

import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import base.BaseScene;
import scene.GameScene;
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
		setScene(menuScene);
		disposeSplashScene();
	}

	// --------------------------------------------------------------//
	// Getters
	// --------------------------------------------------------------//
	public static BaseScene getCurrentScene() {
		return currentScene;
	}

}
