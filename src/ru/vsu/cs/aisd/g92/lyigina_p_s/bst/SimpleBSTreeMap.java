package ru.vsu.cs.aisd.g92.lyigina_p_s.bst;

/**
 * Реализация словаря на базе простого (наивного) дерева поиска
 *
 * @param <K>
 * @param <V>
 */
public class SimpleBSTreeMap<K extends Comparable<K>, V> implements BSTreeMap<K, V> {

    private final BSTree<MapTreeEntry<K, V>> tree = new SimpleBSTree<>();

    @Override
    public BSTree<MapTreeEntry<K, V>> getTree() {
        return tree;
    }
}
