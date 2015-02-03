package com.mygdx.game.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Paha on 2/3/2015.
 */
public class Entity {
    protected Vector2 position;
    protected float rotation;

    public Entity(Vector2 position, float rotation){
        this.position = new Vector2(position);
        this.rotation = rotation;
    }

}
