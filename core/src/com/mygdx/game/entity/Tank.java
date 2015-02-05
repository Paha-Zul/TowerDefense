package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
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
public class Tank extends Entity{
    private static Texture tankTexture = new Texture("Tank.png");

    private Entity target;
    private Grid grid;

    private float shootTimer = 3f;
    private float shootCounter = 0;
    private double rotToTarget = 0;
    private float speed = 100;

    private float range = 12;

    public Tank(Vector2 position, float rotation, Team team, World world, Vector2 health, Grid grid) {
        super(position, rotation, team, world, "tank", health, tankTexture);

        this.createBody();

        this.grid = grid;
        getTarget();
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

    public void getTarget(){
        this.target = this.grid.findClosestEntity(this.position.x, this.position.y, null, this.teamOwner.getRandomEnemyTeam().getId());
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //If we don't have a target
        if(this.target == null || this.target.isDestroyed()){
            this.getTarget(); //Get a target
            if(this.target == null){ //If we still didn't get one.
                this.setDestroyed(); //Destroy this entity.
                return;
            }
        }

        this.moveToTarget(delta);

        rotToTarget = Math.atan2(this.target.position.y - this.position.y, this.target.position.x - this.position.x);

        if (this.shootCounter >= this.shootTimer) {
            if(this.withinRangeOfTarget()) {
                this.shootCounter -= this.shootTimer;
                this.shootBullet();
            }
        } else {
            this.shootCounter+=delta;
        }

        this.position.set(this.body.getPosition());
        this.sprite.setPosition(this.position.x, this.position.y);
        this.sprite.setCenter(this.position.x, this.position.y);
        this.sprite.setRotation((float)rotToTarget*MathUtils.radDeg);
    }

    public void shootBullet(){
        new Bullet(this.position, (float)rotToTarget*MathUtils.radDeg, this.world, this.teamOwner, "bullet", new Vector2(10,10));
    }

    public void moveToTarget(float delta){
        if (!withinRangeOfTarget()){
            float x = (float)Math.cos(rotToTarget)*delta*speed;
            float y = (float)Math.sin(rotToTarget)*delta*speed;

            this.body.setLinearVelocity(x, y);
        }else{
            this.body.setLinearVelocity(0,0);
        }
    }

    public boolean withinRangeOfTarget(){
        return this.position.dst(this.target.position) <= this.range;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        super.render(delta, batch);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
