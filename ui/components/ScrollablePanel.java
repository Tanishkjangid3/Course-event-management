package com.campusevents.ui.components;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * A custom JPanel that implements Scrollable and forces getScrollableTracksViewportWidth()
 * to return true. This guarantees that pages wrap cleanly and never cause horizontal scrollbars.
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    public ScrollablePanel() {
        super();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 64;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Crucial: Forces width to match viewport width, eliminating horizontal scrollbars
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}

