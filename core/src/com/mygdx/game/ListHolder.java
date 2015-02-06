package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entity.Entity;

import java.util.ArrayList;

/**
 * Created by Paha on 2/4/2015.
 */
public class ListHolder {
    private static ArrayList<Entity> quickList = new ArrayList<Entity>();

    public static void addEntity(Entity entity){
        quickList.add(entity);
    }

    public static void update(float delta, SpriteBatch batch, TowerDefense game){

        for(int i=0;i<quickList.size();i++) {
            Entity ent = quickList.get(i);
            if(ent.isDestroyed()){
                ent.destroy();
                quickList.remove(i);
                i--;
                continue;
            }

            if(!ent.isInitiated())
                ent.init();

            ent.update(delta);
            ent.render(delta, game.batch);
        }
    }

}
