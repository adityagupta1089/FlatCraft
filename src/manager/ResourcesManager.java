package manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
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

import android.graphics.Color;
import android.util.Log;
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

	public static ITextureRegion grass_region;

	// --------------------------------------------------------------//
	// Variables for Splash Scene
	// --------------------------------------------------------------//
	public static ITextureRegion splash_region;
	private static BitmapTextureAtlas splashTextureAtlas;

	// --------------------------------------------------------------//
	// Variables for Main Menu Scene
	// --------------------------------------------------------------//
	public static ITextureRegion menu_background_region;
	public static ITextureRegion help_region;

	public static Font caviarDreams;

	private static BuildableBitmapTextureAtlas menuTextureAtlas;

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

	// --------------------------------------------------------------//
	// Load Functions
	// --------------------------------------------------------------//
	public static void loadMenuResources() {
		loadMenuGraphics();
		loadMenuFonts();
		loadMenuAudio();
	}

	public static void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	// --------------------------------------------------------------//
	// Menu Scene
	// --------------------------------------------------------------//
	private static void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(), 2048, 2048,
				TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, gameActivity,
				"menu_background.png");
		//help_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, gameActivity,
		//		"help.png");
		try {
			menuTextureAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadMenuAudio() {

	}

	private static void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture = new BitmapTextureAtlas(gameActivity.getTextureManager(), 1024, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		caviarDreams = FontFactory.createFromAsset(gameActivity.getFontManager(), fontTexture, gameActivity.getAssets(),
				"CaviarDreams.ttf", 100, true, Color.BLACK);
		caviarDreams.load();
	}

	public static void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}

	public static void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	// --------------------------------------------------------------//
	// Game Scene
	// --------------------------------------------------------------//
	private static void loadGameGraphics() {
		try {
			for (String tileName : ResourcesManager.gameActivity.getAssets()
																.list("gfx/game/tiles")) {
				Log.d("Tile", tileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadGameFonts() {

	}

	private static void loadGameAudio() {

	}

	public static void unloadGameTextures() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------------------------------//
	// Load Splash
	// --------------------------------------------------------------//
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
