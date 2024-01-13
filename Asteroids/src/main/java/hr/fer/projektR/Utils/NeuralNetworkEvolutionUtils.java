package hr.fer.projektR.Utils;

import hr.fer.projektR.neuralnet.NeuralNetwork;

public class NeuralNetworkEvolutionUtils implements java.io.Serializable {
    private double bigMutation = 0.12;
    private double relative = 0.5;
    private double alpha = 0.1;
    private int bulletPenalizationScale = 1;

    public NeuralNetworkEvolutionUtils(double bigMutation, double relative, double alpha) {
        super();
        this.bigMutation = bigMutation;
        this.relative = relative;
        this.alpha = alpha;
    }
    public NeuralNetworkEvolutionUtils(double bigMutation, double relative) {
        this(bigMutation, 0.5, 0.1);
    }
    public NeuralNetworkEvolutionUtils(double bigMutation) {
        this(bigMutation, 0.5, 0.1);
    }
    public NeuralNetworkEvolutionUtils() {
        this(0.1, 0.5, 0.1);
    }
    public NeuralNetworkEvolutionUtils(double bigMutation, double relative, double alpha, int bulletPenalizationScale) {
        this(bigMutation, relative, alpha);
        this.bulletPenalizationScale = bulletPenalizationScale;
    }
    // ----------------------------------------------------------------------------------------------------
    // MUTATION METHODS
    final public SerializableBiConsumer<NeuralNetwork, Double> MUTATE_LAYERS = new SerializableBiConsumer<NeuralNetwork, Double>() {
        @Override
        public void accept(NeuralNetwork network, Double mutationChance) {
            for(int i = 0; i < network.getLayers().length; i++) {
                if (Math.random() < mutationChance) {
                    double delat = (Math.random() < bigMutation) ? network.bigRandom()*1 : network.smallRandom()*0.5;
                    network.getLayers()[i].getWeights().applyToAll((t) -> (Math.random()<relative)?t+delat:delat);		
                }
                if (Math.random() < mutationChance) {
                    double delat = (Math.random() < bigMutation)?network.bigRandom()*1:network.smallRandom()*0.5;
                    network.getLayers()[i].getBiases().applyToAll((t) -> (Math.random()<relative)?t+delat:delat);				
                }
            }
        }
    };
    final public SerializableBiConsumer<NeuralNetwork, Double> MUTATE_CONNECTIONS = new SerializableBiConsumer<NeuralNetwork, Double>() {
        @Override
        public void accept(NeuralNetwork network, Double mutationChance) {
            for (int i = 0; i < network.getLayers().length; i++) {
                for (int j = 0; j < network.getLayers()[i].getWeights().getNrow(); j++) {
                    for (int k = 0; k < network.getLayers()[i].getWeights().getNcol(); k++) {
                        if (Math.random() < mutationChance) {
                            double delta = (Math.random() < bigMutation) ? network.bigRandom() : network.smallRandom();
                            if (Math.random() < relative) {
                                network.getLayers()[i].getWeights().matrix[j][k] += delta;
                            }
                            else {
                                network.getLayers()[i].getWeights().matrix[j][k] = delta;
                            }
                            
                        }
                    }
                }
                for (int j = 0; j < network.getLayers()[i].getWeights().getNrow(); j++) {
                    if (Math.random() < mutationChance) {
                        double delta = (Math.random() < bigMutation) ? network.bigRandom() : network.smallRandom();
                        if (Math.random() < relative) {
                                network.getLayers()[i].getWeights().matrix[j][0] += delta;
                            }
                            else {
                                network.getLayers()[i].getWeights().matrix[j][0] = delta;
                            }
                    }
                }
            }
        }
    };
    // ----------------------------------------------------------------------------------------------------
    // REPRODUCTION METHODS
    final public SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork> FROM_TWO_PARENTS = new SerializableTriConsumer<NeuralNetwork, NeuralNetwork, NeuralNetwork>() {
        @Override
        public void accept(NeuralNetwork network, NeuralNetwork first, NeuralNetwork second) {
            for (int i = 0; i < network.getLayers().length; i++) {
                for (int j = 0; j < network.getLayers()[i].getWeights().getNrow(); j++) {
                    for (int k = 0; k < network.getLayers()[i].getWeights().getNcol(); k++) {
                        double len = Math.abs(first.getLayers()[i].getWeights().matrix[j][k] - second.getLayers()[i].getWeights().matrix[j][k]);
                        double low =Math.min(first.getLayers()[i].getWeights().matrix[j][k], second.getLayers()[i].getWeights().matrix[j][k]) - alpha*len;
                        network.getLayers()[i].getWeights().matrix[j][k] = len*(1+2*alpha)*Math.random()+low;
                    }
                }
                for (int j = 0; j < network.getLayers()[i].getWeights().getNrow(); j++) {
                    double len = Math.abs(first.getLayers()[i].getBiases().matrix[j][0] - second.getLayers()[i].getBiases().matrix[j][0]);
                    double low =Math.min(first.getLayers()[i].getBiases().matrix[j][0], second.getLayers()[i].getBiases().matrix[j][0]) - alpha*len;
                    network.getLayers()[i].getBiases().matrix[j][0] = len*(1+2*alpha)*Math.random()+low;
                }
            }
        }
    };
    // ----------------------------------------------------------------------------------------------------
    // FITNESS METHODS
    final public SerializableTriOperator<Double> FITNESS_FUNCTION_LINEAR = new SerializableTriOperator<Double>() {
        @Override
        public Double apply(Double score, Double time, Double bullets) {
            return score + time - bullets * bulletPenalizationScale;
        }
    };
    final public SerializableTriOperator<Double> FITNESS_FUNCTION_LIN_LOG_TIME = new SerializableTriOperator<Double>() {
        @Override
        public Double apply(Double score, Double time, Double bullets) {
            return score + time * Math.log(time) - bullets * bulletPenalizationScale;
        }
    };
    final public SerializableTriOperator<Double> FITNESS_FUNCTION_LIN_LOG_SCORE = new SerializableTriOperator<Double>() {
        @Override
        public Double apply(Double score, Double time, Double bullets) {
            if (score == 0.0) {
                return time;
            }
            return score * Math.log(score) + time - bullets * bulletPenalizationScale;
        }
    };
    final public SerializableTriOperator<Double> FITNESS_FUNCTION_LIN_LOG = new SerializableTriOperator<Double>() {
        @Override
        public Double apply(Double score, Double time, Double bullets) {
            if (score == 0.0) {
                return time * Math.log(time);
            }
            return score * Math.log(score) + time * Math.log(time) - bullets * bulletPenalizationScale;
        }
    };
}
