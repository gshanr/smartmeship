
import java.io.*;
import java.net.*;
public class javatest {
   public static void main(String[] args) throws Exception {
      
	  int c,count;
 
	  c=count=0;
      ServerSocket serverSocket;
      serverSocket = new ServerSocket(4321, 0, InetAddress.getByName("localhost"));

	while(true)
	{
		System.out.println("waiting for connection");
        Socket server = serverSocket.accept();
        System.out.println("accepted");
      try{
    	  BufferedReader fromServer = 
    				new BufferedReader(
    						new InputStreamReader(server.getInputStream()));
    			while(c!=-1)
    			{
    			count++;	
    			c = fromServer.read();
    			System.out.print(c + "-");
    			}
    			System.out.println("received"+count);
      }catch(Exception e){
         // if any I/O error occurs
         e.printStackTrace();
      }
    }
   }
}
