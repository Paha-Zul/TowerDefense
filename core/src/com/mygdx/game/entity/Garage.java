package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Grid;
import com.mygdx.game.Team;

/**
 * Created by Paha on 2/3/2015.
 */
public class Garage extends Entity{
    private static Texture garageTexture = new Texture("Garage.png");

    private float spawnTimer = 5f;
    private float spawnCounter = 0f;
    private Grid grid;

    public Garage(Vector2 position, Team team, World world, float rotation, Vector2 health, Grid grid) {
        super(position, rotation, team, world, "garage", health, garageTexture);

        this.grid = grid;
        this.createBody();
    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.position.x, this.position.y);
        this.body = world.createBody(bodyDef);
        this.body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;

        this.fixture = this.body.createFixture(fixtureDef);
        circle.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        spawnCounter+=delta;
        if(spawnCounter >= spawnTimer){
            spawnCounter -= spawnTimer;
            new Tank(this.position, 0, this.teamOwner, this.world, new Vector2(100,100), this.grid);
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
