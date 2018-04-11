

/*
  Diciembre del 2016

  Sketch para el Robodyn D1 R2 con módulo WiFi ESP8266
  Dedicado a leer dos entradas analógicas enviando una
  señal digital a un relé y así poder alternar la única
  entrada análogica que contiene, para enviarlos por medio
  de un servidor con dirección ip y un puerto.

  Autor: Angelo Loza
*/
#include <ESP8266WiFi.h>
/*
  Nombre y contraseña de la red a conectar
*/
const char* ssid = "LOZA_2";
const char* pass = "bugatti123";
/*
  Nombre y contraseña del WiFi Robodyn (Arduino)
*/
const char* ssid_ap = "SOLAR_PANEL";
const char* pass_ap = "mexoldroid";
/*
  Puerto para acceder al Robodyn
*/
const int port = 10001;
/*
  Crea un servidor con el puerto
*/
WiFiServer server(port);
/*Direccion ip estática (cambiar si es necesario)*/
IPAddress ip(192, 168, 1, 100);
/*Máscara de red (24, cambiar si es necesario)*/
IPAddress mask(255, 255, 255, 0);
/*
  Puerta de enlace (Default Gateway, cambiar si es necesario): Para obtener la puerta de enlace en Windows
  presiona la tecla"Windows" Y la tecla "R" al mismo tiempo y escribe cmd, enseguida en aceptar,
  en la linea de comandos escribe "ipconfig" y luego enter, después buscar la LAN Inalámbrica y la
  puerta de enlace prederterminada y sobreescribir esta:
*/
IPAddress defaultGateway(192, 168, 1, 254);

/*
  Pin del relé
*/
int relay = D1;

void setup() {
  Serial.begin(9600);
  /*
    Establece el nombre y contraseña del módulo ESP8266
    además del canal (default 1) y si quiere esconder
    el nombre de la red.
  */
  WiFi.softAP(ssid_ap, pass_ap, 1, 0);
  /*Conectar a red WiFi*/
  WiFi.config(ip, defaultGateway, mask);
  Serial.print("Conectando a ");
  Serial.println(ssid);
  /*Trata de conectar a la red establecida*/
  WiFi.begin(ssid, pass);
  boolean isConnected;

  while (WiFi.status() != WL_CONNECTED) {
    if (millis() >= 8000) {
      Serial.println("Tiempo fuera de conexión");
      isConnected = false;
      break;
    }
    isConnected = true;
    delay(500);
    Serial.print(".");
  }
  if (isConnected) {
    Serial.println("Se ha conectado exitosamente.");
    Serial.println("WiFi conectado");

    /*Comienza el servidor*/
    server.begin();
    Serial.println("Servidor iniciado");

    /*Imprime la dirección y el puerto*/
    Serial.print("Para acceder a los datos ingresa esta dirección: ");
    Serial.print("http://");
    Serial.print(WiFi.localIP());
    Serial.println(":" + String(port, DEC));
  } else {
    Serial.println("No se ha podido conectar, tiempo fuera, puede acceder al servidor localmente");
    Serial.print("Para acceder a los datos conectese e ingrese esta dirección: ");
    Serial.print("http://");
    Serial.print(WiFi.localIP());
    Serial.println(":" + String(port, DEC));
  }
  pinMode(relay, OUTPUT);
  digitalWrite(relay, LOW);
}
void loop() {
  /*¿Hay algún cliente que requiera los datos?*/
  WiFiClient client = server.available();
  if (client) {
    /*¿El cliente está conectado?*/
    if (client.connected()) {
      /*Concatena los datos a enviar*/
      String datoAEnviar = "";
      /*Símbolo para decir que se comienzan a enviar datos: '#'*/
      datoAEnviar += "#";
      /*Dato 1*/
      datoAEnviar += String(analogRead(0), DEC);
      /*Símbolo de fin de dato 1: '+'*/
      datoAEnviar += "+";
      /*
        Manda uno al relé (pin digital 3 - IO, 10k Pull-up)
      */
      digitalWrite(relay, LOW);
      /*
         Retraso de 100 milisegundos (bastante rápido, calibrar
         segun necesidades)
      */
      delay(200);
      /*
        Quitar el random y uncomment el analogRead
      */
      datoAEnviar += String(analogRead(0), DEC);
      /*Envio de cero a relé*/
      digitalWrite(relay, HIGH);
      delay(200);
      /*Fin de dato 2*/
      datoAEnviar += "+";
      /*Fin de envio de datos: '~'*/
      datoAEnviar += "~";
      /*Imprime a serial y envía a cliente*/
      Serial.println(datoAEnviar);
      client.println(datoAEnviar);
    }
    /*Cierra la conexión*/
    client.stop();
  }
}
