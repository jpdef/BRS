#include <QueueList.h>
#include <NewPing.h>

#define SONAR_NUM 1
//#define MAX_DISTANCE 500 // 500 inches = 1270 cm = 41 ft  & 15ft=180in=457.2
#define MAX_DISTANCE 500 // test for more than 10ft. Should produce a lot of noise.
#define PING_INTERVAL 33 // time between sensor pings in ms -- 29ms is min

unsigned long pingTimer[SONAR_NUM]; //when to trigger pin(through array of sensors)
//unsigned int inches[SONAR_NUM]; // stores distance of specific sensor
signed int inches[SONAR_NUM];
uint8_t currentSensor = 0; // tracks active sensor
volatile boolean stp_strt = true;
NewPing sonar[SONAR_NUM] = {
	//NewPing(2,3,MAX_DISTANCE),
	//NewPing(4,5,MAX_DISTANCE),
	NewPing(6,7,MAX_DISTANCE), // perform test only on one sensor for easiness
	//NewPing(8,9,MAX_DISTANCE),
	//NewPing(10,11,MAX_DISTANCE),
	//NewPing(12,13,MAX_DISTANCE),
};

bool send =false;
int packet_size = 16;
byte* packet = (byte*)calloc(packet_size,sizeof(byte));

QueueList <unsigned int> databuffer; 
void print_data(){
        //all testing will happen here since this will print out the test cases.
  	for (uint8_t i = 0; i < SONAR_NUM; i++) {
          //testing more than 10 ft should cause a lot of OUT OF BOUNDS since data is really noisy
          if(inches[i] == 0){
            Serial.print("OUT OF BOUNDS!!!!");
          } else {
    	    Serial.print(i); 
    	    Serial.print(" =");
    	    Serial.print(inches[i]);
            Serial.print(" : ");
           }
  	Serial.println();
  }
}

//checked every 2 micro seconds
void echoCheck() {  //If ping received, set the sensor distance to array.
    if (sonar[currentSensor].check_timer())
         inches[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN;
}



void int_to_byte(unsigned int num, byte* byte_array, int start){
     byte_array[start] = num;
}

void send_packet(int err){
     /* Need to implement error packet*/     
     packet[0] = 0x00; 
     packet[1] = 0xFF;               // start 
      
     //needs editing for larger data
     packet[2] = (byte) inches[0];
     packet[4] = (byte) inches[1]; 
     packet[6] = (byte) inches[2];
     packet[8] = (byte) inches[3];
     packet[10]= (byte) inches[4];
     packet[12]= (byte) inches[5];  
     packet[packet_size-2] =  0xEE; //end
     packet[packet_size-1] =  0x00;             
     Serial.write(packet,packet_size);
}



void oneSensorCycle() { // Sensor ping cycle complete, do something with the results.
       // push data to queue;
         // if(inches[0] >0){
              print_data();
         // }
}

void setup(){
	Serial.begin(115200);
	pingTimer[0] = millis() + 75; // millis() return time from start of program
     
	for(uint8_t i=1; i < SONAR_NUM; i++){
		pingTimer[i] = (pingTimer[i-1] + PING_INTERVAL);
        }
}	


void serialEvent(){ 
   int num_bytes = Serial.available();
    while(num_bytes>0){
   int sig_byte = Serial.read();
   --num_bytes;
   if(sig_byte == 0xFF){
      stp_strt =true;
      break;
    }else{
      stp_strt = false;
      break;
     }
   }
}

void loop(){
     if(stp_strt){
       for(uint8_t i =0; i < SONAR_NUM; i++){
		if(millis() >= pingTimer[i]){
			pingTimer[i] += PING_INTERVAL * SONAR_NUM;                  //continously updates trigger time
			if(i==0 && currentSensor == SONAR_NUM - 1)
                                oneSensorCycle();                                    //reach last sensor in array
                        sonar[currentSensor].timer_stop();                           //disables iterrupts in case was still running
			currentSensor=i;                                             //update sensor working with
			inches[currentSensor] = 0;                                   //reintialize distance to 0
			sonar[currentSensor].ping_timer(echoCheck); //sets interrupt and check frequency 
		}
	}
      }
}

 

 
