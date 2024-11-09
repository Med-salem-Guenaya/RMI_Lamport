import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CentralComputeServerImpl implements CentralComputeServer {
    private int lamportClock = 0;

    @Override
    public synchronized int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) throws RemoteException {
        lamportClock++; // Increment Lamport clock for each request
        System.out.println("CentralComputeServer received request with timestamp: " + lamportClock);

        // Perform matrix multiplication for the requested rows
        int[][] result = new int[endRow - startRow][matrixB[0].length];
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                result[i - startRow][j] = 0;
                for (int k = 0; k < matrixA[i].length; k++) {
                    result[i - startRow][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        System.out.println("CentralComputeServer completed request for rows " + startRow + " to " + (endRow-1));

        return result;
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
