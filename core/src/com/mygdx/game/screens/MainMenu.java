package com.mygdx.game.screens;

import com.badlogic.gdx.Screen;
import com.mygdx.game.TowerDefense;

/**
 * Created by Paha on 2/3/2015.
 */
public class MainMenu implements Screen {
    private TowerDefense game;

    public MainMenu(TowerDefense game){
        this.game = game;
    }

    @Override
    public void show() {

        this.game.setScreen(new GameScreen(this.game));
    }

    @Override
    public void render(float delta) {
        System.out.println("Rendering main");
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
