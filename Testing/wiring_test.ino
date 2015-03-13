
 
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY

const int trigPin_1 = 2;
const int trigPin_2 = 4;
const int trigPin_3 = 6;
const int trigPin_4 = 8;
const int trigPin_5 = 10;
const int trigPin_6 = 12;

const int echoPin_1 = 3;
const int echoPin_2 = 5;
const int echoPin_3 = 7; 
const int echoPin_4 = 9; 
const int echoPin_5 = 11; 
const int echoPin_6 = 13;
 
void setup() {
  // initialize serial communication:
  Serial.begin(115200);
}
 
long microsecondsToInches(long microseconds)
{
  // According to Parallax's datasheet for the PING))), there are
  // 73.746 microseconds per inch (i.e. sound travels at 1130 feet per
  // second).  This gives the distance travelled by the ping, outbound
  // and return, so we divide by 2 to get the distance of the obstacle.
  // See: http://www.parallax.com/dl/docs/prod/acc/28015-PING-v1.3.pdf
  return microseconds / 74 / 2;
}
void loop()
{
  // establish variables for duration of the ping, 
  // and the distance result in inches and centimeters:
  long duration_1, duration_2, inches_1, inches_2,duration_3,duration_4,duration_5,duration_6;
 
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
  
  //SENSOR 1
  pinMode(trigPin_1, OUTPUT);
  digitalWrite(trigPin_1, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_1, LOW);

  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echoPin_1, INPUT);
  duration_1 = pulseIn(echoPin_1, HIGH);
 
  
  //SENSOR 2
  pinMode(trigPin_2,OUTPUT);
  digitalWrite(trigPin_2, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_2, HIGH);
  digitalWrite(trigPin_2, LOW);
  pinMode(echoPin_2, INPUT);
  duration_2 = pulseIn(echoPin_2, HIGH);
 //SENSOR 3
  pinMode(trigPin_3,OUTPUT);
  digitalWrite(trigPin_3, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_3, HIGH);
  digitalWrite(trigPin_3, LOW);
  pinMode(echoPin_3, INPUT);
  duration_3 = pulseIn(echoPin_3, HIGH);
  
  //SENSOR 4
  pinMode(trigPin_4,OUTPUT);
  digitalWrite(trigPin_4, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_4, HIGH);
  digitalWrite(trigPin_4, LOW);
  pinMode(echoPin_4, INPUT);
  duration_4 = pulseIn(echoPin_4, HIGH);
  //SENSOR 5
  pinMode(trigPin_5,OUTPUT);
  digitalWrite(trigPin_5, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_5, HIGH);
  digitalWrite(trigPin_5, LOW);
  pinMode(echoPin_5, INPUT);
  duration_5 = pulseIn(echoPin_5, HIGH);
   
   //SENSOR 6
  pinMode(trigPin_6,OUTPUT);
  digitalWrite(trigPin_6, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_6, HIGH);
  digitalWrite(trigPin_6, LOW);
  pinMode(echoPin_6, INPUT);
  duration_6 = pulseIn(echoPin_6, HIGH);
  
 



  delayMicroseconds(10);

  Serial.println();
  if(duration_1>0) Serial.print("sensor 1 connected");
  else Serial.print("sensor 1 might be the issue");
  Serial.println();

  if(duration_2>0) Serial.print("sensor 2 connected");
  else Serial.print("sensor 2 might be the issue");
  Serial.println();

  if(duration_3 > 0) Serial.print("sensor 3 connected");
  else Serial.print("sensor 3 might be the issue");
  Serial.println();

  if(duration_4>0) Serial.print("sensor 4 connected");
  else Serial.print("sensor 4 might be the issue");
  Serial.println();

  if(duration_5 > 0)Serial.print("sensor 5 connected");
  else Serial.print("sensor 5 might be the issue");
  Serial.println();
 
 if(duration_6>0) Serial.print("sensor 6 connected");
  else Serial.print("sensor 6 might be the issue");
  Serial.println();

}
//Note:
// We want to interupt on RISING for 0 or 1 
// attachInterupt(0,some_fn,RISING);
//


