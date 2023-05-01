package com.simple.madqubies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;

import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.esotericsoftware.kryonet.Client;

public class Sandbox implements Screen {
    GameSuper game;
    private btCollisionDispatcher dispatcher;
    private btDefaultCollisionConfiguration collisionConfig;
    private btDbvtBroadphase broadphase;
    private btSequentialImpulseConstraintSolver constraintSolver;

    public Sandbox(GameSuper g){
        game = g;
    }

    Environment environment;
    PerspectiveCamera camera;
    ModelInstance stock;
    ModelBatch mb = new ModelBatch();
    ModelInstance inst;
    ModelCache mc;

    Client client;

    World world;
    Box2DDebugRenderer debugRenderer;
    BodyDef playerDef;
    Body playerBody;
    EdgeShape playerShape;
    Fixture playerFixture;


    @Override
    public void show() {

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 236 / 255f, 214 / 255f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(2, 0, 0);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        /*

        float size = 1;
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(.1f, .1f, .1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        stock = new ModelInstance(model);
        Vector3 s = new Vector3(0, .05f, .15f);
        Vector3 m = new Vector3(0, .3f, .4f);
        stock.transform.setToTranslation(m.x, s.y + m.y, m.z + s.z);

        ModelLoader<?> ml = new ObjLoader();
        Model mainModel = ml.loadModel(Gdx.files.internal("laser1.obj"));
        inst = new ModelInstance(mainModel);

        Model newModel = new Model();
        newModel.nodes.addAll(mainModel.nodes);
        for(int i = 0; i < stock.nodes.size; i++){
            Node node = stock.nodes.get(i);
            node.translation.add(new Vector3(0, -.3f, .7f));
            newModel.nodes.add(node);
        }
        inst = new ModelInstance(newModel);
*/

        /*

        class TestRequest{
            String text;
        }

        class TestResponse{
            String txt;
        }

        client = new Client();

        Kryo kryo = client.getKryo();
        kryo.register(TestRequest.class);
        kryo.register(TestResponse.class);

        client.start();
        try {
            client.connect(5000, "192.168.0.190", 54555, 54777);
            Gdx.app.log("CONNECTION", "ConnectedQ");
        } catch (Exception e){
            Gdx.app.log("CONNECTION", e.toString());
        }
        client.addListener(new Listener(){
            public void received (Connection connection, Object object) {
                Gdx.app.log("CONNECTION", "U received a response!");
                if (object instanceof TestResponse) Gdx.app.log("CONNECTION",  ((TestResponse)object).txt);
            }
        });

        TestRequest tr = new TestRequest();
        tr.text = "Client's request";
        client.sendTCP(tr);
    }
    */

        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();

        playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(playerDef);
        playerShape = new EdgeShape();
        playerShape.set(0,0,1,1);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        /*
        inst.transform.rotate(Vector3.Y, Gdx.input.getDeltaX());
        inst.transform.rotate(Vector3.X, Gdx.input.getDeltaY());

        /*
        //inst - центральный обьект, outline - дочерний
        float yAngle = inst.transform.getRotation(new Quaternion(Vector3.Y, 0)).getAngleAroundRad(Vector3.Y);
        float x1 = .55f * (float)Math.sin(yAngle);
        float z1 = .55f * (float)Math.cos(yAngle);
        float xAngle = inst.transform.getRotation(new Quaternion(Vector3.X, 0)).getAngleAroundRad(Vector3.X);
        float y = .35f * (float)Math.cos(xAngle);
        outline.transform.set(new Vector3(x1, y, z1), new Quaternion());


        mb.begin(camera);
        mb.render(inst, environment);
        mb.end();
        mb.begin(camera);
        mb.render(outline, environment);
        mb.end();
         */

        /*
        mb.begin(camera);
        mb.render(inst, environment);
        mb.end();*/

        world.step(1/60f, 6, 2);

        debugRenderer.render(world, camera.combined);

        mb.begin(camera);
        mb.end();


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
