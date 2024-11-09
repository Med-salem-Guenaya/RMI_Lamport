import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.PriorityQueue;
import java.util.Queue;

public class CentralComputeServerImpl implements CentralComputeServer {
    private final Queue<Request> requestQueue = new PriorityQueue<>();
    private int lamportClock = 0;

    public CentralComputeServerImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) {
        // Incrémentation de l'horloge logique de Lamport
        lamportClock++;
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
        System.out.println("CentralComputeServer completed request for rows " + startRow + " to " + endRow);
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

    // Classe interne pour gérer les demandes avec horodatages
    private class Request implements Comparable<Request> {
        int timestamp;
        int serverId;
        //
        Request(int timestamp, int serverId) {
            this.timestamp = timestamp;
            this.serverId = serverId;
        }
        //
        @Override
        public int compareTo(Request other) {
            if (this.timestamp != other.timestamp) {
                return Integer.compare(this.timestamp, other.timestamp);
            }
            return Integer.compare(this.serverId, other.serverId);
        }
    }
}
