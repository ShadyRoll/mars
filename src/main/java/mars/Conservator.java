package mars;

import java.util.ArrayList;
import java.util.Collection;

/* ����� ���� ��������
 * ���197
 */

/**
 * ���������-�����������
 *
 * @param <T> - ��� ������������� ����
 */
public class Conservator<T> implements Martian<T> {

  // ��������� �����������

  /**
   * ������� ������������ �� ������ ����������
   *
   * @param innovator - ���������
   */
  public Conservator(Innovator<T> innovator) {
    code = innovator.getCode();

    // ����� ������ �����
    Collection<Conservator<T>> newChildren = new ArrayList<>(
        innovator.getChildren().size());
    ArrayList<Innovator<T>> innovatorChildren =
        (ArrayList<Innovator<T>>) innovator.getChildren();

    // ��������� �����
    for (Innovator<T> innovatorChild : innovatorChildren) {
      // �������� ����������� � ������ Child-only
      newChildren.add(new Conservator<T>(innovatorChild,
          null, null, this));
    }
    children = newChildren;

    /* ���� �������� ����, ������� � ��� �������������
     * (���� ������ �� ��� ���������� ��������� � ��������, ������ ���������
     * �������� ������, ������� � �� ������ ������ ������ ����� ������� �������,
     * �.�. � ��������� � ������������� ���� �������������, � �� ������ ��������)
     */
    if (innovator.getParent() != null) {
      // �������� ����������� � ������ Avoid-called-child
      parent = new Conservator<>((Innovator<T>) innovator.getParent(),
          innovator,
          this, null);
    } else {
      parent = null;
    }
  }

  /**
   * ������� ������������ �� ������ ���������� ���������� ������ � ������� 2
   * "������" ����� ������������:
   * <p> 1. Child-only: ���������� ���������� ����������� � �������������
   * (������ ����) </p>
   * <p> 2. Avoid-called-child: ���������� �������� � ������������, ����� ��� *
   * ����������� (������ �����) </p>
   *
   * @param innovator              - ���������, ������� ����������
   *                               �������������
   * @param calledChild            - ���������, ������� ������ ����������� ���
   *                               ������ ��������
   * @param calledConservatorChild - ���������, ������� ������ ����������� ���
   *                               ������ �������� (����� ����������� �
   *                               ������������)
   * @param newParent              - ��������, ������� ������ ����������� �����
   *                               ������ �������
   */
  private Conservator(Innovator<T> innovator, Innovator<T> calledChild,
      Conservator<T> calledConservatorChild,
      Conservator<T> newParent) {
    code = innovator.getCode();

    // ���� ����������
    ArrayList<Innovator<T>> innovatorChildren =
        (ArrayList<Innovator<T>>) innovator.getChildren();
    Collection<Conservator<T>> newChildren = new ArrayList<>(
        innovator.getChildren().size());

    // ���� ��� ������ �� �������, ����� ��������
    if (calledChild == null) {
      // ����� Child-only (������ ����)
      for (Innovator<T> innovatorChild : innovatorChildren) {
        // �������� ����������� ��� ���� ����������� � ���� �� ������
        newChildren.add(new Conservator<T>(innovatorChild,
            null, null, this));
      }
      // ��������� ������
      children = newChildren;
      parent = newParent;
      // � ������ ������ ��� ������� ���� �������� ��������
      if (parent == null) {
        throw new NullPointerException(
            "Parent somehow is null in child-only constructor");
      }
    }
    // ����� ����������� ������ ������� ����� ����������
    else {
      // ����� Avoid-called-child (������ �����)
      for (int i = 0; i < innovatorChildren.size(); i++) {
        if (innovatorChildren.get(i) == calledChild) {
          // �������, �������� ����������� ��� ���� �������������
          newChildren.add(calledConservatorChild);
        } else {
          // � ��� ��������� ����� � �� ����� ����� ������� ��������������
          newChildren.add(new Conservator<T>(innovatorChildren.get(i),
              null, null, this));
        }
      }
      children = newChildren;

      if (innovator.getParent() != null) {
        // ���� � � ����� ���������� ���� �������� - ���������� ������ �����
        parent = new Conservator<T>((Innovator<T>) innovator.getParent(),
            innovator, this, null);
      } else {
        // ����� ��������, ��� �������� ���
        parent = null;
      }
    }
  }

  /**
   * ���������� �������� ����� ����������
   * @return ��������
   */
  @Override
  public Martian<T> getParent() {
    return parent;
  }

  /**
   * ���������� ��������� ����� ����� ����������
   * @return ��������� �����
   */
  @Override
  public Collection<? extends Martian<T>> getChildren() {
    return children;
  }

  /**
   * ���������� ������������ ��� ����� ����������
   * @return ������������ ���
   */
  @Override
  public T getCode() {
    return code;
  }

  /**
   * ������������ ���
   */
  final T code;
  /**
   * ����
   */
  final Collection<Conservator<T>> children;
  /**
   * ��������
   */
  final Conservator<T> parent;
}
