package object.monster;

import manager.ResourcesManager;

public class FlyingMonster extends Monster {
    public FlyingMonster(float pX, float pY) {
        super(pX, pY, ResourcesManager.enemy_flying, ResourcesManager.vertexBufferObjectManager);
        this.animate(new long[]{50, 50, 50, 50, 50}, new int[]{0, 1, 2, 1, 0}, true);
    }
}
