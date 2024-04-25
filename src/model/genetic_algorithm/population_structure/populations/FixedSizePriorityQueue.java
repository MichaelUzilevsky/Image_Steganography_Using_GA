package model.genetic_algorithm.population_structure.populations;

import java.lang.reflect.Array;
import java.util.Random;

/**
 * Implements a fixed-size priority queue using a binary heap. This class supports basic heap
 * operations including insert, extractMax, and building a heap from an existing array. It
 * also provides additional functionalities like extracting a random element within a range and
 * printing the heap's content.
 *
 * @param <T> the type of elements in this priority queue, must be Comparable
 */
public class FixedSizePriorityQueue<T extends Comparable<T>> {
    private final T[] elements;
    private int size;
    private static final Random random = new Random();

    /**
     * Constructs a new FixedSizePriorityQueue with a specified capacity.
     *
     * @param clazz    the Class object of the elements' type
     * @param capacity the maximum number of elements the priority queue can hold
     */
    @SuppressWarnings("unchecked")
    public FixedSizePriorityQueue(Class<T> clazz, int capacity) {
        // Initialize the elements array as a Comparable array, then cast to T[]
        this.elements = (T[]) Array.newInstance(clazz, capacity);
        this.size = 0;
    }

    /**
     * Inserts a new element into the priority queue,
     * maintaining the heap property by performing a bubble-up operation
     * starting from the newly added element.
     *
     * @param element the element to insert
     * @throws IllegalStateException if the heap is already full
     */
    public void insert(T element) {
        if (size >= elements.length) {
            throw new IllegalStateException("Heap is full");
        }
        elements[size] = element;
        bubbleUp(size);
        size++;
    }

    /**
     * Removes and returns the maximum element (root of the heap).
     * Maintains the heap property by moving the last element to the root and performing a bubble-down operation.
     *
     * @return the maximum element
     * @throws IllegalStateException if the heap is empty
     */
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

    /**
     * Performs the bubble-up operation to maintain the heap property after an insert operation.
     * This method ensures that the newly inserted element is moved up the heap until the heap
     * property is restored.
     *
     * @param index The index of the newly inserted element.
     */
    private void bubbleUp(int index) {
        boolean shouldContinue = true;
        while (index > 0 && shouldContinue) {
            int parentIndex = (index - 1) / 2;
            if (elements[index].compareTo(elements[parentIndex]) > 0) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                shouldContinue = false; // The heap property is satisfied, stop the loop
            }
        }
    }

    /**
     * Performs the bubble-down operation to maintain the heap property after an extract operation.
     * This method ensures that the heap property is restored by moving down the element at the specified
     * index until all children are smaller (or equal) than the element.
     *
     * @param index The index of the element to bubble down.
     */
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

    /**
     * Swaps two elements in the heap.
     *
     * @param i The index of the first element to swap.
     * @param j The index of the second element to swap.
     */
    private void swap(int i, int j) {
        T temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }

    /**
     * Checks if the priority queue is empty.
     *
     * @return true if the priority queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the current size of the priority queue.
     *
     * @return The number of elements in the priority queue.
     */
    public int size() {
        return size;
    }

    /**
     * Inserts all elements from an array into the heap and builds a max heap.
     * Replacing the current heap content.
     *
     * @param array the array from which elements are to be inserted
     * @throws IllegalArgumentException if the array size exceeds heap capacity
     */
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

    /**
     *Extracts a random element from a specified range within the heap,
     * maintaining the heap structure by swapping the chosen element with the last and re-adjusting the heap.
     *
     * @param start the starting index (inclusive)
     * @param end   the ending index (exclusive)
     * @return the randomly selected element
     * @throws IllegalArgumentException if the specified range is invalid
     */
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

    /**
     * Returns the highest (maximum) element in the priority queue without removing it.
     *
     * @return The highest element in the priority queue.
     */
    public T getHighest(){
        return elements[0];
    }

    /**
     * Returns an array containing all the elements in the priority queue.
     * This method provides direct access to the internal array representation of the heap.
     *
     * @return An array containing all the elements in the priority queue.
     */
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
