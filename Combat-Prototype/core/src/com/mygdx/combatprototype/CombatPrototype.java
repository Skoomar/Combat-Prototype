package com.mygdx.combatprototype;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class CombatPrototype extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	World world;
	Body body;
	Sprite sprite;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;

	float torque = 0.0f;
	boolean drawSprite = true;
	
	final float PIXELS_TO_METRES = 100f;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		sprite = new Sprite(img);

        sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);

        world = new World(new Vector2(0, 0f), true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / PIXELS_TO_METRES,
                (sprite.getY() + sprite.getHeight() / 2) / PIXELS_TO_METRES);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2 / PIXELS_TO_METRES, sprite.getHeight() / 2 / PIXELS_TO_METRES);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;

        body.createFixture(fixtureDef);
        shape.dispose();

        Gdx.input.setInputProcessor(this);

        // Create Box2DDebugRenderer, allows us to see physics simulation controlling scene
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		/* Part 1 of tutorial
		// Centre sprite
		sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);

		// Create a physics world
		// Vector parameter is its gravity
		world = new World(new Vector2(0, -98f), true);


		// BodyDefinition defines physics objects type & position in simulation
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		// Going to use 1:1 dimensions - 1 in physics engine is 1 pixel

		// Set body to same pos as sprite
		bodyDef.position.set(sprite.getX(), sprite.getY());

		// Create body in the world using the body definition
		body = world.createBody(bodyDef);

		// Define dimension of physics shape
		PolygonShape shape = new PolygonShape();

		// Set physics polygon to a box with same dimensions as sprite
		shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

		// FixtureDef defines properties such as density and area of the shape of the body
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;

		Fixture fixture = body.createFixture(fixtureDef);

		// shape is the only disposable here lad
		shape.dispose();
		*/
	}

	private float elapsed = 0;
	@Override
	public void render () {
        // Step the physics simulation forward at rate of 60hz
        world.step(1f/60f, 6, 2);

        // Apply torque to physics body (initially set to 0, controlled with [] keys)
        // Torque applied per frame instead of just once
        body.applyTorque(torque, true);


	    /* Part 1 of tutorial
		// Advance the world by the amount of time that has elapsed since the last frame
		// Generally wouldn't do this in a real game in the render loop
        // Wasteful as you are tying the physics update rate to the frame rate and vice versa
        // More common to update physics at a fixed rate and update render loop fast as possible
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);

		// Update sprite position according to the updated physics body
		sprite.setPosition(body.getPosition().x, body.getPosition().y);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(sprite, sprite.getX(), sprite.getY());
		batch.end();
		*/
	}
	
	@Override
	public void dispose () {
		img.dispose();
		world.dispose();
	}
}
