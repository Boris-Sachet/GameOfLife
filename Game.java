import java.util.Arrays;
import java.util.Random;

public class Game {
    // Settings
    private final Boolean[][] universe,futureUniverse;
    private final int height,width;
    private Boolean universStable = false;

    /**
     * Constructor building the universe
     * @param height of universe
     * @param width of universe*/
    public Game(int height, int width) {
        assert height >= 0 && width >= 0;
        this.universe = new Boolean[height][width];
        this.futureUniverse = new Boolean[height][width];
        this.height = height;
        this.width = width;

        // Initialise universe
        for (int line = 0; line < getHeight(); line++) {
            for (int column = 0; column < getWidth(); column++) {
                universe[line][column] = false;
            }
        }
        // Initialise the future
        System.arraycopy(universe,0,futureUniverse,0,universe.length);
    }
    // Getters
    /**
     * @return number of lines in Universe
     */
    public int getHeight() {
        return height;
    }
    /**
     * @return number if columns in Universe
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the status alive/dead of a cell
     * @param line x position
     * @param column y position
     * @return true if alive, false if dead
     */
    public boolean getCell(int line, int column) {
        assert isInUniverse(line, column);
//        System.out.println(line + " | " + column);
        return universe[line][column];
    }

    /**
     * Get the number of alive neighbours
     * @param line x position
     * @param column y position
     * @return Number of alive neighbours, min 0, max 8
     */
    private int getAliveNeighboursCount (int line, int column) {
        assert isInUniverse(line,column);
//        System.out.println("getAliveNeighboursCount for "+line+column);
        int aliveNeighbours = 0;
//        System.out.println("->"+(line-1)+"|"+(column-1));
        if (isInUniverse((line-1),(column-1))) if (getCell((line-1),(column-1))) aliveNeighbours++; // -1/-1
        if (isInUniverse((line-1), column)   ) if (getCell((line-1), column))    aliveNeighbours++; // -1/0
        if (isInUniverse((line-1),(column+1))) if (getCell((line-1),(column+1))) aliveNeighbours++; // -1/+1
        if (isInUniverse( line   ,(column-1))) if (getCell( line   ,(column-1))) aliveNeighbours++; //  0/-1
        if (isInUniverse( line   ,(column+1))) if (getCell( line   ,(column+1))) aliveNeighbours++; //  0/+1
        if (isInUniverse((line+1),(column-1))) if (getCell((line+1),(column-1))) aliveNeighbours++; // +1/-1
        if (isInUniverse((line+1), column)   ) if (getCell((line+1), column))    aliveNeighbours++; // +1/0
        if (isInUniverse((line+1),(column+1))) if (getCell((line+1),(column+1))) aliveNeighbours++; // +1/+1
//        System.out.println("getAliveNeighboursCount done");
        return aliveNeighbours;
    }

    // Setters
    /**
     * Switch the living status of one cell for the next generation
     * @param line x position
     * @param column y position
     * */
    private void toogleLifeStatus (int line, int column) {
        assert isInUniverse(line, column);
        futureUniverse[line][column] = !universe[line][column];
    }

    /**
     * Generate random life
     * @param seedChance integet [0..100] chance for a cell to become alive
     */
    public void seedLife (int seedChance) {
        Random randy = new Random();
        System.out.println("Start seeding life");
        for (int line = 0; line < getHeight(); line++) {
            for (int column = 0; column < getWidth(); column++) {
                // If cell is dead and dice rool is inferior or equal to seedChance then the cell become alive
/*                if (getCell(line,column) && randy.nextInt(101) <= seedChance) {
                    toogleLifeStatus(line,column);
                    System.out.println("Life sparkled! " + line + column);
                }*/
                int diceRoll = randy.nextInt(101);
                if (!getCell(line,column) && diceRoll <= seedChance) {
                    toogleLifeStatus(line,column);
                    System.out.println("Life sparkled! " + line + "|" + column + " Roll:" + diceRoll);
                }
            }
        }
        System.out.println("Done  seeding life");
    }

    // Checkers
    /**
     * Check if a coordinate is in universe
     * @param line x position
     * @param column y position
     * @return true if coordinates are in bounds of universe
     */
    private boolean isInUniverse(int line, int column) {
        return line >= 0 &&
                column >= 0 &&
                line <= this.width-1 &&
                column <= this.height-1;
    }

    /**
     * Check if the whole universe is dead
     * @retrun true if one cell is found alive in the universe
     */
    public boolean universeIsDead() {
        for (Boolean[] line : universe) {
            for (boolean cell : line) {
                if (cell) return false;
            }
        }
        return true;
    }

    // Spawners

    // Printer
    /**
     * Represent the universe in a string with characters
     * @return String with # for alive cells and space for dead cells with \n at the end of each line
     */
    @Override
    public String toString() {
        String universeString = "" ;

        for (Boolean[] line : universe) {
            universeString += "|";
            for (Boolean cell : line) {
                if (cell) universeString += "#|";
                else universeString += " |";
            }
            universeString += "\n";
        }
        return universeString;
    }

    // Game runners
    /**
     * Make life evolve to the next generation
     */
    public void nextGeneration() {
        // Copy the present into the future in case something spawned
        System.arraycopy(universe,0,futureUniverse,0,universe.length);

        for (int line = 0; line < getHeight(); line++) {
            for (int column = 0; column < getWidth(); column++) {
                int aliveNeighbours = getAliveNeighboursCount(line,column);
                // Cell becomes alive if exactly 3 alive neighbours
                if (aliveNeighbours == 3) {if (!getCell(line,column)) toogleLifeStatus(line,column);}
                // Cell dies with less than two or more than 3 neighbours
                else if (aliveNeighbours < 2 | aliveNeighbours > 3) {if (getCell(line,column)) toogleLifeStatus(line,column);}
            }
        }
        if (Arrays.deepEquals(universe, futureUniverse)) universStable = true;
        // Copy the future into the present
        System.arraycopy(futureUniverse,0,universe,0,futureUniverse.length);
    }

    /**
     * Simulate the passage of time to allow generation of live to pass until the heat death of the universe
     * Also display the state of the universe at each generation
     */
    public void runTime() {
        int generation = 0;
        System.out.println("Beginning of time");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // CLS

        while (!universeIsDead() & generation < 2000 /*& !universStable*/) {
            // CLS
            System.out.println("Generation : " + generation);
            generation++;
            System.out.println(this);
            nextGeneration();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // CLS
        System.out.println("Generation : " + generation);
        System.out.println(this);
        System.out.println("Ending of time");
    }
}
