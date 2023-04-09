package ru.hse;

import ru.hse.check.Check;

public class Main {

    public static int f1(int x) {
        int a = 0 , b = 10;
        if (a < 10) {
            a += 10;
        } else {
            a += 10;
        }
        if (a < 10) {
            a += 10;
        }
        b += 10;
        a *= 100;
        System.out.println(a + b);
        return b + a;
    }

    public static int f2(int y) {
        int a = 0 , b = 10;
        if (a < 10) {
            a += 10;
        } else {
            a += 10;
        }
        b += 10;
        if (a < 10) {
            a += 10;
        }
        a *= 100;
        System.out.println(a + b);
        return b + a;
    }


    public static void main(String[] args) {
        System.out.println(f1(1));
        System.out.println(f2(1));
    }
}