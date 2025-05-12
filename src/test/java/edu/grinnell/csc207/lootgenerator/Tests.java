package edu.grinnell.csc207.lootgenerator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

public class Tests {

    @BeforeAll
    static void setup() throws FileNotFoundException {
        LootGenerator.loadMonsters();
        LootGenerator.loadTreasureClasses();
        LootGenerator.loadArmor();
        LootGenerator.loadAffixes();
    }

    @Test
    void testMonstersLoaded() {
        String monster = LootGenerator.getRandomMonster();
        assertNotNull(monster, "Monster should not be null");
        assertTrue(monster.length() > 0, "Monster name should not be empty");
    }

    @Test
    void testTreasureClassLookup() {
        String monster = LootGenerator.getRandomMonster();
        String treasureClass = LootGenerator.getTreasureClassForMonster(monster);
        assertNotNull(treasureClass, "Treasure class should not be null");
        assertTrue(treasureClass.length() > 0, "Treasure class should not be empty");
    }

    @Test
    void testBaseItemGeneration() {
        String monster = LootGenerator.getRandomMonster();
        String tc = LootGenerator.getTreasureClassForMonster(monster);
        String item = LootGenerator.generateBaseItem(tc);
        assertNotNull(item, "Base item should not be null");
        assertTrue(item.length() > 0, "Base item should not be empty");
    }

    @Test
    void testBaseStatFormat() {
        String monster = LootGenerator.getRandomMonster();
        String tc = LootGenerator.getTreasureClassForMonster(monster);
        String item = LootGenerator.generateBaseItem(tc);
        String stat = LootGenerator.getBaseStat(item);
        assertNotNull(stat);
        assertTrue(stat.startsWith("Defense: "), "Stat should start with 'Defense: '");
        int value = Integer.parseInt(stat.split(" ")[1]);
        assertTrue(value > 0, "Defense value should be positive");
    }

    @Test
    void testAffixStats() {
        List<String> affixStats = new java.util.ArrayList<>();
        String prefix = LootGenerator.generateAffix(LootGenerator.prefixes, affixStats);
        String suffix = LootGenerator.generateAffix(LootGenerator.suffixes, affixStats);

        if (!prefix.isEmpty() || !suffix.isEmpty()) {
            assertFalse(affixStats.isEmpty(), "Affix stats should be added if affix was generated");
            for (String stat : affixStats) {
                String[] parts = stat.split(" ");
                assertTrue(parts.length >= 2, "Affix stat should contain value and description");
                int value = Integer.parseInt(parts[0]);
                assertTrue(value > 0, "Affix value should be positive");
            }
        }
    }
}