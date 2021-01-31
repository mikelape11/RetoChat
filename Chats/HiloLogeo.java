package Chats;

import org.json.simple.JSONObject;

import java.net.Socket;

public class HiloLogeo extends Thread{

    private Socket socket;
    private String nick;
    private String ruta;

    //Getters
    public Socket getSocket() { return socket; }
    public String getNick() { return nick; }
    public String getRuta() { return ruta; }

    //Setters
    public void setSocket(Socket socket) { this.socket = socket; }
    public void setNick(String nick) { this.nick = nick; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    //constructora
    public HiloLogeo(Socket socket, String nick, String ruta) {
        this.socket = socket;
        this.nick = nick;
        this.ruta = ruta;
    }

    @Override
    public void run() {
        //guardamos los datos del usuario
        JSONObject user = Servidor.identificarUsuario(this.socket);

        this.nick = (String) user.get("user");
        this.ruta = (String) user.get("route");

        System.out.println("Usuario reconocido");

        //creamos el hilo de escucha
        HiloEscucha hilo = new HiloEscucha(this.socket, this.nick, this.ruta);
        hilo.start();
    }
}
