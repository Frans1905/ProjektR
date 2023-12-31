package hr.fer.projektR.app;

import hr.fer.projektR.game.Game;
import hr.fer.projektR.math.Vector;
import hr.fer.projektR.neuralnet.NeuralNetowrkAsteroids;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import java.io.*;

public class AsteroidsGameAI extends Application {
	private Game game;
    private NeuralNetowrkAsteroids network;
	boolean keyA = false, keyD = false, keyW = false, keySpace=false;
	
    public AsteroidsGameAI() {
		game = new Game();
        loadNetwork();
	}

    private void loadNetwork() {
        try {   
            // Reading the object from a file
            FileInputStream file = new FileInputStream("NeuralNetworkFile");
            ObjectInputStream in = new ObjectInputStream(file);
             
            // Method for deserialization of object
            network = (NeuralNetowrkAsteroids)in.readObject();
             
            in.close();
            file.close();

            System.out.println("Deserialized!");
        }
        catch(IOException ex) {
            ex.printStackTrace();
            System.out.println("IOException is caught");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is caught");
        }
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
	    // asteroids.testGame(); //BEZ ASTEROIDA
	    // AtomicBoolean canShoot = new AtomicBoolean(true);
	    new AnimationTimer(){
	        public void handle(long currentNanoTime)
	        {
                Vector in = new Vector(28);
                in.fillWith(game.getData());
			    Vector m = network.process(in);

                if (m.matrix[0][0] > 0.5) {
                    game.spaceInput();
                }

                boolean wPress = m.matrix[1][0] > 0.5, aPress = m.matrix[2][0] > 0.5, dPress = m.matrix[3][0] > 0.5;
                for (int j = 0; j < 10 && !game.isOver(); j++) {
                    if (wPress) game.wInput();
                    if (aPress) game.aInput();
                    if (dPress) game.dInput();
                    game.step();
                    gc.fillRect(0, 0, Game.W, Game.H);
                    game.draw(gc);
                    long t = (currentNanoTime - startNanoTime);
                    if (t/1000 < Game.DT/1000) {
                        try {
                            wait((long) (Game.DT - t/1e6),(int) (t%1000000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }	            
	        }

	    }.start();
	   
	    theStage.show();
	}

	public static void launchGame(String[] args) {
		launch(args);
	}
}