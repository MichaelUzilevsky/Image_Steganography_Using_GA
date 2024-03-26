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
 * This chromosome contains five genes, with three having fixed sizes and the others calculated based on the image size.
 */
public class Chromosome implements Comparable<Chromosome> {
    public static final Random random = new Random();

    private static GeneSizeManager geneSizeManager;
    private final int[] genesStartingIndex;
    private final Genes[] genesOrder;
    private final BitArray flexibleGene;
    private final BitArray genes;
    private double fitnessScore;


    /**
     * Initializes a new Chromosome instance for a given image size.
     *
     */
    public Chromosome(int dataSizeInBits) {

        int offsetSize, numberOfSwapsSize = offsetSize = calculateNSandOFFGenesSizes(dataSizeInBits);

        this.flexibleGene = new BitArray(ConstantsClass.FLEXIBLE_GENE_SIZE);
        this.genes = new BitArray(ConstantsClass.DATA_DIRECTION_SIZE + ConstantsClass.DATA_POLARITY_SIZE + numberOfSwapsSize + offsetSize);

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        this.fitnessScore = -1;

        genesOrder = new Genes[4];
        genesStartingIndex = new int[4];

        initiateChromosome();
    }

    public Chromosome(BitArray flexibleGene, BitArray genes, int numberOfSwapsSize, int offsetSize){
        this.genes = genes;
        this.flexibleGene = flexibleGene;

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        genesOrder = new Genes[4];
        genesStartingIndex = new int[4];

        setIndexesForGenes(flexibleGene.toInt());
    }

    public Chromosome(int fc, int ns, int off, int dd, int dp) {
        int offsetSize = UtilsMethods.bitsNeeded(off), numberOfSwapsSize = UtilsMethods.bitsNeeded(ns);

        this.flexibleGene = new BitArray(ConstantsClass.FLEXIBLE_GENE_SIZE);
        this.genes = new BitArray(ConstantsClass.DATA_DIRECTION_SIZE + ConstantsClass.DATA_POLARITY_SIZE + numberOfSwapsSize + offsetSize);

        setGeneSizeManager(numberOfSwapsSize, offsetSize);

        this.fitnessScore = -1;

        genesOrder = new Genes[4];
        genesStartingIndex = new int[4];

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

    public Chromosome(Chromosome other) {
        this.flexibleGene = other.flexibleGene.clone();
        this.genes = other.genes.clone();
        this.fitnessScore = other.fitnessScore;

        // Deep copy genesOrder and genesStartingIndex if necessary
        this.genesOrder = Arrays.copyOf(other.genesOrder, other.genesOrder.length);
        this.genesStartingIndex = Arrays.copyOf(other.genesStartingIndex, other.genesStartingIndex.length);

    }

    public static int calculateNSandOFFGenesSizes(int dataSizeInBits){
        return UtilsMethods.bitsNeeded(UtilsMethods.numberOfSwapsForData(dataSizeInBits));
    }

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
        double mutationProbability = 1.0 / geneSize;
        for (int i = 0; i < geneSize; i++) {
            if (random.nextDouble() <= mutationProbability)
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
        flexibleGeneValue %= 24;
        int tempFlexibleGeneValue = flexibleGeneValue;
        List<Genes> availableGenes = new ArrayList<>(Arrays.asList(Genes.NS, Genes.OFF, Genes.DD, Genes.DP));
        int[] factorials = {6, 2, 1}; // Factorials for n-1, n-2, n-3 (for n=4)

        for (int i = 0; i < 3; i++) { // Only need to calculate the first 3 positions
            int pos = tempFlexibleGeneValue / factorials[i] % (4 - i); // Ensure pos is within the current list size
            tempFlexibleGeneValue %= factorials[i];
            genesOrder[i] = availableGenes.remove(pos);
        }

        // Last number is the remaining one
        genesOrder[3] = availableGenes.get(0);

        setIndexes(genesOrder);

        flexibleGene.modifyBitArrayByNumber(flexibleGeneValue);
    }

    private void setIndexes(Genes[] genesArr) {
        genesStartingIndex[0] = 0;
        int index = 1;
        for (int i = 0; i < genesArr.length - 1; i++) {
            int  x= genesStartingIndex[index - 1];
            int y = geneSizeManager.getGeneSize(genesArr[i]);
            genesStartingIndex[index++] =  x + y;
        }
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    private int findIndexByGeneName(Genes gene) {
        for (int i = 0; i < genesOrder.length; i++){
            if(gene.equals(genesOrder[i]))
                return i;
        }
        return -1;
    }

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

    public void setGene(Genes gene, BitArray value) {
        int indexInGeneArr = findIndexByGeneName(gene);
        int startIndex = getGeneStartingIndex(gene);
        int endIndex = (indexInGeneArr == genesOrder.length - 1) ? genes.size() : genesStartingIndex[indexInGeneArr + 1];
        int index = 0;
        for (int i = startIndex; i < endIndex; i++) {
            genes.set(i, value.get(index++));
        }
    }

    private int getGeneStartingIndex(Genes gene){
        int indexInGeneArr = findIndexByGeneName(gene);
        return genesStartingIndex[indexInGeneArr];
    }

    public BitArray getFlexibleGene() {
        return flexibleGene;
    }

    public BitArray getGenes() {
        return genes;
    }

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
