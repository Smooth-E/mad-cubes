package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import org.omg.CORBA.Environment;

import java.util.ArrayList;

public class RoomList implements Screen {

    RoomList(GameSuper game){
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
    GameObject2D escapeBtn, host, refreshBtn, refreshDecal, headerBG;
    Label headerLabel, hostLabel;
    float rotation = 0;
    static boolean receiving = false, generateList = false;
    static Networking.UpdateRoomListResponse roomResponse;
    float scroll;
    ArrayList<GameObject2D> roomBGs = new ArrayList<>();
    Pixmap roomBG;
    Stage lowerStage = new Stage();
    Pixmap mask;

    static class RoomEntry {
        Networking.Room roomData;
        Stage stage = new Stage();
        GameObject2D base;
        protected int margin = 20;
        protected int outlineWidth = 20;
        protected int cornerRadius;

        RoomEntry(Networking.Room room) {
            Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 4, Pixmap.Format.RGBA8888);
            pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*margin, pixmap.getHeight() - 2*margin, cornerRadius, RoomList.palette[4]), margin, margin);
            pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*margin - 2*outlineWidth,
                    pixmap.getHeight() - 2*margin - 2*outlineWidth, cornerRadius, RoomList.palette[0]), margin + outlineWidth, margin + outlineWidth);

            //base = new GameObject2D();
        }
    }


    @Override
    public void show() {
        palette = game.palette;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        margin = screenHeight/108;
        outlineWidth = margin;

        Pixmap pixmap = new Pixmap(screenHeight/8, screenHeight/8, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(screenHeight/8, screenHeight/8, cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(screenHeight/8 - 2*outlineWidth, screenHeight/8 -  2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        Pixmap bg = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);
        bg.drawPixmap(pixmap, 0, 0);
        Pixmap back = new Pixmap(Gdx.files.internal("arrow back.png"));
        pixmap.drawPixmap(back, 0, 0, back.getWidth(), back.getHeight(), (int)(outlineWidth*5), (int)(outlineWidth*5), (int)(pixmap.getWidth() - 5*2*outlineWidth), (int)(pixmap.getHeight() - 5*2*outlineWidth));
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
        pixmap.drawPixmap(back, 0, 0, back.getWidth(), back.getHeight(), outlineWidth*5, outlineWidth*5, pixmap.getWidth() - outlineWidth *5*2, pixmap.getHeight() - outlineWidth *5*2);
        refreshDecal = new GameObject2D(pixmap, refreshBtn.getX(), refreshBtn.getY());

        Label.LabelStyle style = new Label.LabelStyle(game.loadoutScene.mediumFont, Color.WHITE);
        headerLabel = new Label("servers", style);
        headerLabel.setAlignment(Align.center);
        headerLabel.setPosition(screenWidth/2f, headerBG.getY() + headerBG.getHeight()/2f, Align.center);
        stage.addActor(headerLabel);

        pixmap = new Pixmap(screenWidth - 2*margin, screenHeight/4 - 2*margin, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        host = new GameObject2D(pixmap, margin, screenHeight - headerBG.getHeight() - margin - pixmap.getHeight());

        hostLabel = new Label("host room", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        hostLabel.setAlignment(Align.center);
        hostLabel.setPosition(host.getX() + host.getWidth()/2f, host.getY() + host.getHeight()/2f, Align.center);
        lowerStage.addActor(hostLabel);

        roomBG = new Pixmap(host.getWidth(), host.getHeight(), Pixmap.Format.RGBA8888);
        roomBG.drawPixmap(pixmap, 0, 0);

        /*
        mask = new Pixmap(roomBG.getHeight() - outlineWidth*2, roomBG.getHeight() - outlineWidth*2, Pixmap.Format.RGBA8888);
        mask.setColor(Color.rgba8888(new Color(1,0,0,0)));
        mask.fill();
        mask.drawPixmap(GameSuper.createRoundedRectangle(mask.getWidth(), mask.getHeight(), cornerRadius, new Color(0,1,0,0)), 0, 0);
        for(int x = 0; x < mask.getWidth(); x ++){
            for (int y = 0; y < mask.getHeight(); y ++){
                if(mask.getPixel(x, y) == Color.rgba8888(new Color(0,1,0,0))) mask.drawPixel(x, y, Color.rgba8888(new Color()));
                else if(mask.getPixel(x, y) == Color.rgba8888(new Color(1,0,0,0))) mask.drawPixel(x, y, Color.rgba8888(new Color(0,0,0,0)));
            }
        }
        mask.setBlending(Pixmap.Blending.None);
         */

        if (roomResponse == null) {
            game.client.sendTCP(new Networking.UpdateRoomListRequest());
            receiving = true;
        }
    }

    @Override
    public void render(float delta) {

        if (generateList && roomResponse != null) {
            //for (Actor a : lowerStage.getActors()) a.addAction(Actions.removeActor());
            lowerStage = new Stage();
            roomBGs.clear();
            scroll = 0;
            lowerStage.addActor(hostLabel);
            hostLabel.setPosition(host.getX() + host.getWidth()/2f, host.getY() + host.getHeight()/2f, Align.center);
            host.setY(screenHeight - headerBG.getHeight() - margin - host.getHeight());
            ArrayList<Networking.Room> rooms = roomResponse.rooms;
            for(int i = 0; i < rooms.size(); i ++) {
                int index = roomBGs.size();
                GameSuper.MapData mapData = GameSuper.maps[rooms.get(i).mapID];

                Pixmap bg = new Pixmap(roomBG.getWidth(), roomBG.getHeight(), Pixmap.Format.RGBA8888);
                bg.drawPixmap(roomBG, 0, 0);

                Pixmap pic = new Pixmap(roomBG.getHeight() - outlineWidth*2, roomBG.getHeight() - outlineWidth*2, Pixmap.Format.RGBA8888);
                pic.drawPixmap(mapData.picture, 0, 0, mapData.picture.getWidth(),mapData.picture.getHeight(), 0, 0, roomBG.getHeight() - outlineWidth*2, roomBG.getHeight() - outlineWidth*2);
                //pic.drawPixmap(mask, 0, 0);

                bg.drawPixmap(pic, bg.getWidth() - bg.getHeight() + outlineWidth/2, outlineWidth);

                roomBGs.add(new GameObject2D(bg, host.getX(), screenHeight - headerBG.getHeight() - margin - host.getHeight() - (host.getHeight() + margin)*i - host.getHeight() - margin));

                Label mapName = new Label(mapData.name + " " + i + "/" + (rooms.size() - 1), new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
                mapName.setAlignment(Align.left);
                mapName.setPosition( ( /*roomBGs.get(i).getX()*/ host.getX()) + outlineWidth + margin,
                        /*roomBGs.get(i). getY()*/ ( screenHeight - headerBG.getHeight() - margin - host.getHeight() - (host.getHeight() + margin)*i - host.getHeight() - margin ) + bg.getHeight() - margin*2  - outlineWidth*2 - mapName.getHeight()/2);
                lowerStage.addActor(mapName);
                Label playerCount = new Label("Players: " + rooms.get(i).players.size() + " | " + rooms.get(i).capacity + "   ID: " + rooms.get(i).roomID,
                        new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
                playerCount.setAlignment(Align.right);
                playerCount.setPosition(roomBG.getWidth() - roomBG.getHeight(), roomBGs.get(i).getY() + outlineWidth + margin + playerCount.getHeight()/2, Align.right);
                lowerStage.addActor(playerCount);
            }
            generateList = false;
        }

        Color c = game.palette[1];
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
        if (Gdx.input.justTouched()) {
            if (escapeBtn.contains(x, y)) game.setScreen(game.menu);
            if (refreshBtn.contains(x, y)) {
                game.client.sendTCP(new Networking.UpdateRoomListRequest());
                receiving = true;
            }
        }
        else if (Gdx.input.isTouched() && Gdx.input.getDeltaY() != 0) {
            scroll -= Gdx.input.getDeltaY();
            for(Actor a : lowerStage.getActors())
                a.setPosition(a.getX(), a.getY() - Gdx.input.getDeltaY());
        }

        if (receiving) rotation -= delta * 500;
        else rotation = 0;

        spriteBatch.begin();

        spriteBatch.draw(host, host.getX(), host.getY() + scroll);
        for(GameObject2D o : roomBGs) {
            spriteBatch.draw(o, o.getX(), o.getY() + scroll);
        }
        spriteBatch.end();

        lowerStage.draw();

        spriteBatch.begin();
        spriteBatch.draw(headerBG);
        spriteBatch.draw(escapeBtn);
        spriteBatch.draw(refreshBtn);
        spriteBatch.draw(new TextureRegion(refreshDecal),
                refreshDecal.getX(), refreshDecal.getY(),
                refreshDecal.getWidth()/2f, refreshDecal.getHeight()/2f,
                refreshDecal.getWidth(), refreshDecal.getHeight(), 1, 1, rotation);
        spriteBatch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("", ".resize();");
        game.client.sendTCP(new Networking.UpdateRoomListRequest());
        receiving = true;
    }

    @Override
    public void pause() {
        Gdx.app.log("", ".payse();");
    }

    @Override
    public void resume() {
        Gdx.app.log("", ".resume();");
    }

    @Override
    public void hide() {
        Gdx.app.log("", ".hide();");
    }

    @Override
    public void dispose() {

    }
}
