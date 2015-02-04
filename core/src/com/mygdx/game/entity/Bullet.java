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
import com.mygdx.game.Team;

/**
 * Created by Paha on 2/4/2015.
 */
public class Bullet extends Entity{
    private static Texture bulletTexture = new Texture("Bullet.png");

    private float speed = 1000;
    private float life = 5f;
    private float lifeCounter = 0;

    public Bullet(Vector2 position, float rotation, World world, Team teamOwner, String name, Vector2 health) {
        super(position, rotation, teamOwner, name, health);

        this.world = world;
        this.sprite = new Sprite(bulletTexture);
        this.sprite.setPosition(position.x, position.y);
        this.sprite.setCenter(position.x, position.y);
        this.sprite.setScale(0.4f/ Constants.SCALE);

        this.createBody();
    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.position.x, this.position.y);
        this.body = world.createBody(bodyDef);
        this.body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.isSensor = true;

        this.fixture = this.body.createFixture(fixtureDef);

        circle.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        float rot = this.rotation*MathUtils.degRad;
        this.sprite.setPosition(this.body.getPosition().x, this.body.getPosition().y);
        float x = (float)Math.cos(rot)*delta*speed;
        float y = (float)Math.sin(rot)*delta*speed;
        this.body.setLinearVelocity(x, y);
        this.sprite.setRotation((rot* MathUtils.radDeg));
        this.sprite.setCenter(this.body.getPosition().x, this.body.getPosition().y);

        this.position.set(this.body.getPosition().x, this.body.getPosition().y);

        //Destroy after some time.
        this.lifeCounter+=delta;
        if(this.lifeCounter >= this.life)
            this.setDestroyed();

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
