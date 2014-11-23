// Proyecto ANDRUINO. Versión AnduinoFSKv2
// Notas:
// El objetivo de este proyecto es construir un robot basado en android los más barato posible con mínimo número de componentes y con mínimo código
// La versión FSKv1 no posee sensores. El android solo usa el arduino como actuador sobre motores (para conseguir el mínimo número de componentes).
// La comunicación entre Arduino y Android es por modulación FSK de señal de audio, asíncrona y con explotación de circuito simplex.
// Si desea un robot más completo la versión AndruinoADK sí posee conexión a USB entre Android y Arduino y gran número de sensores.
// Autor: 
// Paco López. Basado en SoftModem-004 by arms22
// Entorno:
// Version de Arduino 0023. Librería de SoftModem modificada para funcionar a 630 bps
// Fecha
// Diciembre 2012, Modificado en 2013

#include <SoftModem.h>    

//definición módem
SoftModem modem;

//definición de pin de motores
int motorDAvance = 10;    //Pin PWM de Arduino
int motorDRetroceso = 9;  //Pin PWM de Arduino
int motorIAvance = 5;     //Pin PWM de Arduino, aunque no se usa, se implemente a "manualmente"
int motorIRetroceso= 8;   //Pin SIM PWM de Arduino, debe ser implentada "manualmente". 
//Nota: Se podría modificar circuito y usar inversor para sólo usar dos señales PWM para controlar motores

// Pines 6 y 7 son ocupados para la recepción de la señal FSK del Android
// Los pines 3 y 11 no pueden ser usados como PWM puesto que la demodulación FSK emplea Timer2 para la interrrupción del Comparador Analógico del microcontrolador

//definición de variables auxiliares
int i;
int comando;

//inicio modem y comunicaciones serie
void setup() {
  modem.begin();
  Serial.begin(9600);
  Serial.println("AndruinoFSKv2 2012");
  delay(500);
}


//blucle principal (el programa es de evaluación debería ser implementado como Red de Petri, pero no lo está)
void loop () {
  
  if(modem.available()) {
    //Lee el valor recibido por el modem  
      comando= modem.read();
      Serial.println("Comando Recibido: ");
      Serial.print(byte(comando));
      Serial.println(" ");  
 
      // Si recibe una ! (ascii 33) entonces RETROC durante 3 segundos (3000 MILISEGUNDOS)
     // PWM es implementado manualmente por lo que "bloquea" la ejecución de otras instrucciones en el microcontrolador
     switch (comando) {
            case 33:
               Serial.println("Retrocede");
               for (i= 1; i <2000; i++){

                 digitalWrite(motorIRetroceso, HIGH);
                 digitalWrite(motorDRetroceso, HIGH);
                 delayMicroseconds(800); //0,8 milisegundos ON 
                 digitalWrite(motorIRetroceso, LOW);
                 digitalWrite(motorDRetroceso, LOW);
                 delayMicroseconds(200); //0,2 milisengudos OFF
                }
              break;
            case 65:
                  Serial.println("Avanza");
                  analogWrite(motorIAvance,200);
                  analogWrite(motorDAvance,200);
                  //Espero 3 segundos
                  delay (2000);
                  //Apago motores
                  analogWrite(motorIAvance,0);
                  analogWrite(motorDAvance,0);
              break;
              case 77:
                  Serial.println("Para");
                   analogWrite(motorIAvance,0);
                   analogWrite(motorDRetroceso,0);
                    digitalWrite(motorIRetroceso, LOW);
                    digitalWrite(motorDRetroceso, LOW);
                  //Espero 3 segundos
                  delay (2000);
              break;
              case 45:
                  Serial.println("Der");
                    for (i= 1; i <2000; i++){

                     digitalWrite(motorIRetroceso, HIGH);
                     digitalWrite(motorDAvance, HIGH);
                     delayMicroseconds(800); //0,8 milisegundos ON 
                     digitalWrite(motorIRetroceso, LOW);
                     digitalWrite(motorDAvance, LOW);
                     delayMicroseconds(200); //0,2 milisengudos OFF
                }
                  //Espero 3 segundos
                  delay (2000);
              break;
              
              case 47:
                  Serial.println("Izq");
                    for (i= 1; i <2000; i++){

                     digitalWrite(motorIAvance, HIGH);
                     digitalWrite(motorDRetroceso, HIGH);
                     delayMicroseconds(800); //0,8 milisegundos ON 
                     digitalWrite(motorIAvance, LOW);
                     digitalWrite(motorDRetroceso, LOW);
                     delayMicroseconds(200); //0,2 milisengudos OFF
                }
                  //Espero 3 segundos
                  delay (2000);
              break;
            //default: 
     }
  }
}
     /*
     if (comando == 33) {
         Serial.println("Retrocede");
        for (i= 1; i <2000; i++){

           digitalWrite(motorIRetroceso, HIGH);
           digitalWrite(motorDRetroceso, HIGH);
           delayMicroseconds(800); //0,8 milisegundos ON 
           digitalWrite(motorIRetroceso, LOW);
           digitalWrite(motorDRetroceso, LOW);
           delayMicroseconds(200); //0,2 milisengudos OFF
        }
     }    
     // Si recibe una A (ascii 65) entonces AVANZA durante 3 segundos
     // PWM es implementado usando analogWrite (por interrupciones del microcontrolador)
     else if (comando == 65) {
        Serial.println("Avanza");
        analogWrite(motorIAvance,200);
        analogWrite(motorDAvance,200);
        //Espero 3 segundos
        delay (2000);
        //Apago motores
        analogWrite(motorIAvance,0);
        analogWrite(motorDAvance,0);
      
      } else {
        //APAGO MOTORES
          analogWrite(motorIAvance,0);
          analogWrite(motorDAvance,0);
          
          digitalWrite(motorIRetroceso, LOW);
          digitalWrite(motorDRetroceso, LOW);
        }*/

