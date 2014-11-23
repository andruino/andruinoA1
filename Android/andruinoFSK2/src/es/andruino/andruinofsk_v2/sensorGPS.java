package es.andruino.andruinofsk_v2;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class sensorGPS implements LocationListener {

	
	Double latitud = 0.0;
	Double longitud = 0.0;
	Double altitud = 0.0;
	String datosLatitud;
	String datosLongitud;
	String datosAltitud;
	String x ="NULL";
	String y ="NULL";

	private LocationManager miLocationManager;

	public sensorGPS(Context contexto) {
		// Inicio de GPS
		miLocationManager = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
		//miLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, (LocationListener) contexto);
		miLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this );
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitud = (Double) location.getLatitude();
		longitud = (Double) location.getLongitude();
		altitud = (Double) location.getAltitude();

		datosLatitud = String.valueOf(latitud);
		datosLongitud = String.valueOf(longitud);
		datosAltitud = String.valueOf(altitud);
		
		posicion posicionInstantanea = new posicion(latitud,longitud);
		x= String.valueOf(posicionInstantanea.getX());
		y= String.valueOf(posicionInstantanea.getY());
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	public String publicaGPS() {
		StringBuilder sb = new StringBuilder();
		String aux = "";

		Date horaActual = new Date();

		String fecha = (horaActual.getYear() + 1900) + ""
				+ (horaActual.getMonth() + 1) + "" + horaActual.getDate() + ""
				+ horaActual.getHours() + "" + horaActual.getMinutes() + ""
				+ horaActual.getSeconds();
		sb.append(fecha);
		sb.append("\n");

		aux = "GPS";
		sb.append(aux);
		sb.append("\n");
		sb.append("Latitud:");
		sb.append(" ");
		sb.append(datosLatitud);
		sb.append(" ; ");
		sb.append("Longitud:");
		sb.append(" ");
		sb.append(datosLongitud);
		sb.append("\n");

		return (sb.toString());
	}

	public String publicaGPSXY() {
		
		posicion miposicion;
		
		StringBuilder sb = new StringBuilder();
		String aux = "";

		Date horaActual = new Date();

		String fecha = (horaActual.getYear() + 1900) + ""
				+ (horaActual.getMonth() + 1) + "" + horaActual.getDate() + ""
				+ horaActual.getHours() + "" + horaActual.getMinutes() + ""
				+ horaActual.getSeconds();
		sb.append(fecha);
		sb.append("\n");

		miposicion = new posicion(latitud,longitud);
		aux = "GPS_XY";
		sb.append(aux);
		sb.append("\n");
		sb.append("X:");
		sb.append(" ");
		sb.append(String.valueOf(miposicion.getX()));
		sb.append(" ; ");
		sb.append("Y:");
		sb.append(" ");
		sb.append(String.valueOf(miposicion.getY()));
		sb.append("\n");

		return (sb.toString());
	}
	
	
	public String getX(){
		posicion miposicion = new posicion(latitud,longitud);
		return(String.valueOf(miposicion.getX()));
	}
	
	public String getY(){
		posicion miposicion = new posicion(latitud,longitud);
		return(String.valueOf(miposicion.getY()));
	}
}



