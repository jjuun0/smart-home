from gpiozero import Servo
from time import sleep

servo = Servo(18)
sleep(1)
    
def open_door():
    servo.max()
    sleep(1)
    print('open the door!')
    servo.min()
    sleep(1)
    
        
if __name__ == '__main__':
    open_door()
    
    
