import cv2 as cv
import time


def capture(save_name):
    camera = cv.VideoCapture(0)

    ret, image = camera.read()
    time.sleep(2)
    cv.imwrite(save_name, image)

    camera.release()
    cv.destroyAllWindows()


if __name__ == '__main__':
    capture()