//#include <QueueList.h>
#include <NewPing.h>

//my libary
extern "C"{
  #include <List.h>
}

#define SONAR_NUM 2
#define MAX_DISTANCE 200 // 500 inches = 1270 cm = 41 ft  & 15ft=180in=457.2
#define PING_INTERVAL 33 // time between sensor pings in ms -- 29ms is min


unsigned long pingTimer[SONAR_NUM]; //when to trigger pin(through array of sensors)
unsigned int inches[SONAR_NUM]; // stores distance of specific sensor
uint8_t currentSensor = 0; // tracks active sensor

NewPing sonar[SONAR_NUM] = {
	NewPing(12,8,MAX_DISTANCE),
	NewPing(2,4,MAX_DISTANCE),
};

//QueueList <unsigned long> dataPacket; 
//create newList
List dataPacket = newList();

void setup(){
	Serial.begin(115200);
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

//checked every 2 micro seconds
void echoCheck() {  //If ping received, set the sensor distance to array.
    if (sonar[currentSensor].check_timer())
         inches[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN;
}
 
void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
  	for (uint8_t i = 0; i < SONAR_NUM; i++) {
    	  //to serial monitor
    	  Serial.print(i); 
    	  Serial.print(" =");
    	  Serial.print(inches[i]);
    	  Serial.print(" inches ");
    	  //dataPacket.push(inches[i]); //to send
        }

        
  	   Serial.println();
  	   Serial.print("data ready to send");
       Serial.println();
        /*Serial.print("the front of the list");
        Serial.print(front(dataP));
        Serial.println();
        Serial.print("the back of the list");
        Serial.print(back(dataP));
        
        sendData();
        clear(dataP);
  	    */
        append(dataPacket,inches);
        //clear(dataPacket); // not sure when to delete/how many will queue up? 
        deleteFront(dataPacket);
        Serial.println();
}

 
void sendData(){
    
    
}
