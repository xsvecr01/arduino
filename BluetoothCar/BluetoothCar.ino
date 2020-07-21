//Controlling car with bluetooth: 1 dc motor, H-bridge, 1 servo for steering
#include <Servo.h>

Servo servo;
String value1, value2;
int steer, motor;
int fwd = 5;
int bwd = 6;
//int speeed = 3;
String input = "";

void setup() 
{
  Serial.begin(9600);
  delay(500);
  
  //pinMode(speeed, OUTPUT); 
  pinMode(fwd, OUTPUT);
  pinMode(bwd, OUTPUT);
  
  servo.attach(9);
  servo.write(95);  // set servo to mid-point
}

void loop()
{ 
  if (Serial.available() > 0)
  {
    input = Serial.readStringUntil('X');

    //divide input string into 2 parts: steer for servo and speed for motor
    for (int i = 0; i < input.length(); i++)
    {
      if (input.substring(i, i+1) == "\n")
      {
        value2 = input.substring(0, i);
        value1 = input.substring(i+1);
      break;
      }
    }
    //divided strings: value1 - servo ; value2 - motor
    steer = (value1.toInt());
    motor = (value2.toInt());
    servo.write(steer);

    if (motor > 10 && motor <= 255){
      //analogWrite(fwd, (motor-10)*5);
      analogWrite(fwd, motor);
      analogWrite(bwd, 0);
      //analogWrite(speeed, (motor-10)*5);
    }

    else if (motor < -10 && motor >= -255){
      analogWrite(fwd, 0);
      analogWrite(bwd, -(motor));
    }

    else if (motor < 10 && motor > -10){
      analogWrite(fwd, 0);
      analogWrite(bwd, 0);
    }

    else Serial.println("konec");
    delay(1);
  }

  else{
    servo.write(95);
    analogWrite(fwd,0);
    analogWrite(bwd,0);
  }
}
