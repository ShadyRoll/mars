package mars;

import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.Stack;

/* Попов Олег Олегович
 * БПИ197
 */

/**
 * Генеологическое дерево марсиан
 */
public class GenealogicalTree<T> {

  /**
   * Создает генеалогическое дерево марсиан с генетическим кодом данного типа
   *
   * @param codeType - тип генетического кода
   */
  public GenealogicalTree(Class<?> codeType) {
    this(null, codeType);
  }

  /**
   * Создает генеалогическое дерево марсиан с генетическим кодом данного типа на
   * основе наслдеников марсианина
   *
   * @param progenitor - марсианин ("прародитель")
   * @param codeType   - тип генетического кода
   */
  public GenealogicalTree(Martian<T> progenitor, Class<?> codeType) {
    this.progenitor = progenitor;
    codeTypeStr = codeType.getSimpleName();
  }

  /**
   * Генерирует дерево
   *
   * @return дерево (строка)
   */
  public String generateTree() {
    return writeMartianWithChildren(progenitor, 0);
  }

  /**
   * Считывает дерево из строки
   *
   * @param strTree - строка
   * @return прародитель этого дерева (родитель всех родителей)
   * @throws IllegalClassFormatException - если строка имела неверный формат,
   *                                     будет возращена эта ошибка "сериализации"
   */
  public Martian<T> readTree(String strTree)
      throws IllegalClassFormatException {
    // Изначально устанавливаем тип дерева неизвестным
    treeType = TreeType.UNDEFINED;

    String[] lines = strTree.split("\n");
    // Считываем прародителя (он всегда первый)
    progenitor = readMartian(lines[0]);
    // Считываем остальных марсиан
    readMartians(lines);
    // Если дерево состоит из консерваторов, сделаем их сохранненых новаторов
    if (treeType == TreeType.CONSERVATORS) {
      progenitor = new Conservator<T>((Innovator<T>) getProgenitor());
    }
    return getProgenitor();
  }

  /**
   * Считывает марсиан после прародителя из строк Я делаю так: сначала считываем
   * всех марсиан, будто они новаторы (чтобы я мог менять их поля), а потом,
   * если нужно, я превращаю все дерево в консерваторов.
   *
   * @param lines - строки с ифномацией о марсианах
   * @throws IllegalClassFormatException - если строка имела неверный формат
   */
  private void readMartians(String[] lines) throws IllegalClassFormatException {
    // Стек для хранения родителей
    Stack<Innovator<T>> parentStack = new Stack<>();
    // Сразу добавляем туда прародителя
    parentStack.add((Innovator<T>) getProgenitor());
    // Текущее количество пробелов
    int curSpaceLevel = 4;
    // Текущий марсианин
    Innovator<T> curMartian;
    for (int i = 1; i < lines.length; i++) {
      // Текущее кол-во пробелов
      int numOfSpaces = countSpaces(lines[i]);
      curMartian = (Innovator<T>) readMartian(lines[i].substring(numOfSpaces));
      if (numOfSpaces > curSpaceLevel) {
        // Если уровень повысился, добавляем предыдущего марсианина в стек родителей
        var lastFatherChildren = ((ArrayList<Innovator<T>>) parentStack.peek()
            .getChildren());
        parentStack.add(lastFatherChildren.get(lastFatherChildren.size() - 1));
      } else if (numOfSpaces < curSpaceLevel) {
        // Если уровень понизился - удаляем верхнего родителя
        parentStack.pop();
      }
      // Добавляем в верхнего родителя ребенка (этого марсианина)
      parentStack.peek().addChild(curMartian);
      curSpaceLevel = numOfSpaces;
    }
  }

  /**
   * Возвращает прародителя
   *
   * @return прародитель
   */
  public Martian<T> getProgenitor() {
    return progenitor;
  }

  /**
   * Считает количество пробелов в начале строки
   *
   * @param str - строка
   * @return количетсво пробелов
   */
  private int countSpaces(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) != ' ') {
        return i;
      }
    }
    return str.length();
  }

  /**
   * Преобразует марсианина и его детей в строки
   *
   * @param martian    - марсианин
   * @param spaceLevel - уровень пробелов (количество пробелов в начале строки)
   * @return строки марсианина и его детей в нужном формате
   */
  private String writeMartianWithChildren(Martian<T> martian, int spaceLevel) {
    StringBuilder builder = new StringBuilder();
    // Отступаем на нужное кол-во пробелов
    builder.append("    ".repeat(spaceLevel));
    // Выводим этого марсианина
    builder.append(formatMartian(martian));
    // Выводим его детей
    for (var child : martian.getChildren()) {
      builder.append(writeMartianWithChildren(child, spaceLevel + 1));
    }
    return builder.toString();
  }

  /**
   * Преобразует марсианина в строку
   *
   * @param martian - марсианин
   * @return строка (инофрмация о марсианине)
   */
  private String formatMartian(Martian<T> martian) {
    return progenitor.getClass().getSimpleName()
        + " (" + martian.getCode().getClass().getSimpleName()
        + ":" + martian.getCode() + ")\n";
  }

  /**
   * Считывает марсианина из строки
   *
   * @param str - строка
   * @return марсианин
   * @throws IllegalClassFormatException - если строка имела неверный формат
   */
  private Martian<T> readMartian(String str)
      throws IllegalClassFormatException {
    // Позиция разделителя (это " (")
    int separatorPos = str.indexOf(" (");
    if (separatorPos == -1) {
      throw new IllegalClassFormatException(
          "Can't find valid separator in line");
    }
    // Имя (тип) марсианина
    String name = str.substring(0, separatorPos);
    String typeAndValStr = str.substring(separatorPos + 2, str.length() - 1);
    String[] typeAndValArr = typeAndValStr.split(":");
    String typeStr = typeAndValArr[0];
    String val = typeAndValArr[1];
    if (getProgenitor() != null && !typeStr
        .equals(getProgenitor().getCode().getClass().getSimpleName())) {
      throw new IllegalClassFormatException(
          "This tree serialise only " + progenitor.getCode().getClass()
              .getSimpleName() + ", but " + typeStr + " found");
    }
    // Значение генетического кода марсианина
    Object valObj = getMartianCode(val);
    checkMartianName(name);
    return new Innovator<T>((T) valObj);
  }

  /**
   * Получает значение генетического кода марсианина
   *
   * @param val - строка со значениме генетического кода
   * @return значение генетического кода марсианина
   */
  private Object getMartianCode(String val) {
    Object valObj = null;
    // Парсим значение в зависимости от типа
    switch (codeTypeStr) {
      case "String":
        valObj = val;
        break;
      case "Integer":
        valObj = Integer.parseInt(val);
        break;
      case "Double":
        valObj = Double.parseDouble(val);
        break;
    }
    return valObj;
  }

  /**
   * Проверяет, соответвуе ли тип марсианина типу марсиан в этом дереве
   *
   * @param name - имя (тип) марсианина
   * @throws IllegalClassFormatException - если найдено несоответсвие
   */
  private void checkMartianName(String name)
      throws IllegalClassFormatException {
    if (treeType == TreeType.UNDEFINED) {
      if (name.equals(TreeType.INNOVATORS.getName())) {
        treeType = TreeType.INNOVATORS;
      } else if (name.equals(TreeType.CONSERVATORS.getName())) {
        treeType = TreeType.CONSERVATORS;
      } else {
        throw new IllegalClassFormatException(
            "Unknown martian type (" + name + ")");
      }
    } else if (!name.equals(treeType.getName())) {
      throw new IllegalClassFormatException(
          "Found different types of martian in single tree");
    }
  }

  /**
   * Тип дерева (какие марсиане в нем находятся)
   */
  enum TreeType {
    UNDEFINED("Undefined"), INNOVATORS("Innovator"), CONSERVATORS(
        "Conservator");
    private final String name;

    TreeType(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  /**
   * Тип генетического кода марсиан в дереве (строка)
   */
  private final String codeTypeStr;
  /**
   * Тип дерева
   */
  private TreeType treeType;
  /**
   * Прародитель
   */
  private Martian<T> progenitor;
}
