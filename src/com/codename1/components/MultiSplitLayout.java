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
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;

import java.util.*;

/**
 * The MultiSplitLayout layout manager recursively arranges its
 * components in row and column groups called "Splits".  Elements of
 * the layout are separated by Components called "Dividers". The overall
 * layout is defined with a simple tree model whose nodes are
 * instances of MultiSplitLayout.Split, MultiSplitLayout.Divider,
 * and MultiSplitLayout.Leaf. Named Leaf nodes represent the space
 * allocated to a component that was added with a constraint that
 * matches the Leaf's name.  Extra space is distributed
 * among row/column siblings according to their 0.0 to 1.0 weight.
 * If no weights are specified then the last sibling always gets
 * all of the extra space, or space reduction.
 *
 * <p>
 * Although MultiSplitLayout can be used with any Container, it's
 * the default layout manager for MultiSplitPane.  MultiSplitPane
 * supports interactively dragging the Dividers.
 *
 * @author Sergey Gerashenko - CodenameOne
 * @see MultiSplitPane
 */
public class MultiSplitLayout extends Layout {
    private final Map<String, Component> childMap = new HashMap<>();
    private Node model;
    private boolean floatingDividers = true;

    /**
     * Create a MultiSplitLayout with a default model with a single
     * Leaf node named "default".
     *
     * #see setModel
     */
    public MultiSplitLayout() {
        this(new Leaf("default"));
    }

    /**
     * Create a MultiSplitLayout with the specified model.
     *
     * #see setModel
     */
    public MultiSplitLayout(Node model) {
        this.model = model;
    }

    /**
     * Return the root of the tree model.
     *
     * @return the value of the model property.
     * @see #setModel
     */
    public Node getModel() {
        return model;
    }

    /**
     * Set the root of the tree. The model can be a Split node
     * (the typical case) or a Leaf.
     *
     * @param model the root of the tree model.
     * @throws IllegalArgumentException if model is a Divider or null
     * @see #getModel
     */
    public void setModel(Node model) {
        if ((model == null) || (model instanceof Divider))
            throw new IllegalArgumentException("invalid model");
        this.model = model;
    }

    /**
     * Add a component to this MultiSplitLayout. The
     * <code>name</code> should match the name property of the Leaf
     * node that represents the bounds of <code>child</code>. After
     * layoutContainer() recomputes the bounds of all of the nodes in
     * the model, it will set this child's bounds to the bounds of the
     * Leaf node with <code>name</code>. Note: if a component was already
     * added with the same name, this method does not remove it from
     * its parent.
     *
     * @param name identifies the Leaf node that defines the child's bounds
     * @param child the component to be added
     * @see #removeLayoutComponent
     */
    public void addLayoutComponent(String name, Component child) {
        if (name == null)
            throw new IllegalArgumentException("name not specified");
        childMap.put(name, child);
    }

    /**
     * Removes the specified component from the layout.
     *
     * @param child the component to be removed
     * @see #addLayoutComponent
     */
    public void removeLayoutComponent(Component child) {
        String name = child.getName();
        if (name != null) {
            childMap.remove(name);
        }
    }

    /**
     * @return the value of the floatingDividers property
     * @see #setFloatingDividers
     */
    public boolean isFloatingDividers() {
        return floatingDividers;
    }

    /**
     * If true, Leaf node bounds match the corresponding component's
     * preferred size and Splits are resized accordingly.
     * If false then the Dividers define the bounds of the adjacent
     * Split and Leaf nodes. Typically this property is set to false
     * after the (MultiSplitPane) user has dragged a Divider.
     *
     * @see #isFloatingDividers
     */
    public void setFloatingDividers(boolean floatingDividers) {
        this.floatingDividers = floatingDividers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(Container parent) {
        Dimension size = preferredNodeSize(getModel());
        return sizeWithInsets(parent, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layoutContainer(Container parent) {
        checkLayout(getModel());
        Dimension size = new Dimension(parent.getWidth(), parent.getHeight());
        Style parentStyle = parent.getStyle();
        int width = size.getWidth() - parentStyle.getHorizontalPadding();
        int height = size.getHeight() - parentStyle.getVerticalPadding();
        Rectangle bounds = new Rectangle(parentStyle.getPaddingLeft(parent.isRTL()), parentStyle.getPaddingTop(), width, height);
        layout1(getModel(), bounds);
        layout2(getModel(), bounds);
    }

    private void checkLayout(Node root) {
        if (root instanceof Split) {
            Split split = (Split)root;
            if (split.getChildren().size() <= 2) {
                throwInvalidLayout("Split must have > 2 children", root);
            }
            Iterator<Node> splitChildren = split.getChildren().iterator();
            double weight = 0.0;
            while(splitChildren.hasNext()) {
                Node splitChild = splitChildren.next();
                if (splitChild instanceof Divider) {
                    throwInvalidLayout("expected a Split or Leaf Node", splitChild);
                }
                if (splitChildren.hasNext()) {
                    Node dividerChild = splitChildren.next();
                    if (!(dividerChild instanceof Divider)) {
                        throwInvalidLayout("expected a Divider Node", dividerChild);
                    }
                }
                weight += splitChild.getWeight();
                checkLayout(splitChild);
            }
            if (weight > 1.0) {
                throwInvalidLayout("Split children's total weight > 1.0", root);
            }
        }
    }

    private void layout1(Node root, Rectangle bounds) {
        if (root instanceof Leaf) {
            root.setBounds(bounds);
        }
        else if (root instanceof Split) {
            Split split = (Split)root;
            Iterator<Node> splitChildren = split.getChildren().iterator();
            Rectangle childBounds;

            if (split.isRowLayout()) {
                double x = bounds.getX();
                while(splitChildren.hasNext()) {
                    Node splitChild = splitChildren.next();
                    Divider dividerChild = splitChildren.hasNext() ? (Divider)(splitChildren.next()) : null;
                    double childWidth;
                    if (isFloatingDividers()) {
                        childWidth = preferredNodeSize(splitChild).getWidth();
                    }else{
                        if (dividerChild != null) {
                            childWidth = dividerChild.getBounds().getX() - x;
                        }
                        else {
                            childWidth = split.getBounds().getX() + split.getBounds().getWidth() - x;
                        }
                    }

                    childBounds = boundsWithXandWidth(bounds, x, childWidth);
                    layout1(splitChild, childBounds);

                    if (isFloatingDividers() && (dividerChild != null)) {
                        double dividerX = childBounds.getX() + childBounds.getWidth();
                        Rectangle dividerBounds = boundsWithXandWidth(bounds, dividerX, preferredNodeSize(dividerChild).getWidth());
                        dividerChild.setBounds(dividerBounds);
                    }

                    if (dividerChild != null) {
                        x = dividerChild.getBounds().getX() + dividerChild.getBounds().getWidth();
                    }
                }
            } else {
                double y = bounds.getY();
                while(splitChildren.hasNext()) {
                    Node splitChild = splitChildren.next();
                    Divider dividerChild =
                            (splitChildren.hasNext()) ? (Divider)(splitChildren.next()) : null;
                    double childHeight;

                    if (isFloatingDividers()) {
                        childHeight = preferredNodeSize(splitChild).getHeight();
                    }else{
                        if (dividerChild != null) {
                            childHeight = dividerChild.getBounds().getY() - y;
                        }
                        else {
                            childHeight = split.getBounds().getY() + split.getBounds().getHeight() - y;
                        }
                    }

                    childBounds = boundsWithYandHeight(bounds, y, childHeight);
                    layout1(splitChild, childBounds);

                    if (isFloatingDividers() && (dividerChild != null)) {
                        double dividerY = childBounds.getY() + childBounds.getHeight();
                        Rectangle dividerBounds = boundsWithYandHeight(bounds, dividerY, preferredNodeSize(dividerChild).getHeight());
                        dividerChild.setBounds(dividerBounds);
                    }
                    if (dividerChild != null) {
                        y = dividerChild.getBounds().getY() + dividerChild.getBounds().getHeight();
                    }
                }
            }
            minimizeSplitBounds(split, bounds);
        }
    }

    private void layout2(Node root, Rectangle bounds) {
        if (root instanceof Split) {
            Split split = (Split)root;
            boolean grow = split.isRowLayout()
                    ? (split.getBounds().getWidth() <= bounds.getWidth())
                    : (split.getBounds().getHeight() <= bounds.getHeight());
            if (grow) {
                root.setBounds(bounds);
                layoutGrow(split, bounds);
            }
            else {
                layoutShrink(split, bounds);
            }
        } else {
            Component child = childForNode(root);
            if (child != null) {
                child.setX(bounds.getX());
                child.setY(bounds.getY());
                child.setSize(bounds.getSize());
            }
            root.setBounds(bounds);
        }
    }

    private void layoutShrink(Split split, Rectangle bounds) {
        Rectangle splitBounds = split.getBounds();
        ListIterator<Node> splitChildren = split.getChildren().listIterator();

        if (split.isRowLayout()) {
            int totalWidth = 0;
            int minWeightedWidth = 0;
            int totalWeightedWidth = 0;
            for(Node splitChild : split.getChildren()) {
                int nodeWidth = splitChild.getBounds().getWidth();
                int nodeMinWidth = Math.min(nodeWidth, preferredNodeSize(splitChild).getWidth());
                totalWidth += nodeWidth;
                if (splitChild.getWeight() > 0.0) {
                    minWeightedWidth += nodeMinWidth;
                    totalWeightedWidth += nodeWidth;
                }
            }
            double x = bounds.getX();
            double extraWidth = splitBounds.getWidth() - bounds.getWidth();
            double availableWidth = extraWidth;
            boolean onlyShrinkWeightedComponents = (totalWeightedWidth - minWeightedWidth) > extraWidth;

            while(splitChildren.hasNext()) {
                Node splitChild = splitChildren.next();
                Rectangle splitChildBounds = splitChild.getBounds();
                double minSplitChildWidth = preferredNodeSize(splitChild).getWidth();
                double splitChildWeight = (onlyShrinkWeightedComponents)
                        ? splitChild.getWeight()
                        : (splitChildBounds.getWidth() / (double)totalWidth);

                if (!splitChildren.hasNext()) {
                    double newWidth =  Math.max(minSplitChildWidth, bounds.getX() + bounds.getWidth() - x);
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
                    layout2(splitChild, newSplitChildBounds);
                }
                else if ((availableWidth > 0.0) && (splitChildWeight > 0.0)) {
                    double allocatedWidth = Math.rint(splitChildWeight * extraWidth);
                    double oldWidth = splitChildBounds.getWidth();
                    double newWidth = Math.max(minSplitChildWidth, oldWidth - allocatedWidth);
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
                    layout2(splitChild, newSplitChildBounds);
                    availableWidth -= (oldWidth - splitChild.getBounds().getWidth());
                }
                else {
                    double existingWidth = splitChildBounds.getWidth();
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, existingWidth);
                    layout2(splitChild, newSplitChildBounds);
                }
                x = splitChild.getBounds().getX() + splitChild.getBounds().getWidth();
            }
        }else {
            int totalHeight = 0;
            int minWeightedHeight = 0;
            int totalWeightedHeight = 0;
            for(Node splitChild : split.getChildren()) {
                int nodeHeight = splitChild.getBounds().getHeight();
                int nodeMinHeight = Math.min(nodeHeight, preferredNodeSize(splitChild).getHeight());
                totalHeight += nodeHeight;
                if (splitChild.getWeight() > 0.0) {
                    minWeightedHeight += nodeMinHeight;
                    totalWeightedHeight += nodeHeight;
                }
            }

            double y = bounds.getY();
            double extraHeight = splitBounds.getHeight() - bounds.getHeight();
            double availableHeight = extraHeight;
            boolean onlyShrinkWeightedComponents =
                    (totalWeightedHeight - minWeightedHeight) > extraHeight;

            while(splitChildren.hasNext()) {
                Node splitChild = splitChildren.next();
                Rectangle splitChildBounds = splitChild.getBounds();
                double minSplitChildHeight = preferredComponentSize(splitChild).getHeight();
                double splitChildWeight = (onlyShrinkWeightedComponents)
                        ? splitChild.getWeight()
                        : (splitChildBounds.getHeight() / (double)totalHeight);

                if (!splitChildren.hasNext()) {
                    double oldHeight = splitChildBounds.getHeight();
                    double newHeight =  Math.max(minSplitChildHeight, bounds.getY() + bounds.getHeight() - y);
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
                    layout2(splitChild, newSplitChildBounds);
                    availableHeight -= (oldHeight - splitChild.getBounds().getHeight());
                }
                else if ((availableHeight > 0.0) && (splitChildWeight > 0.0)) {
                    double allocatedHeight = Math.rint(splitChildWeight * extraHeight);
                    double oldHeight = splitChildBounds.getHeight();
                    double newHeight = Math.max(minSplitChildHeight, oldHeight - allocatedHeight);
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
                    layout2(splitChild, newSplitChildBounds);
                    availableHeight -= (oldHeight - splitChild.getBounds().getHeight());
                }
                else {
                    double existingHeight = splitChildBounds.getHeight();
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, existingHeight);
                    layout2(splitChild, newSplitChildBounds);
                }
                y = splitChild.getBounds().getY() + splitChild.getBounds().getHeight();
            }
        }
        minimizeSplitBounds(split, bounds);
    }

    private void layoutGrow(Split split, Rectangle bounds) {
        Rectangle splitBounds = split.getBounds();
        ListIterator<Node> splitChildren = split.getChildren().listIterator();
        Node lastWeightedChild = split.lastWeightedChild();

        if (split.isRowLayout()) {
            double x = bounds.getX();
            double extraWidth = bounds.getWidth() - splitBounds.getWidth();
            double availableWidth = extraWidth;

            while(splitChildren.hasNext()) {
                Node splitChild = splitChildren.next();
                Rectangle splitChildBounds = splitChild.getBounds();
                double splitChildWeight = splitChild.getWeight();

                if (!splitChildren.hasNext()) {
                    double newWidth = bounds.getX() + bounds.getWidth() - x;
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
                    layout2(splitChild, newSplitChildBounds);
                }
                else if ((availableWidth > 0.0) && (splitChildWeight > 0.0)) {
                    double allocatedWidth = splitChild.equals(lastWeightedChild) ? availableWidth :
                            Math.rint(splitChildWeight * extraWidth);
                    double newWidth = splitChildBounds.getWidth() + allocatedWidth;
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, newWidth);
                    layout2(splitChild, newSplitChildBounds);
                    availableWidth -= allocatedWidth;
                }
                else {
                    double existingWidth = splitChildBounds.getWidth();
                    Rectangle newSplitChildBounds = boundsWithXandWidth(bounds, x, existingWidth);
                    layout2(splitChild, newSplitChildBounds);
                }
                x = splitChild.getBounds().getX() + splitChild.getBounds().getWidth();
            }
        }else {
            double y = bounds.getY();
            double extraHeight = bounds.getY() + bounds.getHeight() - splitBounds.getHeight();
            double availableHeight = extraHeight;

            while(splitChildren.hasNext()) {
                Node splitChild = splitChildren.next();
                Rectangle splitChildBounds = splitChild.getBounds();
                double splitChildWeight = splitChild.getWeight();

                if (!splitChildren.hasNext()) {
                    double newHeight = bounds.getY() + bounds.getHeight() - y;
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
                    layout2(splitChild, newSplitChildBounds);
                }
                else if ((availableHeight > 0.0) && (splitChildWeight > 0.0)) {
                    double allocatedHeight = (splitChild.equals(lastWeightedChild))
                            ? availableHeight
                            : Math.rint(splitChildWeight * extraHeight);
                    double newHeight = splitChildBounds.getHeight() + allocatedHeight;
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, newHeight);
                    layout2(splitChild, newSplitChildBounds);
                    availableHeight -= allocatedHeight;
                }
                else {
                    double existingHeight = splitChildBounds.getHeight();
                    Rectangle newSplitChildBounds = boundsWithYandHeight(bounds, y, existingHeight);
                    layout2(splitChild, newSplitChildBounds);
                }
                y = splitChild.getBounds().getY() + splitChild.getBounds().getHeight();
            }
        }
    }

    private Component childForNode(Node node) {
        if (node instanceof Leaf) {
            String name = ((Leaf)node).getName();
            return (name != null) ? childMap.get(name) : null;
        }else if (node instanceof Divider){
            String name = ((Divider)node).getName();
            return (name != null) ? childMap.get(name) : null;
        }
        return null;
    }

    private Dimension preferredComponentSize(Node node) {
        Component child = childForNode(node);
        return (child != null) ? child.getPreferredSize() : new Dimension(0, 0);
    }

    private Dimension preferredNodeSize(Node root) {
        if(root instanceof Split) {
            Split split = (Split)root;
            List<Node> splitChildren = split.getChildren();
            int width = 0;
            int height = 0;
            if (split.isRowLayout()) {
                for(Node splitChild : splitChildren) {
                    Dimension size = preferredNodeSize(splitChild);
                    width += size.getWidth();
                    height = Math.max(height, size.getHeight());
                }
            }
            else {
                for(Node splitChild : splitChildren) {
                    Dimension size = preferredNodeSize(splitChild);
                    width = Math.max(width, size.getWidth());
                    height += size.getHeight();
                }
            }
            return new Dimension(width, height);
        }
        else{
            return preferredComponentSize(root);
        }
    }

    private Dimension sizeWithInsets(Container parent, Dimension size) {
        Style parentStyle = parent.getAllStyles();
        int width = size.getWidth() + parentStyle.getHorizontalPadding();
        int height = size.getHeight() + parentStyle.getVerticalPadding();
        return new Dimension(width, height);
    }

    private Rectangle boundsWithYandHeight(Rectangle bounds, double y, double height) {
        Rectangle r = new Rectangle();
        r.setBounds(bounds.getX(), (int)y, bounds.getWidth(), (int)height);
        return r;
    }

    private Rectangle boundsWithXandWidth(Rectangle bounds, double x, double width) {
        Rectangle r = new Rectangle();
        r.setBounds((int)x, bounds.getY(), (int)width, bounds.getHeight());
        return r;
    }

    private void minimizeSplitBounds(Split split, Rectangle bounds) {
        Rectangle splitBounds = new Rectangle(bounds);
        List<Node> splitChildren = split.getChildren();
        Node lastChild = splitChildren.get(splitChildren.size() - 1);
        Rectangle lastChildBounds = lastChild.getBounds();
        if (split.isRowLayout()) {
            int lastChildMaxX = lastChildBounds.getX() + lastChildBounds.getWidth();
            splitBounds.setX(lastChildMaxX + splitBounds.getX());
            splitBounds.setY(bounds.getY() + bounds.getHeight() + splitBounds.getY());
        }
        else {
            int lastChildMaxY = lastChildBounds.getY() + lastChildBounds.getHeight();
            splitBounds.setX(bounds.getX() + bounds.getWidth() + splitBounds.getX());
            splitBounds.setY(lastChildMaxY + splitBounds.getY());
        }
        split.setBounds(splitBounds);
    }

    private Divider dividerAt(Node root, int x, int y) {
        if (root instanceof Divider) {
            Divider divider = (Divider)root;
            return (divider.getBounds().contains(x, y)) ? divider : null;
        }
        else if (root instanceof Split) {
            Split split = (Split)root;
            for(Node child : split.getChildren()) {
                if (child.getBounds().contains(x, y)) {
                    return dividerAt(child, x, y);
                }
            }
        }
        return null;
    }

    /**
     * Return the Divider whose bounds contain the specified
     * point, or null if there isn't one.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the Divider at x,y
     */
    public Divider dividerAt(int x, int y) {
        return dividerAt(getModel(), x, y);
    }

    /**
     * The specified Node is either the wrong type or was configured
     * incorrectly.
     */
    public static class InvalidLayoutException extends RuntimeException {
        private final Node node;
        public InvalidLayoutException (String msg, Node node) {
            super(msg);
            this.node = node;
        }

        /**
         * @return the invalid Node.
         */
        public Node getNode() { return node; }
    }

    private void throwInvalidLayout(String msg, Node node) {
        throw new InvalidLayoutException(msg, node);
    }

    /**
     * Base class for the nodes that model a MultiSplitLayout.
     */
    public static abstract class Node {
        private Split parent = null;
        private Rectangle bounds = new Rectangle();
        private double weight = 0.0;

        /**
         * Returns the Split parent of this Node, or null.
         *
         * @return the value of the parent property.
         * @see #setParent
         */
        public Split getParent() { return parent; }

        /**
         * Set the value of this Node's parent property.  The default
         * value of this property is null.
         *
         * @param parent a Split or null.
         * @see #getParent
         */
        public void setParent(Split parent) {
            this.parent = parent;
        }

        /**
         * Returns the bounding Rectangle for this Node.
         *
         * @return the value of the bounds property.
         * @see #setBounds
         */
        public Rectangle getBounds() {
            return new Rectangle(this.bounds);
        }

        /**
         * Set the bounding Rectangle for this node.  The value of
         * bounds may not be null.  The default value of bounds
         * is equal to <code>new Rectangle(0,0,0,0)</code>.
         *
         * @param bounds the new value of the bounds property
         * @throws IllegalArgumentException if bounds is null
         * @see #getBounds
         */
        public void setBounds(Rectangle bounds) {
            if (bounds == null) {
                throw new IllegalArgumentException("null bounds");
            }
            this.bounds = new Rectangle(bounds);
        }

        /**
         * Value between 0.0 and 1.0 used to compute how much space
         * to add to this sibling when the layout grows or how
         * much to reduce when the layout shrinks.
         *
         * @return the value of the weight property
         * @see #setWeight
         */
        public double getWeight() { return weight; }

        /**
         * The weight property is a between 0.0 and 1.0 used to
         * compute how much space to add to this sibling when the
         * layout grows or how much to reduce when the layout shrinks.
         * If rowLayout is true then this node's width grows
         * or shrinks by (extraSpace * weight).  If rowLayout is false,
         * then the node's height is changed.  The default value
         * of weight is 0.0.
         *
         * @param weight a double between 0.0 and 1.0
         * @see #getWeight
         * @see MultiSplitLayout#layoutContainer
         * @throws IllegalArgumentException if weight is not between 0.0 and 1.0
         */
        public void setWeight(double weight) {
            if ((weight < 0.0) || (weight > 1.0)) {
                throw new IllegalArgumentException("invalid weight");
            }
            this.weight = weight;
        }

        /**
         * Return the Node that comes after this one in the parent's
         * list of children, or null.  If this node's parent is null,
         * or if it's the last child, then return null.
         *
         * @return the Node that comes after this one in the parent's list of children.
         * @see #previousSibling
         * @see #getParent
         */
        public Node nextSibling() {
            return siblingAtOffset(+1);
        }

        /**
         * Return the Node that comes before this one in the parent's
         * list of children, or null.  If this node's parent is null,
         * or if it's the last child, then return null.
         *
         * @return the Node that comes before this one in the parent's list of children.
         * @see #nextSibling
         * @see #getParent
         */
        public Node previousSibling() {
            return siblingAtOffset(-1);
        }

        private Node siblingAtOffset(int offset) {
            Split parent = getParent();
            if (parent == null) { return null; }
            List<Node> siblings = parent.getChildren();
            int index = siblings.indexOf(this);
            if (index == -1) { return null; }
            index += offset;
            return ((index > -1) && (index < siblings.size())) ? siblings.get(index) : null;
        }
    }

    /**
     * Defines a vertical or horizontal subdivision into two or more
     * tiles.
     */
    public static class Split extends Node {
        private List<Node> children = Collections.emptyList();
        private boolean rowLayout = true;

        /**
         * Returns true if the this Split's children are to be
         * laid out in a row: all the same height, left edge
         * equal to the previous Node's right edge.  If false,
         * children are laid on in a column.
         *
         * @return the value of the rowLayout property.
         * @see #setRowLayout
         */
        public boolean isRowLayout() { return rowLayout; }

        /**
         * Set the rowLayout property.  If true, all of this Split's
         * children are to be laid out in a row: all the same height,
         * each node's left edge equal to the previous Node's right
         * edge.  If false, children are laid on in a column.  Default
         * value is true.
         *
         * @param rowLayout true for horizontal row layout, false for column
         * @see #isRowLayout
         */
        public void setRowLayout(boolean rowLayout) {
            this.rowLayout = rowLayout;
        }

        /**
         * Returns this Split node's children. The returned value
         * is not a reference to the Split's internal list of children
         *
         * @return the value of the children property.
         * @see #setChildren
         */
        public List<Node> getChildren() {
            return new ArrayList<>(children);
        }

        /**
         * Set's the children property of this Split node. The parent
         * of each new child is set to this Split node, and the parent
         * of each old child (if any) is set to null. This method
         * defensively copies the incoming List. Default value is
         * an empty List.
         *
         * @param children List of children
         * @see #getChildren
         * @throws IllegalArgumentException if children is null
         */
        public void setChildren(List<Node> children) {
            if (children == null) {
                throw new IllegalArgumentException("children must be a non-null List");
            }
            for(Node child : this.children) {
                child.setParent(null);
            }
            this.children = new ArrayList<>(children);
            for(Node child : this.children) {
                child.setParent(this);
            }
        }

        /**
         * Convenience method that returns the last child whose weight
         * is > 0.0.
         *
         * @return the last child whose weight is > 0.0.
         * @see #getChildren
         * @see Node#getWeight
         */
        public final Node lastWeightedChild() {
            List<Node> children = getChildren();
            Node weightedChild = null;
            for(Node child : children) {
                if (child.getWeight() > 0.0) {
                    weightedChild = child;
                }
            }
            return weightedChild;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            int nChildren = getChildren().size();
            StringBuffer sb = new StringBuffer("MultiSplitLayout.Split");
            sb.append(isRowLayout() ? " ROW [" : " COLUMN [");
            sb.append(nChildren + ((nChildren == 1) ? " child" : " children"));
            sb.append("] ");
            sb.append(getBounds());
            return sb.toString();
        }
    }

    /**
     * Models a Component child.
     */
    public static class Leaf extends Node {
        private String name;

        /**
         * Create a Leaf node with the specified name can not
         * be null.
         *
         * @param name value of the Leaf's name property
         * @throws IllegalArgumentException if name is null
         */
        public Leaf(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }

        /**
         * Return the Leaf's name.
         *
         * @return the value of the name property.
         * @see #setName
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of the name property. Name may not be null.
         *
         * @param name value of the name property
         * @throws IllegalArgumentException if name is null
         */
        public void setName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer("MultiSplitLayout.Leaf");
            sb.append(" \"");
            sb.append(getName());
            sb.append("\"");
            sb.append(" weight=");
            sb.append(getWeight());
            sb.append(" ");
            sb.append(getBounds());
            return sb.toString();
        }
    }

    /**
     * Models a single vertical/horizontal divider.
     *
     * Note: If the Component that the divider represents should
     * be draggable, the Component should ignore pointer events.
     * this can be achieved with setIgnorePointerEvents() on any
     * Component.
     */
    public static class Divider extends Node {
        private String name;

        /**
         * Create a Divider node with the specified name can not
         * be null.
         *
         * @param name value of the Divider's name property
         * @throws IllegalArgumentException if name is null
         */
        public Divider(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }

        /**
         * Return the divider's name.
         *
         * @return the value of the name property.
         * @see #setName
         */
        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }

        /**
         *{@inheritDoc}
         */
        public String toString() {
            StringBuffer sb = new StringBuffer("MultiSplitLayout.Divider");
            sb.append(" \"");
            sb.append(getName());
            sb.append("\"");
            sb.append(" weight=");
            sb.append(getWeight());
            sb.append(" ");
            sb.append(getBounds());
            return sb.toString();
        }
    }

    /**
     * Print the tree with enough detail for simple debugging.
     */
    public static void printModel(Node root) {
        printModel("", root);
    }

    private static void printModel(String indent, Node root) {
        if (root instanceof Split) {
            Split split = (Split)root;
            System.out.println(indent + split);
            for(Node child : split.getChildren()) {
                printModel(indent + "  ", child);
            }
        }
        else {
            System.out.println(indent + root);
        }
    }
}
