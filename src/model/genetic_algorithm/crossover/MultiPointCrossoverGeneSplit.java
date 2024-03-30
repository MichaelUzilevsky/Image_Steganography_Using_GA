package model.genetic_algorithm.crossover;

import model.data_managers.BitArray;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;

import java.util.ArrayList;
import java.util.List;


/**
 * Implements a crossover strategy that splits the chromosomes to its genes  and exchanges segments
 * between two parent chromosomes to produce offspring. This strategy aims to combine genetic
 * material from both parents while maintaining segments of individual genes, potentially
 * preserving beneficial gene combinations.
 */
public class MultiPointCrossoverGeneSplit implements CrossoverStrategy{

    /**
     * Performs crossover on two parent chromosomes by splitting each gene at its midpoint
     * and exchanging halves between the parents. This method considers the fitness scores of
     * the parents to determine the stronger and weaker parent, ensuring that the stronger
     * parent's genes are more likely to be passed on to the offspring.
     *
     * @param parent1 The first parent chromosome.
     * @param parent2 The second parent chromosome.
     * @return A list of offspring chromosomes resulting from the crossover.
     */
    @Override
    public List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
        Chromosome strong, weak;

        if (parent1.getFitnessScore() >= parent2.getFitnessScore()){
            strong = parent1;
            weak = parent2;
        }
        else {
            strong = parent2;
            weak = parent1;
        }
        Genes[] allGenes = strong.getGenesOrder();

        List<Chromosome> offSprings = new ArrayList<>(2);
        List<BitArray> temp;
        BitArray genes1 = new BitArray(strong.getGenes().size());
        BitArray genes2 = new BitArray(weak.getGenes().size());
        int genes1Index = 0, genes2Index = 0;
        for (Genes geneName : allGenes){
            temp = splitGene(strong.getGene(geneName), weak.getGene(geneName));
            BitArray gene1 = temp.remove(0);
            BitArray gene2 = temp.remove(0);
            genes1.set(genes1Index , gene1);
            genes2.set(genes2Index, gene2);
            genes1Index += gene1.size();
            genes2Index += gene2.size();
        }

        Chromosome  offSpring1 = new Chromosome(strong.getFlexibleGene(), genes1,
                strong.getGene(Genes.NS).size(), strong.getGene(Genes.OFF).size());

        Chromosome  offSpring2 = new Chromosome(strong.getFlexibleGene(), genes2,
                weak.getGene(Genes.NS).size(), weak.getGene(Genes.OFF).size());

        offSprings.add(offSpring1);
        offSprings.add(offSpring2);
        return offSprings;
    }

    /**
     * Splits each gene of the parent chromosomes at a midpoint, creating two new gene segments.
     * These segments are then recombined to form genes for the offspring, with one offspring
     * receiving the first halves from parent1 and second halves from parent2, and vice versa
     * for the second offspring.
     *
     * @param parent1Gene The gene from the first parent.
     * @param parent2Gene The gene from the second parent.
     * @return A list containing two new BitArrays, each representing a gene for the offspring.
     */
    private List<BitArray> splitGene(BitArray parent1Gene, BitArray parent2Gene){
        List<BitArray> mixedGenes = new ArrayList<>(2);

        int mid = (parent1Gene.size() - 1) / 2;

        BitArray gene1 = new BitArray(parent1Gene.size()), gene2 = new BitArray(parent1Gene.size());

        for (int i = 0; i <= mid; i++) {
            gene1.set(i, parent1Gene.get(i));
            gene2.set(i, parent2Gene.get(i));
        }
        for (int i = mid + 1; i < parent1Gene.size(); i++) {
            gene1.set(i, parent2Gene.get(i));
            gene2.set(i, parent1Gene.get(i));
        }
        mixedGenes.add(gene1);
        mixedGenes.add(gene2);

        return mixedGenes;
    }

    public static void main(String[] args){
        MultiPointCrossoverGeneSplit multiPointCrossover = new MultiPointCrossoverGeneSplit();
        Chromosome chromosome1 = new Chromosome(128);
        Chromosome chromosome2 = new Chromosome(128);
        System.out.println(chromosome1);
        System.out.println(chromosome2);

        for (Chromosome c : multiPointCrossover.crossover(chromosome1, chromosome2)){
            System.out.println(c);
        }

    }
}
