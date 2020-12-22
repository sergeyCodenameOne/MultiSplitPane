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

import java.util.ArrayList;
import java.util.List;


public class MultiSplitPane extends Container {
    private boolean continuousDrag = true;
    private Split root;

    /**
     * Create new instance of MultiSplitPane with the given Split root.
     *
     * @param root The root of the tree model hierarchy.
     * @see        Split
     */
    public MultiSplitPane(Split root) {
        super(new MultiSplitLayout());
        if(root == null){
            throw new IllegalArgumentException("Root can't be null");
        }
        this.root = root;
        setDraggable(true);
    }

    /**
     * set the root of the Nodes tree hierarchy.
     *
     * @param root the root to be set.
     * @see        Split
     */
    public void setRoot(Split root){
        this.root = root;
    }

    /**
     * Method that return the current root of the MultiSplitPane.
     *
     * @return the property value of <code>root<code/>
     * @see    Split
     */
    public Split getRoot(){
        return root;
    }

    /**
     * Method that checks the model given as root and add all its
     * Components to the MultiSplitPane Container.
     * Must be used after the root was set and before the MultiSplitPane
     * is showed on the screen.
     *
     * @return self for call chaining.
     */
    public MultiSplitPane build(){
        checkModel(root);
        removeAll();
        addComponentsToContainer(this, root);
        return this;
    }

    /**
     * Method that returns the property value of the <code>continuousDrag<code/>
     *
     * @return The property value of <code>continuousLayout</code>.
     * @see    #setContinuousDrag(boolean)
     */
    public boolean isContinuousDrag() {
        return continuousDrag;
    }

    /**
     * If true the container will revalidate itself continuously
     * while being dragged.
     * If false the container will revalidate itself only after the
     * the drag has been finished.
     *
     */
    public void setContinuousDrag(boolean continuousDrag) {
        this.continuousDrag = continuousDrag;
    }

    /**
     * A base class for all the node hierarchy tree model.
     */
    abstract static class Node{
        private Split parent = null;
        private Rectangle bounds = new Rectangle();
        private double weight = 0.0;

        public double getWeight() {
            return weight;
        }

        /**
         * Method that sets the new weight of the node.
         *
         * The weight property is decides how much of the
         * extra space in the Split will be given to that
         * child. If the extra space of the split is negative
         * (happens when the preferred size of all the children
         * is bigger than the size of the split that contains
         * them) then the weight will decides how much space to
         * take from the child.
         *
         * The weight must be (0 <= weight <= 1) and
         * the total weight of all children in the Split must
         * be less then 1.
         *
         * @param weight new weight of the Node.
         *
         * @return       self for call chaining.
         */
        public Node setWeight(double weight) {
            if ((weight < 0.0) || (weight > 1.0)) {
                throw new IllegalArgumentException("Invalid weight");
            }
            this.weight = weight;
            return this;
        }

        /**
         * returns the parent Node of this Node.
         *
         * @return The property value of the <code>parent</code>
         */
        public Split getParent() {
            return parent;
        }

        /**
         * Sets the parent Node of this Node.
         * @param parent parent Node.
         */
        public void setParent(Split parent) {
            this.parent = parent;
        }

        /**
         * Return the place on the screen of and the
         * size of that Node.
         *
         * @return property value of <code>bounds</code>
         */
        public Rectangle getBounds() {
            return new Rectangle(this.bounds);
        }

        /**
         * Sets the place on the screen and the size of
         * the Node.
         *
         * @param bounds new bounds of the node.
         */
        public void setBounds(Rectangle bounds) {
            if (bounds == null) {
                throw new IllegalArgumentException("null bounds");
            }
            this.bounds = new Rectangle(bounds);
        }

        /**
         * Returns the preferred size of the node dependent
         * on the child/children of the Node.
         *
         * @return the Preferred size of the node.
         */
        abstract Dimension getPreferredSize();
    }

    /**
     * Hold the children and defines if they be arranged into row/column.
     */
    public static class Split extends Node{
        private List<Node> children = new ArrayList<>();
        private final boolean rowSplit;

        /**
         * Create new instance of Split Node.
         * The Split become the Parent of all the children.
         *
         * The nodes must have at least 2 Leaf's.
         * Between every 2 Leaf's must be a Divider.
         *
         * @param rowSplit true for row split and false for column.
         * @param nodes the children Nodes of the Split.
         */
        public Split(boolean rowSplit, Node... nodes) {
            for (Node node : nodes){
                children.add(node);
                node.setParent(this);
            }
            this.rowSplit = rowSplit;
        }

        /**
         * Method that returns the current state of the split.
         * If true the children will be arranged into row..
         * If false the children will be arranged into column.
         * @return the property value of <code>rowSplit</code>
         */
        public boolean isRowSplit() {
            return rowSplit;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize(){
            int totalWidth = 0;
            int totalHeight = 0;
            if (rowSplit) {
                for(Node child : children) {
                    Dimension size = child.getPreferredSize();
                    totalWidth += size.getWidth();
                    totalHeight = Math.max(totalHeight, size.getHeight());
                }
            }
            else {
                for(Node child : children) {
                    Dimension size = child.getPreferredSize();
                    totalWidth = Math.max(totalWidth, size.getWidth());
                    totalHeight += size.getHeight();
                }
            }
            return new Dimension(totalWidth, totalHeight);
        }

        /**
         * Method that returns List of all children of that split.
         *
         * @return List of the Splits children.
         */
        public final List<Node> getChildren() {
            return children;
        }

        /**
         *
         * @param children the new children of the
         */
        public void setChildren(List<Node> children) {
            for(Node child : this.children){
                child.setParent(null);
            }
            this.children = new ArrayList<>(children);
            for(Node child : this.children){
                child.setParent(this);
            }
        }
    }

    /**
     * Node that represent Component.
     */
    public static class Leaf extends Node{
        private Component child;

        /**
         * Create new instance of Leaf that hold the given Component.
         * @param child Component to represent.
         */
        public Leaf(Component child) {
            this.child = child;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize(){
            return child.getPreferredSize();
        }

        /**
         * Method that returns the Component that the Node represent.
         *
         * @return the property value of <code>child</code>
         */
        public Component getChild() {
            return child;
        }

        /**
         * Sets a new Component that the Leaf will represent.
         * @param child the new Component
         */
        public void setChild(Component child) {
            this.child = child;
        }
    }

    /**
     * vertical/horizontal divider that will divide between avery
     * 2 Leaf's.
     */
    public static class Divider extends Node{
        private Component child;

        /**
         * Create new instance of Divider Node that hold the given Component
         * to divide between every 2 Leaf's.
         * Note: if the component is instance of Container by default the
         * container will will be set to ignore all pointer events.
         * If the user will decide to change it back so the Divider will not
         * respond to dragging.
         * @see #setIgnorePointerEvents(boolean)
         *
         * @param child Component to represent.
         */
        public Divider(Component child) {
            if (child instanceof Container){
                child.setIgnorePointerEvents(true);
            }
            this.child = child;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize(){
            return child.getPreferredSize();
        }

        /**
         * Returns the Component that the Divider represent.
         *
         * @return the property value of <code>child<code/>
         */
        public Component getChild() {
            return child;
        }

        /**
         * Sets a new Component that the Divider will represent.
         *
         * Note: if the component is instance of Container by default the
         * container will will be set to ignore all pointer events.
         * If the user will decide to change it back so the Divider will not
         * respond to dragging.
         * @see #setIgnorePointerEvents(boolean)
         *
         * @param child the new Component.
         */
        public void setChild(Component child) {
            this.child = child;
            if (child instanceof Container){
                child.setIgnorePointerEvents(true);
            }
        }
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

    private Divider getDividerAt(Node root, int x, int y) {
        if (root instanceof Divider) {
            Divider divider = (Divider)root;
            return (divider.getBounds().contains(x, y)) ? divider : null;
        }
        else if (root instanceof Split) {
            Split split = (Split)root;
            for(Node child : split.getChildren()) {
                if (child.getBounds().contains(x, y)) {
                    return getDividerAt(child, x, y);
                }
            }
        }
        return null;
    }

    private Divider dragDivider = null;
    private int lastX;
    private int lastY;

    private void startDrag(int x, int y) {
        Divider divider = getDividerAt(root, x - getAbsoluteX(), y - getAbsoluteY());
        if (divider != null) {
            dragDivider = divider;
            lastX = x;
            lastY = y;
            ((MultiSplitLayout)getLayout()).setFloatingDividers(true);
        }
    }

    private void updateDrag(int x, int y) {
        if (dragDivider != null) {
            Rectangle dividerOldBounds = dragDivider.getBounds();
            Rectangle dividerNewBounds = new Rectangle(dividerOldBounds);

            int dividerIndex = dragDivider.getParent().getChildren().indexOf(dragDivider);
            Rectangle prevLeafBounds = dragDivider.getParent().getChildren().get(dividerIndex - 1).getBounds();
            Rectangle nextLeafBounds = dragDivider.getParent().getChildren().get(dividerIndex + 1).getBounds();
            if (dragDivider.getParent().isRowSplit()){
                int xDragged = lastX - x;
                int minPosition = prevLeafBounds.getX();
                int maxPosition = nextLeafBounds.getWidth() + nextLeafBounds.getX() - dragDivider.getBounds().getWidth();
                int oldDividerX = dragDivider.getBounds().getX();
                int newDividerX = oldDividerX - xDragged;

                if (newDividerX < minPosition){
                    newDividerX = minPosition;
                }else if(newDividerX > maxPosition){
                    newDividerX = maxPosition;
                }else{
                    lastX = x;
                }
                dividerNewBounds.setX(newDividerX);
            }else{
                int yDragged = lastY - y;
                int minPosition = prevLeafBounds.getY();
                int maxPosition = nextLeafBounds.getHeight() + nextLeafBounds.getY() - dragDivider.getBounds().getHeight();
                int oldDividerY = dragDivider.getBounds().getY();
                int newDividerY = oldDividerY - yDragged;

                if (newDividerY < minPosition){
                    newDividerY = minPosition;
                }else if (newDividerY > maxPosition){
                    newDividerY = maxPosition;
                }else{
                    lastY = y;
                }
                dividerNewBounds.setY(newDividerY);
            }
            dragDivider.setBounds(dividerNewBounds);
            if (isContinuousDrag()) {
                revalidate();
            }
        }
    }

    private void finishDrag() {
        if (dragDivider != null) {
            dragDivider = null;
            if (!isContinuousDrag()) {
                revalidate();
            }
        }
    }

    static private void checkModel(Node root) {
        if (root instanceof Split) {
            List<Node> splitChildren = ((Split) root).getChildren();
            if (splitChildren.size() < 3) {
                throw new InvalidModelException("Split cannot hold less then 3 Nodes", root);
            }
            double totalWeight = 0.0;
            int childrenSize = splitChildren.size();
            for (int i = 0; i < childrenSize; i++) {
                Node child = splitChildren.get(i);
                if (i % 2 == 0) {
                    if (child instanceof Divider) {
                        throw new InvalidModelException("Expected Leaf or Split", root);
                    }
                } else {
                    if (!(child instanceof Divider)) {
                        throw new InvalidModelException("Expected Divider", root);
                    }
                }
                totalWeight += child.getWeight();
            }

            if (totalWeight > 1.0) {
                throw new InvalidModelException("Total children weight must be less then 1.0", root);
            }
        }
    }

    private static void addComponentsToContainer(Container cnt, Node root){
        if (root instanceof Divider){
            cnt.add(((Divider) root).getChild());
        }else if (root instanceof Leaf){
            cnt.add(((Leaf) root).getChild());
        }else{
            for (Node child : ((Split)root).getChildren())
            addComponentsToContainer(cnt, child);
        }
    }

    /**
     * Exception that will be thrown if the model was built incorrect.
     */
    public static class InvalidModelException extends RuntimeException {
        private final Node node;
        public InvalidModelException (String msg, Node node) {
            super(msg);
            this.node = node;
        }

        /**
         * @return the invalid Node.
         */
        public Node getNode() {
            return node;
        }
    }
}
