import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.instrument.IllegalClassFormatException;
import mars.Conservator;
import mars.GenealogicalTree;
import mars.Innovator;
import mars.Martian;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/* Попов Олег Олегович
 * БПИ197
 */

/**
 * Класс с тестами генеалогического дерева
 */
public class testGenealogicalTree {

  /**
   * Тест создания дерева (генерация в текст)
   */
  @Test
  public void testMakeTree1() {
    Innovator<String> innovator = new Innovator<>("innovator");

    GenealogicalTree<String> tree = new GenealogicalTree<>(innovator,
        String.class);
    String treeStr = tree.generateTree();
    String[] lines = treeStr.split("\n");
    assertEquals(1, lines.length);
    assertEquals("Innovator (String:innovator)", lines[0]);
  }

  /**
   * Тест создания дерева (генерация в текст) с бОльшим количеством марсиан
   */
  @Test
  public void testMakeTree2() {
    Innovator<Integer> innovator = new Innovator<>(5);
    Innovator<Integer> childInnovator = new Innovator<>(10);
    innovator.addChild(childInnovator);
    Conservator<Integer> conservator = new Conservator<>(innovator);

    GenealogicalTree<Integer> tree = new GenealogicalTree<>(conservator,
        Integer.class);
    String treeStr = tree.generateTree();
    String[] lines = treeStr.split("\n");
    assertEquals(2, lines.length);
    assertEquals("    Conservator (Integer:10)", lines[1]);
  }

  /**
   * Тест чтения дерева из текста
   */
  @Test
  public void testRead() {
    try {
      String treeStr =
          "Conservator (Double:1.1)\n"
              + "    Conservator (Double:2.2)\n"
              + "    Conservator (Double:3.3)\n";
      GenealogicalTree<Double> tree = new GenealogicalTree<>(Double.class);
      Martian<Double> martian = tree.readTree(treeStr);
      assertEquals(2, martian.getFamily().size());
      assertEquals(1.1D, martian.getCode());
    } catch (Exception ex) {
      Assert.fail("Exception caught! (" + ex.getMessage() + ")");
    }
  }

  /**
   * Тест на чтение пустой строки (исключение)
   */
  @Test
  public void testReadTreeException() {
    try {
      GenealogicalTree<String> tree = new GenealogicalTree<>(String.class);
      Martian<String> martian = tree.readTree("");
      Assert.fail("Expected IllegalClassFormatException");
    } catch (IllegalClassFormatException ex) {
      assertEquals("Can't find valid separator in line", ex.getMessage());
    }
  }

  /**
   * Тест на несовпадение типа генетического кода в дереве (исключение)
   */
  @Test
  public void testReadTreeException2() {
    try {
      String treeStr =
          "Conservator (Double:1.1)\n"
              + "    Conservator (Double:2.2)\n";
      GenealogicalTree<String> tree = new GenealogicalTree<>(String.class);
      Martian<String> martian = tree.readTree(treeStr);
      Assert.fail("Expected IllegalClassFormatException");
    } catch (IllegalClassFormatException ex) {
      assertEquals("This tree serialise only String, but Double found",
          ex.getMessage());
    }
  }

  /**
   * Тест чтения из строк и повторной записи
   */
  @Test
  public void testReadThenWrite() {
    try {
      String treeStr =
          "Innovator (String:John)\n"
              + "    Innovator (String:James)\n"
              + "        Innovator (String:Jared)\n"
              + "    Innovator (String:Johan)\n"
              + "    Innovator (String:Jamil)\n"
              + "        Innovator (String: Jotl)\n"
              + "            Innovator (String:Jack)\n";
      GenealogicalTree<String> tree = new GenealogicalTree<>(String.class);
      tree.readTree(treeStr);
      assertEquals(treeStr, tree.generateTree());
    } catch (Exception ex) {
      Assert.fail("Exception caught! (" + ex.getMessage() + ")");
    }
  }
}
