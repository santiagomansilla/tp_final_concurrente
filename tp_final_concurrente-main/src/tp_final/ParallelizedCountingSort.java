package tp_final;

import static tp_final.ArrayOperations.isSorted;
import static tp_final.ArrayOperations.randomizeArray;



public class ParallelizedCountingSort {
	
	private static final int N_ITEMS = 30_000_000;
	
	//private static final int N_ITEMS = 20_000_000;
	
	//private static final int N_ITEMS = 10_000_000;
	
	//private static final int N_ITEMS = 1_000_000;

	 //private static final int N_ITEMS = 100_000;

	 //private static final int N_ITEMS = 10_000;

	 //private static final int N_ITEMS = 1_000;
	
	
	
    private static final int MAX_VAL = 20_000;

    private static final int N_THREADS = 4;

    private static int[] array, output;

    private static Thread[] threads;
    private static CountSortThread[] sorts;

    public static void hideSort() {
        threads = new Thread[N_THREADS];
        sorts = new CountSortThread[N_THREADS];

        array = new int[N_ITEMS];
        output = new int[N_ITEMS];
     

        randomizeArray(array, N_ITEMS, MAX_VAL);

        // Inicialización de hilos
        for (int i = 1; i <= N_THREADS; i++)
            sorts[i - 1] = new CountSortThread(i, array);
        for (int i = 0; i < N_THREADS; i++)
            threads[i] = new Thread(sorts[i]);

        var t0 = System.currentTimeMillis();
        for (int i = 0; i < N_THREADS; i++)
            threads[i].start();

        boolean exited = false;
        while (!exited) {
            exited = true;
            int i;
            for (i = 0; i < N_THREADS; i++)
                if (threads[i].isAlive())
                    exited = false;
                else {
                   int idx = sorts[i].start;
                   for (int j = 0; j < sorts[i].size; j++) {
                        if (idx < N_ITEMS) {
                            output[idx] = sorts[i].output[j];
                            idx++;
                        }
                    }
                }
        }

        int idx = 0;
        for(int i=0;i<N_THREADS;i++){
            for(int j=0;j<sorts[i].size;j++){
                if(idx < N_ITEMS){
                    output[idx] = sorts[i].output[j];
                    idx++;
                }
            }
        }

        var t1 = System.currentTimeMillis();
        System.out.println("ORDENAMIENTO CONCURRENTE TARDO: " + (t1 - t0) + " milisegundos");
        System.out.println("Array is sorted:" + isSorted(output, N_ITEMS));
    
     // impresion del array ordenado concurrentemente
     		// System.out.println("impresion del array concurrente:");
     		/*
     		 * for (int i = 0; i < N_ITEMS; i++) { System.out.print("[" + output[i] + "]");
     		 * }
     		 */
     		System.out.println(); // salto de línea

     		
    
    }




	private static void countSort(int[] array, int[] output, int[] count, int size) {
		int i;
		for (i = 1; i <= size; i++)
			count[array[i]]++;
		
		//Sumo en la posicion del array la posicion anterior
		for (i = 1; i < count.length; i++)
			count[i] += count[i - 1];
		
		
		for (i = size; i >= 1; i--) {
			output[count[array[i]]] = array[i];
			count[array[i]]--;
		}
	}
	
	private static class CountSortThread implements Runnable {
		public int[] array, output, count;
		private int size, width;

		public int start, end, id;

		public CountSortThread(int id, int[] input) { // id es 1-4 incl.
			this.id = id;

			width = MAX_VAL / N_THREADS;
			size = 0;

			start = (id - 1) * width;
			end = id * (width + 1);

			for (int i = 0; i < N_ITEMS; i++)
				if (start <= input[i] && input[i] < end)
					size++;

//			array = new int[size + 1];
//			output = new int[size + 1];
//			count = new int[N_ITEMS + 1];
			
			array = new int[N_ITEMS];
            output = new int[N_ITEMS];
            count = new int[end - start];
            /*
			int addIdx = 0;
			for (int i = 0; i < N_ITEMS; i++) {
				if ((id - 1) * width <= input[i] && input[i] < id * (width + 1)) {
					array[addIdx] = input[i];
					addIdx++;
				}
			}*/
		}

		/**
		 * Ejecuta el ordenamiento en la subsección del array dada.
		 */
		@Override
		public void run() {
			countSort(array, output, count, size);/*
			for (int j = start; j < start + size; j++) {
				if (j < N_ITEMS) {
					ParallelizedCountingSort.output[j] = output[j - start];
				}
			}*/
		}
	}
}