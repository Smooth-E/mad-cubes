package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g3d.model.Node;

import java.util.ArrayList;

public class MultiplayerScene implements Screen {

    static GameSuper game;
    int roomID, mapID;

    MultiplayerScene(GameSuper game, int roomID, int mapID) {
        MultiplayerScene.game = game;
        this.roomID = roomID;
        this.mapID = mapID;
    }

    static Color[] palette;
    static PerspectiveCamera camera;
    static Environment environment;
    static float camOffsetX = 0,
            camOffsetY = 10,
            camOffsetZ = 0;
    static int screenHeight, screenWidth, outlineWidth, cornerRadius = 20, margin;
    static GameObject2D.MySpriteBatch spriteBatch;
    static Stage stage;
    static Touchpad touchpad, atkTouchpad;
    static Touchpad.TouchpadStyle touchpadStyle;
    static GameObject2D pauseBtn, pauseBG, hpBarBG, hpBarFG;
    static Label roomIDLabel;
    ModelBatch modelBatch = new ModelBatch();
    GameSuper.MyShapeRenderer shapeRenderer = new GameSuper.MyShapeRenderer();

    ModelInstance map, myCharacter;
    Model myWeapon, character, bullet;
    Vector3 myWeaponPosition = new Vector3();
    float moveSpeed = 5, angleDeg = 0, angleRad, aimLineLength = Gdx.graphics.getHeight()/2f, weaponFloatingAnim = 0;

    World world;
    Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
    BodyDef characterBodyDef;
    Body /*characterCollisionBody,*/ characterPhysicalBody;
    ArrayList<Body> playerSensors = new ArrayList<>();
    FixtureDef characterSensorDef;
    Fixture characterFixture;

    Vector3 spawn1, spawn2, mySpawn;

    FrameBuffer debugBuffer;

    static boolean receivingPlayerState = true;
    static ArrayList<Networking.Player> otherPlayers = new ArrayList<>();
    static float hp = 100;

    boolean haveToShoot = false;
    ArrayList<Bullet> myBullets = new ArrayList<>();
    Vector2 oldAtkDir = new Vector2();
    ArrayList<Bullet> bulletsToDestroy = new ArrayList<>();

    float bulletSpread, damage = 10;

    static class Bullet {
        ModelInstance instance;
        Body body;
        Vector2 direction;
        Fixture fixture;
    }

    static ModelInstance pad;

    boolean isPaused = false;
    static GameObject2D continueBtn, leaveBtn;
    static Label leaveLabel, continueLabel, pauseLabel, hpLabel;

    static boolean moduleFound;
    static GameObject2D modulePic;
    static Pixmap moduleBG;
    int moduleAnimState = 0;
    float picScale = 0, picPosX, picPosY;

    public static void create() {
        stage = new Stage();
        spriteBatch = new GameObject2D.MySpriteBatch();

        //palette = game.palette;
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        outlineWidth = screenHeight/108;
        margin = outlineWidth;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 /255f,214 /255f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camOffsetX, camOffsetY, camOffsetZ);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        touchpadStyle = new Touchpad.TouchpadStyle();
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
        touchpad.setPosition(screenWidth/1980f*100, screenHeight/10.8f);

        Touchpad.TouchpadStyle atkStyle = new Touchpad.TouchpadStyle();
        atkStyle.background = touchpadStyle.background;
        pixmap = new Pixmap(screenHeight/4/2, screenHeight/4/2, Pixmap.Format.RGBA8888);
        pixmap.setColor(palette[5]);
        pixmap.fillCircle(pixmap.getHeight()/2, pixmap.getHeight()/2, pixmap.getHeight()/2);
        pixmap.setColor(palette[1]);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("atk_icon.png")), 0, 0, 800, 800, 5, 5, pixmap.getHeight() - 10, pixmap.getHeight() - 10);
        atkStyle.knob = new TextureRegionDrawable(new Texture(pixmap));
        atkTouchpad = new Touchpad(1, atkStyle);
        stage.addActor(atkTouchpad);
        atkTouchpad.setPosition(screenWidth - screenWidth/1980f*100 - touchpad.getWidth(), screenHeight/10.8f);


        pixmap = new Pixmap(screenHeight/8, screenHeight/8, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getHeight(), pixmap.getWidth(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getHeight() - 2*outlineWidth, pixmap.getWidth() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("pause.png")), 0, 0, 360, 360, 0, 0, pixmap.getWidth(), pixmap.getHeight());
        pauseBtn = new GameObject2D(pixmap, screenWidth - pixmap.getWidth(), screenHeight - pixmap.getHeight());

        pixmap = new Pixmap(screenWidth/2, screenHeight, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + cornerRadius, pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - outlineWidth + cornerRadius, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pauseBG = new GameObject2D(pixmap, screenWidth/2f, 0);

        roomIDLabel = new Label("", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        roomIDLabel.setPosition(screenWidth, outlineWidth, Align.bottomRight);
        roomIDLabel.setSize(pauseBG.getWidth() - outlineWidth, game.loadoutScene.smallFont.getLineHeight());
        roomIDLabel.setAlignment(Align.left);
        stage.addActor(roomIDLabel);

        pixmap = new Pixmap(screenWidth/3, screenHeight/10, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + cornerRadius, pixmap.getHeight(), cornerRadius, game.palette[5]), -cornerRadius, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + cornerRadius - outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), -cornerRadius, outlineWidth);
        hpBarBG = new GameObject2D(pixmap, 0, screenHeight - pixmap.getHeight());

        pixmap = new Pixmap(screenWidth/3, screenHeight/10 - 2*outlineWidth, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() + cornerRadius, pixmap.getHeight(), cornerRadius, game.palette[1]), -cornerRadius, 0);
        hpBarFG = new GameObject2D(pixmap, 0, hpBarBG.getY() + outlineWidth);

        pad = new ModelInstance(new ObjLoader().loadModel(Gdx.files.internal("plate.obj")));
        pad.transform.translate(0, -.2f, 0);

        pixmap = new Pixmap(pauseBtn.getWidth(), pauseBtn.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[5]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("play.png")), 0, 0,
                175, 175, outlineWidth + margin, outlineWidth + margin, pixmap.getWidth() - 2*outlineWidth - 2*margin, pixmap.getHeight() - 2*outlineWidth - 2*margin);
        continueBtn = new GameObject2D(pixmap, pauseBtn.getX(), pauseBtn.getY());
        continueBtn.setActive(false);

        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth() - 2*outlineWidth, pixmap.getHeight() - 2*outlineWidth, cornerRadius, palette[0]), outlineWidth, outlineWidth);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("cross.png")),
                0,
                0,
                982,
                982,
                outlineWidth + margin, outlineWidth + margin,
                pixmap.getWidth() - 2*outlineWidth - 2*margin,
                pixmap.getHeight() - 2*outlineWidth - 2*margin);
        leaveBtn = new GameObject2D(pixmap, pauseBtn.getX(), pauseBtn.getY() - pixmap.getHeight() - 2*outlineWidth);
        leaveBtn.setActive(false);

        continueLabel = new Label("continue", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        continueLabel.setHeight(continueBtn.getHeight());
        continueLabel.setAlignment(Align.right);
        continueLabel.setPosition(continueBtn.getX() - margin, continueBtn.getY(), Align.bottomRight);
        stage.addActor(continueLabel);
        continueLabel.setText("");

        leaveLabel = new Label("leave", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        leaveLabel.setHeight(leaveBtn.getHeight());
        leaveLabel.setAlignment(Align.right);
        leaveLabel.setPosition(leaveBtn.getX() - margin, leaveBtn.getY(), Align.bottomRight);
        stage.addActor(leaveLabel);
        leaveLabel.setText("");

        pauseLabel = new Label("pause", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        pauseLabel.setHeight(continueBtn.getHeight());
        pauseLabel.setAlignment(Align.right);
        pauseLabel.setPosition(continueBtn.getX() - margin, continueBtn.getY(), Align.bottomRight);
        stage.addActor(pauseLabel);

        hpLabel = new Label("HP: 100|100", new Label.LabelStyle(game.loadoutScene.smallFont, Color.WHITE));
        hpLabel.setHeight(hpBarBG.getHeight() - 2*outlineWidth);
        hpLabel.setAlignment(Align.left);
        hpLabel.setPosition(margin, hpBarBG.getY() + outlineWidth);
        stage.addActor(hpLabel);

        pixmap = new Pixmap((int)(touchpad.getWidth()/1.5f), (int)(touchpad.getHeight()/1.5f), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(), cornerRadius, palette[4]), 0, 0);
        pixmap.drawPixmap(GameSuper.createRoundedRectangle(pixmap.getWidth()*7/8, pixmap.getHeight()*7/8, cornerRadius, palette[6]), pixmap.getWidth()/8/2, pixmap.getHeight()/8/2);
        pixmap.setColor(palette[7]);
        pixmap.fillCircle(pixmap.getWidth()/2, pixmap.getWidth()*2 + pixmap.getHeight(), pixmap.getWidth()*2 + pixmap.getHeight()/5);
        moduleBG = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
        moduleBG.drawPixmap(pixmap, 0, 0);

        moduleBG = new Pixmap(game.loadoutScene.slotPixmap.getWidth(), game.loadoutScene.slotPixmap.getHeight(), Pixmap.Format.RGBA8888);
        moduleBG.drawPixmap(game.loadoutScene.slotPixmap, 0, 0);
    }

    @Override
    public void show() {
        create(); //move it to loading screen when finish

        bullet = new ObjLoader().loadModel(Gdx.files.internal("bullet.obj"));

        otherPlayers = new ArrayList<>();

        roomIDLabel.setText("room id: " + roomID);

        map = GameSuper.maps[mapID].map;

        character = new ObjLoader().loadModel(Gdx.files.internal("cube.obj"));
        myCharacter = new ModelInstance(character);

        myWeapon = game.playerData.inventory.get(game.playerData.currentBuild[0]).model;

        world = new World(Vector2.Zero, true);

        world.setContactListener(new CollisionDetector());

        if (mapID == 0){
            //edgy square map (';
            BodyDef definition = new BodyDef();
            FixtureDef fixtureDefinition = new FixtureDef();
            Body body;

            definition.type = BodyDef.BodyType.StaticBody;
            definition.position.set(0, 0);
            body = world.createBody(definition);
            CircleShape mainCircle = new CircleShape();
            mainCircle.setRadius(1.1f);
            fixtureDefinition.shape = mainCircle;
            body.createFixture(fixtureDefinition);

            definition.position.set(6.05f, 0);
            body = world.createBody(definition);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(0.1f, 12.4f);
            fixtureDefinition.shape = polygonShape;
            body.createFixture(fixtureDefinition);

            definition.position.set(-6.05f, 0);
            body = world.createBody(definition);
            body.createFixture(fixtureDefinition);

            definition.position.set(0, 6.25f);
            body = world.createBody(definition);
            polygonShape = new PolygonShape();
            polygonShape.setAsBox(12.4f, .1f);
            fixtureDefinition.shape = polygonShape;
            body.createFixture(fixtureDefinition);

            definition.position.set(0, -6.25f);
            body = world.createBody(definition);
            body.createFixture(fixtureDefinition);

            definition.position.set(4, 5.7f);
            body = world.createBody(definition);
            polygonShape.setAsBox(3.5f/2, 0.7f/2);
            fixtureDefinition.shape = polygonShape;
            body.createFixture(fixtureDefinition);

            definition.position.set(-4, -5.7f);
            world.createBody(definition).createFixture(fixtureDefinition);

            polygonShape.setAsBox(.7f/2, 3.5f/2);
            fixtureDefinition.shape = polygonShape;
            definition.position.set(5.5f, -4.25f);
            world.createBody(definition).createFixture(fixtureDefinition);
            definition.position.set(-5.5f, 4.25f);
            world.createBody(definition).createFixture(fixtureDefinition);

            spawn1 = new Vector3(-5.1f, .1f, -5.3f);
            spawn2 = new Vector3(5.1f, .1f, 5.3f);
        }
        else if (mapID == 1) {
            map.transform.set(new Vector3(0, 9/10f - .1f, 0), new Quaternion());

            spawn1 = new Vector3(-4.3f, 0, -4.3f);
            spawn2 = new Vector3(4.3f, 0, 4.3f);

            BodyDef def = new BodyDef();
            FixtureDef fixture = new FixtureDef();
            Body body;
            def.type = BodyDef.BodyType.StaticBody;
            PolygonShape s = new PolygonShape();

            def.position.set(-1.1f, -0.7f);
            s.setAsBox(0.4f/2, 1.6f/2);
            fixture.shape = s;
            body = world.createBody(def);
            body.createFixture(fixture);

            def.position.set(1.3f, .5f);
            body = world.createBody(def);
            body.createFixture(fixture);

            s.setAsBox(3.8f/2f, 0.4f/2f);
            def.position.set(3, -.1f);
            fixture.shape = s;
            world.createBody(def).createFixture(fixture);

            def.position.set(-3, -.1f);
            world.createBody(def).createFixture(fixture);

            s.setAsBox(20, .1f/2);
            def.position.set(0, -5);
            fixture.shape = s;
            world.createBody(def).createFixture(fixture);

            def.position.set(0, 5);
            world.createBody(def).createFixture(fixture);

            s.setAsBox(.1f/2, 20);
            fixture.shape = s;
            def.position.set(-5, 0);
            world.createBody(def).createFixture(fixture);

            def.position.set(5, 0);
            world.createBody(def).createFixture(fixture);

            CircleShape c = new CircleShape();
            c.setRadius(1.2f);
            fixture.shape = c;
            def.position.set(5, -5);
            world.createBody(def).createFixture(fixture);

            def.position.set(-5, 5);
            world.createBody(def).createFixture(fixture);
        }

        if (GameSuper.random.nextInt(2) == 0) mySpawn = spawn1;
        else mySpawn = spawn2;

        myCharacter.transform.translate(mySpawn);

        characterBodyDef = new BodyDef();
        characterBodyDef.type = BodyDef.BodyType.DynamicBody;
        characterBodyDef.position.set(new Vector2(myCharacter.transform.getTranslation(Vector3.Zero).x, myCharacter.transform.getTranslation(Vector3.Zero).z));

        characterPhysicalBody = world.createBody(characterBodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(.5f);
        FixtureDef characterFixtureDef = new FixtureDef();
        characterFixtureDef.shape = circleShape;
        characterFixtureDef.density = 0.5f;
        characterFixtureDef.friction = .5f;

        characterFixture = characterPhysicalBody.createFixture(characterFixtureDef);

        /*
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(1.1f/2, 1.1f/2);
        characterFixtureDef.shape = polygonShape;
        characterFixtureDef.isSensor = true;
        characterSensorDef = characterFixtureDef;

        characterCollisionBody = world.createBody(characterBodyDef);
        characterCollisionBody.createFixture(characterFixtureDef);
         */

        try {
            debugBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, true);
        } catch (Exception e) {
            Gdx.app.log("", e.toString());
        }

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        camera.position.set(myCharacter.transform.getTranslation(Vector3.Zero).add(0, 8, 0));
        camera.lookAt(myCharacter.transform.getTranslation(Vector3.Zero));
        camera.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Color c = palette[0];
        //Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        hpLabel.setText("HP: " + (int) hp + " | 100");

        if(Gdx.input.justTouched()){
            int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
            Gdx.app.log("", "" + x + " "  + y + "\npausebtn: " + pauseBtn.contains(x, y) +
                    "\ncont: " + continueBtn.contains(x, y) + "\nleave: " + leaveBtn.contains(x, y));
            boolean playSound = true;
            if (pauseBtn.contains(x, y)) {
                pauseBtn.setActive(false);
                isPaused = true;
                leaveBtn.setActive(true);
                continueBtn.setActive(true);
            }
            else if (continueBtn.contains(x, y)) {
                isPaused = false;
                continueBtn.setActive(false);
                leaveBtn.setActive(false);
                pauseBtn.setActive(true);
            }
            else if (leaveBtn.contains(x, y)) {
                game.client.sendTCP(new Networking.DisconnectRequest(game.UID, roomID));
                leaveBtn.setActive(false);
                pauseBtn.setActive(true);
                continueBtn.setActive(false);
                game.client.sendTCP(new Networking.UpdateRoomListRequest());
                RoomChoose.receiving = true;
                game.roomList.pageIndex = 0;
                game.roomList.roomID = -1;
                game.setScreen(game.roomList);
            }
            if (isPaused) {
                continueLabel.setText("continue");
                leaveLabel.setText("leave");
                pauseLabel.setText("");
            }
            else {
                continueLabel.setText("");
                leaveLabel.setText("");
                pauseLabel.setText("pause");
                playSound = false;
            }

            if (playSound) game.loading.btnPress.play();
        }

        if (hp <= 0){
            hp = 100;
            characterPhysicalBody.setTransform(mySpawn.x, mySpawn.z, 0);
            //myCharacter.transform.set(mySpawn, new Quaternion());
        }

        if (atkTouchpad.isTouched()) {
            angleDeg = new Vector2(atkTouchpad.getKnobPercentX(), atkTouchpad.getKnobPercentY()).angleDeg();
            angleRad =  new Vector2(atkTouchpad.getKnobPercentX(), atkTouchpad.getKnobPercentY()).angleRad();
            haveToShoot = true;
        }
        else {

            if(haveToShoot) {
                haveToShoot = false;
                Bullet b = new Bullet();
                b.direction = new Vector2(new Vector2(1, 0).rotate(angleDeg).x * 10, -new Vector2(1, 0).rotate(angleDeg).y * 10);
                b.instance = new ModelInstance(bullet);
                b.instance.transform.set(myCharacter.transform);
                BodyDef d = new BodyDef();
                d.type = BodyDef.BodyType.DynamicBody;
                d.fixedRotation = true;
                b.body = world.createBody(d);
                FixtureDef f = new FixtureDef();
                PolygonShape s = new PolygonShape();
                s.setAsBox(.1f/2, .1f/2);
                f.shape = s;
                f.isSensor = true;
                b.fixture = b.body.createFixture(f);
                b.body.setTransform(characterPhysicalBody.getPosition(), myCharacter.transform.getRotation(new Quaternion()).getAngleAround(Vector3.Y));
                b.body.setLinearVelocity(b.direction);
                myBullets.add(b);

                game.loading.shootSound.play();
            }
            if (touchpad.isTouched()) {
                angleDeg = new Vector2(touchpad.getKnobPercentX(), touchpad.getKnobPercentY()).angleDeg();
                angleRad = new Vector2(touchpad.getKnobPercentX(), touchpad.getKnobPercentY()).angleRad();
            }
        }

        if (myBullets.size() > 0) for (Bullet b : myBullets) b.instance.transform.set(new Vector3(b.body.getPosition().x, .5f, b.body.getPosition().y), new Quaternion(Vector3.Y, b.body.getAngle()));

        oldAtkDir = new Vector2(atkTouchpad.getKnobPercentX(), atkTouchpad.getKnobPercentY());

        characterPhysicalBody.setLinearVelocity(touchpad.getKnobPercentX() * moveSpeed, -touchpad.getKnobPercentY() * moveSpeed);

        myCharacter.transform.set(
                new Vector3(characterPhysicalBody.getPosition().x, 0, characterPhysicalBody.getPosition().y),
                new Quaternion(Vector3.Y, angleDeg)
        );

        //characterCollisionBody.setTransform(myCharacter.transform.getTranslation(Vector3.Zero).x, myCharacter.transform.getTranslation(Vector3.Zero).z, -angleRad);

        /*
        camera.position.set(camOffsetX, camOffsetY, camOffsetZ);
        camera.rotate(Vector3.Z, -360);
        camera.lookAt(0, 0, 0);
        camera.update();
        */

        modelBatch.begin(camera);
        //modelBatch.render(pad, environment);
        modelBatch.render(map, environment);
        modelBatch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 1, 1, .1f));
        Vector3 center = myCharacter.transform.getTranslation(Vector3.Zero);
        center = camera.project(center);
        Vector2 end = new Vector2((float) Math.cos(angleRad)*(aimLineLength * Math.abs(atkTouchpad.getKnobPercentX()) ) ,
                (float) Math.sin(angleRad)*(aimLineLength * Math.abs(atkTouchpad.getKnobPercentY()) ))
                .add(new Vector2(center.x, center.y));
        shapeRenderer.rectLine(new Vector2(center.x, center.y), end, outlineWidth*10);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        modelBatch.begin(camera);

        Model model = new Model();
        for(Node node : character.nodes) model.nodes.add(node);
        Model w = myWeapon;
        for(Node node : w.nodes) {
            node.rotation.set(new Quaternion(Vector3.Y, -90));
            node.translation.set(0, 1, 1);
            model.nodes.add(node);
        }
        ModelInstance instance = new ModelInstance(model);
        instance.transform.set(myCharacter.transform);

        for (Networking.Player p : otherPlayers) {
            Model playerModel = new Model();
            for(Node node : character.nodes) playerModel.nodes.add(node);
            if (p.weaponTypes != null) {
                w = GameSuper.modules[1][p.weaponTypes[0]].model;
                for (Node node : w.nodes) {
                    node.rotation.set(Vector3.Y, -90);
                    node.translation.set(0, 1, 1);
                    playerModel.nodes.add(node);
                }
            }
            ModelInstance inst = new ModelInstance(playerModel);
            inst.transform.set(new Vector3(p.x, 0.1f, p.z), new Quaternion(Vector3.Y, p.angle));
            modelBatch.render(inst, environment);

            if(p.environment != null) {
                for (Networking.EnvironmentUnit u : p.environment) {
                    /*float x = myCharacter.transform.getTranslation(Vector3.Zero).x, y = myCharacter.transform.getTranslation(Vector3.Zero).z;
                    if ( u != null && !(u.positionX >= x - 1/2f && u.positionX <= x + 1/2f) && !(u.positionY >= y -1/2f && u.positionY <= y + 1/2f) ) {*/
                        ModelInstance i = new ModelInstance(bullet);
                        i.transform.translate(u.positionX, .5f, u.positionY);
                        i.transform.rotate(Vector3.Y, u.angle);
                        modelBatch.render(i, environment);
                   // }
                }
            }

        }

        modelBatch.render(instance, environment);

        for (Bullet b : myBullets) modelBatch.render(b.instance);

        modelBatch.end();

        //module found animation here
        spriteBatch.begin();
        if (moduleFound) {
            WeaponModule module = new WeaponModule(1, new WeaponModule.CharacteristicItem());
            game.playerData.inventory.add(module);
            Pixmap p = new Pixmap(moduleBG.getWidth(), moduleBG.getHeight(), Pixmap.Format.RGBA8888);
            p.drawPixmap(moduleBG, 0, 0);
            Texture t = new Texture(module.picture.getTextureData());
            t.getTextureData().prepare();
            p.drawPixmap(t.getTextureData().consumePixmap(), 0, 0, module.picture.getWidth(), module.picture.getHeight(), 0, 0,
                    p.getWidth(), p.getWidth());
            modulePic = new GameObject2D(p, screenWidth, screenHeight/2f);
            picPosX = screenWidth;
            picPosY = screenHeight/2f;
            picScale = 0;
            moduleAnimState = 1;
            moduleFound = false;
            game.loading.pickupSound.play();
        }
        else if (moduleAnimState == 1) {
            if (picScale < 1) picScale += .1f;
            if (picPosX > atkTouchpad.getX() + touchpad.getWidth()*.25f) picPosX -= 30f;
            else {
                picPosX = atkTouchpad.getX() + touchpad.getWidth()*.25f;
                moduleAnimState = 2;
            }
            spriteBatch.draw(modulePic, picPosX, picPosY, modulePic.getWidth()*picScale, modulePic.getHeight()*picScale);
        }
        else if (moduleAnimState == 2) {
            if (picScale < 1) picScale += .1f;
            if (picPosY > touchpad.getY() + touchpad.getHeight() - modulePic.getHeight()) {
                picPosY -= 30f;
            }
            else {
                moduleAnimState = 0;
                picPosX = screenWidth;
                picScale = 0;
                picPosY = screenHeight/2f;
            }
            spriteBatch.draw(modulePic, picPosX, picPosY, modulePic.getWidth()*picScale, modulePic.getHeight()*picScale);
        }
        spriteBatch.end();

        hpBarFG.setX(-1 * hpBarFG.getWidth() * (100 - hp) / 100f - outlineWidth);

        spriteBatch.begin();
        spriteBatch.draw(hpBarBG);
        spriteBatch.draw(hpBarFG);
        spriteBatch.draw(pauseBtn);
        spriteBatch.draw(continueBtn);
        spriteBatch.draw(leaveBtn);
        //spriteBatch.draw(debugBuffer.getColorBufferTexture(), 0, 0);
        spriteBatch.end();

        Networking.PlayerStateChangeRequest request = new Networking.PlayerStateChangeRequest(game.UID,
                myCharacter.transform.getTranslation(Vector3.Zero).x,
                myCharacter.transform.getTranslation(Vector3.Zero).z,
                myCharacter.transform.getRotation(new Quaternion()).getAngleAround(Vector3.Y),
                100,
                roomID);
        request.playerState.weaponTypes = new  int[]{
                game.playerData.inventory.get(game.playerData.currentBuild[0]).characteristic.id,
                game.playerData.inventory.get(game.playerData.currentBuild[1]).characteristic.id,
                game.playerData.inventory.get(game.playerData.currentBuild[2]).characteristic.id,
                game.playerData.inventory.get(game.playerData.currentBuild[3]).characteristic.id,
                game.playerData.inventory.get(game.playerData.currentBuild[4]).characteristic.id,
        };
        request.playerState.environment = new Networking.EnvironmentUnit[myBullets.size()];
        for (int i = 0; i < myBullets.size(); i ++){

            boolean add = true;
            for (Networking.Player p : otherPlayers) {
                float x = p.x, y = p.z;
                if (myBullets.get(i).body.getPosition().x >= x - 1/1.5f &&
                        myBullets.get(i).body.getPosition().x <= x + 1/1.5f &&
                        myBullets.get(i).body.getPosition().y >= y - 1/1.5f &&
                        myBullets.get(i).body.getPosition().y <= y + 1/1.5f) {
                    bulletsToDestroy.add(myBullets.get(i));

                    request.damage.add(new Networking.PlayerStateChangeRequest.DamageContainer(p.UID, damage));

                    add = false;

                    game.loading.collideSound.play();
                    break;
                }
            }
            /*if (add)*/ request.playerState.environment[i] = new Networking.EnvironmentUnit(myBullets.get(i).body.getPosition().x, myBullets.get(i).body.getPosition().y, myBullets.get(i).body.getAngle());
        }
        game.client.sendTCP(request);

        stage.act();
        stage.draw();

        world.step(1/60f, 6, 2);

        for(Bullet b : bulletsToDestroy){
            world.destroyBody(b.body);
            if (myBullets.contains(b))  myBullets.remove(myBullets.indexOf(b));
        }
        bulletsToDestroy = new ArrayList<>();

        debugBuffer.begin();
        Gdx.gl.glClearColor(c.r,c.g,c.b,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.position.set(camOffsetX, camOffsetZ, camOffsetY);
        camera.rotate(Vector3.Z, 360);
        camera.lookAt(0, 0, 0);
        camera.update();
        box2DDebugRenderer.render(world, camera.combined);
        debugBuffer.end();
    }

    class CollisionDetector implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            game.loading.collideSound.play();
            for(Bullet b : myBullets){
                if ( (contact.getFixtureA() == b.fixture && contact.getFixtureB() != characterFixture) || (contact.getFixtureA() != characterFixture && contact.getFixtureB() == b.fixture)) {
                    bulletsToDestroy.add(b);
                }
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }

    boolean isInRect (float rectX, float rectY, float rectWidth, float rectHeight, float angleDeg, float x, float y) {
        float xOffset = x - rectX, yOffset = y - rectY;
        return (
                x * Math.cos(angleDeg) - y * Math.sin(angleDeg) + xOffset >= rectX &&
                        x * Math.cos(angleDeg) - y * Math.sin(angleDeg) + xOffset <= rectX + rectWidth &&
                        x * Math.sin(angleDeg) + y * Math.cos(angleDeg) + yOffset >= rectY &&
                        x * Math.sin(angleDeg) + y * Math.cos(angleDeg) + yOffset <= rectY + rectHeight
                );
        /*
        x*cosd(deg) - y*sin(deg) + xOffset >= rectangle.getX()
                && x*cosd(deg) - y*sin(deg) + xOffset <= rectangle.getX() + rectangle.getWidth()
                && x*sind(deg) + y*cosd(deg) + yOffset >= rectangle.getY()
                && x*sind(deg) + y*cosd(deg) + yOffset <= rectangle.getY() + rectangle.getHeight();
         */
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
