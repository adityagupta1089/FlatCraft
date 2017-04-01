package manager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;
import main.GameActivity;
import object.tile.TilesLoader;

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
	public static BoundCamera camera;
	public static Engine engine;
	public static VertexBufferObjectManager vertexBufferObjectManager;

	public static ITextureRegion grassRegion;

	// --------------------------------------------------------------//
	// Variables for Splash Scene
	// --------------------------------------------------------------//
	public static ITextureRegion splash_region;
	private static BitmapTextureAtlas splashTextureAtlas;

	// --------------------------------------------------------------//
	// Variables for Main Menu Scene
	// --------------------------------------------------------------//
	public static ITextureRegion menuBackgroundRegion;
	public static ITextureRegion helpRegion;

	public static Font caviarDreams;

	private static BuildableBitmapTextureAtlas menuTextureAtlas;

	// --------------------------------------------------------------//
	// Variables for Game Scene
	// --------------------------------------------------------------//
	// Tiles
	public static Map<String, ITextureRegion> tileRegions;
	public static Map<String, Boolean> tilePassability;

	private static BuildableBitmapTextureAtlas gameTilesTextureAtlas;
	private static BuildableBitmapTextureAtlas mOnScreenControlTextureAtlas;
	private static BuildableBitmapTextureAtlas inventoryAtlas;
	private static BuildableBitmapTextureAtlas gameTextureAtlas;

	// misc. tiles
	public static TextureRegion skyBoxBottomRegion;
	public static TextureRegion skyBoxSideHillsRegion;
	public static TextureRegion skyBoxTopRegion;
	public static TextureRegion sunRegion;

	// Controller
	public static ITextureRegion mOnScreenControlBaseTextureRegion;
	public static ITextureRegion mOnScreenControlKnobTextureRegion;

	// game scene buttons
	public static ITextureRegion pauseRegion;
	public static ITextureRegion menuRegion;

	public static ITiledTextureRegion placeTilesYesRegion;
	public static ITiledTextureRegion placeTilesNoRegion;

	public static ITiledTextureRegion soundRegion;

	// Player
	public static ITextureRegion playerRegion;

	// Inventory
	public static TextureRegion inventoryBaseRegion;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	public static void prepare(GameActivity pGameActivity, BoundCamera pCamera,
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
		menuTextureAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(), 2048,
				2048, TextureOptions.DEFAULT);
		menuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(menuTextureAtlas, gameActivity, "menu_background.png");
		try {
			menuTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadMenuAudio() {

	}

	private static void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture = new BitmapTextureAtlas(gameActivity.getTextureManager(), 1024,
				1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		caviarDreams = FontFactory.createFromAsset(gameActivity.getFontManager(), fontTexture,
				gameActivity.getAssets(), "CaviarDreams.ttf", 100, true, Color.BLACK);
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
		loadTileGraphics();
		loadBackgroundGraphics();
		loadPlayerGraphics();
		loadTiles();
		loadAnalogOnScreenController();
		loadInventoryGraphics();
	}

	private static void loadTileGraphics() {
		tileRegions = new HashMap<String, ITextureRegion>();
		gameTilesTextureAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(),
				1024, 1024, TextureOptions.DEFAULT);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/tiles/");
		try {
			for (String tileName : ResourcesManager.gameActivity.getAssets()
					.list("gfx/game/tiles")) {
				if (!tileName.contains(".png")) continue;
				ITextureRegion tempRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromAsset(gameTilesTextureAtlas, gameActivity, tileName);
				tileRegions.put(tileName.split("\\.")[0].toUpperCase(Locale.ENGLISH), tempRegion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			gameTilesTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			gameTilesTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadBackgroundGraphics() {
		gameTextureAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(), 2048,
				2048, TextureOptions.REPEATING_NEAREST);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/background/");
		skyBoxBottomRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(gameTextureAtlas, gameActivity, "skybox_bottom.png");
		skyBoxSideHillsRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(gameTextureAtlas, gameActivity, "skybox_sideHills.png");
		skyBoxTopRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
				gameActivity, "skybox_top.png");
		sunRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
				gameActivity, "sun.png");
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/characters/");
		playerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas,
				gameActivity, "player.png");
		try {
			gameTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadPlayerGraphics() {
		// TODO Auto-generated method stub

	}

	private static void loadAnalogOnScreenController() {
		mOnScreenControlTextureAtlas = new BuildableBitmapTextureAtlas(
				gameActivity.getTextureManager(), 512, 512, TextureOptions.DEFAULT);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/controller/");
		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mOnScreenControlTextureAtlas, gameActivity, "onscreen_control_base.png");
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mOnScreenControlTextureAtlas, gameActivity, "onscreen_control_knob.png");
		placeTilesYesRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mOnScreenControlTextureAtlas, gameActivity, "yes.png", 2, 1);
		placeTilesNoRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mOnScreenControlTextureAtlas, gameActivity, "no.png", 2, 1);
		pauseRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mOnScreenControlTextureAtlas, gameActivity, "pause.png");
		menuRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mOnScreenControlTextureAtlas, gameActivity, "menu.png");
		soundRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				mOnScreenControlTextureAtlas, gameActivity, "sound.png", 2, 1);
		try {
			mOnScreenControlTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			mOnScreenControlTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadInventoryGraphics() {
		inventoryAtlas = new BuildableBitmapTextureAtlas(gameActivity.getTextureManager(), 1024,
				1024, TextureOptions.DEFAULT);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/inventory/");
		inventoryBaseRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(inventoryAtlas,
				gameActivity, "base.png");
		try {
			inventoryAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			inventoryAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private static void loadGameFonts() {

	}

	private static void loadGameAudio() {

	}

	private static void loadTiles() {
		TilesLoader.loadTiles();
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
				TextureOptions.DEFAULT);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
				gameActivity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public static void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

}
