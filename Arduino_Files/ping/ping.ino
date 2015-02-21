#include <QueueList.h>
#include <NewPing.h>

#define SONAR_NUM 2
#define MAX_DISTANCE 500 // 500 inches = 1270 cm = 41 ft  & 15ft=180in=457.2
#define PING_INTERVAL 33 // time between sensor pings in ms -- 29ms is min

unsigned long pingTimer[SONAR_NUM];  //when to trigger pin(through array of sensors)
unsigned int inches[SONAR_NUM];      // stores distance of specific sensor
uint8_t currentSensor = 0;           // tracks active sensor
volatile boolean stp_strt = true;
NewPing sonar[SONAR_NUM] = {
	NewPing(12,8,MAX_DISTANCE),
	NewPing(2,4,MAX_DISTANCE),
};

/*defines write buffer*/
int packet_size = 16;
byte* packet = (byte*)calloc(packet_size,sizeof(byte));

void print_data(){
  	for (uint8_t i = 0; i < SONAR_NUM; i++) {
    	  Serial.print(i); 
    	  Serial.print(" =");
    	  Serial.print(inches[i]);
          Serial.print(" : ");
        }
  	Serial.println();
}

/*checks distance every 2ms, updates inches array*/
void echoCheck() {   
    if (sonar[currentSensor].check_timer())
         inches[currentSensor] = sonar[currentSensor].ping_result / US_ROUNDTRIP_IN;
}


/*converts uint to byte for serial communication*/
void int_to_byte(unsigned int num, byte* byte_array, int start){
     byte_array[start] = num;
}

/*makes packet out of data, then sends*/
void send_packet(int err){
     /* Need to implement error packet*/     
     packet[0] = 0x00; 
     packet[1] = 0xFF;               // start 
      
     //needs editing for larger data
     packet[2] = (byte) 0;
     packet[4] = (byte) 0; 
     packet[6] = (byte) inches[0];
     packet[8] = (byte) inches[1];
     packet[10]= (byte) 100;
     packet[12]= (byte) 100;  
     packet[packet_size-2] =  0xEE; //end
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
	      if(i==0 && currentSensor == SONAR_NUM - 1)                   //reach last sensor in array 
                 oneSensorCycle();                                         //send out data  
              sonar[currentSensor].timer_stop();                           //disables iterrupts in case was still running
              currentSensor=i;                                             //update sensor working with
              inches[currentSensor] = 0;                                   //reintialize distance to 0
              sonar[currentSensor].ping_timer(echoCheck);                  //sets interrupt and check frequency 
           }
       }
    }
}

 

 
