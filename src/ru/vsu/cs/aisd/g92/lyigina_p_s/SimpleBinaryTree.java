package ru.vsu.cs.aisd.g92.lyigina_p_s;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Реализация простейшего бинарного дерева
 */
public class SimpleBinaryTree<T extends Comparable<? super T>> implements BinaryTree<T> {

    protected class SimpleTreeNode implements BinaryTree.TreeNode<T> {
        public T value;
        public SimpleTreeNode left;
        public SimpleTreeNode right;
        public Color color = Color.BLACK;

        public SimpleTreeNode(T value, SimpleTreeNode left, SimpleTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public SimpleTreeNode(T value) {
            this(value, null, null);
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public TreeNode<T> getLeft() {
            return left;
        }

        @Override
        public TreeNode<T> getRight() {
            return right;
        }

        @Override
        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public void setColor(Color color) {
            this.color = color;
        }
    }

    protected SimpleTreeNode root = null;

    protected Function<String, T> fromStrFunc;
    protected Function<T, String> toStrFunc;

    public SimpleBinaryTree(Function<String, T> fromStrFunc, Function<T, String> toStrFunc) {
        this.fromStrFunc = fromStrFunc;
        this.toStrFunc = toStrFunc;
    }

    public SimpleBinaryTree(Function<String, T> fromStrFunc) {
        this(fromStrFunc, Object::toString);
    }

    public SimpleBinaryTree() {
        this(null);
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }

    public void clear() {
        root = null;
    }

    private T fromStr(String s) throws Exception {
        s = s.trim();
        if (s.length() > 0 && s.charAt(0) == '"') {
            s = s.substring(1);
        }
        if (s.length() > 0 && s.charAt(s.length() - 1) == '"') {
            s = s.substring(0, s.length() - 1);
        }
        if (fromStrFunc == null) {
            throw new Exception("Не определена функция конвертации строки в T");
        }
        return fromStrFunc.apply(s);
    }

    private static class IndexWrapper {
        public int index = 0;
    }

    private void skipSpaces(String bracketStr, IndexWrapper iw) {
        while (iw.index < bracketStr.length() && Character.isWhitespace(bracketStr.charAt(iw.index))) {
            iw.index++;
        }
    }

    private T readValue(String bracketStr, IndexWrapper iw) throws Exception {
        // пропуcкаем возможные пробелы
        skipSpaces(bracketStr, iw);
        if (iw.index >= bracketStr.length()) {
            return null;
        }
        int from = iw.index;
        boolean quote = bracketStr.charAt(iw.index) == '"';
        if (quote) {
            iw.index++;
        }
        while (iw.index < bracketStr.length() && (
                    quote && bracketStr.charAt(iw.index) != '"' ||
                    !quote && !Character.isWhitespace(bracketStr.charAt(iw.index)) && "(),".indexOf(bracketStr.charAt(iw.index)) < 0
               )) {
            iw.index++;
        }
        if (quote && bracketStr.charAt(iw.index) == '"') {
            iw.index++;
        }
        String valueStr = bracketStr.substring(from, iw.index);
        T value = fromStr(valueStr);
        skipSpaces(bracketStr, iw);
        return value;
    }

    private SimpleTreeNode fromBracketStr(String bracketStr, IndexWrapper iw) throws Exception {
        T parentValue = readValue(bracketStr, iw);
        SimpleTreeNode parentNode = new SimpleTreeNode(parentValue);
        if (bracketStr.charAt(iw.index) == '(') {
            iw.index++;
            skipSpaces(bracketStr, iw);
            if (bracketStr.charAt(iw.index) != ',') {
                parentNode.left = fromBracketStr(bracketStr, iw);
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) == ',') {
                iw.index++;
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) != ')') {
                parentNode.right = fromBracketStr(bracketStr, iw);
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) != ')') {
                throw new Exception(String.format("Ожидалось ')' [%d]", iw.index));
            }
            iw.index++;
        }

        return parentNode;
    }

    public void fromBracketNotation(String bracketStr) throws Exception {
        IndexWrapper iw = new IndexWrapper();
        SimpleTreeNode root = fromBracketStr(bracketStr, iw);
        if (iw.index < bracketStr.length()) {
            throw new Exception(String.format("Ожидался конец строки [%d]", iw.index));
        }
        this.root = root;
    }

    private boolean isBSTree() {
        T prev = null;
        for(T value : this) {
           if (prev != null)
               if (value.compareTo(prev) < 0)
                   return false;
           prev = value;
        }
        return true;
    }
/*
    private boolean isBSTreeRecursion1(TreeNode<T> node) {
        if ((node.getLeft() == null || (node.getLeft().getLeft() == null && node.getLeft().getRight() == null)) &&
                (node.getRight() == null || (node.getRight().getLeft() == null && node.getRight().getRight() == null)))
            return (node.getLeft() == null || node.getLeft().getValue().compareTo(node.getValue()) < 0) &&
                    (node.getRight() == null || node.getRight().getValue().compareTo(node.getValue()) > 0);

        return isBSTreeRecursion1(node.getLeft()) && isBSTreeRecursion1(node.getRight()) &&
                node.getLeft().getValue().compareTo(node.getValue()) < 0 &&
                node.getRight().getValue().compareTo(node.getValue()) > 0;
    }
*/
    private boolean isBSTreeRecursion(TreeNode<T> node, T lowerBound, T higherBound) {
        if (node == null)
            return true;
        return (lowerBound == null || node.getValue().compareTo(lowerBound) > 0) &&
                (higherBound == null || node.getValue().compareTo(higherBound) < 0) &&
                isBSTreeRecursion(node.getLeft(), lowerBound, node.getValue()) &&
                isBSTreeRecursion(node.getRight(), node.getValue(), higherBound);
    }

    public boolean solution() {
        for (TreeNode<T> a : BinaryTreeAlgorithms.inOrderNodes(root)) {
            for (TreeNode<T> b : BinaryTreeAlgorithms.inOrderNodes(root)) {
                if (a != b) {
                    T tmp = a.getValue();
                    a.setValue(b.getValue());
                    b.setValue(tmp);
                    if (isBSTreeRecursion(root, null, null)) {
                        a.setColor(Color.RED);
                        b.setColor(Color.RED);
                        return true;
                    }
                    b.setValue(a.getValue());
                    a.setValue(tmp);
                }
            }
        }
        return false;
    }

    public boolean solution1() {
        List<TreeNode<T>> mistakes = new ArrayList<>();
        TreeNode<T> prev = null;
        for (TreeNode<T> node : BinaryTreeAlgorithms.inOrderNodes(root)) {
            if (prev != null) {
                if (node.getValue().compareTo(prev.getValue()) < 0) {
                    mistakes.add(mistakes.size()>0 ? node : prev);
                }
            }
            prev = node;
        }
        if (mistakes.size() != 2)
            return false;
        T tmp = mistakes.get(0).getValue();
        mistakes.get(0).setValue(mistakes.get(1).getValue());
        mistakes.get(1).setValue(tmp);
        if (isBSTreeRecursion(root, null, null)) {
            mistakes.get(0).setColor(Color.RED);
            mistakes.get(1).setColor(Color.RED);
            return true;
        }
        mistakes.get(1).setValue(mistakes.get(0).getValue());
        mistakes.get(0).setValue(tmp);
        return false;
    }
}
