#include <ESP8266WiFi.h>

//const char* ssid = "INFINITUM0420_2.4";
//const char* password = "0Kas3wHEJK";
char ssid [] = "LOZA_2";
char pass [] = "bugatti123";

const char* ssid_ap = "SOLAR_PANEL";
const char* pass_ap = "bugatti123";

//char ssid[] = "CHINGA_TU_MADRE";
//char pass[] = "272991218";
WiFiServer server(10001);
/*Direccion ip estática*/
IPAddress ip(192,168,1,99);
IPAddress mask(255,255,255,0);
/*
* Puerta de enlace: Para obtener la puerta de enlace en Windows presiona la tecla
* "Windows" Y la tecla "R" al mismo tiempo y escribe cmd, enseguida en aceptar, 
* en la linea de comandos escribe "ipconfig" y luego enter, después buscar la 
* puerta de enlace prederterminada y sobreescribir esta:
*/
IPAddress defaultGateway(192,168,1,254);

void setup() {
  Serial.begin(9600);
  // Connect to WiFi network
  WiFi.softAP(ssid_ap,pass_ap,1,0);
  WiFi.config(ip,defaultGateway,mask);
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("SUCCESS!");
  Serial.println("WiFi connected");

  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.print("Use this URL : ");
  Serial.print("http://");
  Serial.print(WiFi.localIP());
  Serial.println("/");


  // put your setup code here, to run once:
}
boolean isConnected;
void loop() {
  WiFiClient client = server.available();
  if (client) {

    if (client.connected()) {
      String datoAEnviar = "";
        datoAEnviar += "#";
        datoAEnviar += String(analogRead(0), DEC);
        datoAEnviar += "+";
        datoAEnviar += String(random(900,1024), DEC);
        datoAEnviar += "+";
        datoAEnviar += "~";
        Serial.println(datoAEnviar);
        client.println(datoAEnviar);
    }

    // close the connection:
    client.stop();
  }
}
