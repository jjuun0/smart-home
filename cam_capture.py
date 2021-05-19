import cv2
import time


def capture(save_name):
    camera = cv2.VideoCapture(0)

    ret, image = camera.read()
    # time.sleep(2)
    cv2.imwrite(save_name, image)

    camera.release()
    cv2.destroyAllWindows()


if __name__ == '__main__':
    print(cv2.__version__)
    # capture()