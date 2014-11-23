package es.andruino.andruinofsk_v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class hiloVideo implements Runnable {
	private static final int SERVERPORT = 8000; //Puerto 8000 imágenes
	private ServerSocket ss;
	public Socket soquete;
	public boolean conectado = false;

	public void run() {
		while (MainActivity.andruinoON) {

			try {
				ss = new ServerSocket(SERVERPORT);
				soquete = ss.accept();

			} catch (IOException e) {
				// e.printStackTrace();
			}
			if (soquete != null) {
				conectado = soquete.isConnected();
			} else
				conectado = false;

			while (conectado) {

				conectado = soquete.isConnected();
			}

		}
	}
}
