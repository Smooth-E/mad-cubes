package com.simple.madqubies;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;

public class Networking {

    static GameSuper game;
    Networking(GameSuper game){
        Networking.game = game;
    }

    public static void initialize(Client client){
        Kryo kryo = client.getKryo();
        kryo.register(Player.class);
        kryo.register(EnvironmentUnit.class);
        kryo.register(Room.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(RoomRequest.class);
        kryo.register(RoomResponse.class);
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(UpdateRoomListRequest.class);
        kryo.register(UpdateRoomListResponse.class);
        kryo.register(PlayerStateChangeRequest.class);
        kryo.register(PlayerStateChangedResponse.class);
        kryo.register(UIDRequest.class);
        kryo.register(UIDResponse.class);
        kryo.register(int[].class);
        kryo.register(EnvironmentUnit[].class);
        kryo.register(PlayerStateChangeRequest.DamageContainer.class);
        kryo.register(DisconnectRequest.class);
        kryo.register(ModuleFoundResponse.class);

        client.addListener(new Listener(){
            public void received(Connection connection, Object object){
                if (object instanceof  UIDResponse) {
                    game.UID = ( (UIDResponse) object ).UID;
                }
                else if (object instanceof UpdateRoomListResponse && RoomChoose.receiving) {
                    RoomChoose.roomResponse = (UpdateRoomListResponse) object;
                    RoomChoose.results = RoomChoose.roomResponse.rooms;
                    RoomChoose.receiving = false;
                    RoomChoose.generateList = true;
                }
                else if (object instanceof JoinResponse) {
                    JoinResponse response = (JoinResponse) object;
                    if (response.result && RoomChoose.receiving) {
                        game.roomList.roomID = response.roomID;
                        game.roomList.mapID = response.mapID;
                        RoomChoose.receiving = false;
                    }
                }
                else if (object instanceof PlayerStateChangedResponse) {
                    if(((PlayerStateChangedResponse) object).status) {
                        if (MultiplayerScene.receivingPlayerState) {
                            PlayerStateChangedResponse response = (PlayerStateChangedResponse) object;
                            MultiplayerScene.otherPlayers = response.otherPlayers;
                            MultiplayerScene.hp = response.myHP;
                        }
                    }
                }
                else if (object instanceof RoomResponse && RoomChoose.receiving){
                    RoomChoose.receiving = false;
                    game.roomList.mapID = game.roomList.hostMapID;
                    game.roomList.roomID = ((RoomResponse) object).roomID;
                }
                else if (object instanceof ModuleFoundResponse) MultiplayerScene.moduleFound = true;
            }
        });
    }

    static class ModuleFoundResponse{}

    static class DisconnectRequest{
        int UID, roomID;

        DisconnectRequest(){}
        DisconnectRequest(int uid, int roomID){
            this.UID = uid;
            this.roomID = roomID;
        }
    }

    static class Player {
        float x, z, angle, hp;
        int UID;
        int[] weaponTypes = new int[5];
        EnvironmentUnit[] environment = new EnvironmentUnit[0];

        Player(){}
        Player(int UID, float x, float z, float angle, float hp){
            this.x = x;
            this.z = z;
            this.angle = angle;
            this.UID = UID;
            environment = new EnvironmentUnit[0];
            this.hp = hp;
        }
    }

    static class EnvironmentUnit {
        float positionX, positionY, angle;

        EnvironmentUnit(){};
        EnvironmentUnit(float x, float y, float angle) {
            this.angle = angle;
            this.positionX = x;
            this.positionY = y;
        }
    }

    static class Room{
        int capacity, mapID, roomID;
        ArrayList<Player> players;

        Room(){}
        Room(int capacity, int mapID){
            this.capacity = capacity;
            this.mapID = mapID;
            this.players = new ArrayList<Player>(0);
            this.roomID = rooms.size();
        }
    }

    static ArrayList<Room> rooms = new ArrayList<>();

    static class RoomRequest{
        int capacity, mapID;
        int UID;

        RoomRequest(){}
        RoomRequest(int UID, int capacity, int mapID){
            this.UID = UID;
            this.capacity = capacity;
            this.mapID = mapID;
        }
    }

    static class RoomResponse{
        int roomID;

        RoomResponse(){}
        RoomResponse(int roomID){
            this.roomID = roomID;
        }
    }

    static class JoinRequest{
       int UID;
        int roomID;

        JoinRequest(){}
        JoinRequest(int UID, int roomID){
            this.UID = UID;
            this.roomID = roomID;
        }
    }

    static class JoinResponse{
        int roomID, mapID;
        boolean result;
    }

    static class UpdateRoomListRequest {}

    static class UpdateRoomListResponse {
        ArrayList<Room> rooms;

        UpdateRoomListResponse(){}
        UpdateRoomListResponse(ArrayList<Room> rooms){
            this.rooms = rooms;
        }
    }

    static class PlayerStateChangeRequest {
        int roomID;
        Player playerState;
        static class DamageContainer{
            int UID;
            float damage;
            DamageContainer(){}
            DamageContainer(int UID, float damage){
                this.UID = UID;
                this.damage = damage;
            }
        }
        ArrayList<DamageContainer> damage = new ArrayList<>();

        PlayerStateChangeRequest(){}
        PlayerStateChangeRequest(Player playerState, int roomID){
            this.playerState = playerState;
            this.roomID = roomID;
        }
        PlayerStateChangeRequest(int UID, float x, float z, float angle, float hp, int roomID){
            this(new Player(UID, x, z, angle, hp), roomID);
        }
    }

    static class PlayerStateChangedResponse{
        ArrayList<Player> otherPlayers;
        float myHP = 100;
        boolean status = false;

        PlayerStateChangedResponse(){}
        PlayerStateChangedResponse(ArrayList<Player> otherPlayers){
            this.otherPlayers = otherPlayers;
        }
    }

    static class UIDRequest{}

    static class UIDResponse {
        int UID;

        UIDResponse(){}
        UIDResponse(int UID){
            this.UID = UID;
        }
    }

}
