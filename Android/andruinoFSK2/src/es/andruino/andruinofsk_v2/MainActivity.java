package es.andruino.andruinofsk_v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;


import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// @SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	// ////////////////////////////////////////////////////
	// Variables de la comunicaci�n Android -> Arduino
	// ////////////////////////////////////////////////////

	private static final int comando_adelante = 125; // env�o A, para adelante
	private static final int comando_atras = 189; // env�o !, para detr�s
	private static final int comando_para = 34; // env�o , para detr�s
	private static final int comando_izq = 66; // env�o , para detr�s
	private static final int comando_der = 165; // env�o , para detr�s
	private static final int MSG_ID2 = 0x2; //Códido de intercambio de ordénes entre hilo y Main
	private trxFSK mitrxFSK;

	// ////////////////////////////////////////////////////
	// Variables de la comunicaci�n PC - Android
	// ////////////////////////////////////////////////////

	// Objetos relacionados
	private hiloOrdenes objhiloOrdenes;
	private Thread miHiloOrdenes;
	public String mensajeDeHiloOrdenes = "";

	// ////////////////////////////////////////////////////
	// Variables relacionadas con la c�mara
	// ////////////////////////////////////////////////////

	// Objetos relacionados con la transmisi�n de Video
	private hiloVideo objhiloVideo;
	private Thread miHiloVideo;

	// HEADERS del protocolo HTTP
	// Sin cache y tiempos a cero
	// http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html permite el refresco de la imagen, s�lo valido en Firefox
	private static final String BOUNDARY = "SeparadorAndruino2013";
	private static final String HTTP_HEADER = "HTTP/1.0 200 OK\r\n"
			+ "Server: Andruino\r\n"
			+ "Connection: close\r\n"
			+ "Max-Age: 0\r\n"
			+ "Expires: 0\r\n"
			+ "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n"
			+ "Pragma: no-cache\r\n" 
			+ "Content-Type: multipart/x-mixed-replace; " 
			+ "boundary=" + BOUNDARY + "\r\n" + "\r\n" + "--" + BOUNDARY + "\r\n";

	// Variables SurfaceView para captar las im�genes de la c�mara
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean cameraConfigured = false;
	boolean prepared = false;
	
	// ////////////////////////////////////////////////////
	// Variables relacionadas con sensores
	// ////////////////////////////////////////////////////

	private sensores misSensores;
	private hiloSensores objhiloSensores;
	private Thread miHiloSensores;
	private String datosSensores;
	
	private sensorGPS misensorGPS;

	// ////////////////////////////////////////////////////
	// Variables relacionadas con el Beacon Wifi
	// ////////////////////////////////////////////////////
	private sensorWifi misensorWifi;
	  
	// ////////////////////////////////////////////////////
	// Variables est�ticas, accesibles directamente por cualquier hilo
	// ////////////////////////////////////////////////////
	static TextView tvSensores;
	static boolean andruinoON = false;

	// /////////////////////////////////////////////////////////////
	// C�digo onCreate, se lanza al iniciar la apliaci�n, ver ciclo de vida de
	// aplicaci�n
	// /////////////////////////////////////////////////////////////
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Pone variable de estado a ON
		andruinoON = true;

		// Inicia la clase sensores
		misSensores = new sensores(this);
		datosSensores = " ";
		
		// Inicia la clase de GPS
		misensorGPS = new sensorGPS(this);

		// Inicia la clase de trxFSK (para transmisi�n de audio)
		mitrxFSK = new trxFSK();
		
		// Inicia la clase del Beacon Wifi
		misensorWifi = new sensorWifi (this); 
	 

		// Botones para el control desde el Android
		Button bt1 = (Button) findViewById(R.id.bt1);
		Button bt2 = (Button) findViewById(R.id.bt2);

		// Listener de los botones
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// comando = 125;
				mitrxFSK.envdato(comando_adelante);
			}
		});

		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// comando = 189;
				mitrxFSK.envdato(comando_atras);
			}
		});

		// Prepara el texto para mostar la IP en un TextView
		TextView tvIP = (TextView) findViewById(R.id.TextViewIP);
		tvIP.setText(getLocalIpAddress());

		// Prepara el texto del intercambio de mensajes con la m�quina remota
		TextView tv = (TextView) findViewById(R.id.TextViewMensaje);
		tv.setText("Esperando una orden...");

		// Visualizaci�n de datos de sensores
		tvSensores = (TextView) findViewById(R.id.textViewSensores);

		// Inicio de la C�mara
		preview = (SurfaceView) findViewById(R.id.preview);
		preview.setZOrderOnTop(true); // 
		previewHolder = preview.getHolder(); 
		previewHolder.setFormat(PixelFormat.TRANSPARENT); //
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Lanza el hilo de �rdenes
		objhiloOrdenes = new hiloOrdenes(mHandlerOrdenes);
		miHiloOrdenes = new Thread(objhiloOrdenes);
		miHiloOrdenes.start();

		// Lanza el hilo de Video
		objhiloVideo = new hiloVideo();
		miHiloVideo = new Thread(objhiloVideo);
		miHiloVideo.start();

		// Lanza el hilo de Sensores (logs de los sensores)
		objhiloSensores = new hiloSensores();
		miHiloSensores = new Thread(objhiloSensores);
		miHiloSensores.start();

		/* TIMER !!!!!!
		 * TIMER NO FUNCIONA adecuadamente, se una CallBack de C�mara PROVISIONALMENTE  
		 * 
		 * el timer para cada 0,5sg (X) escriba el valor de los sensores Timer
		 * updateTimer = new Timer("actualizacionSensores");
		 * updateTimer.scheduleAtFixedRate(new TimerTask() { public void run() {
		 * publicaSensores(); } }, 0, 500);
		 */
		
		// Inicia la camara
		try {
			camera = Camera.open();
			camera.startPreview();
		} catch(RuntimeException exception){
			Toast.makeText(this, "Camera error (No camera, used by other app,...)", Toast.LENGTH_LONG).show();
		}
		

	}

	// /////////////////////////////////////////////////////////////
	// Proceso de la c�mara
	// /////////////////////////////////////////////////////////////

	private Camera.PreviewCallback mPreviewListener = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			
			if (objhiloVideo.conectado) {
				

				// Define el ancho y alto de la imagen a transmitir
				int ancho = camera.getParameters().getPreviewSize().width;
				int alto = camera.getParameters().getPreviewSize().height;
				Rect rect = new Rect(0, 0, ancho, alto);
				
				try {

					// Crea el Stream y Buffer para mandar datos
					OutputStream os = objhiloVideo.soquete.getOutputStream();
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					buffer.reset();

					// Captura la imagen del SurfaceView y la convierte en JPG
					YuvImage frame = new YuvImage(data, ImageFormat.NV21,ancho, alto, null);
					frame.compressToJpeg(rect, 50, buffer);

					// Simula un servidor HTTP: env�a las cabeceras http y las
					// im�genes.
					// Ver RFC 1341
					os.write(HTTP_HEADER.getBytes());
					os.write(("--" + BOUNDARY + "\r\n Content-type: image/jpg\r\n Content-Length: " + buffer.size() + "\r\n\r\n").getBytes());
					buffer.writeTo(os);
					os.write("\r\n\r\n".getBytes());
					os.flush(); //Publica en el socket

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// Emito sensores cada vez que recibo una imagen (PROVISIONAL, debe ajustarse con TIMER)
			// Aprovecha esto para mandar datos de sensores (ya que da error
			// tener un Stream invocado por el Timer)
			if (objhiloSensores.conectado) {
				datosSensores = misSensores.publicaSensores(); //Publica todos los sensores
				//datosSensores= misensorGPS.publicaGPSXY(); // Publica GPS
				datosSensores=datosSensores + ";" + publicaXYA(); // Publica datos de beacos de wifi
				tvSensores.setText(datosSensores);
				OutputStream osSensores;
				try {
					osSensores = objhiloSensores.soquete.getOutputStream();
					osSensores.write(datosSensores.getBytes());
					osSensores.flush(); //Publica en el socket
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// fin de publicacion de sensores
		}
	};

	
	// Parámetros de la cámara. Asocia el listener de cámara.
	private void camaraParameters(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
							
				Camera.Parameters p = camera.getParameters();
				p.set("jpeg-quality", 50);
				camera.setPreviewDisplay(previewHolder);
				
			} catch (Throwable t) {

			}

			if (!cameraConfigured) {
				//Camera.Parameters parameters = camera.getParameters();
				camera.setPreviewCallback(mPreviewListener);
				cameraConfigured = true;
			}
		}
	}
	

	// Eventos del SurfaceHolder
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			camaraParameters(width, height);
			camera.startPreview();
			
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};

	// ////////////////////////////////////////////////////
	// M�todo que consigue la IP del equipo, obtenida de
	// http://stackoverflow.com/questions/5307992/get-the-ipaddress-using-java
	// (Ir� a librer�a)
	// ////////////////////////////////////////////////////
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			// Log.e(tag, ex.toString());
		}
		return "";
	}

	// Fin de M�todo que consigue la IP del equipo

	
	// onStop
	@Override
	protected void onStop() {
		super.onStop();
		andruinoON = false;
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
			
		}
		
		// Cierra la aplicaci�n, para evitar tener que manejar los hijos (DEBE SER MEJORADO)
		System.exit(0);

		// Desregistra sensores y GPS
		// mySensorManager.unregisterListener(OrientacionSensorEventListener);
	}

	// onResume
	@Override
	public void onResume() {
		super.onResume();
		// Pone variable de estado a ON
		andruinoON = true;
		
		// Deber�a definir nuevamente el Callback de la c�mara y reiniciar los hilos (DEBE SER MEJORADO)
		
	}

	// onPause
	@Override
	public void onPause() {
		super.onPause();
		andruinoON = false;
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
		// Cierra la aplicaci�n, para evitar tener que manejar los hijos (DEBE SER MEJORADO)
		System.exit(0);

		// Desregistra sensores y GPS
		// mySensorManager.unregisterListener(OrientacionSensorEventListener);
	}
	


	// ////////////////////////////////////////////////////
	// Manejador para el intercambio de mensajes desde el hilo Ordenes a MainActivity
	// ////////////////////////////////////////////////////

	//@SuppressLint("HandlerLeak")
	public Handler mHandlerOrdenes = new Handler() {

		// byte[] bufferOrdenes = new byte[4];

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_ID2:
				TextView tv = (TextView) findViewById(R.id.TextViewMensaje);
				tv.setText("Comando: " + String.valueOf(msg.arg1));
				// Deber�a usar Case en lugar de tanto if...(CAMBIAR)
				if (msg.arg1 == 1) {
					// comando = 125;
					mitrxFSK.envdato(comando_adelante);
				}
				if (msg.arg1 == 2) {
					// comando = 189;
					mitrxFSK.envdato(comando_atras);
				}
				if (msg.arg1 == 3) {
					// comando = 189;
					mitrxFSK.envdato(comando_para);
				}
				if (msg.arg1 == 4) {
					// comando = 189;
					mitrxFSK.envdato(comando_izq);
				}
				if (msg.arg1 == 5) {
					// comando = 189;
					mitrxFSK.envdato(comando_der);
				}
				if (msg.arg1 == 6){
					/*
					//Twitea su posición
					String tweetUrl = "https://twitter.com/intent/tweet?text=Andruino en Lat:"+misensorGPS.datosLatitud +" Lon:"+ misensorGPS.datosLatitud +"&url="
	                        + "https://www.google.com &hashtags=android,twitter";
							Uri uri = Uri.parse(tweetUrl);
							startActivity(new Intent(Intent.ACTION_VIEW, uri));
							*/
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

public String publicaXYA() {
			
		StringBuilder sb = new StringBuilder();
	

		Date horaActual = new Date();

		String fecha = (horaActual.getYear() + 1900) + ""
				+ (horaActual.getMonth()<9?'0':"")+ (horaActual.getMonth() + 1) + "" + (horaActual.getDate()<10?'0':"")+ horaActual.getDate() + ""
				+ (horaActual.getHours()<10?'0':"") + horaActual.getHours() + "" + (horaActual.getMinutes()<10?'0':"")+ horaActual.getMinutes() + ""
				+ (horaActual.getSeconds()<10?'0':"")+ horaActual.getSeconds();
		sb.append(fecha);
		sb.append(";");
		sb.append (misensorGPS.datosLatitud);
		sb.append(";");
		sb.append (misensorGPS.datosLongitud);
		sb.append(";");
		sb.append(misensorGPS.getX());
		sb.append(";");
		sb.append(misensorGPS.getY());
		sb.append(";");
		sb.append(misSensores.getAzimut());
		sb.append(";");
		sb.append(misensorWifi.getBeacons());
		sb.append("\r\n");
		
		return (sb.toString());
	}
	
}