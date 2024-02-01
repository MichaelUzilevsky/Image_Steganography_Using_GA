package model.genetic_algorithm.population_structure;

import java.util.EnumMap;
import java.util.Map;

public class GeneSizeManager {
    private final Map<Genes, Integer> geneSizes;

    public GeneSizeManager() {
        this.geneSizes = new EnumMap<>(Genes.class);
    }

    public void setGeneSize(Genes gene, int size) {
        geneSizes.put(gene, size);
    }

    public int getGeneSize(Genes gene) {
        return geneSizes.getOrDefault(gene, 0);
    }

}

