import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComputeServer extends Remote {
    // La méthode doit accepter les deux matrices et les indices startRow et endRow
    int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) throws RemoteException;
}
