package com.mygdx.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Team;

/**
 * Created by Paha on 2/3/2015.
 */
public class Tank extends Entity{
    public Tank(Vector2 position, float rotation, Vector2 health, Team team) {
        super(position, rotation, team, "tank", health);
    }
}
