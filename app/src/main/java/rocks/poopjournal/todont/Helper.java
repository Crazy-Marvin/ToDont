package rocks.poopjournal.todont;

import android.content.Context;

import java.util.ArrayList;

public class Helper {
    public static ArrayList<String> labels_array = new ArrayList<String>();
    public static ArrayList<String[]> data=new ArrayList<>();
    public static ArrayList<String[]> avoidedlogdata=new ArrayList<>();
    public static ArrayList<String[]> donedata=new ArrayList<>();
    public static ArrayList<String[]> donelogdata=new ArrayList<>();
    public static ArrayList<String[]> habitsdata=new ArrayList<>();
    public static ArrayList<String> avoidedweeklydata=new ArrayList<String>();
    public static ArrayList<String> avoidedmonthlydata=new ArrayList<String>();
    public static ArrayList<String> donemonthlydata=new ArrayList<String>();
    public static ArrayList<String> doneweeklydata=new ArrayList<String>();
    public static ArrayList<String> doneyearlydata=new ArrayList<String>();
    public static ArrayList<String> avoidedyearlydata=new ArrayList<String>();
    public static int SelectedButtonOfTodayTab=0;
    public static int SelectedButtonOfLogTab=0;
    public static int SelectedButtonOfLogDailyTab=0;
    public static Boolean isTodaySelected=true;
    public static String isnightmodeon="light";
    public static String getSelecteddate="";

}
