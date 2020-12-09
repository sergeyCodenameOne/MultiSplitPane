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

public class MultiSplitPane extends Container {
    private final Split root;

    public static MultiSplitPane split(Component leftTop, Component rightBottom, Divider divider){
        return MultiSplitPane.split(leftTop, rightBottom, divider, Split.DEFAULT_MIN_RATIO, Split.DEFAULT_MAX_RATIO, Split.DEFAULT_RATIO);
    }

    public static MultiSplitPane split(Component leftTop, Component rightBottom, Divider divider, double minRatio, double maxRatio, double ratio){
        Split split = new Split(leftTop, rightBottom, divider, minRatio, maxRatio, ratio);
        split.setMaxRatio(maxRatio);
        split.setMinRatio(minRatio);
        return new MultiSplitPane(split);
    }

    public MultiSplitPane split(Component cmp, Divider divider, boolean isTopOrLeft){
        return this.split(cmp, divider, isTopOrLeft, Split.DEFAULT_MIN_RATIO, Split.DEFAULT_MAX_RATIO, Split.DEFAULT_RATIO);
    }

    public MultiSplitPane split(Component cmp, Divider divider, boolean isTopOrLeft, double minRatio, double maxRatio, double ratio){
        removeComponent(root);
        Split root = isTopOrLeft ?  new Split(cmp, getRoot(), divider, minRatio, maxRatio, ratio) :
                                    new Split(getRoot(), cmp, divider, minRatio, maxRatio, ratio);
        setRoot(root);
        return this;
    }

    private MultiSplitPane(Split root){
        super(new BorderLayout());
        this.root = root;
        add(BorderLayout.CENTER, root);
    }

    private void setRoot(Split root){
        add(BorderLayout.CENTER, root);

    }

    public final Split getRoot(){
        return root;
    }

    public static class Divider extends Container {
        private boolean showDivider = true;
        private int lastX, lastY;
        private boolean inDrag;
        private final boolean isHorizontal;
        private final Button btnCollapse;
        private final Button btnExpand;
        private final Label dragLabel;

        private static final String EXPAND_BUTTON_UIID = "Label";
        private static final String COLLAPSE_BUTTON_UIID = "Label";
        private static final String DRAG_HANDLE_UIID = "Label";

        public Divider(boolean isHorizontal) {
            super(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
            this.isHorizontal = isHorizontal;

            this.getAllStyles().setBorder(createBorder());

            btnCollapse = new Button();
            btnCollapse.setUIID(COLLAPSE_BUTTON_UIID);
            btnCollapse.setIcon(getDefaultCollapseIcon(isHorizontal));
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
            btnExpand.setIcon(getDefaultExpandIcon(isHorizontal));
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
            dragLabel.setIcon(getDefaultDragIcon(isHorizontal));
            dragLabel.setIgnorePointerEvents(true);

            add(BorderLayout.CENTER, dragLabel);
            if (isHorizontal){
                add(BorderLayout.WEST, BoxLayout.encloseX(btnCollapse, btnExpand));
            }else{
                add(BorderLayout.NORTH, BoxLayout.encloseY(btnCollapse, btnExpand));
            }
            setDraggable(true);
        }

        public boolean isHorizontal(){
            return isHorizontal;
        }

        public void setCollapseButtonUIID(String id){
            btnCollapse.setUIID(id);
        }

        public void setExpandButtonUIID(String id){
            btnExpand.setUIID(id);
        }

        public void setDragLabelUIID(String id){
            dragLabel.setUIID(id);
        }

        public void setCollapseIcon(Image collapseImage){
            btnCollapse.setIcon(collapseImage);
        }

        public void setExpandIcon(Image collapseImage){
            btnExpand.setIcon(collapseImage);
        }

        public void setDragLabelIcon(Image collapseImage) {
            dragLabel.setIcon(collapseImage);
        }

        public void showDivider(boolean show){
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

        public boolean isShow(){
            return showDivider;
        }

        private Border createBorder() {
            return isHorizontal ?   Border.createCompoundBorder(Border.createBevelRaised(),
                    Border.createBevelRaised(),
                    Border.createEmpty(),
                    Border.createEmpty()) :
                    Border.createCompoundBorder(Border.createEmpty(),
                            Border.createEmpty(),
                            Border.createBevelRaised(),
                            Border.createBevelRaised());
        }

        private void updateParentRatio(final int x, final int y){
            Split parent = (Split)getParent();
            if(isHorizontal){
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
                if (newRatio > 1.0){
                    parent.setRatio(1.0);
                }else if (newRatio < 0){
                    parent.setRatio(0);
                }else{
                    parent.setRatio(newRatio);
                }
            }
            lastX = x;
            lastY = y;
        }

        private boolean isInParentBounds(final int coordinate){
            Rectangle parentNodeBounds = getParent().getBounds(new Rectangle());
            if (isHorizontal){
                int minY = getParent().getAbsoluteY();
                int maxY = getParent().getAbsoluteY() + parentNodeBounds.getHeight();
                return coordinate >= minY && coordinate <= maxY;
            }else{
                int minX = getParent().getAbsoluteX();
                int maxX = getParent().getAbsoluteX() + parentNodeBounds.getWidth();
                return coordinate >= minX && coordinate <= maxX;
            }
        }

        private Image getDefaultCollapseIcon(boolean isHorizontal) {
            Style s = UIManager.getInstance().getComponentStyle(COLLAPSE_BUTTON_UIID);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_LEFT, s, 3);
            }
        }

        private Image getDefaultExpandIcon(boolean isHorizontal) {
            Style s = UIManager.getInstance().getComponentStyle(EXPAND_BUTTON_UIID);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, s, 3);
            }
        }

        private Image getDefaultDragIcon(boolean isHorizontal) {
            Style s = UIManager.getInstance().getComponentStyle(DRAG_HANDLE_UIID);
            Image img = FontImage.createMaterial(FontImage.MATERIAL_DRAG_HANDLE, s, 3);
            if (isHorizontal) {
                return img;
            } else {
                return img.rotate90Degrees(true);
            }
        }

        @Override
        protected boolean isStickyDrag() {
            return true;
        }

        @Override
        public void pointerPressed(int x, int y) {
            super.pointerPressed(x, y);
            inDrag = true;
            lastX = x;
            lastY = y;
        }


        @Override
        public void pointerDragged(int x, int y) {
            if (!inDrag) {
                return;
            }
            updateParentRatio(x, y);
            this.revalidate();
        }

        @Override
        public void pointerReleased(int x, int y) {
            super.pointerReleased(x, y);
            inDrag = false;
        }
    }

    public static class MultiSplitLayout extends Layout {

        @Override
        public Dimension getPreferredSize(Container parent) {
            Split split = (Split) parent;
            Component leftTop = split.getLeftTopChild();
            Component rightBottom = split.getRightBottomChild();
            int width;
            int height;
            Dimension topLeftSize = leftTop.getPreferredSize();
            Dimension rightBottomSize = rightBottom.getPreferredSize();

            if (split.isHorizontal()) {
                width = Math.max(topLeftSize.getWidth(), rightBottomSize.getWidth());
                height = topLeftSize.getHeight() + rightBottomSize.getHeight() + split.getDivider().getHeight();
            } else {
                width = topLeftSize.getWidth() + rightBottomSize.getWidth() + split.getDivider().getWidth();
                height = Math.max(topLeftSize.getHeight(), rightBottomSize.getHeight());
            }
            return new Dimension(width, height);
        }

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
            if (split.isHorizontal()) {
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

    private static class Split extends Container{
        private final Divider divider;
        private final Component leftTop;
        private final Component rightBottom;
        private double ratio;
        private double minRatio;
        private double maxRatio;

        private static final double DEFAULT_RATIO = 0.5;
        private static final double DEFAULT_MIN_RATIO = 0;
        private static final double DEFAULT_MAX_RATIO = 1;

        private Split(Component leftTop, Component rightBottom ,Divider div, double minRatio, double maxRatio, double ratio) {
            this.leftTop = leftTop;
            this.rightBottom = rightBottom;
            this.minRatio = minRatio;
            this.maxRatio = maxRatio;
            this.ratio = ratio;
            setUIID("MultiSplit");
            divider = div;
            setLayout(new MultiSplitLayout());
            add(leftTop);
            add(div);
            add(rightBottom);
        }

        private void setMinRatio(double minRatio){
            if ( ((minRatio < 0.0) || (minRatio > maxRatio))){
                throw new IllegalArgumentException("invalid Ratio");
            }
            if (ratio < minRatio) {
                ratio = minRatio;
            }
            this.minRatio = minRatio;
        }

        private double getMinRatio(){
            return minRatio;
        }

        private void setMaxRatio(double maxRatio){
            if ( ((maxRatio < minRatio) || (maxRatio > 1.0))){
                throw new IllegalArgumentException("invalid ratio");
            }
            if (ratio > maxRatio) {
                ratio = maxRatio;
            }
            this.maxRatio = maxRatio;
        }

        private double getMaxRatio(){
            return maxRatio;
        }

        private double getRatio() {
            return ratio;
        }

        private void setRatio(double ratio) {
            if ((ratio < minRatio) || (ratio > maxRatio)) {
                throw new IllegalArgumentException("invalid ratio");
            }
            this.ratio = ratio;
        }

        private boolean isHorizontal() {
            return divider.isHorizontal();
        }


        private Component getLeftTopChild() {
            return leftTop;
        }

        private Component getRightBottomChild() {
            return rightBottom;
        }

        private Divider getDivider() {
            return divider;
        }
    }
}
