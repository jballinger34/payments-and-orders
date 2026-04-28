package me.jamie.paymentspractice.view;

public interface UserIO {

    void print(String msg);
    double readDouble(String prompt, double min, double max);
    double readDouble(String prompt);
    float readFloat(String prompt, float min, float max);
    float readFloat(String prompt);
    int readInt(String prompt, int min, int max);
    int readInt(String prompt);
    long readLong(String prompt, long min, long max);
    long readLong(String prompt);
    String readString(String prompt);

}
