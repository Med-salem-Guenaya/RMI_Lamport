import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements MatrixMultiplication {

    @Override
    public int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) throws RemoteException {
        try {
            // Connect to the registry of worker servers (ComputeServer instances)
            int numServers = 5;  // Number of available ComputeServer instances
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ComputeServer[] computeServers = new ComputeServer[numServers];

            // Lookup each compute server from the registry
            for (int i = 0; i < numServers; i++) {
                computeServers[i] = (ComputeServer) registry.lookup("ComputeServer" + i);
                System.out.println("Connected to ComputeServer" + i);
            }

            // Divide the rows of matrixA between the compute servers
            int numRows = matrixA.length;
            int rowsPerServer = numRows / numServers; // base rows per worker
            int remainder = numRows % numServers; // remainder rows to be distributed

            int[][] partialResults = new int[matrixA.length][matrixB[0].length];

            // Assign the matrix multiplication work to each server
            int currentRow = 0;
            for (int i = 0; i < numServers; i++) {
                // Calculate start and end row for each worker
                int startRow = currentRow;
                int endRow = startRow + rowsPerServer + (i < remainder ? 1 : 0); // Add extra row if within remainder

                currentRow = endRow; // Update the current row position for the next worker

                // Call the compute server to handle partial matrix multiplication
                int[][] resultPart = computeServers[i].multiplyPartial(matrixA, matrixB, startRow, endRow);

                // Collect partial results from each server
                for (int r = startRow; r < endRow; r++) {
                    partialResults[r] = resultPart[r - startRow];
                }
            }

            return partialResults; // Return the final result after collecting from all servers
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        try {
            // Create the Server and register it with the RMI registry
            Server server = new Server();
            MatrixMultiplication stub = (MatrixMultiplication) UnicastRemoteObject.exportObject(server, 0);

            // Register the server under the name "MatrixMultiplication"
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MatrixMultiplication", stub);

            System.out.println("Server is ready and listening for client requests...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
