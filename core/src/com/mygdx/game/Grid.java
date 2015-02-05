package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.entity.Entity;

/**
 * Created by Paha on 2/3/2015.
 */
public class Grid {
    private Node[][] grid;
    private int width,height,squareSize;

    public Grid(int width, int height, int squareSize){
        this.width = width;
        this.height = height;
        this.squareSize = squareSize;

        grid = new Node[width/squareSize + 1][height/squareSize + 1];

        for(int x=0;x<grid.length;x++){
            for(int y=0;y<grid.length;y++){
                grid[x][y] = new Node(x, y);

                double rand = MathUtils.random();
                if(rand < 0.01){
                    grid[x][y].hasResource = true;
                }
            }
        }
    }

    public Node getNode(float x, float y){
        int xIndex = (int)(x/getSquareSize());
        int yIndex = (int)(y/getSquareSize());
        return this.getNode(xIndex, yIndex);
    }

    public Node getNode(int xIndex, int yIndex){
        if(xIndex < 0 || xIndex >= grid.length || yIndex < 0 || yIndex >= grid[0].length)
            return null;

        return grid[xIndex][yIndex];
    }

    public Entity findClosestEntity(float x, float y, String name, int teamID){
        boolean finished = false;
        int xIndex = (int)(x/getSquareSize());
        int yIndex = (int)(y/getSquareSize());
        int radius = 0;

        while(!finished) {
            finished = true;
            int startX = xIndex - radius;
            int endX = xIndex + radius;
            int startY = yIndex - radius;
            int endY = yIndex + radius;

            for (int xx = startX; xx <= endX; xx++) {
                for (int yy = startY; yy <= endY; yy++) {
                    if (!(xx == startX || xx == endX || yy == startY || yy == endY))
                        continue;

                    Node node = this.getNode(xx, yy);
                    if (node != null) {
                        finished = false;
//                        System.out.println();
//                        System.out.println("null? node: "+(node==null));
//                        System.out.println("building: "+(node.building==null));
//                        if(node.building!=null) {
//                            System.out.println("building name: " + node.building.getName());
//                            System.out.println("teamOwner: " + (node.building.getTeamOwner() == null));
//                        }
//                        System.out.println("name: "+(name == null));
                        if (node.building != null && node.building.getTeamOwner().getId() == teamID && (name == null || node.building.getName().equals(name)))
                            return node.building;
                    }
                }
            }

            radius++;
        }

        return null;
    }

    public void perform(Functional.Perform<Node[][]> perform){
        perform.perform(this.grid);
    }

    public int getSizeX(){
        return this.grid.length;
    }

    public int getSizeY(){
        return this.grid[0].length;
    }

    public float getSquareSize(){
        return this.squareSize/Constants.SCALE;
    }

    public class Node{
        private Team controller;
        private int controlCounter = 0;
        private int x,y;
        private boolean hasResource;
        private Entity building;

        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX(){
            return this.x;
        }

        public int getY(){
            return this.y;
        }

        public Entity getBuilding(){
            return this.building;
        }

        public boolean hasResource(){
            return this.hasResource;
        }

        public Team getController(){
            return this.controller;
        }

        public void setBuilding(Entity entity){
            this.building = entity;
        }

        public void setController(Team team){
            if(this.controller == null){
                this.controller = team;
                this.controlCounter++;
            }else if(this.controller.getId() == team.getId()) {
                this.controlCounter++;
            }
        }

        public void clearController(Team team){
            if(this.controller == null)
                return;

            if(this.controller.getId() == team.getId()) {
                this.controlCounter--;
                if(this.controlCounter <= 0)
                    this.controller = null;
            }
        }

        public void clearBuilding(){
            this.building = null;
        }
    }
}
