#include <SPI.h>
#include <Mirf.h>
#include <nRF24L01.h>
#include <MirfHardwareSpiDriver.h>

#define ADDRESS "sink"
#define SEND_ADDRESS "socket"

unsigned long sent_message_payload = 0;
unsigned long received_message_payload = 0;

char incomingByte;

void setup(){
  Serial.begin(9600);

  Mirf.spi = &MirfHardwareSpi;
  
  Mirf.init();
  
  Mirf.setRADDR((byte *) ADDRESS);
  
  Mirf.payload = sizeof(unsigned long);

  Mirf.config();
}

void loop(){
  
  if (Serial.available() > 0) { 
    incomingByte = Serial.read();
    Mirf.setTADDR((byte *)SEND_ADDRESS);
    
    if(incomingByte == '0') {
      sent_message_payload = 0;
      Mirf.send((byte *)&sent_message_payload);
  
      while(Mirf.isSending()){
      }
/*
    Serial.println(ADDRESS);
    Serial.print("sent_message_payload = ");          
    Serial.println(sent_message_payload);          
    Serial.println("Finished sending");
*/
    }

    if(incomingByte == '1') {
      sent_message_payload = 1;
      Mirf.send((byte *)&sent_message_payload);
      
      while(Mirf.isSending()){
      }
/*
    Serial.println(ADDRESS);
    Serial.print("sent_message_payload = ");          
    Serial.println(sent_message_payload);          
    Serial.println("Finished sending");
*/
    }
  }
  
  if(Mirf.dataReady()){
    Mirf.getData((byte *) &received_message_payload);
    Serial.println(received_message_payload);    
  }

  //delay(50);  
}
