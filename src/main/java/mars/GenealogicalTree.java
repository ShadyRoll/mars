package mars;

import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.Stack;

/* ����� ���� ��������
 * ���197
 */

/**
 * ��������������� ������ �������
 */
public class GenealogicalTree<T> {

  /**
   * ������� ��������������� ������ ������� � ������������ ����� ������� ����
   *
   * @param codeType - ��� ������������� ����
   */
  public GenealogicalTree(Class<?> codeType) {
    this(null, codeType);
  }

  /**
   * ������� ��������������� ������ ������� � ������������ ����� ������� ���� ��
   * ������ ����������� ����������
   *
   * @param progenitor - ��������� ("�����������")
   * @param codeType   - ��� ������������� ����
   */
  public GenealogicalTree(Martian<T> progenitor, Class<?> codeType) {
    this.progenitor = progenitor;
    codeTypeStr = codeType.getSimpleName();
  }

  /**
   * ���������� ������
   *
   * @return ������ (������)
   */
  public String generateTree() {
    return writeMartianWithChildren(progenitor, 0);
  }

  /**
   * ��������� ������ �� ������
   *
   * @param strTree - ������
   * @return ����������� ����� ������ (�������� ���� ���������)
   * @throws IllegalClassFormatException - ���� ������ ����� �������� ������,
   *                                     ����� ��������� ��� ������ "������������"
   */
  public Martian<T> readTree(String strTree)
      throws IllegalClassFormatException {
    // ���������� ������������� ��� ������ �����������
    treeType = TreeType.UNDEFINED;

    String[] lines = strTree.split("\n");
    // ��������� ����������� (�� ������ ������)
    progenitor = readMartian(lines[0]);
    // ��������� ��������� �������
    readMartians(lines);
    // ���� ������ ������� �� �������������, ������� �� ����������� ���������
    if (treeType == TreeType.CONSERVATORS) {
      progenitor = new Conservator<T>((Innovator<T>) getProgenitor());
    }
    return getProgenitor();
  }

  /**
   * ��������� ������� ����� ����������� �� ����� � ����� ���: ������� ���������
   * ���� �������, ����� ��� �������� (����� � ��� ������ �� ����), � �����,
   * ���� �����, � ��������� ��� ������ � �������������.
   *
   * @param lines - ������ � ���������� � ���������
   * @throws IllegalClassFormatException - ���� ������ ����� �������� ������
   */
  private void readMartians(String[] lines) throws IllegalClassFormatException {
    // ���� ��� �������� ���������
    Stack<Innovator<T>> parentStack = new Stack<>();
    // ����� ��������� ���� �����������
    parentStack.add((Innovator<T>) getProgenitor());
    // ������� ���������� ��������
    int curSpaceLevel = 4;
    // ������� ���������
    Innovator<T> curMartian;
    for (int i = 1; i < lines.length; i++) {
      // ������� ���-�� ��������
      int numOfSpaces = countSpaces(lines[i]);
      curMartian = (Innovator<T>) readMartian(lines[i].substring(numOfSpaces));
      if (numOfSpaces > curSpaceLevel) {
        // ���� ������� ���������, ��������� ����������� ���������� � ���� ���������
        var lastFatherChildren = ((ArrayList<Innovator<T>>) parentStack.peek()
            .getChildren());
        parentStack.add(lastFatherChildren.get(lastFatherChildren.size() - 1));
      } else if (numOfSpaces < curSpaceLevel) {
        // ���� ������� ��������� - ������� �������� ��������
        parentStack.pop();
      }
      // ��������� � �������� �������� ������� (����� ����������)
      parentStack.peek().addChild(curMartian);
      curSpaceLevel = numOfSpaces;
    }
  }

  /**
   * ���������� �����������
   *
   * @return �����������
   */
  public Martian<T> getProgenitor() {
    return progenitor;
  }

  /**
   * ������� ���������� �������� � ������ ������
   *
   * @param str - ������
   * @return ���������� ��������
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
   * ����������� ���������� � ��� ����� � ������
   *
   * @param martian    - ���������
   * @param spaceLevel - ������� �������� (���������� �������� � ������ ������)
   * @return ������ ���������� � ��� ����� � ������ �������
   */
  private String writeMartianWithChildren(Martian<T> martian, int spaceLevel) {
    StringBuilder builder = new StringBuilder();
    // ��������� �� ������ ���-�� ��������
    builder.append("    ".repeat(spaceLevel));
    // ������� ����� ����������
    builder.append(formatMartian(martian));
    // ������� ��� �����
    for (var child : martian.getChildren()) {
      builder.append(writeMartianWithChildren(child, spaceLevel + 1));
    }
    return builder.toString();
  }

  /**
   * ����������� ���������� � ������
   *
   * @param martian - ���������
   * @return ������ (���������� � ����������)
   */
  private String formatMartian(Martian<T> martian) {
    return progenitor.getClass().getSimpleName()
        + " (" + martian.getCode().getClass().getSimpleName()
        + ":" + martian.getCode() + ")\n";
  }

  /**
   * ��������� ���������� �� ������
   *
   * @param str - ������
   * @return ���������
   * @throws IllegalClassFormatException - ���� ������ ����� �������� ������
   */
  private Martian<T> readMartian(String str)
      throws IllegalClassFormatException {
    // ������� ����������� (��� " (")
    int separatorPos = str.indexOf(" (");
    if (separatorPos == -1) {
      throw new IllegalClassFormatException(
          "Can't find valid separator in line");
    }
    // ��� (���) ����������
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
    // �������� ������������� ���� ����������
    Object valObj = getMartianCode(val);
    checkMartianName(name);
    return new Innovator<T>((T) valObj);
  }

  /**
   * �������� �������� ������������� ���� ����������
   *
   * @param val - ������ �� ��������� ������������� ����
   * @return �������� ������������� ���� ����������
   */
  private Object getMartianCode(String val) {
    Object valObj = null;
    // ������ �������� � ����������� �� ����
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
   * ���������, ���������� �� ��� ���������� ���� ������� � ���� ������
   *
   * @param name - ��� (���) ����������
   * @throws IllegalClassFormatException - ���� ������� �������������
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
   * ��� ������ (����� �������� � ��� ���������)
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
   * ��� ������������� ���� ������� � ������ (������)
   */
  private final String codeTypeStr;
  /**
   * ��� ������
   */
  private TreeType treeType;
  /**
   * �����������
   */
  private Martian<T> progenitor;
}
