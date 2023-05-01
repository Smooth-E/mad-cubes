package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


import java.util.ArrayList;

public class RoomChoose implements Screen {

    RoomChoose(GameSuper game){
        this.game = game;
    }
    GameSuper game;
    static Color[] palette;
    Stage stage = new Stage();
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    PerspectiveCamera camera;
    Environment environment;
    int screenHeight, screenWidth;
    int outlineWidth = 10, cornerRadius = 20, margin = 40;
    GameObject2D escapeBtn, refreshBtn, refreshDecal, headerBG, hostBG, searchBar, leftBG, rightBG;
    Label headerLabel, previousLabel, nextLabel, tryToUpdateLabel;
    float rotation = 0;
    static boolean receiving = false, generateList = false;
    static Networking.UpdateRoomListResponse roomResponse;
    TextField searchField;
    String filter;
    GameObject2D mapBG1, mapPic1, mapBG2, mapPic2, mapBG3, mapPic3;
    Label mapName1, mapCapacity1, mapID1, mapName2, mapCapacity2, mapID2, mapName3, mapCapacity3, mapID3;
    Pixmap mapPicTemplate;
    int pageIndex = 0;
    static ArrayList<Networking.Room> results;
    BitmapFont verySmallFont;
    GameObject2D waitingServer, loading;
    int roomID = -1, mapID;
    ModelBatch modelBatch = new ModelBatch();
    float mapAngle = 0;
    boolean isHosting = false;
    GameObject2D hostPlate, hostGo, hostCancel, hostLeft, hostRight, hostMapNameBG, hostCapacityBG, hostMinus, hostPlus, hostHider;
    Label hostHeader, hostMapHeader, hostMapName, hostCapacityHeader, hostCapacity, hostGoLabel;
    Stage hostStage = new Stage();
    int hostMapID = 0, hostCapacityAmount = 2;
    Environment hostEnvironment;
    PerspectiveCamera hostCamera;


    @Override
    public void show() {

        palette = game.palette;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        margin = screenHeight/108;
        outlineWidth = margin;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = screenWidth/4/10;
        parameter.color = palette[0];
        parameter.borderColor = palette[5];
        parameter.borderWidth = 5;
        verySmallFont = game.fontGenerator.generateFont(parameter);

        Pixmap pixmap = new Pixmap(screenHeight/8, screenHeight/8, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(screenHeight/8, screenHeight/8, cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(screenHeight/8 - 2*outlineWidth, screenHeight/8 -  2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        Pixmap bg = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        bg.drawPixmap(pixmap, 0, 0);
        Pixmap back = new Pixmap(Gdx.files.internal("arrow back.png"));
        pixmap.drawPixmap(back, 0, 0, back.getWidth(), back.getHeight(),
                outlineWidth + 2*margin, outlineWidth + 2*margin, pixmap.getWidth() - 4*margin - 2*outlineWidth, pixmap.getHeight() - 4*margin - 2*outlineWidth);
        escapeBtn = new GameObject2D(pixmap, 0, screenHeight - pixmap.getHeight());
        refreshBtn = new GameObject2D(bg, screenWidth - bg.getWidth(), screenHeight - bg.getHeight());

        pixmap = new Pixmap(screenWidth - pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(palette[5]);
        pixmap.fill();
        pixmap.setColor(palette[0]);
        pixmap.fillRectangle(0, outlineWidth, pixmap.getWidth(), pixmap.getHeight() - 2*outlineWidth);
        headerBG = new GameObject2D(pixmap, bg.getWidth()/2f, screenHeight - pixmap.getHeight());

        pixmap = new Pixmap(bg.getWidth(), bg.getHeight(), Pixmap.Format.RGBA8888);
        back = new Pixmap(Gdx.files.internal("refresh.png"));
        pixmap.drawPixmap(back, 0, 0, back.getWidth(), back.getHeight(),
                outlineWidth + 2*margin, outlineWidth + 2*margin, pixmap.getWidth() - outlineWidth - 4*margin, pixmap.getHeight() - outlineWidth - 4*margin);
        refreshDecal = new GameObject2D(pixmap, refreshBtn.getX(), refreshBtn.getY());

        pixmap = new Pixmap(bg.getWidth(), bg.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(bg, 0, 0);
        pixmap.drawPixmap(
                new Pixmap(Gdx.files.internal("plus_sign.png")), 0, 0, 410, 410,
                outlineWidth + 2*margin, outlineWidth + 2*margin, bg.getWidth() - 2*outlineWidth - 4*margin, bg.getHeight() - 2*outlineWidth - 4*margin
        );
        hostBG = new GameObject2D(pixmap, screenWidth - pixmap.getWidth(), 0);

        pixmap = new Pixmap(screenWidth - hostBG.getWidth() + outlineWidth, hostBG.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + outlineWidth, pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.setColor(palette[5]);
        pixmap.fillRectangle(pixmap.getWidth()/2, 0, pixmap.getWidth(), pixmap.getHeight());
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + outlineWidth - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]),
                outlineWidth, outlineWidth);
        pixmap.setColor(palette[0]);
        pixmap.fillRectangle(pixmap.getWidth()/2, outlineWidth, pixmap.getWidth(), pixmap.getHeight() - 2*outlineWidth);
        /*
        pixmap.drawPixmap(
                new Pixmap(Gdx.files.internal("search.png")), 0, 0, 360, 360,
                outlineWidth + 2*margin, outlineWidth + 2*margin, pixmap.getHeight() - 4*margin - 2*outlineWidth, pixmap.getHeight() - 4*margin - 2*outlineWidth
        );
        */
        searchBar = new GameObject2D(pixmap, 0, 0);

        Label.LabelStyle style = new Label.LabelStyle(game.loadoutScene.mediumFont, Color.WHITE);
        headerLabel = new Label("servers", style);
        headerLabel.setAlignment(Align.center);
        headerLabel.setPosition(screenWidth/2f, headerBG.getY() + headerBG.getHeight()/2f, Align.center);
        stage.addActor(headerLabel);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = new TextureRegionDrawable(new Texture(new Pixmap(searchBar.getWidth() - escapeBtn.getWidth(),
                searchBar.getHeight() - 2*outlineWidth,
                Pixmap.Format.RGBA8888)));
        //textFieldStyle.cursor = new TextureRegionDrawable(new Texture(new Pixmap(0, 0, Pixmap.Format.RGB888)));
        textFieldStyle.disabledBackground = new TextureRegionDrawable(new Texture(new Pixmap(0, 0, Pixmap.Format.RGB888)));
        textFieldStyle.font = game.loadoutScene.smallFont;
        textFieldStyle.fontColor = Color.WHITE;
        searchField = new TextField("", textFieldStyle);
        searchField.setAlignment(Align.left);
        searchField.setPosition(hostBG.getWidth() - outlineWidth, outlineWidth, Align.bottomLeft);
        searchField.setSize(searchBar.getWidth() - escapeBtn.getWidth(), searchBar.getHeight() - 2*outlineWidth);
        stage.addActor(searchField);

        tryToUpdateLabel = new Label("maybe try to update the list...", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        tryToUpdateLabel.setPosition(screenWidth/2f, screenHeight/2f, Align.center);
        tryToUpdateLabel.setAlignment(Align.center);
        stage.addActor(tryToUpdateLabel);

        previousLabel = new Label("p\nr\ne\nv", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        previousLabel.setPosition(previousLabel.getWidth()/2f + margin*2, screenHeight/2f, Align.center);
        previousLabel.setAlignment(Align.center);
        stage.addActor(previousLabel);

        pixmap = new Pixmap( (int)(previousLabel.getWidth() + 4*margin + 2*outlineWidth)*2, screenHeight/2 + 4*margin + 2*outlineWidth, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        leftBG = new GameObject2D(pixmap, -pixmap.getWidth()/2f, screenHeight/2f - pixmap.getHeight()/2f);

        nextLabel = new Label("n\ne\nx\nt", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        nextLabel.setPosition(screenWidth - nextLabel.getWidth()/2f - 2*margin, screenHeight/2f, Align.center);
        nextLabel.setAlignment(Align.center);
        stage.addActor(nextLabel);

        rightBG = new GameObject2D(pixmap, screenWidth - pixmap.getWidth()/2f, screenHeight/2f - pixmap.getHeight()/2f);

        pixmap = new Pixmap( (screenWidth - leftBG.getWidth()*2 - 4*2*margin)/3, leftBG.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        mapBG1 = new GameObject2D(pixmap, leftBG.getWidth() + 2*margin, leftBG.getY());
        mapBG2 = new GameObject2D(pixmap, mapBG1.getX() + mapBG1.getWidth() + 2*margin, leftBG.getY());
        mapBG3 = new GameObject2D(pixmap, mapBG2.getX() + mapBG2.getWidth() + 2*margin, leftBG.getY());

        mapPicTemplate = new Pixmap(mapBG1.getWidth(), mapBG1.getWidth(), Pixmap.Format.RGBA8888);
        mapPicTemplate.drawPixmap(GameSuper.createRoundedRectangle(mapPicTemplate.getWidth(), mapPicTemplate.getHeight(), cornerRadius, palette[5]), 0, 0);

        mapPic1 = new GameObject2D(mapPicTemplate, mapBG1.getX(), mapBG1.getY());
        mapPic2 = new GameObject2D(mapPicTemplate, mapBG2.getX(), mapBG2.getY());
        mapPic3 = new GameObject2D(mapPicTemplate, mapBG3.getX(), mapBG3.getY());

        mapName1 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapName1.setPosition(mapBG1.getX() + outlineWidth + margin, mapBG1.getY() + mapBG1.getHeight() - outlineWidth, Align.topLeft);
        mapName1.setAlignment(Align.left);
        mapName1.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapName1);

        mapName2 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapName2.setPosition(mapBG2.getX() + outlineWidth + margin, mapBG1.getY() + mapBG1.getHeight() - outlineWidth, Align.topLeft);
        mapName2.setAlignment(Align.left);
        mapName2.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapName2);

        mapName3 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapName3.setPosition(mapBG3.getX() + margin + outlineWidth, mapBG1.getY() + mapBG1.getHeight() - outlineWidth, Align.topLeft);
        mapName3.setAlignment(Align.left);
        mapName3.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapName3);

        mapCapacity1 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapCapacity1.setPosition(mapName1.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight() - margin, Align.topLeft);
        mapCapacity1.setAlignment(Align.left);
        mapCapacity1.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapCapacity1);

        mapID1 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapID1.setPosition(mapName1.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight()*2 - margin*2, Align.topLeft);
        mapID1.setAlignment(Align.left);
        mapID1.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapID1);

        mapCapacity2 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapCapacity2.setPosition(mapName2.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight() - margin, Align.topLeft);
        mapCapacity2.setAlignment(Align.left);
        mapCapacity2.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapCapacity2);

        mapID2 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapID2.setPosition(mapName2.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight()*2 - margin*2, Align.topLeft);
        mapID2.setAlignment(Align.left);
        mapID2.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapID2);

        mapCapacity3 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapCapacity3.setPosition(mapName3.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight() - margin, Align.topLeft);
        mapCapacity3.setAlignment(Align.left);
        mapCapacity3.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapCapacity3);

        mapID3 = new Label("", new Label.LabelStyle(verySmallFont, Color.WHITE));
        mapID3.setPosition(mapName3.getX(), mapName1.getY() - mapName1.getStyle().font.getLineHeight()*2 - margin*2, Align.topLeft);
        mapID3.setAlignment(Align.left);
        mapID3.setSize(mapBG1.getWidth() - 2*outlineWidth, mapName1.getHeight());
        stage.addActor(mapID3);

        pixmap = new Pixmap(screenHeight/4, screenHeight/4, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        waitingServer = new GameObject2D(pixmap, screenWidth/2f - pixmap.getWidth()/2f, screenHeight/2f - pixmap.getHeight()/2f);

        pixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("loading.png")), 0, 0, 660, 660,
                outlineWidth + margin*2, outlineWidth + margin*2, pixmap.getWidth() - outlineWidth*2 - 4*margin, pixmap.getHeight() - 2*outlineWidth - 4*margin);
        loading = new GameObject2D(pixmap, screenWidth/2f - pixmap.getWidth()/2f, screenHeight/2f - pixmap.getHeight()/2f);

        pixmap = new Pixmap(screenHeight - 2*margin*4, screenHeight - 2*margin*4, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.setColor(palette[5]);
        pixmap.fillRectangle(pixmap.getWidth()/2 - outlineWidth/2, 0, outlineWidth, pixmap.getHeight());
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight()/5, cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight()/5 - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        hostPlate = new GameObject2D(pixmap, (screenWidth - pixmap.getWidth())/2f, (screenHeight - pixmap.getWidth())/2f);

        pixmap = new Pixmap(pixmap.getHeight()/5/2, pixmap.getHeight()/5/2, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("cross.png")), 0, 0, 982, 982,
                outlineWidth + margin, outlineWidth + margin, pixmap.getWidth() -2*outlineWidth - 2*margin, pixmap.getHeight() - 2*outlineWidth - 2*margin);
        hostCancel = new GameObject2D(pixmap, hostPlate.getX() - margin - pixmap.getWidth(), hostPlate.getY() + hostPlate.getHeight() - pixmap.getHeight());

        pixmap = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(palette[0].r, palette[0].g, palette[0].b, .5f);
        pixmap.fill();
        hostHider = new GameObject2D(pixmap, 0, 0);

        pixmap = new Pixmap(hostPlate.getWidth()/2 + outlineWidth/2, hostPlate.getHeight()/5, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        hostGo = new GameObject2D(pixmap, hostPlate.getX() + hostPlate.getWidth()/2f - outlineWidth/2f, hostPlate.getY());

        hostHeader = new Label("host room", headerLabel.getStyle());
        hostHeader.setAlignment(Align.center);
        hostHeader.setWidth(hostPlate.getWidth());
        hostHeader.setHeight(hostPlate.getHeight()/5f);
        hostHeader.setPosition(hostPlate.getX(), hostPlate.getY() + hostPlate.getHeight()/5f*4, Align.bottomLeft);
        hostStage.addActor(hostHeader);

        hostGoLabel = new Label("host", headerLabel.getStyle());
        hostGoLabel.setSize(hostGo.getWidth(), hostGo.getHeight());
        hostGoLabel.setAlignment(Align.center);
        hostGoLabel.setPosition(hostGo.getX(), hostGo.getY(), Align.bottomLeft);
        hostStage.addActor(hostGoLabel);

        hostMapHeader = new Label("Map\nedgy square", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        hostMapHeader.setAlignment(Align.center);
        hostMapHeader.setSize(hostPlate.getWidth()/2f - 1.5f*outlineWidth, hostPlate.getHeight()/5f);
        hostMapHeader.setPosition(hostPlate.getX() + outlineWidth + margin, hostPlate.getY() + hostPlate.getHeight()/5f*3, Align.bottomLeft);
        hostStage.addActor(hostMapHeader);

        pixmap = new Pixmap(hostCancel.getWidth(), hostCancel.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("play.png")), 0, 0, 175, 175,
                outlineWidth + margin, outlineWidth + margin, pixmap.getWidth() -2*outlineWidth - 2*margin, pixmap.getHeight() - 2*outlineWidth - 2*margin);
        hostRight = new GameObject2D(pixmap, hostPlate.getX() + hostPlate.getWidth()/2f - outlineWidth/2f - margin - pixmap.getWidth(),
                hostPlate.getHeight()/5f*2 + (hostPlate.getHeight()/5f - pixmap.getHeight()));

        Pixmap newPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                newPixmap.drawPixel(x, y, pixmap.getPixel(pixmap.getWidth() - 1 - x, y));
            }
        }
        hostLeft = new GameObject2D(newPixmap, hostPlate.getX() + outlineWidth + margin, hostRight.getY());

        hostCapacity = new Label("capacity\n 2 players", hostMapHeader.getStyle());
        hostCapacity.setSize(hostMapHeader.getWidth(), hostMapHeader.getHeight());
        hostCapacity.setAlignment(Align.center);
        hostCapacity.setPosition(hostMapHeader.getX(Align.bottomLeft), hostPlate.getY() + hostPlate.getHeight()/5f, Align.bottomLeft);
        hostStage.addActor(hostCapacity);

        pixmap = new Pixmap(hostLeft.getWidth(), hostLeft.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("plus_sign.png")), 0, 0, 410, 410,
                outlineWidth + margin, outlineWidth + margin, pixmap.getWidth() -2*outlineWidth - 2*margin, pixmap.getHeight() - 2*outlineWidth - 2*margin);
        hostPlus = new GameObject2D(pixmap, hostRight.getX(), hostPlate.getY() + (hostPlate.getHeight()/5f - pixmap.getHeight()));

        pixmap = new Pixmap(hostLeft.getWidth(), hostLeft.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("minus_sign.png")), 0, 0, 410, 410,
                outlineWidth + margin, outlineWidth + margin, pixmap.getWidth() -2*outlineWidth - 2*margin, pixmap.getHeight() - 2*outlineWidth - 2*margin);
        hostMinus = new GameObject2D(pixmap, hostLeft.getX(), hostPlus.getY());

        if (roomResponse == null) {
            game.client.sendTCP(new Networking.UpdateRoomListRequest());
            receiving = true;
        }

        Gdx.input.setInputProcessor(stage);

        pixmap.dispose();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 /255f,214 /255f, -1f, -0.8f, -0.2f));

        hostEnvironment = new Environment();
        hostEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        hostEnvironment.add(new DirectionalLight().set(0.8f, 236 /255f,214 /255f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(5, 5, 0);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        hostCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hostCamera.position.set(5, 45, 0);
        hostCamera.lookAt(0, 40,0);
        hostCamera.near = 1f;
        hostCamera.far = 300f;
        hostCamera.update();
    }

    @Override
    public void render(float delta) {

        Color c = game.palette[1];
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();

        if (roomID != -1){
            stage.clear();
            hostStage.clear();
            game.setScreen(new MultiplayerScene(game, roomID, mapID));
        }

        int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
        if (Gdx.input.justTouched()) {
            if (!isHosting) {
                if (escapeBtn.contains(x, y)) game.setScreen(game.menu);
                else if (hostBG.contains(x, y)) isHosting = true;
                else if (refreshBtn.contains(x, y)) {
                    game.client.sendTCP(new Networking.UpdateRoomListRequest());
                    pageIndex = 0;
                    receiving = true;
                } else if (leftBG.contains(x, y) && results != null) {
                    if (pageIndex > 0) pageIndex--;
                    else pageIndex = results.size() / 3;
                } else if (rightBG.contains(x, y) && results != null) {
                    if (pageIndex * 3 < results.size() - 2) pageIndex++;
                    else pageIndex = 0;
                } else if (!receiving) {
                    if (mapBG1.isActive() && mapBG1.contains(x, y)) {
                        game.client.sendTCP(new Networking.JoinRequest(game.UID, pageIndex * 3));
                        receiving = true;
                    } else if (mapBG2.isActive() && mapBG2.contains(x, y)) {
                        game.client.sendTCP(new Networking.JoinRequest(game.UID, pageIndex * 3 + 1));
                        receiving = true;
                    } else if (mapBG3.isActive() && mapBG3.contains(x, y)) {
                        game.client.sendTCP(new Networking.JoinRequest(game.UID, pageIndex * 3 + 2));
                        receiving = true;
                    }
                }
            }
            else {
                if (hostCancel.contains(x, y)){
                    isHosting = false;
                }
                else if (hostLeft.contains(x, y)) {
                    if (hostMapID == 0) hostMapID = GameSuper.maps.length - 1;
                    else hostMapID --;
                }
                else if (hostRight.contains(x, y)) {
                    if (hostMapID == GameSuper.maps.length - 1) hostMapID = 0;
                    else hostMapID ++;
                }
                else if (hostPlus.contains(x, y)) hostCapacityAmount ++;
                else if (hostMinus.contains(x, y) && hostCapacityAmount > 1) hostCapacityAmount --;
                else if (hostGo.contains(x, y)) {
                    game.client.sendTCP(new Networking.RoomRequest(game.UID, hostCapacityAmount, hostMapID));
                    receiving = true;
                    isHosting = false;
                }
            }
        }

        if (roomResponse != null) {
            if (!searchField.getText().equals("")) {
                if (searchField.getText().length() > "search it ya know ".length())
                    searchField.setText(searchField.getText().substring(0, searchField.getText().length() - 2));
                filter = searchField.getText();
                headerLabel.setText(filter);
                results.clear();
                for (Networking.Room room : roomResponse.rooms) {
                    if (Integer.toString(room.roomID).contains(filter)) results.add(room);
                }
            }
            else {
                headerLabel.setText("servers");
                results = roomResponse.rooms;
            }
        }

        if (results != null && results.size() > 0) {
            tryToUpdateLabel.setText("");
            mapName1.setText(GameSuper.maps[results.get(pageIndex*3).mapID].name);
            mapCapacity1.setText("players: " + results.get(pageIndex*3).players.size() + " | " + results.get(pageIndex*3).capacity);
            mapID1.setText("ID: " + results.get(pageIndex*3).roomID);

            if (results.size() - 1 >= pageIndex*3 + 1) {
                mapBG2.setActive(true);
                mapName2.setText(GameSuper.maps[results.get(pageIndex*3 + 1).mapID].name);
                mapCapacity2.setText("players: " + results.get(pageIndex*3 + 1).players.size() + " | " + results.get(pageIndex*3 + 1).capacity);
                mapID2.setText("ID: " + results.get(pageIndex*3 + 1).roomID);
            }
            else {
                mapBG2.setActive(false);
                mapName2.setText("");
                mapCapacity2.setText("");
                mapID2.setText("");
            }

            if (results.size() - 1 >= pageIndex*3 + 2) {
                mapBG3.setActive(true);
                mapName3.setText(GameSuper.maps[results.get(pageIndex*3 + 2).mapID].name);
                mapCapacity3.setText("players: " + results.get(pageIndex*3 + 2).players.size() + " | " + results.get(pageIndex*3 + 2).capacity);
                mapID3.setText("ID: " + results.get(pageIndex*3 + 2).roomID);
            }
            else {
                mapBG3.setActive(false);
                mapName3.setText("");
                mapCapacity3.setText("");
                mapID3.setText("");
            }

        }
        else {
            mapBG1.setActive(false);
            mapBG2.setActive(false);
            mapBG3.setActive(false);
            tryToUpdateLabel.setText("try to update the list\nor host the room by yourself");
            Gdx.app.log("", "roomresponse is null");
        }

        /*
        if (results == null) {
            tryToUpdateLabel.setPosition(screenWidth/2f, screenHeight/2f, Align.center);
            mapBG1.setActive(false);
            mapBG2.setActive(false);
            mapBG3.setActive(false);
        }
        else {
            tryToUpdateLabel.setX(screenWidth*8);
        }
         */

        spriteBatch.begin();
        spriteBatch.draw(headerBG);
        spriteBatch.draw(searchBar);
        spriteBatch.draw(hostBG);
        spriteBatch.draw(leftBG);
        spriteBatch.draw(rightBG);
        spriteBatch.end();

        spriteBatch.begin();
        spriteBatch.draw(mapBG1);
        spriteBatch.draw(mapBG2);
        spriteBatch.draw(mapBG3);

        spriteBatch.draw(escapeBtn);
        spriteBatch.draw(refreshBtn);
        spriteBatch.draw(refreshDecal);
        spriteBatch.end();

        mapAngle += 1;
        if (roomResponse != null && roomResponse.rooms.size() >= pageIndex*3+1) {
            Gdx.gl.glViewport((int) mapBG1.getX() + outlineWidth,
                    (int) mapBG1.getY() + outlineWidth,
                    mapBG1.getWidth() - 2 * outlineWidth,
                    (int) (mapBG1.getHeight() - outlineWidth - verySmallFont.getLineHeight() * 3 - margin * 3));
            camera.viewportWidth = mapBG1.getWidth() - 2 * outlineWidth;
            camera.viewportHeight = (int) (mapBG1.getHeight() - outlineWidth - verySmallFont.getLineHeight() * 3 - margin * 3);

            ModelInstance i = new ModelInstance(GameSuper.maps[roomResponse.rooms.get(pageIndex * 3).mapID].map);
            i.transform.rotate(Vector3.Y, mapAngle);
            i.transform.scale(.5f, .5f, .5f);

            camera.update();
            modelBatch.begin(camera);
            modelBatch.render(i, environment);
            modelBatch.end();

            if (roomResponse.rooms.size() >= pageIndex * 3 + 2) {
                Gdx.gl.glViewport((int) mapBG2.getX() + outlineWidth,
                        (int) mapBG2.getY() + outlineWidth,
                        mapBG2.getWidth() - 2 * outlineWidth,
                        (int) (mapBG2.getHeight() - verySmallFont.getLineHeight() * 3 - outlineWidth - margin * 3));
                i = new ModelInstance(GameSuper.maps[roomResponse.rooms.get(pageIndex * 3 + 1).mapID].map);
                i.transform.rotate(Vector3.Y, mapAngle);
                i.transform.scale(.5f, .5f, .5f);

                camera.update();
                modelBatch.begin(camera);
                modelBatch.render(i, environment);
                modelBatch.end();

                if (roomResponse.rooms.size() >= pageIndex * 3 + 3) {
                    Gdx.gl.glViewport((int) mapBG3.getX() + outlineWidth,
                            (int) mapBG3.getY() + outlineWidth,
                            mapBG2.getWidth() - 2 * outlineWidth,
                            (int) (mapBG2.getHeight() - verySmallFont.getLineHeight() * 3 - outlineWidth - margin * 3));
                    i = new ModelInstance(GameSuper.maps[roomResponse.rooms.get(pageIndex * 3 + 1).mapID].map);
                    i.transform.rotate(Vector3.Y, mapAngle);
                    i.transform.scale(.5f, .5f, .5f);

                    camera.update();
                    modelBatch.begin(camera);
                    modelBatch.render(i, environment);
                    modelBatch.end();
                }
            }
        }
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        spriteBatch.begin();
        if (receiving) {
            spriteBatch.draw(waitingServer);
            spriteBatch.draw(new TextureRegion(loading), loading.getX(), loading.getY(),
                    loading.getWidth() / 2f, loading.getHeight() / 2f,
                    loading.getWidth(), loading.getHeight(), 1, 1, rotation);
            rotation -= delta*500;
        }
        spriteBatch.end();

        //stage.act();
        stage.draw();

        if (isHosting) {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            spriteBatch.begin();
            spriteBatch.draw(hostHider);
            spriteBatch.draw(hostPlate);
            spriteBatch.draw(hostCancel);
            spriteBatch.draw(hostGo);
            spriteBatch.draw(hostRight);
            spriteBatch.draw(hostLeft);
            spriteBatch.draw(hostMinus);
            spriteBatch.draw(hostPlus);
            spriteBatch.end();

            hostMapHeader.setText("map\n" + GameSuper.maps[hostMapID].name);
            hostCapacity.setText("capacity\n" + hostCapacityAmount + " players");

            hostStage.act();
            hostStage.draw();

            Gdx.gl.glViewport((int) hostGo.getX() + outlineWidth, (int) hostGo.getY() + hostGo.getHeight(), hostGo.getWidth() - 2*outlineWidth, hostPlate.getHeight()/5*4 - hostGo.getHeight());
            camera.viewportHeight = hostPlate.getHeight()/5*4 - hostGo.getHeight();
            camera.viewportWidth = hostGo.getWidth() - 2*outlineWidth;
            camera.update();

            modelBatch.begin(camera);
            ModelInstance i = new ModelInstance(GameSuper.maps[hostMapID].map);
            i.transform.rotate(Vector3.Y, mapAngle);
            i.transform.translate(0, 0, 0);
            i.transform.scale(.5f, .5f, .5f);
            modelBatch.render(i, environment);
            modelBatch.end();
        }
    }

    public void clearLabels(){
        mapName1.setText("");
        mapName2.setText("");
        mapName3.setText("");
        mapID1.setText("");
        mapID2.setText("");
        mapID3.setText("");
        mapCapacity1.setText("");
        mapCapacity2.setText("");
        mapCapacity3.setText("");
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
        stage.clear();
        hostStage.clear();
    }

    @Override
    public void dispose() {

    }
}
