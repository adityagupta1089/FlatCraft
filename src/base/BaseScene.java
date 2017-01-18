package base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import main.GameActivity;
import manager.ResourcesManager;

public abstract class BaseScene extends Scene {
	//--------------------------------------------------------------//
	// Variables
	// --------------------------------------------------------------//
	protected VertexBufferObjectManager vertexBufferObjectManager;
	public Camera camera;
	GameActivity gameActivity;
	protected Engine engine;

	//--------------------------------------------------------------//
	// Constructor
	// --------------------------------------------------------------//

	public BaseScene() {
		this.vertexBufferObjectManager = ResourcesManager.vertexBufferObjectManager;
		this.camera = ResourcesManager.camera;
		this.gameActivity = ResourcesManager.gameActivity;
		this.engine = ResourcesManager.engine;
		createScene();
	}

	//--------------------------------------------------------------//
	// Abstraction
	// --------------------------------------------------------------//

	public abstract void createScene();

	public abstract void onBackKeyPressed();

	public abstract void disposeScene();

}
