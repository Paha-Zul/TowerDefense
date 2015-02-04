package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Paha on 2/3/2015.
 */
public class TeamManager {
    private static HashMap<Integer, Team> teamList = new HashMap<Integer, Team>();

    public TeamManager(){

    }

    public static void addTeam(Team team){
        TeamManager.teamList.put(team.getId(), team);
    }

    public static void removeTeam(Team team){
        TeamManager.removeTeam(team.getId());
    }

    public static void removeTeam(int id){
        TeamManager.teamList.remove(id);
    }

    public static Team getTeam(int id){
        return TeamManager.teamList.get(id);
    }

    public static ArrayList<Team> getTeamList(){
        return new ArrayList<Team>(teamList.values());
    }

    public static Team getRandomTeam(){
        return (new ArrayList<Team>(TeamManager.teamList.values())).get(MathUtils.random(teamList.size()-1));
    }

    public static void addEntityToTeam(String name, Entity entity, Team team){
        getTeam(team.getId()).addEntity(name, entity);
    }
}
