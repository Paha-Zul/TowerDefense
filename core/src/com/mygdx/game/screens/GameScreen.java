package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.*;
import com.mygdx.game.entity.*;

/**
 * Created by Paha on 2/3/2015.
 */
public class GameScreen implements Screen, InputProcessor{

    private TowerDefense game;
    private Grid grid;
    private int spawnType = 1;
    private ShapeRenderer renderer;
    private int currTeam = 0;
    private static Texture tileTexture = new Texture("Tile.png");

    private static Color screenColor = new Color(237f/255f, 221f/255f, 221f/255f, 1);

    public GameScreen(TowerDefense game){
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        this.game.world.setContactListener(new Contact());
        this.renderer = new ShapeRenderer();
        this.grid = new Grid(2000, 2000, 25);

        Team team1 = startTeam("ProSquad", new Vector2(200/Constants.SCALE,200/Constants.SCALE));
        Team team2 = startTeam("ProTip", new Vector2(400/Constants.SCALE,400/Constants.SCALE));

        this.game.camera.position.set(3000f, 3000f, 0.3f);
        this.game.camera.update();

        team1.getEnemyTeams();
        team2.getEnemyTeams();
    }

    private Team startTeam(String name, Vector2 start){
        Team team = new Team(name);
        TeamManager.addTeam(team);

        Grid.Node node = grid.getNode(start.x, start.y);
        Vector2 pos = new Vector2(node.getX()*grid.getSquareSize() + grid.getSquareSize()/2, node.getX()*grid.getSquareSize() + grid.getSquareSize()/2);

        Tower tower = new Tower(pos, 0, new Vector2(100,100), team, this.game.world, this.grid);
        ListHolder.addEntity(tower);

        return team;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(screenColor.r, screenColor.g, screenColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.game.world.step(delta, 8, 3);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        ListHolder.update(delta, this.game.batch, this.game);

        this.drawTiles();
        game.batch.end();

        //this.game.box2DDebugRenderer.render(this.game.world, this.game.camera.combined);

        this.renderGrid();

        this.moveCamera(delta);
        this.game.camera.update();
    }

    private void drawTiles(){
        Color color;
        float squareSize = this.grid.getSquareSize();
        float offset = squareSize*0.05f;
        float doubleOffset = offset*2;

        for(int x=0;x<this.grid.getSizeX();x++){
            for(int y=0;y<this.grid.getSizeY();y++){
                Grid.Node node = this.grid.getNode(x, y);
                if(node.getController() != null){
                    color = new Color(node.getController().getColor());
                    color.a = 0.5f;
                    this.game.batch.setColor(color);
                    this.game.batch.draw(tileTexture, x*squareSize + offset, y*squareSize + offset, squareSize - doubleOffset, squareSize - doubleOffset);
                }else
                    this.game.batch.setColor(Color.WHITE);
            }
        }
    }

    private void renderGrid(){
        this.renderer.setProjectionMatrix(this.game.camera.combined);
        this.renderer.begin(ShapeRenderer.ShapeType.Line);
        float squareSize = this.grid.getSquareSize();
        for(int x=0;x<this.grid.getSizeX();x++){
            for(int y=0;y<this.grid.getSizeY();y++){
                Grid.Node node = this.grid.getNode(x, y);
                renderer.rect(x*squareSize, y*squareSize, squareSize, squareSize);
            }
        }
        this.renderer.end();
    }

    private void moveCamera(float delta){
        float move = delta*5;

        if(Gdx.input.isKeyPressed(Input.Keys.W))
            this.game.camera.translate(0,move);
        if(Gdx.input.isKeyPressed(Input.Keys.S))
            this.game.camera.translate(0,-move);
        if(Gdx.input.isKeyPressed(Input.Keys.A))
            this.game.camera.translate(-move, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            this.game.camera.translate(move, 0);
    }

    @Override
    public void resize(int width, int height) {
        this.game.camera.setToOrtho(false, Gdx.graphics.getWidth()/Constants.SCALE, Gdx.graphics.getHeight()/Constants.SCALE);
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.NUM_1)
            this.spawnType = 1;
        else if(keycode == Input.Keys.NUM_2)
            this.spawnType = 2;
        else if(keycode == Input.Keys.NUM_3)
            this.spawnType = 3;
        else if(keycode == Input.Keys.NUMPAD_1)
            this.currTeam = 0;
        else if(keycode == Input.Keys.NUMPAD_2)
            this.currTeam = 1;


        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoords = this.game.camera.unproject(new Vector3(screenX, screenY, 0));

        if(this.spawnType == 1) {
            Grid.Node node = this.grid.getNode(worldCoords.x, worldCoords.y);
            if (node != null && node.getBuilding() == null) {
                if(node.getController() != null && node.getController().getId() == this.currTeam) {
                    Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
                    Entity tower = new Tower(pos, 0, new Vector2(100, 100), TeamManager.getTeam(this.currTeam), this.game.world, this.grid);
                    ListHolder.addEntity(tower);
                }
            }
        }else if(this.spawnType == 2){
            Grid.Node node = this.grid.getNode(worldCoords.x, worldCoords.y);
            if (node != null && node.getBuilding() == null && (node.getController() != null && node.getController().getId() == this.currTeam)) {
                Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
                Entity bullet = new Silo(pos, 0, TeamManager.getTeam(this.currTeam), "silo", new Vector2(100, 100), this.grid, this.game.world);
                ListHolder.addEntity(bullet);
            }
        }else if(this.spawnType == 3){
            Grid.Node node = this.grid.getNode(worldCoords.x, worldCoords.y);
            if(node != null) {
                Entity missile = new Missile(new Vector2(worldCoords.x, worldCoords.y), 0, new Vector2(100, 100), TeamManager.getTeam(this.currTeam), this.game.world, this.grid);
                ListHolder.addEntity(missile);
            }
        }


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        this.game.camera.zoom += amount*0.1f;
        return false;
    }
}
