package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.*;

import java.util.LinkedList;

/**
 * Created by Paha on 2/3/2015.
 */
public class Tower extends Entity{
    private static Texture towerTexture = new Texture("Tower.png");
    private Fixture attackSensor;
    private Grid grid;
    private Grid.Node currNode;
    private Entity target;

    private LinkedList<Entity> targetList = new LinkedList<Entity>();

    private float shootTimer = 1f;
    private float shootTimerCounter = 0f;

    Functional.Perform<Grid.Node[][]> addController = (g) -> {
        int radius = 3;
        int startX = (int)(position.x/grid.getSquareSize()) - radius;
        int endX = (int)(position.x/grid.getSquareSize()) + radius;
        int startY = (int)(position.y/grid.getSquareSize()) - radius;
        int endY = (int)(position.y/grid.getSquareSize()) + radius;

        //Loop over the area
        for(int x=startX;x<=endX;x++){
            for(int y=startY;y<=endY;y++){
                Grid.Node node = grid.getNode(x, y);
                if(node != null){ //If not null...
                    node.setController(this.getTeamOwner()); //Set it as ours
                }
            }
        }
    };

    Functional.Perform<Grid.Node[][]> removeController = (g) -> {
        int radius = 3;
        int startX = (int)(position.x/grid.getSquareSize()) - radius;
        int endX = (int)(position.x/grid.getSquareSize()) + radius;
        int startY = (int)(position.y/grid.getSquareSize()) - radius;
        int endY = (int)(position.y/grid.getSquareSize()) + radius;

        //Loop over the area
        for(int x=startX;x<=endX;x++){
            for(int y=startY;y<=endY;y++){
                Grid.Node node = grid.getNode(x, y);
                if(node != null){ //If not null...
                    node.clearController(this.getTeamOwner());
                }
            }
        }
    };

    public Tower(Vector2 position, float rotation, Vector2 health, Team team, World world, Grid grid) {
        super(position, rotation, team, world, "tower", health, towerTexture);

        this.grid = grid;
        this.currNode = grid.getNode(this.position.x, this.position.y);
        this.currNode.setBuilding(this);

        this.createBody();

        grid.perform(addController);
    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.position.x, this.position.y);
        this.body = world.createBody(bodyDef);
        this.body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.isSensor = false;

        CircleShape circle2 = new CircleShape();
        circle2.setRadius(10);

        FixtureDef attackSensor = new FixtureDef();
        attackSensor.shape = circle2;
        attackSensor.isSensor = true;

        this.fixture = this.body.createFixture(fixtureDef);
        this.attackSensor = this.body.createFixture(attackSensor);

        circle.dispose();
        circle2.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);


        if(this.target != null && !this.target.isDestroyed()){
            boolean dst = this.position.dst(this.target.position) <= this.attackSensor.getShape().getRadius() + this.target.fixture.getShape().getRadius();
            if(dst) {
                this.shootTimerCounter += delta;
                if (this.shootTimerCounter >= shootTimer) {
                    this.shootTimerCounter -= shootTimer;
                    float rot = (float) Math.atan2(target.position.y - this.position.y, target.position.x - this.position.x);
                    Bullet bullet = new Bullet(this.position, rot * MathUtils.radDeg, this.world, this.teamOwner, "bullet", new Vector2(10, 10));
                }
            }else
                this.target = null;
        }else{
            this.target = getNextTarget();
        }

    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        super.render(delta, batch);
    }

    public Entity getNextTarget(){
        if(this.targetList.size() > 0){
            Entity target = this.targetList.pollFirst();
            if(target == null || target.isDestroyed() || this.position.dst(target.position) > this.attackSensor.getShape().getRadius() + target.fixture.getShape().getRadius())
                return null;

            return target;
        }

        return null;
    }

    public void addTarget(Entity target){
        this.targetList.add(target);
    }

    @Override
    public void destroy() {
        grid.perform(removeController);
        this.currNode.clearBuilding();
        this.grid = null;
        this.currNode = null;

        super.destroy();
    }
}
