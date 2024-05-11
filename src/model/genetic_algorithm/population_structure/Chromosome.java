package model.genetic_algorithm.population_structure;

import model.data_managers.BitArray;
import model.utils.ConstantsClass;
import model.utils.UtilsMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents a single chromosome in the genetic algorithm's population.
 * A chromosome encapsulates the parameters (genes) that define a solution within the genetic algorithm's search space.
 * Each gene represents a specific characteristic of the solution, such as the number of swaps, offset, data direction, and data polarity.
 * The fitness score of the chromosome is a measure of how good the solution it represents is.
 */
public class Chromosome implements Comparable<Chromosome> {
    public static final Random random = new Random();
    public static final int GENES_AMOUNT = 4;
    public static final double MUTATION_PROBABILITY = 0.2;

    private static GeneSizeManager geneSizeManager;
    private final int[] genesStartingIndex;
    private final Genes[] genesOrder;
    private final BitArray flexibleGene;
    private final BitArray genes;
    private double fitnessScore;


    /**
     * Constructs a Chromosome with a specified data size in bits. This constructor initializes the chromosome with random gene values.
     *
     * @param dataSizeInBits The size of the data, in bits, that this chromosome's genes will operate on.
     */
    public Chromosome(int dataSizeInBits) {

        int offsetSize, numberOfSwapsSize = offsetSize = calculateNSandOFFGenesSizes(dataSizeInBits);

        this.flexibleGene = new BitArray(ConstantsClass.FLEXIBLE_GENE_SIZE);
        this.genes = new BitArray(ConstantsClass.DATA_DIRECTION_SIZE + ConstantsClass.DATA_POLARITY_SIZE + numberOfSwapsSize + offsetSize);

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        this.fitnessScore = -1;

        genesOrder = new Genes[GENES_AMOUNT];
        genesStartingIndex = new int[GENES_AMOUNT];

        initiateChromosome();
    }

    /**
     * Constructs a Chromosome with specified genes and sizes. This constructor is used for creating a new chromosome from specific genes.
     *
     * @param flexibleGene      The gene representing the flexible order of operations.
     * @param genes             The BitArray representing all the genes except for the flexible gene.
     * @param numberOfSwapsSize The size of the number of swaps gene.
     * @param offsetSize        The size of the offset gene.
     */
    public Chromosome(BitArray flexibleGene, BitArray genes, int numberOfSwapsSize, int offsetSize){
        this.genes = genes;
        this.flexibleGene = flexibleGene;

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        genesOrder = new Genes[GENES_AMOUNT];
        genesStartingIndex = new int[GENES_AMOUNT];

        setIndexesForGenes(flexibleGene.toInt());
    }

    /**
     * Constructor for creating a chromosome with predefined genes.
     *
     * @param fc Flexible gene value
     * @param ns Number of swaps gene value
     * @param off Offset gene value
     * @param dd Data direction gene value
     * @param dp Data polarity gene value
     */
    public Chromosome(int fc, int ns, int off, int dd, int dp) {
        int offsetSize = UtilsMethods.bitsNeeded(off), numberOfSwapsSize = UtilsMethods.bitsNeeded(ns);

        this.flexibleGene = new BitArray(ConstantsClass.FLEXIBLE_GENE_SIZE);
        this.genes = new BitArray(ConstantsClass.DATA_DIRECTION_SIZE + ConstantsClass.DATA_POLARITY_SIZE + numberOfSwapsSize + offsetSize);

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        this.fitnessScore = -1;

        genesOrder = new Genes[GENES_AMOUNT];
        genesStartingIndex = new int[GENES_AMOUNT];

        flexibleGene.modifyBitArrayByNumber(fc);
        setIndexesForGenes(flexibleGene.toInt());

        BitArray temp = new BitArray(UtilsMethods.bitsNeeded(ns));
        temp.modifyBitArrayByNumber(ns);
        setGene(Genes.NS, temp);

        temp = new BitArray(UtilsMethods.bitsNeeded(off));
        temp.modifyBitArrayByNumber(off);
        setGene(Genes.OFF, temp);

        temp = new BitArray(UtilsMethods.bitsNeeded(dd));
        temp.modifyBitArrayByNumber(dd);
        setGene(Genes.DD, temp);

        temp = new BitArray(ConstantsClass.DATA_POLARITY_SIZE);
        temp.modifyBitArrayByNumber(dp);
        setGene(Genes.DP, temp);
    }

    /**
     * Copy constructor. Creates a deep copy of another Chromosome.
     *
     * @param other The Chromosome to copy.
     */
    public Chromosome(Chromosome other) {
        this.flexibleGene = other.flexibleGene.clone();
        this.genes = other.genes.clone();
        this.fitnessScore = other.fitnessScore;

        // Deep copy genesOrder and genesStartingIndex if necessary
        this.genesOrder = Arrays.copyOf(other.genesOrder, other.genesOrder.length);
        this.genesStartingIndex = Arrays.copyOf(other.genesStartingIndex, other.genesStartingIndex.length);

    }

    /**
     * Calculates the sizes of the number of swaps and offset genes based on the image size.
     *
     * @param dataSizeInBits Size of the image in bits
     * @return Total size required for the number of swaps and offset genes
     */
    public static int calculateNSandOFFGenesSizes(int dataSizeInBits){
        return UtilsMethods.bitsNeeded(UtilsMethods.numberOfSwapsForData(dataSizeInBits));
    }

    /**
     * Sets the gene size manager with the sizes of number of swaps and offset genes.
     *
     * @param numberOfSwapsSize Size of the number of swaps gene
     * @param offsetSize Size of the offset gene
     */
    private void setGeneSizeManager(int numberOfSwapsSize, int offsetSize){
        geneSizeManager = new GeneSizeManager();
        geneSizeManager.setGeneSize(Genes.NS, numberOfSwapsSize);
        geneSizeManager.setGeneSize(Genes.OFF, offsetSize);
        geneSizeManager.setGeneSize(Genes.DD, ConstantsClass.DATA_DIRECTION_SIZE);
        geneSizeManager.setGeneSize(Genes.DP, ConstantsClass.DATA_POLARITY_SIZE);
    }

    /**
     * Initiates the chromosome by randomly setting the bits of each gene.
     */
    public void initiateChromosome() {
        initiateGene(flexibleGene);
        initiateGene(genes);

        setIndexesForGenes(flexibleGene.toInt());
    }

    /**
     * Initiates a given gene by randomly setting its bits.
     *
     * @param gene The gene to be initiated.
     */
    private void initiateGene(BitArray gene) {
        for (int i = 0; i < gene.size(); i++) {
            gene.set(i, random.nextInt(2) == 1);
        }

    }

    /**
     * Mutates the chromosome by applying mutations to each gene.
     */
    public void mutateChromosome() {
        for (Genes gene : genesOrder){
            mutateGene(getGeneStartingIndex(gene), geneSizeManager.getGeneSize(gene));
        }
    }

    /**
     * mutate a given gene by flipping its bits
     * @param start the starting index of this gene.
     * @param geneSize the size of this gene.
     */
    private void mutateGene(int start, int geneSize) {

        for (int i = 0; i < geneSize; i++) {
            if (random.nextDouble() <= MUTATION_PROBABILITY)
                genes.flip(start + i);
        }
    }

    /**
     * Modifies the genes array based on the value of the flexible gene.
     * This involves rearranging the genes according to the decoded permutation represented by the flexible gene's value.
     *
     * @param flexibleGeneValue The value of the flexible gene used to determine the permutation.
     */
    public void setIndexesForGenes(int flexibleGeneValue) {
        flexibleGeneValue %= ConstantsClass.POSSIBLE_COMBINATIONS_AMOUNT_FOR_FLEXIBLE_GENE;
        int tempFlexibleGeneValue = flexibleGeneValue;
        List<Genes> availableGenes = new ArrayList<>(Arrays.asList(Genes.NS, Genes.OFF, Genes.DD, Genes.DP));

        int[] factorials = new int[GENES_AMOUNT - 1];
        for (int i = 0; i < GENES_AMOUNT - 1; i++){
            factorials[i] = UtilsMethods.factorial(GENES_AMOUNT - 1 - i);
        }

        for (int i = 0; i < GENES_AMOUNT - 1; i++) { // Only need to calculate the first 3 positions
            int pos = tempFlexibleGeneValue / factorials[i] % (GENES_AMOUNT - i); // Ensure pos is within the current list size
            tempFlexibleGeneValue %= factorials[i];
            genesOrder[i] = availableGenes.remove(pos);
        }

        // Last number is the remaining one
        genesOrder[GENES_AMOUNT - 1] = availableGenes.get(0);

        setIndexes(genesOrder);

        flexibleGene.modifyBitArrayByNumber(flexibleGeneValue);
    }

    /**
     * Sets starting indexes for each gene based on the order of genes.
     *
     * @param genesArr Order of genes
     */
    private void setIndexes(Genes[] genesArr) {
        genesStartingIndex[0] = 0;
        int index = 1;
        for (int i = 0; i < genesArr.length - 1; i++) {
            int  x= genesStartingIndex[index - 1];
            int y = geneSizeManager.getGeneSize(genesArr[i]);
            genesStartingIndex[index++] =  x + y;
        }
    }

    /**
     * Retrieves the fitness score of the chromosome.
     *
     * @return Fitness score of the chromosome
     */
    public double getFitnessScore() {
        return fitnessScore;
    }

    /**
     * Sets the fitness score of the chromosome.
     *
     * @param fitnessScore Fitness score of the chromosome
     */
    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    /**
     * Finds the index of a gene in the order of genes.
     *
     * @param gene Gene to find the index of
     * @return Index of the gene, -1 if not found
     */
    private int findIndexByGeneName(Genes gene) {
        for (int i = 0; i < genesOrder.length; i++){
            if(gene.equals(genesOrder[i]))
                return i;
        }
        return -1;
    }

    /**
     * Retrieves a specific gene from the chromosome.
     *
     * @param gene Gene to retrieve
     * @return Gene value as a BitArray
     */
    public BitArray getGene(Genes gene) {
        int indexInGeneArr = findIndexByGeneName(gene);
        int startIndex = getGeneStartingIndex(gene);
        int endIndex = (indexInGeneArr == genesOrder.length - 1) ? genes.size() : genesStartingIndex[indexInGeneArr + 1];
        BitArray geneVal = new BitArray(endIndex  - startIndex);
        int geneIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            geneVal.set(geneIndex++, genes.get(i));
        }
        return geneVal;
    }

    /**
     * Sets a specific gene in the chromosome.
     *
     * @param gene Gene to set
     * @param value Value to set the gene to
     */
    public void setGene(Genes gene, BitArray value) {
        int indexInGeneArr = findIndexByGeneName(gene);
        int startIndex = getGeneStartingIndex(gene);
        int endIndex = (indexInGeneArr == genesOrder.length - 1) ? genes.size() : genesStartingIndex[indexInGeneArr + 1];
        int index = 0;
        for (int i = startIndex; i < endIndex; i++) {
            genes.set(i, value.get(index++));
        }
    }

    /**
     * Retrieves the starting index of a gene.
     *
     * @param gene Gene to retrieve the starting index for
     * @return Starting index of the gene
     */
    private int getGeneStartingIndex(Genes gene){
        int indexInGeneArr = findIndexByGeneName(gene);
        return genesStartingIndex[indexInGeneArr];
    }

    /**
     * Retrieves the flexible gene.
     *
     * @return Flexible gene as a BitArray
     */
    public BitArray getFlexibleGene() {
        return flexibleGene;
    }

    /**
     * Retrieves the chromosome genes.
     *
     * @return Chromosome genes as a BitArray
     */
    public BitArray getGenes() {
        return genes;
    }

    /**
     * Retrieves the order of genes in the chromosome.
     *
     * @return Order of genes as an array of Genes
     */
    public Genes[] getGenesOrder(){
        return this.genesOrder;
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

    /**
     * Returns a string representation of the chromosome.
     *
     * @return String representation of the chromosome
     */
    @Override
    public String toString() {
//        return "Flexible Gene: " + flexibleGene.toString() + "\n" +
//                "Number of Swaps Gene: " + getGene(Genes.NS).toString() + "\n" +
//                "Offset Gene: " + getGene(Genes.OFF).toString() + "\n" +
//                "Data Polarity Gene: " + getGene(Genes.DP).toString() + "\n" +
//                "Data Direction Gene: " + getGene(Genes.DD).toString() + "\n" +
              return   "Fitness: "+ getFitnessScore()+ "\n";
    }

    public static void main(String[] args) {
        Chromosome c = new Chromosome(128);
        Chromosome c1 = new Chromosome(128);
        System.out.println(c);
        System.out.println(c1);
        c.setIndexesForGenes(0);
        System.out.println(c);
    }
}
