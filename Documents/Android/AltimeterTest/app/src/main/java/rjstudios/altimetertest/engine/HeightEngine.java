package rjstudios.altimetertest.engine;

import java.util.Vector;

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

    Vector<Float> temperaturePhone; //temp collected from device in Celsius
    Vector<Float> temperatureAir; //temp collected from JSON openweathermap API
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


    public Vector<Float> getPressureAir() {
        return pressureAir;
    }

    public void setPressureAir(int position, float pressureAir) {
        this.pressureAir.set(position, pressureAir);
    }

    public Vector<Float> getPressurePhone() {
        return pressurePhone;
    }

    public void setPressurePhone(float pressurePhone, int position) {
        this.pressurePhone.set(position, pressurePhone);
    }

    public float getBeforePhonePressure() {return this.pressurePhone.get(BEFORE);}

    public float getAfterPhonePressure() {return this.pressurePhone.get(AFTER);}


    private Vector<Float> heightDif;
    private double aveHeight;

    //maybe make an array? filter through multiple data samples for accuracy?
    //discard the data points that are destroying the average

    public void calcAirDens(float temperature)
    {

    }

    public float calcHeightTemp()
    {
        return IDEAL_GAS_CONSTANT * (temperaturePhone.get(BEFORE) - temperaturePhone.get(AFTER))/ MOLAR_MASS_AIR / GRAVITY;
    }
    public float calcHeightPress(){
        return (pressurePhone.get(BEFORE) - pressurePhone.get(AFTER)) / DENSITY_AIR_GENERAL / GRAVITY;
    }
     public float calcHeightPress(float press){
         return ((pressurePhone.get(BEFORE) - press /*+ pressureAir.get(BEFORE) - pressureAir.get(AFTER)*/) / DENSITY_AIR_GENERAL / GRAVITY * 100);
     }

    public double getAveHeight()
    {
        return aveHeight;
    }
    public String toString()
    {
        String text = "";
        text += "There are " + this.heightDif.size() + " of items in the list\n";
        for(int i = 0; i < this.heightDif.size(); i++)
        {
            text += (i + 1) + "= " + this.heightDif.get(i) + '\n';
        }
        text += "The average is: " + aveHeight;
        return text;
    }


}
