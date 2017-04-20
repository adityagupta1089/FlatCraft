package object.monster;

import manager.ResourcesManager;

public class WalkingMonster extends Monster {
    public WalkingMonster(float pX, float pY) {
        super(pX, pY, ResourcesManager.enemy_walking, ResourcesManager.vertexBufferObjectManager);
        this.animate(new long[]{100, 100, 100, 100, 100}, new int[]{0, 1, 2, 1, 0}, true);
    }
}
