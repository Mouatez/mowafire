#include <SoftwareSerial.h>
#include <SPI.h>
#include <Mirf.h>
#include <nRF24L01.h>
#include <MirfHardwareSpiDriver.h>

#define ADDRESS "socket"
#define SEND_ADDRESS "sink"
#define DEALAY_RATIO 20
#define CURRENT_SENSOR A0 // Analog input pin that sensor is attached to
#define ON 1
#define OFF 0
 
float amplitude_current;               //amplitude current
float effective_value;       //effective current

unsigned long sent_message_payload = 0;
unsigned long received_message_payload = 0;

SoftwareSerial mySerial(5, 6); //

unsigned long energy_consumption = 0;

char incomingByte;
int  Device_Control_PIN = 2;

uint16_t delays_number = 0;

boolean device_status = OFF;

void setup() {
  mySerial.begin(9600);
  Serial.begin(9600);
  pinMode(CURRENT_SENSOR, INPUT);
  Mirf.spi = &MirfHardwareSpi;
  
  Mirf.init();
  
  Mirf.setRADDR((byte *) ADDRESS);
  
  Mirf.payload = sizeof(unsigned long);

  Mirf.config();
  
  pinMode(Device_Control_PIN, OUTPUT);
}

void loop() {
 
  if (mySerial.available() > 0) { 
    incomingByte = mySerial.read();
    Serial.print("Bluetooth_message = ");          
    Serial.println(incomingByte);          

    incomingByte = incomingByte - '0'; 
    if(incomingByte == ON) {
       digitalWrite(Device_Control_PIN, LOW);
       device_status = ON;
       Serial.println("Bluetooth_ON");
    }
    if(incomingByte == OFF) {
       digitalWrite(Device_Control_PIN, HIGH);
       device_status = OFF;
       Serial.println("Bluetooth_OFF");
    }
  }

  if(Mirf.dataReady()){
    
    Mirf.getData((byte *) &received_message_payload);
   
    incomingByte = (uint8_t) received_message_payload;
    
    Serial.print("NRF_message = ");          
    Serial.println(incomingByte);          

    if(incomingByte == ON) {
       digitalWrite(Device_Control_PIN, LOW);
       device_status = ON;
       Serial.println("NRF_ON");
    }
    if(incomingByte == OFF) {
       digitalWrite(Device_Control_PIN, HIGH);
       device_status = OFF;
       Serial.println("NRF_OFF");
    }
  }
  
  //energy_consumption++;
  
  if( (delays_number % DEALAY_RATIO) == 0 && device_status == ON){
    int sensor_max;
 
    sensor_max = getMaxValue();
    //Serial.println(sensor_max);
 
    amplitude_current=(float)(sensor_max-512)/1024*5/100*1000000;
    //Serial.println(amplitude_current);

    effective_value=amplitude_current/1.414;
    //Serial.println(effective_value);
    
    energy_consumption = effective_value;
    mySerial.println(energy_consumption);
  
    sent_message_payload = energy_consumption;
  
    Mirf.setTADDR((byte *)SEND_ADDRESS);
    Mirf.send((byte *)&sent_message_payload);
  
    while(Mirf.isSending()){
    }
  }

  delays_number++;
  delay(50);
}

int getMaxValue()
{
    int sensorValue;             //value read from the sensor
    int sensorMax = 0;
    uint32_t start_time = millis();
    while((millis()-start_time) < 200)//sample for 1000ms
    {
        sensorValue = analogRead(CURRENT_SENSOR);
        if (sensorValue > sensorMax)
        {
            /*record the maximum sensor value*/
            sensorMax = sensorValue;
        }
    }
    return sensorMax;
}

