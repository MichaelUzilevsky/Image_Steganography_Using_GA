package model.genetic_algorithm.crossover;

import model.data_managers.BitArray;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a multipoint crossover strategy for genetic algorithms.
 * In this strategy, genes are alternately selected from each parent to construct
 * the offspring,where genes are alternately selected from each parent to construct the offspring.
 * This approach provides a more diversified genetic mix from both parents,
 * potentially enhancing the genetic diversity of the population.
 */
public class MultiPointCrossover implements CrossoverStrategy{

    /**
     * This method performs a crossover between two parent chromosomes by alternately selecting genes from each parent
     * to form two new offspring.
     * This method aims to mix the genetic material from both parents,
     * enhancing the potential for beneficial genetic combinations in the offspring.
     *
     * @param parent1 The first parent chromosome involved in the crossover.
     * @param parent2 The second parent chromosome involved in the crossover.
     * @return A list containing two offspring chromosomes, each constructed from an
     *         alternating selection of genes from the parents.
     */
    @Override
    public List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {

        Genes[] allGenes = parent1.getGenesOrder();

        Map<Genes,BitArray> offspring1Genes = new HashMap<>();
        Map<Genes,BitArray> offspring2Genes = new HashMap<>();

        // Alternate picking genes from each parent for each offspring
        for (int i = 0; i < allGenes.length; i++) {
            if (i % 2 == 0) { // Even indices get genes from parent1 for offspring1, parent2 for offspring2
                offspring1Genes.put(allGenes[i], (parent1.getGene(allGenes[i])));
                offspring2Genes.put(allGenes[i] , (parent2.getGene(allGenes[i])));
            } else { // Odd indices get genes from parent2 for offspring1, parent1 for offspring2
                offspring1Genes.put(allGenes[i] ,parent2.getGene(allGenes[i]));
                offspring2Genes.put(allGenes[i] ,parent1.getGene(allGenes[i]));
            }
        }

        // Construct the offspring Chromosomes from the mixed genes
        Chromosome offspring1 = constructOffspringFromGenes(offspring1Genes, parent1);
        Chromosome offspring2 = constructOffspringFromGenes(offspring2Genes, parent2);

        List<Chromosome> offsprings = new ArrayList<>(2);
        offsprings.add(offspring1);
        offsprings.add(offspring2);

        return offsprings;
    }

    /**
     * Constructs a new offspring chromosome from a given set of genes. This method
     * combines the genetic material from the provided genes map to form a complete
     * chromosome structure, based on the gene order of the base parent chromosome.
     * This method constructs a new offspring chromosome
     * by combining genes according to the gene order specified in the base parent chromosome.
     * This structured approach ensures that the genetic material is correctly
     * organized according to the established gene order.
     *
     * @param genes The map of genes (Gene type to BitArray) used to construct the offspring.
     * @param baseParent The parent chromosome providing the base structure and gene order.
     * @return A new Chromosome instance representing the offspring.
     */
    private Chromosome constructOffspringFromGenes(Map<Genes, BitArray> genes, Chromosome baseParent) {
        BitArray combinedGenes = new BitArray(baseParent.getGenes().size());
        int index = 0;
        for (Genes gene: baseParent.getGenesOrder()) {
            combinedGenes.set(index, genes.get(gene));
            index += genes.get(gene).size();
        }

        return new Chromosome(baseParent.getFlexibleGene(), combinedGenes,
                baseParent.getGene(Genes.NS).size(), baseParent.getGene(Genes.OFF).size());
    }

    public static void main(String[] args){
        MultiPointCrossover multiPointCrossover = new MultiPointCrossover();
        Chromosome chromosome1 = new Chromosome(32);
        Chromosome chromosome2 = new Chromosome(32);
        System.out.println(chromosome1);
        System.out.println(chromosome2);

        for (Chromosome c : multiPointCrossover.crossover(chromosome1, chromosome2)){
            System.out.println(c);
        }

    }
}
