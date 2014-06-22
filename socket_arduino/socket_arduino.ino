#include <SoftwareSerial.h>
#include <SPI.h>
#include <Mirf.h>
#include <nRF24L01.h>
#include <MirfHardwareSpiDriver.h>

#define ADDRESS  "socket"
#define SEND_ADDRESS  "sink"
#define DEALAY_RATIO  20

unsigned long sent_message_payload = 0;
unsigned long received_message_payload = 0;

SoftwareSerial mySerial(5, 6); //

unsigned long energy_consumption = 1;

char incomingByte;  // incoming data
int  Device_Control_PIN = 12;

uint16_t delays_number = 0;

boolean device_status = 0;

void setup() {
  mySerial.begin(9600);
  Serial.begin(9600);
  
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
    if(incomingByte == '0') {
       digitalWrite(Device_Control_PIN, LOW);
       device_status = 0;
       //mySerial.println("OFF");
    }
    if(incomingByte == '1') {
       digitalWrite(Device_Control_PIN, HIGH);
       device_status = 1;
       //mySerial.println("ON");
    }
  }
  
  if(Mirf.dataReady()){
    
    Mirf.getData((byte *) &received_message_payload);
   
    incomingByte = (uint8_t) received_message_payload;
    
    Serial.print("NRF_incomingByte = ");          
    Serial.println(incomingByte);          

    if(incomingByte == 0) {
       digitalWrite(Device_Control_PIN, LOW);
       device_status = 0;
       Serial.println("OFF");
    }
    if(incomingByte == 1) {
       digitalWrite(Device_Control_PIN, HIGH);
       device_status = 1;
       Serial.println("ON");
    }
  }
  
  energy_consumption++;
  
  if( (delays_number % DEALAY_RATIO) == 0 && device_status == 1){
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










