package ru.vsu.cs.aisd.g92.lyigina_p_s.bst.avl;

import ru.vsu.cs.aisd.g92.lyigina_p_s.bst.BSTreeMap;
import ru.vsu.cs.aisd.g92.lyigina_p_s.bst.BSTree;

/**
 * Реализация словаря на базе АВЛ-деревьев
 *
 * @param <K>
 * @param <V>
 */
public class AVLTreeMap<K extends Comparable<K>, V> implements BSTreeMap<K, V> {

    private final BSTree<MapTreeEntry<K, V>> tree = new AVLTree<>();

    @Override
    public BSTree<MapTreeEntry<K, V>> getTree() {
        return tree;
    }
}
