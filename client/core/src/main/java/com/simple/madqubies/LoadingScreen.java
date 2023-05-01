package com.simple.madqubies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Client;

public class LoadingScreen implements Screen {
    GameSuper game;
    public LoadingScreen(GameSuper game){
        this.game = game;
    }

    Stage stage = new Stage();
    Label label, description;

    boolean connectionFailed = false;

    int count = -2;
    GameSuper.MyShapeRenderer shapeRenderer = new GameSuper.MyShapeRenderer();
    FreeTypeFontGenerator fontGenerator;
    Sound theme, btnPress, pickupSound, collideSound, shootSound;
    long themeSoundID;

    @Override
    public void show() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("edit-undo.brk.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Gdx.graphics.getWidth()/(1980/300);
        parameter.borderColor = game.palette[4];
        parameter.borderWidth = 10;
        game.menu.logoFont = fontGenerator.generateFont(parameter);

        label = new Label("Mad Cubes", new Label.LabelStyle(game.menu.logoFont, game.palette[6]));
        label.setScale(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/4f);
        label.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/4f*3, Align.center);
        stage.addActor(label);


        parameter.size = Gdx.graphics.getHeight()/25;
        parameter.borderColor = game.palette[4];
        parameter.borderWidth = 1;
        parameter.color = game.palette[1];
        BitmapFont font = fontGenerator.generateFont(parameter);
        description = new Label("very very very very long text", new Label.LabelStyle(font, new Color(1,1,1,1)));
        description.setAlignment(Align.center);
        description.setPosition(Gdx.graphics.getWidth()/2f, description.getHeight()/2f + 20, Align.center);
        description.setText("Connecting to the server...");
        stage.addActor(description);

        theme = Gdx.audio.newSound(Gdx.files.internal("menu.wav"));
        themeSoundID = -1;

        btnPress = Gdx.audio.newSound(Gdx.files.internal("click.wav"));
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("pickup.wav"));
        collideSound = Gdx.audio.newSound(Gdx.files.internal("collide.wav"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
    }

    @Override
    public void render(float delta) {
        /*
        if (themeSoundID == -1) {
            themeSoundID = theme.play();
            theme.setLooping(themeSoundID, true);
        }
        */

        Color c = game.palette[0];
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.draw();

        if (count == -1){
            game.client = new Client();
            game.client.start();
            try {
                game.client.connect(5000, "ip addres (ipv4 or external/public)", /*tpc port/*, /*udp port/*);
                Networking.initialize(game.client);
                Gdx.app.log("CONNECTION", "Connected successfully!");
            } catch (Exception e) {
                connectionFailed = true;
                Gdx.app.log("CONNECTION ERROR", e.toString());
                count = -2;
            }
        }
        //else if (count < 0) count ++;
        else {
            switch (count) {
                case 0:
                    description.setText("Preparing lights for menu...");
                    game.menu.environment = new Environment();
                    game.menu.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
                    game.menu.environment.add(new DirectionalLight().set(0.8f, 236 / 255f, 214 / 255f, -1f, -0.8f, -0.2f));
                    break;
                case 1:
                    description.setText("preparing camera for menu...");
                    game.menu.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    game.menu.camera.position.set(game.menu.camOffsetX, game.menu.camOffsetY, game.menu.camOffsetZ);
                    game.menu.camera.lookAt(0, 1, 0);
                    game.menu.camera.near = 1f;
                    game.menu.camera.far = 300f;
                    game.menu.camera.update();
                    game.menu.screenHeight = Gdx.graphics.getHeight();
                    game.menu.screenWidth = Gdx.graphics.getWidth();
                    break;
                case 2:
                    description.setText("generating fonts and texts...");
                    fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("edit-undo.brk.ttf"));
                    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                    parameter.size = 300;
                    parameter.borderColor = game.palette[4];
                    parameter.borderWidth = 10;
                    game.menu.logoFont = fontGenerator.generateFont(parameter);
                    FreeTypeFontGenerator.FreeTypeFontParameter smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                    smallParameter.size = Gdx.graphics.getWidth()/(1920/200);
                    smallParameter.borderColor = game.palette[4];
                    smallParameter.borderWidth = 5;
                    smallParameter.color = game.palette[1];
                    game.menu.guiFont = fontGenerator.generateFont(smallParameter);
                    break;
                case 4:
                    description.setText("drawing gui for menu...");
                    Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() / 3 + 300, Gdx.graphics.getHeight() / 5, Pixmap.Format.RGBA8888);
                    pixmap.setColor(game.palette[5]);
                    pixmap.fillRectangle(pixmap.getHeight()/2, 0, pixmap.getWidth(), pixmap.getHeight());
                    pixmap.setColor(game.palette[0]);
                    //pixmap.fillRectangle(0, 0, pixmap.getHeight() / 2, pixmap.getHeight());
                    pixmap.setColor(game.palette[5]);
                    pixmap.fillCircle(pixmap.getHeight() / 2, pixmap.getHeight() / 2, pixmap.getHeight() / 2);
                    pixmap.setColor(game.palette[0]);
                    pixmap.fillCircle(pixmap.getHeight() / 2 + 5, pixmap.getHeight() / 2, pixmap.getHeight() / 2 - 10);
                    pixmap.setColor(game.palette[6]);
                    game.menu.GUIs.put("Play", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth() + 200, 100));
                    game.menu.GUIs.put("Equipment", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth(), Gdx.graphics.getHeight() / 2f - pixmap.getHeight() / 2f));
                    game.menu.GUIs.put("Liderboard", new GameObject2D(pixmap, Gdx.graphics.getWidth() - pixmap.getWidth() + 200, Gdx.graphics.getHeight() - pixmap.getHeight() - 100));
                    break;
                case 5:
                    description.setText("writing labels for menu...");
                    Label logoUp = new Label("Mad", new Label.LabelStyle(game.menu.logoFont, game.palette[6]));
                    logoUp.setPosition(stage.getWidth() / 4 - logoUp.getWidth() / 2, stage.getHeight() - logoUp.getHeight() - 50);
                    logoUp.setAlignment(1);
                    game.menu.lowerStage.addActor(logoUp);
                    Label logoLow = new Label("Cubes", new Label.LabelStyle(game.menu.logoFont, game.palette[6]));
                    logoLow.setPosition(stage.getWidth() / 4 - logoLow.getWidth() / 2, 50);
                    game.menu.lowerStage.addActor(logoLow);
                    break;
                case 6:
                    description.setText("preparing lights for loadout changer...");
                    game.loadoutScene.environment = new Environment();
                    game.loadoutScene.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
                    game.loadoutScene.environment.add(new DirectionalLight().set(0.8f, 236 / 255f, 214 / 255f, -1f, -0.8f, -0.2f));
                    break;
                case 7:
                    description.setText("preparing camera fo loadout changer...");
                    game.loadoutScene.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight());
                    game.loadoutScene.camera.position.set(game.loadoutScene.camOffsetX, game.loadoutScene.camOffsetY, game.loadoutScene.camOffsetZ);
                    game.loadoutScene.camera.lookAt(0, 1, 0);
                    game.loadoutScene.camera.near = 1f;
                    game.loadoutScene.camera.far = 300f;
                    game.loadoutScene.camera.update();
                    game.loadoutScene.screenHeight = Gdx.graphics.getHeight();
                    game.loadoutScene.screenWidth = Gdx.graphics.getWidth();
                    game.loadoutScene.shapeRenderer = new GameSuper.MyShapeRenderer();
                    break;
                case 8:
                    //drawing stuff for loadout
                    description.setText("drawing gui for loadout changer...");
                    int screenHeight = Gdx.graphics.getHeight(), screenWidth = Gdx.graphics.getWidth();
                    Color[] palette = game.palette;
                    pixmap = new Pixmap(screenHeight / 7, screenHeight / 7, Pixmap.Format.RGBA8888);
                    pixmap.setColor(palette[5]);
                    pixmap.fillRectangle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
                    pixmap.setColor(palette[0]);
                    pixmap.fillRectangle(pixmap.getWidth() / 2, 0, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), 20, palette[5]), 0, 0);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 20, pixmap.getHeight() - 20, 20, palette[0]), 10, 10);
                    Pixmap arrow = new Pixmap(Gdx.files.internal("arrow back.png"));
                    pixmap.drawPixmap(arrow, 0, 0, arrow.getWidth(), arrow.getHeight(), pixmap.getWidth() / 4, pixmap.getHeight() / 4, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
                    game.loadoutScene.backButton = new GameObject2D(pixmap, 0, screenHeight / 7f * 6, 12);
                    game.loadoutScene.gui.add(game.loadoutScene.backButton);
                    pixmap = new Pixmap(screenWidth / 2 + 20, screenHeight, Pixmap.Format.RGBA8888);
                    pixmap.setColor(palette[0]);
                    pixmap.fillRectangle(20, 0, pixmap.getWidth() - 20, pixmap.getHeight());
                    game.loadoutScene.listBG = new GameObject2D(pixmap, screenWidth / 2f - 20, 0, 0);
                    pixmap = new Pixmap(screenWidth, game.loadoutScene.backButton.getHeight(), Pixmap.Format.RGB888);
                    pixmap.setColor(palette[5]);
                    pixmap.fill();
                    pixmap.setColor(palette[0]);
                    pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight() - 10);
                    game.loadoutScene.headerBG = new GameObject2D(pixmap, game.loadoutScene.backButton.getWidth() / 2f, screenHeight - pixmap.getHeight(), 11);
                    game.loadoutScene.gui.add(game.loadoutScene.headerBG);
                    pixmap = new Pixmap(screenWidth / 2 / 5 - 10, screenWidth / 10, Pixmap.Format.RGBA8888);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), 20, palette[4]), 0, 0);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 10, pixmap.getHeight() - 10, 20, palette[5]), 5, 5);
                    pixmap.setColor(palette[7]);
                    pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() + 100, 150);
                    game.loadoutScene.areaEdge = new Vector2(screenWidth - game.loadoutScene.listBG.getX(), screenHeight - game.loadoutScene.headerBG.getHeight());

                    game.loadoutScene.bodySlot = new GameObject2D(pixmap, game.loadoutScene.areaEdge.x / 2f - pixmap.getWidth() / 2f, game.loadoutScene.areaEdge.y / 2f - pixmap.getHeight() / 2f, 1);
                    game.loadoutScene.scopeSlot = new GameObject2D(pixmap, game.loadoutScene.areaEdge.x / 4f - pixmap.getWidth() / 2f, game.loadoutScene.areaEdge.y / 4f * 3 - pixmap.getHeight() / 2f, 1);
                    game.loadoutScene.stockSlot = new GameObject2D(pixmap, game.loadoutScene.areaEdge.x / 4f * 3 - pixmap.getWidth() / 2f, game.loadoutScene.areaEdge.y / 4f * 3 - pixmap.getHeight() / 2f, 1);
                    game.loadoutScene.underbarrelSlot = new GameObject2D(pixmap, game.loadoutScene.areaEdge.x / 4f - pixmap.getWidth() / 2f, game.loadoutScene.areaEdge.y / 4f - pixmap.getHeight() / 2f, 1);
                    game.loadoutScene.handleSlot = new GameObject2D(pixmap, game.loadoutScene.areaEdge.x / 4f * 3 - pixmap.getWidth() / 2f, game.loadoutScene.areaEdge.y / 4f - pixmap.getHeight() / 2f, 1);
                    game.loadoutScene.gui.add(game.loadoutScene.bodySlot);
                    game.loadoutScene.gui.add(game.loadoutScene.scopeSlot);
                    game.loadoutScene.gui.add(game.loadoutScene.stockSlot);
                    game.loadoutScene.gui.add(game.loadoutScene.underbarrelSlot);
                    game.loadoutScene.gui.add(game.loadoutScene.handleSlot);

                    pixmap = new Pixmap(screenWidth / 2 + 20, screenHeight / 20 * 7, Pixmap.Format.RGBA8888);
                    pixmap.setColor(game.palette[5]);
                    pixmap.fill();
                    pixmap.setColor(game.palette[0]);
                    pixmap.fillRectangle(20, 20, pixmap.getWidth() - 20, pixmap.getHeight() - 20);
                    game.loadoutScene.descriptionBG = new GameObject2D(pixmap, screenWidth - pixmap.getWidth(), 0, 20);

                    pixmap = new Pixmap((screenWidth / 2 - 60) / 2, screenHeight / 4 / 4, Pixmap.Format.RGBA8888);
                    pixmap.setColor(game.palette[5]);
                    pixmap.fillCircle(pixmap.getHeight() / 2, pixmap.getHeight() / 2, pixmap.getHeight() / 2);
                    pixmap.fillCircle(pixmap.getWidth() - pixmap.getHeight() / 2, pixmap.getHeight() / 2, pixmap.getHeight() / 2);
                    pixmap.fillRectangle(pixmap.getHeight() / 2, 0, pixmap.getWidth() - pixmap.getHeight(), pixmap.getHeight());
                    game.loadoutScene.upgradeBtn = new GameObject2D(pixmap, screenWidth / 2f + 20, 20);
                    game.loadoutScene.compressBtn = new GameObject2D(pixmap, screenWidth - pixmap.getWidth() - 20, 20);
                    pixmap = new Pixmap(game.loadoutScene.backButton.getWidth() * 3, game.loadoutScene.backButton.getHeight() / 5 * 3, Pixmap.Format.RGBA8888);
                    c = game.palette[7];
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), pixmap.getHeight() / 2, new Color(c.r, c.g, c.b, .5f)), 0, 0);
                    Pixmap crystal = new Pixmap(Gdx.files.internal("crystal.png"));
                    pixmap.drawPixmap(crystal, 0, 0, crystal.getWidth(), crystal.getHeight(), pixmap.getWidth() - pixmap.getHeight(), 0, pixmap.getHeight(), pixmap.getHeight());
                    game.loadoutScene.coinsBG = new GameObject2D(pixmap, screenWidth - pixmap.getWidth() - 20, screenHeight - pixmap.getHeight() - game.loadoutScene.backButton.getHeight() / 5f, 20);

                    pixmap = new Pixmap(screenWidth / 2 - 40, game.loadoutScene.upgradeBtn.getHeight(), Pixmap.Format.RGBA8888);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), pixmap.getHeight() / 2, game.palette[4]), 0, 0);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 20, pixmap.getHeight() - 20, (pixmap.getHeight() - 20) / 2, game.palette[5]), 10, 10);
                    game.loadoutScene.levelBG = new GameObject2D(pixmap, game.loadoutScene.upgradeBtn.getX(), game.loadoutScene.upgradeBtn.getY() + game.loadoutScene.upgradeBtn.getHeight() + 20);
                    break;
                case 11:
                    description.setText("adding some small changes...");
                    palette = game.palette;
                    screenHeight = Gdx.graphics.getHeight();
                    screenWidth = Gdx.graphics.getWidth();
                    pixmap = new Pixmap(screenWidth / 2 / 5 - 10, screenWidth / 10, Pixmap.Format.RGBA8888);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), 20, palette[4]), 0, 0);
                    pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 10, pixmap.getHeight() - 10, 20, palette[5]), 5, 5);
                    pixmap.setColor(palette[7]);
                    pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() + 100, 150);
                    game.loadoutScene.slotPixmap = pixmap;
                    break;
                case 9:
                    description.setText("generating fonts for loadout changer...");
                    fontGenerator = game.fontGenerator;
                    /*FreeTypeFontGenerator.FreeTypeFontParameter*/
                    smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                    smallParameter.size = game.loadoutScene.backButton.getHeight();
                    smallParameter.borderColor = game.palette[4];
                    smallParameter.borderWidth = 5;
                    smallParameter.color = game.palette[1];
                    game.loadoutScene.mediumFont = fontGenerator.generateFont(smallParameter);
                    smallParameter.size = Gdx.graphics.getHeight() / 4 / 4;
                    game.loadoutScene.smallFont = fontGenerator.generateFont(smallParameter);
                    break;
                /*
            case 10:
                description.setText("doing useless stuff cuz y not :)");
                palette = game.palette;
                //camera = new OrthographicCamera();
                //viewport = new ExtendViewport(800, 600, camera);
                screenWidth = Gdx.graphics.getWidth();
                screenHeight = Gdx.graphics.getHeight();

                int margin = 100;
                int radius = 50, w = (int)screenWidth - 2*margin, h = (int)screenHeight/3 - 2*margin, borderWidth = 10;
                Pixmap lower = GameSuper.createRoundedRectangle(w, h, radius, palette[6]);
                Pixmap upper = GameSuper.createRoundedRectangle(w - borderWidth, h - borderWidth, radius, palette[0]);
                lower.drawPixmap(upper, borderWidth/2, borderWidth/2);
                game.modeChoose.GUIs.put("1", new GameObject2D(lower, margin, margin));
                game.modeChoose.GUIs.put("2", new GameObject2D(lower, margin, screenHeight/2 - lower.getHeight()/2f));
                game.modeChoose.GUIs.put("3", new GameObject2D(lower, margin, screenHeight - lower.getHeight() - margin));
                game.modeChoose.GUIs.get("1").setX(-lower.getWidth());
                game.modeChoose.GUIs.get("2").setX(screenWidth);
                game.modeChoose.GUIs.get("3").setY(screenHeight);

                fontGenerator = game.fontGenerator;
                /*FreeTypeFontGenerator.FreeTypeFontParameter*/ /*smallParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                smallParameter.size = 200;
                smallParameter.borderColor = game.palette[4];
                smallParameter.borderWidth = 5;
                smallParameter.color = game.palette[1];
                game.modeChoose.guiFont = fontGenerator.generateFont(smallParameter);

                game.modeChoose.labelStyle = new Label.LabelStyle(game.modeChoose.guiFont, Color.WHITE);
                game.modeChoose.classic = new Label("Classic Mode", game.modeChoose.labelStyle);
                game.modeChoose.classic.setScale(lower.getWidth(), lower.getHeight());
                game.modeChoose.arcade = new Label("Arcade Mode", game.modeChoose.labelStyle);
                game.modeChoose.arcade.setScale(lower.getWidth(), lower.getHeight());
                game.modeChoose.leader = new Label("Leader Board", game.modeChoose.labelStyle);
                game.modeChoose.leader.setScale(lower.getWidth(), lower.getHeight());
                game.modeChoose.stage.addActor(game.modeChoose.classic);
                game.modeChoose.stage.addActor(game.modeChoose.arcade);
                game.modeChoose.stage.addActor(game.modeChoose.leader);
                break;*/
                case 12 :
                    MultiplayerScene.palette = game.palette;
                    //MultiplayerScene.create();
                    break;
                case 13:
                    description.setText("loading models and initializing save files...");
                    ModelLoader<?> modelLoader = new ObjLoader();
                    //Model akm = modelLoader.loadModel(Gdx.files.internal("akm body.obj"));
                    GameSuper.modules = new WeaponModule[][]{
                            //0 - no module
                            {
                                    new WeaponModule("None", new Model(), new Texture(Gdx.files.internal("no object.png"))),
                                    //new WeaponModule("None", new Model(), new Texture(Gdx.files.internal("no object.png"))),
                            },
                            //1 - bodies
                            {
                                    new WeaponBody("AKM Body",
                                            modelLoader.loadModel(Gdx.files.internal("akm-1.obj")),
                                            new Texture(Gdx.files.internal("akm body.png")),
                                            Vector3.Zero,
                                            Vector3.Zero,
                                            new Vector3(0, -.1f, .8f),
                                            Vector3.Zero,
                                            Vector3.Zero),
                                    new WeaponBody("Shotgun Body",
                                            modelLoader.loadModel(Gdx.files.internal("shotgun head-1.obj")),
                                            new Texture(Gdx.files.internal("shotgun.png")),
                                            Vector3.Zero,
                                            Vector3.Zero,
                                            new Vector3(0, -.1f, .6f),
                                            Vector3.Zero,
                                            Vector3.Zero),
                                    new WeaponBody("Laser",
                                            modelLoader.loadModel(Gdx.files.internal("laser1.obj")),
                                            new Texture(Gdx.files.internal("laser1.png")),
                                            Vector3.Zero,
                                            Vector3.Zero,
                                            new Vector3(0, -.3f, .7f),
                                            Vector3.Zero,
                                            Vector3.Zero),
                                    new WeaponModule("None", new Model(), new Texture(Gdx.files.internal("no object.png"))),
                                    new WeaponModule("None", new Model(), new Texture(Gdx.files.internal("no object.png"))),
                                    new WeaponModule("None", new Model(), new Texture(Gdx.files.internal("no object.png"))),
                            },
                            //2- underbarrels
                            {

                            },
                            //3 - stocks
                            {

                            },
                            //4 - scopes
                            {

                            },
                            //5 - handles
                            {

                            }
                    };

                    GameSuper.maps = new GameSuper.MapData[]{
                            new GameSuper.MapData("Edgy Square", new Pixmap(Gdx.files.internal("maps/1.2.png")), modelLoader.loadModel(Gdx.files.internal("map1.obj")), 0, 0, 0),
                            new GameSuper.MapData("Zig-Zag", new Pixmap(Gdx.files.internal("maps/1.2.png")), modelLoader.loadModel(Gdx.files.internal("2.obj")), 0, 0, 0)
                    };

                    game.playerData = game.loadData();
                    if (game.UID == -1) {
                        game.client.sendTCP(new Networking.UIDRequest());
                        game.UID = -2;
                        count --;
                    }
                    else if (game.UID == -2) count --;
                    break;
                case 14:
                    //game.setScreen(new RoomChoose(game));
                    //game.setScreen(game.roomList);
                    game.setScreen(game.menu);
                    break;
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(game.palette[7]);
        shapeRenderer.roundedRect(-20, Gdx.graphics.getHeight()/4f - 10, Gdx.graphics.getWidth()/14f*count + 10 + 20, Gdx.graphics.getHeight()/8f + 20, 20);
        shapeRenderer.setColor(game.palette[1]);
        shapeRenderer.rect(0, Gdx.graphics.getHeight()/4f, Gdx.graphics.getWidth()/14f*count, Gdx.graphics.getHeight()/8f);
        shapeRenderer.end();

        count++;
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
