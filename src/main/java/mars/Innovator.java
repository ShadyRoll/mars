package mars;

import java.util.ArrayList;
import java.util.Collection;

/* Попов Олег Олегович
 * БПИ197
 */

/**
 * Марсианин-инноватор
 *
 * @param <T> - тип генетического кода
 */
public class Innovator<T> implements Martian<T> {

  /**
   * Создает инноватора с данным генетическим кодом
   *
   * @param code - генетический код
   */
  public Innovator(T code) {
    this(code, null, null);
  }

  /**
   * Создает инноватора, задавая родителя, детей и генетический код
   *
   * @param code     - генетический код
   * @param children - дети
   * @param parent   - родитель
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
   * Возвращает родителя
   *
   * @return родитель
   */
  public Martian<T> getParent() {
    return parent;
  }

  /**
   * Возвращает детей
   *
   * @return коллекция детей
   */
  public Collection<Innovator<T>> getChildren() {
    return children;
  }

  /**
   * Возвращает генетический код
   *
   * @return генетический код
   */
  @Override
  public T getCode() {
    return code;
  }

  /**
   * Устанавливает значение генетического кода
   *
   * @param code - новый генетичский код
   */
  public void setCode(T code) {
    this.code = code;
  }

  /**
   * Устанавливает новую коллецию детей
   *
   * @param children - новая коллекция детей
   * @return успешна ли операция
   */
  public boolean setChildren(Collection<Innovator<T>> children) {
    // Проверим на циклы
    for (var child : children) {
      if (familyIntersect(child)) {
        return false;
      }
    }
    if (children.contains(this)) {
      return false;
    }
    // Проведем вставку всех новый детей
    Collection<Innovator<T>> prevChildren = this.children;
    this.children = new ArrayList<>();
    for (var child : children) {
      if (!addChild(child)) {
        // Если хотя бы 1 вставка неуспешна - отменяем все изменения детей
        this.children = prevChildren;
        return false;
      }
    }
    return true;
  }

  /**
   * Уставаливает родителя
   *
   * @param parent - новый родитель
   * @return успешна ли операция
   */
  public boolean setParent(Innovator<T> parent) {
    // Проверим на циклы
    if (parent != null && familyIntersect(parent)) {
      return false;
    }

    this.parent = parent;
    parent.addChild(this);
    return true;
  }

  /**
   * Добавляет ребенка к этому марсианину
   *
   * @param child - ребенок
   * @return успешна ли операция
   */
  public boolean addChild(Innovator<T> child) {
    // Проверим на пересечение (циклы, петли)
    if (familyIntersect(child)) {
      return false;
    }
    children.add(child);
    child.parent = this;
    return true;
  }

  /**
   * Удаляет ребенка
   *
   * @param child - ребенок, которого нужно удалить
   * @return успешна ли операция
   */
  public boolean delChild(Innovator<T> child) {
    // Проверим на пересечение (циклы, петли)
    if (!children.contains(child)) {
      return false;
    }
    children.remove(child);
    child.parent = null;
    return true;
  }

  /**
   * Генетический код
   */
  private T code;
  /**
   * Коллекция детей
   */
  private Collection<Innovator<T>> children;
  /**
   * Родитель
   */
  private Innovator<T> parent;
}
