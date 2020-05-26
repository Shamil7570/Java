package ru.geekbrains;

public class Main {

    public static void main(String[] args) {
        System.out.println(calculate(2,3,5,7));
        System.out.println(task10and20(34, 78));
        isPositiveOrNegative(90);
        isNegative(45);
        greetings("John");
        year(87);


        //2 задание
        byte b = 0x00;
        short sh = 128;
        int i = 1124;
        long l = 146457;
        float f = 2345.234f;
        double d = 13.5;
        char ch = 'x';
        boolean boo = true;// write your code here
    }


    //3 задание
    public static int calculate(int a, int b, int c, int d){
        return a * (b + (c / d));
    }


    //4 задание
    public static boolean task10and20(int x1, int x2){
        int sum = x1 + x2;
        if(sum>=10 && sum<=20){
            return  true;
        }
        else{
           return false;
        }
    }

    //5 задание
    public static void isPositiveOrNegative(int a){
        if(a>=0){
            System.out.println("Положительное число");
        } else{
            System.out.println("Отрицательное число");
        }

    }

    //6 задание
    public static boolean isNegative(int a){
        if(a < 0){
            return true;
        }else {
            return false;
        }
    }

    //7 задание
    public static void greetings(String name){
        System.out.println("Привет, " + name);
    }


    //8 задание
    public static void year(int a){
        if (!(a % 4 == 0) || (a % 100 == 0) && !(a % 400 == 0)){
            System.out.println(a + " Год не високосный");
        } else {
            System.out.println(a + " Год високосный");
        }
    }




}
