package model.genetic_algorithm.population_structure.populations;

import java.lang.reflect.Array;
import java.util.Random;

public class FixedSizePriorityQueue<T extends Comparable<T>> {
    private final T[] elements;
    private int size;
    private static final Random random = new Random();

    @SuppressWarnings("unchecked")
    public FixedSizePriorityQueue(Class<T> clazz, int capacity) {
        // Initialize the elements array as a Comparable array, then cast to T[]
        this.elements = (T[]) Array.newInstance(clazz, capacity);
        this.size = 0;
    }

    public void insert(T element) {
        if (size >= elements.length) {
            throw new IllegalStateException("Heap is full");
        }
        elements[size] = element;
        bubbleUp(size);
        size++;
    }

    public T extractMax() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        T max = elements[0]; // Max element is at the root of the heap
        elements[0] = elements[size - 1];
        size--;
        bubbleDown(0);
        return max;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (elements[index].compareTo(elements[parentIndex]) <= 0) {
                break; // The heap property is satisfied
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void bubbleDown(int index) {
        int largest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < size && elements[left].compareTo(elements[largest]) > 0) {
            largest = left;
        }
        if (right < size && elements[right].compareTo(elements[largest]) > 0) {
            largest = right;
        }
        if (largest != index) {
            swap(index, largest);
            bubbleDown(largest);
        }
    }

    private void swap(int i, int j) {
        T temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void buildMaxHeap(T[] array) {
        if (array.length > elements.length) {
            throw new IllegalArgumentException("Input array is larger than the heap capacity");
        }
        System.arraycopy(array, 0, elements, 0, array.length);
        size = array.length;
        for (int i = size / 2 - 1; i >= 0; i--) {
            bubbleDown(i);
        }
    }

    public void printHeap() {
        if (!isEmpty()) {
            T x = extractMax();
            System.out.println(x);
            printHeap();
            insert(x);
        }
        else
            System.out.println();
    }

    public T extractRandomInRange(int start, int end) {
        if (start < 0 || end > size || start > end) {
            throw new IllegalArgumentException("Invalid range.");
        }
        // Generate a random index within the specified range.
        int randomIndex = start + random.nextInt(start, end);

        // Swap the randomly selected element with the last element in the heap.
        swap(randomIndex, size - 1);

        // Extract the element from the end of the heap.
        T extractedElement = elements[size - 1];
        size--; // Reduce the size of the heap.

        // Reorganize the heap starting from the randomly selected index.
        bubbleDown(randomIndex);

        return extractedElement;
    }

    public T getHighest(){
        return elements[0];
    }

    public T[] getElements(){
        return  this.elements;
    }

    public static void main(String[] args) {
        FixedSizePriorityQueue<Integer> maxHeap = new FixedSizePriorityQueue<>(Integer.class, 10);

        // Insert elements
        maxHeap.insert(3);
        maxHeap.insert(1);
        maxHeap.insert(6);

        // Extract the maximum element
        System.out.println("Max element: " + maxHeap.extractMax());

        Integer[] array = {2, 4, 5, 1, 9, 7};
        maxHeap.buildMaxHeap(array);

        maxHeap.printHeap();

        while (!maxHeap.isEmpty()){
            System.out.println(maxHeap.extractRandomInRange(0, maxHeap.size()));
            maxHeap.printHeap();
        }

    }
}
