/*
 * sketch for Embedded World show
 * Lantronix Inc.
 * xPico Wifi Arduino Demo
 *
 * Simple serial protocol to turn on, turn off or Blink the LED
 *   1 - turn on
 *   0 - turn off
 *   2 - blink the LED
 *
 *    written by Gary Marrs, Lantronix FAE
 *    02-21-14
*/

const int ledPin = 13; // pin the LED is connected to
int   blinkRate=0;     // blink rate stored in this variable
int num = 0;
boolean btrue;

void setup()
{
  Serial.begin(9600); // Initialize serial port to send and receive at 9600 baud
  pinMode(ledPin, OUTPUT); // set this pin as output
  delay(10000);                                          // delay 10 secs to allow xPico Wifi to boot up
  //Serial.println("setup complete");
}

void loop()
{
 Serial.println("hola");

}  // end of loop

// blink the LED with the on and off times determined by blinkRate
void blink()
{
  Serial.println("hola!");
  digitalWrite(ledPin,HIGH);
  delay(blinkRate); // delay depends on blinkrate value
  digitalWrite(ledPin,LOW);
  delay(blinkRate);
}
