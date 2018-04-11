// Internet de las cosas / http://internetdelascosas.cl
//
// Sketch que permite activar un relay 

int relay = D1;

// La rutina setup se ejecuta al iniciar el Arduino o al presionar el boton reset
void setup() {                
// Inicialica el pin digital relay (que vale 2)  como salida
  pinMode(relay, OUTPUT);     
}

// La rutina loop se ejecuta en forma infinita despues de inicializado el Arduino
void loop() {
  digitalWrite(relay, HIGH);   // Envia el valor HIGH (5V) al pin digital relay
  delay(5000);                 // Espera 2 segundos
  digitalWrite(relay, LOW);    // Envia el valor LOW (0V) al pin digital relay
  delay(5000);                // Espera 10 segundos
}
