package model.genetic_algorithm.population_structure;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages the sizes of genes within chromosomes for a genetic algorithm. This class allows for
 * the dynamic setting and retrieval of gene sizes, facilitating flexible chromosome configurations.
 */
public class GeneSizeManager {

    /**
     * Stores the sizes of different gene types. Uses an EnumMap for efficient storage
     * and quick access, keyed by {@link Genes} enum values.
     */
    private final Map<Genes, Integer> geneSizes;

    /**
     * Constructs a new GeneSizeManager instance. Initializes the internal storage
     * for managing gene sizes.
     */
    public GeneSizeManager() {
        this.geneSizes = new EnumMap<>(Genes.class);
    }

    /**
     * Sets the size for a specific gene type.
     *
     * @param gene The gene whose size is to be set.
     * @param size The size value to be associated with the gene.
     */
    public void setGeneSize(Genes gene, int size) {
        geneSizes.put(gene, size);
    }

    /**
     * Retrieves the size of a specific gene type. If a size has not been explicitly set
     * for a given gene, this method returns a default size of 0.
     *
     * @param gene The gene whose size is being queried.
     * @return The size of the specified gene, or 0 if the size has not been set.
     */
    public int getGeneSize(Genes gene) {
        return geneSizes.getOrDefault(gene, 0);
    }

}

