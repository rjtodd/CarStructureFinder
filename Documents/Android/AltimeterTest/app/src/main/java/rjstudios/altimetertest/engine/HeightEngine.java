package rjstudios.altimetertest.engine;

import java.util.Vector;

import rjstudios.altimetertest.MainActivity;

/**
 * Created by Ronan on 6/26/2017.
 */

public class HeightEngine
{
    //----------------IMPORTANT--------------------//
    //----------EVERYTHING MUST BE IN SI UNITS-----//
    final int BEFORE = 0;
    final int AFTER = 1;
    final float GRAVITY = 9.80665f; //yeah this won't need to be changed much
    final float ACCURACY_CONSTANT = 5f; //see how many data points is needed to get an accurate number
    final float IDEAL_GAS_CONSTANT = 8.314f;
    final float MOLAR_MASS_AIR = 0.029f;
    final float DENSITY_AIR_GENERAL = 1.225f; //AT SEA LEVEL AND 15C
    final float SPECIFIC_HEAT_DRY_AIR = 287.07F; // UNIT: J/KG/K
    final float SPECIIC_HEAT_HUMID = 461.485f; // UNIT: J/KG/K
    float densityAirCalculated; //density of air using the temperature and pressure
    float waterPartialPressure; //polynomial expansion


    //TO-DO
    //Change all these Vectors to Arrays because its only storing two values
    //Or come up with a good reason to keep it
    long[] timeElapsed = new long[2]; //keep track of time between marking and finding the car in milliseconds
    Vector<Float> temperaturePhone = new Vector<>(); //temp collected from device in Celsius
    Vector<Float> temperatureAir = new Vector<>(); //temp collected from JSON openweathermap API
    Vector<Float> pressureAir = new Vector<>(); //atmospheric pressure
    Vector<Float> pressurePhone = new Vector<>(); //pressure from the phone  THIS IS MEASURED IN hPa   1hPa = 100Pa
    Vector<Float> humidity = new Vector<>(); //ratio so there is no unit

    public HeightEngine()
    {
        pressureAir.add(-1f);
        pressureAir.add(-1f);
        pressurePhone.add(-1f);
        pressurePhone.add(-1f);
        temperatureAir.add(-999f);
        temperatureAir.add(-999f);
        temperaturePhone.add(-999f);
        temperaturePhone.add(-999f);
        humidity.add(-1f);
        humidity.add(-1f);
        //this.heightDif = new Vector<>();
    }

    public int getBEFORE(){
        return BEFORE;
    }

    public int getAFTER(){
        return AFTER;
    }

    public long[] getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed, int position) {
        this.timeElapsed[position] = timeElapsed;
    }

    public void setTemperaturePhone(float tempPhone, int position) {
        this.temperaturePhone.set(position, tempPhone);
    }

    public void setTemperatureAir(float tempAir, int position){
        this.temperatureAir.set(position,tempAir);
    }

    public void setPressureAir(float pressureAir, int position) {
        this.pressureAir.set(position, pressureAir);
    }

    public void setHumidity(float hum, int position){
        this.humidity.set(position, hum);
    }

    public float getPressureAir(int position){
        return this.pressureAir.get(position);
    }


    public void setPressurePhone(float pressurePhone, int position) {
        this.pressurePhone.set(position, pressurePhone);
    }

    public float getPhonePressure(int position) {return this.pressurePhone.get(position);}

    public float getTemperature(int position) {return this.temperatureAir.get(position);}

    public float getHumidity(int position) {return this.humidity.get(position);}

    public float calcAirDensity(int position){
        return (getPhonePressure(position)/SPECIFIC_HEAT_DRY_AIR/getTemperature(position)*(1 + getHumidity(position)) /
                (1 + getHumidity(position) * SPECIIC_HEAT_HUMID/SPECIFIC_HEAT_DRY_AIR));
    }


     public float calcHeightPress(float pressurePhoneAfter){
         return ((pressurePhone.get(BEFORE) - pressurePhoneAfter ) / DENSITY_AIR_GENERAL / GRAVITY * MainActivity.PRESSURE_CONVERSION);
     }

    public float calcHeightPressWeather(float pressurePhoneAfter){
        return ((pressurePhone.get(BEFORE) - pressurePhoneAfter - pressureAir.get(BEFORE) + pressureAir.get(AFTER)) / (calcAirDensity(BEFORE) - calcAirDensity(AFTER)) / GRAVITY * MainActivity.PRESSURE_CONVERSION);
    }
}
