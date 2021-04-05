package mars;

import java.util.ArrayList;
import java.util.Collection;

/* ѕопов ќлег ќлегович
 * Ѕѕ»197
 */

/**
 * »нтерфейс марсианина
 *
 * @param <T> - тип генетического кода
 */
public interface Martian<T> {

  /**
   * ¬озвращает родител€
   *
   * @return родитель
   */
  Martian<T> getParent();

  /**
   * ¬озвращает генетический код марсианина
   *
   * @return генетический код
   */
  T getCode();

  /**
   * ¬озвращает детей марсианина
   *
   * @return коллекци€ детей
   */
  Collection<? extends Martian<T>> getChildren();

  /**
   * ¬озвращает всех наслдеников марсианина
   *
   * @return коллекци€ всех наследников (детей, детей этих детей, ...)
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
   * ѕровер€ет, есть ли среди детей марсианина ребенок с данным генетическим
   * кодом
   *
   * @param value - значение генетического кода
   * @return есть ли такой ребенок
   */
  default boolean hasChildWithValue(final T value) {
    return keyInCollection(value, getChildren());
  }

  /**
   * ѕровер€ет, есть ли среди всех наслдеников марсианина ребенок с данным
   * генетическим кодом
   *
   * @param value - значение генетического кода
   * @return есть ли такой наследник
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
   * ѕровер€ет, есть ли пересечение среди наследников с данным марсианином
   *
   * @param martian - другой марсианин
   * @return есть ли пересечени€
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
   * ѕровер€ет, есть ли марсианин с данным генетическим кодом в коллекиции
   *
   * @param value      - генетический код
   * @param collection - коллекци€ марсиан
   * @return есть ли марсианин с данным генетическим кодом в коллекиции
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

