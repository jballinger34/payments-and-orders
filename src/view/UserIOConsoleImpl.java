package view;

import java.util.Scanner;

public class UserIOConsoleImpl implements UserIO {

    private static final Scanner s = new Scanner(System.in);

    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

    // want a helper method readValue that does the loop and try/catch logic
    // uses a parser (pass function as argument?) to determine how to pass str
    // then each method will be simple, i.e readLong just calls readValue


    @Override
    public String readString(String prompt){
        print(prompt);
        return s.nextLine();
    }

    @Override
    public double readDouble(String prompt, double min, double max) {
        boolean isValid = false;
        double result = 0;
        while(!isValid){
            result = readDouble(prompt);
            if(result >= min && result <= max){
                isValid = true;
            } else {
                print("Double not in range (" + min + "," + max + ")");
            }
        }
        return result;
    }

    @Override
    public double readDouble(String prompt){
        while(true){
            try{
                String userStr = readString(prompt);
                return Double.parseDouble(userStr);
            } catch (NumberFormatException e){
                print("Invalid Double");
            }
        }
    }

    @Override
    public float readFloat(String prompt, float min, float max) {
        boolean isValid = false;
        float result = 0;
        while(!isValid){
            result = readFloat(prompt);
            if(result >= min && result <= max){
                isValid = true;
            } else {
                print("Float not in range (" + min + "," + max + ")");
            }
        }
        return result;
    }

    @Override
    public float readFloat(String prompt) {
        while(true){
            try {
                String userStr = readString(prompt);
                return Float.parseFloat(userStr);
            } catch (NumberFormatException e){
                print("Invalid Float");
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        boolean isValid = false;
        int result = 0;
        while(!isValid){
            result = readInt(prompt);
            if(result >= min && result <= max){
                isValid = true;
            } else {
                print("Int not in range (" + min + "," + max + ")");
            }
        }
        return result;
    }

    @Override
    public int readInt(String prompt) {
        while(true){
            try {
                String userStr = readString(prompt);
                return Integer.parseInt(userStr) ;
            } catch (NumberFormatException e){
                print("Invalid Int");
            }
        }
    }

    @Override
    public long readLong(String prompt, long min, long max) {
        boolean isValid = false;
        long result = 0;
        while(!isValid){
            result = readLong(prompt);
            if(result >= min && result <= max){
                isValid = true;
            } else {
                print("Long not in range (" + min + "," + max + ")");
            }
        }
        return result;
    }

    @Override
    public long readLong(String prompt) {
        while(true){
            try {
                String userStr = readString(prompt);
                return Long.parseLong(userStr) ;
            } catch (NumberFormatException e){
                print("Invalid Long");
            }
        }
    }

}


