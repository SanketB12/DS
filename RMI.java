//Client.java
import java.rmi.*;
import java.util.*;
public class Client{
    public static void main(String [] args)
    {
        try{
            String serverUrl="rmi://localhost/Server";
            ServerIntf serverIntf= (ServerIntf) Naming.lookup(serverUrl);
            Scanner sc=new Scanner(System.in);
            double num1=sc.nextDouble();
            double num2=sc.nextDouble();
            System.out.println("Num1 is: "+num1);
            System.out.println("Num2 is: "+num2);
            double result1=serverIntf.add(num1,num2);
            double result2=serverIntf.sub(num1,num2);
            double result3=serverIntf.mul(num1,num2);
            double result4=serverIntf.div(num1,num2)
            System.out.println("Result of Addition = "+result1);
            System.out.println("Result of Subtraction = "+result2);
            System.out.println("Result of Multiplication = "+result3);
            System.out.println("Result of Division = "+result4);
        }
        catch(Exception e){
            System.out.println("Exception Occured"+e.getMessage());
        }
    }
}

//Server.java
import java.rmi.*;
public class Server{
    public static void main(String [] args)
    {
        try{
            ServerImpl serverImpl=new ServerImpl();
            Naming.rebind("Server",serverImpl);
            System.out.println("Server Started ...");
        }
        catch(Exception e){
            System.out.println("Exception Occured"+e.getMessage());
        }
    }
}

//ServerImpl.java
import java.rmi.*;
import java.rmi.server.*;
public class ServerImpl extends UnicastRemoteObject implements ServerIntf{
    public 	ServerImpl() throws RemoteException{
    }
    public double add(double d1, double d2) throws RemoteException{
        return d1+d2;
    }
    public double sub(double d1, double d2) throws RemoteException{
        return d1-d2;
    }
    public double mul(double d1, double d2) throws RemoteException{
        return d1*d2;
    }
    public double div(double d1, double d2) throws RemoteException{
        try{
            return d1/d2;
        }
        catch (ArithmeticException e)
        {
            System.out.print("Cannot divide by Zero"+e.getMessage());
            return Double.NaN;
        }
    }
}

//ServerIntf.java
import java.rmi.*;
public interface ServerIntf extends Remote {
    public double add(double d1, double d2) throws RemoteException;
    public double sub(double d1, double d2) throws RemoteException;
    public double mul(double d1, double d2) throws RemoteException;
    public double div(double d1, double d2) throws RemoteException;
}

//Commands
//1st terminal
//        java *.java
//        rmiregistry
//
//        2nd terminal
//        java Server
//
//        3rd terminal
//        java Client
//        Enter Input
//
//        Formulas:
//        P*R*N/100 simple interest
//        F=C*(9/5) + 32
//        Miles=Kilometer*1.6


