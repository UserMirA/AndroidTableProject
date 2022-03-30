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
        int selectedDay = 0;
        Scanner input = new Scanner(System.in);

        for (int i = 0; i < days.length-1;i++) {
            int numberOfDay = (int) days[i][0];

            //Интерфейс
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

            /*
            У каждого варианта есть условия, которые должны быть удовлетворены для того чтобы вариант действий
            был предложен пользователю
             */

            //Условия для первого варианта
            f1 =false;
            if (-1*variance <= days[i+1][1]){ //Если расходы меньше бюджета на завтра
                System.out.println("1 - оставить на завтра");
                f1 = true;
            }

            //Условия для второго варианта
            int [] possibleDays = new int[7];
            f2 = false;
            int k = 0;
            for (int j = numberOfDay+1; j <= numberOfDay+7; j++){
                if(j<startDay + countOfDays){ //если j не выходит за рамки массива days
                    if (days[j-startDay][1] >= -1*variance){
                        possibleDays[k] = j;
                        k++;
                    }
                }
            }

            if (k != 0){
                System.out.println("2 - перенести на другой день");
                f2 = true;
            }

            int [] possibleDaysOut = new int[k];
            System.arraycopy(possibleDays, 0, possibleDaysOut, 0, k);

            System.out.println("3 - распределить между всеми");

            //Проверка введенного значения
            f = false;
            int variant = 0;

            while (!f) {
                System.out.print(">>> ");
                variant = input.nextInt();
                if (((variant==1)&(f1))|(variant==3)){
                    f = true; //Если введен корректный вариант цикл прерывается
                }else if ((variant==2)&(f2)) {
                    System.out.println("Возможные дни для переноса: ");
                    int num = 1;
                    for (int j: possibleDaysOut){
                        System.out.format("%d - %s  ", num, week[j % 7]);
                        num++;
                    }
                    System.out.println();

                    System.out.print(">>> ");
                    selectedDay = input.nextInt();

                    if((selectedDay>k)|(selectedDay<=0)){
                        System.out.println("Выбран некорректный день");
                        System.out.println("Выберите один из предложенных вариантов");
                        if(f1){
                            System.out.println("1 - оставить на завтра");
                        }
                        System.out.println("2 - перенести на другой день");
                        System.out.println("3 - распределить между всеми");
                    }else {
                        f = true;
                    }
                }else {
                    System.out.println("Выберите один из предложенных вариантов");
                    if(f1){
                        System.out.println("1 - оставить на завтра");
                    }
                    if(f2){
                        System.out.println("2 - перенести на другой день");
                    }
                    System.out.println("3 - распределить между всеми");
                }
            }

            //Изменение таблицы согласно выбранному варианту действий
            switch (variant) {
                case 1 -> this.putOffToTomorrow(i, variance);
                case 2 -> this.putOffToSomeDay(variance, possibleDaysOut[selectedDay-1] );
                case 3 -> this.distributeBetweenAll(i, variance);
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
        if (!exceed) {  //Если не было превышения бюджета
            System.out.format("Сегодня %s. Это последний день. У вас осталось %.2f ",
                    week[(days.length - 1) % 7], days[days.length - 1][1]);
        }
    }

    //Оставить на завтра (1-й вариант)
    void putOffToTomorrow(int i, double variance){
        days[i+1][1] += variance;
        partsTable[i+1][1] = days[i+1][1]/onePart;
    }

    //Оставить на воскресенье (2-й вариант)
    void putOffToSomeDay(double variance, int selectedDay){
        days[selectedDay-startDay][1] += variance;
        partsTable[selectedDay-startDay][1] = days[selectedDay-startDay][1]/onePart;  //Изменение таблицы частей
    }

    //Распределение между оставшимися днями (3-й вариант)
    void distributeBetweenAll(int i, double variance){
        /*
        Чтобы рапределить остаток между всеми я нахожу остаток приходящийся на одну часть, вычисляю для каждого
        дня и прибавляю к бюджету дня
         */

        //Нахождение количества частей всех дней, кроме уже прошедших
        parts = 0;
        for (int x = i+1; x < partsTable.length; x++){
            parts += partsTable[x][1];
        }

        double partOfVariance = variance/parts; //Вес одной части остатка

        //Изменение бюджетов дней
        for (int x = i+1; x < days.length; x++){
            days[x][1] += partOfVariance * partsTable[x][1];
        }
    }

    //Вывод в терминал
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