package com.simple.madqubies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.utils.Array;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

public class WeaponModule {

    public class TypeConstants{
        public int BODY = 0;
        public int UNDERBARREL = 1;
        public int STOCK = 2;
        public int SCOPE = 3;
        public int HANDLE = 4;
    }

    public static class CharacteristicItem implements Serializable {
        int id; //characteristics id
        float value; //the value of char
        float modifier; //each level multiplies the value by modifier
        int lvl = 0;
        int xp = 0; //xp already gained on this level
        int startXP; //amount of xp needed to upgrade 1st lvl [amount of xp needed each level multiplies by modifier too]
        String name = "";
        String[] names = new String[]{
                "Damage",
                //"Magazine Capacity",
                //"Critical DMG %",
                "Bullet Spread",
                "Movement Speed",
                //"Shooting Speed"
        };
        int cost;

        public CharacteristicItem(int id, int value, int startXP, int modifier, int cost){
            this.id = id;
            this.name = this.names[this.id];
            this.value = value;
            this.modifier = modifier;
            this.startXP = startXP;
            this.cost = cost;
        }

        public CharacteristicItem(){
            Random random = GameSuper.random;
            id = random.nextInt(names.length);
            name = names[id];
            cost = random.nextInt(1001 - 100) + 100;
            modifier = GameSuper.random.nextFloat() * 5 + 1.1f; //float from 1.1f to 5 inclusive
            if (id == 0) value = random.nextInt(301) + 100; //damage count from 100 to 300 (inclusive)
            else if (id == 1) value = random.nextInt(11) + 1; //mag capacity from 1 to 10 inclusive
            else if (id == 3) value = random.nextFloat() * .1f + 0.01f; //crit% is less than 0.1f (10%)
            else if (id == 4) value = 1; //bullet spread is always 1 =)
            else if (id == 5 || id == 6) value = random.nextFloat() * .4f + .1f; //movement and shooting speed are from .1f to .5f
            else value = -1; //something is wrong
            startXP = random.nextInt(201) + 100; //from 100 to 300
        }
    }

    Vector3 extraPosition; //the position that belongs to the weapon's body
    Texture picture; //the profile picture of a module
    WeaponModule.CharacteristicItem characteristic; //the item char
    Model model;
    int type; //module type
    String name; //module name
    int id;
    static int lastID = 0;

    public WeaponModule(int type, WeaponModule.CharacteristicItem characteristic){
        this.type = type;
        this.characteristic = characteristic;
        //int index = GameSuper.random.nextInt(GameSuper.modules[type].length);
        int index = characteristic.id;
        //index = 0; //for debug
        WeaponModule template = GameSuper.modules[type][index];
        model = template.model;
        name = template.name;
        picture = template.picture;
        this.id = lastID;
        lastID++;
    }

    public WeaponModule(String name, Model model, Texture pic){
        this.name = name;
        this.model =  model;
        this.picture = pic;
    }

    public static Model combine(Model parent, Model child, Vector3 extraPosition){
        for(Node node : child.nodes){
            node.translation.add(extraPosition);
            parent.nodes.add(node);
        }
        return parent;
    }

    public static Model combine(Model parent, Model child, float x, float y, float z){
        return combine(parent, child, new Vector3(x, y, z));
    }

    static class ModuleContainer implements Serializable {
        int type;
        CharacteristicItem characteristic;

        ModuleContainer(int type, CharacteristicItem characteristic) {
            this.type = type;
            this.characteristic = characteristic;
        }

        WeaponModule toModule(){
            if (type == 1) return new WeaponBody(type, characteristic);
            else return new SecondaryModule(type, characteristic);
        }
    }
}