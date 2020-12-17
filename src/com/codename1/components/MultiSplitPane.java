/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Codename One through http://www.codenameone.com/ if you
 * need additional information or have any questions.
 */
package com.codename1.components;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.geom.Rectangle;


public class MultiSplitPane extends Container {
    private boolean continuousLayout = true;

    /**
     * Creates a MultiSplitPane with it's LayoutManager set to
     * to an empty MultiSplitLayout.
     */
    public MultiSplitPane() {
        super(new MultiSplitLayout());
        setDraggable(true);
    }

    /**
     * Creates a MultiSplitPane with it's LayoutManager set to
     * to an empty MultiSplitLayout.
     */
    public MultiSplitPane(MultiSplitLayout.Node model){
        super(new MultiSplitLayout());
        getMultiSplitLayout().setModel(model);
        setDraggable(true);
    }

    /**
     * Add a component to this MultiSplitPane. The
     * <code>name</code> should match the name property of the Leaf
     * node that represents the bounds of <code>child</code>.  After
     * layoutContainer() recomputes the bounds of all of the nodes in
     * the model, it will set this child's bounds to the bounds of the
     * Leaf node with <code>name</code>.
     *
     * @param name the component name.
     * @param cmp the component to be added.
     * @see #removeComponent
     */
    public MultiSplitPane addComponent(String name, Component cmp){
        super.addComponent(cmp);
        getMultiSplitLayout().addLayoutComponent(name, cmp);
        return this;
    }

    public void removeComponent(Component cmp){
        getMultiSplitLayout().removeLayoutComponent(cmp);
        super.removeComponent(cmp);
    }

    /**
     * A convenience method that returns the layout manager cast
     * to MultiSplitLayout.
     *
     * @return this MultiSplitPane's layout manager
     * @see #setModel
     */
    public final MultiSplitLayout getMultiSplitLayout() {
        return (MultiSplitLayout)getLayout();
    }

    /**
     * A method that sets the MultiSplitLayout model.
     *
     * @param model the root of the MultiSplitLayout model
     * @see #getMultiSplitLayout
     * @see MultiSplitLayout#setModel
     */
    public final void setModel(MultiSplitLayout.Node model) {
        getMultiSplitLayout().setModel(model);
    }

    /**
     * Sets the value of the <code>continuousLayout</code> property.
     * If true, then the layout is revalidated continuously while
     * a divider is being dragged. The default value is true.
     *
     * @param continuousLayout value of the continuousLayout property
     * @see #isContinuousLayout
     */
    public void setContinuousLayout(boolean continuousLayout) {
        this.continuousLayout = continuousLayout;
    }

    /**
     * Returns true if dragging a divider only updates
     * the layout when the drag gesture ends (typically, when the
     * mouse button is released).
     *
     * @return the value of the <code>continuousLayout</code> property
     * @see #setContinuousLayout
     */
    public boolean isContinuousLayout() {
        return continuousLayout;
    }

    private boolean dragUnderway = false;
    private MultiSplitLayout.Divider dragDivider = null;
    private Rectangle initialDividerBounds = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private int dragMin = -1;
    private int dragMax = -1;

    private void startDrag(int x, int y) {
        MultiSplitLayout msl = getMultiSplitLayout();
        MultiSplitLayout.Divider divider = msl.dividerAt(x - getAbsoluteX(), y - getAbsoluteY());
        if (divider != null) {
            MultiSplitLayout.Node prevNode = divider.previousSibling();
            MultiSplitLayout.Node nextNode = divider.nextSibling();
            if ((prevNode == null) || (nextNode == null)) {
                dragUnderway = false;
            }
            else {
                initialDividerBounds = divider.getBounds();
                dragOffsetX = x - initialDividerBounds.getX();
                dragOffsetY = y - initialDividerBounds.getY();
                dragDivider  = divider;
                Rectangle prevNodeBounds = prevNode.getBounds();
                Rectangle nextNodeBounds = nextNode.getBounds();
                if (dragDivider.getParent().isRowLayout()) {
                    dragMin = prevNodeBounds.getX();
                    dragMax = nextNodeBounds.getX() + nextNodeBounds.getWidth();
                    dragMax -= dragDivider.getBounds().getWidth();
                }
                else {
                    dragMin = prevNodeBounds.getY();
                    dragMax = nextNodeBounds.getY() + nextNodeBounds.getHeight();
                    dragMax -= dragDivider.getBounds().getHeight();
                }
                getMultiSplitLayout().setFloatingDividers(false);
                dragUnderway = true;
            }
        }
        else {
            dragUnderway = false;
        }
    }

    private void updateDrag(int x, int y) {
        if (!dragUnderway) {
            return;
        }
        Rectangle oldBounds = dragDivider.getBounds();
        Rectangle bounds = new Rectangle(oldBounds);
        if (dragDivider.getParent().isRowLayout()) {
            bounds.setX(x - dragOffsetX);
            bounds.setX(Math.max(bounds.getX(), dragMin));
            bounds.setX(Math.min(bounds.getX(), dragMax));
        }
        else {
            bounds.setY(y - dragOffsetY);
            bounds.setY(Math.max(bounds.getY(), dragMin));
            bounds.setY(Math.min(bounds.getY(), dragMax));
        }
        dragDivider.setBounds(bounds);
        if (isContinuousLayout()) {
            revalidate();
            repaintDragLimits();
        }
        else {
            Rectangle rec = union(oldBounds, bounds);
            repaint(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());
        }
    }

    private void finishDrag() {
        if (dragUnderway) {
            clearDragState();
            if (!isContinuousLayout()) {
                revalidate();
                repaint();
            }
        }
    }

    private void clearDragState() {
        dragDivider = null;
        initialDividerBounds = null;
        dragOffsetX = 0;
        dragOffsetY = 0;
        dragMin = -1;
        dragMax = -1;
        dragUnderway = false;
    }

    private void repaintDragLimits() {
        Rectangle damageR = dragDivider.getBounds();
        if (dragDivider.getParent().isRowLayout()) {
            damageR.setX(dragMin);
            damageR.setWidth(dragMax - dragMin);
        }
        else {
            damageR.setY(dragMin);
            damageR.setHeight(dragMax - dragMin);
        }
        repaint(damageR.getX(), damageR.getY(), damageR.getWidth() , damageR.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isStickyDrag() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        startDrag(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pointerDragged(int x, int y) {
        updateDrag(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        finishDrag();
    }

    private static Rectangle union(Rectangle src1, Rectangle src2) {
        int x1 = Math.min(src1.getX(), src2.getX());
        int y1 = Math.min(src1.getY(), src2.getY());
        int x2 = Math.max(src1.getX() + src1.getWidth(), src2.getX() + src2.getWidth());
        int y2 = Math.max(src1.getY() + src1.getHeight(), src2.getY() + src2.getHeight());
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
}
