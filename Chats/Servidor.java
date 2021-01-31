package Chats;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Servidor {

    static ArrayList<HiloLogeo> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        try {
            //iniciamos servidor
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.print("Servidor escuchando\n");

            //aceptamos conexiones
            while (true) {
                //estara preguntando hasta que entre un usuario.
                Socket socketUsuario = serverSocket.accept();
                System.out.println("Usuario conectado");
                //ejecutar el hilo de logeo
                HiloLogeo hilo = new HiloLogeo(socketUsuario, "nick", "ruta");
                hilo.start();
                //guardamos el hilo en la lista
                usuarios.add(hilo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //identificamos el usuario y la ruta de un socket
    public static JSONObject identificarUsuario(Socket s) {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String linea;
            while ((linea = bf.readLine()) != null) {
                System.out.println(linea);
                JSONParser jsonParser = new JSONParser();
                JSONObject objetoUsuario = (JSONObject) jsonParser.parse(linea);
                return objetoUsuario;
            }
        } catch (SocketException se) {
            //se.printStackTrace();
            System.out.println("Error en servidor conexion cliente-servidor, en servidor");
        } catch (IOException ioe) {
            //ioe.printStackTrace();
            System.out.println("Error en servidor");
        } catch (ParseException e) {
            //e.printStackTrace();
            System.out.println("Error en servidor, parseo del JSON");
        }

        return null;
    }

    //enviemos un mensaje a los usuarios
    public static void enviarMsg(Socket cliente, JSONObject mensaje) throws IOException {
        for(HiloLogeo user: usuarios){
            Socket s = user.getSocket();
            System.out.println("Cliente conectado");
            BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            if(cliente != user.getSocket()){
                //si el usuario es de la misma ruta del mensaje, se le envia
                if(user.getRuta().equals(mensaje.get("route"))){
                    bfw.write(mensaje + "\n");
                    bfw.flush();
                }
            }
        }
        System.out.println(mensaje);
    }

    public static void logout(Socket socket) {
        for(HiloLogeo user: usuarios){
            Socket s = user.getSocket();
            if (socket == s){
                user.interrupt();
                usuarios.remove(user);
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Usuario deslogueado");
                }
                break;
            }
        }
    }
}
