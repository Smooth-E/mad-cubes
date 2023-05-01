package com.simple.madqubies;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.esotericsoftware.kryonet.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.FileHandler;

import javax.activation.DataContentHandler;
import javax.xml.crypto.Data;
import javax.xml.soap.Text;

import static java.rmi.server.RemoteServer.getLog;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameSuper extends Game {
    public Menu menu;
    public SinglePlayer singlePlayer;
    public ModeChoose modeChoose;
    public LoadingScreen loading;
    public MultiplayerSample mps;
    LoadoutScene loadoutScene;
    RoomChoose roomList;
    Color[] palette = {
            new Color(13 / 255f, 43 / 255f, 69 / 255f, 1),
            new Color(32 / 255f, 60 / 255f, 86 / 255f, 1),
            new Color(84 / 255f, 78 / 255f, 104 / 255f, 1),
            new Color(141 / 255f, 105 / 255f, 122 / 255f, 1),
            new Color(208 / 255f, 109 / 255f, 89 / 255f, 1),
            new Color(255 / 255f, 170 / 255f, 94 / 255f, 1),
            new Color(255 / 255f, 212 / 255f, 163 / 255f, 1),
            new Color(255 / 255f, 236 / 255f, 214 / 255f, 1)

    };
    public static WeaponModule[][] modules;
    public static Random random = new Random();
    Networking networking = new Networking(this);
    int UID = -1;
    boolean connected = false;
    Client client;
    static String vertexShader, fragmentShader;
    static ShaderProgram outlineShaderProgram;

    public static class MapData {
        Pixmap picture;
        ModelInstance map;
        String name;

        ArrayList<Shape> collider;

        MapData(){}

        MapData(String name, Pixmap picture, Model model, float offsetX, float offsetY, float offsetZ) {
            this.name = name;
            this.picture = picture;
            this.map = new ModelInstance(model);
            map.transform.translate(offsetX, offsetY, offsetZ);
        }
    }

    public static MapData[] maps;

    public static class MyShapeRenderer extends ShapeRenderer {
        public void roundedRect(float x, float y, float width, float height, float radius){
            // Central rectangle
            super.rect(x + radius, y + radius, width - 2*radius, height - 2*radius);

            // Four side rectangles, in clockwise order
            super.rect(x + radius, y, width - 2*radius, radius);
            super.rect(x + width - radius, y + radius, radius, height - 2*radius);
            super.rect(x + radius, y + height - radius, width - 2*radius, radius);
            super.rect(x, y + radius, radius, height - 2*radius);

            // Four arches, clockwise too
            super.arc(x + radius, y + radius, radius, 180f, 90f);
            super.arc(x + width - radius, y + radius, radius, 270f, 90f);
            super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
            super.arc(x + radius, y + height - radius, radius, 90f, 90f);
        }
    }

    public static Pixmap createRoundedRectangle(int width, int height, int cornerRadius, Color color) {

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap ret = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(color);

        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);

        pixmap.fillRectangle(cornerRadius, 0, width - cornerRadius * 2, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - cornerRadius * 2);

        ret.setColor(color);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixmap.getPixel(x, y) != 0) ret.drawPixel(x, y);
            }
        }
        pixmap.dispose();

        return ret;
    }


    String path = "save.isItAText";
    public DataStore playerData;

    public DataStore loadData() {
        //DataStore data = null;
        DataSaveState data = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(Gdx.files.getLocalStoragePath() + "/" + path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            data = (DataSaveState) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
        }
        catch (Exception e){
            Gdx.app.log("ERROR", "Unable to load from file!\n" + e.toString());
            return new DataStore();
        }
        return data.toDataStore();
    }

    public void saveData(DataSaveState data){
        try{
            FileOutputStream f = new FileOutputStream(Gdx.files.getLocalStoragePath() + "/" + path);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(data);
            o.close();
            f.close();
        }
        catch (Exception e){
            Gdx.app.log("ERROR", "Unable to write file!\n" + e.toString());
        }
    }

    FreeTypeFontGenerator fontGenerator;

    public static class CompareGUIViaLayer implements Comparator<GameObject2D>{
        @Override
        public int compare(GameObject2D o1, GameObject2D o2) {
            return Integer.compare(o1.getLayer(), o2.getLayer());
        }
    }

    @Override
    public void create() {
        FreeTypeFontGenerator.setMaxTextureSize(2048);

        random.setSeed(LocalTime.now().getNano() * LocalTime.now().getSecond());

        //playerData = loadData();

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("edit-undo.brk.ttf"));

        vertexShader = Gdx.files.internal("vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("fragment.glsl").readString();
        outlineShaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        menu = new Menu(this);
        singlePlayer = new SinglePlayer(this);
        modeChoose = new ModeChoose(this);
        loadoutScene = new LoadoutScene(this);
        loading = new LoadingScreen(this);
        Sandbox sandbox = new Sandbox(this);
        mps = new MultiplayerSample(this);
        roomList = new RoomChoose(this);
        setScreen(loading);
    }
}