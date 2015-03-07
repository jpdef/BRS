#include <QueueList.h>
#include <NewPing.h>

#define SONAR_NUM 1
#define MAX_DISTANCE 300 // 500 inches = 1270 cm = 41 ft  & 15ft=180in=457.2
#define PING_INTERVAL 33 // time between sensor pings in ms -- 29ms is min

unsigned long pingTimer[SONAR_NUM];  //when to trigger pin(through array of sensors)
unsigned int inches[SONAR_NUM];      // stores distance of specific sensor
unsigned int prev_inches[SONAR_NUM];
unsigned int prev_time[SONAR_NUM];
unsigned int V[SONAR_NUM];
unsigned int time=0,velc;
uint8_t currentSensor = 0;           // tracks active sensor
volatile boolean stp_strt = true;
NewPing sonar[SONAR_NUM] = {
	NewPing(6,7,MAX_DISTANCE),
	//NewPing(8,9,MAX_DISTANCE),
};

/*defines write buffer*/
int packet_size = 16;
byte* packet = (byte*)calloc(packet_size,sizeof(byte));

void print_data(){
  	for (uint8_t i = 0; i < SONAR_NUM; i++) {
    	  if(inches[i] > 0){
            Serial.println();
            Serial.print("distance = ");
            Serial.print(inches[i]);
            Serial.println();
            Serial.print("change Distance =  ");
            Serial.print(prev_inches[i]);
            Serial.println();
          }
        }
  	//Serial.println();
}

/*checks distance every 2ms, updates inches array*/
//attempt gathering velocity data here- note: extra code here not ideal
void echoCheck() {   
    if (sonar[currentSensor].check_timer()){
         prev_inches[currentSensor] = inches[currentSensor]; //<- lasted edit not copying any value
         inches[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN; //note: .ping_result returns time a for roundtrip
         prev_time[currentSensor] = prev_inches[currentSensor] *  US_ROUNDTRIP_IN * .5; //time previous waited for ping
         time = ((SONAR_NUM * PING_INTERVAL) + (inches[currentSensor] * US_ROUNDTRIP_IN * .5) - prev_time[currentSensor]); //need to account for different ping triggers
         V[currentSensor] = (inches[currentSensor] - prev_inches[currentSensor] / (time));
    }
}


/*converts uint to byte for serial communication*/
void int_to_byte(unsigned int num, byte* byte_array, int start){
     byte_array[start] = num;
}

/*makes packet out of data, then sends*/
void send_packet(int err){
     /* Need to implement error packet*/     //odd[index] = velocity
     packet[0] = 0x00;                       //packet[0] can be 0xFF then packet[1]= will be velocity[0]
     packet[1] = 0xFF;               // start 
      
     //needs editing for larger data
     packet[2] = (byte) 0;
     packet[4] = (byte) 0; 
     packet[6] = (byte) inches[0];
     packet[8] = (byte) inches[1];
     packet[10]= (byte) 100;
     packet[12]= (byte) 100;  
     packet[packet_size-2] =  0xEE; //end //this will be 13th put velocity here
     packet[packet_size-1] =  0x00;             
     Serial.write(packet,packet_size);
}


void oneSensorCycle() {        // Sensor ping cycle complete, send packet.
           if(inches[0] >0){
              send_packet(0);
              Serial.flush();
          }
}

void setup(){
	Serial.begin(115200);
	pingTimer[0] = millis() + 75; // millis() return time from start of program
        for(uint8_t i=1; i < SONAR_NUM; i++){
		pingTimer[i] = (pingTimer[i-1] + PING_INTERVAL);
        }
}	

/*Handles control signals from android*/
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
	      pingTimer[i] += PING_INTERVAL * SONAR_NUM;                   //continously updates trigger time
	      if(i==0 && currentSensor == SONAR_NUM - 1){                   //reach last sensor in array 
                   oneSensorCycle();                                         //send out data  
                   print_data();
              }   
              sonar[currentSensor].timer_stop();                           //disables iterrupts in case was still running
              currentSensor=i;                                             //update sensor working with
              inches[currentSensor] = 0;                                   //reintialize distance to 0
              sonar[currentSensor].ping_timer(echoCheck);                  //sets interrupt and check frequency 
           }
       }
       //memcpy(Di,inches,SONAR_NUM);
    }
}

 

 
