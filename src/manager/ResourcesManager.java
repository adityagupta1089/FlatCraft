package manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import main.GameActivity;

public class ResourcesManager {
	// --------------------------------------------------------------//
	// Constants
	// --------------------------------------------------------------//
	public static final int HEIGHT = 1080;
	public static final int WIDTH = 1920;

	// --------------------------------------------------------------//
	// Variables from Game Activity
	// --------------------------------------------------------------//
	public static GameActivity gameActivity;
	public static Camera camera;
	public static Engine engine;
	public static VertexBufferObjectManager vertexBufferObjectManager;

	// --------------------------------------------------------------//
	// Variables for Splash Scene
	// --------------------------------------------------------------//
	public static ITextureRegion splash_region;
	private static BitmapTextureAtlas splashTextureAtlas;

	// --------------------------------------------------------------//
	// Variables for Main Menu Scene
	// --------------------------------------------------------------//
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;

	private BuildableBitmapTextureAtlas menuTextureAtlas;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	public static void prepare(GameActivity pGameActivity, Camera pCamera,
			VertexBufferObjectManager pVertexBufferObjectManager, Engine pEngine) {
		gameActivity = pGameActivity;
		camera = pCamera;
		vertexBufferObjectManager = pVertexBufferObjectManager;
		engine = pEngine;
	}

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, gameActivity,
				"menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, gameActivity,
				"play.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, gameActivity,
				"options.png");

		try {
			this.menuTextureAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadMenuAudio() {

	}

	private void loadGameGraphics() {

	}

	private void loadGameFonts() {

	}

	private void loadGameAudio() {

	}

	public static void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, gameActivity,
				"splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public static void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

}
