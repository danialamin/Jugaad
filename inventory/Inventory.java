package inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<KeyItem> items = new ArrayList<>();
    private int maxCapacity = 10;

    public boolean addItem(KeyItem item) {
        if (isFull()) return false;
        items.add(item);
        return true;
    }

    public KeyItem removeItem(int itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemId() == itemId) {
                return items.remove(i);
            }
        }
        return null;
    }

    public boolean hasItem(int itemId) {
        for (KeyItem item : items) {
            if (item.getItemId() == itemId) return true;
        }
        return false;
    }

    public List<KeyItem> getItems() {
        return items;
    }

    public boolean isFull() {
        return items.size() >= maxCapacity;
    }
}
