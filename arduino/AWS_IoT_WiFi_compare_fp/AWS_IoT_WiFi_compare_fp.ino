/*
  AWS IoT WiFi

  This sketch securely connects to an AWS IoT using MQTT over WiFi.
  It uses a private key stored in the ATECC508A and a public
  certificate for SSL/TLS authetication.

  It publishes a message every 5 seconds to arduino/outgoing
  topic and subscribes to messages on the arduino/incoming
  topic.

  The circuit:
  - Arduino MKR WiFi 1010 or MKR1000

  The following tutorial on Arduino Project Hub can be used
  to setup your AWS account and the MKR board:

  https://create.arduino.cc/projecthub/132016/securely-connecting-an-arduino-mkr-wifi-1010-to-aws-iot-core-a9f365

  This example code is in the public domain.
*/

#include <ArduinoBearSSL.h>
#include <ArduinoECCX08.h>
#include <ArduinoMqttClient.h>
#include <WiFiNINA.h> // change to #include <WiFi101.h> for MKR1000

#include <time.h>
#include <ArduinoJson.h>

#include "arduino_secrets.h"

#include <Adafruit_Fingerprint.h>
Adafruit_Fingerprint finger = Adafruit_Fingerprint(&Serial1);

/////// Enter your sensitive data in arduino_secrets.h
const char ssid[]        = SECRET_SSID;
const char pass[]        = SECRET_PASS;
const char broker[]      = SECRET_BROKER;
const char* certificate  = SECRET_CERTIFICATE;

WiFiClient    wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient    mqttClient(sslClient);

unsigned long lastMillis = 0;

//int LED = 6;  // 6번 포트에 연결
//String led_state = "OFF";  // led 현재 상태 변수

//int result_id = -1;
char* result_doc;
const char* msg;
const char* ignore1 = "No finger detected";
const char* ignore2 = "Unknown error";

void setup() {
  Serial.begin(115200);
  while (!Serial);

  //  pinMode(LED, OUTPUT);

  if (!ECCX08.begin()) {
    Serial.println("No ECCX08 present!");
    while (1);
  }

  // Set a callback to get the current time
  // used to validate the servers certificate
  ArduinoBearSSL.onGetTime(getTime);

  // Set the ECCX08 slot to use for the private key
  // and the accompanying public certificate for it
  sslClient.setEccSlot(0, certificate);

  // Optional, set the client id used for MQTT,
  // each device that is connected to the broker
  // must have a unique client id. The MQTTClient will generate
  // a client id for you based on the millis() value if not set
  //
  // mqttClient.setId("clientId");

  // Set the message callback, this function is


  // called when the MQTTClient receives a message
  //  mqttClient.onMessage(onMessageReceived);


  //  while (!Serial);  // For Yun/Leo/Micro/Zero/...
  //  delay(100);
  Serial.println("\n\nAdafruit finger detect test");

  // set the data rate for the sensor serial port
  finger.begin(57600);
  delay(5);
  if (finger.verifyPassword()) {
    Serial.println("Found fingerprint sensor!");
  } else {
    Serial.println("Did not find fingerprint sensor :(");
    while (1) {
      delay(1);
    }
  }

  Serial.println(F("Reading sensor parameters"));
  finger.getParameters();
  Serial.print(F("Status: 0x")); Serial.println(finger.status_reg, HEX);
  Serial.print(F("Sys ID: 0x")); Serial.println(finger.system_id, HEX);
  Serial.print(F("Capacity: ")); Serial.println(finger.capacity);
  Serial.print(F("Security level: ")); Serial.println(finger.security_level);
  Serial.print(F("Device address: ")); Serial.println(finger.device_addr, HEX);
  Serial.print(F("Packet len: ")); Serial.println(finger.packet_len);
  Serial.print(F("Baud rate: ")); Serial.println(finger.baud_rate);

  finger.getTemplateCount();

  if (finger.templateCount == 0) {
    Serial.print("Sensor doesn't contain any fingerprint data. Please run the 'enroll' example.");
  }
  else {
    Serial.println("Waiting for valid finger...");
    Serial.print("Sensor contains "); Serial.print(finger.templateCount); Serial.println(" templates");
  }
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }

  if (!mqttClient.connected()) {
    // MQTT client is disconnected, connect
    connectMQTT();
  }

  // poll for new MQTT messages and send keep alives
  mqttClient.poll();

  //  // publish a message roughly every 5 seconds.
  //  if (millis() - lastMillis > 5000) {
  //    lastMillis = millis();
  //
  //    publishMessage();
  //  }
//  char str[100];
//  time_t rawtime = getTime() + 32400;
//  struct tm * timeinfo;
//  timeinfo = localtime(&rawtime);
//  Serial.println(strftime(str, 100, "%Y-%m-%d %H:%M:%S", timeinfo));

  Serial.println(get_current_time());

  //  result_id = getFingerprintIDez();
  result_doc = getFingerprintJson(finger);
  Serial.println(result_doc);
  StaticJsonDocument<150> doc;
  deserializeJson(doc, result_doc);
  msg = doc["Message"];

  if (strcmp(msg, ignore1) != 0 && strcmp(msg, ignore2) != 0) {
    publishMessage(doc);
    Serial.println(msg);
  }
  else {
    Serial.println("Nothing SEND!!!");
  }

  delay(1000);
}

unsigned long getTime() {
  // get the current time from the WiFi module
  return WiFi.getTime();
}

String get_current_time()
{
  time_t rawtime = getTime() + 32400; // + 9시간(한국)
  struct tm * timeinfo;

//  time ( &rawtime );
  timeinfo = localtime ( &rawtime );

  char output[30];
  strftime(output, 30, "%Y-%m-%d %H:%M:%S", timeinfo);

  return String(output);
}

void connectWiFi() {
  Serial.print("Attempting to connect to SSID: ");
  Serial.print(ssid);
  Serial.print(" ");

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the network");
  Serial.println();
}

void connectMQTT() {
  Serial.print("Attempting to MQTT broker: ");
  Serial.print(broker);
  Serial.println(" ");

  while (!mqttClient.connect(broker, 8883)) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  Serial.println();

  Serial.println("You're connected to the MQTT broker");
  Serial.println();

  // subscribe to a topic
  //  mqttClient.subscribe("arduino/incoming");
}

void publishMessage(StaticJsonDocument<150> doc) {
  Serial.println("Publishing message");

  // send message, the Print interface can be used to set the message contents
  mqttClient.beginMessage("arduino/outgoing");

  char msg[150];
  //  StaticJsonDocument<100> doc;

  doc["Date"] = get_current_time();

  //  strcpy(msg, doc);
  serializeJson(doc, msg);

  mqttClient.print(msg);
  mqttClient.endMessage();
}

//void onMessageReceived(int messageSize) {
//  // we received a message, print out the topic and contents
//  Serial.print("Received a message with topic '");
//  Serial.print(mqttClient.messageTopic());
//  Serial.print("', length ");
//  Serial.print(messageSize);
//  Serial.println(" bytes:");
//
//  char json[512];
//  int count = 0;
//
//  // use the Stream interface to print the contents
//  while (mqttClient.available()) {
////    Serial.print((char)mqttClient.read());
//      json[count++] = (char)mqttClient.read();
//  }
//
//  DynamicJsonDocument doc(1024);
//  deserializeJson(doc, json);
//  String request = doc["LED"];
//
////  Serial.print(request);
////
////  if (request.equals("ON")) {
////    digitalWrite(LED, HIGH);
////    led_state = "ON";
////  }
////  else if (request.equals("OFF")) {
////    digitalWrite(LED, LOW);
////    led_state = "OFF";
////  }
//
//  Serial.println();
//  Serial.println();
//}
