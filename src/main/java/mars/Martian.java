package mars;

import java.util.ArrayList;
import java.util.Collection;

/* ����� ���� ��������
 * ���197
 */

/**
 * ��������� ����������
 *
 * @param <T> - ��� ������������� ����
 */
public interface Martian<T> {

  /**
   * ���������� ��������
   *
   * @return ��������
   */
  Martian<T> getParent();

  /**
   * ���������� ������������ ��� ����������
   *
   * @return ������������ ���
   */
  T getCode();

  /**
   * ���������� ����� ����������
   *
   * @return ��������� �����
   */
  Collection<? extends Martian<T>> getChildren();

  /**
   * ���������� ���� ����������� ����������
   *
   * @return ��������� ���� ����������� (�����, ����� ���� �����, ...)
   */
  default Collection<? extends Martian<T>> getFamily() {
    Collection<Martian<T>> family = new ArrayList<>();
    for (var child : getChildren()) {
      family.add(child);
      family.addAll(child.getFamily());
    }
    return family;
  }

  /**
   * ���������, ���� �� ����� ����� ���������� ������� � ������ ������������
   * �����
   *
   * @param value - �������� ������������� ����
   * @return ���� �� ����� �������
   */
  default boolean hasChildWithValue(final T value) {
    return keyInCollection(value, getChildren());
  }

  /**
   * ���������, ���� �� ����� ���� ����������� ���������� ������� � ������
   * ������������ �����
   *
   * @param value - �������� ������������� ����
   * @return ���� �� ����� ���������
   */
  default boolean hasDescadantWithValue(final T value) {
    for (var martian : getFamily()) {
      if (martian.getCode() == value) {
        return true;
      }
    }
    return false;
  }

  /**
   * ���������, ���� �� ����������� ����� ����������� � ������ �����������
   *
   * @param martian - ������ ���������
   * @return ���� �� �����������
   */
  default boolean familyIntersect(final Martian<T> martian) {
    Collection<? extends Martian<T>> myFamily = getFamily();
    Collection<? extends Martian<T>> otherFamily = martian.getFamily();

    if (myFamily.contains(martian) || otherFamily.contains(this)) {
      return true;
    }

    for (var myFamilyMartian : myFamily) {
      if (otherFamily.contains(myFamilyMartian)) {
        return true;
      }
    }

    return false;
  }

  /**
   * ���������, ���� �� ��������� � ������ ������������ ����� � ����������
   *
   * @param value      - ������������ ���
   * @param collection - ��������� �������
   * @return ���� �� ��������� � ������ ������������ ����� � ����������
   */
  default boolean keyInCollection(final T value,
      final Collection<? extends Martian<T>> collection) {
    for (var martian : collection) {
      if (martian.getCode() == value) {
        return true;
      }
    }
    return false;
  }
}

