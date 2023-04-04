package ru.hse;

public class Main {

    public static void F() {
        int a = 3, b = 10;

        if (a == 1) {
            a += 1;
            a += 5;
            System.out.println(a);
        } else if (a == 2) {
            if (b < 100) {
                System.out.println("2");
            } else {
                System.out.println(12222222);
            }
            System.out.println(12333);
        } else if (a == 3) {
            System.out.println(123333);
        }else {
            System.out.println(1);
        }
        System.out.println("123");
    }
    public static void FF(int c) {
        int csadifgaosdigf;
        int a = 3, b = 10;
        c  = 10;
        switch (a) {
            case 1:
                System.out.println(a);
                break;
            case 2:
                if (b < 100) {
                    System.out.println("2");
                }else {
                    System.out.println(12222222);
                }
                System.out.println(12333);
                break;
            case 3:
                System.out.println(123333);
                break;
            default:
                System.out.println(1);
        }
        System.out.println("123");
    }


    public static void main(String[] args) {
        int a;
        int  b = 10;
        a = 3;

        if (a == 1) {
            System.out.println(a + b);
        }

        if (a == 1) {
            a = 5;
            a *= 10;
        }

        if (a == 1) {

            System.out.println(a);
        } else if (a == 2) {
            if (b < 100) {
                System.out.println(a * 10 + b);
                b *= 10 + a;
            } else {
                System.out.println(b);
            }
            System.out.println(a + b);
        } else if (a == 3) {
            System.out.println(a * a);
            a += 10;
        }else {
            System.out.println(b * 10);
        }
        a *= 10;
        System.out.println("123");
        FF(123);

        for (int k = 0; k < 100; ++k) {
            if (a == 1) {
                a += 2;
                System.out.println(a + 2);
            }
            b += 100;
            for (int i = 0; i < 1; ++i) {
                if (a > 10) {
                    System.out.println(12333);
                    a += 10;
                } else {
                    System.out.println(122222);
                    b += 100;
                }
                a = a + 10;
                if (a > 10) {
                    System.out.println(12333);
                    continue;
                } else {
                    System.out.println(122222);
                }
                if (a > 10) {
                    System.out.println(12333);
                } else {
                    System.out.println(122222);
                    continue;
                }
                b = 10;
                if (a > 10) {
                    System.out.println(12333);
                    continue;
                }

                if (a > 10) {
                } else {
                    continue;
                }

                System.out.println(123);
                if (a > 10) {
                    System.out.println(12333);
                    continue;
                } else {
                    System.out.println(122222);
                    continue;
                }

            }
        }
        for (int i = 0; i < 10; ++i ) {
            if (a < 10) {
                System.out.println(12333);
                a += 10;
                continue;
            }
            System.out.println(12333);
            b += 10;
        }

        while (a < 100) {
            System.out.println(1233);
        }
    }
}