package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

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
	 * lista de clientes conectados
	 */
	private static ArrayList<HiloCliente> clientes;
	
	/**
	 * El main actuara como si fuera un cliente que puede decidir que clientes agregara al servidor, con cual quiere hablar y además poder visualisar la cantidad de clientes conectados al servidor
	 */
	public static void main(String[] args) {
		
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader( System.in));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			//llave usada para la incriptación cesar
			int llaveDeIncriptacion = 0;

			socket = new Socket(LOCAL_HOST, PORT);
			//array que guarda los hilos de clientes agregados al servidor
			clientes = new ArrayList<HiloCliente>();
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			
			while(true) {
				 bw.write("Escriba:\nAGREGAR: Agrega un cliente al servidor.\nCONECTAR: Lo conecta con un cliente.\nENLISTAR: Muestra los id de los clientes disponibles\nCUALQUIER OTRA COSA: Se toma como mensaje para el cliente conectado\n");
				 bw.flush();
				String mensaje = br.readLine();
				switch(mensaje){
				case "AGREGAR":
					HiloCliente hilo = new HiloCliente();
					hilo.start();
					hilo.id = clientes.size()+1;
					clientes.add(hilo);
					bw.write("Cliente agregado\n");
					bw.flush();
					break;
				 case "ENLISTAR":
					 for (int i = 0; i < clientes.size(); i++) {
						bw.write("Cliente id: "+clientes.get(i).id+"\n");
					}
					
					 break;
				 case "CONECTAR":
					 bw.write("Escriba el id del usuario con el que desea hablar\n");
					 bw.flush();
					 out.writeUTF("CONECTAR;"+br.readLine());
					 String llaveHexa = in.readUTF();
					 llaveDeIncriptacion = Integer.parseInt(llaveHexa, 16);
					 break;
				default:
		
					out.writeUTF(cifradoCesar(mensaje,llaveDeIncriptacion));
					break;
				
				}
				bw.write("\n");
				
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	//Hilo que se encarga de escuchar los mensajes que le llegan a un cliente determinado 
	public static class HiloCliente extends Thread {
		int id;
		int llaveDesincriptación;
		public void run(){
			
			try {
				Socket socketCliente = new Socket(LOCAL_HOST, PORT);
				DataInputStream in = new DataInputStream(socketCliente.getInputStream());
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
				String mensajeDelServidor = "";
				while(true) {
					mensajeDelServidor=in.readUTF();
					//si el mensaje del servidor tiene la forma "LLAVE;hexadecimal" es por que le esta llegan la clave para poder desencriptar los mensajes que reciba
					if(mensajeDelServidor.split(";")[0].equals("LLAVE")) {
						llaveDesincriptación = Integer.parseInt(mensajeDelServidor.split(";")[1], 16);
					}else {
						bw.write("Mensaje que recibio el cliente "+id+" es:  "+descifradoCesar(mensajeDelServidor, llaveDesincriptación)+"\n");
						mensajeDelServidor = "";
						bw.flush();
						
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	//método para cifrar el texto
    public static String cifradoCesar(String texto, int codigo) {
        StringBuilder cifrado = new StringBuilder();
        codigo = codigo % 26;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) >= 'a' && texto.charAt(i) <= 'z') {
                if ((texto.charAt(i) + codigo) > 'z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            } else if (texto.charAt(i) >= 'A' && texto.charAt(i) <= 'Z') {
                if ((texto.charAt(i) + codigo) > 'Z') {
                    cifrado.append((char) (texto.charAt(i) + codigo - 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) + codigo));
                }
            }
        }
        return cifrado.toString();
    }

    //método para descifrar el texto
    public static String descifradoCesar(String texto, int codigo) {
        StringBuilder cifrado = new StringBuilder();
        codigo = codigo % 26;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) >= 'a' && texto.charAt(i) <= 'z') {
                if ((texto.charAt(i) - codigo) < 'a') {
                    cifrado.append((char) (texto.charAt(i) - codigo + 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) - codigo));
                }
            } else if (texto.charAt(i) >= 'A' && texto.charAt(i) <= 'Z') {
                if ((texto.charAt(i) - codigo) < 'A') {
                    cifrado.append((char) (texto.charAt(i) - codigo + 26));
                } else {
                    cifrado.append((char) (texto.charAt(i) - codigo));
                }
            }
        }
        return cifrado.toString();
    }
	
	
	

}
