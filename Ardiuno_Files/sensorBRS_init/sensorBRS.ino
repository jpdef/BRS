#include <QueueList.h>
#include <NewPing.h>

//my libary
extern "C"{
  #include <List.h>
}

#define SONAR_NUM 2
#define MAX_DISTANCE  420// about 15 ft in cm -->need calculations to convert to inches
#define PING_INTERVAL 33 // time between sensor pings in ms -- 29ms is min
byte* packet;
const int packet_size = 16;


unsigned long pingTimer[SONAR_NUM]; //when to trigger pin(through array of sensors)
unsigned int inches[SONAR_NUM]; // stores distance of specific sensor
uint8_t currentSensor = 0; // tracks active sensor

NewPing sonar[SONAR_NUM] = {
	NewPing(12,8,MAX_DISTANCE),
	NewPing(2,4,MAX_DISTANCE),
};

QueueList <unsigned long> databuffer; 
//unsigned long sensorArray[]  = {0,0,0,0} // distance1,velocity1,distance2,velocity2
//List databuffer = newList();

void setup(){
	Serial.begin(115200);
        packet = (byte*)malloc(packet_size*sizeof(byte));
	pingTimer[0] = millis() + 75; // millis() return time from start of program
        for(uint8_t i=1; i < SONAR_NUM; i++){
		pingTimer[i] = (pingTimer[i-1] + PING_INTERVAL);
        }
}	
	
void loop(){
	for(uint8_t i =0; i < SONAR_NUM; i++){
		if(millis() >= pingTimer[i]){
			pingTimer[i] += PING_INTERVAL * SONAR_NUM; //continously updates trigger time
			if(i==0 && currentSensor == SONAR_NUM - 1) oneSensorCycle();// reach last sensor in array
			sonar[currentSensor].timer_stop();  // disables iterrupts in case was still running
			currentSensor=i; //update sensor working with
			inches[currentSensor] = 0; //reintialize distance to 0
			sonar[currentSensor].ping_timer(echoCheck); //sets interrupt and check frequency 
		}
	}
}

//checked every 2 micro seconds-- set in ping_timer()
void echoCheck() {  //If ping received, set the sensor distance to array.
    if (sonar[currentSensor].check_timer())
         inches[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN;
}
 
void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
    uint8_t* tmp = (uint8_t*) calloc(SONAR_NUM,sizeof(uint8_t));
    for (uint8_t i = 0; i < SONAR_NUM; i++) {
    	  //to serial monitor
    	  if(inches[i]!=0){
            tmp[i] = inches[i];
            /*Serial.print(i); 
    	    Serial.print(" =");
    	    Serial.print(inches[i]);
    	    Serial.pr/int(" inches ");
            Serial.println();
            */
          }
        }

        
  	//Serial.println();
  	//Serial.print("data ready to send");
        //Serial.println();
        /*Serial.print("the front of the list");
        Serial.print(front(dataP));
        Serial.println();
        Serial.print("the back of the list");
        Serial.print(back(dataP));
        
        sendData();
        clear(dataP);
  	*/
        databuffer.push(tmp);
        //deleteBack(dataPacket);
        //Serial.println();
}

void long_to_byte(unit8_t num, byte* byte_array, int start){
     byte_array[start] = (num & 0x000000FFUL);
}


void send_packet(unit8_t* data,int err){
     /* Need to implement error packet*/     
     packet[0] = 0x00; 
     packet[1] = 0xFF;               // start 
     long_to_byte(data[0],packet,2);      // data
     long_to_byte(0,packet,4);
     long_to_byte(data[1],packet,6);
     long_to_byte(0,packet,8);
     long_to_byte(0,packet,10);
     long_to_byte(0,packet,12);
     packet[packet_size-2] =  0xEE; //end
     packet[packet_size-1] =  0x00;  
           
     Serial.write(packet,packet_size);

}

void serialEvent(){
   int sig_byte = Serial.read();
   if(sig_byte == 0xFF){
      stp_strt = true;
      int i = 0;
       if(!databuffer.isEmpty)
       send_packet(databuffer.pop(),1);
    }else{
       Serial.write(0xEE);
       stp_strt = false;
       while(! q.isEmpty() ){
             q.pop();
       }
    }

}


void sendData(){
}
