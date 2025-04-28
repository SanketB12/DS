//Client.java
import java.io.*;
import java.net.*;
import java.util.*;
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of clocks: ");
        int n = scanner.nextInt();
        int[] times = new int[n];
        writer.println(n);
        for (int i = 0; i < n; i++) {
            System.out.print("Enter time for clock " + (i + 1) + ": ");
            times[i] = scanner.nextInt();
            writer.println(times[i]);
        }
        for (int i = 0; i < n; i++) {
            int adjustment = Integer.parseInt(reader.readLine());
            System.out.println("Clock " + (i + 1) + " adjustment: " + adjustment + ", New time: " + (times[i] + adjustment));
        }
        socket.close();
        scanner.close();
    }
}

//Server.java
import java.io.*;
import java.net.*;
import java.util.*;
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started. Waiting for client...");
        Socket client = serverSocket.accept();
        System.out.println("Client connected.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        int n = Integer.parseInt(reader.readLine());
        List<Integer> times = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int time = Integer.parseInt(reader.readLine());
            times.add(time);
            System.out.println("Received time for clock " + (i + 1) + ": " + time);
        }
        int sum = 0;
        for (int time : times) sum += time;
        int avg = sum / n;
        System.out.println("Average time: " + avg);
        for (int time : times) {
            int adjustment = avg - time;
            writer.println(adjustment);
            System.out.println("Sent adjustment: " + adjustment);
        }
        client.close();
        serverSocket.close();
    }
}

//client.py
from dateutil import parser
        import threading
        import datetime
        import socket
        import time

        slave_client = socket.socket()

        def startSendingTime():
        while True:
        try:
        slave_client.send(str(datetime.datetime.now()).encode())
        print(f"[Client] Send this time to the Server: {datetime.datetime.now()}")

        time.sleep(5)
        except Exception as e:
        print("[Client Sending Error]", e)
        break

        def startReceivingTime():
        while True:
        try:
        data = slave_client.recv(1024)
        if not data:
        break
        synchronized_time = parser.parse(data.decode())
        print(f"[Client] Received synchronized time: {synchronized_time}")
        except Exception as e:
        print("[Client Receiving Error]", e)
        break

        def initiateSlaveClient(port=8080):
        try:
        slave_client.connect(('localhost', port))
        print("[Client] Connected to server successfully.")

        send_thread = threading.Thread(target=startSendingTime)
        recv_thread = threading.Thread(target=startReceivingTime)

        send_thread.start()
        recv_thread.start()

        send_thread.join()
        recv_thread.join()

        except Exception as e:
        print("[Client Connection Error]", e)

        if __name__ == "__main__":
        initiateSlaveClient(8080)

//server.py
        from dateutil import parser
        import threading
        import datetime
        import socket
        import time

        client_data = {}

        def startReceivingClockTime(connector, address):
        while True:
        try:
        clock_time_string = connector.recv(1024).decode()
        if not clock_time_string:
        break
        clock_time = parser.parse(clock_time_string)
        clock_time_difference = datetime.datetime.now() - clock_time

        client_data[address] = {
        "clock_time": clock_time,
        "time_difference": clock_time_difference,
        "connector": connector
        }

        print("Client Data updated with: " + str(address))
        time.sleep(5)

        except Exception as e:
        print(f"Connection with {address} closed.")
        break

        def startConnecting(master_server):
        while True:
        try:
        master_slave_connector, addr = master_server.accept()
        slave_address = str(addr[0]) + ":" + str(addr[1])
        print(slave_address + " got connected successfully")

        current_thread = threading.Thread(
        target=startReceivingClockTime,
        args=(master_slave_connector, slave_address,)
        )
        current_thread.start()

        except socket.timeout:
        continue  # allow checking if server is shutting down
        except Exception as e:
        print("Error in accepting connection:", e)
        break

        def calculateAvgDifference():
        current_client_data = client_data.copy()
        if len(current_client_data) == 0:
        return datetime.timedelta(0)

        time_difference_list = [
        client["time_difference"]
        for client_addr, client in current_client_data.items()
        ]

        sum_of_clock_difference = sum(
        time_difference_list,
        datetime.timedelta(0, 0)
        )

        average_clock_difference = (
        sum_of_clock_difference / len(current_client_data)
        )

        return average_clock_difference

        def synchronizeAllClocks():
        while True:
        try:
        print("\nNew synchronization cycle started.")
        if len(client_data) > 0:
        average_clock_difference = calculateAvgDifference()
        for client_addr, client in list(client_data.items()):
        try:
        synchronize_time = datetime.datetime.now() + average_clock_difference
        client['connector'].send(str(synchronize_time).encode())
        except Exception as e:
        print(f"Unable to send time to {client_addr}. Removing client.")
        del client_data[client_addr]
        else:
        print("No clients connected. Synchronization not applicable.")

        time.sleep(5)

        except Exception as e:
        print("Error during synchronization:", e)
        break

        def initiateClockServer(port=8080):
        master_server = socket.socket()
        master_server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        master_server.settimeout(1)  # Add timeout to accept

        print("Socket at master node created successfully\n")
        master_server.bind(('', port))

        master_server.listen(10)
        print("Clock server started...\n")

        try:
        print("Starting to make connections...\n")
        master_thread = threading.Thread(
        target=startConnecting,
        args=(master_server,)
        )
        master_thread.start()

        print("Starting synchronization parallelly...\n")
        sync_thread = threading.Thread(
        target=synchronizeAllClocks,
        args=()
        )
        sync_thread.start()

        master_thread.join()
        sync_thread.join()

        except KeyboardInterrupt:
        print("\n[Server Shutdown] Keyboard interrupt received. Closing server socket.")
        master_server.close()

        if __name__ == "__main__":
        initiateClockServer(8080)

//Commands
        sudo apt update
        sudo apt install python3
        python3 server.py
        python3 client.py

