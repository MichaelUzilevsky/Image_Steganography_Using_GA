package model.genetic_algorithm.crossover;

import model.data_managers.BitArray;
import model.genetic_algorithm.population_structure.Chromosome;
import model.genetic_algorithm.population_structure.Genes;

import java.util.ArrayList;
import java.util.List;

public class MultiPointCrossover implements CrossoverStrategy{
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
        MultiPointCrossover multiPointCrossover = new MultiPointCrossover();
        Chromosome chromosome1 = new Chromosome(4,4);
        Chromosome chromosome2 = new Chromosome(4,4);
        System.out.println(chromosome1);
        System.out.println(chromosome2);

        for (Chromosome c : multiPointCrossover.crossover(chromosome1, chromosome2)){
            System.out.println(c);
        }

    }
}
