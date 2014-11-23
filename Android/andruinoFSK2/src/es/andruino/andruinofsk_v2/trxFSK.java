package es.andruino.andruinofsk_v2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class trxFSK {
	
		/////////////////////////////////////////////////////////////////////////
		// Variables de la comunicación del Android al Arduino por FSK
		// ///////////////////////////////////////////////////////////////////////

		private final int sampleRate = 44100; // frecuencia de muestreo = 44000Hz,
												// para que supere el doble de la
												// frecuencia más alta (Nyquist)
		private final double freq_Baja = 3150; // frecuencia de "0" lógico, señal
												// senoidal con una frecuencia de
												// 3150Hz
		private final double freq_Alta = 6300; // freceuncia de "1" lógico, señal
												// senoidal con una frecuencia de
												// 6300Hz
		// private static final int amplitud = 30000; //volumen de señal de audio
		private final int tasaBinaria = 630; // tasa binaria = 630 bps
		private final int numeroBits = 11; // numero de bits por cada transmisión
											// asíncrona: bit start(low)+8bits datos
											// + 2 bit stop(high)
		private final int numeroMuestras = (numeroBits * sampleRate) / tasaBinaria; // número
																					// de
																					// muestras
																					// por
																					// cada
																					// transmisión
																					// (11
																					// bits)
		private final int numeroMuestrasPorBit = sampleRate / tasaBinaria; // número
																			// de
																			// muestras
																			// por
																			// cada
																			// bit
		private final double senoidal[] = new double[numeroMuestras]; // array para
																		// muestras

		private static final int BIT_UNO = 1; // "1" lógico
		private static final int BIT_CERO = 0; // "0" lógico

		private byte salidaAudio[] = new byte[2 * numeroMuestras]; // array que
																	// contiene la
																	// senal
																	// modulada
																	// generada

		// private int comando; // comando enviado por FSK (Si recibe un caracter |
		// entonces va hacia atrás / Si envia A entonces va hacia adelante)

		//private static final int comando_adelante = 125; // envío A, para adelante
		//private static final int comando_atras = 189; // envío !, para detrás
		

		// ////////////////////////////////////////////////////
		// Funciones de audio, para librería también
		// ////////////////////////////////////////////////////
		
		trxFSK (){}

		// Envia dato por FSK
		void envdato(int comando) {
			genTone(comando);
			final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRate,
					// AudioFormat.CHANNEL_CONFIGURATION_MONO, // Deprecated
					AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
					salidaAudio.length, AudioTrack.MODE_STATIC);
			audioTrack.write(salidaAudio, 0, salidaAudio.length);
			audioTrack.play();
		}

		// Genera el array con la señal de audio, para despues hacerla sonar
		void genTone(int dato) {

			int bitSecuencia; // determina el bit que toca en la secuencia de los 11
			int[] bits = new int[11]; // 1 star + 8 datos + 2 stops

			bits[0] = BIT_CERO; // Bit de start
			// 8 Bit de datos
			for (int i = 0; i < 8; i++) {
				int mask = (int) Math.pow(2, i);
				int value = (dato & mask) >>> i; // >>> desplazamiento a la derecha
													// sin signo, tantas veces como
													// indique i
				if (value == 1)
					bits[i + 1] = BIT_UNO;
				else
					bits[i + 1] = BIT_CERO;
			}
			// 2 Bit Stop
			bits[9] = BIT_UNO; // Bit de stop
			bits[10] = BIT_UNO; // Bit de stop

			// Genero la señal senoidal
			for (int i = 0; i < numeroMuestras; ++i) {
				bitSecuencia = (int) i / numeroMuestrasPorBit; // determina por que
																// bit de la
																// secuencia va
				if (bits[bitSecuencia] == BIT_UNO)
					senoidal[i] = (Math.sin(2 * Math.PI * i * freq_Baja
							/ sampleRate));
				else
					senoidal[i] = (Math.sin(2 * Math.PI * i * freq_Alta
							/ sampleRate));
			}

			// Convierte a 16 bit pcm
			int j = 0;
			for (final double valores : senoidal) {
				// Escala a la máxima amplitud 32000
				final short val = (short) ((valores * 32767));
				// en 16 bit PCM, el primer byte es la parte más baja de los 16 bits
				salidaAudio[j++] = (byte) (val & 0x00ff);
				salidaAudio[j++] = (byte) ((val & 0xff00) >>> 8);
			}
		}

		// Fin de funciones de audio

}
