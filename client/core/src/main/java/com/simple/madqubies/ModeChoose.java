package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;

public class ModeChoose implements Screen {

    GameSuper game;
    public ModeChoose(GameSuper game){
        this.game = game;
    }

    Color[] palette;
    GameSuper.MyShapeRenderer shapeRenderer = new GameSuper.MyShapeRenderer();
    OrthographicCamera camera;
    Viewport viewport;
    float screenWidth, screenHeight;
    HashMap<String, GameObject2D> GUIs = new HashMap<String, GameObject2D>(3);
    SpriteBatch spriteBatch = new SpriteBatch();
    boolean[] animationTriggers = new boolean[3];
    /*
    The trigger of every animation in scene stores here
    [if trigger == false then animation not played yet]
    0 - the lower "Leaderboard" button goes up
    1 - center button pops up;
    2 - upper "Classic Mode" button goes down
    */
    Stage stage = new Stage();
    Label classic, arcade, leader;
    Label.LabelStyle labelStyle;
    FreeTypeFontGenerator fontGenerator;
    BitmapFont guiFont;
    boolean needReset = false; //if true resets guis and other stuff
    Vector2 touchPos;
    int margin;

    @Override
    public void show() {
        palette = game.palette;
        //camera = new OrthographicCamera();
        //viewport = new ExtendViewport(800, 600, camera);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        /*
        margin = 100;
        int radius = 50, w = (int)screenWidth - 2*margin, h = (int)screenHeight/3 - 2*margin, borderWidth = 10;
        Pixmap lower = GameSuper.createRoundedRectangle(w, h, radius, palette[6]);
        Pixmap upper = GameSuper.createRoundedRectangle(w - borderWidth, h - borderWidth, radius, palette[0]);
        lower.drawPixmap(upper, borderWidth/2, borderWidth/2);
        GUIs.put("1", new GameObject2D(lower, margin, margin));
        GUIs.put("2", new GameObject2D(lower, margin, screenHeight/2 - lower.getHeight()/2f));
        GUIs.put("3", new GameObject2D(lower, margin, screenHeight - lower.getHeight() - margin));
        GUIs.get("1").setX(-lower.getWidth());
        GUIs.get("2").setX(screenWidth);
        GUIs.get("3").setY(screenHeight);
        */

        /*
        fontGenerator = game.fontGenerator;
        FreeTypeFontGenerator.FreeTypeFontParameter smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParameter.size = 200;
        smallParameter.borderColor = game.palette[4];
        smallParameter.borderWidth = 5;
        smallParameter.color = game.palette[1];
        guiFont = fontGenerator.generateFont(smallParameter);
        labelStyle = new Label.LabelStyle(guiFont, Color.WHITE);
        */

        /*
        classic = new Label("Classic Mode", labelStyle);
        classic.setScale(lower.getWidth(), lower.getHeight());
        arcade = new Label("Arcade Mode", labelStyle);
        arcade.setScale(lower.getWidth(), lower.getHeight());
        leader = new Label("Leader Board", labelStyle);
        leader.setScale(lower.getWidth(), lower.getHeight());
        stage.addActor(classic);
        stage.addActor(arcade);
        stage.addActor(leader);
        */

    }

    @Override
    public void render(float delta) {

        if (needReset){
            Gdx.app.log("", GUIs.get("1").getPosition().toString() + ", " + GUIs.get("1").getHeight());
            needReset = false;
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(palette[0].r, palette[0].g, palette[0].b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        GameObject2D o = null;

        for (int i = 0; i < animationTriggers.length; i++) {
            if(!animationTriggers[i]) {
                float animSpeed = screenHeight/2f;
                switch (i){
                    case 0:
                        o = GUIs.get("1");
                        if (o.getX() < o.getOrigin().x) o.setX(o.getX() + animSpeed * o.getWidth()/o.getHeight() / 1.5f * delta);
                        else {
                            o.setPosition(o.getOrigin());
                            animationTriggers[0] = true;
                        }
                        break;
                    case 1:
                        o = GUIs.get("2");
                        if(o.getX() > o.getOrigin().x) o.setX(o.getX() - animSpeed * o.getWidth()/o.getHeight() / 1.5f * delta);
                        else {
                            o.setPosition(o.getOrigin());
                            animationTriggers[1] = true;
                        }
                        break;
                    case 2:
                        o = GUIs.get("3");
                        if(o.getOrigin().y < o.getY()) o.setY(o.getY() - animSpeed * delta);
                        else {
                            o.setX(o.getOrigin().x);
                            animationTriggers[2] = true;
                        }
                        break;
                }
            }
        }

        o = GUIs.get("1");
        leader.setPosition( o.getX() + o.getWidth()/2f, o.getY() + o.getHeight()/2f, Align.center);
        o = GUIs.get("2");
        arcade.setPosition( o.getX() + o.getWidth()/2f, o.getY() + o.getHeight()/2f, Align.center);
        o = GUIs.get("3");
        classic.setPosition( o.getX() + o.getWidth()/2f, o.getY() + o.getHeight()/2f, Align.center);

        spriteBatch.begin();
        for(Map.Entry<String, GameObject2D> entry: GUIs.entrySet()){
            GameObject2D object = entry.getValue();
            float x = object.getX(), y = object.getY();
            spriteBatch.draw(object, x, y);
        }
        spriteBatch.end();

        stage.draw();

        if(Gdx.input.justTouched()){
            touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            for(Map.Entry<String, GameObject2D> entry : GUIs.entrySet()){
                if(entry.getValue().contains(touchPos)){
                    switch (entry.getKey()){
                        case "1":
                            game.setScreen(game.menu);
                            break;
                        case "2":
                            game.setScreen(game.menu);
                            break;
                        case "3":
                            game.setScreen(game.menu);
                            break;
                    }
                }
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("", "resize()");
        if (needReset){
            /*
            margin, margin));
            margin, screenHeight/2 - lower.getHeight()/2f));
            margin, screenHeight - lower.getHeight() - margin))
             */
            int h = (int)screenHeight/3 - 2*margin;
            GUIs.get("1").setPosition(GUIs.get("1").getOrigin());
            GUIs.get("2").setPosition(GUIs.get("2").getOrigin());
            GUIs.get("3").setPosition(GUIs.get("3").getOrigin());
            needReset = false;
        }
    }

    @Override
    public void pause() {
        Gdx.app.log("", "pause()");
    }

    @Override
    public void resume() {
        Gdx.app.log("", "resume()");
    }

    @Override
    public void hide() {
        Gdx.app.log("", "hide()");
        needReset = true;
    }

    @Override
    public void dispose() {
        for(Map.Entry<String, GameObject2D> entry : GUIs.entrySet()){
            entry.getValue().dispose();
        }
        spriteBatch.dispose();
        stage.dispose();
    }
}
