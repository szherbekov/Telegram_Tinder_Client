package liga.tinder.client.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ScrollableListWrapper {

    private final List<Profile> listToScroll;
    private int currentIndex = 0;

    public ScrollableListWrapper(List<Profile> listToScroll) {
        this.listToScroll = new LinkedList<>(listToScroll);
    }

    public ScrollableListWrapper(Set<Profile> profileSet) {
        listToScroll = new LinkedList<>(profileSet);
    }

    public Profile getCurrentProfile() {
        return listToScroll.get(currentIndex);
    }

    public Profile getNextProfile() {
        return listToScroll.get(++currentIndex);
    }

    public boolean isLast() {
        return currentIndex == listToScroll.size() - 1;
    }

    public int getSize() {
        return listToScroll.size();
    }

    public int getCurrent() {
        return currentIndex;
    }

    public boolean isEmpty() {
        return listToScroll.isEmpty();
    }

    public Profile getPreviousProfile() {
        return listToScroll.get(--currentIndex);
    }

    public boolean isFirst() {
        return currentIndex == 0;
    }

    public void resetCurrentIndex() {
        currentIndex = 0;
    }

    public void resetCurrentIndexFromLast() {
        currentIndex = listToScroll.size() - 1;
    }
}
