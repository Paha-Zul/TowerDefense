package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
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
    private static Texture tileTexture = new Texture(Gdx.files.internal("Tile.png"), true);
    private static Texture resourceTexture = new Texture(Gdx.files.internal("Resource.png"), true);
    private static Texture background = new Texture("background.png");

    private static Color screenColor = new Color(237f/255f, 221f/255f, 221f/255f, 1);

    private Rectangle towerRect = new Rectangle();
    private Rectangle siloRect = new Rectangle();
    private Rectangle mineRect = new Rectangle();
    private Rectangle garageRect = new Rectangle();
    private Rectangle moneyRect = new Rectangle();

    private Rectangle player1Rect = new Rectangle();
    private Rectangle player2Rect = new Rectangle();

    public volatile int stepped = 0;
    private Thread box2D;

    public GameScreen(TowerDefense game){
        this.game = game;
    }

    static{
        tileTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
        resourceTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
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

        box2D = new Thread(new Box2DThread(this.game.world, this));
        box2D.start();
    }

    private Team startTeam(String name, Vector2 start){
        Team team = new Team(name);
        TeamManager.addTeam(team);

        Grid.Node node = grid.getNode(start.x, start.y);
        Vector2 pos = new Vector2(node.getX()*grid.getSquareSize() + grid.getSquareSize()/2, node.getX()*grid.getSquareSize() + grid.getSquareSize()/2);

        Tower tower = new Tower(pos, 0, new Vector2(100,100), team, this.game.world, this.grid);

        return team;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(screenColor.r, screenColor.g, screenColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        this.drawTiles();

        ListHolder.update(delta, this.game.batch, this.game);

        this.towerSelectGUI(this.game.batch);

        this.stepped++;
        this.game.world.step(delta, 8, 3);

        game.batch.end();

        //this.game.box2DDebugRenderer.render(this.game.world, this.game.camera.combined);

        //this.renderGrid();


        this.moveCamera(delta);
        this.game.camera.update();
    }

    private void towerSelectGUI(SpriteBatch batch){
        Matrix4 proj = batch.getProjectionMatrix();
        batch.setProjectionMatrix(this.game.UICamera.combined);

        if(GUI.Button(towerRect, "Tower", batch)){
            this.spawnType = 1;
        }

        if(GUI.Button(siloRect, "Silo", batch)){
            this.spawnType = 2;
        }

        if(GUI.Button(mineRect, "Mine", batch)){
            this.spawnType = 3;
        }

        if(GUI.Button(garageRect, "Garage", batch)){
            this.spawnType = 4;
        }

        batch.setColor(TeamManager.getTeam(0).getColor());
        if(GUI.Button(player1Rect, "Player1", batch)){
            this.currTeam = 0;
        }

        batch.setColor(TeamManager.getTeam(1).getColor());
        if(GUI.Button(player2Rect, "Player2", batch)){
            this.currTeam = 1;
        }

        batch.setColor(Color.WHITE);

        GUI.Texture(moneyRect, background, batch);
        GUI.Label("$"+TeamManager.getTeam(this.currTeam).getMoney(), batch, moneyRect, true);

        batch.setProjectionMatrix(proj);
    }

    private void drawTiles(){
        Color color;
        float squareSize = this.grid.getSquareSize();
        float offset = squareSize*0.05f;
        float doubleOffset = offset*2;

        for(int x=0;x<this.grid.getSizeX();x++){
            for(int y=0;y<this.grid.getSizeY();y++){
                Grid.Node node = this.grid.getNode(x, y);
                if(node.hasResource()){
                    this.game.batch.draw(resourceTexture, x, y, squareSize, squareSize);
                }

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
        this.game.camera.setToOrtho(false, width/Constants.SCALE, height/Constants.SCALE);
        this.game.UICamera.setToOrtho(false, width, height);

        float rectWidth = 100f;
        float startX = (width - (rectWidth*4f))/2;
        System.out.println("StartX: "+startX);
        float rectHeight = (float)height*0.1f;

        towerRect.set(startX, 0, 100, 100);
        siloRect.set(startX + rectWidth*1, 0, 100, 100);
        mineRect.set(startX + rectWidth*2, 0, 100, 100);
        garageRect.set(startX + rectWidth*3, 0, 100, 100);


        moneyRect.set(width*0.9f, 0, width*0.1f, width*0.1f);

        player1Rect.set(width*0.9f, height*0.75f, width*0.1f, width*0.1f);
        player2Rect.set(width*0.9f, height*0.6f, width*0.1f, width*0.1f);
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
       if(keycode == Input.Keys.NUMPAD_1)
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
        Team team  = TeamManager.getTeam(this.currTeam);

//        if(!team.hasEnoughMoney(100))
//            return false;

        Grid.Node node = this.grid.getNode(worldCoords.x, worldCoords.y);
        if(node == null || node.getController() == null || node.getController().getId() != team.getId() || node.getBuilding() != null)
            return false;

        if(this.spawnType == 1) {
            Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
            new Tower(pos, 0, new Vector2(100, 100), team, this.game.world, this.grid);
        }else if(this.spawnType == 2){
            Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
            new Silo(pos, 0, team, "silo", new Vector2(100, 100), this.grid, this.game.world);
        }else if(this.spawnType == 3){
            Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
            new Mine(pos, 0, team, this.game.world, new Vector2(100,100));
        }else if(this.spawnType == 4){
            Vector2 pos = new Vector2(node.getX() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f, node.getY() * this.grid.getSquareSize() + this.grid.getSquareSize() * 0.5f);
            new Garage(pos, team, this.game.world, 0, new Vector2(100,100), this.grid);
        }else if(this.spawnType == 5){
            //Entity missile = new Missile(new Vector2(worldCoords.x, worldCoords.y), 0, new Vector2(100, 100), TeamManager.getTeam(this.currTeam), this.game.world, this.grid);
            Entity tank = new Tank(new Vector2(worldCoords.x, worldCoords.y), 0, team, this.game.world,  new Vector2(100, 100), this.grid);
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
