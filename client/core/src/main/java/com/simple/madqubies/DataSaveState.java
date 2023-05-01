package com.simple.madqubies;

import java.io.Serializable;
import java.util.ArrayList;

public class DataSaveState implements Serializable {
    ArrayList<WeaponModule.ModuleContainer> inventory = new ArrayList<>();
    int[] currentBuild = new int[5];

    DataSaveState (DataStore data) {
        for (WeaponModule module : data.inventory) {
            this.inventory.add(new WeaponModule.ModuleContainer(module.type, module.characteristic));
        }
        this.currentBuild = data.currentBuild;
    }

    DataStore toDataStore(){
        DataStore data = new DataStore();
        data.currentBuild = this.currentBuild;
        data.inventory.clear();
        for(WeaponModule.ModuleContainer module : this.inventory){
            data.inventory.add(module.toModule());
        }
        return data;
    }
}