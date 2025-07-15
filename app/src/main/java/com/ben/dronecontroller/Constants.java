package com.ben.dronecontroller;

import java.util.UUID;

public class Constants {
    public static final String SCAN_PERIOD = "SCAN_PERIOD";
    public static final String HIDE_UNKNOWN_DEVICES = "HIDE_UNKNOWN_DEVICES";

    public static final UUID[] serviceUuid = new UUID[]{
            UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    };

    public static final UUID[] service0CharacteristicUuid = new UUID[]{
            UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002aa6-0000-1000-8000-00805f9b34fb")
    };

    public static final UUID[] service1CharacteristicUuid = new UUID[]{
            UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb")
    };


    public static final UUID[] service2CharacteristicUuid = new UUID[]{
            UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a23-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a2a-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("00002a50-0000-1000-8000-00805f9b34fb"),
    };

    public static final UUID[] service3CharacteristicUuid = new UUID[]{
            UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"), // receive
            UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"), // transmit
            UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb")
    };

    public static final UUID[] writeCharacteristicUuid = new UUID[]{
            UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"),
            UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb")
    };

    public enum ChartDisplayMode{
        CROSSHAIR_CHART,
        LINE_CHART,
        TARGET_ANGLE_CHART
    }

}
