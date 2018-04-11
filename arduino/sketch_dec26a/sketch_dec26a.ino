#include <ESP8266WiFi.h>

void setup() {
  Serial.begin(9600);
  // put your setup code here, to run once:

}

void loop() {
  Serial.print("a0:");
  Serial.println(analogRead(0));
  Serial.print("a1:");
  Serial.println(analogRead(1));
  delay(3000);
  // put your main code here, to run repeatedly:

}
