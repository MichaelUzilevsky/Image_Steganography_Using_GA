package model.genetic_algorithm.selection;

import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.populations.PopulationImplementation;

/**
 * Implements the roulette wheel selection strategy for a genetic algorithm. This probabilistic selection method
 * chooses chromosomes for the next generation based on their fitness scores relative to the total fitness of the population.
 * Chromosomes with higher fitness have a higher chance of being selected, but there is also a chance for less fit chromosomes
 * to be selected, promoting genetic diversity.
 */
public class RouletteWheelSelection implements SelectionStrategy{

    /**
     * This method selects chromosomes for the next generation using the roulette wheel selection approach,
     * where each chromosome's probability of being selected is proportional to its fitness score
     * relative to the total fitness of the population.
     * This method helps to ensure that chromosomes with higher fitness have a better chance of being selected,
     * while still allowing for the possibility of selecting less fit chromosomes, thus maintaining genetic diversity.
     *
     * @param currentGeneration The current population from which to select the next generation.
     * @param amountToSelect The number of chromosomes to be selected for the next generation.
     * @return An array of chromosomes selected for the next generation.
     */
    @Override
    public Chromosome[] selectNextGeneration(PopulationImplementation currentGeneration, int amountToSelect) {
        double totalFitness = sumFitness(currentGeneration);
        Chromosome[] selectedChromosomes = new Chromosome[amountToSelect];

        double randomNumber;
        double cumulativeFitness;

        boolean select;

        for (int i = 0; i < amountToSelect; i++) {
            randomNumber = Math.random() * totalFitness;
            cumulativeFitness = 0.0;

            select = false;
            for (int chromosome = 0; chromosome < currentGeneration.getPopulationSize() && ! select; chromosome++) {
                cumulativeFitness += currentGeneration.getPopulation()[chromosome].getFitnessScore();
                if (cumulativeFitness >= randomNumber || chromosome == currentGeneration.getPopulationSize() - 1) {
                    selectedChromosomes[i] = currentGeneration.getPopulation()[chromosome];
                    select = true;
                }
            }
        }
        return selectedChromosomes;


    }

    /**
     * Calculates the total fitness of the population,
     * which is used in the roulette wheel selection method to determine each chromosome's probability of being selected.
     * This total fitness is a crucial component in ensuring that the selection process is proportionate to the
     * fitness levels of the chromosomes, thereby maintaining a fitness-oriented approach to selection.
     *
     * @param currentGeneration The population whose total fitness is to be calculated.
     * @return The sum of fitness scores of all chromosomes in the population.
     */
    private double sumFitness(PopulationImplementation currentGeneration){
        double sumFitness = 0;
        for (Chromosome chromosome :  currentGeneration.getPopulation()){
            sumFitness += chromosome.getFitnessScore();
        }
        return sumFitness;
    }
}
