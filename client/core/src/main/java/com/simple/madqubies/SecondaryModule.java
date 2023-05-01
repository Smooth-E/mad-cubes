package com.simple.madqubies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class SecondaryModule extends WeaponModule{

    Vector3 joint;

    public SecondaryModule(int type, CharacteristicItem characteristic) {
        super(type, characteristic);
    }

    public SecondaryModule(String name, Model model, Texture pic, Vector3 joint) {
        super(name, model, pic);
        this.joint = joint;
    }
}
