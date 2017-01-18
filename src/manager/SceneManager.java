package manager;

import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import base.BaseScene;
import scene.GameScene;
import scene.SplashScene;

public class SceneManager {
	// --------------------------------------------------------------//
	// Scenes
	// --------------------------------------------------------------//
	public static BaseScene currentScene;
	public static GameScene gameScene;
	public static SplashScene splashScene;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	public static void setScene(BaseScene scene) {
		ResourcesManager.engine.setScene(scene);
		currentScene = scene;
	}

	public static void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene() {
		ResourcesManager.unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}

	// --------------------------------------------------------------//
	// Getters
	// --------------------------------------------------------------//
	public static BaseScene getCurrentscene() {
		return currentScene;
	}

}
