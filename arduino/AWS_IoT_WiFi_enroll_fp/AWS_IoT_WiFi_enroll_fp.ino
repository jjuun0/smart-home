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

#include "arduino_secrets.h"
#include <ArduinoJson.h>
#include <Adafruit_Fingerprint.h>

/////// Enter your sensitive data in arduino_secrets.h
const char ssid[]        = SECRET_SSID;
const char pass[]        = SECRET_PASS;
const char broker[]      = SECRET_BROKER;
const char* certificate  = SECRET_CERTIFICATE;

WiFiClient    wifiClient;            // Used for the TCP socket connection
BearSSLClient sslClient(wifiClient); // Used for SSL/TLS connection, integrates with ECC508
MqttClient    mqttClient(sslClient);

// 미리 FingerPrint DB 에서 조회후에 등록바람.
uint8_t want_id;  // 사용자가 등록하고 싶어하는 id
String want_name = "";  // 사용자가 등록하고 싶어하는 name
boolean send_message = false;

Adafruit_Fingerprint finger = Adafruit_Fingerprint(&Serial1);

void setup() {
  Serial.begin(115200);
  while (!Serial);

  Serial.println("\n\nAdafruit Fingerprint sensor enrollment");

  finger.begin(57600);

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

  Serial.print("Sensor contains ");
  Serial.print(finger.templateCount);
  Serial.println(" templates");

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
  mqttClient.onMessage(onMessageReceived);


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


  if (!send_message) { // 사용자로 부터 id 와 name 을 입력받는다
    want_id = readnumber();
    if (want_id < 1 || want_id > 127) {
      Serial.println("Please typing id 1 ~ 127");
      return;
    }
    Serial.println(want_id);

    want_name = readname();

    if (want_name.length() > 0) {
      Serial.println(want_name);
      publishMessage(want_id, want_name);
      send_message = true;
    }
  }

  delay(1000);
}

uint8_t readnumber() { // 시리얼 모니터에서 입력받음
  uint8_t num = 0;
  Serial.print("Put ID (from 1 to 127) : ");

  while (num == 0) {
    while (! Serial.available());
    num = Serial.parseInt();
  }
  return num;
}

String readname() {  // 시리얼 모니터에서 입력받음
  String string = "";
  char cTemp;
  Serial.print("Put Name : ");
  while (string == "") {
    while (! Serial.available());
    //     cTemp = Serial.read();
    //     string.concat(cTemp);
    string = Serial.readStringUntil('\n');
  }
  return string;
}

unsigned long getTime() {
  // get the current time from the WiFi module
  return WiFi.getTime();
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
  mqttClient.subscribe("aws/fingerprint/enroll");
}

void publishMessage(uint8_t id, String name_s) {
  // 사용자가 등록하고 싶어하는 id, name 을 DB 에서 조회하기 위해
  // aws 한테 위의 정보를 알려준다
  Serial.println("Publishing message");

  // send message, the Print interface can be used to set the message contents
  mqttClient.beginMessage("arduino/fingerprint/enroll");
  char msg[100];
  StaticJsonDocument<100> doc;

  doc["ID"] = id;
  doc["Name"] = name_s;

  serializeJson(doc, msg);
  mqttClient.print(msg);
  mqttClient.endMessage();
}


void publishMessageDBSave(int id, String name_s) {
  // 아두이노에서 지문등록 과정이 올바르게 수행된다면
  // db 에 저장하기 위해 aws 한테 저장하라고 알려준다.
  Serial.println("Publishing message DB Save");

  // send message, the Print interface can be used to set the message contents
  mqttClient.beginMessage("arduino/fingerprint/db");
  char msg[100];
  StaticJsonDocument<100> doc;

  doc["ID"] = id;
  doc["Name"] = name_s;

  serializeJson(doc, msg);
  mqttClient.print(msg);
  mqttClient.endMessage();
}

void onMessageReceived(int messageSize) {
  // DB 조회후 결과를 아두이노가 aws 로 부터 받는다
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  char receive[512];
  int count = 0;

  // use the Stream interface to print the contents
  while (mqttClient.available()) {
    receive[count++] = (char)mqttClient.read();
  }
  DynamicJsonDocument doc(1024);
  deserializeJson(doc, receive);
  String enroll = doc["Enroll"];
  String message = doc["Message"];
  Serial.println(message);

  if (enroll == "True") {  // 등록이 허가된다면 지문등록 + DB 저장
    while (!getFingerprintEnroll(want_id));
    publishMessageDBSave(want_id, want_name);
  }
  return;
}
