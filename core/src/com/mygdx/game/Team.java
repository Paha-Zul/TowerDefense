package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.entity.Entity;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Paha on 2/3/2015.
 */
public class Team {
    private HashMap<String, ArrayList<Entity>> buildingMap = new HashMap<String, ArrayList<Entity>>();
    private ArrayList<Team> enemyTeams = new ArrayList<Team>();
    private static int idCounter = 0;

    private String name;
    private Color color;
    private int id;

    public Team(String name){
        this.name = name;
        this.color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
        this.id = idCounter++;

    }

    public void update(float delta){

    }

    public void render(float delta, SpriteBatch batch){
        for(ArrayList<Entity> list : buildingMap.values())
            for(Entity ent : list){
                ent.update(delta);
                ent.render(delta, batch);
            }
    }

    public void addEntity(@NotNull String name, @NotNull Entity entity){
        if(!buildingMap.containsKey(name)) buildingMap.put(name, new ArrayList<Entity>());

        buildingMap.get(name).add(entity);
    }

    public void removeEntity(String name, Entity entity){
        ArrayList<Entity> list = buildingMap.get(name);
        if(list == null) return;
        list.remove(entity);
    }

    public void getEnemyTeams(){
        ArrayList<Team> teams = TeamManager.getTeamList();
        for(Team team : teams)
            if(team.getId() != this.getId())
                enemyTeams.add(team);
    }

    public Team getRandomEnemyTeam(){
        return enemyTeams.get(MathUtils.random(enemyTeams.size()-1));
    }

    public int getId(){
        return this.id;
    }

    public Color getColor(){
        return this.color;
    }

    @Override
    public String toString() {
        return "Team: "+this.name+" ID: "+this.getId();
    }
}
