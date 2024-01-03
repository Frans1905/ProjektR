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

public class AsteroidsGameAI extends Application {
	private Game game;
    private NeuralNetowrkAsteroids network;
	boolean keyA = false, keyD = false, keyW = false, keySpace=false;
	public static int asteroidsN = 5;

    public AsteroidsGameAI() {
        loadNetwork("src/main/resources/NN");
        asteroidsN = network.getLayers()[0].getWeights().getNcol() - 3;
        asteroidsN /= 5;
        game = new Game();
	}

    private void loadNetwork(String name) {
    	network = new NeuralNetowrkAsteroids(1,1);
    	network.loadFrom(name);
    }

	@Override
	public void start(Stage theStage) throws Exception {
		theStage.setTitle("AsteroidsAI");
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
	    new AnimationTimer(){
            int j = 0;
            boolean wPress = false, aPress = false, dPress = false;
	        public void handle(long currentNanoTime)
	        {
                if (game.isOver()) {
                    return;
                }

                if (j % 10 == 0) {
                    Vector in = new Vector(asteroidsN * 5 + 3);
                    in.fillWith(game.getData());
                    Vector m = network.process(in);

                    if (m.matrix[0][0] > 0.5) {
                        game.spaceInput();
                    }

                    wPress = m.matrix[1][0] > 0.5;
                    aPress = m.matrix[2][0] > 0.5;
                    dPress = m.matrix[3][0] > 0.5;
                }
                if (wPress) game.wInput();
                if (aPress) game.aInput();
                if (dPress) game.dInput();
                game.step();
                gc.fillRect(0, 0, Game.W, Game.H);
                game.draw(gc);

                j++;
                if (j == 10) {
                    j = 0;
                }
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
	   
	    theStage.show();
	}

	public static void launchGame(String[] args) {
		launch(args);
	}
}
