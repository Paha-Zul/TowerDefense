package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.GameScreen;

/**
 * Created by Paha on 2/5/2015.
 */
public class Box2DThread implements Runnable{
    private final World world;
    private float stepTime = 1f/60f;
    private GameScreen game;

    public Box2DThread(final World world, GameScreen game){
        this.world = world;
        this.game = game;
    }

    @Override
    public void run() {
        while(true) {
            int steps = game.stepped;
            synchronized (this.world) {
                for (int i = 0; i < steps; i++) {
                    world.step(stepTime, 8, 3);
                    game.stepped--;
                }
            }

        }
    }
}
