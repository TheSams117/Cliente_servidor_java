package modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import modelo.Cliente.HiloCliente;

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
	 * Hashmap que guarda el hilos que escucharan a los clientes
	 */
	private static HashMap<Integer,HiloCliente> hilos;
	/**
	 * Contador de clientes que se conectan
	 */
	private static int CantidadClientes;
	
	
	public static void main(String[] args) {
		//Se incia el hasmap que contendra a los hilos que atienden a los clientes
		hilos = new HashMap<>();
		CantidadClientes = 0;
		
		//Se crea e inicia el hilo que espera que se conecte un nuevo cliente
		HiloServerSocket hiloServer = new HiloServerSocket();
		hiloServer.run();
	
	}
	/**
	 * Hilo que espera a que se conecte un cliente
	 * Cuando se conecta un cliente este crea un hilo que recibira y enviara mensajes especialmente a ese cliente
	 */
	public static class HiloServerSocket extends Thread {
		public void run(){
			
			
			try {
				
				serverSocket = new ServerSocket(PORT);
				System.out.println("::Servidor escuchando a los posibles clientes::");
				
				
				
				while(true) {
					
					
					Socket socket = serverSocket.accept();
					//Creacion del hilo que escuchara al cliente que acaba de llegar 
					HiloCliente hilo = new HiloCliente();
					hilo.comunicacion(socket,new DataInputStream(socket.getInputStream()), new DataOutputStream(socket.getOutputStream()));
					// Se guarda el hilo para que persista mientras esta encendido el servidor
					hilos.put(CantidadClientes, hilo);
					CantidadClientes+=1;
					// El hilo comienza a esperar recibir o enviar mensajes que llegen del cliente
					hilo.start();
				}	
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	
	
	/**
	 * Hilo que recibira y enviara mensajes de un cliente especifico 
	 * @author sergi
	 *
	 */
	public static class HiloCliente extends Thread {
		DataInputStream in;
		DataOutputStream out;
		Socket socket;
		// id que permite identificar cada cliente y lograr la comunicación 
		int clienteId = 0;
		public void run(){
			try {				
				while(true) {
					
					
					String mensajeObtenidoCliente = in.readUTF();
					
				
					switch(mensajeObtenidoCliente.split(";")[0]){
					// si el cliente recibe la plabra "CONECTAR;id" se guardara ese id para poder comunicarse con ese cliente.
					// además envia la llave en hexadecimal a los dos cliente que empezaran a enviarse mensajes incriptados 
					 case "CONECTAR":
						clienteId = Integer.parseInt(mensajeObtenidoCliente.split(";")[1]);
						int llaveEncriptacion = (int) (Math.random()*26+1);
						String llaveEnHexadecimal = Integer.toHexString(llaveEncriptacion);
						out.writeUTF(llaveEnHexadecimal);
						enviarMensaje("LLAVE;"+llaveEnHexadecimal);
						
						 break;
					default:
						//en cualquier otro caso la palabra que llegue se toma como un mensaje a enviar al cliente con el id guardado al realizar la conexion 
						enviarMensaje(mensajeObtenidoCliente);
					
					}
					

				
				}	
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		public void comunicacion(Socket asocket,DataInputStream ain, DataOutputStream aout) {
			in = ain;
			out = aout;
			socket = asocket;
		}
		/**
		 * Se envia el mensaje que llega por parametro al cliente con el id designado previamente en la conexión
		 */
		public void enviarMensaje(String mensaje) {
			try {
				hilos.get(clienteId).out.writeUTF(mensaje);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
