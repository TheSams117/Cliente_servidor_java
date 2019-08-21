package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Cliente {

	/**
	 * Direccion local de la maquina
	 */
	public static final String LOCAL_HOST = "localhost";
	/**
	 * Puerto por donde se establecera la conexion
	 */
	public static final int PORT = 8000;
	/**
	 * Socket que permitira la conexion con el servidor
	 */
	private static Socket socket;
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		
		DataInputStream in;
		DataOutputStream out;

		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader( System.in));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			
			System.out.println("::Cliente disponible para ser atendido:: \nIngrese los dos numeros"
					+ " en formato int;int");
			socket = new Socket(LOCAL_HOST, PORT);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			while(true) {
				
				
				String mensaje = br.readLine();
				
				out.writeUTF(mensaje);
				String mensajeDelServidor = in.readUTF();
				bw.write("La suma es : " + mensajeDelServidor+"\n");
				bw.flush();
			}
			

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	

}
