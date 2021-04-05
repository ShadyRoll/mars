package mars;

import java.util.ArrayList;
import java.util.Collection;

/* ����� ���� ��������
 * ���197
 */

/**
 * ���������-���������
 *
 * @param <T> - ��� ������������� ����
 */
public class Innovator<T> implements Martian<T> {

  /**
   * ������� ���������� � ������ ������������ �����
   *
   * @param code - ������������ ���
   */
  public Innovator(T code) {
    this(code, null, null);
  }

  /**
   * ������� ����������, ������� ��������, ����� � ������������ ���
   *
   * @param code     - ������������ ���
   * @param children - ����
   * @param parent   - ��������
   */
  public Innovator(T code, Collection<Innovator<T>> children,
      Innovator<T> parent) {
    setCode(code);
    if (children != null) {
      this.children = children;
    } else {
      this.children = new ArrayList<>();
    }
    this.parent = parent;
  }

  /**
   * ���������� ��������
   *
   * @return ��������
   */
  public Martian<T> getParent() {
    return parent;
  }

  /**
   * ���������� �����
   *
   * @return ��������� �����
   */
  public Collection<Innovator<T>> getChildren() {
    return children;
  }

  /**
   * ���������� ������������ ���
   *
   * @return ������������ ���
   */
  @Override
  public T getCode() {
    return code;
  }

  /**
   * ������������� �������� ������������� ����
   *
   * @param code - ����� ����������� ���
   */
  public void setCode(T code) {
    this.code = code;
  }

  /**
   * ������������� ����� �������� �����
   *
   * @param children - ����� ��������� �����
   * @return ������� �� ��������
   */
  public boolean setChildren(Collection<Innovator<T>> children) {
    // �������� �� �����
    for (var child : children) {
      if (familyIntersect(child)) {
        return false;
      }
    }
    if (children.contains(this)) {
      return false;
    }
    // �������� ������� ���� ����� �����
    Collection<Innovator<T>> prevChildren = this.children;
    this.children = new ArrayList<>();
    for (var child : children) {
      if (!addChild(child)) {
        // ���� ���� �� 1 ������� ��������� - �������� ��� ��������� �����
        this.children = prevChildren;
        return false;
      }
    }
    return true;
  }

  /**
   * ������������ ��������
   *
   * @param parent - ����� ��������
   * @return ������� �� ��������
   */
  public boolean setParent(Innovator<T> parent) {
    // �������� �� �����
    if (parent != null && familyIntersect(parent)) {
      return false;
    }

    this.parent = parent;
    parent.addChild(this);
    return true;
  }

  /**
   * ��������� ������� � ����� ����������
   *
   * @param child - �������
   * @return ������� �� ��������
   */
  public boolean addChild(Innovator<T> child) {
    // �������� �� ����������� (�����, �����)
    if (familyIntersect(child)) {
      return false;
    }
    children.add(child);
    child.parent = this;
    return true;
  }

  /**
   * ������� �������
   *
   * @param child - �������, �������� ����� �������
   * @return ������� �� ��������
   */
  public boolean delChild(Innovator<T> child) {
    // �������� �� ����������� (�����, �����)
    if (!children.contains(child)) {
      return false;
    }
    children.remove(child);
    child.parent = null;
    return true;
  }

  /**
   * ������������ ���
   */
  private T code;
  /**
   * ��������� �����
   */
  private Collection<Innovator<T>> children;
  /**
   * ��������
   */
  private Innovator<T> parent;
}
