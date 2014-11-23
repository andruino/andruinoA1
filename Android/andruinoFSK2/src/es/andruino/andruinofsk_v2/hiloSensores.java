package es.andruino.andruinofsk_v2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class hiloSensores implements Runnable {
	private static final int SERVERPORT = 7000; //Puerto 7000 Sensores
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
