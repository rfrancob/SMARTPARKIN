#include <ArduinoJson.h>
#define SENSOR 0
#include <Ultrasonic.h>
#include <SoftwareSerial.h>
#include<String.h>

#define rxPin 2
#define txPin 3
#define pino_trigger 4
#define pino_echo 5
#define pinLedRFID 11
SoftwareSerial mySerial =  SoftwareSerial(rxPin, txPin);
Ultrasonic ultrasonic(pino_trigger, pino_echo);
int data1 = 0, dado = 0;
int newtag[14] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};

typedef struct Vaga
{
  int idVaga;
  int sensorVermelhoLED;
  int sensorAmareloLED;
  int statusSensor;
  double latitude;
  double longitude;
} vaga;

vaga b1;


int configuraStatusVaga(vaga *b, int statusSensor){
  b->statusSensor = statusSensor;
  return 0;
}

void clearTag(){
   for (int z=0; z<14; z++) 
    {
      newtag[z]=0;
    }
}

void readTag() 
// poll serial port to see if tag data is coming in (i.e. a read attempt)
{
  if (mySerial.available() > 0) // if a read has been attempted
  {
    // read the incoming number on serial RX
    digitalWrite(pinLedRFID, HIGH);
    delay(100);  // Needed to allow time for the data to come in from the serial buffer. 
    for (int z=0; z<14; z++) // read the rest of the tag
    {
      data1=mySerial.read();
      newtag[z]=data1;
    }
    digitalWrite(pinLedRFID, LOW);
    mySerial.flush(); // stops multiple reads
  }
}

int criaJson(vaga *b){
  StaticJsonBuffer<256> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  root["vaga"] = b->idVaga;
  root["ocupada"] = b->statusSensor;
  root["latitude"] = b->latitude;
  root["longitude"] = b->longitude;
  JsonArray& array = jsonBuffer.createArray();
  for (int i=0; i<14; i++) {
    String str = String( newtag[i] , DEC );
    array.add(str);
  }
  root["key"] = array;

  root.printTo(Serial);
  Serial.println("");
}

int configuraVaga(vaga *b,int idVaga, int sensorVermelhoLED, int sensorAmareloLED, double latitude, double longitude){
  b->idVaga = idVaga;
  b->sensorVermelhoLED = sensorVermelhoLED;
  b->sensorAmareloLED = sensorAmareloLED;
  b->longitude = longitude;
  b->latitude = latitude;
  pinMode(b->sensorVermelhoLED, OUTPUT); 
  pinMode(b->sensorAmareloLED, OUTPUT); 
  return 0;
}
int count = 0;
int checkVaga(vaga *b){
  
  float cmMsec, inMsec = 0;
  long microsec = ultrasonic.timing();

  for(int i = 0; i < 4; i++)
  inMsec += ultrasonic.convert(microsec, Ultrasonic::CM);

  cmMsec = inMsec/4;
  if (cmMsec > 50) 
  {
    configuraStatusVaga(b, 0);
    digitalWrite(b->sensorVermelhoLED, HIGH);
    digitalWrite(b->sensorAmareloLED, LOW);

    clearTag();
  }
  else
  {
    configuraStatusVaga(b, 1);
    digitalWrite(b->sensorVermelhoLED, LOW); 
    digitalWrite(b->sensorAmareloLED, HIGH);
    readTag();

  }
  return 0;
}


void setup() {
  configuraVaga(&b1,1, 13, 12, 15153123, 54579412);
  pinMode(rxPin, INPUT);
  pinMode(txPin, OUTPUT);
  
  pinMode(pinLedRFID, OUTPUT);
  mySerial.begin(9600);
  mySerial.flush(); 
  Serial.begin(9600);
}

void loop() {
    checkVaga(&b1);
    if(Serial.available() > 0){ 
      dado = Serial.read();
      switch(dado){
                case 1:
                   criaJson(&b1);
                break;
    }
  }
}
