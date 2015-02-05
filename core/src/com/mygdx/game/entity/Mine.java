package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Team;

/**
 * Created by Paha on 2/3/2015.
 */
public class Mine extends Entity{
    private static Texture mineTexture = new Texture("Mine.png");

    private int incomeAmount = 1;
    private float incomeTimer = 0.5f;
    private float incomeCounter = 0f;

    public Mine(Vector2 position, float rotation, Team team, World world, Vector2 health) {
        super(position, rotation, team, world, "mine", health, mineTexture);

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
    public void init() {
        super.init();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        this.incomeCounter+=delta;
        if(this.incomeCounter >= this.incomeTimer){
            this.incomeCounter -= this.incomeTimer;
            this.teamOwner.addMoney(this.incomeAmount);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
