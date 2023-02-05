package nwc.hardware.carlift.datas;

public class Data {
    public final static int TYPE_SENSOR_DISPLAY = 0;
    public final static int TYPE_USAGE_COUNTER = 1;
    public final static int TYPE_USAGE_DAY = 2;
    public final static int TYPE_SERVICE_INTERVAL = 3;
    public final static int TYPE_SYNC_INTERVAL_TIME = 4;
    public final static int TYPE_FR_SYNC_INTERVAL = 5;
    public final static int TYPE_2ND_STAGE = 6;
    public final static int TYPE_LR_SYNC_INTERVAL = 7;
    public final static int TYPE_ANGLE_SENSOR = 8;
    public final static int TYPE_PHOTO_SENSOR = 9;
    public final static int TYPE_LOCK_SENSOR = 10;
    public final static int TYPE_SENSOR_LIMIT = 11;
    public final static int TYPE_REMOTE_CONTROL = 12;
    public final static int TYPE_FL_OFFSET_VALUE = 13;
    public final static int TYPE_FR_OFFSET_VALUE = 14;
    public final static int TYPE_RL_OFFSET_VALUE = 15;
    public final static int TYPE_RR_OFFSET_VALUE = 16;
    public final static int TYPE_BOTTOM_SET_VALUE = 17;
    public final static int TYPE_SPOILER_VALUE = 18;

    private int type = -1;
    private float value = 0;
    private float minValue = 0;
    private float maxValue = 0;
    private final float stepValue = 1;

    public Data(int type){
        this.type = type;
        switch (type){
            case TYPE_SENSOR_DISPLAY:
                minValue = 0;
                value = minValue;
                maxValue = 2;
                break;
            case TYPE_SERVICE_INTERVAL:
                minValue = 0;
                value = minValue;
                maxValue = 1;
                break;
            case TYPE_SYNC_INTERVAL_TIME:
                minValue = 1;
                value = minValue;
                maxValue = 9;
                break;
            case TYPE_FR_SYNC_INTERVAL:
                minValue = 1;
                value = minValue;
                maxValue = 9;
                break;
            case TYPE_2ND_STAGE:
                minValue = 3;
                value = minValue;
                maxValue = 7;
                break;
            case TYPE_LR_SYNC_INTERVAL:
                minValue = 1;
                value = minValue;
                maxValue = 9;
                break;
            case TYPE_ANGLE_SENSOR:
                minValue = 0;
                value = minValue;
                maxValue = 1;
                break;
            case TYPE_PHOTO_SENSOR:
                minValue = 0;
                value = minValue;
                maxValue = 1;
                break;
            case TYPE_LOCK_SENSOR:
                minValue = 0;
                value = minValue;
                maxValue = 1;
                break;
            case TYPE_SENSOR_LIMIT:
                minValue = 20;
                value = minValue;
                maxValue = 89;
                break;
            case TYPE_REMOTE_CONTROL:
                minValue = 0;
                value = minValue;
                maxValue = 2;
                break;
            case TYPE_FL_OFFSET_VALUE:
                minValue = 0;
                value = minValue;
                maxValue = 10;
                break;
            case TYPE_FR_OFFSET_VALUE:
                minValue = 0;
                value = minValue;
                maxValue = 10;
                break;
            case TYPE_RL_OFFSET_VALUE:
                minValue = 0;
                value = minValue;
                maxValue = 10;
                break;
            case TYPE_RR_OFFSET_VALUE:
                minValue = 0;
                value = minValue;
                maxValue = 10;
                break;
            case TYPE_BOTTOM_SET_VALUE:
                minValue = 0;
                value = minValue;
                maxValue = 20;
                break;
            case TYPE_SPOILER_VALUE:
                minValue = 1;
                value = minValue;
                maxValue = 10;
                break;
            default:
                this.type = -1;
                break;
        }
    }

    public int getType() {
        return type;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getStepValue() {
        return stepValue;
    }

    public String getTypetoString(int type){
        String title = "unKnown";
        switch (type){
            case TYPE_SENSOR_DISPLAY:
                title = "Sensor Display";
                break;
            case TYPE_SERVICE_INTERVAL:
                title = "Service Interval";
                break;
            case TYPE_SYNC_INTERVAL_TIME:
                title = "Sync Interval Time";
                break;
            case TYPE_FR_SYNC_INTERVAL:
                title = "F-R Sync Interval";
                break;
            case TYPE_2ND_STAGE:
                title = "2nd Stage";
                break;
            case TYPE_LR_SYNC_INTERVAL:
                title = "L-R Sync Interval";
                break;
            case TYPE_ANGLE_SENSOR:
                title = "Angle Sensor";
                break;
            case TYPE_PHOTO_SENSOR:
                title = "Photo Sensor";
                break;
            case TYPE_LOCK_SENSOR:
                title = "Lock Sensor";
                break;
            case TYPE_SENSOR_LIMIT:
                title = "Sensor Limit";
                break;
            case TYPE_REMOTE_CONTROL:
                title = "Remote Control";
                break;
            case TYPE_FL_OFFSET_VALUE:
                title = "F-L Offset Value";
                break;
            case TYPE_FR_OFFSET_VALUE:
                title = "F-R Offset Value";
                break;
            case TYPE_RL_OFFSET_VALUE:
                title = "R-L Offset Value";
                break;
            case TYPE_RR_OFFSET_VALUE:
                title = "R-R Offset Value";
                break;
            case TYPE_BOTTOM_SET_VALUE:
                title = "Bottom Set Value";
                break;
            case TYPE_SPOILER_VALUE:
                title = "Spoiler Value";
                break;
            default:
                break;
        }
        return title;
    }
}
