import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by ZhangJian on 2016/11/7.
 */
public class AnimalChess {
    private static int currentStep = 0, nextStep, lastStep, undoCount = 0;
    //地形地图
    private static char[][] tileMap = new char[7][9];
    //左方动物数组
    private static char[][] leftAnimalMap = new char[7][9];
    //右方动物数组
    private static char[][] rightAnimalMap = new char[7][9];
    //保存左方动物移动记录
    private static char[][][] copyLeftMap = new char[500][7][9];
    //保存右方动物移动记录
    private static char[][][] copyRightMap = new char[500][7][9];
    //存储必要的返回值
    private static String[] backValue = new String[4];
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("欢迎来到斗兽棋游戏");
        help();
        Scanner input1 = null, input2 = null, input3 = null;
        try {
            input1 = new Scanner(new File("tile.txt"));
            input2 = new Scanner(new File("leftanimal.txt"));
            input3 = new Scanner(new File("rightanimal.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int j = 0; j < 7; j++) {
            String inputLine1 = input1.next();
            String inputLine2 = input2.next();
            String inputLine3 = input3.next();
            tileMap[j] = inputLine1.toCharArray();
            leftAnimalMap[j] = inputLine2.toCharArray();
            rightAnimalMap[j] = inputLine3.toCharArray();
        }
        boolean player = true;
        for (int i = 0; i < leftAnimalMap.length; i++)
            for (int j = 0; j < leftAnimalMap[i].length; j++) {
                copyLeftMap[0][i][j] = leftAnimalMap[i][j];
                copyRightMap[0][i][j] = rightAnimalMap[i][j];
            }
        printMap();
        enter(player);
    }

    /**
    *输入操作
    * 判断玩家是否胜利
     */
    static void enter(boolean player) {
        do {
            String input;
            if (WInOrLose()) {
                System.out.println("\n" + ((player) ? "右方玩家赢了" : "左方玩家赢了"));
                input = scanner.nextLine();
                while (!(input.equals("restart") || input.equals("exit"))) {
                    System.out.println("请输入restart重新开始或者输入exit退出游戏");
                    input = scanner.nextLine();
                }
            } else {
                if (player == true)
                    System.out.println("\n左方行动 ：");
                else System.out.println("\n右方行动 ：");
                input = scanner.nextLine();
            }
            System.out.println(input);
            eatingFunction(input, player);
        } while (true);
    }

    /**
    *Some accessibility features:
    *undo & redo & help & restart & exit
     */
    static boolean assistFunction(String input, boolean player) {
        if (input.equals("help"))
            help();
        else if (input.equals("undo")) {
            if (currentStep >= 1) {
                nextStep = currentStep - 1;
                for (int i = 0; i < leftAnimalMap.length; i++)
                    for (int j = 0; j < leftAnimalMap[i].length; j++) {
                        leftAnimalMap[i][j] = copyLeftMap[nextStep][i][j];
                        rightAnimalMap[i][j] = copyRightMap[nextStep][i][j];
                    }
                player = !player;
                currentStep = nextStep;
                undoCount++;
                printMap();
                enter(player);
            } else System.out.println("已经回到初局，不能再悔棋了！");
        } else if (input.equals("redo")) {
            lastStep = currentStep + undoCount;
            nextStep = currentStep + 1;
            if (nextStep <= lastStep) {
                for (int i = 0; i < leftAnimalMap.length; i++)
                    for (int j = 0; j < leftAnimalMap[i].length; j++) {
                        leftAnimalMap[i][j] = copyLeftMap[nextStep][i][j];
                        rightAnimalMap[i][j] = copyRightMap[nextStep][i][j];
                    }
                player = !player;
                currentStep = nextStep;
                undoCount--;
                printMap();
                enter(player);
            } else System.out.println("已经回到最后的记录,不能再取消悔棋了！");
        } else if (input.equals("restart")) {
            for (int i = 0; i < leftAnimalMap.length; i++)
                for (int j = 0; j < leftAnimalMap[i].length; j++) {
                    leftAnimalMap[i][j] = copyLeftMap[0][i][j];
                    rightAnimalMap[i][j] = copyRightMap[0][i][j];
                }
            printMap();
            for (int m = 0; m < leftAnimalMap.length; m++)
                for (int n = 0; n < leftAnimalMap[m].length; n++) {
                    copyLeftMap[currentStep][m][n] = leftAnimalMap[m][n];
                    copyRightMap[currentStep][m][n] = rightAnimalMap[m][n];
                }
            enter(true);
        } else if (input.equals("exit")) {
            System.out.println(((player) ? "左方" : "右方") + "玩家退出游戏");
            System.exit(0);
        } else return false;
        return true;
    }

    /**
    *Receive instruction and do it.
     */
    static void eatingFunction(String input, boolean player) {
        backValue = new String[]{"a", "a", "a", "a"};
        String[] animal = {"鼠", "猫", "狼", "狗", "豹", "虎", "狮", "象"};
        char[] strToArr = input.toCharArray();
        if (assistFunction(input, player)) {
        } else if (strToArr.length == 2 && Character.isDigit(strToArr[0]) && strToArr[0] - '0' > 0 && strToArr[0] - '0' < 9
                && (strToArr[1] == 'a' | strToArr[1] == 'w' | strToArr[1] == 's' | strToArr[1] == 'd')) {
            int indexX = 0, indexY = 0;
            Move move = new Move();
            if (player) {
                stop:
                for (int i = 0; i < leftAnimalMap.length; i++)
                    for (int j = 0; j < leftAnimalMap[i].length; j++)
                        if (leftAnimalMap[i][j] == strToArr[0]) {
                            indexX = i;
                            indexY = j;
                            break stop;
                        }
                if (leftAnimalMap[indexX][indexY] == strToArr[0]) {
                    switch (strToArr[1]) {
                        case 'a':
                            backValue = checkLeftMoveToLeft(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.leftToLeftOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.leftToLeftFour(indexX, indexY);
                            break;
                        case 'd':
                            backValue = checkLeftMoveToRight(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.leftToRightOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.leftToRightFour(indexX, indexY);
                            break;
                        case 'w':
                            backValue = checkLeftMoveToUp(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.leftToUpOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.leftToUpThree(indexX, indexY);
                            break;
                        case 's':
                            backValue = checkLeftMoveToDown(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.leftToDownOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.leftToDownThree(indexX, indexY);
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println(animal[strToArr[0] - '1'] + "已经为你尽忠了qvq");
                }
            } else {
                stop:
                for (int i = 0; i < leftAnimalMap.length; i++)
                    for (int j = 0; j < leftAnimalMap[i].length; j++)
                        if (rightAnimalMap[i][j] == strToArr[0]) {
                            indexX = i;
                            indexY = j;
                            break stop;
                        }
                if (rightAnimalMap[indexX][indexY] == strToArr[0]) {
                    switch (strToArr[1]) {
                        case 'a':
                            backValue = checkRightMoveToLeft(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.rightToLeftOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.rightToLeftFour(indexX, indexY);
                            break;
                        case 'd':
                            backValue = checkRightMoveToRight(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.rightToRightOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.rightToRightFour(indexX, indexY);
                            break;
                        case 'w':
                            backValue = checkRightMoveToUp(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.rightToUpOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.rightToUpThree(indexX, indexY);
                            break;
                        case 's':
                            backValue = checkRightMoveToDown(player, indexX, indexY);
                            if (backValue[0].equals("1"))
                                move.rightToDownOne(indexX, indexY);
                            if (backValue[1].equals("2"))
                                move.rightToDownThree(indexX, indexY);
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println(animal[strToArr[0] - '1'] + "已经为你尽忠了qvq");
                }
            }
            outPut(animal, strToArr);
            enter(Boolean.parseBoolean(backValue[2]));
        } else {
            System.out.println("不能识别的指令！");
            enter(player);
        }
    }

    /**
    *Explain the function of backValue[3]
     */
    static void outPut(String[] animal, char[] strToArr) {
        switch (backValue[3]) {
            case "3":
                System.out.println("不能越界！");
                break;
            case "4":
                System.out.println(animal[strToArr[0] - '1'] + "不能下水！");
                break;
            case "5":
                System.out.println("水里有敌方老鼠，现在不能过河");
                break;
            case "6":
                System.out.println("过河失败！");
                break;
            case "7":
                System.out.println("你是敌人派来的内奸吗？你确定要干掉己方人员！");
                break;
            case "8":
                System.out.println("不能进入自己家哦");
                break;
            case "9":
                System.out.println("水里的鼠不能吃象哦");
                break;
            case "10":
                System.out.println("勇气可嘉，但你不觉得你吃不了对方吗？");
        }
    }

    /**
    *Judge whether the pieces can move and return a new sting array.
    * Example:
    * {"1","2","true","3"}
     */
    static String[] checkLeftMoveToLeft(boolean player, int i, int j) {
        if (j - 1 < 0) {
            backValue[3] = "3";
        } else if (((i == 2 && j == 1) || (i == 3 && j == 2) || (i == 4 && j == 1)) && rightAnimalMap[i][j - 1] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (rightAnimalMap[i][j - 1] <= leftAnimalMap[i][j]) {
            if (tileMap[i][j - 1] == '1' && leftAnimalMap[i][j] != '1') {
                if (leftAnimalMap[i][j] == '2' || leftAnimalMap[i][j] == '3' || leftAnimalMap[i][j] == '4' || leftAnimalMap[i][j] == '5' || leftAnimalMap[i][j] == '8') {
                } else if (leftAnimalMap[i][j] == '6' || leftAnimalMap[i][j] == '7') {
                    if (rightAnimalMap[i][j - 1] == '1' || rightAnimalMap[i][j - 2] == '1' || rightAnimalMap[i][j - 3] == '1') {
                        backValue[3] = "4";
                    } else if (rightAnimalMap[i][j - 4] <= leftAnimalMap[i][j] && leftAnimalMap[i][j - 4] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "5";
                }
            } else if (rightAnimalMap[i][j - 1] == '1' && leftAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (leftAnimalMap[i][j - 1] != '0') {
                backValue[3] = "7";
            } else if (i == 3 && j == 1) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (rightAnimalMap[i][j - 1] == '8' && leftAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkRightMoveToLeft(boolean player, int i, int j) {
        if (j - 1 < 0) {
            backValue[3] = "3";
        } else if (leftAnimalMap[i][j - 1] <= rightAnimalMap[i][j]) {
            if (tileMap[i][j - 1] == '1' && rightAnimalMap[i][j] != '1') {
                if (rightAnimalMap[i][j] != '6' && rightAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (rightAnimalMap[i][j] == '6' || rightAnimalMap[i][j] == '7') {
                    if (leftAnimalMap[i][j - 1] == '1' || leftAnimalMap[i][j - 2] == '1' || leftAnimalMap[i][j - 3] == '1') {
                        backValue[3] = "5";
                    } else if (leftAnimalMap[i][j - 4] <= rightAnimalMap[i][j] && rightAnimalMap[i][j - 4] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (leftAnimalMap[i][j - 1] == '1' && rightAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (rightAnimalMap[i][j - 1] != '0') {
                backValue[3] = "7";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (leftAnimalMap[i][j - 1] == '8' && rightAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkLeftMoveToRight(boolean player, int i, int j) {
        if (j + 1 > 8) {
            backValue[3] = "3";
        } else if (rightAnimalMap[i][j + 1] <= leftAnimalMap[i][j]) {
            if (tileMap[i][j + 1] == '1' && leftAnimalMap[i][j] != '1') {
                if (leftAnimalMap[i][j] != '6' && leftAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (leftAnimalMap[i][j] == '6' || leftAnimalMap[i][j] == '7') {
                    if (rightAnimalMap[i][j + 1] == '1' || rightAnimalMap[i][j + 2] == '1' || rightAnimalMap[i][j + 3] == '1') {
                        backValue[3] = "5";
                    } else if (rightAnimalMap[i][j + 4] <= leftAnimalMap[i][j] && leftAnimalMap[i][j + 4] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (rightAnimalMap[i][j + 1] == '1' && leftAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (leftAnimalMap[i][j + 1] != '0') {
                backValue[3] = "7";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (rightAnimalMap[i][j + 1] == '8' && leftAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkRightMoveToRight(boolean player, int i, int j) {
        if (j + 1 > 8) {
            backValue[3] = "3";
        } else if (((i == 2 && j == 7) || (i == 3 && j == 6) || (i == 4 && j == 7)) && leftAnimalMap[i][j + 1] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (leftAnimalMap[i][j + 1] <= rightAnimalMap[i][j]) {
            if (tileMap[i][j + 1] == '1' && rightAnimalMap[i][j] != '1') {
                if (rightAnimalMap[i][j] != '6' && rightAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (rightAnimalMap[i][j] == '6' || rightAnimalMap[i][j] == '7') {
                    if (leftAnimalMap[i][j + 1] == '1' || leftAnimalMap[i][j + 2] == '1' || leftAnimalMap[i][j + 3] == '1') {
                        backValue[3] = "5";
                    } else if (leftAnimalMap[i][j + 4] <= rightAnimalMap[i][j] && rightAnimalMap[i][j + 4] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (leftAnimalMap[i][j + 1] == '1' && rightAnimalMap[i][j] == '8') {
                backValue[3] = "103";
            } else if (leftAnimalMap[i][j + 1] != '0') {
                backValue[3] = "7";
            } else if (i == 3 && j == 7) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (leftAnimalMap[i][j + 1] == '8' && rightAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
                toCountPrintSave();
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkLeftMoveToUp(boolean player, int i, int j) {
        if (i - 1 < 0) {
            backValue[3] = "3";
        } else if (((i == 5 && j == 0) || (i == 4 && j == 1)) && rightAnimalMap[i - 1][j] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (rightAnimalMap[i - 1][j] <= leftAnimalMap[i][j]) {
            if (tileMap[i - 1][j] == '1' && leftAnimalMap[i][j] != '1') {
                if (leftAnimalMap[i][j] != '7' && leftAnimalMap[i][j] != '6') {
                    backValue[3] = "4";
                } else if (leftAnimalMap[i][j] == '6' || leftAnimalMap[i][j] == '7') {
                    if (rightAnimalMap[i - 1][j] == '1' || rightAnimalMap[i - 2][j] == '1') {
                        backValue[3] = "5";
                    } else if (rightAnimalMap[i - 3][j] <= leftAnimalMap[i][j] && leftAnimalMap[i - 3][j] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (rightAnimalMap[i - 1][j] == '1' && leftAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (leftAnimalMap[i - 1][j] != '0') {
                backValue[3] = "7";
            } else if (i == 4 && j == 0) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (rightAnimalMap[i - 1][j] == '8' && leftAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkRightMoveToUp(boolean player, int i, int j) {
        if (i - 1 < 0) {
            backValue[3] = "3";
        } else if (((i == 5 && j == 8) || (i == 4 && j == 7)) && leftAnimalMap[i - 1][j] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (leftAnimalMap[i - 1][j] <= rightAnimalMap[i][j]) {
            if (tileMap[i - 1][j] == '1' && rightAnimalMap[i][j] != '1') {
                if (rightAnimalMap[i][j] != '6' && rightAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (rightAnimalMap[i][j] == '6' || rightAnimalMap[i][j] == '7') {
                    if (leftAnimalMap[i - 1][j] == '1' || leftAnimalMap[i - 2][j] == '1') {
                        backValue[3] = "5";
                    } else if (leftAnimalMap[i - 3][j] <= rightAnimalMap[i][j] && rightAnimalMap[i - 3][j] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (leftAnimalMap[i - 1][j] == '1' && rightAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (rightAnimalMap[i - 1][j] != '0') {
                backValue[3] = "7";
            } else if (i == 4 && j == 8) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (leftAnimalMap[i - 1][j] == '8' && rightAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkLeftMoveToDown(boolean player, int i, int j) {
        if (i + 1 > 6) {
            backValue[3] = "3";
        } else if (((i == 1 && j == 0) || (i == 2 && j == 1)) && rightAnimalMap[i + 1][j] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (rightAnimalMap[i + 1][j] <= leftAnimalMap[i][j]) {
            if (tileMap[i + 1][j] == '1' && leftAnimalMap[i][j] != '1') {
                if (leftAnimalMap[i][j] != '6' && leftAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (leftAnimalMap[i][j] == '6' || leftAnimalMap[i][j] == '7') {
                    if (rightAnimalMap[i + 1][j] == '1' || rightAnimalMap[i + 2][j] == '1') {
                        backValue[3] = "5";
                    } else if (rightAnimalMap[i + 3][j] <= leftAnimalMap[i][j] && leftAnimalMap[i + 3][j] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (rightAnimalMap[i + 1][j] == '1' && leftAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (leftAnimalMap[i + 1][j] != '0') {
                backValue[3] = "7";
            } else if (i == 2 && j == 0) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (rightAnimalMap[i + 1][j] == '8' && leftAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    static String[] checkRightMoveToDown(boolean player, int i, int j) {
        if (i + 1 > 6) {
            backValue[3] = "3";
        } else if (((i == 1 && j == 8) || (i == 2 && j == 7)) && leftAnimalMap[i + 1][j] != '0') {
            player = !player;
            backValue[0] = "1";
        } else if (leftAnimalMap[i + 1][j] <= rightAnimalMap[i][j]) {
            if (tileMap[i + 1][j] == '1' && rightAnimalMap[i][j] != '1') {
                if (rightAnimalMap[i][j] != '6' && rightAnimalMap[i][j] != '7') {
                    backValue[3] = "4";
                } else if (rightAnimalMap[i][j] == '6' || rightAnimalMap[i][j] == '7') {
                    if (leftAnimalMap[i + 1][j] == '1' || leftAnimalMap[i + 2][j] == '1') {
                        backValue[3] = "5";
                    } else if (leftAnimalMap[i + 3][j] <= rightAnimalMap[i][j] && rightAnimalMap[i + 3][j] == '0') {
                        player = !player;
                        backValue[1] = "2";
                    } else backValue[3] = "6";
                }
            } else if (leftAnimalMap[i + 1][j] == '1' && rightAnimalMap[i][j] == '8') {
                backValue[3] = "10";
            } else if (rightAnimalMap[i + 1][j] != '0') {
                backValue[3] = "7";
            } else if (i == 2 && j == 8) {
                backValue[3] = "8";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else if (leftAnimalMap[i + 1][j] == '8' && rightAnimalMap[i][j] == '1') {
            if (tileMap[i][j] == '1') {
                backValue[3] = "9";
            } else {
                player = !player;
                backValue[0] = "1";
            }
        } else {
            backValue[3] = "10";
        }
        backValue[2] = "" + player + "";
        return backValue;
    }

    /**
    *Check if one of them won this round.
     */
    static boolean WInOrLose() {
        char[][] Zero = new char[7][9];
        int mm = 0;
        int nn = 0;
        int xx = 0;
        int yy = 0;
        for (int i = 0; i < leftAnimalMap.length; i++) {
            for (int j = 0; j < leftAnimalMap[0].length; j++) {
                if (leftAnimalMap[i][j] != '0') {
                    nn++;
                    if (!Boolean.parseBoolean(checkLeftMoveToDown(true, i, j)[2])
                            | !Boolean.parseBoolean(checkLeftMoveToLeft(true, i, j)[2])
                            | !Boolean.parseBoolean(checkLeftMoveToRight(true, i, j)[2])
                            | !Boolean.parseBoolean(checkLeftMoveToUp(true, i, j)[2])) {
                    } else
                        mm++;
                }
                if (rightAnimalMap[i][j] != '0') {
                    yy++;
                    if (!Boolean.parseBoolean(checkRightMoveToDown(true, i, j)[2])
                            | !Boolean.parseBoolean(checkRightMoveToLeft(true, i, j)[2])
                            | !Boolean.parseBoolean(checkRightMoveToRight(true, i, j)[2])
                            | !Boolean.parseBoolean(checkRightMoveToUp(true, i, j)[2])) {
                    } else
                        xx++;
                }
            }
        }
        if (Zero == leftAnimalMap || Zero == rightAnimalMap || leftAnimalMap[3][8] != '0' || rightAnimalMap[3][0] != '0' || mm == nn || xx == yy) {
            return true;
        } else return false;

    }
    /**
    *Copy current maps.
     */

    static void save() {
        for (int m = 0; m < leftAnimalMap.length; m++)
            for (int n = 0; n < leftAnimalMap[m].length; n++) {
                copyLeftMap[currentStep][m][n] = leftAnimalMap[m][n];
                copyRightMap[currentStep][m][n] = rightAnimalMap[m][n];
            }
    }

    /**
    *Print maps.
     */
    static void printMap() {
        Scanner input1 = null;
        try {
            input1 = new Scanner(new File("tile.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 7; i++) {
            String inputLine1 = input1.next();
            System.out.print("\n");
            for (int j = 0; j < inputLine1.length(); j++) {
                char a1 = inputLine1.charAt(j);
                char b1 = leftAnimalMap[i][j];
                char c1 = rightAnimalMap[i][j];
                if (a1 != '0') {
                    switch (a1) {
                        case '1':
                            if (c1 == '1')
                                System.out.print("鼠1 ");
                            else if (b1 == '1')
                                System.out.print(" 1鼠");
                            else
                                System.out.print(" 水 ");
                            break;
                        case '2':
                            if (b1 == '0' && c1 == '0')
                                System.out.print("1陷 ");
                            else choosePrintMap(b1, c1);
                            break;
                        case '3':
                            if (b1 == '0' && c1 == '0')
                                System.out.print("1穴 ");
                            else choosePrintMap(b1, c1);
                            break;
                        case '4':
                            if (b1 == '0' && c1 == '0')
                                System.out.print(" 陷2");
                            else choosePrintMap(b1, c1);
                            break;
                        case '5':
                            if (b1 == '0' && c1 == '0')
                                System.out.print(" 穴2");
                            else choosePrintMap(b1, c1);
                            break;
                        default:
                            System.out.println();
                    }
                } else if (a1 == '0') {
                    choosePrintMap(b1, c1);
                }
            }
        }
    }

    static void choosePrintMap(char b1, char c1) {
        if (b1 == '0' && c1 == '0') {
            System.out.print(" 　 ");
        } else if (c1 == '1') {
            System.out.print(" 鼠1");
        } else if (c1 == '2') {
            System.out.print(" 猫2");
        } else if (c1 == '3') {
            System.out.print(" 狼3");
        } else if (c1 == '4') {
            System.out.print(" 狗4");
        } else if (c1 == '5') {
            System.out.print(" 豹5");
        } else if (c1 == '6') {
            System.out.print(" 虎6");
        } else if (c1 == '7') {
            System.out.print(" 狮7");
        } else if (c1 == '8') {
            System.out.print(" 象8");
        } else if (b1 == '1') {
            System.out.print("1鼠 ");
        } else if (b1 == '2') {
            System.out.print("2猫 ");
        } else if (b1 == '3') {
            System.out.print("3狼 ");
        } else if (b1 == '4') {
            System.out.print("4狗 ");
        } else if (b1 == '5') {
            System.out.print("5豹 ");
        } else if (b1 == '6') {
            System.out.print("6虎 ");
        } else if (b1 == '7') {
            System.out.print("7狮 ");
        } else if (b1 == '8') {
            System.out.print("8象 ");
        }
    }

    static void help() {
        System.out.println("指令介绍：\n\n" +
                "1.移动指令\n" +
                "\t\t移动指令由两部分组成。\n" +
                "\t\t第一部分是1-8，根据战斗力分别对应鼠（1），猫（2），狗（3），狼（4），豹（5），虎（6），狮（7），象（8）\n" +
                "\t\t第二部分是w a s d中的一个，w代表向上，a代表向左，s代表向下，d代表向右\n" +
                "\t\t比如指令“1d”表示鼠向右走一格\n" +
                "2.游戏指令\n" +
                "\t\t输入 restart 重新开始游戏\n" +
                "\t\t输入 help 查看帮助\n" +
                "\t\t输入 undo 悔棋\n" +
                "\t\t输入 redo 取消悔棋\n" +
                "\t\t输入 exit 退出游戏");

    }

    static void toCountPrintSave() {
        currentStep++;
        undoCount = 0;
        save();
        printMap();
    }

    /**
    *The class explains the above instructions
    * and changes the current status of pieces.
     */
    static class Move {
        Move() {
        }

        void leftToLeftOne(int i, int j) {
            leftAnimalMap[i][j - 1] = leftAnimalMap[i][j];
            rightAnimalMap[i][j - 1] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToLeftFour(int i, int j) {
            leftAnimalMap[i][j - 4] = leftAnimalMap[i][j];
            rightAnimalMap[i][j - 4] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToLeftOne(int i, int j) {
            rightAnimalMap[i][j - 1] = rightAnimalMap[i][j];
            leftAnimalMap[i][j - 1] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToLeftFour(int i, int j) {
            rightAnimalMap[i][j - 4] = rightAnimalMap[i][j];
            leftAnimalMap[i][j - 4] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToRightOne(int i, int j) {
            leftAnimalMap[i][j + 1] = leftAnimalMap[i][j];
            rightAnimalMap[i][j + 1] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToRightFour(int i, int j) {
            leftAnimalMap[i][j + 4] = leftAnimalMap[i][j];
            rightAnimalMap[i][j + 4] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToRightOne(int i, int j) {
            rightAnimalMap[i][j + 1] = rightAnimalMap[i][j];
            leftAnimalMap[i][j + 1] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToRightFour(int i, int j) {
            rightAnimalMap[i][j + 4] = rightAnimalMap[i][j];
            leftAnimalMap[i][j + 4] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToUpOne(int i, int j) {
            leftAnimalMap[i - 1][j] = leftAnimalMap[i][j];
            rightAnimalMap[i - 1][j] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToUpThree(int i, int j) {
            leftAnimalMap[i - 3][j] = leftAnimalMap[i][j];
            rightAnimalMap[i - 3][j] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToUpOne(int i, int j) {
            rightAnimalMap[i - 1][j] = rightAnimalMap[i][j];
            leftAnimalMap[i - 1][j] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToUpThree(int i, int j) {
            rightAnimalMap[i - 3][j] = rightAnimalMap[i][j];
            leftAnimalMap[i - 3][j] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToDownOne(int i, int j) {
            leftAnimalMap[i + 1][j] = leftAnimalMap[i][j];
            rightAnimalMap[i + 1][j] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void leftToDownThree(int i, int j) {
            leftAnimalMap[i + 3][j] = leftAnimalMap[i][j];
            rightAnimalMap[i + 3][j] = '0';
            leftAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToDownOne(int i, int j) {
            rightAnimalMap[i + 1][j] = rightAnimalMap[i][j];
            leftAnimalMap[i + 1][j] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }

        void rightToDownThree(int i, int j) {
            rightAnimalMap[i + 3][j] = rightAnimalMap[i][j];
            leftAnimalMap[i + 3][j] = '0';
            rightAnimalMap[i][j] = '0';
            toCountPrintSave();
        }
    }
}
