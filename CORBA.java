//ReverseServer.java
import ReverseApp.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.io.*;

public class ReverseServer {
    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, null);
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            ReverseImpl reverseImpl = new ReverseImpl();
            reverseImpl.setORB(orb);

            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(reverseImpl);
            Reverse href = ReverseHelper.narrow(ref);

            // Write IOR to a file
            FileOutputStream file = new FileOutputStream("ReverseIOR.txt");
            PrintWriter writer = new PrintWriter(file);
            writer.println(orb.object_to_string(href));
            writer.close();

            System.out.println("Server ready...");
            orb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//ReverseClient.java
import ReverseApp.*;
        import org.omg.CORBA.*;
        import java.io.*;

public class ReverseClient {
    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, null);

            // Read IOR from file
            BufferedReader br = new BufferedReader(new FileReader("ReverseIOR.txt"));
            String ior = br.readLine();
            br.close();

            org.omg.CORBA.Object objRef = orb.string_to_object(ior);
            Reverse reverseRef = ReverseHelper.narrow(objRef);

            String input = "CORBA is cool";
            String reversed = reverseRef.reverse_string(input);
            String upperCase = reverseRef.uppercase(input);

            System.out.println("Original: " + input);
            System.out.println("Reversed: " + reversed);
            System.out.println("Uppercase: " + upperCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//ReverseImpl.java
import ReverseApp.*;
        import org.omg.CORBA.*;

public class ReverseImpl extends ReversePOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public String reverse_string(String str) {
        return new StringBuilder(str).reverse().toString();
    }
    public String uppercase(String str) {
        return str.toUpperCase();
    }
}

//Reverse.idl
module ReverseApp {
    interface Reverse {
        string reverse_string(in string str);
        string uppercase(in string str);
    };
};



//Commands
//idlj -fall Reverse.idl
//        orbd -ORBInitialPort 1050 &
//        javac *.java
//        java ReverseServer -ORBInitialPort 1050 -ORBInitialHost localhost
//        2nd terminal
//        javac *.java
//        java ReverseClient -ORBInitialPort 1050 -ORBInitialHost localhost
//        sudo apt update
//        sudo apt install openjdk-8-jdk
//        sudo update-alternatives --config java
//        sudo update-alternatives --config javac





