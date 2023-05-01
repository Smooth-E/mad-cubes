/*package com.simple.madqubies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class WeaponModule{
    
    public class TypeConstants{
        public int BODY = 0;
        public int UNDERBARREL = 1;
        public int STOCK = 2;
        public int SCOPE = 3;
        public int HANDLE = 4;
    }

    public class CharacteristicItem{
        int id; //characteristics id
        float value; //the value of char
        float modifier; //each level multiplies the value by modifier
        int lvl;
        int xp; //xp already gained on this level
        int startXP; //amount of xp needed to upgrade 1st lvl [amount of xp needed each level multiplies by modifier too]

        public CharacteristicItem(int id, int value, int startXP, int modifier){
            this.id = id;
            this.value = value;
            this.modifier = modifier;
            this.startXP = startXP;
            this.xp = 0;
            this.lvl = 0;
        }
    }

    Vector3 extraPosition; //the position that belongs to the weapon's body
    Texture picture; //the profile picture of a module
    CharacteristicItem[] chars; //the item chars
    Model model;
    int type; //module type
    String name; //module name

    public WeaponModule(int type, CharacteristicItem[] chars){
        this.type = type;
        this.chars = chars;
    }

    public WeaponModule(String name, Model model, Texture pic, Vector3 extraPosition){
        this.name = name;
        this. extraPosition = extraPosition;
        this.model = model;
        this.picture = pic;
    }
}
*/