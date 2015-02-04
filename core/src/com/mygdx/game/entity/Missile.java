package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;
import com.mygdx.game.Grid;
import com.mygdx.game.Team;

/**
 * Created by Paha on 2/3/2015.
 */
public class Missile extends Entity{
    private static Texture missileTexture = new Texture("Missile.png");

    private Entity target;
    private Grid grid;
    private float speed = 100;

    public Missile(Vector2 position, float rotation, Vector2 health, Team team, World world, Grid grid) {
        super(position, rotation, team, "missile", health);

        this.sprite = new Sprite(missileTexture);
        this.sprite.setPosition(position.x, position.y);
        this.sprite.setCenter(position.x, position.y);
        this.sprite.setScale(0.30f/ Constants.SCALE);
        this.world = world;
        this.grid = grid;

        this.createBody();

        this.target = grid.findClosestEntity(position.x, position.y, null, this.teamOwner.getRandomEnemyTeam().getId());
        this.sprite.setColor(this.teamOwner.getColor());
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

        //If we have a target and it's not already destroyed, move towards it!
        if(this.target != null && !this.target.destroyed){
            double rot = Math.atan2(target.position.y - this.position.y , target.position.x - this.position.x);
            float x = (float)Math.cos(rot)*delta*speed;
            float y = (float)Math.sin(rot)*delta*speed;
            this.body.setLinearVelocity(x, y);
            this.sprite.setRotation((float) (rot* MathUtils.radDeg));

        //Otherwise, find a new one!
        }else{
            this.target = this.grid.findClosestEntity(this.position.x, this.position.y, null, this.teamOwner.getRandomEnemyTeam().getId());
            if(this.target == null) this.setDestroyed(); //If it's still null after checking, just destroy me!
        }

        this.position.set(this.body.getPosition());
        this.sprite.setPosition(this.position.x, this.position.y);
        this.sprite.setCenter(this.position.x, this.position.y);


    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        super.render(delta, batch);

        sprite.draw(batch);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
