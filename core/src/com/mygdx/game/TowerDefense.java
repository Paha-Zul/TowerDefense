package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.screens.MainMenu;

public class TowerDefense extends Game {
    public SpriteBatch batch;
    private Texture img;
    public OrthographicCamera camera;
    public OrthographicCamera UICamera;
    public World world;
    public Box2DDebugRenderer box2DDebugRenderer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
        camera = new OrthographicCamera(Gdx.graphics.getWidth()/Constants.SCALE, Gdx.graphics.getHeight()/Constants.SCALE);
        UICamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        world = new World(new Vector2(0,0), true);
        box2DDebugRenderer = new Box2DDebugRenderer();

        this.setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
        super.render();
	}
}
