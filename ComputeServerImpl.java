import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeServerImpl implements ComputeServer {
    private int serverId;
    private int lamportClock;

    public ComputeServerImpl(int serverId) {
        this.serverId = serverId;
        this.lamportClock = 0;
    }

    @Override
    public int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) throws RemoteException {
        try {
            // Connect to the central compute server
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            CentralComputeServer centralServer = (CentralComputeServer) registry.lookup("CentralComputeServer");

            // Increment Lamport clock and request partial multiplication from the central server
            lamportClock++;
            System.out.println("ComputeServer" + serverId + " requesting access to compute rows " + startRow + " to " + (endRow-1) + " with Lamport clock " + lamportClock);
            return centralServer.multiplyPartial(matrixA, matrixB, startRow, endRow);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ComputeServerImpl <serverId>");
            return;
        }

        try {
            // Get the server ID from the arguments
            int serverId = Integer.parseInt(args[0]);

            // Create and export the ComputeServerImpl instance
            ComputeServerImpl obj = new ComputeServerImpl(serverId);
            ComputeServer stub = (ComputeServer) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the ComputeServer instance to the RMI registry with a unique name
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.rebind("ComputeServer" + serverId, stub);

            System.out.println("ComputeServer" + serverId + " is ready.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
