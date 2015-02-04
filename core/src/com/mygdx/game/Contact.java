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

        Entity aEnt = (Entity)a.getBody().getUserData();
        Entity bEnt = (Entity)b.getBody().getUserData();

        ContactInfo infoA = new ContactInfo(a.getBody(), a, aEnt);
        ContactInfo infoB = new ContactInfo(b.getBody(), b, bEnt);

        if(aEnt == null || bEnt == null)
            return;



        //If one of them is a missile and one is a tower...
        if((aEnt.getName().equals("tower") || bEnt.getName().equals("tower")) && (aEnt.getName().equals("missile") || bEnt.getName().equals("missile"))){
            Entity tower = aEnt.getName().equals("tower") ? aEnt : bEnt;
            Entity missile = aEnt.getName().equals("missile") ? aEnt : bEnt;

            //If neither is a sensor... kill both.
            if(!a.isSensor() && !b.isSensor()) {
                //System.out.println("Destroyed: "+tower.getName()+" and "+missile.getName());
                tower.setDestroyed();
                missile.setDestroyed();

            //If both are sensors...
            }else if(a.isSensor() && b.isSensor()){

            //Otherwise, one is a sensor. It's most likely a tower. Check which is a tower and set the other as its target.
            }else if(a.isSensor() || b.isSensor()){
                Entity sensor = (infoA.fixture.isSensor()) ? infoA.entity : infoB.entity;
                Entity other = (!infoA.fixture.isSensor()) ? infoA.entity : infoB.entity;

                System.out.println("sensor: "+sensor.getName()+" other: "+other.getName());

                if(sensor.getName().equals("tower")){
                    ((Tower)sensor).setTarget(other);
                }else if(sensor.getName().equals("bullet")){
                    other.destroy();
                    sensor.destroy();
                }
            }
        }

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
