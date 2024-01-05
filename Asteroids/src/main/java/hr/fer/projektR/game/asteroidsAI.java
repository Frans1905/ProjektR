package hr.fer.projektR.game;

import hr.fer.projektR.Utils.NeuralNetworkEvolutionUtils;
import hr.fer.projektR.evolucijski.Evolution;
import hr.fer.projektR.evolucijski.JedinkaFactroy;
import hr.fer.projektR.math.Vector;
import hr.fer.projektR.neuralnet.NeuralNetworkAsteroids;
import hr.fer.projektR.neuralnet.NeuralNetwork;
import java.io.*;

/**
 * Kako mijenjati parametre:
 * -------------------------
 * ispisuju se podaci nakon prve i dalje nakon svakih 10 generacija (medijan, najbolji, nalosiji)
 * sprema se najbolji iz prve generacije i iz svake 25-te
 * na kraju se ispisuju podaci za prvu generaciju i najbolji medijan, najbolji generalno i najboolji najlosiji iz spremljenih generacija
 * 
 * 	SMALL_RANDOM_RANGE: +- kolko idu brojevi za male mutacije (uniformna distribucija)
 * 	BIG_RANDOM_RANGE: +- kolko idu brojevi za velike mutacije (normalna distribucija)
 * 	ASTEROIDS_N: koliko asteroida se salje mrezi koa input
 * 	maxIter: koliko generacija se maksimalno generira
 * 
 * 	NeuralNetworkEvolutionUtils
 * 	---------------------------
 * 		sadrzi metode za mutacije, stvaranej djece i racunanje fitnessa
 * 
 * 		bigMutation: vjerojatnost da mutacija bude velika (DEFAULT: 0.1)
 * 		relative: vjerojatnost da se "gen" prilikom mutacije poveca, u suprotnom se zamijeni (DEFAULT: 0.5)
 * 		alpha: parametar za BLX-alpha crossover; samo za metode za reprodukciju (DEFAULT: 0.1)
 * 		
 * 	NeuralNetworkAsteroids
 * 	----------------------
 * 		fromParentMethod: metoda za stvaranje djeteta iz 2 roditelja (DEFAULT: BLX-alpha crossover, alpha = 0.1)
 *		fitnessMethod: metoda za racunanje fitnessa na temelju rezultata u igri i prezivljenog vremena (DEFAULT: linearno)
 *		mutationMethod: metoda za mutiranje jedinke (DEFAULT: isto kao NeuralNetworkEvolutionUtils.MUTATE_LAYERS, bigMutation = 0.1, relative = 0.5)
 *		numberOfRepetitions: koliko puta mreza igra igru za odredivanje fitnessa (DEFAULT: -)
 * 		c: arhitektura neuronske mreze
 * 
 * 	Evolution
 * 	---------
 * 		n: broj jedinki u generaciji (DEFAULT: -)
 * 		firstN: koliko se jedinki iz bolje polovice prenosi u sljedecu generaciju; najbolji se uvijek prenosi (DEFAULT: 1)
 * 		fact: tvornica mreza (DEFAULT: -)
 * 		mutationChance: vjerojatnost mutacija (DEFAULT: -)
 * 		minMutation: minimalna vjerojatnost mutacija (DEFAULT: 0.05)
 * 		mutationReductionFactor: za koji faktor se smanjuje vjerojatnost mutacija s vremenom (DEFAULT: 1.0)
 * 		mutationIncrement: za koliko se poveca vjerojatnost mutacije ako medijan stagnira (DEFAULT: 0.0)
 * 		samePercentageTreshold: unutar kojeg postotka promjene se smatra da medijan stagnira (DEFAULT: 0.0)
 * 		staysTheSameMax: koliko dugo medijan treba stagnirati da se poveca vjerojatnost mutacije; staysTheSameMax * 10 generacija (DEFAULT: 10)
 * 		oneParent: true ako se djeca stvaraju iz jednog roditelja (u tom slucaju metoda za stvaranje djece nije vazna), false ako se djeca stvaraju iz dva roditelja (DEFAULT: false)
 */
public class AsteroidsAI {
	public static final int SMALL_RANDOM_RANGE = 2;
	public static final int BIG_RANDOM_RANGE = 20;
	public static final int ASTEROIDS_N = 8;
	public static final int VECTOR_SIZE = ASTEROIDS_N * 5 + 3;
	public static void main(String[] args) {
		NeuralNetworkEvolutionUtils utils = new NeuralNetworkEvolutionUtils(0.12, 0.5, 0.1);
		JedinkaFactroy<NeuralNetworkAsteroids> fact = new JedinkaFactroy<NeuralNetworkAsteroids>() {
			@Override
			public NeuralNetworkAsteroids create() {
				return new NeuralNetworkAsteroids(utils.FROM_TWO_PARENTS, utils.FITNESS_FUNCTION_LINEAR, utils.MUTATE_CONNECTIONS, 10, VECTOR_SIZE, 24, 4);
			}
		};
		Evolution darwin = new Evolution(3000, 3000 / 3, fact, 0.6, 0.05, 0.3, 0.25, 0.08, 10, true);
		Vector in = new Vector(VECTOR_SIZE);
		
		// System.out.println(darwin.run(20000, 1, 10000)-1); 
		System.out.println(darwin.run(200000, 1, 4000)-1);
		Game game = new Game(ASTEROIDS_N);
		game.newGame();
		while (!game.isOver()) {
			in.fillWith(game.getData());
			Vector m = ((NeuralNetwork)darwin.getBest()).process(in);
			if (m.matrix[0][0] > 0.5) {
				game.spaceInput();
			}
			boolean wPress = m.matrix[1][0] > 0.5, aPress = m.matrix[2][0] > 0.5, dPress = m.matrix[3][0] > 0.5;
			for (int j = 0; j < 10 && !game.isOver(); j++) {
				if (wPress) game.wInput();
				if (aPress) game.aInput();
				if (dPress) game.dInput();
				game.step();
			}
		}
		saveObject((NeuralNetworkAsteroids) darwin.getBest(), "src/main/resources/NeuralNetworkFile");
		System.out.println(game.getScore());
	}
	// <T extends Serializable>
	public static  void saveObject (java.io.Serializable best, String name) {
		try {   
        	//Saving of object in a file
            FileOutputStream file = new FileOutputStream(name);
            ObjectOutputStream out = new ObjectOutputStream(file);
             
            // Method for serialization of object
            out.writeObject(best);
             
            out.close();
            file.close();

			System.out.println("Serialized!");
        }
        catch(IOException ex) {
			ex.printStackTrace();
            System.out.println("IOException is caught");
        }
	}
}
