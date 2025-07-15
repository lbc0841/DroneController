package com.ben.dronecontroller;

import java.util.Arrays;

public class DroneCommandHandler {
    private final BluetoothBinder btBinder;

    public DroneCommandHandler(BluetoothBinder btBinder){
        this.btBinder = btBinder;
    }

    // setMotorSpeed 0x01
    public void setMotorSpeed(int speed_fr, int speed_fl, int speed_br, int speed_bl) {
        byte[] data = new byte[]{
                (byte) 0xAA, // header
                (byte) 0x01, // command
                (byte) speed_fl, // fl
                (byte) speed_fr, // fr
                (byte) speed_bl, // bl
                (byte) speed_br, // br
                0x00 // checksum
        };

        data[data.length-1] = calculateChecksum(Arrays.copyOf(data, data.length-1));

        btBinder.TransmitData(data);
    }

    // setTargetAngle 0x06
    public void setTargetAngle(int pitch, int roll) {

        byte[] data = new byte[]{
                (byte) 0xAA, // Header
                (byte) 0x06, // 命令
                (byte) pitch, // TargetPitch
                (byte) roll, // TargetRoll
                (byte) 0x00 // Checksum
        };

        btBinder.TransmitData(data);
    }

    // setPidGain
    public void setPidGain(int kp, int ki, int kd) {

        byte[] data = new byte[]{
                (byte) 0xAA, // header
                (byte) 0x05, // command
                (byte) kp, // kp
                (byte) ki, // ki
                (byte) kd, // kd
                (byte) 0x00 // checksum
        };

        data[data.length-1] = calculateChecksum(Arrays.copyOf(data, data.length-1));

        btBinder.TransmitData(data);
    }

    // getQmi8658Value 0x02
    public void getQmi8658Value() {
        byte[] data = new byte[]{
                (byte) 0xAA, // header
                (byte) 0x03, // command
                (byte) 0x00  // checksum
        };

        data[data.length-1] = calculateChecksum(Arrays.copyOf(data, data.length-1));

        btBinder.TransmitData(data);
    }

    // getLps22hbValue 0x03
    public void getLps22hbValue() {
        byte[] data = new byte[]{
                (byte) 0xAA, // header
                (byte) 0x03, // command
                (byte) 0x00  // checksum
        };

        data[data.length-1] = calculateChecksum(Arrays.copyOf(data, data.length-1));

        btBinder.TransmitData(data);
    }

    // getBattery 0x04
    public void getBattery() {
        byte[] data = new byte[]{
                (byte) 0xAA, // header
                (byte) 0x04, // command
                (byte) 0x00  // checksum
        };

        data[data.length-1] = calculateChecksum(Arrays.copyOf(data, data.length-1));

        btBinder.TransmitData(data);
    }

    // checksum
    private byte calculateChecksum(byte[] data) {
        byte checksum = data[0];
        for(int i=1; i<data.length; i++) {
            checksum = (byte)(checksum^data[i]);
        }
        return checksum;
    }
}
