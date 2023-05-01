package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Map;

public class Menu implements Screen {
    GameSuper game;
    public Menu(GameSuper game){
        this.game = game;
    }

    HashMap<String, GameObject> objects;
    ModelBatch modelBatch = new ModelBatch();
    PerspectiveCamera camera;
    float camOffsetX = 0, camOffsetY = 1, camOffsetZ = 3;
    Environment environment;
    SpriteBatch spriteBatch = new SpriteBatch();
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Stage stage = new Stage(), lowerStage = new Stage();
    FreeTypeFontGenerator fontGenerator;
    BitmapFont logoFont, guiFont;
    float screenHeight, screenWidth;
    HashMap<String, GameObject2D> GUIs = new HashMap<String, GameObject2D>(3);

    Label playLabel, loadoutLabel, exitLabel;

    ModelInstance cube;
    Model[] cubeAnimFrames;
    float timePassed = 0, frameGap = 0.1f;
    int frameIndex;


    @Override
    public void show() {
        /*
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 /255f,214 /255f, -1f, -0.8f, -0.2f));
         */

        /*
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camOffsetX, camOffsetY, camOffsetZ);
        camera.lookAt(0,1,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
         */

        Bullet.init();
        //objects = new HashMap<String, GameObject>(2);
        ModelLoader<?> loader = new ObjLoader();
        Vector3 charPos = camera.unproject(new Vector3(-screenWidth / 4f, screenHeight / 2, 0));
        //objects.put("character", new GameObject(loader.loadModel(Gdx.files.internal("cube.obj")), new btBoxShape(Vector3.Zero), new Vector3(charPos.x, 0.2f, 0), 0));
        //objects.get("character").transform.rotate(new Quaternion(Vector3.Y, -70));

        Model f1 = loader.loadModel(Gdx.files.internal("cube01.obj")),
                f2 = loader.loadModel(Gdx.files.internal("cube02.obj")),
                f3 = loader.loadModel(Gdx.files.internal("cube03.obj"));
        cubeAnimFrames = new Model[4];
        cubeAnimFrames[0] = f1;
        cubeAnimFrames[1] = f2;
        cubeAnimFrames[2] = f3;
        cubeAnimFrames[3] = f2;
        cube = new ModelInstance(f1);
        cube.transform.set(new Vector3(charPos.x, charPos.y, 0), new Quaternion(Vector3.Y, -70));

        /*
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("edit-undo.brk.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 300;
        parameter.borderColor = game.palette[4];
        parameter.borderWidth = 10;
        logoFont = fontGenerator.generateFont(parameter);
        FreeTypeFontGenerator.FreeTypeFontParameter smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParameter.size = 200;
        smallParameter.borderColor = game.palette[4];
        smallParameter.borderWidth = 5;
        smallParameter.color = game.palette[1];
        guiFont = fontGenerator.generateFont(smallParameter);
        */

        /*
        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() / 3 + 300, Gdx.graphics.getHeight() / 5, Pixmap.Format.RGB888);
        pixmap.setColor(game.palette[5]);
        pixmap.fill();
        pixmap.setColor(game.palette[0]);
        pixmap.fillRectangle(0,0, pixmap.getHeight()/2, pixmap.getHeight());
        pixmap.setColor(game.palette[5]);
        pixmap.fillCircle(pixmap.getHeight() / 2, pixmap.getHeight() / 2, pixmap.getHeight() / 2);
        pixmap.setColor(game.palette[0]);
        pixmap.fillCircle(pixmap.getHeight()/2 + 5, pixmap.getHeight()/2, pixmap.getHeight()/2 - 10);
        pixmap.setColor(game.palette[6]);
        GUIs.put("Play", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth() + 200, 100));
        GUIs.put("Equipment", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth(), Gdx.graphics.getHeight()/2f - pixmap.getHeight()/2f));
        GUIs.put("Liderboard", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth() + 200, Gdx.graphics.getHeight() - pixmap.getHeight() - 100));
         */

        /*
        Label logoUp = new Label("Mad", new Label.LabelStyle(logoFont, game.palette[6]));
        logoUp.setPosition(stage.getWidth() / 4 - logoUp.getWidth() / 2, stage.getHeight() - logoUp.getHeight() - 50);
        logoUp.setAlignment(1);
        lowerStage.addActor(logoUp);
        Label logoLow = new Label("Cubes", new Label.LabelStyle(logoFont, game.palette[6]));
        logoLow.setPosition(stage.getWidth() / 4 - logoLow.getWidth() / 2, 50);
        lowerStage.addActor(logoLow);
        */

        playLabel = new Label("play", new Label.LabelStyle(guiFont, Color.WHITE));
        playLabel.setAlignment(Align.left);
        playLabel.setPosition(GUIs.get("Play").getPosition().x + GUIs.get("Play").getHeight() + 50, Gdx.graphics.getHeight() - GUIs.get("Play").getPosition().y - GUIs.get("Play").getHeight()/2f, Align.left);
        stage.addActor(playLabel);

        loadoutLabel = new Label("items", new Label.LabelStyle(guiFont, Color.WHITE));
        loadoutLabel.setAlignment(Align.left);
        loadoutLabel.setPosition(GUIs.get("Equipment").getPosition().x + GUIs.get("Equipment").getHeight() + 50,
                Gdx.graphics.getHeight() - GUIs.get("Equipment").getPosition().y - GUIs.get("Equipment").getHeight()/2f, Align.left);
        stage.addActor(loadoutLabel);

        exitLabel = new Label("exit", new Label.LabelStyle(guiFont, Color.WHITE));
        exitLabel.setAlignment(Align.left);
        exitLabel.setPosition(GUIs.get("Liderboard").getPosition().x + GUIs.get("Liderboard").getHeight() + 50,
                Gdx.graphics.getHeight() - GUIs.get("Liderboard").getPosition().y - GUIs.get("Liderboard").getHeight()/2f, Align.left);
        stage.addActor(exitLabel);

        Label uidLabel = new Label("UID:" + game.UID, new Label.LabelStyle(game.loading.description.getStyle().font, Color.WHITE));
        uidLabel.setAlignment(Align.bottomRight);
        uidLabel.setPosition(screenWidth, 0, Align.bottomRight);
        stage.addActor(uidLabel);

        //Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        timePassed += delta;
        if (timePassed >= frameGap){
            timePassed = 0;
            frameIndex ++;
            if (frameIndex == cubeAnimFrames.length) frameIndex = 0;
            cube = new ModelInstance(cubeAnimFrames[frameIndex], cube.transform);
        }

        if (Gdx.input.justTouched()){
            Gdx.app.log("","Touch coords: " + Gdx.input.getX() + ", " + Gdx.input.getY());
            for(Map.Entry<String, GameObject2D> entry: GUIs.entrySet()){
                GameObject2D obj = entry.getValue();
                if(obj.contains(Gdx.input.getX(), Gdx.input.getY())){
                    game.loading.btnPress.play();
                    Gdx.app.log("", entry.getKey());
                    switch (entry.getKey()){
                        case "Play":
                            game.setScreen(game.roomList);
                            break;
                        case "Equipment":
                            game.setScreen(game.loadoutScene);
                            break;
                        case "Liderboard":
                            Gdx.app.exit();
                            break;
                    }
                }
            }
        }
        else if (Gdx.input.isTouched()) cube.transform.rotate(Vector3.Y, Gdx.input.getDeltaX());


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        lowerStage.draw();

        modelBatch.begin(camera);
        modelBatch.render(cube, environment);
        modelBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(game.palette[1]);
        shapeRenderer.circle(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth() / 2f + 100);
        shapeRenderer.setColor(game.palette[0]);
        shapeRenderer.circle(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2f, Gdx.graphics.getWidth() / 2f);
        shapeRenderer.end();

        spriteBatch.begin();
        for (Map.Entry<String, GameObject2D> entry: GUIs.entrySet()){
            GameObject2D object = entry.getValue();
            float x = object.getPosition().x, y = object.getPosition().y;
            spriteBatch.draw(object, x, y);
        }
        //guiFont.draw(spriteBatch, "Play", GUIs.get("Play").getPosition().x + GUIs.get("Play").getHeight() + 50, Gdx.graphics.getHeight() - GUIs.get("Play").getPosition().y - GUIs.get("Play").getHeight()/4f);
        //guiFont.draw(spriteBatch, "Stuff", GUIs.get("Equipment").getPosition().x + GUIs.get("Equipment").getHeight() + 50, Gdx.graphics.getHeight() - GUIs.get("Equipment").getPosition().y - GUIs.get("Equipment").getHeight()/4f);
        //guiFont.draw(spriteBatch, "Exit", GUIs.get("Liderboard").getPosition().x + GUIs.get("Liderboard").getHeight() + 50, Gdx.graphics.getHeight() - GUIs.get("Liderboard").getPosition().y - GUIs.get("Liderboard").getHeight()/4f);
        spriteBatch.end();


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
        fontGenerator.dispose();

        for (Map.Entry<String, GameObject> entry : objects.entrySet()) {
            entry.getValue().dispose();
        }
    }
}
