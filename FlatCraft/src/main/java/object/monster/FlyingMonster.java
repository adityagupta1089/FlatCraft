package object.monster;

import manager.ResourcesManager;

public class FlyingMonster extends Monster {
    public FlyingMonster(float pX, float pY) {
        super(pX, pY, ResourcesManager.enemy_flying, ResourcesManager.vertexBufferObjectManager);
    }
}
