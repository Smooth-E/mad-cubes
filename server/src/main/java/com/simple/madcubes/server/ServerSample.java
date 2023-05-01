package com.simple.madcubes.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.net.InetAddress;


public class ServerSample {

    static String ERROR_TAG = "ERROR", WARNING_TAG = "WARNING", DEBUG_TAG = "DEBUG", CONNECTION_INFO_TAG = "CONNECTION INFO", CONNECTION_ERROR_TAG = "CONNECTION ERROR";

    static Server server;
    static Kryo kryo;

    static class Player {
        float x, z, angle, hp;
        int UID;
        int[] weaponTypes;
        EnvironmentUnit[] environment;

        Player(){}
        Player(int UID, float x, float z, float angle, float hp){
            this.x = x;
            this.z = z;
            this.angle = angle;
            this.UID = UID;
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
        PlayerStateChangeRequest(int UID, float x, float z, float angle, float damage, int roomID){
            this(new Player(UID, x, z, angle, damage), roomID);
        }
    }

    static class PlayerStateChangedResponse{
        ArrayList<Player> otherPlayers = new ArrayList<>();
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

    static ArrayList<Integer> usersUIDs = new ArrayList<>();

    static class ModuleFoundResponse {}

    static class DisconnectRequest{
        int UID, roomID;

        DisconnectRequest(){}
        DisconnectRequest(int uid, int roomID){
            this.UID = uid;
            this.roomID = roomID;
        }
    }


    public static void main(String[] args) throws UnknownHostException {
        /*
        rooms.add(new Room(100, 1));
        rooms.add(new Room(100, 1));
        rooms.add(new Room(100, 1));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 1));
         */
        //rooms.add(new Room(1, 0));

        rooms.add(new Room(100, 1));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 0));
        rooms.add(new Room(100, 1));

        server = new Server();
        server.start();
        try{
            server.bind(/*tcp port/*, /*udp port/*);
        } catch (Exception e){
            log(CONNECTION_ERROR_TAG, e.toString());
        }

        InetAddress ip = InetAddress.getLocalHost();
        System.out.print(ip.getHostAddress());

        kryo = server.getKryo();
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

        LocalDateTime time = LocalDateTime.now();
        long seed = time.getYear() * time.getDayOfYear() * time.getHour() * time.getMinute() * time.getSecond();
        Random random = new Random(seed);

        server.addListener(new Listener(){
            public void received (Connection connection, Object object) {
                if (object instanceof UIDRequest) {
                    int UID = random.nextInt();
                    while (usersUIDs.contains(UID)) {
                        UID = random.nextInt();
                    }
                    usersUIDs.add(UID);
                    connection.sendTCP(new UIDResponse(UID));
                }
                if(object instanceof RoomRequest){
                    RoomRequest request = (RoomRequest) object;
                    Room room = new Room(request.capacity, request.mapID);
                    room.players.add( new Player(request.UID, 0, 0, 0, 100) );
                    rooms.add(room);
                    connection.sendTCP(new RoomResponse(rooms.indexOf(room)));
                }
                else if (object instanceof JoinRequest){
                    JoinRequest request = (JoinRequest) object;
                    int roomID = request.roomID;
                    JoinResponse response = new JoinResponse();
                    if (rooms.get(roomID) != null && rooms.get(roomID).players.size() < rooms.get(roomID).capacity){
                        rooms.get(roomID).players.add(new Player(request.UID, 0,0,0, 100));
                        response.result = true;
                        response.roomID = request.roomID;
                        response.mapID = rooms.get(roomID).mapID;
                    }
                    else {
                        response.result = false;
                    }
                    connection.sendTCP(response);
                }
                else if (object instanceof UpdateRoomListRequest) connection.sendTCP(new UpdateRoomListResponse(rooms));
                else if (object instanceof PlayerStateChangeRequest) {
                    try {
                        PlayerStateChangeRequest request = (PlayerStateChangeRequest) object;
                        int roomID = request.roomID;
                        Player state = request.playerState;
                        int UID = request.playerState.UID;
                        PlayerStateChangedResponse response = new PlayerStateChangedResponse();
                        for (int i = 0; i < rooms.get(roomID).players.size(); i++) {
                            Player player = rooms.get(roomID).players.get(i);
                            if (player.UID == UID) {
                                rooms.get(roomID).players.get(i).angle = state.angle;
                                rooms.get(roomID).players.get(i).environment = state.environment;
                                rooms.get(roomID).players.get(i).x = state.x;
                                rooms.get(roomID).players.get(i).z = state.z;
                                rooms.get(roomID).players.get(i).weaponTypes = state.weaponTypes;
                                response.myHP = rooms.get(roomID).players.get(i).hp;
                                response.status = true;
                            } else {
                                for (PlayerStateChangeRequest.DamageContainer c : request.damage) {
                                    if (c.UID == rooms.get(roomID).players.get(i).UID) {
                                        rooms.get(roomID).players.get(i).hp -= c.damage;
                                    }
                                }
                                if (rooms.get(roomID).players.get(i).hp < 0){
                                    rooms.get(roomID).players.get(i).hp = 100;
                                    connection.sendTCP(new ModuleFoundResponse());
                                }
                                else if (rooms.get(roomID).players.get(i).hp == 0)
                                    rooms.get(roomID).players.get(i).hp -= 100;
                                response.otherPlayers.add(rooms.get(roomID).players.get(i));
                            }
                        }
                        connection.sendTCP(response);
                    }
                    catch (Exception ignored) {}
                }
                else if (object instanceof DisconnectRequest) {
                    DisconnectRequest request = (DisconnectRequest) object;
                    for (Player p : rooms.get(request.roomID).players) {
                        if (p.UID == request.UID){
                            rooms.get(request.roomID).players.remove(p);
                            break;
                        }
                    }
                    if (rooms.get(request.roomID).players.size() == 0) rooms.remove(request.roomID);
                }
            }
        });
    }

    static void print(String str){
        System.out.print(str);
    }

    static void log(String tag, String msg){
        System.out.println("[" + tag + "]: " + msg);
    }

    static void log(String msg){
        System.out.println(msg);
    }

}
