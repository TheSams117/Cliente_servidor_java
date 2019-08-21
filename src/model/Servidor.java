package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	
	/**
	 * Puerto por donde el servidor atendera a los clientes
	 */
	public static final int PORT = 8000;
	/**
	 * El servidor dispone de un serversocket, para permitir la conexion a los clientes
	 */
	private static  ServerSocket serverSocket;
	/**
	 * El servidor dispone de un socket para atender a cada cliente por individual
	 */
	private static Socket socket;
	

	
	
	public static void main(String[] args) {
		
		DataInputStream in;
		DataOutputStream out;
		
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("::Servidor escuchando a los posibles clientes::");
			socket = serverSocket.accept();
			System.out.println("El cliente se ha conectado!");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			while(true) {
				
				
				String mensajeObtenidoCliente = in.readUTF();
				System.out.println("El mensaje enviado por el cliente fue : " + mensajeObtenidoCliente);
				String respuestaServer = metodoServicioServer(mensajeObtenidoCliente);
				out.writeUTF(respuestaServer);
			
			}
			
			
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}




	private static String metodoServicioServer(String mensajeObtenidoCliente) {
		
		String[] cadena = mensajeObtenidoCliente.split(";");
		
		return Integer.parseInt(cadena[0]) + Integer.parseInt(cadena[1]) + "";
	}
	
	


}
