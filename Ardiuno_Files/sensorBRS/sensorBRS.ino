//working as of 2/12



#include <NewPing.h>

#include <NewPing.h>


#define SONAR_NUM 2
#define MAX_DISTANCE 200 // in cm
#define PING_INTERVAL 33// time between sensor pings in ms -- 29ms is min


unsigned long pingTimer[SONAR_NUM]; //when to trigger pin(through array of sensors)
unsigned int cm[SONAR_NUM]; // stores distance of specific sensor
uint8_t currentSensor = 0; // tracks active sensor

NewPing sonar[SONAR_NUM] = {
	NewPing(12,8,MAX_DISTANCE),
	NewPing(2,4,MAX_DISTANCE),
};

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
			pingTimer[i] += PING_INTERVAL * SONAR_NUM;
			if(i==0 && currentSensor == SONAR_NUM - 1) oneSensorCycle();// reach last sensor in array
			sonar[currentSensor].timer_stop();  // disables iterrupts in case was still running
			currentSensor=i; //update sensor working with
			cm[currentSensor] = 0; //reintialize distance to 0
			sonar[currentSensor].ping_timer(echoCheck); 
		}
	}
}

void echoCheck() {  //If ping received, set the sensor distance to array.
    if (sonar[currentSensor].check_timer())
         cm[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN;
}
      
void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
  	for (uint8_t i = 0; i < SONAR_NUM; i++) {
    	Serial.print(i);
    	Serial.print("=");
    	Serial.print(cm[i]);
    	Serial.print("inches ");
  	}
  		Serial.println();
}


