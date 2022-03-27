package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //Ввод переменных
        Scanner input = new Scanner(System.in);

        //Ввод числа дней. Можно ввести неправильное значение несколько раз поэтому через while
        boolean f = false;
        int countOfDays = 0;
        while (!f) {
            System.out.print("Введите срок >>> ");
            countOfDays = input.nextInt();
            if (countOfDays>0){
                f = true;
            }else {
                System.out.println("Срок должен быть больше нуля. Повторите ввод:");
            }
        }

        //Ввод начального дня
        f = false;
        int nameOfDay = 0;
        System.out.println("Выберите начальный день");
        while (!f) {
            System.out.print("0 - Понедельник; 1 - Вторник; 2 - Среда; 3 - Четверг; 4-Пятница; " +
                    "5 - Суббота; 6 - Воскресенье >>> ");
            nameOfDay = input.nextInt();
            if ((nameOfDay>=0)&(nameOfDay<=6)){
                f = true;
            }else {
                System.out.println("Введите число от нуля до шести:");
            }
        }

        //Ввод бюджета
        f = false;
        double budget = 0;
        while (!f) {
            System.out.print("Введите бюджет на заданный период >>> ");
            budget = input.nextDouble();
            if (budget>0){
                f = true;
            }else {
                System.out.println("Бюджет должен быть больше нуля. Повторите ввод:");
            }
        }

        //Создание таблицы
        Table newTable = new Table(budget, countOfDays, nameOfDay);
        newTable.createDays();
        newTable.countDayBudget();

        //Вывод результата
        System.out.println("Таблица распределения финансов:");
        System.out.println();
        newTable.printTable();

        //Динамическое изменение бюджета
        newTable.countDays();
    }
}

class Table{
    private double budget;
    private final int countOfDays;
    private float parts;
    private double onePart;
    private final int startDay;
    private double[][] partsTable;
    private double[][] days;
    private static final String[] week = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private boolean exceed = false;

    //Конструкторы (Ввод переменных)
    public Table(double budget, int countOfDays, int startDay){
        this.budget = budget;
        this.countOfDays = countOfDays;
        this.startDay = startDay;
    }

    //Вывод всех переменных
    public void printAll(){
        System.out.println("Срок = " + countOfDays + " дней");
        System.out.println("Частей = " + parts);
        System.out.println("Одна часть = " + onePart);
        tablePrint(partsTable);
        tablePrint(days);
    }

    //Создание таблицы частей
    public void createDays(){
        /*
        Массив частей нужен для увеличения бюджета на определенные дни и для распределения
        остатка между оставшимися днями
         */
        this.partsTable = new double[countOfDays][2];
        for (int i=0; i < countOfDays; i++){
            this.partsTable[i][0] = (i + this.startDay)%7; //Расчет дня недели
            if (this.partsTable[i][0] == 6 | this.partsTable[i][0] == 2) {  // Специальные дни фиксированные - среда
                this.partsTable[i][1] = 1.5; //На 50% больше обычного       // и воскресенье
            } else{
                this.partsTable[i][1] = 1;
            }
        }
        System.out.println();
    }

    //Расчет ежедневного бюджета
    public void countDayBudget(){
        /*
        Расчет бюджета на день происходит путем умножения "веса" одной части на коэфициент (количество частей)
        каждого дня. Рассчет "веса" одной части тоже происходит здесь
         */

        //Нахождение общего количества частей
        this.days = new double[countOfDays][2];
        for (double[] x: partsTable){
            this.parts += x[1];
        }

        this.onePart = this.budget / this.parts; //Расчет одной части

        //Расчет бюджета на каждый день
        for (int i=0; i < countOfDays; i++){
            this.days[i][0] = this.partsTable[i][0];
            this.days[i][1] = this.partsTable[i][1] * this.onePart;
        }
    }
    //Вывод готовой таблицы
    public void printTable(){
        tablePrint(days);
    }

    //Изменение таблицы в зависимости от расходов
    public void countDays(){
        System.out.println("----------------------------------------------------");
        boolean f;
        boolean f1;
        boolean f2;
        Scanner input = new Scanner(System.in);
        for (int i = 0; i < days.length-1;i++) {
            int numberOfDay = (int) days[i][0];

            System.out.format("Сегодня %s. Бюджет %.2f ", week[numberOfDay % 7], days[i][1]);
            System.out.println();
            System.out.print("Введите расход за сегодня >>> ");
            double expenses = input.nextDouble();
            System.out.println();

            //Проверка на превышение бюджета
            budget -= expenses;
            if (budget < 0) {
                System.out.println("Вы превысили бюджет");
                exceed = true;
                break;
            }

            //Распределение остатка
            double variance = days[i][1] - expenses;
            System.out.format("Остаток %.2f. Как его распределить?\n", variance);

            //Условия для первого варианта
            f1 =false;
            if (Math.abs(variance) <= days[i+1][1]){
                System.out.println("1 - оставить на завтра");
                f1 = true;
            }

            //Условия для второго варианта
            f2 = false;
            if (days.length-i > 6 - days[i][0]) {
                if ((Math.abs(variance) <= days[i + (6 - numberOfDay)][1]) & (days[i][0] != 6)){
                    System.out.println("2 - оставить на воскресенье");
                    f2 = true;
                } else if ((days.length-i-1) >= 7) {
                    if  (Math.abs(variance) <= days[i + 7][1]) {
                        System.out.println("2 - оставить на воскресенье");
                        f2 = true;
                    }
                }
            }

            System.out.println("3 - распределить между всеми");

            //Проверка введенного значения
            f = false;
            int variant = 0;
            while (!f) {
                System.out.print(">>> ");
                variant = input.nextInt();
                if (((variant==1)&(f1))|((variant==2)&(f2))|(variant==3)){
                    f = true;
                }else {
                    System.out.println("Выберите один из предложенных вариантов");
                }
            }
            switch (variant) {
                case 1 -> this.leaveForTomorrow(i, variance);
                case 2 -> this.leaveForSunday(i, variance, numberOfDay);
                case 3 -> this.distributeBetweenAll(i, variance, numberOfDay);
            }
            days[i][1] = expenses;

            //Оформление
            System.out.println();
            System.out.println("----------------------------------------------------");
            tablePrint(partsTable);
            System.out.println();
            tablePrint(days);
            System.out.println("----------------------------------------------------");
        }

        //Последний день
        if (!exceed) {
            System.out.format("Сегодня %s. Это последний день. У вас осталось %.2f ", week[(days.length - 1) % 7], days[days.length - 1][1]);
        }
    }

    //Оставить на завтра (1-й вариант)
    void leaveForTomorrow(int i, double variance){
        double buffer;
        buffer = days[i+1][1] + variance;
        partsTable[i+1][1] = buffer/onePart;
        days[i+1][1] = buffer;
    }

    //Оставить на воскресенье (2-й вариант, временное решение)
    void leaveForSunday(int i, double variance, int numberOfDay){
        double buffer;
        buffer = days[i + (6 - numberOfDay)][1] + variance;
        partsTable[i + (6 - numberOfDay)][1] = buffer/onePart;
        days[i + (6 - numberOfDay)][1] = buffer;
    }

    //Распределение между оставшимися днями (3-й вариант)
    void distributeBetweenAll(int i, double variance, int numberOfDay){
        parts = 0;
        for (int x = i+1; x < partsTable.length; x++){
            parts += partsTable[x][1];
        }

        double partOfVariance = variance/parts;

        for (int x = i+1; x < days.length; x++){
            days[x][1] += partOfVariance * partsTable[x][1];
        }
    }

    //Вывод в терминал (временное решение, можно сюда не смотреть)
    void tablePrint(double[][] table){

        System.out.println("| Monday    | Tuesday   | Wednesday | Thursday  | Friday    | Saturday  | Sunday    |");

        int printZero = 6 - (int) table[0][0];
        int zero = (int) table[0][0];
        while (printZero < 6){
            System.out.format("| %10.2f", 0.0);
            printZero ++;
        }

        int k = 0;
        int lastI = 0;
        while (k < table.length) {
            for (int i = k; i < k+7-zero; i++) {
                if (i < table.length) {
                    System.out.format("| %10.2f", table[i][1]);
                    lastI = i;
                }else {
                    System.out.format("| %10.2f", 0.0);
                }
            }
            System.out.println("|");
            k = lastI+1;
            zero = 0;
        }
    }
}