package es.andruino.andruinofsk_v2;

//import android.util.FloatMath;

// Función imcompleta. Imcompleted function 
public class posicion {
	//37.399857,-6.035138
	private static double latitud_original=37.399857; //DEBERIA SER Latitud de Triana, centro del Universo
	private static double longitud_original=-6.035138; //DEBERIA SER Longitud de Triana, centro del Universo
	//private static final int    RADIO_TIERRA_METROS    = 6371000;
	
	double x;
	double y;
	double z;
	int angulo;
	

	public posicion(double lat, double lon)
	{
		// APROXIMACI�N DE TRANSFORMACION DE COORDENADAS CONSIDERANDO TIERRA PLANA
		// Simplificaci�n s�lo v�lida en triana (37.38 de latitud), y asumiendo el robot se mover� 50 o 100 metros como m�ximo
		// 1� latitud son 110567,2 metros
		// 1� longitud son 88568,9 metros a la latitud de triana
		// Mirar http://mathforum.org/library/drmath/view/51833.html

		x = (lat - latitud_original)* 110567.2; // Formula general  y = R*(latitud2-latitud1)*pi/180, R radio de la tierra en metros
		y= (lon - longitud_original)* 88568.9; // Formula general x = R*(longitud2-longitud1)*(pi/180)*cos(latitud1)
	
		//////////////////////////
		// Formula Haversine
		//Obetnido de http://stackoverflow.com/questions/5983099/converting-longitude-latitude-to-x-y-coordinate
		//////////////////////////
		/*
		double Re = 6378137;
		double Rp = 6356752.31424518;

		double latrad = lat/180.0*Math.PI;
		double lonrad = lon/180.0*Math.PI;

		double coslat = Math.cos(latrad);
		double sinlat = Math.sin(latrad);
		double coslon = Math.cos(lonrad);
		double sinlon = Math.sin(lonrad);

		double term1 = (Re*Re*coslat)/
		  Math.sqrt(Re*Re*coslat*coslat + Rp*Rp*sinlat*sinlat);

		double term2 = alt*coslat + term1;

		x=coslon*term2;
		y=sinlon*term2;
		z = alt*sinlat + (Rp*Rp*sinlat)/
				Math.sqrt(Re*Re*coslat*coslat + Rp*Rp*sinlat*sinlat);
				*/
	}

	double getX() {
		return x;
	}
	
	double getY(){
		return y;
	}
	
	double getZ(){
		return z;
	}
	

	
}