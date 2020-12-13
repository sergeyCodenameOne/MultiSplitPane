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

import com.codename1.ui.*;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;


/**
 * A Multi Split Pane component.
 * <p>Multi split pane is a container that splits its available space for two Components and provides a draggable divider
 * between them. The MultiSplitPane can divide multiple times. The division can be horizontal and vertical, if the orientation is
 * #HORIZONTAL_SPLIT then the components are laid out horizontally (side by side with a vertical bar as a divider).
 * If the orientation is #VERTICAL_SPLIT, then the components are laid out vertically (one above
 * the other)</p>
 *
 * <p>The bar divider bar includes arrows to collapse and expand the divider.</p>

 * @author Sergey Gerashenko.
 */
public class MultiSplitPane extends Container {
    private Split root;

    /**
     * Constant used for orientation.
     */
    public static final int HORIZONTAL_SPLIT = 0;

    /**
     * Constant used for orientation.
     */
    public static final int VERTICAL_SPLIT = 1;

    /**
     * Creates a new MultiSplitPane Container that split itself into two parts, one for each component given.
     *
     * @param leftTop left/top component of the split.
     * @param rightBottom right/bottom component of the split.
     * @param orientation The orientation.  One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
     *
     * @return the newly created container.
     */
    public static MultiSplitPane split(Component leftTop, Component rightBottom, int orientation){
        Split split = new Split(leftTop, rightBottom, orientation);
        return new MultiSplitPane(split);
    }


    /**
     * split the container into two parts, one is the old root and other is the new Component
     *
     * @param cmp the new component for the split.
     * @param orientation the orientation of the new split. One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
     * @param isTopOrLeft decides if the new component will be on the top/left side of the division or the bottom/right side.
     * @return this for call chaining.
     */
    public MultiSplitPane split(Component cmp, int orientation, boolean isTopOrLeft){
        removeComponent(root);
        Split root = isTopOrLeft ?  new Split(cmp, getRoot(), orientation) :
                                    new Split(getRoot(), cmp, orientation);
        setRoot(root);
        return this;
    }

    private MultiSplitPane(Split root){
        super(new BorderLayout());
        this.root = root;
        add(BorderLayout.CENTER, root);
    }


    /**
     * function that return the first Split container in the hierarchy.
     *
     * @return the root of the MultiSplitPane.
     */
    public final Split getRoot(){
        return root;
    }

    /**
     * function that set the first Split container in the hierarchy.
     */
    private void setRoot(Split root){
        add(BorderLayout.CENTER, root);
    }

    /**
     * Container that splits its available space into three parts, two for the component given, and one fo the divider between them.
     * the Split can have horizontal orientation or vertical orientation.
     */
    public static class Split extends Container{
        private final Divider divider;
        private final Component leftTop;
        private final Component rightBottom;
        private double ratio;
        private double minRatio;
        private double maxRatio;

        private static final double DEFAULT_RATIO = 0.5;
        private static final double DEFAULT_MIN_RATIO = 0;
        private static final double DEFAULT_MAX_RATIO = 1;

        private Split(Component leftTop, Component rightBottom ,int orientation) {
            this.leftTop = leftTop;
            this.rightBottom = rightBottom;
            this.minRatio = DEFAULT_MIN_RATIO;
            this.maxRatio = DEFAULT_MAX_RATIO;
            this.ratio = DEFAULT_RATIO;
            setUIID("MultiSplit");
            divider = new Divider(orientation);
            setLayout(new SplitLayout());
            add(leftTop);
            add(divider);
            add(rightBottom);
        }

        /**
         * set the min ratio of the Split.
         * @param minRatio new min ratio of the split (0 <= minRatio <= maxRatio);
         *
         * @return Self for chaining.
         */
        public Split setMinRatio(double minRatio){
            if ( ((minRatio < 0.0) || (minRatio > maxRatio))){
                throw new IllegalArgumentException("invalid Ratio");
            }
            if (ratio < minRatio) {
                ratio = minRatio;
            }
            this.minRatio = minRatio;
            return this;
        }

        /**
         * return the maximum ratio of the Split.
         *
         * @return
         */
        public double getMinRatio(){
            return minRatio;
        }

        /**
         * set the max ratio of the Split.
         * @param maxRatio new max ratio of the split (minRatio <= maxRatio <= 1);
         *
         * @return Self for chaining.
         */
        public Split setMaxRatio(double maxRatio){
            if ( ((maxRatio < minRatio) || (maxRatio > 1.0))){
                throw new IllegalArgumentException("invalid ratio");
            }
            if (ratio > maxRatio) {
                ratio = maxRatio;
            }
            this.maxRatio = maxRatio;
            return this;
        }

        /**
         * return the minimum ratio of the Split.
         *
         * @return
         */
        public double getMaxRatio(){
            return maxRatio;
        }

        /**
         * return the current ratio of the split.
         *
         * @return
         */
        public double getRatio() {
            return ratio;
        }

        /**
         * sets new ratio for the Split.
         *
         * @param ratio the new ratio of the Split (minRatio <= ratio <= maxRatio).
         * @return Self for chaining.
         */
        public Split setRatio(double ratio) {
            if ((ratio < minRatio) || (ratio > maxRatio)) {
                throw new IllegalArgumentException("invalid ratio");
            }
            this.ratio = ratio;
            return this;
        }

        /**
         * return the orientation of the Split, One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
         *
         * @return
         */
        public int getOrientation() {
            return divider.getOrientation();
        }

        /**
         * return the left/Top child of the Split depends on its orientation.
         *
         * @return
         */
        public Component getLeftTopChild() {
            return leftTop;
        }

        /**
         * return the right/bottom child of the Split depends on its orientation.
         *
         * @return
         */
        public Component getRightBottomChild() {
            return rightBottom;
        }

        /**
         * sets the dividers UIID of the split.
         *
         * @param id new UIID of the divider.
         * @return Self for chaining.
         */
        public Split setDividerUIID(String id){
            divider.setUIID(id);
            return this;
        }

        /**
         * Set whether to show the expand button on the divider. Default is {@literal true}.
         *
         * @param show {@literal true} to show the expand button.  {@literal false} to hide him.
         * @return Self for chaining.
         */
        public Split showExpandButton(boolean show){
            divider.showExpandButton(show);
            return this;
        }

        /**
         * Set whether to show/hide the collapse button on the divider. Default is {@literal true}.
         *
         * @param show {@literal true} to show the collapse button.  {@literal false} to hide him.
         * @return Self for chaining.
         */
        public Split showCollapseButton(boolean show){
            divider.showCollapseButton(show);
            return this;
        }

        /**
         * Set whether to show/hide the drag label on the divider. Default is {@literal true}.
         *
         * @param show {@literal true} to show the collapse button.  {@literal false} to hide him.
         * @return Self for chaining.
         */
        public Split showDragLabel(boolean show){
            divider.showDragLabel(show);
            return this;
        }

        /**
        * Sets the UIID to use for the expand button.  Default is "Label"
        *
        * @param id The UIID to use for the expand button.
        * @return Self for chaining.
        */
        public Split setExpandButtonUIID(String id){
            divider.setExpandButtonUIID(id);
            return this;
        }

        /**
         * Sets the UIID to use for the collapse button.  Default is "Label"
         *
         * @param id The UIID to use for the collapse button.
         * @return Self for chaining.
         */
        public Split setCollapseButtonUIID(String id){
            divider.setCollapseButtonUIID(id);
            return this;
        }

        /**
         * Sets the UIID to use for the drag label.  Default is "Label"
         *
         * @param id The UIID to use for the drag label.
         * @return Self for chaining.
         */
        public Split setDragLabelUIID(String id){
            divider.setDragLabelUIID(id);
            return this;
        }


        /**
         * Sets the icon to use for the expand button.
         *
         * @param icon the new Image for the button.
         * @return Self for chaining.
         */
        public Split setExpandButtonIcon(Image icon){
            divider.setExpandIcon(icon);
            return this;
        }

        /**
         * Sets the icon to use for the collapse button.
         *
         * @param icon the new Image for the button.
         * @return Self for chaining.
         */
        public Split setCollapseButtonIcon(Image icon){
            divider.setCollapseIcon(icon);
            return this;
        }

        /**
         * Sets the icon to use for the drag label.
         *
         * @param icon the new Image for the label.
         * @return Self for chaining.
         */
        public Split setDragLabelIcon(Image icon){
            divider.setDragLabelIcon(icon);
            return this;
        }

        /**
         * returns the divider of the Split.
         *
         * @return
         */
        private Divider getDivider() {
            return divider;
        }

        /**
         * class that responsible for arranging the Components inside the Split container.
         */
        private static class SplitLayout extends Layout {

            /**
             * {@inheritDoc}
             */
            @Override
            public Dimension getPreferredSize(Container parent) {
                Split split = (Split) parent;
                Component leftTop = split.getLeftTopChild();
                Component rightBottom = split.getRightBottomChild();
                int width;
                int height;
                Dimension topLeftSize = leftTop.getPreferredSize();
                Dimension rightBottomSize = rightBottom.getPreferredSize();

                if (split.getOrientation() == VERTICAL_SPLIT) {
                    width = Math.max(topLeftSize.getWidth(), rightBottomSize.getWidth());
                    height = topLeftSize.getHeight() + rightBottomSize.getHeight() + split.getDivider().getHeight();
                } else {
                    width = topLeftSize.getWidth() + rightBottomSize.getWidth() + split.getDivider().getWidth();
                    height = Math.max(topLeftSize.getHeight(), rightBottomSize.getHeight());
                }
                return new Dimension(width, height);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void layoutContainer(Container parent) {
                Dimension size = new Dimension(parent.getWidth(), parent.getHeight());
                Style parentStyle = parent.getStyle();
                int paddingTop = parentStyle.getPaddingTop();
                int paddingBottom = parentStyle.getPaddingBottom();
                int paddingRight = parentStyle.getPaddingRight(parent.isRTL());
                int paddingLeft = parentStyle.getPaddingLeft(parent.isRTL());

                int width = size.getWidth() - paddingRight - paddingLeft;
                int height = size.getHeight() - paddingBottom - paddingTop;

                Rectangle bounds = new Rectangle(paddingLeft, paddingTop, width, height);
                layout((Split)parent, bounds);
            }

            private void layout(Split split, Rectangle bounds) {
                if (split.getOrientation() == VERTICAL_SPLIT) {
                    Component topChild = split.getLeftTopChild();
                    Divider divider = split.getDivider();
                    Component bottomChild = split.getRightBottomChild();
                    int y = bounds.getY();
                    int x = bounds.getX();
                    Dimension totalChildrenSize = new Dimension(bounds.getWidth(), bounds.getHeight() - divider.getPreferredH());

                    Dimension topChildDim = new Dimension(totalChildrenSize.getWidth(), (int) (totalChildrenSize.getHeight() * split.getRatio()));
                    topChild.setSize(topChildDim);
                    topChild.setX(x);
                    topChild.setY(y);
                    y += topChildDim.getHeight();

                    divider.setX(x);
                    divider.setY(y);
                    divider.setSize(new Dimension(bounds.getWidth(), divider.getPreferredH()));
                    y += divider.getHeight();

                    Dimension bottomChildDim = new Dimension(totalChildrenSize.getWidth(), (int) (totalChildrenSize.getHeight() * (1 - split.getRatio())));
                    bottomChild.setSize(bottomChildDim);
                    bottomChild.setX(x);
                    bottomChild.setY(y);

                    if(topChild instanceof Split){
                        layoutContainer((Split)topChild);
                    }

                    if(bottomChild instanceof Split){
                        layoutContainer((Split)bottomChild);
                    }
                } else {
                    Component leftChild = split.getLeftTopChild();
                    Divider divider = split.getDivider();
                    Component rightChild = split.getRightBottomChild();
                    int x = bounds.getX();
                    int y = bounds.getY();
                    Dimension totalChildrenSize = new Dimension(bounds.getWidth() - divider.getPreferredW(), bounds.getHeight());

                    Dimension leftChildDim = new Dimension(((int) (totalChildrenSize.getWidth() * split.getRatio())), totalChildrenSize.getHeight());
                    leftChild.setSize(leftChildDim);
                    leftChild.setX(x);
                    leftChild.setY(y);
                    x += leftChildDim.getWidth();

                    divider.setX(x);
                    divider.setY(y);
                    divider.setSize(new Dimension(divider.getPreferredW(), bounds.getHeight()));
                    x += divider.getWidth();


                    Dimension rightChildDim = new Dimension(((int) (totalChildrenSize.getWidth() * (1 - split.getRatio()))), totalChildrenSize.getHeight());
                    rightChild.setSize(rightChildDim);
                    rightChild.setX(x);
                    rightChild.setY(y);

                    if(leftChild instanceof Split){
                        layoutContainer((Split)leftChild);
                    }

                    if(rightChild instanceof Split){
                        layoutContainer((Split)rightChild);
                    }
                }
            }
        }

        /**
         * The divider class used by the Split Container to separate between two components.
         */
        private static class Divider extends Container {
            private boolean showDivider = true;
            private int lastX, lastY;
            private boolean inDrag;
            private final int orientation;
            private final Button btnCollapse;
            private final Button btnExpand;
            private final Label dragLabel;

            private static final String EXPAND_BUTTON_UIID = "Label";
            private static final String COLLAPSE_BUTTON_UIID = "Label";
            private static final String DRAG_HANDLE_UIID = "Label";

            private Divider(int orientation) {
                super(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
                this.orientation = orientation;

                this.getAllStyles().setBorder(createBorder());

                btnCollapse = new Button();
                btnCollapse.setUIID(COLLAPSE_BUTTON_UIID);
                btnCollapse.setIcon(getDefaultCollapseIcon(orientation));
                btnCollapse.getAllStyles().setPadding(0,0,0,0);
                btnCollapse.getAllStyles().setMargin(0,0,0,0);
                btnCollapse.addActionListener(e->{
                    double newRatio;
                    if(((Split)getParent()).getRatio() <= 0.5){
                        newRatio = ((Split)getParent()).getMinRatio();
                    }else{
                        newRatio = Math.max(((Split) getParent()).getMinRatio(), 0.5);

                    }
                    ((Split)getParent()).setRatio(newRatio);
                    this.getParent().animateLayout(250);
                });

                btnExpand = new Button();
                btnExpand.setUIID(EXPAND_BUTTON_UIID);
                btnExpand.getAllStyles().setPadding(0,0,0,0);
                btnExpand.getAllStyles().setMargin(0,0,0,0);
                btnExpand.setIcon(getDefaultExpandIcon(orientation));
                btnExpand.addActionListener(e->{
                    double newRatio;
                    if(((Split)getParent()).getRatio() < 0.5){
                        newRatio = Math.min(((Split) getParent()).getMaxRatio(), 0.5);
                    }else{
                        newRatio = ((Split)getParent()).getMaxRatio();
                    }
                    ((Split)getParent()).setRatio(newRatio);
                    this.getParent().animateLayout(250);
                });

                dragLabel = new Label();
                dragLabel.setUIID(DRAG_HANDLE_UIID);
                dragLabel.getAllStyles().setPadding(0,0,0,0);
                dragLabel.getAllStyles().setMargin(0,0,0,0);
                dragLabel.setIcon(getDefaultDragIcon(orientation));
                dragLabel.setIgnorePointerEvents(true);

                add(BorderLayout.CENTER, dragLabel);
                if (orientation == VERTICAL_SPLIT){
                    add(BorderLayout.WEST, BoxLayout.encloseX(btnCollapse, btnExpand));
                }else{
                    add(BorderLayout.NORTH, BoxLayout.encloseY(btnCollapse, btnExpand));
                }
                setDraggable(true);
            }

            /**
             * Return the orientation of the divider.  One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
             *
             * @return
             */
            private int getOrientation(){
                return orientation;
            }

            /**
             * Set whether to show the expand button on the divider. Default is {@literal true}.
             *
             * @param show {@literal true} to show the expand button.  {@literal false} to hide him.
             */
            private void showExpandButton(boolean show){
                btnExpand.setVisible(show);
                btnExpand.setHidden(!show);
            }

            /**
             * Set whether to show the collapse button on the divider. Default is {@literal true}.
             *
             * @param show {@literal true} to show the expand button.  {@literal false} to hide him.
             */
            private void showCollapseButton(boolean show){
                btnCollapse.setVisible(show);
                btnCollapse.setHidden(!show);
            }

            /**
             * Set whether to show the drag label on the divider. Default is {@literal true}.
             *
             * @param show {@literal true} to show the expand button.  {@literal false} to hide him.
             */
            private void showDragLabel(boolean show){
                dragLabel.setVisible(show);
                dragLabel.setHidden(!show);
            }

            /**
             * sets the UIID of the collapse button.
             *
             * @param id new UIID of the button.
             */
            private void setCollapseButtonUIID(String id){
                btnCollapse.setUIID(id);
            }

            /**
             * sets the UIID of the expand button.
             *
             * @param id new UIID of the button.
             */
            private void setExpandButtonUIID(String id){
                btnExpand.setUIID(id);
            }

            /**
             * sets the UIID of the drag label.
             *
             * @param id new UIID of the button.
             */
            private void setDragLabelUIID(String id){
                dragLabel.setUIID(id);
            }

            /**
             * Sets the icon of the expand button.
             *
             * @param icon the new Image for the button.
             */
            private void setCollapseIcon(Image icon){
                btnCollapse.setIcon(icon);
            }

            /**
             * Sets the icon of the expand button.
             *
             * @param icon the new Image for the button.
             */
            private void setExpandIcon(Image icon){
                btnExpand.setIcon(icon);
            }

            /**
             * Sets the icon of the drag label.
             *
             * @param icon the new Image for the button.
             */
            private void setDragLabelIcon(Image icon) {
                dragLabel.setIcon(icon);
            }

            /**
             * Set whether to show the divider. Default is {@literal true}.
             *
             * @param show {@literal true} to show the divider.  {@literal false} to hide him.
             */
            private void showDivider(boolean show){
                if (showDivider != show){
                    if (show){
                        setHidden(true);
                        setVisible(false);
                    }else{
                        setHidden(false);
                        setVisible(true);
                    }
                    showDivider = show;
                    revalidate();
                }
            }

            /**
             * return the current state of the divider.
             *
             * @return
             */
            private boolean isShow(){
                return showDivider;
            }

            /**
             * returns a default border of the divider.
             * @return
             */
            private Border createBorder() {
                return (orientation == HORIZONTAL_SPLIT) ?   Border.createCompoundBorder(Border.createBevelRaised(),
                        Border.createBevelRaised(),
                        Border.createEmpty(),
                        Border.createEmpty()) :

                        Border.createCompoundBorder(Border.createEmpty(),
                        Border.createEmpty(),
                        Border.createBevelRaised(),
                        Border.createBevelRaised());
            }

            /**
             * updates the parent ratio according the x, and y coordinates given.
             * @param x x coordinate.
             * @param y y coordinate.
             */
            private void updateParentRatio(final int x, final int y){
                Split parent = (Split)getParent();
                if(orientation == VERTICAL_SPLIT){
                    if (!isInParentBounds(y)){
                        return;
                    }
                    int parentHeight = parent.getHeight() - this.getHeight();
                    double draggedRatio = ((double)y - (double)lastY) / parentHeight;
                    double newRatio = parent.getRatio() + draggedRatio;
                    if (newRatio > parent.maxRatio){
                        parent.setRatio(((Split)getParent()).getMaxRatio());
                    }else parent.setRatio(Math.max(newRatio, ((Split) getParent()).getMinRatio()));
                }else{
                    if (!isInParentBounds(x)){
                        return;
                    }
                    int parentWidth = parent.getWidth() - this.getWidth();
                    double draggedRatio = ((double)x - (double)lastX) / parentWidth;
                    double newRatio = parent.getRatio() + draggedRatio;
                    if (newRatio > parent.getMaxRatio()){
                        newRatio = parent.getMaxRatio();
                    }else if (newRatio < parent.getMinRatio()){
                        newRatio = parent.getMinRatio();
                    }
                    parent.setRatio(newRatio);
                }
                lastX = x;
                lastY = y;
            }

            /**
             * indicates if the given coordinate is in the parents bounds.
             * @param coordinate the current coordinate.
             *
             * @return
             */
            private boolean isInParentBounds(final int coordinate){
                Rectangle parentNodeBounds = getParent().getBounds(new Rectangle());
                if (orientation == VERTICAL_SPLIT){
                    int minY = getParent().getAbsoluteY();
                    int maxY = getParent().getAbsoluteY() + parentNodeBounds.getHeight();
                    return coordinate >= minY && coordinate <= maxY;
                }else{
                    int minX = getParent().getAbsoluteX();
                    int maxX = getParent().getAbsoluteX() + parentNodeBounds.getWidth();
                    return coordinate >= minX && coordinate <= maxX;
                }
            }

            /**
             * returns the default collapse button Image.
             *
             * @param orientation One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
             * @return
             */
            private Image getDefaultCollapseIcon(int  orientation) {
                Style s = UIManager.getInstance().getComponentStyle(COLLAPSE_BUTTON_UIID);
                if (orientation == VERTICAL_SPLIT) {
                    return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, s, 3);
                } else {
                    return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_LEFT, s, 3);
                }
            }

            /**
             * returns the default expand button Image.
             *
             * @param orientation One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
             * @return
             */
            private Image getDefaultExpandIcon(int  orientation) {
                Style s = UIManager.getInstance().getComponentStyle(EXPAND_BUTTON_UIID);
                if (orientation == VERTICAL_SPLIT) {
                    return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, s, 3);
                } else {
                    return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, s, 3);
                }
            }

            /**
             * returns the default drag label Image.
             *
             * @param orientation One of {@link #HORIZONTAL_SPLIT} or {@link #VERTICAL_SPLIT}.
             * @return
             */
            private Image getDefaultDragIcon(int  orientation) {
                Style s = UIManager.getInstance().getComponentStyle(DRAG_HANDLE_UIID);
                Image img = FontImage.createMaterial(FontImage.MATERIAL_DRAG_HANDLE, s, 3);
                if (orientation == VERTICAL_SPLIT) {
                    return img;
                } else {
                    return img.rotate90Degrees(true);
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
                inDrag = true;
                lastX = x;
                lastY = y;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void pointerDragged(int x, int y) {
                if (!inDrag) {
                    return;
                }
                updateParentRatio(x, y);
                this.revalidate();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void pointerReleased(int x, int y) {
                super.pointerReleased(x, y);
                inDrag = false;
            }
        }
    }
}
