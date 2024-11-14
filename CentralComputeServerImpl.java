import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.PriorityQueue;
import java.util.Queue;

public class CentralComputeServerImpl implements CentralComputeServer {
    private int lamportClock = 0;

    @Override
    public synchronized int[][] multiplyPartialWithTimestamp(int[][] matrixA, int[][] matrixB, int startRow, int endRow, int workerTimestamp) throws RemoteException {
        System.out.println("CentralComputerServer received request with timestamp:" + workerTimestamp);
        // Synchronize the Lamport clock with the worker's timestamp
        lamportClock = Math.max(lamportClock, workerTimestamp) + 1;
        System.out.println("CentralComputeServer updated timestamp to: " + lamportClock);


        // Perform matrix multiplication for the requested rows
        int[][] result = new int[endRow - startRow][matrixB[0].length];

        // Calcul du produit partiel de matrices
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                result[i - startRow][j] = 0;
                for (int k = 0; k < matrixA[i].length; k++) {
                    result[i - startRow][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        System.out.println("CentralComputeServer completed request for rows " + startRow + " to " + (endRow-1));
        printMatrix(result, "Result sent back to worker:");

        return result;
    }

    private void printMatrix(int[][] matrix, String message) {
        System.out.println(message);
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    @Override
    public synchronized int getTimestamp() throws RemoteException {
        // Return the current Lamport timestamp
        return lamportClock;
    }

    public static void main(String[] args) {
        try {
            CentralComputeServerImpl centralServer = new CentralComputeServerImpl();
            CentralComputeServer stub = (CentralComputeServer) UnicastRemoteObject.exportObject(centralServer, 0);

            Registry registry = LocateRegistry.getRegistry(1099);
            registry.rebind("CentralComputeServer", stub);

            System.out.println("CentralComputeServer is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
