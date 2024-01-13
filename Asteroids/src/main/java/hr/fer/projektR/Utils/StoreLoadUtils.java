package hr.fer.projektR.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import hr.fer.projektR.math.Matrix;
import hr.fer.projektR.math.Vector;
import hr.fer.projektR.neuralnet.Layer;
import hr.fer.projektR.neuralnet.NeuralNetwork;
import hr.fer.projektR.neuralnet.NeuralNetworkAsteroids;

public class StoreLoadUtils {
    // main se koristi kada se zeli spremiti mreza (spremaju se samo slojevi mreze, ostali parametri se ne spremaju, ali nakon evolucije nisu niti bitni)
    public static void main(String[] args) {
        NeuralNetworkAsteroids network = loadNetworkSerialized("src/main/resources/NeuralNetworkAsteroids175");
        System.out.println("Loaded");
        System.out.println(network.getLayers().length);
        saveTo("src/main/resources/textSaves/NeuralNetworkAsteroids200", network);
        System.out.println("Saved");
    }   

    public static void saveObjectSerialized (java.io.Serializable best, String name) {
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

    public static NeuralNetworkAsteroids loadNetworkSerialized(String name) {
        NeuralNetworkAsteroids network = new NeuralNetworkAsteroids(1, 1, null, null, null, 10, 10, 24, 4);
        try {   
            // Reading the object from a file
            FileInputStream file = new FileInputStream(name);
            ObjectInputStream in = new ObjectInputStream(file);
             
            // Method for deserialization of object
            network = (NeuralNetworkAsteroids)in.readObject();
             
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
        return network;
    }

    public static void saveTo(String fileName, NeuralNetwork network) {
		Path file = Paths.get(fileName);
		if (Files.notExists(file)) {
			try {
				Files.createFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new BufferedOutputStream(
								Files.newOutputStream(file)),"UTF-8"))
				) {
			writer.write(String.valueOf(network.getLayers().length));
			writer.newLine();
			for (Layer l : network.getLayers()) {
				writer.write(String.format("%d %d %d", l.getWeights().getNrow(),l.getWeights().getNcol(),l.getBiases().getNrow()));
				writer.newLine();
				for (int i = 0; i < l.getWeights().getNrow(); i++) {
					for (int j = 0; j < l.getWeights().getNcol(); j++) {
						writer.write(String.valueOf(l.getWeights().matrix[i][j]));
						writer.write(" ");
					}
					writer.newLine();
				}
				for (int i = 0; i < l.getBiases().getNrow(); i++) {
					writer.write(String.valueOf(l.getBiases().matrix[i][0]));
					writer.write(" ");
				}
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static NeuralNetworkAsteroids loadFrom(String fileName, NeuralNetworkAsteroids network) {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), Charset.forName("UTF-8"))) {
			int l = Integer.parseInt(reader.readLine().strip());
			network.setLayers(new Layer[l]);
			for (int i = 0; i < l; i++) {
				String[] str = reader.readLine().split(" ");
				int r = Integer.parseInt(str[0]), c = Integer.parseInt(str[1]), n = Integer.parseInt(str[2]);
				double[][] data = new double[r][c];
				double[] vec = new double[n];
				for (int j = 0; j < r ; j++) {
					String[] ws = reader.readLine().split(" ");
					for (int k = 0; k < c; k++) {
						data[j][k] = Double.parseDouble(ws[k]);
					}
				}
				String[] ws = reader.readLine().split(" ");
				for (int j = 0; j < n ; j++) {
					vec[j] = Double.parseDouble(ws[j]);
				}
				network.getLayers()[i] = new Layer(new Matrix(data), new Vector(vec));
                network.getLayers()[i].setActivations(new Vector(n));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return network;
	}
}
