package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;

import java.util.ArrayList;
import java.util.Collections;

public class LoadoutScene implements Screen {
    GameSuper game;
    public LoadoutScene(GameSuper game){
        this.game = game;
    }
    Color[] palette;
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Environment environment;
    PerspectiveCamera camera;
    int screenHeight, screenWidth;
    float camOffsetX = 3, camOffsetY = - 0.5f, camOffsetZ;
    GameSuper.MyShapeRenderer shapeRenderer;
    Array<GameObject2D> gui = new Array<>();
    DataStore playerData;
    FreeTypeFontGenerator fontGenerator;
    BitmapFont mediumFont, smallFont;
    Label.LabelStyle labelStyle;
    Stage stage = new Stage();
    Label sceneName, upgradeLabel, compressLabel, descriptionLabel, coinsLabel, lvlLabel;
    GameObject2D listBG, backButton, headerBG, bodySlot, scopeSlot, stockSlot, underbarrelSlot, handleSlot, descriptionBG, upgradeBtn, compressBtn, coinsBG, levelBG, levelFG;
    Vector2 areaEdge;
    Pixmap slotPixmap;
    ArrayList<WeaponModule> inventory;
    ArrayList<GameObject2D> invGui;
    int upDown = 0;
    int margin;
    ModelBatch modelBatch = new ModelBatch();
    ModelInstance weapon;
    boolean wasTouched;
    float touchTime = 0;
    WeaponModule selectedItem = null;
    float levelLength;
    Vector2 touchCoordinates;
    Stage itemStage = new Stage();


    @Override
    public void show() {
        playerData = game.playerData;
        palette = game.palette;

        /*
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 / 255f, 214 / 255f, -1f, -0.8f, -0.2f));



        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camOffsetX, camOffsetY, camOffsetZ);
        camera.lookAt(0, 1, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        shapeRenderer = new GameSuper.MyShapeRenderer();



        Pixmap pixmap;
        pixmap = new Pixmap(screenHeight / 7, screenHeight / 7, Pixmap.Format.RGB888);
        pixmap.setColor(palette[1]);
        pixmap.fill();
        pixmap.setColor(palette[5]);
        pixmap.fillRectangle(pixmap.getWidth()/2, pixmap.getHeight()/2, pixmap.getWidth()/2, pixmap.getHeight()/2);
        pixmap.setColor(palette[0]);
        pixmap.fillRectangle(pixmap.getWidth()/2, 0, pixmap.getWidth()/2, pixmap.getHeight()/2);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), 20, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 20, pixmap.getHeight() - 20, 20, palette[0]), 10, 10);
        Pixmap arrow = new Pixmap(Gdx.files.internal("arrow back.png"));
        pixmap.drawPixmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), pixmap.getWidth() / 4, pixmap.getHeight() / 4, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
        backButton = new GameObject2D(pixmap, 0, screenHeight / 7f * 6, 12);
        gui.add(backButton);
        pixmap = new Pixmap(screenWidth / 2 + 20, screenHeight, Pixmap.Format.RGB888);
        pixmap.setColor(palette[5]);
        pixmap.fill();
        pixmap.setColor(palette[0]);
        pixmap.fillRectangle(20, 0, pixmap.getWidth() - 20, pixmap.getHeight());
        listBG = new GameObject2D(pixmap, screenWidth / 2f - 20, 0, 0);
        pixmap = new Pixmap(screenWidth, backButton.getHeight(), Pixmap.Format.RGB888);
        pixmap.setColor(palette[5]);
        pixmap.fill();
        pixmap.setColor(palette[0]);
        pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight() - 10);
        headerBG = new GameObject2D(pixmap, backButton.getWidth()/2f, screenHeight - pixmap.getHeight(), 11);
        gui.add(headerBG);
        pixmap = new Pixmap(screenWidth/2/5-10, screenWidth/10, Pixmap.Format.RGB888);
        pixmap.setColor(palette[1]);
        pixmap.fill();
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), 20, palette[4]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 10, pixmap.getHeight() - 10, 20, palette[5]), 5, 5);
        pixmap.setColor(palette[7]);
        pixmap.fillCircle(pixmap.getWidth()/2,  pixmap.getHeight() + 100, 150);

        //might be commented//
        Pixmap pic = new Pixmap(Gdx.files.internal("weapons/bodies/shotgun.png"));
        pixmap.drawPixmap(pic, 0, 0, pic.getWidth(), pic.getHeight(), 0, 0, pixmap.getWidth(), pixmap.getHeight());
        //might be commented//

        areaEdge = new Vector2(screenWidth - listBG.getX(), screenHeight - headerBG.getHeight());

        bodySlot = new GameObject2D(pixmap, areaEdge.x/2f - pixmap.getWidth()/2f, areaEdge.y/2f - pixmap.getHeight()/2f, 1);
        gui.add(bodySlot);



        fontGenerator = game.fontGenerator;
        FreeTypeFontGenerator.FreeTypeFontParameter smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParameter.size = backButton.getHeight();
        smallParameter.borderColor = game.palette[4];
        smallParameter.borderWidth = 5;
        smallParameter.color = game.palette[1];
        mediumFont = fontGenerator.generateFont(smallParameter);
        */

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = mediumFont;
        sceneName = new Label("Loadout", labelStyle);
        sceneName.setPosition( (screenWidth - backButton.getWidth()*2)/2f, Gdx.graphics.getHeight() - sceneName.getHeight()/2f, Align.center);
        stage.addActor(sceneName);
        labelStyle.font = smallFont;
        compressLabel = new Label("Compress", labelStyle);
        compressLabel.setPosition(compressBtn.getX() + compressBtn.getWidth()/2f, compressBtn.getY() + compressBtn.getHeight()/2f, Align.center);
        itemStage.addActor(compressLabel);
        upgradeLabel = new Label("Upgrade", labelStyle);
        upgradeLabel.setPosition(upgradeBtn.getX() + upgradeBtn.getWidth()/2f, upgradeBtn.getY() + upgradeBtn.getHeight()/2f, Align.center);
        itemStage.addActor(upgradeLabel);
        coinsLabel = new Label(Integer.toString(playerData.coins) , labelStyle);
        coinsLabel.setPosition(coinsBG.getX() + (coinsBG.getWidth() - coinsBG.getHeight()/2f)/2f, coinsBG.getY() + coinsBG.getHeight()/2f, Align.center);
        stage.addActor(coinsLabel);
        lvlLabel = new Label("lvl. 100", labelStyle);
        lvlLabel.setPosition(levelBG.getX() + levelBG.getWidth() - 25, levelBG.getY() + levelBG.getHeight()/2f, Align.right);
        itemStage.addActor(lvlLabel);
        descriptionLabel = new Label("The description is here!", labelStyle);
        descriptionLabel.setPosition(screenWidth/2f + 20, descriptionBG.getHeight() - 20 - 20 - 20 - 20, Align.topLeft);
        itemStage.addActor(descriptionLabel);

        GameSuper.CompareGUIViaLayer comparator = new GameSuper.CompareGUIViaLayer();
        gui.sort(comparator);

        inventory = playerData.inventory;
        Collections.sort(inventory, (weaponModule, t1) -> Integer.compare(weaponModule.id, t1.id));
        invGui = new ArrayList<>();
        margin = ((screenWidth + 100)/2 - slotPixmap.getWidth()*4)/6;
        for(int i = 0; i < inventory.size(); i++){
            invGui.add(new GameObject2D(slotPixmap,
                    screenWidth/2f + (margin + slotPixmap.getWidth()) * (i%4) + margin,
                    screenHeight - headerBG.getHeight() - margin - slotPixmap.getHeight() - i/4*(margin+slotPixmap.getHeight())));
        }

        WeaponModule bodyModule = playerData.inventory.get(playerData.currentBuild[0]);
        Model model, body = bodyModule.model;
        model = WeaponModule.combine(body, new Model(), Vector3.Zero);
        weapon = new ModelInstance(model);
        weapon.transform.set(new Vector3(0, .5f, 0), new Quaternion());
        weapon.transform.rotate(Vector3.Y, 180);
        weapon.transform.rotate(Vector3.X,  -25);

        selectedItem = playerData.inventory.get(playerData.currentBuild[0]);
        descriptionLabel.setText(selectedItem.name + "\n" + selectedItem.characteristic.name + "[" + (float)Math.round(selectedItem.characteristic.value * 1000) / 1000 + "]");
        lvlLabel.setText("lvl" + selectedItem.characteristic.lvl);
        levelLength = (float)selectedItem.characteristic.xp / selectedItem.characteristic.startXP;
    }

    @Override
    public void render(float delta) {
        Color c = palette[1];
        Gdx.gl.glClearColor(c.r, c.g, c.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (Gdx.input.justTouched()){
            wasTouched = true;
            touchCoordinates = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        }

        float x = Gdx.input.getX(), y = Gdx.input.getY();
        if(Gdx.input.justTouched() && backButton.contains(x, screenHeight - y)) game.setScreen(game.menu);
        else if (!Gdx.input.isTouched() && wasTouched && touchCoordinates.equals(new Vector2(Gdx.input.getX(), Gdx.input.getY())) && Gdx.input.getX() > screenWidth/2f){
            wasTouched = false;
            Gdx.app.log("DEBUG", "Touch coords: " + Gdx.input.getX() + ", " + (screenHeight - Gdx.input.getY()));
             x = Gdx.input.getX();
             y = screenHeight - Gdx.input.getY();
            for(int i = 0; i < invGui.size(); i ++){
                GameObject2D btn = invGui.get(i);
                if(btn.contains(x, y)){
                    Gdx.app.log("", Integer.toString(i));
                    WeaponModule wm = playerData.inventory.get(i);
                    if(wm.type != 0) playerData.currentBuild[wm.type - 1] = i;
                    selectedItem = playerData.inventory.get(i);
                    lvlLabel.setText("lvl. " + selectedItem.characteristic.lvl);
                    float val = (float)Math.round(wm.characteristic.value * 1000) / 1000;
                    descriptionLabel.setText(wm.name + "\n" + wm.characteristic.name + " [" + val + "]");
                    levelLength = (float)selectedItem.characteristic.xp / selectedItem.characteristic.startXP;
                    Model model = new Model();
                    for (Node node : wm.model.nodes){
                        node.translation.set(Vector3.Zero);
                    }
                    ModelInstance w = new ModelInstance(WeaponModule.combine(new Model(), wm.model, Vector3.Zero));
                    w.transform.set(weapon.transform);
                    weapon = w;
                }
            }
        }
        else if (Gdx.input.getDeltaY() != 0 || Gdx.input.getDeltaY() != 0){
            if(Gdx.input.getX() > screenWidth / 2f) {
                float deltaY = Gdx.input.getDeltaY(), pos = screenHeight - headerBG.getHeight() - margin - slotPixmap.getHeight();
                //scrolling down deltaY > 0, up deltaY < 0
                if( (deltaY > 0 && invGui.get(0).getY() > pos) || (deltaY < 0 && invGui.get(invGui.size() - 1).getY() < pos) )
                    for (int i = 0; i < invGui.size(); i++)
                        invGui.get(i).setY(invGui.get(i).getY() - Gdx.input.getDeltaY());
            }
            else {
                weapon.transform.rotate(Vector3.Y, Gdx.input.getDeltaX());
            }
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());

        modelBatch.begin(camera);
        modelBatch.render(weapon, environment);
        modelBatch.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float radius = 20;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(palette[5]);
        shapeRenderer.rect(screenWidth/2f - 20, 0, screenWidth/2f + radius, screenHeight);
        shapeRenderer.setColor(palette[0]);
        shapeRenderer.rect(screenWidth/2f, 0, screenWidth/2f + radius, screenHeight);

        shapeRenderer.setColor(palette[5]);
        shapeRenderer.circle(0, screenHeight, screenHeight/8f);
        shapeRenderer.setColor(palette[0]);
        shapeRenderer.circle(0, screenHeight, screenHeight/8f - 20);
        shapeRenderer.end();

        spriteBatch.begin();
        spriteBatch.draw(listBG);

        spriteBatch.draw(bodySlot);
        spriteBatch.draw(stockSlot);
        spriteBatch.draw(scopeSlot);
        spriteBatch.draw(handleSlot);
        spriteBatch.draw(underbarrelSlot);

        for(int i = 0; i < inventory.size(); i++){
            //invGui.get(i).setY(invGui.get(i).getY());
            GameObject2D o = invGui.get(i);
            spriteBatch.draw(o, o.getX(), o.getY() + upDown);
            Texture p = inventory.get(i).picture;
            spriteBatch.draw(p, o.getX(), o.getY() + upDown, o.getWidth(), o.getHeight(), 0, 0, p.getWidth(), p.getHeight(), false, false);
        }

        spriteBatch.draw(headerBG);
        spriteBatch.draw(backButton);
        int[] i = playerData.currentBuild;
        spriteBatch.draw(playerData.inventory.get(i[0]).picture, bodySlot.getX(), bodySlot.getY(), bodySlot.getWidth(), bodySlot.getHeight());
        spriteBatch.draw(playerData.inventory.get(i[1]).picture, underbarrelSlot.getX(), underbarrelSlot.getY(), underbarrelSlot.getWidth(), underbarrelSlot.getHeight());
        spriteBatch.draw(playerData.inventory.get(i[2]).picture, stockSlot.getX(), stockSlot.getY(), stockSlot.getWidth(), stockSlot.getHeight());
        spriteBatch.draw(playerData.inventory.get(i[3]).picture, scopeSlot.getX(), scopeSlot.getY(), scopeSlot.getWidth(), scopeSlot.getHeight());
        spriteBatch.draw(playerData.inventory.get(i[4]).picture, handleSlot.getX(), handleSlot.getY(), handleSlot.getWidth(), handleSlot.getHeight());

        if (selectedItem != null) {
            spriteBatch.draw(descriptionBG);
            spriteBatch.draw(upgradeBtn);
            spriteBatch.draw(compressBtn);
            spriteBatch.draw(levelBG);
        }

        spriteBatch.draw(coinsBG);

        spriteBatch.end();

        if (selectedItem != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(palette[0]);
            shapeRenderer.circle(levelBG.getX() + levelBG.getHeight() / 2f, levelBG.getY() + levelBG.getHeight() / 2f, levelBG.getHeight() / 2f - 5);
            shapeRenderer.rect(levelBG.getX() + levelBG.getHeight() / 2f + 5, levelBG.getY() + 5, (levelBG.getWidth() - levelBG.getHeight()) * levelLength - 5, levelBG.getHeight() - 10);
            shapeRenderer.circle(levelBG.getX() + levelBG.getHeight() / 2f + (levelBG.getWidth() - levelBG.getHeight()) * levelLength, levelBG.getY() + levelBG.getHeight() / 2f, levelBG.getHeight() / 2f - 5);
            shapeRenderer.end();

            itemStage.draw();
        }

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
        selectedItem = null;
        lvlLabel.setText("");
        upgradeLabel.setText("");
        compressLabel.setText("");
        descriptionLabel.setText("");
    }

    @Override
    public void dispose() {

    }
}
