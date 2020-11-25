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
import com.codename1.util.EasyThread;

import static java.lang.Thread.sleep;

public class MultiSplitPane extends Container{


    public MultiSplitPane() {
        super(new MultiSplitLayout());
    }

    public MultiSplitPane(Node model){
        super(new MultiSplitLayout());
        setModel(model);
    }

    public void setModel(Node model) {
        ((MultiSplitLayout)getLayout()).setModel(model);
        removeAll();
        addComponents(model);
    }

    private void addComponents(Node root){
        if (root instanceof Leaf){
            add(((Leaf)root).getChildComponent());
        }else{
            add(((Split)root).getDivider());
            addComponents(((Split)root).getLeftTopChild());
            addComponents(((Split)root).getRightBottomChild());
        }
    }

    private MultiSplitLayout getMultiSplitLayout() {
        return (MultiSplitLayout)getLayout();
    }

    public static class Divider extends Container {
        private boolean showDivider = true;
        private int lastX, lastY;
        private boolean inDrag;
        private final boolean isHorizontal;

        private final Button btnCollapse;
        private final Button btnExpand;
        private final Label dragLabel;
        private Split parentNode = null;

        private final String expandButtonUIID = "Label";
        private final String collapseButtonUIID = "Label";
        private final String dragHandleUIID = "Label";


        private Divider(boolean isHorizontal) {
            super(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
            this.isHorizontal = isHorizontal;

            this.getAllStyles().setBorder(createBorder());


            btnCollapse = new Button();
            btnCollapse.setUIID(collapseButtonUIID);
            btnCollapse.setIcon(getDefaultCollapseIcon(isHorizontal));
            btnCollapse.getAllStyles().setPadding(0,0,0,0);
            btnCollapse.getAllStyles().setMargin(0,0,0,0);
            btnCollapse.addActionListener(e ->{
                double newRatio;
                if(parentNode.getRatio() <= 0.5){
                    newRatio = parentNode.getMinRatio();
                }else{
                    if(parentNode.getMinRatio() <= 0.5){
                        newRatio = 0.5 ;
                    }else{
                        newRatio = parentNode.getMinRatio();
                    }

                }
                moveDivider(parentNode.getRatio(), newRatio, 200);
            });

            btnExpand = new Button();
            btnExpand.setUIID(expandButtonUIID);
            btnExpand.getAllStyles().setPadding(0,0,0,0);
            btnExpand.getAllStyles().setMargin(0,0,0,0);
            btnExpand.setIcon(getDefaultExpandIcon(isHorizontal));
            btnExpand.addActionListener(e ->{
                double newRatio;
                if(parentNode.getRatio() < 0.5){
                    if (parentNode.getMaxRatio() <= 0.5){
                        newRatio = parentNode.getMaxRatio();
                    }else{
                        newRatio = 0.5;
                    }
                }else{
                    newRatio = parentNode.getMaxRatio();
                }
                moveDivider(parentNode.getRatio(), newRatio, 200);
            });

            dragLabel = new Label();
            dragLabel.setUIID(dragHandleUIID);
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

        //TODO see if can find more convenient way to fix the animation or remove the animation and make the transition instant.
        private void moveDivider(double oldRatio, double newRatio, long millis){
            final double animationStep = 0.005;
            EasyThread.start("").run(()->{
                if(oldRatio > newRatio){
                    int sleepTime = (int)((double)millis / ((oldRatio - newRatio) / animationStep));
                    if (sleepTime == 0){
                        sleepTime = 1;
                    }
                    for (double ratio = oldRatio; ratio > newRatio; ratio -= animationStep){
                        try{
                            sleep(sleepTime);
                        }catch (InterruptedException exception){

                        }
                        parentNode.setRatio(ratio);
                        CN.callSerially(()->{
                            revalidate();
                        });
                    }
                    parentNode.setRatio(newRatio);
                }else if (oldRatio < newRatio){
                    int sleepTime = (int)((double)millis / ((newRatio - oldRatio) / animationStep));
                    if (sleepTime == 0){
                        sleepTime = 1;
                    }
                    for (double ratio = oldRatio; ratio < newRatio; ratio += animationStep){
                        try{
                            sleep(sleepTime);
                        }catch (InterruptedException exception){

                        }
                        parentNode.setRatio(ratio);
                        CN.callSerially(()-> revalidate());
                    }
                    parentNode.setRatio(newRatio);
                }
            });
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

        private void setParentNode(Split parentNode){
            this.parentNode = parentNode;
        }

        private Split getParentNode(){
            return parentNode;
        }

        private void updateParentRatio(final int x, final int y){
            Rectangle parentLimits = parentNode.getBounds();
            if(isHorizontal){
                if (!inParentBounds(y)){
                    return;
                }
                int parentHeight = parentLimits.getHeight() - this.getHeight();
                double draggedRatio = ((double)y - (double)lastY) / parentHeight;
                double newRatio = parentNode.getRatio() + draggedRatio;
                if (newRatio > getParentNode().maxRatio){
                    parentNode.setRatio(parentNode.getMaxRatio());
                }else if (newRatio < parentNode.getMinRatio()){
                    parentNode.setRatio(parentNode.getMinRatio());
                }else{
                    parentNode.setRatio(newRatio);
                }
            }else{
                if (!inParentBounds(x)){
                    return;
                }
                int parentWidth = parentLimits.getWidth() - this.getWidth();
                double draggedRatio = ((double)x - (double)lastX) / parentWidth;
                double newRatio = parentNode.getRatio() + draggedRatio;
                if (newRatio > 1.0){
                    parentNode.setRatio(1.0);
                }else if (newRatio < 0){
                    parentNode.setRatio(0);
                }else{
                    parentNode.setRatio(newRatio);
                }
            }
            lastX = x;
            lastY = y;
        }

        private boolean inParentBounds(final int coordinate){
            Rectangle parentNodeBounds = parentNode.getBounds();
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
            Style s = UIManager.getInstance().getComponentStyle(collapseButtonUIID);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_LEFT, s, 3);
            }
        }

        private Image getDefaultExpandIcon(boolean isHorizontal) {
            Style s = UIManager.getInstance().getComponentStyle(expandButtonUIID);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, s, 3);
            }
        }

        private Image getDefaultDragIcon(boolean isHorizontal) {
            Style s = UIManager.getInstance().getComponentStyle(dragHandleUIID);
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

    private static class MultiSplitLayout extends Layout {
        private Node model = null;

        private Node getModel() {
            return model;
        }

        private void setModel(Node root) {
            this.model = root;
        }


        private Dimension preferredComponentSize(Leaf node) {
            Component child = node.getChildComponent();
            return (child != null) ? child.getPreferredSize() : new Dimension(0, 0);
        }

        @Override
        public Dimension getPreferredSize(Container parent) {
            Style parentStyle = parent.getStyle();
            Dimension size = preferredNodeSize(getModel());
            size.setWidth(size.getWidth() + parentStyle.getHorizontalPadding());
            size.setHeight(size.getHeight() + parentStyle.getVerticalPadding());
            return size;
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
            layout(getModel(), bounds);
        }

        private void layout(Node root, Rectangle bounds) {
            if (root instanceof Leaf) {
                root.setBounds(bounds);
                Component cmp = ((Leaf) root).getChildComponent();
                cmp.setX(bounds.getX());
                cmp.setY(bounds.getY());
                cmp.setSize(bounds.getSize());
            } else {
                Split split = (Split) root;
                split.setBounds(bounds);
                if (split.isHorizontal()) {
                    Node topChild = split.getLeftTopChild();
                    Divider divider = split.getDivider();
                    Node bottomChild = split.getRightBottomChild();
                    int y = bounds.getY();
                    int x= bounds.getX();
                    Dimension totalChildrenSize = new Dimension(bounds.getWidth(), bounds.getHeight() - divider.getPreferredH());

                    Dimension topChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * split.getRatio()));
                    topChild.setBounds(new Rectangle(x, y, topChildDim));
                    y += topChildDim.getHeight();

                    divider.setX(x);
                    divider.setY(y);
                    divider.setSize(new Dimension(bounds.getWidth(), divider.getPreferredH()));
                    y += divider.getHeight();

                    Dimension bottomChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * (1 - split.getRatio())));
                    bottomChild.setBounds(new Rectangle(bounds.getX(), y, bottomChildDim));

                    layout(topChild, topChild.getBounds());
                    layout(bottomChild, bottomChild.getBounds());
                } else {
                    Node leftChild = split.getLeftTopChild();
                    Divider divider = split.getDivider();
                    Node rightChild = split.getRightBottomChild();
                    int x = bounds.getX();
                    int y = bounds.getY();
                    Dimension totalChildrenSize = new Dimension(bounds.getWidth() - divider.getPreferredW(), bounds.getHeight());

                    Dimension leftChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * split.getRatio())), totalChildrenSize.getHeight());
                    leftChild.setBounds(new Rectangle(x, y, leftChildDim));
                    x += leftChildDim.getWidth();

                    divider.setX(x);
                    divider.setY(y);
                    divider.setSize(new Dimension(divider.getPreferredW(), bounds.getHeight()));
                    x += divider.getWidth();

                    Dimension bottomChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * (1 - split.getRatio()))), totalChildrenSize.getHeight());
                    rightChild.setBounds(new Rectangle(x, y, bottomChildDim));

                    layout(leftChild, leftChild.getBounds());
                    layout(rightChild, rightChild.getBounds());
                }
            }
        }

        private Dimension preferredNodeSize(Node root) {
            if (root instanceof Leaf) {
                return preferredComponentSize((Leaf)root);
            } else {
                Split split = (Split) root;
                Node leftTop = ((Split) root).getLeftTopChild();
                Node rightBottom = ((Split) root).getRightBottomChild();
                int width;
                int height;
                Dimension topLeftSize = preferredNodeSize(leftTop);
                Dimension rightBottomSize = preferredNodeSize(rightBottom);

                if (split.isHorizontal()) {
                    width = Math.max(topLeftSize.getWidth(), rightBottomSize.getWidth());
                    height = topLeftSize.getHeight() + rightBottomSize.getHeight() + ((Split) root).getDivider().getHeight();
                } else {
                    width = topLeftSize.getWidth() + rightBottomSize.getWidth() + ((Split) root).getDivider().getWidth();
                    height = Math.max(topLeftSize.getHeight(), rightBottomSize.getHeight());
                }
                return new Dimension(width, height);
            }
        }

        /**
         * The specified Node is either the wrong type or was configured
         * incorrectly.
         */
        public static class InvalidLayoutException extends RuntimeException {
            public InvalidLayoutException (String msg) {
                super(msg);
            }
        }

        private void throwInvalidLayout(String msg) {
            throw new InvalidLayoutException(msg);
        }
    }


    public static abstract class Node {
        private Rectangle bounds = new Rectangle();

        public Rectangle getBounds() {
            return new Rectangle(this.bounds);
        }

        public void setBounds(Rectangle bounds) {
            if (bounds == null) {
                throw new IllegalArgumentException("null bounds");
            }
            this.bounds = new Rectangle(bounds);
        }
    }

    public static class Split extends Node {
        private final Divider divider;
        private final Node leftTop;
        private final Node rightBottom;
        private boolean isHorizontal;
        private double ratio = 0.5;
        private double minRatio = 0;
        private double maxRatio = 1;


        public Split(boolean isHorizontal, Node leftTop, Node rightBottom) {
            this.isHorizontal = isHorizontal;

            this.leftTop = leftTop;
            this.rightBottom = rightBottom;
            divider = new Divider(isHorizontal);
            divider.setParentNode(this);
        }

        public void setMinRatio(double minRatio){
            if ( ((minRatio < 0.0) || (minRatio > maxRatio))){
                throw new IllegalArgumentException("invalid Ratio");
            }
            if (ratio < minRatio) {
                ratio = minRatio;
            }
            this.minRatio = minRatio;
        }

        public double getMinRatio(){
            return minRatio;
        }

        public void setMaxRatio(double maxRatio){
            if ( ((maxRatio < minRatio) || (maxRatio > 1.0))){
                throw new IllegalArgumentException("invalid ratio");
            }
            if (ratio > maxRatio) {
                ratio = maxRatio;
            }
            this.maxRatio = maxRatio;
        }

        public double getMaxRatio(){
            return maxRatio;
        }

        public double getRatio() {
            return ratio;
        }

        public void setRatio(double ratio) {
            if ((ratio < minRatio) || (ratio > maxRatio)) {
                throw new IllegalArgumentException("invalid ratio");
            }
            this.ratio = ratio;
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        public void setOrientation(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }

        public Node getLeftTopChild() {
            return leftTop;
        }

        public Node getRightBottomChild() {
            return rightBottom;
        }

        public Divider getDivider() {
            return divider;
        }
    }

    public static class Leaf extends Node {
        private final Component child;

        public Leaf(Component child) {
            if (child == null) {
                throw new IllegalArgumentException("child of leaf cannot be null");
            }
            this.child = child;
        }

        public Component getChildComponent() {
            return child;
        }
    }
}
