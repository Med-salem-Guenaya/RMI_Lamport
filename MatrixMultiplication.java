import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplication extends Remote {
    int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) throws RemoteException;
}
