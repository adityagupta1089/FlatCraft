package hud;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

import manager.ResourcesManager;

public class FlatCraftHUD extends HUD {

    private static final int ROWS = 6;
    private static final int COLS = 6;
    private static int QUICK_COUNT = 8;
    public InventoryItem currItem;
    public boolean inventorySceneShown = false;
    private List<InventoryItem> quickAccess;
    private List<InventoryItem> quickAccess2;
    private List<IEntity> entities;
    private Sprite inventoryScene;
    private int inventoryScenes;
    private int inventorySceneCount = 0;

    public FlatCraftHUD() {
        super();
        entities = new ArrayList<>();
        quickAccess = new ArrayList<>();
        quickAccess2 = new ArrayList<>();
        ResourcesManager.world.onPopulateQuickAccess(quickAccess);
        inventoryScenes = ((int) Math.ceil((quickAccess.size() - QUICK_COUNT) / (float) (ROWS * COLS))) - 1;
    }

    public void initTiles() {
        Sprite base = new Sprite(ResourcesManager.WIDTH / 2, 180, ResourcesManager.WIDTH / 2, 131, ResourcesManager
                .inventoryBaseRegion, ResourcesManager.vertexBufferObjectManager);
        attachChild(base);

        int cnt = 0;
        final float X0 = base.getX() - base.getWidth() / 2;
        final float Xn = base.getX() + base.getWidth() / 2;
        final float deltaX = (Xn - X0) / (2 * QUICK_COUNT);
        final float pY = base.getY();

        ResourcesManager.selector = new Sprite(X0 + deltaX, pY, 2 * deltaX, base.getHeight(),
                ResourcesManager.selectorRegion, ResourcesManager.vertexBufferObjectManager);

        for (InventoryItem ie : quickAccess) {
            if (cnt == 0) currItem = ie;
            if (cnt++ < QUICK_COUNT) {
                ie.setListener(this);
                this.attachChild(ie);
                ie.setOnQuickAccess(true);
                ie.setPosition(X0 + (2 * cnt - 1) * deltaX, pY);
                this.registerTouchArea(ie);
            } else {
                quickAccess2.add(ie);
            }
        }
        quickAccess.removeAll(quickAccess2);
        this.attachChild(ResourcesManager.selector);
    }

    @Override
    public void attachChild(IEntity pEntity) throws IllegalStateException {
        super.attachChild(pEntity);
        entities.add(pEntity);
    }

    private void cleanEntities() {
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

    @Override
    public void dispose() {
        ResourcesManager.engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                cleanEntities();
                clearTouchAreas();
                clearUpdateHandlers();
                System.gc();
            }
        });
    }

    public void inventorySceneToggle() {
        if (!inventorySceneShown || inventorySceneCount++ < inventoryScenes) {
            if (inventoryScene == null) {
                inventoryScene = new Sprite(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2 + 150,
                        ResourcesManager
                                .metalBaseRegion, ResourcesManager.vertexBufferObjectManager);
                attachChild(inventoryScene);
                inventorySceneShown = true;
            } else {
                inventoryScene.detachChildren();
                if (inventorySceneCount == 0) {
                    inventorySceneShown = true;
                    inventoryScene.setVisible(true);
                }
            }
            if (inventorySceneCount > 0) {
                for (int i = (ROWS * COLS) * (inventorySceneCount - 1); i < quickAccess2.size(); i++)
                    this.unregisterTouchArea(quickAccess2.get(i));
            }
            final float width = quickAccess.get(0).getWidth();
            final float height = quickAccess.get(0).getHeight();
            final float PADDING_X = (inventoryScene.getWidth() - COLS * width) / (COLS + 1f);
            final float PADDING_Y = (inventoryScene.getHeight() - ROWS * height) / (ROWS + 1f);
            final float X0 = inventoryScene.getX() - inventoryScene.getWidth() / 2;
            final float Y0 = inventoryScene.getY() + inventoryScene.getHeight() / 2;
            for (int i = 0, c = (ROWS * COLS) * inventorySceneCount; i < ROWS && c < quickAccess2.size(); i++) {
                for (int j = 0; j < COLS && c < quickAccess2.size(); j++, c++) {
                    InventoryItem ie = quickAccess2.get(c);
                    ie.setListener(this);
                    final float[] parentCoordinates = inventoryScene.convertSceneCoordinatesToLocalCoordinates(X0 + (j +
                            1) * PADDING_X + (j + 0.5f) * width, Y0 - (i + 1) * PADDING_Y - (i + 0.5f) * height);
                    ie.setPosition(parentCoordinates[0], parentCoordinates[1]);
                    inventoryScene.attachChild(ie);
                    this.registerTouchArea(ie);
                }
            }
        } else {
            for (int i = (ROWS * COLS) * (inventorySceneCount - 1); i < quickAccess2.size(); i++)
                this.unregisterTouchArea(quickAccess2.get(i));
            inventorySceneCount = 0;
            inventoryScene.setVisible(false);
            inventoryScene.detachChildren();
            inventorySceneShown = false;
        }
    }

    public void resetInventory() {
        if (inventorySceneShown) {
            inventorySceneCount = 0;
            inventoryScene.setVisible(false);
            inventoryScene.detachChildren();
            inventorySceneShown = false;
            ResourcesManager.selector.setVisible(false);
        }
    }

    public void swap(InventoryItem qaItem, InventoryItem qa2Item) {
        int i1 = quickAccess.indexOf(qaItem);
        int i2 = quickAccess2.indexOf(qa2Item);
        quickAccess2.set(i2, qaItem);
        quickAccess.set(i1, qa2Item);
    }

    public Sprite getInventoryScene() {
        return inventoryScene;
    }
}
