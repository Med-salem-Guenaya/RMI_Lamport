import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CentralComputeServer extends Remote {
    int[][] multiplyPartialWithTimestamp(int[][] matrixA, int[][] matrixB, int startRow, int endRow, int workerTimestamp) throws RemoteException;
    // New method to return the Lamport timestamp to the worker server
    int getTimestamp() throws RemoteException;
}
