# DroneController

調整 PID / 遙控無人機

## 相關 repo

- [Drone](https://github.com/lbc0841/Drone)
- [Drone PCB](https://github.com/lbc0841/DronePCB)
- [Drone Controller](https://github.com/lbc0841/DroneController)

## 元件

- STM32: STM32F103TBU6
- 藍牙模塊: E104-BT52

## APP 選單

- Gyroscope Test: <br>
測試陀螺儀濾波

- BLE Connection Test: <br>
測試 BLE 傳輸/接收資料

- Command List: <br>
無

- Drone Test / PID Setup: <br>
無人機測試(馬達 陀螺儀)，PID 調適

- Options: <br>
設置

- About App: APP <br>
版本

## 無人機通訊協議

### 手機 -> 無人機

| Header | Command | 數值 * N | Checksum |
|------|------|----------|------|
| 0xAA | 0x?? | 0x?? * N | 0x?? |

(Checksum = Header xor Command xor Val1 xor Val2 ...)
