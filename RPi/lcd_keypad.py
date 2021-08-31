import board
import digitalio
import adafruit_character_lcd.character_lcd as characterlcd

from time import sleep

lcd_columns = 16
lcd_rows = 2

lcd_rs = digitalio.DigitalInOut(board.D25)
lcd_en = digitalio.DigitalInOut(board.D24)
lcd_d7 = digitalio.DigitalInOut(board.D22)
lcd_d6 = digitalio.DigitalInOut(board.D27)
lcd_d5 = digitalio.DigitalInOut(board.D17)
lcd_d4 = digitalio.DigitalInOut(board.D23)
# lcd_backlight = digitalio.DigitalInOut(board.D13)


lcd = characterlcd.Character_LCD(
    lcd_rs, lcd_en, lcd_d4, lcd_d5, lcd_d6, lcd_d7, lcd_columns, lcd_rows
    )

def print_result(result_message):
    lcd.message = result_message
    sleep(3)
    lcd.clear()
# lcd.message = 'Hello python'
# lcd.clear()

if __name__ == '__main__':
    print_result('test msg')

