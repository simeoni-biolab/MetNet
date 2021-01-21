/*
MetNet: comparison of Metabolic Networks
 */
package MetNet.Models;

/*
Timer to measure the execution time of the comparison
*/    
public class ExecutionTimer {
    
    private static long startTime;
    private static long endTime;
 
    protected static void startTest() {
        startTime = System.nanoTime();
    }
 
    protected static void finishTest() {
        endTime = System.nanoTime();
        final long duration = endTime - startTime;
        System.out.println("Duration: " + duration/1000000 + "ms" );
    }

}
