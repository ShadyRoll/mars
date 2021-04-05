import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import mars.Conservator;
import mars.Innovator;
import mars.Martian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Попов Олег Олегович
 * БПИ197
 */

/**
 * Класс тестов для классов марсиан
 */
public class testMartianClass {

  /**
   * Инноватор для тестов
   */
  static Innovator<String> innovator;

  /**
   * Перед каждым тестом обновляем новатора
   */
  @BeforeEach
  public void prepare() {
    innovator = new Innovator<>("innovator");
  }

  /**
   * Тест на сохранность генетического кода марсиан при операциях
   */
  @Test
  public void testCodes() {
    assertEquals("innovator", innovator.getCode());

    Conservator<String> conservator = new Conservator<>(innovator);
    assertEquals("innovator", conservator.getCode());

    Innovator<Integer> innovatorInt = new Innovator<>(1);
    Conservator<Integer> conservatorInt = new Conservator<>(innovatorInt);
    assertEquals(1, innovatorInt.getCode());
    assertEquals(1, conservatorInt.getCode());

    Innovator<Double> innovatorDouble = new Innovator<>(1.1);
    Conservator<Double> conservatorDouble = new Conservator<>(innovatorDouble);
    assertEquals(1.1, innovatorDouble.getCode());
    assertEquals(1.1, conservatorDouble.getCode());
  }

  /**
   * Проверка на добавление детей марсианам
   */
  @Test
  public void testAddChildren() {
    Innovator<String> childInnovator = new Innovator<>("child1");
    innovator.addChild(childInnovator);

    assertEquals(1, innovator.getChildren().size());

    Martian<String> copiedChild = ((ArrayList<Innovator<String>>) innovator
        .getChildren()).get(0);

    assertEquals("child1", copiedChild.getCode());
    assertEquals(childInnovator, copiedChild);
  }

  /**
   * Тест на удаление детей
   */
  @Test
  public void testDelChild() {
    Innovator<String> childInnovator = new Innovator<>("child1");
    innovator.addChild(childInnovator);

    boolean success = innovator.delChild(childInnovator);
    assertTrue(success);
    assertEquals(0, innovator.getChildren().size());
    assertNull(childInnovator.getParent());
  }

  /**
   * Тест методов инноватора
   */
  @Test
  public void testInnovatorOpportunities() {
    Collection<Innovator<String>> children = new ArrayList<>();
    Innovator<String> childInnovator = new Innovator<>("child1");
    children.add(childInnovator);

    innovator.setChildren(children);

    assertEquals(children.size(), innovator.getChildren().size());
    for (var child : innovator.getChildren()) {
      assertEquals(innovator, child.getParent());
    }
  }

  /**
   * Тест на неудачное добавление детей
   */
  @Test
  public void testChildrenInsertFail() {
    Collection<Innovator<String>> children = new ArrayList<>();
    Innovator<String> childInnovator = new Innovator<>("child1");
    children.add(childInnovator);
    children.add(innovator);

    boolean success = innovator.setChildren(children);
    assertFalse(success);

    children.remove(innovator);
    children.add(childInnovator);
    children.add(childInnovator);
    success = innovator.setChildren(children);
    assertFalse(success);
  }

  /**
   * Тест перевода новаторов в консерваторов
   */
  @Test
  public void testConversionToConservator() {
    Innovator<String> childInnovator1 = new Innovator<>("child1");
    boolean v = innovator.addChild(childInnovator1);

    Innovator<String> childInnovator2 = new Innovator<>("child2");
    childInnovator1.addChild(childInnovator2);

    assertEquals(1, innovator.getChildren().size());
    assertEquals(1, childInnovator1.getChildren().size());
    assertEquals(0, childInnovator2.getChildren().size());

    Martian<String> conservator = new Conservator<>(childInnovator2);

    assertEquals(childInnovator2.getCode(), conservator.getCode());
    assertEquals(childInnovator1.getCode(), conservator.getParent().getCode());
    assertEquals(innovator.getCode(),
        conservator.getParent().getParent().getCode());
  }

  /**
   * Проверка метода hasChildWithValue()
   */
  @Test
  public void testHasChildWithValue() {
    Innovator<String> childInnovator1 = new Innovator<>("child");
    innovator.addChild(childInnovator1);
    Innovator<String> childInnovator2 = new Innovator<>("childOfChild");
    childInnovator1.addChild(childInnovator2);

    assertFalse(innovator.hasChildWithValue(innovator.getCode()));
    assertTrue(innovator.hasChildWithValue("child"));
    assertFalse(innovator.hasChildWithValue("childOfChild"));
  }

  /**
   * Проверка метода hasDescadantWithValue()
   */
  @Test
  public void testHasDescadantWithValue() {
    Innovator<String> childInnovator1 = new Innovator<>("child");
    innovator.addChild(childInnovator1);
    Innovator<String> childInnovator2 = new Innovator<>("childOfChild");
    childInnovator1.addChild(childInnovator2);

    assertFalse(innovator.hasDescadantWithValue(innovator.getCode()));
    assertTrue(innovator.hasDescadantWithValue("child"));
    assertTrue(innovator.hasDescadantWithValue("childOfChild"));
  }

  /**
   * Тест на метод getFamily()
   */
  @Test
  public void testGetFamily() {
    ArrayList<Innovator<String>> childInnovators = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      assertTrue(childInnovators.add(new Innovator<>("child" + i)));
      if (i > 0) {
        assertTrue(
            childInnovators.get(i).setParent(childInnovators.get(i - 1)));
      }
    }
    assertTrue(childInnovators.get(0).setParent(innovator));
    assertEquals(10, innovator.getFamily().size());
    assertTrue(innovator.getFamily().contains(childInnovators.get(4)));
  }
}
