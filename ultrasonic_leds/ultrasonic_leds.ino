#include <ArduinoJson.h>
#define SENSOR 0
#include <Ultrasonic.h>
int dado; 
typedef struct Vaga
{
  String idVaga;
  int sensorVermelhoLED;
  int sensorAmareloLED;
  int sensorVaga;
  int statusSensor;
} vaga;
#define pino_trigger 4
#define pino_echo 5

vaga b1,b2;
Ultrasonic ultrasonic(pino_trigger, pino_echo);

int configuraStatusVaga(vaga *b, int statusSensor){
  b->statusSensor = statusSensor;
  return 0;
}

int criaJson(vaga *b){
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  root["vaga"] = b->idVaga;
  root["ocupada"] = b->statusSensor;
  root.printTo(Serial);
  Serial.println("");
}
int configuraVaga(vaga *b,String idVaga, int sensorVermelhoLED, int sensorAmareloLED, int sensorVaga){
  b->idVaga = idVaga;
  b->sensorVermelhoLED = sensorVermelhoLED;
  b->sensorAmareloLED = sensorAmareloLED;
  b->sensorVaga = sensorVaga;
  pinMode(b->sensorVermelhoLED, OUTPUT); 
  pinMode(b->sensorAmareloLED, OUTPUT); 
  pinMode(b->sensorVaga, INPUT); 
  return 0;
}

int checkVaga(vaga *b){
  int val = digitalRead(b->sensorVaga);
  configuraStatusVaga(b, val);
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
  }
  else
  {
    configuraStatusVaga(b, 1);
    digitalWrite(b->sensorVermelhoLED, LOW); 
    digitalWrite(b->sensorAmareloLED, HIGH);
  }
  return 0;
}

int enviaVagas(){
  
}
void setup() {
  configuraVaga(&b1,"1", 13, 12, 6);
  configuraVaga(&b2,"2", 10, 11, 7);
  Serial.begin(9600);
}

void loop() {
    checkVaga(&b1);
    checkVaga(&b2);
    if(Serial.available() > 0){ 
      dado = Serial.read();
      switch(dado){
                case 1:
                   criaJson(&b1);
                break;
                case 2:
                   criaJson(&b2);
                break;
    }
  }
}
