package model.genetic_algorithm.population_structure;

public interface PopulationImplementation {

    /**
     * adds chromosome to the population
     * @param chromosome not null
     */
    public void add(Chromosome chromosome);

    /**
     * first initialize a complete random population of chromosomes
     * @param imageWidth image width
     * @param imageHeight image height
     */
    public void initializeChromosomes(int imageWidth, int imageHeight);
}
