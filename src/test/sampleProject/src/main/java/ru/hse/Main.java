package ru.hse;

import ru.hse.check.Check;

public class Main {

    public static void f1(int x) {
        int a = 0, b = 10;
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
    }

    public static void f2() {
        int a = 0, b = 10;
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
    }

    public static void f3(int x) {
        int a = 0, b = 10;
        if (a < 10) {
            a += 10;
        } else {
            if (a < 10) {
                a += 10;
            } else {
                a += 10;
            }
            if (a < 10) {
                a += 10;
            }
            b += 10;
        }
        if (a < 10) {
            a += 10;
        }
        b += 10;
        a *= 100;
        System.out.println(a + b);
    }

    public static void f4() {
        int a = 0, b = 10;
        if (a < 10) {
            a += 10;
        } else {
            if (a < 10) {
                a += 10;
            } else {
                a += 10;
            }
            b += 10;
            if (a < 10) {
                a += 10;
            }
        }
        b += 10;
        if (a < 10) {
            a += 10;
        }
        a *= 100;
        System.out.println(a + b);
    }

    public static void f5(int a) {
        int  b = 10, c = 3;
        if (a < 10) {
            System.out.println(a + b);
        }
        if (a < 10) {
            System.out.println(a + b);
        }
        System.out.println(a + b);
    }

    public static void check1(int x) {
        int a = 0, b = 10;
        if (a < 10) {
            a += 10;
        } else {
            a += 10;
        }
        System.out.println(a);
    }

    public static void check2(int x) {
        int a = 0, b = 10;
        for (int i = 0; i < 10; ++i) {
            System.out.println(a);
        }
        System.out.println(a);
    }


    public static void main(String[] args) {
        f4();
    }
}