import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CentralComputeServer extends Remote {
    int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) throws RemoteException;
}
