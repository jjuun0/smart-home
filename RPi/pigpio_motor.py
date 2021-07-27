import pigpio
from time import sleep

pi = pigpio.pi()


def open_door():
    pi.set_servo_pulsewidth(18, 0)
    sleep(1)

    # 90 degree
    pi.set_servo_pulsewidth(18, 1500)
    sleep(3)

    # 0 degree
    pi.set_servo_pulsewidth(18, 600)
    sleep(1)


def infinite():
    while True:
            pi.set_servo_pulsewidth(18, 0)
            sleep(1)

            # 0 degree
            pi.set_servo_pulsewidth(18, 600)
            sleep(1)

            # 90 degree
            pi.set_servo_pulsewidth(18, 1500)
            sleep(1)

            # 180 degree
            pi.set_servo_pulsewidth(18, 2400)
            sleep(1)


if __name__ == "__main__":
    # infinite()
    open_door()

