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

import com.codename1.ui.Container;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;

import java.util.Iterator;

/**
 * The MultiSplitLayout that works only with MultiSplitPane and recursively
 * layout the given model.
 * The model of this layout manager is Nodes tree model. The root must be Split that
 * can hold Leaf's(that represents Components), Dividers(that also represent
 * components), and Splits(that holds a group of Nodes).
 */
public class MultiSplitLayout extends Layout {
    private boolean floatingDividers = false;

    /**
     * Returns the floatingDividers property value.
     *
     * @return the property value of <code> </code>
     */
    public boolean isFloatingDividers() {
        return floatingDividers;
    }

    /**
     * Decides if the the Model will be arranged by the
     * Components preferred size or by the dividers dragging.
     * by default set to false and when the user drag one of
     * the dividers the value automatically sets to true.
     *
     * @param floatingDividers the new value of floatingDividers property.
     */
    public void setFloatingDividers(boolean floatingDividers) {
        this.floatingDividers = floatingDividers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(Container parent) {
        MultiSplitPane.Split root = ((MultiSplitPane)parent).getRoot();
        Dimension size = root.getPreferredSize();
        return sizeWithPadding(parent, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layoutContainer(Container parent) {
        MultiSplitPane.Node root =((MultiSplitPane) parent).getRoot();
        Dimension size = new Dimension(parent.getWidth(), parent.getHeight());
        Style parentStyle = parent.getStyle();
        int width = size.getWidth() - parentStyle.getHorizontalPadding();
        int height = size.getHeight() - parentStyle.getVerticalPadding();
        Rectangle bounds = new Rectangle(parentStyle.getPaddingLeft(parent.isRTL()), parentStyle.getPaddingTop(), width, height);
        layoutNodesBounds(root, bounds, false, 0);
        setComponentsBounds(root, bounds);
    }

    private void layoutNodesBounds(MultiSplitPane.Node root, Rectangle bounds, boolean allocateExtraSpace, int extraSpace) {
        if (root instanceof MultiSplitPane.Leaf) {
            root.setBounds(bounds);
        }
        else if (root instanceof MultiSplitPane.Split) {
            MultiSplitPane.Split split = (MultiSplitPane.Split)root;
            Iterator<MultiSplitPane.Node> splitChildren = split.getChildren().iterator();
            Rectangle childBounds;

            if (split.isRowSplit()) {
                int x = bounds.getX();
                while(splitChildren.hasNext()) {
                    MultiSplitPane.Node splitChild = splitChildren.next();
                    MultiSplitPane.Divider dividerChild = splitChildren.hasNext() ? (MultiSplitPane.Divider)(splitChildren.next()) : null;
                    double childWidth;
                    if (!isFloatingDividers()) {
                        childWidth = splitChild.getPreferredSize().getWidth();
                        if(extraSpace != 0){
                            childWidth += extraSpace * splitChild.getWeight();
                        }
                    }else{
                        if (dividerChild != null) {
                            childWidth = dividerChild.getBounds().getX() - x;
                        }
                        else {
                            childWidth = split.getBounds().getX() + split.getBounds().getWidth() - x;
                            int childPreferredWidth = splitChild.getPreferredSize().getWidth();
                            if (childWidth > childPreferredWidth){
                                childWidth = childPreferredWidth;
                            }
                        }
                    }
                    childBounds = new Rectangle(x, bounds.getY(), (int)childWidth, bounds.getHeight());
                    layoutNodesBounds(splitChild, childBounds, false, 0);

                    if (!isFloatingDividers() && (dividerChild != null)) {
                        double dividerX = childBounds.getX() + childBounds.getWidth();
                        Rectangle dividerBounds = new Rectangle((int)dividerX, bounds.getY(), dividerChild.getPreferredSize().getWidth(), bounds.getHeight());
                        dividerChild.setBounds(dividerBounds);
                    }

                    if(dividerChild != null){
                        x = dividerChild.getBounds().getX() + dividerChild.getBounds().getWidth();
                        Rectangle dividerNewBounds = new Rectangle(dividerChild.getBounds());
                        dividerNewBounds.setHeight(bounds.getHeight());
                        dividerNewBounds.setY(bounds.getY());
                        dividerChild.setBounds(dividerNewBounds);
                    }else{
                        x = splitChild.getBounds().getX() + splitChild.getBounds().getWidth();
                    }
                }
                extraSpace = bounds.getWidth() - x;
            } else {
                int y = bounds.getY();
                while(splitChildren.hasNext()) {
                    MultiSplitPane.Node splitChild = splitChildren.next();
                    MultiSplitPane.Divider dividerChild =
                            (splitChildren.hasNext()) ? (MultiSplitPane.Divider)(splitChildren.next()) : null;
                    int childHeight;
                    if (!isFloatingDividers()) {
                        childHeight = splitChild.getPreferredSize().getHeight();
                        if (extraSpace != 0){
                            childHeight += extraSpace * splitChild.getWeight();
                        }
                    }else{
                        if (dividerChild != null) {
                            childHeight = dividerChild.getBounds().getY() - y;
                        }
                        else {
                            childHeight = split.getBounds().getY() + split.getBounds().getHeight() - y;
                            int childPreferredHeight = splitChild.getPreferredSize().getHeight();
                            if (childHeight > childPreferredHeight){
                                childHeight = childPreferredHeight;
                            }
                        }
                    }
                    childBounds = new Rectangle(bounds.getX(), y, bounds.getWidth(), childHeight);
                    layoutNodesBounds(splitChild, childBounds, false, 0);

                    if (!isFloatingDividers() && (dividerChild != null)) {
                        int dividerY = childBounds.getY() + childBounds.getHeight();
                        Rectangle dividerBounds = new Rectangle(bounds.getX(), dividerY, bounds.getWidth(), dividerChild.getPreferredSize().getHeight());
                        dividerChild.setBounds(dividerBounds);
                    }
                    if (dividerChild != null) {
                        y = dividerChild.getBounds().getY() + dividerChild.getBounds().getHeight();
                        Rectangle dividerNewBounds = new Rectangle(dividerChild.getBounds());
                        dividerNewBounds.setWidth(bounds.getWidth());
                        dividerNewBounds.setX(bounds.getX());
                        dividerChild.setBounds(dividerNewBounds);
                    }else{
                        y = splitChild.getBounds().getY() + splitChild.getBounds().getHeight();
                    }
                }
                extraSpace = bounds.getHeight() - y;
            }
            split.setBounds(bounds);
            if (extraSpace != 0 && !allocateExtraSpace){
                layoutNodesBounds(root, bounds, true, extraSpace);
            }
        }
    }

    private void setComponentsBounds(MultiSplitPane.Node root, Rectangle bounds) {
        if (root instanceof MultiSplitPane.Leaf){
            ((MultiSplitPane.Leaf) root).getChild().setX(bounds.getX());
            ((MultiSplitPane.Leaf) root).getChild().setY(bounds.getY());
            ((MultiSplitPane.Leaf) root).getChild().setWidth(bounds.getWidth());
            ((MultiSplitPane.Leaf) root).getChild().setHeight(bounds.getHeight());
        }
        if (root instanceof MultiSplitPane.Divider){
            ((MultiSplitPane.Divider) root).getChild().setX(bounds.getX());
            ((MultiSplitPane.Divider) root).getChild().setY(bounds.getY());
            ((MultiSplitPane.Divider) root).getChild().setWidth(bounds.getWidth());
            ((MultiSplitPane.Divider) root).getChild().setHeight(bounds.getHeight());
        }else if (root instanceof MultiSplitPane.Split){
            for (MultiSplitPane.Node child : ((MultiSplitPane.Split) root).getChildren()){
                setComponentsBounds(child, child.getBounds());
            }
        }
    }

    private static Dimension sizeWithPadding(Container parent, Dimension size) {
        Style parentStyle = parent.getAllStyles();
        int width = size.getWidth() + parentStyle.getHorizontalPadding();
        int height = size.getHeight() + parentStyle.getVerticalPadding();
        return new Dimension(width, height);
    }
}
