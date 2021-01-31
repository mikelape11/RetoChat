package Chats;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class HiloEscucha extends Thread{

    private Socket socket;
    private String nick;
    private String ruta;
    private BufferedReader bfr;

    //Getters
    public Socket getSocket() { return socket; }
    public String getNick() { return nick; }
    public String getRuta() { return ruta; }

    //Setters
    public void setSocket(Socket socket) { this.socket = socket; }
    public void setNick(String nick) { this.nick = nick; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    //constructora
    public HiloEscucha(Socket socket, String nick, String ruta) {
        this.socket = socket;
        this.nick = nick;
        this.ruta = ruta;

        try {
            this.bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error en HiloEscucha, BufferedReader");
        }

    }

    @Override
    public void run() {
        //Toca leer el mensaje que llega
        String linea;
        try {
            //leemos lo que nos llego
            while ( (linea = bfr.readLine() ) != null) {
                JSONParser parseador = new JSONParser();
                JSONObject objetoJSON = (JSONObject) parseador.parse(linea);

                if (objetoJSON.get("action").equals("logout")) {
                    System.out.println("Accion de deslogeo");
                    Servidor.logout(this.socket);
                } else {
                    Servidor.enviarMsg(this.socket, objetoJSON);
                }
            }

        } catch (SocketException se) {
            //se.printStackTrace();
            HiloLogeo usuario = null;
            for(HiloLogeo user: Servidor.usuarios){
                if(user.getSocket() == this.socket){
                    usuario = user;
                }
            }
            Servidor.usuarios.remove(usuario);

            System.out.println("Error en HiloEscucha, conexion cliente-servidor detenida");
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error en HiloEscucha");
        } catch (ParseException pe) {
            pe.printStackTrace();
            System.out.println("Error en HiloEscucha, Parseador de JSON");
        }


    }
}
