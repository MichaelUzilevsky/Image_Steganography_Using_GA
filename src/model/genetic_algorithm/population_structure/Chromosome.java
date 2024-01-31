package model.genetic_algorithm.population_structure;

import model.data_manegers.BitArray;
import model.utils.BitsUtils;
import model.utils.ConstantsClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents a single chromosome in the genetic algorithm's population.
 * This chromosome contains five genes, with three having fixed sizes and the others calculated based on the image size.
 * <p>
 * The sizes for 'numberOfSwaps' and 'offset' are calculated as:
 * log2((width * height) / 2) bits, where width * height is the total pixel count in the image.
 * This represents the maximum number of bits for the text, divided by 2 due to changes in the bit array order.
 */
public class Chromosome implements Comparable<Chromosome> {
    public static final Random random = new Random();

    // fixed sizes of genes
    public static final int FLEXIBLE_GENE_SIZE = 5; // 4! = 24 -> 11000 MAX 5 BITS
    public static final int DATA_DIRECTION_SIZE = 1; // 0 OR 1
    public static final int DATA_POLARITY_SIZE = 2; // 00 01 10 11
    public static final int MUTATIONS_PER_GENE = 2;
    public final int numberOfSwapsSize;
    public final int offsetSize;

    private final BitArray flexibleGene;
    private final BitArray numberOfSwapsGene;
    private final BitArray offsetGene;
    private final BitArray dataPolarityGene;
    private final BitArray dataDirectionGene;
    private final BitArray[] genes;

    private double fitnessScore;


    /**
     * Initializes a new Chromosome instance for a given image size.
     *
     * @param imageWidth  The width of the image.
     * @param imageHeight The height of the image.
     */
    public Chromosome(int imageWidth, int imageHeight){

        numberOfSwapsSize = offsetSize = BitsUtils.bitsNeeded(ConstantsClass.BITS_REPLACED_PER_BYTE * ConstantsClass.BYTES_IN_PIXEL *
                imageHeight * imageWidth) / 2;

        this.flexibleGene = new BitArray(FLEXIBLE_GENE_SIZE);
        this.dataDirectionGene = new BitArray(DATA_DIRECTION_SIZE);
        this.dataPolarityGene = new BitArray(DATA_POLARITY_SIZE);
        this.numberOfSwapsGene = new BitArray(numberOfSwapsSize);
        this.offsetGene = new BitArray(offsetSize);

        this.genes = new BitArray[] {numberOfSwapsGene, offsetGene, dataDirectionGene, dataPolarityGene};

        this.fitnessScore = -1;
        initiateChromosome();
    }

    /**
     * Initiates the chromosome by randomly setting the bits of each gene.
     */
    public void initiateChromosome(){
        initiateGene(flexibleGene);
        for (BitArray bitArray : genes) {
            initiateGene(bitArray);
        }
        modifyGenesArrayBasedOnFlexibleChromosome(flexibleGene.toInt());
    }

    /**
     * Initiates a given gene by randomly setting its bits.
     *
     * @param gene The gene to be initiated.
     */
    private void initiateGene(BitArray gene){
        for (int i = 0; i < gene.size(); i++){
            gene.set(i, random.nextInt(2) == 1);
        }

    }

    /**
     * Mutates the chromosome by applying mutations to each gene.
     */
    public void mutateChromosome() {
        for (BitArray gene : genes){
            mutateGene(gene);
        }
    }

    /**
     * Mutates a given gene by flipping two of its bits.
     *
     * @param gene The gene to be mutated.
     */
    private void mutateGene(BitArray gene){
        for (int i = 0; i < MUTATIONS_PER_GENE; i++) {
            gene.flip(random.nextInt(2));
        }
    }

    /**
     * Modifies the genes array based on the value of the flexible gene.
     * This involves rearranging the genes according to the decoded permutation represented by the flexible gene's value.
     *
     * @param flexibleGeneValue The value of the flexible gene used to determine the permutation.
     */
    public void modifyGenesArrayBasedOnFlexibleChromosome(int flexibleGeneValue) {
        List<BitArray> availableGenes = new ArrayList<>(Arrays.asList(numberOfSwapsGene, offsetGene,
                dataDirectionGene, dataPolarityGene));

        int[] factorials = {6, 2, 1}; // Factorials for n-1, n-2, n-3 (for n=4)

        for (int i = 0; i < 3; i++) { // Only need to calculate the first 3 positions
            int pos = flexibleGeneValue / factorials[i];
            flexibleGeneValue %= factorials[i];
            genes[i] = availableGenes.remove(pos);
        }

        // Last number is the remaining one
        genes[3] = availableGenes.get(0);
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(float fitnessScore) {
        this.fitnessScore = fitnessScore;
    }


    /**
     * comparison based on the fitness score of the chromosome
     *
     * @param chromosome the object to be compared.
     * @return positive if this is bigger than chromosome negative if smaller
     */
    @Override
    public int compareTo(Chromosome chromosome) {
        return Double.compare(this.fitnessScore, chromosome.fitnessScore);
    }
}
