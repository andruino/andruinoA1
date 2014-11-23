package es.andruino.andruinofsk_v2;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class sensores implements SensorEventListener {

	private List<Sensor> listaSensores;
	private String nombreSensores[] = new String [50];
	private float datosSensores[][] = new float [50][10];
	SensorManager sm;


	public sensores(Context contexto) {
		 sm = (SensorManager) contexto.getSystemService(Context.SENSOR_SERVICE);
		listaSensores = sm.getSensorList(Sensor.TYPE_ALL);
		//listaSensores = sm.getSensorList(Sensor.TYPE_ORIENTATION);
		int n = 0;

		for (Sensor sensor : listaSensores) {
			nombreSensores[n] = sensor.getName();
			sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			n++;
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	

			int n = 0;
			for (Sensor sensor : listaSensores) {
				if (event.sensor == sensor) {
					for (int i = 0; i < event.values.length; i++) {
						datosSensores[n][i]=(event.values[i]);
					}
				}
				n++;
			}
			
	}
	
	public float getDatosSensores (int x, int y){
		if (datosSensores!=null)
			return datosSensores[x][y];
		else
			return -1;
	}
	
	public String getNombreSensor(int x){
		return nombreSensores[x];
	}
	
	public String publicaSensores() {
		StringBuilder sb = new StringBuilder();
	 	String aux="";
	 	
	 	Date horaActual=new Date();

		String fecha =(horaActual.getYear()+1900)+""+(horaActual.getMonth()+1)+""+horaActual.getDate()+""+horaActual.getHours()+""+horaActual.getMinutes()+""+horaActual.getSeconds();
	 	sb.append(fecha);
	 	sb.append("\n");
	 	
		int i =0;
		for (i=0; i< listaSensores.size(); i++){
			 aux=getNombreSensor(i);
			 if (aux != null && !aux.equals("")) {
			 sb.append(Integer.toString(i));
			 sb.append(getNombreSensor(i));
             sb.append("\n");
             sb.append(getDatosSensores(i, 0));
             sb.append(" ");
             sb.append(getDatosSensores(i, 1));
             sb.append(" ");
             sb.append(getDatosSensores(i, 2));
             sb.append(" ");
             sb.append("\n");
			 } else {
             continue; }
			 i++;
		}
		return (sb.toString());
	}
	
	public void desregistraSensores(){
		sm.unregisterListener(this);

	}
	
	public String getAzimut(){
		//Solo v�lido si esta activado exclusivamente el sensor de orientaci�n
		return(String.valueOf(getDatosSensores(0, 0)));
	}
}
