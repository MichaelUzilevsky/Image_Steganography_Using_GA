package model.genetic_algorithm.population_structure;

/**
 * Enum representing the types of genes present in a chromosome for a genetic algorithm.
 * Each gene type serves a specific role in the algorithm's data manipulation and
 * genetic operations, contributing to the overall fitness and behavior of the organism.
 */
public enum Genes {
    /**
     * Number of Swaps (NS): Represents the gene that controls the number of swap operations
     * to be performed as part of the algorithm's data manipulation process.
     */
    NS,

    /**
     * Offset (OFF): Represents the gene that determines the offset value for swapping
     * operations, affecting how data is rearranged within the chromosome.
     */
    OFF,

    /**
     * Data Direction (DD): Encodes the direction of data flow during the swapping process,
     * which can be left-to-right or right-to-left, influencing the pattern of data manipulation.
     */
    DD,

    /**
     * Data Polarity (DP): Defines the gene responsible for controlling the polarity of data,
     * potentially inverting bits as part of the algorithm's encoding or mutation process.
     */
    DP
}
