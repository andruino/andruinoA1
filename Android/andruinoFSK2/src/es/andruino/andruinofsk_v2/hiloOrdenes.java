package es.andruino.andruinofsk_v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class hiloOrdenes implements Runnable {
	private static final int SERVERPORT = 6000; //Puerto 6000 para órdenes
	protected static final int MSG_ID2 = 0x2; 
	private ServerSocket ss;
	public Socket soquete;
	public boolean conectado = false;
	Handler mHandler;

	public hiloOrdenes(Handler manejador) {
		mHandler = manejador;
	}

	@Override
	public void run() {
		while (MainActivity.andruinoON) {
			Socket s = null;
			boolean banner = true;
			int comando = 0;
			try {
				ss = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {
				Message m = new Message();
				m.what = MSG_ID2;
				m.arg1 = 0; // ninguna orden
				try {

					if (s == null) {
						s = ss.accept(); // Pone el servidor a escuchar
						banner = true;
					}
					// Buffer de recepcion de datos
					BufferedReader input = new BufferedReader(
							new InputStreamReader(s.getInputStream()));
					// Buffer de emisi�n de datos
					BufferedWriter output = new BufferedWriter(
							new OutputStreamWriter(s.getOutputStream()));

					// Si se conect� por primera vez, da mensaje de Bienvenida
					if (banner == true) {
						banner = false;
						output.write("AndruinoFSK 2013. Version 2ASIR");
						output.write("....Greetings Professor Lopez !!\r\n\r\n");
						output.write("Commands: forward, back, stop, left, right \r\n\r\n");
						//output.write("....Saludos 2ASIR!!\r\n\r\n");
						//output.write("Comandos: adelante , atras, para , izq , der \r\n\r\n");
						output.flush();
					}

					// Lee comando recibido
					String recibido = null;
					recibido = input.readLine();

					// Ordenes recibidas desde el PC
					// Orden "para"
					//if ("atras".equals(recibido)) {
					if ("back".equals(recibido)) {
						comando = 2;
						m.arg1 = comando;
						//output.write("Comando Atras Recibido\r\n");
						output.write("Back command received\r\n");
						output.flush();
					}

					// Orden "marcha"
					//if ("adelante".equals(recibido)) {
					if ("forward".equals(recibido)) {
						comando = 1;
						m.arg1 = comando;
						//output.write("Comando Adelante Recibido\r\n");
						output.write("Forward command received\r\n");
						output.flush();
					}

					// Orden "para"
					//if ("para".equals(recibido)) {
					if ("stop".equals(recibido)) {
						comando = 3;
						m.arg1 = comando;
						//output.write("Comando Para Recibido\r\n");
						output.write("Stop command received\r\n");
						output.flush();
					}
					
					// Orden "izq"
					//if ("izq".equals(recibido)) {
					if ("left".equals(recibido)) {
						comando = 4;
						m.arg1 = comando;
						//output.write("Comando Izq Recibido\r\n");
						output.write("Left command received\r\n");
						output.flush();
					}
					
					// Orden "der"
					//if ("der".equals(recibido)) {
					if ("right".equals(recibido)) {
						comando = 5;
						m.arg1 = comando;
						//output.write("Comando Der Recibido\r\n");
						output.write("Right command received\r\n");
						output.flush();
					}
					/*
					// Orden "twit"
					//if ("der".equals(recibido)) {
					if ("twit".equals(recibido)) {
						comando = 6;
						m.arg1 = comando;
						//output.write("Comando Der Recibido\r\n");
						output.write("Twit command received\r\n");
						output.flush();
					}
					*/
					// Transmite mensaje a Hilo principal (MainActivity)
					mHandler.sendMessage(m);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}