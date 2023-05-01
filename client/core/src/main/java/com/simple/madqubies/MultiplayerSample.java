package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class MultiplayerSample implements Screen {

    GameSuper game;
    public MultiplayerSample(GameSuper game){ this.game = game;}
    Color[] palette;
    ModelLoader loader;
    Model[] cubeAnimFrames;
    ModelInstance myCube;
    float timePassed, frameGap = 0.5f;
    int frameIndex;
    Stage stage = new Stage();
    Environment environment;
    PerspectiveCamera camera;
    float camOffsetX = 0,
            camOffsetY = 5,
            camOffsetZ = -5;
    int screenHeight, screenWidth;
    ModelBatch modelBatch = new ModelBatch();
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Touchpad touchpad;
    Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
    float movingSpeed = 1 / 25f;
    float characterAngle = -70;
    GameSuper.MyShapeRenderer shapeRenderer = new GameSuper.MyShapeRenderer();
    PerspectiveCamera miniCamera;
    Texture hp;
    Client client;
    ArrayList<ModelInstance> otherPlayers = new ArrayList<>();
    int playerID;

    ArrayList<GameInfo> otherTransforms = new ArrayList<>();

    static class GameInfo{
        GameInfo(){}
        GameInfo(float x, float y, float z, float angle, int playerID){
            this.x = x;
            this.y = y;
            this.z = z;
            this.angle = angle;
            this.playerID = playerID;
        }
        float x, y, z, angle;
        int playerID;
    }

    static class InfoContainer{
        InfoContainer(){}
        ArrayList<GameInfo> info;
    }

    @Override
    public void show() {

        playerID = new Random().nextInt();

        client = new Client();
        client.start();
        try{
            client.connect(5000, "192.168.0.190", 54555, 54777);
        } catch (Exception e){
            Gdx.app.log("CONNECTION", e.toString());
        }

        client.getKryo().register(GameInfo.class);
        client.getKryo().register(InfoContainer.class);
        client.getKryo().register(java.util.ArrayList.class);

        client.addListener(new Listener(){
            public void received(Connection connection, Object object){
                if (object instanceof InfoContainer){
                    InfoContainer infoContainer = (InfoContainer)object;
                    ArrayList<GameInfo> info = infoContainer.info;
                    //otherPlayers.clear();
                    ArrayList<ModelInstance> newInfo = new ArrayList<>();
                    for (GameInfo gameInfo : info) {
                        if (gameInfo.playerID != playerID){
                            ModelInstance playerInstance = new ModelInstance(cubeAnimFrames[2]);
                            playerInstance.transform.set(new Vector3(gameInfo.x, gameInfo.y, gameInfo.z), new Quaternion(Vector3.Y, gameInfo.angle));
                            newInfo.add(playerInstance);
                        }
                    }
                    otherPlayers = newInfo;
                }
            }
        });

        palette = game.palette;

        loader = new ObjLoader();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 /255f,214 /255f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camOffsetX, camOffsetY, camOffsetZ);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        Model f1 = loader.loadModel(Gdx.files.internal("cube01.obj")),
                f2 = loader.loadModel(Gdx.files.internal("cube02.obj")),
                f3 = loader.loadModel(Gdx.files.internal("cube03.obj"));
        cubeAnimFrames = new Model[4];
        cubeAnimFrames[0] = f1;
        cubeAnimFrames[1] = f2;
        cubeAnimFrames[2] = f3;
        cubeAnimFrames[3] = f2;
        myCube = new ModelInstance(f1);


        Pixmap pixmap = new Pixmap(screenHeight/4, screenHeight/4, Pixmap.Format.RGBA8888);
        pixmap.setColor(palette[5]);
        pixmap.fillCircle(pixmap.getHeight()/2, pixmap.getHeight()/2, pixmap.getHeight()/2);
        pixmap.setColor(palette[1]);
        pixmap.fillCircle(pixmap.getHeight()/2, pixmap.getHeight()/2, pixmap.getHeight()/2 - 10);
        touchpadStyle.background = new TextureRegionDrawable(new Texture(pixmap));
        pixmap = new Pixmap(screenHeight/4/2, screenHeight/4/2, Pixmap.Format.RGBA8888);
        pixmap.setColor(palette[5]);
        pixmap.fillCircle(pixmap.getHeight()/2, pixmap.getHeight()/2, pixmap.getHeight()/2);
        pixmap.setColor(palette[1]);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("touchpad_arrows.png")), 0, 0, 200, 200, 5, 5, pixmap.getHeight() - 10, pixmap.getHeight() - 10);
        touchpadStyle.knob = new TextureRegionDrawable(new Texture(pixmap));
        touchpad = new Touchpad(1, touchpadStyle);
        stage.addActor(touchpad);
        touchpad.setPosition(100, 100);

        pixmap = new Pixmap(screenHeight/8, screenHeight/8 /20*18, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("hp.png")), 0, 0, 200, 180, 0, 0, pixmap.getHeight(), pixmap.getHeight());
        hp = new Texture(pixmap);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Vector3 position = myCube.transform.getTranslation(Vector3.Zero);
        float angle = myCube.transform.getRotation(new Quaternion()).getAxisAngle(Vector3.Y);
        client.sendTCP(new GameInfo(position.x, position.y, position.z, angle, playerID));

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Color c = palette[0];
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act();

        timePassed += delta;
        if (timePassed >= frameGap){
            timePassed = 0;
            frameIndex ++;
            if (frameIndex == cubeAnimFrames.length) frameIndex = 0;
            myCube = new ModelInstance(cubeAnimFrames[frameIndex], myCube.transform);
        }

        if (touchpad.isTouched()) {
            float tx = touchpad.getKnobPercentX(), ty = touchpad.getKnobPercentY();
            myCube.transform.set(myCube.transform.getTranslation(Vector3.Zero).add(new Vector3(movingSpeed * -tx, 0, movingSpeed * ty)), new Quaternion(Vector3.Y, new Vector2(-tx, -ty).angle()));
        }

        modelBatch.begin(camera);
        modelBatch.render(myCube, environment);
        for(int i = 0; i < otherPlayers.size(); i ++){
            ModelInstance playerInstance = otherPlayers.get(i);
            if(playerInstance != null) modelBatch.render(playerInstance, environment);
        }
        modelBatch.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(palette[5]);
        shapeRenderer.rect(0, screenHeight - screenHeight/8f - 30, screenWidth/3f, screenHeight/8f);
        shapeRenderer.triangle(screenWidth/3f, screenHeight - screenHeight/8f - 30, screenWidth/3f, screenHeight - 30, screenWidth/3f + 50, screenHeight - 30);
        shapeRenderer.setColor(palette[1]);
        shapeRenderer.rect(0, screenHeight - screenHeight/8f - 30 + 10, screenWidth/3f - 10, screenHeight/8f - 20);
        shapeRenderer.triangle(screenWidth/3f - 10, screenHeight - screenHeight/8f - 30 + 10, screenWidth/3f - 10, screenHeight - 30 - 10, screenWidth/3f + 50 - 20, screenHeight - 30 - 10);


        /*
        shapeRenderer.setColor(palette[5]);
        shapeRenderer.roundedRect(0, screenHeight/4f*3, screenHeight/4f, screenHeight/4f, 10);
        shapeRenderer.setColor(palette[0]);
        shapeRenderer.roundedRect(10, screenHeight/4f*3 + 10, screenHeight/4f - 20, screenHeight/4f - 20, 10);
        */
        shapeRenderer.end();



        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
