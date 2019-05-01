package UIConsole;
import GameObjects.Player;
import GameObjects.Territory;
import java.util.Map;


public class Table {
    private int rows, columns;
    private int rowLength, columnLength;
    private int style; // 0 = Many Tables, 1 = One Table
    private Map<Integer, Territory> territoriesMap;
    private Player player1, player2;
    private final int COLUMN_LENGTH = 8;
    private final int ROWS_LENGTH = 4;
    private final String EXTENDED_ASCII_HORIZONTAL_BAR = "─";
    private final String EXTENDED_ASCII_VERTICL_BAR = "│";
    private final String EXTENDED_ASCII_TOP_LEFT = "┌";
    private final String EXTENDED_ASCII_TOP_RIGHT = "┐";
    private final String EXTENDED_ASCII_BOTTOM_LEFT = "└";
    private final String EXTENDED_ASCII_BOTTOM_RIGHT = "┘";
    private final char EXTENDED_ASCII_MIDDLE_CENTER = '┼';
    private final char EXTENDED_ASCII_MIDDLE_RIGHT = '┤';
    private final char EXTENDED_ASCII_MIDDLE_LEFT = '├';
    private final char EXTENDED_ASCII_BOTTOM_CENTER = '┴';
    private final char EXTENDED_ASCII_TOP_CENTER = '┬';
    private final String PLAYER_1_SIGN = "X";
    private final String PLAYER_2_SIGN = "O";

    Table(Map<Integer, Territory> territoriesMap, int rows, int columns, Player player1, Player player2) {
        this.style =0;
        this.player1 = player1;
        this.player2 = player2;
        this.territoriesMap = territoriesMap;
        this.rows = rows;
        this.columns = columns;
        rowLength = ROWS_LENGTH;
        columnLength = COLUMN_LENGTH;
    }

    /* prints chosen style */
    public void print()
    {
        if(style ==0)
            printManyTableStyle();
        else printOneTableStyle();

    }
    public void setTableDefaultStyle(int style)
    {
        this.style = style;
    }
    /* prints many tables data style */
    private void printManyTableStyle() {
        for (int i = 0; i < rows; i++) { // prints rows of tables
            printTOPBorder();
            for (int z = 0; z < rowLength * columns; z++) { // prints all details
                printRowBoard();
                printIntoTableWithRightPadding(i,z);
                printRowBoard();
                if ((z + 1) % columns == 0)
                    System.out.println();
            }
            printBOTTOMBorder();
        }
    }

    private void printIntoTableWithRightPadding(int i,int z)
    {
        int padRight = getPadRightLength((i * columns + ((z) % columns)) + 1, (z) / columns + 1);
        printCurrentTerritoryFields((i * columns + ((z) % columns)) + 1, (z) / columns + 1);
        for (int w = padRight; w < columnLength; w++) {
            System.out.print(' '); // prints row in single table
        }
    }

    private int getPadRightLength(int territoryID, int territoryFieldID) {
        Territory currentPrintedObject = territoriesMap.get(territoryID);
        if (territoryFieldID == 1) return Integer.toString(currentPrintedObject.getID()).length();
        if (territoryFieldID == 2) return Integer.toString(currentPrintedObject.getProfit()).length();
        if (territoryFieldID == 3) return 1;
        if (territoryFieldID == 4) return Integer.toString(currentPrintedObject.getArmyThreshold()).length();
        else return 0;
    }

    private void printCurrentTerritoryFields(int territoryID, int territoryFieldID) {
        Territory currentPrintedObject = territoriesMap.get(territoryID);
        if (territoryFieldID == 1) System.out.print(currentPrintedObject.getID());
        if (territoryFieldID == 2) System.out.print(currentPrintedObject.getProfit());
        if (territoryFieldID == 3) {
            if (currentPrintedObject.getConquer() == null) System.out.print('N');
            else {
                if (currentPrintedObject.getConquer().equals(player1))
                    System.out.print(PLAYER_1_SIGN);
                else System.out.print(PLAYER_2_SIGN);
            }
        }
        if (territoryFieldID == 4) System.out.print(currentPrintedObject.getArmyThreshold());
    }

    private void printTOPBorder() {
        printColumnBoard(EXTENDED_ASCII_TOP_LEFT, EXTENDED_ASCII_TOP_RIGHT);
    }

    private void printBOTTOMBorder() {
        printColumnBoard(EXTENDED_ASCII_BOTTOM_LEFT, EXTENDED_ASCII_BOTTOM_RIGHT);
    }

    private void printRowBoard() {
        System.out.print(EXTENDED_ASCII_VERTICL_BAR);
    }

    private void printColumnBoard(String extended_ascii_top_left, String extended_ascii_top_right) {
        for (int i = 0; i < columns; i++) {
            System.out.print(extended_ascii_top_left);
            for (int j = 0; j < columnLength; j++) {
                System.out.print(EXTENDED_ASCII_HORIZONTAL_BAR);
            }
            System.out.print(extended_ascii_top_right);
        }
        System.out.println();
    }

    /* prints One Table Data Style  */
    private void printOneTableStyle() {
        for (int i = 0; i < rows; i++) { // prints rows of tables
            if(i==0)
                printOneTableStyleColumnBoard(i);
            for (int z = 0; z < rowLength * columns; z++) { // prints all details
                printRowBoard();
                printIntoTableWithRightPadding(i,z);
                if ((z + 1) % columns == 0) {
                    printRowBoard();
                    System.out.println();
                }
            }
            printOneTableStyleColumnBoard(i + 1);
        }
    }
    private void printOneTableStyleColumnBoard(int rows) {
        for (int i = 0; i < columns; i++) {
            printOneTableStyleFrame(rows, i);
            for (int j = 0; j < columnLength; j++) {
                System.out.print(EXTENDED_ASCII_HORIZONTAL_BAR);
            }
        }
        printOneTableStyleFrame(rows, columns);
        System.out.println();
    }

    private void printOneTableStyleFrame(int rows, int columnsIndex) {
        if (rows == 0) {
            if (columnsIndex == 0)
                System.out.print(EXTENDED_ASCII_TOP_LEFT);
            else if (columnsIndex == columns) {
                System.out.print(EXTENDED_ASCII_TOP_RIGHT);
            } else {
                System.out.print(EXTENDED_ASCII_TOP_CENTER);
            }
        }
        else if (rows == this.rows) {
            if (columnsIndex == 0)
                System.out.print(EXTENDED_ASCII_BOTTOM_LEFT);
            else if (columnsIndex == columns) {
                System.out.print(EXTENDED_ASCII_BOTTOM_RIGHT);
            } else {
                System.out.print(EXTENDED_ASCII_BOTTOM_CENTER);
            }
        } else {
            if (columnsIndex == 0)
                System.out.print(EXTENDED_ASCII_MIDDLE_LEFT);
            else if (columnsIndex == columns) {
                System.out.print(EXTENDED_ASCII_MIDDLE_RIGHT);
            } else {
                System.out.print(EXTENDED_ASCII_MIDDLE_CENTER);
            }
        }
    }
}
