package hr.fer.projektR.app;

import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.projektR.game.Game;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

public class AsteroidsGame extends Application {
	private Game asteroids;
	boolean keyA = false, keyD = false, keyW = false, keySpace=false;
	public AsteroidsGame() {
		asteroids = new Game();
	}

	@Override
	public void start(Stage theStage) throws Exception {
		theStage.setTitle("Asteroids");
	    Group root = new Group();
	    Scene theScene = new Scene( root );
	    theStage.setScene( theScene );
	    Canvas canvas = new Canvas(800,  600);
	    root.getChildren().add( canvas );
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    Affine t = new Affine();
	    t.appendTranslation(-80, -80);
	    gc.setTransform(t);
	    gc.setFill( Color.BLACK );
	    gc.setStroke(Color.WHITE);
	    long startNanoTime = System.nanoTime();
//	    asteroids.testGame(); BEZ ASTEROIDA
	    AtomicBoolean canShoot = new AtomicBoolean(true);
	    new AnimationTimer(){
	        public void handle(long currentNanoTime)
	        {
	        	if (keyA) asteroids.aInput();
	        	if (keyD) asteroids.dInput();
	        	if (keyW) asteroids.wInput();
	        	if (keySpace) {
	        		asteroids.spaceInput();
	        		keySpace=false;
	        	}
	        	asteroids.step();
	    	    gc.fillRect(0, 0, Game.W, Game.H);
	        	asteroids.draw(gc);
	            long t = (currentNanoTime - startNanoTime); 
	            if (t/1000 < Game.DT/1000) {
	            	try {
						wait((long) (Game.DT - t/1e6),(int) (t%1000000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            }
	        }

	    }.start();
	   
        theScene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                    	switch (e.getCode()) {
						case A:
							keyA=true;
							break;
						case D:
							keyD=true;
							break;
						case W:
							keyW=true;
							break;
						case SPACE:
							if (canShoot.compareAndSet(true, false)) {
								keySpace=true;
							}
							break;
						default:
						}
                    }
                });
        
        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                    	switch (e.getCode()) {
                    	case LEFT:
						case A:
							keyA=false;
							break;
						case RIGHT:
						case D:
							keyD=false;
							break;
						case UP:
						case W:
							keyW=false;
							break;
						case SPACE:
							canShoot.set(true);
						default:
						}
                    }
                });

	    
	    
	    theStage.show();
	}

	public static void main(String[] args) {
		launch(args);

	}

}
