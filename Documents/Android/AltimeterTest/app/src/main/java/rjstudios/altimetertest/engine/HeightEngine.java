package rjstudios.altimetertest.engine;

import java.util.Vector;

import rjstudios.altimetertest.MainActivity;
import rjstudios.altimetertest.classes.MapData;

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

    float densityAirCalculated; //density of air using the temperature and pressure
    float waterPartialPressure; //polynomial expansion

    //TO-DO
    //Change all these Vectors to Arrays because its only storing two values
    //Or come up with a good reason to keep it
    float[] temperaturePhone = new float[2]; //temp collected from device in Celsius
    float[] temperatureAir = new float[2]; //temp collected from JSON openweathermap API
    Vector<Float> pressureAir = new Vector<>(); //atmospheric pressure
    Vector<Float> pressurePhone = new Vector<>(); //pressure from the phone  THIS IS MEASURED IN hPa   1hPa = 100Pa


    public HeightEngine()
    {
        pressureAir.add(-1f);
        pressureAir.add(-1f);
        pressurePhone.add(-1f);
        pressurePhone.add(-1f);
        //this.heightDif = new Vector<>();
    }

    public int getBEFORE(){
        return BEFORE;
    }

    public int getAFTER(){
        return AFTER;
    }



    public void setPressureAir(float pressureAir, int position) {
        this.pressureAir.set(position, pressureAir);
    }

    public float getPressureAir(int position){
        return this.pressureAir.get(position);
    }


    public void setPressurePhone(float pressurePhone, int position) {
        this.pressurePhone.set(position, pressurePhone);
    }

    public float getBeforePhonePressure() {return this.pressurePhone.get(BEFORE);}


     public float calcHeightPress(float pressurePhoneAfter){
         return ((pressurePhone.get(BEFORE) - pressurePhoneAfter ) / DENSITY_AIR_GENERAL / GRAVITY * MainActivity.PRESSURE_CONVERSION);
     }

    public float calcHeightPressWeather(float pressurePhoneAfter){
        return ((pressurePhone.get(BEFORE) - pressurePhoneAfter + pressureAir.get(BEFORE) - pressureAir.get(AFTER)) / DENSITY_AIR_GENERAL / GRAVITY * MainActivity.PRESSURE_CONVERSION);
    }
}
