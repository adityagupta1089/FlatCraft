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
    private static BaseScene currentScene;
    private static GameScene gameScene;
    private static SplashScene splashScene;
    private static MainMenuScene menuScene;
    private static LoadingScene loadingScene;

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
        ResourcesManager.menuMusic.play();
        menuScene = new MainMenuScene();
        loadingScene = new LoadingScene();
        setScene(menuScene);
        disposeSplashScene();
    }

    // --------------------------------------------------------------//
    // Menu Scene <-> Game Scene
    // --------------------------------------------------------------//
    public static void loadGameScene(final Engine mEngine, final int mode) {
        setScene(loadingScene);
        ResourcesManager.unloadMenuTextures();
        mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                ResourcesManager.loadGameResources();
                ResourcesManager.gameMusic.play();
                GameScene.setGameMode(mode);
                gameScene = new GameScene();
                setScene(gameScene);
            }
        }));
    }

    public static void loadMenuScene(final Engine mEngine) {
        ResourcesManager.menuMusic.play();
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
