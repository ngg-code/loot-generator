package edu.grinnell.csc207.lootgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The LootGenerator program simulates fighting monsters and collecting randomly
 * generated loot based on Diablo-style mechanics.
 */
public class LootGenerator {
    /** The path to the dataset (either the small or large set). */
    private static final String DATA_SET = "data/large";

    public static List<String[]> monsters = new ArrayList<>();
    public static List<String[]> treasureClasses = new ArrayList<>();
    public static List<String[]> armorItems = new ArrayList<>();
    public static List<String[]> prefixes = new ArrayList<>();
    public static List<String[]> suffixes = new ArrayList<>();

    /**
     * The main method that runs the LootGenerator program.
     * 
     * @param args command line arguments (not used)
     * @throws FileNotFoundException if the data files are not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("This program kills monsters and generates loot!");
        loadMonsters();
        loadTreasureClasses();
        loadArmor();
        loadAffixes();
        if (monsters.isEmpty()) {
            System.err.println("Error: No monsters loaded. Check file path and format.");
            return;
        }

        while (true) {
            String monster = getRandomMonster();
            System.out.println("Fighting " + monster + "...");
            System.out.println("You have slain " + monster + "!");
            System.out.println(monster + " dropped:\n");

            String treasureClass = getTreasureClassForMonster(monster);
            String baseItem = generateBaseItem(treasureClass);
            String fullItemName = baseItem;
            String baseStat = getBaseStat(baseItem);

            List<String> affixStats = new ArrayList<>();
            String prefix = generateAffix(prefixes, affixStats);
            String suffix = generateAffix(suffixes, affixStats);

            if (!prefix.isEmpty()) {
                fullItemName = prefix + " " + fullItemName;
            }
            if (!suffix.isEmpty()) {
                fullItemName = fullItemName + " " + suffix;
            }
            System.out.println(fullItemName);
            System.out.println(baseStat);
            for (String stat : affixStats) {
                System.out.println(stat);
            }
            System.out.println("Fight again [y/n]?");
            Scanner newScanner = new Scanner(System.in);
            String response = newScanner.nextLine();
            if (response.equalsIgnoreCase("n")) {
                System.out.println("Thanks for playing!");
                newScanner.close();
                break;
            }
        }
    }

    /** Loads the monster data from the file. */
    static void loadMonsters() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(DATA_SET + "/monstats.txt"));
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split("\t");
            if (parts.length >= 4) {
                monsters.add(parts);
            }
        }
        scanner.close();
    }

    /** Loads the treasure class data from the file. */
    static void loadTreasureClasses() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(DATA_SET + "/TreasureClassEx.txt"));
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split("\t");
            if (parts.length >= 4) {
                treasureClasses.add(parts);
            }
        }
        scanner.close();
    }

    /** Loads the armor data from the file. */
    static void loadArmor() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(DATA_SET + "/armor.txt"));
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split("\t");
            if (parts.length >= 3) {
                armorItems.add(parts);
            }
        }
        scanner.close();
    }

    /** Loads the affix data from the file. */
    static void loadAffixes() throws FileNotFoundException {
        Scanner prefixScanner = new Scanner(new File(DATA_SET + "/MagicPrefix.txt"));
        while (prefixScanner.hasNextLine()) {
            String[] parts = prefixScanner.nextLine().split("\t");
            if (parts.length >= 4) {
                prefixes.add(parts);
            }
        }
        prefixScanner.close();

        Scanner suffixScanner = new Scanner(new File(DATA_SET + "/MagicSuffix.txt"));
        while (suffixScanner.hasNextLine()) {
            String[] parts = suffixScanner.nextLine().split("\t");
            if (parts.length >= 4) {
                suffixes.add(parts);
            }
        }
        suffixScanner.close();
    }

    /**
     * Generates a random monster name.
     * 
     * @return a random monster name
     */
    static String getRandomMonster() {
        Random rand = new Random();
        return monsters.get(rand.nextInt(monsters.size()))[0];
    }

    /**
     * Generates a random treasure class for a given monster.
     * 
     * @param monsterName the name of the monster
     * @return the treasure class for the monster
     */
    static String getTreasureClassForMonster(String monsterName) {
        for (String[] monster : monsters) {
            if (monster[0].equals(monsterName)) {
                return monster[3];
            }
        }
        return null;
    }

    /**
     * Generates a base item from the treasure class.
     * 
     * @param treasureClass the treasure class
     * @return a base item from the treasure class
     */
    static String generateBaseItem(String treasureClass) {
        Random rand = new Random();
        for (String[] tc : treasureClasses) {
            if (tc[0].equals(treasureClass)) {
                String[] candidates = {tc[1], tc[2], tc[3] };
                while (true) {
                    String chosen = candidates[rand.nextInt(candidates.length)];
                    for (String[] armor : armorItems) {
                        if (armor[0].equals(chosen)) {
                            return chosen;
                        }
                    }
                    return generateBaseItem(chosen);
                }
            }
        }
        return null;
    }

    /**
     * Generates a base stat for the item.
     * 
     * @param itemName the name of the item
     * @return the base stat for the item
     */
    static String getBaseStat(String itemName) {
        Random rand = new Random();
        for (String[] armor : armorItems) {
            if (armor[0].equals(itemName)) {
                int min = Integer.parseInt(armor[1]);
                int max = Integer.parseInt(armor[2]);
                int def = min + rand.nextInt(max - min + 1);
                return "Defense: " + def;
            }
        }
        return "Unknown Base Stat";
    }

    /**
     * Generates an affix for the item.
     * 
     * @param affixList  the list of affixes
     * @param affixStats the list to store affix stats
     * @return the generated affix
     */
    static String generateAffix(List<String[]> affixList, List<String> affixStats) {
        Random rand = new Random();
        if (rand.nextBoolean() && !affixList.isEmpty()) {
            String[] affix = affixList.get(rand.nextInt(affixList.size()));
            String name = affix[0];
            String modCode = affix[1];
            int min = Integer.parseInt(affix[2]);
            int max = Integer.parseInt(affix[3]);
            int value = min + rand.nextInt(max - min + 1);
            affixStats.add(value + " " + modCode);
            return name;
        }
        return "";
    }
}