package Chats;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Usuario2 {

    public static void main(String[] args) {

        try {
            //conectamos con el servidor
            Socket socket = new Socket("localhost", 1234);

            //el writer para loguearse
            BufferedWriter bfw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //cogemos el nombre de la bd
            String nick = "Migimu";

            //cogemos la ruta al que pertenece de la bd
            String ruta = "Ruta X";

            JSONObject loginUsuario = new JSONObject();

            //los datos que vamos a enviar
            loginUsuario.put("action","login");
            loginUsuario.put("user", nick);
            loginUsuario.put("route", ruta);

            bfw.write(loginUsuario + "\n");
            bfw.flush();
            System.out.println("Logueado");

            //activar el hilo de escucha del servidor
            HiloEscucha hilo = new HiloEscucha(socket, nick, ruta);
            hilo.start();

            Scanner sc = new Scanner(System.in);
            //capacidad para escribir en el servidor
            while (true){
                System.out.println("Mensaje: ");
                String mensaje= sc.nextLine();

                if(!mensaje.equalsIgnoreCase("logout")){
                    JSONObject jsonMensaje = new JSONObject();
                    jsonMensaje.put("action", "msg");
                    jsonMensaje.put("value", mensaje);
                    jsonMensaje.put("route", ruta);
                    jsonMensaje.put("from", nick);

                    bfw.write(jsonMensaje + "\n");
                    bfw.flush();
                } else {
                    System.out.println("Desconectando");
                    break;
                }
            }

            sc.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error en Usuario");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en Usuario, fallo general");
        }

    }

}
