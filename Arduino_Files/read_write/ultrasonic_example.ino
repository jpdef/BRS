 #include <QueueList.h>
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY
const byte err_packet[] = {0xEF};
const num_sensors = 6;
bool  stp_strt = true;
int startByte =0;

byte* packet;
QueueList<long> q;
QueueList<long*> ql;
void setup() {
  // initialize serial communication:
  Serial.begin(115200);
  packet = (byte*)malloc(packet_size*sizeof(byte));
  srand(42);
}

void long_to_byte(long* nums, byte* byte_array){
     //take only 8 bits
     for(int i = 0; i < num_sensors; ++i){
       byte_array[i] = (num[0] & 0x000000FFUL);
     }
}



void send_packet(long r_1, long r_2,int err){
     /* Need to implement error packet*/     
     packet[0] = 0x00; 
     packet[1] = 0xFF;               // start 
     long_to_byte(r_1,packet,2);      // data
     packet[packet_size-2] =  0xEE; //end
     packet[packet_size-1] =  0x00;  
           
     Serial.write(packet,packet_size);

}

void serialEvent(){
   int sig_byte = Serial.read();
   long buff[6] = {0,0,0,0,0,0};
   if(sig_byte == 0xFF){
      stp_strt = true;
      int i = 0;
      while(!q.isEmpty() && i < 5){
            buff[i] = q.pop();  
            ++i;    
       }
       send_packet(buff[0],buff[1],1);
    }else{
       Serial.write(0xEE);
       stp_strt = false;
       while(! q.isEmpty() ){
             q.pop();
       }
    }

}

void loop()
{
 if(stp_strt){
   long* a = (long*)malloc(6*sizeof(long));
   ql.push(a);
   long* r = ql.pop();
   q.push(get_distance(trigPin_1,echoPin_1));
   q.push(get_distance(trigPin_2,echoPin_2)); 
   delay(50);
 }
}    
     
     
long microsecondsToCentimeters(long microseconds)
{    
      // The speed of sound is 340 m/s or 29 microseconds per centimeter.
      // The ping travels out and back, so to find the distance of the
      // object we take half of the distance travelled.
      return microseconds / 29 / 2;
}    
     
    
    
    
    
