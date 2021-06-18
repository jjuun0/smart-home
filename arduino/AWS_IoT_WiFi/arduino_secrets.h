// Fill in  your WiFi networks SSID and password
#define SECRET_SSID "와이파이 이름"
#define SECRET_PASS "와이파이 비번"

// Fill in the hostname of your AWS IoT broker
#define SECRET_BROKER "aws iot 엔드 포인트"

// Fill in the boards public certificate
const char SECRET_CERTIFICATE[] = R"(
-----BEGIN CERTIFICATE-----
aws iot - 보안 - 인증서 - 다운로드 -> (.crt) 파일의 내용
-----END CERTIFICATE-----
)";
