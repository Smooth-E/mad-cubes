package com.simple.madqubies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class WeaponBody extends WeaponModule{

    Vector3 zeroPoint, underbarrelJoint, stockJoint, scopeJoint, handleJoint;

    public WeaponBody(int type, CharacteristicItem characteristic) {
        super(type, characteristic);
    }

    public WeaponBody(String name, Model model, Texture pic, Vector3 zeroPoint, Vector3 underbarrelJoint, Vector3 stockJoint, Vector3 scopeJoint, Vector3 handleJoint) {
        super(name, model, pic);
        this.zeroPoint = zeroPoint;
        this.underbarrelJoint = underbarrelJoint;
        this.stockJoint = stockJoint;
        this.scopeJoint = scopeJoint;
        this.handleJoint = handleJoint;
    }
}
