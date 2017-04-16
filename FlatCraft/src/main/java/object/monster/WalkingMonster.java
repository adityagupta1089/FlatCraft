package object.monster;

import manager.ResourcesManager;

public class WalkingMonster extends Monster {
    public WalkingMonster(float pX, float pY) {
        super(pX, pY, ResourcesManager.enemy_walking, ResourcesManager.vertexBufferObjectManager);
    }
}
