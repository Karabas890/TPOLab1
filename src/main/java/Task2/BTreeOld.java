package Task2;

import java.util.*;

class BTreeOld {
    private int MAX_KEYS; // Максимальное количество ключей в узле

    private Node root; // Корень дерева

    public BTreeOld() {
        MAX_KEYS = 3; // По умолчанию максимальное количество ключей в узле - 3
        root = new Node(true); // Корень дерева является листом
    }

    // Метод для изменения максимального количества ключей в узле
    public void setMaxKeys(int maxKeys) {
        if (maxKeys <= 0) {
            throw new IllegalArgumentException("Максимальное количество ключей должно быть больше 0");
        }
        if (maxKeys > 5) {
            throw new IllegalArgumentException("Максимальное количество ключей должно быть меньше 5");
        }
        MAX_KEYS = maxKeys; // Устанавливаем новое значение MAX_KEYS
        root = new Node(true); // Очистить дерево при изменении максимального количества ключей
    }

    // Метод для добавления элемента в дерево
    public void insert(int key) {
        if (root.keys.size() == MAX_KEYS) { // Если корень переполнен
            Node newRoot = new Node(false); // Создаем новый корень
            newRoot.children.add(root); // Старый корень становится дочерним элементом нового корня
            split(newRoot, 0); // Разделяем старый корень
            root = newRoot; // Новый корень становится корнем дерева
        }
        insertNonFull(root, key); // Добавляем ключ в дерево
    }

    // Вставка ключа в не переполненный узел
    private void insertNonFull(Node node, int key) {
        int i = node.keys.size() - 1;
        if (node.isLeaf) { // Если узел является листом
            while (i >= 0 && key < node.keys.get(i)) {
                i--; // Ищем правильную позицию для ключа
            }
            node.keys.add(i + 1, key); // Вставляем ключ
        } else { // Если узел не является листом
            while (i >= 0 && key < node.keys.get(i)) {
                i--; // Ищем правильную позицию для ключа
            }
            i++; // Позиция для вставки
            if (node.children.get(i).keys.size() == MAX_KEYS) { // Если дочерний узел переполнен
                split(node, i); // Разделяем дочерний узел
                if (key > node.keys.get(i)) { // Если ключ больше нового среднего значения, переходим к следующему дочернему узлу
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key); // Рекурсивно вставляем ключ в дочерний узел
        }
    }

    // Разделение узла на два
    private void split(Node parent, int index) {
        Node child = parent.children.get(index); // Получаем дочерний узел
        Node newChild = new Node(child.isLeaf); // Новый дочерний узел
        int mid = child.keys.size() / 2; // Находим середину ключей
        parent.keys.add(index, child.keys.get(mid)); // Вставляем в родительский узел ключ из середины
        parent.children.add(index + 1, newChild); // Добавляем новый дочерний узел в родительский узел

        // Перемещаем ключи из старого дочернего узла в новый
        for (int i = mid + 1; i < child.keys.size(); i++) {
            newChild.keys.add(child.keys.get(i));
        }
        child.keys.subList(mid, child.keys.size()).clear(); // Удаляем перемещенные ключи из старого дочернего узла

        // Если дочерний узел не является листом, перемещаем также дочерние узлы
        if (!child.isLeaf) {
            for (int i = mid + 1; i < child.children.size(); i++) {
                newChild.children.add(child.children.get(i));
            }
            child.children.subList(mid + 1, child.children.size()).clear(); // Удаляем перемещенные дочерние узлы из старого узла
        }
    }

    // Поиск элемента в дереве
    public boolean search(int key) {
        return search(root, key); // Рекурсивный поиск
    }

    // Рекурсивный метод для поиска элемента в узле
    private boolean search(Node node, int key) {
        int i = 0;
        // Ищем правильную позицию для ключа
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }
        if (i < node.keys.size() && key == node.keys.get(i)) { // Если ключ найден
            return true;
        }
        if (node.isLeaf) { // Если узел - лист и ключ не найден, возвращаем false
            return false;
        }
        return search(node.children.get(i), key); // Рекурсивно ищем в дочернем узле
    }

    // Удаление элемента из дерева
    public void delete(int key) {
        delete(root, key); // Рекурсивное удаление
    }

    // Рекурсивный метод для удаления элемента из узла
    private void delete(Node node, int key) {
        int i = 0;
        // Ищем ключ в узле
        while (i < node.keys.size() && key > node.keys.get(i)) {
            i++;
        }
        if (i < node.keys.size() && key == node.keys.get(i)) { // Если ключ найден
            if (node.isLeaf) { // Если узел является листом
                node.keys.remove(i); // Удаляем ключ из листа
            } else { // Если узел не является листом
                Node child = node.children.get(i);
                if (child.keys.size() > MAX_KEYS / 2) { // Если дочерний узел имеет достаточно ключей
                    node.keys.set(i, child.keys.get(child.keys.size() - 1)); // Перемещаем на место удаляемого ключа ключ из дочернего узла
                    delete(child, child.keys.get(child.keys.size() - 1)); // Рекурсивно удаляем ключ из дочернего узла
                } else { // Если дочерний узел не имеет достаточно ключей
                    Node sibling = node.children.get(i + 1); // Получаем соседний узел
                    child.keys.add(node.keys.get(i)); // Перемещаем ключ из родителя в дочерний узел
                    node.keys.set(i, sibling.keys.get(0)); // Перемещаем ключ из соседа в родитель
                    sibling.keys.remove(0); // Удаляем ключ из соседнего узла
                    delete(child, key); // Рекурсивно удаляем ключ
                }
            }
        } else if (!node.isLeaf) { // Если ключ не найден в текущем узле, рекурсивно ищем в дочерних узлах
            delete(node.children.get(i), key);
        }
    }

    // Очистить дерево
    public void clear() {
        root = new Node(true); // Устанавливаем новый корень
    }

    // Вспомогательный класс для узлов дерева
    private static class Node {
        boolean isLeaf; // Признак того, что узел является листом
        List<Integer> keys; // Список ключей в узле
        List<Node> children; // Список дочерних узлов

        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new ArrayList<>(); // Инициализация списка ключей
            this.children = new ArrayList<>(); // Инициализация списка дочерних узлов
        }
    }

    // Метод для вывода дерева
    public void printTree() {
        printTree(root, "", true); // Рекурсивный вызов для печати дерева
    }

    // Рекурсивный метод для обхода и вывода дерева
    private void printTree(Node node, String indent, boolean isLast) {
        System.out.println(indent + (isLast ? "└── " : "├── ") + node.keys); // Вывод ключей узла
        indent += isLast ? "    " : "│   "; // Обновляем отступы для вывода
        for (int i = 0; i < node.children.size(); i++) {
            printTree(node.children.get(i), indent, i == node.children.size() - 1); // Рекурсивно выводим дочерние узлы
        }
    }
}
