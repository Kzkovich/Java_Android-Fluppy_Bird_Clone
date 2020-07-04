package ru.kzkovich.flappybirdclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.omg.PortableInterceptor.Interceptor;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	
	Texture BACKGROUND;
	Texture[] bird;
	Texture bottomTube;
	Texture topTube;
	Texture gameOver;
	int birdWingsState = 0;
	int flyWidth;
	int flyHeight;
	int bottomTubeCenterHeight;
	int topTubeCenterHeight;
	int fallingSpeed = 0;
	int gameStateFlag = 0;
	int spaceBetweenTubes = 500;
	int tubeSpeed = 5;
	int tubeNumbers = 5;
	int distanceBetweenTubes;
	float tubeX[] = new float[tubeNumbers];
	float tubeShift[] = new float[tubeNumbers];
	
	int gameScore = 0;
	int passedTubeIndex = 0;
	
	Circle birdCircle;
	Rectangle topTubeRectangles[];
	Rectangle bottomTubeRectangles[];
//	ShapeRenderer shapeRenderer;
	Random random;
	BitmapFont scoreFont;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		BACKGROUND = new Texture("background.png");
//		shapeRenderer = new ShapeRenderer();
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.CORAL);
		scoreFont.getData().setScale(10);
		
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[tubeNumbers];
		bottomTubeRectangles = new Rectangle[tubeNumbers];
		
		bird = new Texture[2];
		bottomTube = new Texture("bottom_tube.png");
		topTube = new Texture("top_tube.png");
		gameOver = new Texture("game_over.png");
		bird[0] = new Texture("bird_wings_up.png");
		bird[1] = new Texture("bird_wings_down.png");
		random = new Random();
		flyWidth = Gdx.graphics.getWidth() / 2 - bird[0].getWidth() / 2;
		bottomTubeCenterHeight = Gdx.graphics.getHeight() / 2 - spaceBetweenTubes / 2 - bottomTube.getHeight();
		topTubeCenterHeight = Gdx.graphics.getHeight() / 2 + spaceBetweenTubes / 2;
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
		initGame();
	}
	
	public void initGame(){
		
		gameScore = 0;
		passedTubeIndex = 0;
		fallingSpeed = 0;
		flyHeight = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;
		for (int i = 0; i < tubeNumbers; i++) {
			tubeX[i] = Gdx.graphics.getWidth() / 2
					- topTube.getWidth() / 2
					+ Gdx.graphics.getWidth()
					+ i * distanceBetweenTubes;
			tubeShift[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - spaceBetweenTubes - 100);
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(BACKGROUND, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		if (gameStateFlag == 1) {
			
			Gdx.app.log("Game score", String.valueOf(gameScore));
			if (tubeX[passedTubeIndex] < Gdx.graphics.getWidth() / 2) {
				gameScore++;
				
				if (passedTubeIndex < tubeNumbers - 1) {
					passedTubeIndex++;
				} else {
					passedTubeIndex = 0;
				}
			}
			
			if (Gdx.input.justTouched()){
				fallingSpeed = -19;
			}
			
			for (int i = 0; i < tubeNumbers; i++) {
				
				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] = tubeNumbers * distanceBetweenTubes;
				} else {
					tubeX[i] -= tubeSpeed;
				}
				batch.draw(bottomTube, tubeX[i], bottomTubeCenterHeight + tubeShift[i]);
				batch.draw(topTube, tubeX[i], topTubeCenterHeight + tubeShift[i]);
				topTubeRectangles[i] = new Rectangle(tubeX[i], topTubeCenterHeight + tubeShift[i],
						topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], bottomTubeCenterHeight + tubeShift[i],
						bottomTube.getWidth(), topTube.getHeight());
			}
			
			if (flyHeight > 0) {
				fallingSpeed++;
				flyHeight -= fallingSpeed;
			} else {
				gameStateFlag = 2;
			}
			
		} else if (gameStateFlag == 0) {
			if (Gdx.input.justTouched()) {
				gameStateFlag = 1;
			}
		} else if (gameStateFlag == 2) {
			batch.draw(gameOver,
					Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
					Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
			if (Gdx.input.justTouched()) {
				gameStateFlag = 1;
				initGame();
			}
		}
		
		if (birdWingsState == 0) {
			birdWingsState = 1;
		} else {
			birdWingsState = 0;
		}
		
		batch.draw(bird[birdWingsState], flyWidth, flyHeight);
		scoreFont.draw(batch, String.valueOf(gameScore), 100, 200);
		batch.end();
		
		birdCircle.set(Gdx.graphics.getWidth() / 2,
				flyHeight + bird[birdWingsState].getHeight() / 2,
				bird[birdWingsState].getWidth() / 2);
		
		for (int i = 0; i < tubeNumbers; i++) {
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
					Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				gameStateFlag = 2;
			}
		}
	}
	
}
