package com.simple.madqubies;

import java.util.ArrayList;

public class DataStore {
    // Class that stores all of player data
    String name = "name";
    //HashMap<String, WeaponModule> inventory = new HashMap<>();
    ArrayList<WeaponModule> inventory = new ArrayList<WeaponModule>(0);
    int[] currentBuild = new int[5];
    int classicScore = 0;
    float classicTime = 0;
    int survivalScore = 0;
    float survivalTime = 0;
    int coins = 1000;

    public DataStore(){
        currentBuild = new int[]{1, 0, 0, 0, 0};
        inventory.add(new WeaponModule(0, new WeaponModule.CharacteristicItem(0,0,0,0,0)));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
        inventory.add(new WeaponModule(1, new WeaponModule.CharacteristicItem()));
    }
}

