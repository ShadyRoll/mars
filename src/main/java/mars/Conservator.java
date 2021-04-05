package mars;

import java.util.ArrayList;
import java.util.Collection;

/* Попов Олег Олегович
 * БПИ197
 */

/**
 * Марсианин-консерватор
 *
 * @param <T> - тип генетического кода
 */
public class Conservator<T> implements Martian<T> {

  // Дефолтный конструктор

  /**
   * Создает консерватора на основе инноватора
   *
   * @param innovator - инноватор
   */
  public Conservator(Innovator<T> innovator) {
    code = innovator.getCode();

    // Новый массив детей
    Collection<Conservator<T>> newChildren = new ArrayList<>(
        innovator.getChildren().size());
    ArrayList<Innovator<T>> innovatorChildren =
        (ArrayList<Innovator<T>>) innovator.getChildren();

    // Переносим детей
    for (Innovator<T> innovatorChild : innovatorChildren) {
      // Вызываем конструктор в режиме Child-only
      newChildren.add(new Conservator<T>(innovatorChild,
          null, null, this));
    }
    children = newChildren;

    /* Если родитель есть, нделаем и его консерватором
     * (этот момент не был полноценно обговорен в условиях, разыне ассистены
     * говорили разное, поэтому я на всякий случай сделал более сложный вариант,
     * т.е. я превращаю в консерваторов всей родственников, а не только потомков)
     */
    if (innovator.getParent() != null) {
      // Вызываем конструктор в режиме Avoid-called-child
      parent = new Conservator<>((Innovator<T>) innovator.getParent(),
          innovator,
          this, null);
    } else {
      parent = null;
    }
  }

  /**
   * Создает консерватора на основе разлтичных переданных данных Я выделил 2
   * "режима" этого конструктора:
   * <p> 1. Child-only: рекурсивно превращает наследников в консерваторов
   * (проход вниз) </p>
   * <p> 2. Avoid-called-child: превращает родителя в консерватора, потом его *
   * наслдеников (проход вверх) </p>
   *
   * @param innovator              - инноватор, который становится
   *                               консерватором
   * @param calledChild            - наследник, который вызвал конструктор для
   *                               своего родителя
   * @param calledConservatorChild - наследник, который вызвал конструктор для
   *                               своего родителя (после превращения в
   *                               консерватора)
   * @param newParent              - родитель, который вызвал конструктор этого
   *                               своего ребенка
   */
  private Conservator(Innovator<T> innovator, Innovator<T> calledChild,
      Conservator<T> calledConservatorChild,
      Conservator<T> newParent) {
    code = innovator.getCode();

    // Дети инноватора
    ArrayList<Innovator<T>> innovatorChildren =
        (ArrayList<Innovator<T>>) innovator.getChildren();
    Collection<Conservator<T>> newChildren = new ArrayList<>(
        innovator.getChildren().size());

    // Если нас вызвал не ребенок, тогда родитель
    if (calledChild == null) {
      // Режим Child-only (проход вниз)
      for (Innovator<T> innovatorChild : innovatorChildren) {
        // Вызываем конструктор для всех наслдеников в этом же режиме
        newChildren.add(new Conservator<T>(innovatorChild,
            null, null, this));
      }
      // Обновляем данные
      children = newChildren;
      parent = newParent;
      // В данном режиме нам обязаны были передать родителя
      if (parent == null) {
        throw new NullPointerException(
            "Parent somehow is null in child-only constructor");
      }
    }
    // Иначе конструктор вызвал ребенок этого марсианина
    else {
      // Режим Avoid-called-child (проход вверх)
      for (int i = 0; i < innovatorChildren.size(); i++) {
        if (innovatorChildren.get(i) == calledChild) {
          // Ребенок, вызваший конструктор уже стал консерватором
          newChildren.add(calledConservatorChild);
        } else {
          // А вот остальных детей и их детей нужно сделать консерваторами
          newChildren.add(new Conservator<T>(innovatorChildren.get(i),
              null, null, this));
        }
      }
      children = newChildren;

      if (innovator.getParent() != null) {
        // Если и у этого марсианина есть родитель - продолжаем проход вверх
        parent = new Conservator<T>((Innovator<T>) innovator.getParent(),
            innovator, this, null);
      } else {
        // Иначе помечаем, что родителя нет
        parent = null;
      }
    }
  }

  /**
   * Возвращает родителя этого марсианина
   * @return родитель
   */
  @Override
  public Martian<T> getParent() {
    return parent;
  }

  /**
   * Возвращает коллекцию детей этого марсианина
   * @return коллекция детей
   */
  @Override
  public Collection<? extends Martian<T>> getChildren() {
    return children;
  }

  /**
   * Возвращает генетический код этого марсианина
   * @return генетический код
   */
  @Override
  public T getCode() {
    return code;
  }

  /**
   * Генетический код
   */
  final T code;
  /**
   * Дети
   */
  final Collection<Conservator<T>> children;
  /**
   * Родитель
   */
  final Conservator<T> parent;
}
