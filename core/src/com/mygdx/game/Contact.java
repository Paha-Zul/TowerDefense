package com.mygdx.game;


import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.entity.Tower;

/**
 * Created by Paha on 2/3/2015.
 */
public class Contact implements ContactListener {
    @Override
    public void beginContact(com.badlogic.gdx.physics.box2d.Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a == null || b == null)
            return;

        Entity aEnt = (Entity)a.getBody().getUserData();
        Entity bEnt = (Entity)b.getBody().getUserData();

        if(aEnt == null || bEnt == null || aEnt.isDestroyed() || bEnt.isDestroyed())
            return;

        ContactInfo infoA = new ContactInfo(a.getBody(), a, aEnt);
        ContactInfo infoB = new ContactInfo(b.getBody(), b, bEnt);

        //If neither is a sensor... kill both.
        if((!a.isSensor() && !b.isSensor()) && !infoA.entity.sameTeam(infoB.entity)) {
            //System.out.println("Destroyed: "+tower.getName()+" and "+missile.getName());
            infoA.entity.setDestroyed();
            infoB.entity.setDestroyed();

        //Both are sensors
        }else if(a.isSensor() && b.isSensor()){

            //If one is a missile and one is a tower (the tower attack range fixture)
            if(!infoA.entity.sameTeam(infoB.entity) && (hasNames(infoA, infoB, "missile", "tower"))){
                Entity tower = infoA.entity.getName().equals("tower") ? infoA.entity : infoB.entity;
                Entity missile = infoA.entity.getName().equals("missile") ? infoA.entity : infoB.entity;

                ((Tower)tower).addTarget(missile);

            //One is a missile and one is a bullet
            }else if(!infoA.entity.sameTeam(infoB.entity) && hasNames(infoA, infoB, "missile", "bullet")){
                infoA.entity.setDestroyed();
                infoB.entity.setDestroyed();
            }


        //One of them is a sensor
        }else{
            Entity sensor = (infoA.fixture.isSensor()) ? infoA.entity : infoB.entity;
            Entity other = (!infoA.fixture.isSensor()) ? infoA.entity : infoB.entity;

            //If the sensor is a bullet
            if(sensor.getName().equals("bullet") && !sensor.sameTeam(other)) {
                other.setDestroyed();
                sensor.setDestroyed();

            //If the sensor is a missile
            }else if(((hasNames(infoA, infoB, "missile", "tower")  || hasNames(infoA, infoB, "missile", "silo")) && !sensor.sameTeam(other))){
                sensor.setDestroyed();
                other.setDestroyed();
            }
        }
    }

    private boolean oneHasName(ContactInfo infoA, ContactInfo infoB, String name){
        return infoA.entity.getName().equals(name) || infoB.entity.getName().equals(name);
    }

    private boolean hasNames(ContactInfo infoA, ContactInfo infoB, String name1, String name2){
        return (infoA.entity.getName().equals(name1) || infoB.entity.getName().equals(name1)) && (infoA.entity.getName().equals(name2) || infoB.entity.getName().equals(name2));
    }

    @Override
    public void endContact(com.badlogic.gdx.physics.box2d.Contact contact) {

    }

    @Override
    public void preSolve(com.badlogic.gdx.physics.box2d.Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(com.badlogic.gdx.physics.box2d.Contact contact, ContactImpulse impulse) {

    }

    private class ContactInfo{
        public Body body;
        public Entity entity;
        public Fixture fixture;

        public ContactInfo(Body body, Fixture fixture, Entity entity){
            this.body = body;
            this.fixture = fixture;
            this.entity = entity;
        }
    }
}
