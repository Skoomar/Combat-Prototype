package com.mygdx.combatprototype;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class CombatPrototype extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	World world;
	Body body;
	Body bodyEdgeScreen;
	Sprite sprite;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera camera;
	Matrix4 debugMatrix;
	BitmapFont font;

	float torque = 0.0f;
	boolean drawSprite = true;
	
	final float PIXELS_TO_METRES = 100f;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		sprite = new Sprite(img);

		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);

		world = new World(new Vector2(0, -1f), true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / PIXELS_TO_METRES,
                (sprite.getY() + sprite.getHeight() / 2) / PIXELS_TO_METRES);

		body = world.createBody(bodyDef);

		// Store the sprite the body represetns
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(sprite.getWidth() / 2 / PIXELS_TO_METRES,
                sprite.getHeight() / 2 / PIXELS_TO_METRES);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.1f;
		fixtureDef.restitution = 0.5f; // Restitution is how bouncy the surface is

		body.createFixture(fixtureDef);
		shape.dispose();

		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.StaticBody;
		float w = Gdx.graphics.getWidth() / PIXELS_TO_METRES;

		// Set height to 50 pixels above bottom of screen so edge is visible in physics debugger
        float h = Gdx.graphics.getHeight() - 50 / PIXELS_TO_METRES;
        bodyDef.position.set(0, 0);
        FixtureDef fixtureDef2 = new FixtureDef();

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-w / 2, -h / 2, w / 2, h / 2);
        fixtureDef2.shape = edgeShape;

        bodyEdgeScreen = world.createBody(bodyDef2);
        bodyEdgeScreen.createFixture(fixtureDef2);
        edgeShape.dispose();

        Gdx.input.setInputProcessor(this);

        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		/* Part 3
        sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);

        world = new World(new Vector2(0, 0f), true);

        /* Part 3 Tutorial
                Box2D is tuned to use MKS system (Metres, Kilograms, Seconds)
                Sprite is a 256x256 image, density (fixtureDef.density) is currently set as 1kg/ms^2
                The simulation sprite therefore has a mass of 65336kg, very heavy
                This is probably not what you're after
                Don't want to use pixels of the image to define properties like mass, density etc
                Easy work around: Use one set of coordinates in Box2D and translate between pixels and metres
                Divide by chosen value (PIXELS_TO_METRES) to get Box2D coords and multiply to get pixels

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / PIXELS_TO_METRES,
                (sprite.getY() + sprite.getHeight() / 2) / PIXELS_TO_METRES);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2 / PIXELS_TO_METRES, sprite.getHeight() / 2 / PIXELS_TO_METRES);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        shape.dispose();

        Gdx.input.setInputProcessor(this);

        // Create Box2DDebugRenderer, allows us to see physics simulation controlling scene
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        */

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
	    camera.update();

	    // Step physics simulation at 60hz
        world.step(1f/60f, 6, 2);

        body.applyTorque(torque, true);

        sprite.setPosition((body.getPosition().x * PIXELS_TO_METRES) - sprite.getWidth() / 2,
                body.getPosition().y * PIXELS_TO_METRES - sprite.getHeight() / 2);

        sprite.setRotation((float) Math.toDegrees(body.getAngle()));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METRES, PIXELS_TO_METRES, 0);
        batch.begin();

        if (drawSprite) {
            batch.draw(sprite, sprite.getX(), sprite.getY(),
                    sprite.getOriginX(), sprite.getOriginY(),
                    sprite.getWidth(), sprite.getHeight(),
                    sprite.getScaleX(), sprite.getScaleY(),
                    sprite.getRotation());
        }
        font.draw(batch,
                "Restitution" + body.getFixtureList().first().getRestitution(),
                -Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        batch.end();

        debugRenderer.render(world, debugMatrix);

	    /* Part 3
        // Step the physics simulation forward at rate of 60hz
        world.step(1f/60f, 6, 2);

        // Apply torque to physics body (initially set to 0, controlled with [] keys)
        // Torque applied per frame instead of just once
        body.applyTorque(torque, true);

        // Set sprite position from updated physics body location
		sprite.setPosition((body.getPosition().x * PIXELS_TO_METRES) - sprite.getWidth() / 2,
				(body.getPosition().y * PIXELS_TO_METRES) - sprite.getHeight() / 2);

		// Same for rotation
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);

		// Scale down sprite batches projection to box2D size
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METRES, PIXELS_TO_METRES, 0);

		batch.begin();

		if (drawSprite) {
			batch.draw(sprite, sprite.getX(), sprite.getY(),
					sprite.getOriginX(), sprite.getOriginY(),
					sprite.getWidth(), sprite.getHeight(),
					sprite.getScaleX(), sprite.getScaleY(),
					sprite.getRotation());
		}

		batch.end();

		// Now render physics world using scaled down matrix
		// Note: this is only used for debugging purposes
		debugRenderer.render(world, debugMatrix);

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

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
        // Set velocity at fixed rate in chosen direction
        if (keycode == Input.Keys.LEFT)
		    body.setLinearVelocity(-1f, 0f);
        if (keycode == Input.Keys.RIGHT)
            body.setLinearVelocity(1f, 0f);

        // Apply force to centre (whatever that means)
        if (keycode == Input.Keys.UP)
            body.applyForceToCenter(0f, 10f, true);
        if (keycode == Input.Keys.DOWN)
            body.applyForceToCenter(0f, -10f, true);

        // On bracket [ or ], apply torque clockwise/anti-clockwise
        if (keycode == Input.Keys.LEFT_BRACKET)
            torque += 0.1f;
        if (keycode == Input.Keys.RIGHT_BRACKET)
            torque -= 0.1f;

        // Remove torque with backslash /
        if (keycode == Input.Keys.BACKSLASH)
            torque = 0.0f;

        // Reset on spacebar
        if (keycode == Input.Keys.SPACE) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            torque = 0f;
            sprite.setPosition(0f, 0f);
            body.setTransform(0f, 0f, 0f);
        }

        // ESC key toggles visibility of sprite allow user to see physics debug info
		if (keycode == Input.Keys.ESCAPE)
		    drawSprite = !drawSprite;

        // Part 3
        if (keycode == Input.Keys.COMMA)
            body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution() - 0.1f);

        if (keycode == Input.Keys.PERIOD)
            body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution() + 0.1f);

        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.NUM_1)
            drawSprite = !drawSprite;

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	// On touch, apply force from direction of user's touch
	// Can result in object spinning
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		body.applyForce(1f, 1f, screenX, screenY, true);
		body.applyTorque(0.4f, true);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
