package world;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import java.util.List;

import hud.InventoryItem;

public class MultiPlayerWorld extends World {

    public MultiPlayerWorld(BoundCamera camera) {
        super(camera);
    }

    @Override
    public void createPlayer(Camera camera) {

    }

    @Override
    public Vector2 getGravity() {
        return null;
    }

    @Override
    public void createBackground() {

    }

    @Override
    public void createForeground() {

    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }

    @Override
    public void onPopulateQuickAccess(List<InventoryItem> quickAccess) {

    }
}
