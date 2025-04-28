//Average.java
import mpi.MPIException;
import mpi.MPI;
import java.util.*;
public class Average{
    public static void main(String []args) throws MPIException{
        MPI.Init(args);
        int rank=MPI.COMM_WORLD.Rank();
        int size=MPI.COMM_WORLD.Size();
        int root=0;
        int TotalElements=16;
        int ElementsperProcess=TotalElements/size;
        double [] Fullarray=new double [TotalElements];
        double [] Subarray=new double [ElementsperProcess];
        double [] GatherAverages=new double [size];
        if(rank==root)
        {
            Fullarray=new double [TotalElements];

            Random rand=new Random();
            System.out.println("Intitalising Random array at root: ");

            for(int i=0;i<TotalElements;i++)
            {
                Fullarray[i]=(double)(rand.nextInt(100)+1);
                System.out.print(Fullarray[i]+" ");
            }
            System.out.println();

        }
        MPI.COMM_WORLD.Scatter(Fullarray,0,ElementsperProcess,MPI.DOUBLE,
                Subarray,0,ElementsperProcess,MPI.DOUBLE,
                root);
        double localsum=0;

        for(double val:Subarray)
        {
            localsum+=val;
        }
        double localavg=(1.0*localsum)/ElementsperProcess;
        System.out.println("Process"+rank+" local average: "+localavg);


        MPI.COMM_WORLD.Gather(new double[]{localavg},0,1,MPI.DOUBLE,
                GatherAverages,0,1,MPI.DOUBLE,
                root);
        if(rank==root){
            double totalavg=0;
            for(double avg:GatherAverages)
            {
                totalavg+=avg;
            }
            totalavg=(1.0*totalavg)/size;
            System.out.println("Final Average is: "+totalavg);

        }
        MPI.Finalize();
    }
}


//DistributedSum.java
import mpi.MPI;
import mpi.MPIException;

public class DistributedSum {

    public static void main(String[] args) throws MPIException {

        // Initialize MPI
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();     // Current process ID
        int size = MPI.COMM_WORLD.Size();     // Total number of processes
        int root = 0;

        int[] sendBuffer=new int [size];
        int[] recvBuffer = new int[1]; // EVERY process must allocate this
        int[] gatherBuffer=new int [size];

        if (rank == root) {
            sendBuffer = new int[size];
            System.out.println("Root initializing data:");
            for (int i = 0; i < size; i++) {
                sendBuffer[i] = i + 1;
                System.out.println("Element[" + i + "] = " + sendBuffer[i]);
            }

        }
        // Distribute one element to each process
        MPI.COMM_WORLD.Scatter(
                sendBuffer, 0, 1, MPI.INT,
                recvBuffer, 0, 1, MPI.INT,
                root);


        int localproduct=recvBuffer[0];
        System.out.println("Process " + rank + " received " + recvBuffer[0] +
                " and computed intermediate sum: " + localproduct);

        // Gather all intermediate sums to root
        MPI.COMM_WORLD.Gather(
                new int[]{localproduct}, 0, 1, MPI.INT,
                gatherBuffer, 0, 1, MPI.INT,
                root);

        // Root computes final sum
        if (rank == root) {
            int totalPro = 1;
            for (int val : gatherBuffer) {
                totalPro *= val;
            }
            System.out.println("Final sum at root: " + totalPro);
        }

        MPI.Finalize();
    }
}



//Reciprocal.java
import mpi.MPIException;
import mpi.MPI;
import java.util.*;

public class Reciprocal{
    public static void main(String []args) throws MPIException{
        MPI.Init(args);

        int rank=MPI.COMM_WORLD.Rank();
        int size=MPI.COMM_WORLD.Size();

        int root=0;

        int [] sendBuffer=new int[size];
        int [] recvBuffer=new int[1];
        double [] gatherBuffer=new double[size];
        if(rank==root)
        {
            sendBuffer=new int [size];
            System.out.println("Intitalising array at root: ");

            for(int i=0;i<size;i++)
            {
                sendBuffer[i]=i+1;

                System.out.println("Element "+i+"="+sendBuffer[i]);
            }
        }
        MPI.COMM_WORLD.Scatter(sendBuffer,0,1,MPI.INT,
                recvBuffer,0,1,MPI.INT,
                root);

        double localreciprocal=1.0/recvBuffer[0];

        System.out.println("Process "+rank+" "+"received: "+recvBuffer[0]+" and generated: "+localreciprocal);

        MPI.COMM_WORLD.Gather(new double[]{localreciprocal},0,1,MPI.DOUBLE,
                gatherBuffer,0,1,MPI.DOUBLE,
                root);

        if(rank==root){
            System.out.println("Final Array of Reciprocal at root: ");

            for(int i=0;i<size;i++)
            {
                System.out.printf("1/%d= %.4f\n",sendBuffer[i],gatherBuffer[i]);
            }
        }

        MPI.Finalize();
    }
}

//Commands
//nano ~/.bashrc
//        export MPJ_HOME=/home/..../mpj
//        export PATH=$PATH:$MPJ_HOME/bin
//        source ~/.bashrc
//
//        javac -cp mpj/lib/mpj.jar fileName.java
//        mpjrun.sh -cp . -np 4 fileName
//
//        (extract mpj in same directory)
//        https://sourceforge.net/projects/mpjexpress/files/releases/
//
//        Crtl O enter cntrl x


