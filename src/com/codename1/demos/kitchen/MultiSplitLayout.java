package com.codename1.demos.kitchen;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MultiSplitLayout extends Layout {
    private final Map<String, Component> childMap = new HashMap<>();
    private List<Component> dividerList = new ArrayList<>();
    private MultiSplitLayout.Node model;

    public MultiSplitLayout() {
        this(new MultiSplitLayout.Leaf("default"));
    }

    public List<Component> getDividerList(){
        return dividerList;
    }

    public MultiSplitLayout(MultiSplitLayout.Node model) {
        this.model = model;
        addDividers(model);
    }

    public MultiSplitLayout.Node getModel() {
        return model;
    }

    public void setModel(MultiSplitLayout.Node model) {
        this.model = model;
        addDividers(model);
    }

    public void addLayoutComponent(String name, Component child) {
        if (name == null || child == null) {
            throw new IllegalArgumentException("name not specified");
        }
        childMap.put(name, child);
    }

    public void removeLayoutComponent(Component child) {
        if (child != null) {
            String name = child.getName();
            if (name != null) {
                childMap.remove(name);
            }
        }
    }

    private void addDividers(MultiSplitLayout.Node root){
        if (model instanceof Leaf){
            return;
        }else{
            dividerList.add(((Split)root).getDivider());
        }
    }

    private Component childForNode(MultiSplitLayout.Node node) {
        if (node instanceof MultiSplitLayout.Leaf) {
            MultiSplitLayout.Leaf leaf = (MultiSplitLayout.Leaf) node;
            String name = leaf.getName();
            return (name != null) ? childMap.get(name) : null;
        }
        return null;
    }

    private Dimension preferredComponentSize(MultiSplitLayout.Node node) {
        Component child = childForNode(node);
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
        Dimension size = parent.getPreferredSize();
        int width = size.getWidth();
        int height = size.getHeight();
        Rectangle bounds = new Rectangle(0, 0, width, height);
        layout(getModel(), bounds);
    }


    private void layout(MultiSplitLayout.Node root, Rectangle bounds) {
        if (root instanceof MultiSplitLayout.Leaf) {
            root.setBounds(bounds);
            Component cmp = childForNode(root);
            cmp.setX(bounds.getX());
            cmp.setY(bounds.getY());
            cmp.setSize(bounds.getSize());
            return;
        } else {
            MultiSplitLayout.Split split = (MultiSplitLayout.Split) root;
            split.setBounds(bounds);
            if (split.isHorizontal()) {
                Node topChild = split.getLeftTopChild();
                MultiSplitPane.Divider div = split.getDivider();
                Node bottomChild = split.getRightBottomChild();
                int y = bounds.getY();
                int x= bounds.getX();
                Dimension totalChildrenSize = new Dimension(bounds.getWidth(), bounds.getHeight() - div.getHeight());

                Dimension topChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * split.getWeight()));
                topChild.setBounds(new Rectangle(x, y, topChildDim));
                y += topChildDim.getHeight();

                div.setX(x);
                div.setY(y);
                div.setSize(new Dimension(bounds.getWidth(), div.getPreferredH()));
                y += div.getHeight();

                Dimension bottomChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * (1 - split.getWeight())));
                bottomChild.setBounds(new Rectangle(bounds.getX(), y, bottomChildDim));

                layout(topChild, topChild.getBounds());
                layout(bottomChild, bottomChild.getBounds());

            } else {
                Node leftChild = split.getLeftTopChild();
                MultiSplitPane.Divider div = split.getDivider();
                Node rightChild = split.getRightBottomChild();
                int x = bounds.getX();
                int y = bounds.getY();
                Dimension totalChildrenSize = new Dimension(bounds.getWidth() - div.getWidth(), bounds.getHeight());

                Dimension leftChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * split.getWeight())), totalChildrenSize.getHeight());
                leftChild.setBounds(new Rectangle(x, y, leftChildDim));
                x += leftChildDim.getWidth();

                div.setX(x);
                div.setY(y);
                div.setSize(new Dimension(bounds.getWidth(), div.getPreferredH()));
                div.setSize(new Dimension(div.getPreferredW(), bounds.getHeight()));
                x += div.getWidth();

                Dimension bottomChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * (1 - split.getWeight()))), totalChildrenSize.getHeight());
                rightChild.setBounds(new Rectangle(x, y, bottomChildDim));

                layout(leftChild, leftChild.getBounds());
                layout(rightChild, rightChild.getBounds());
            }
        }
    }

    private Dimension preferredNodeSize(MultiSplitLayout.Node root) {
        if (root instanceof MultiSplitLayout.Leaf) {
            return preferredComponentSize(root);
        } else {
            MultiSplitLayout.Split split = (MultiSplitLayout.Split) root;
            MultiSplitLayout.Node leftTop = ((MultiSplitLayout.Split) root).getLeftTopChild();
            MultiSplitLayout.Node rightBottom = ((MultiSplitLayout.Split) root).getRightBottomChild();
            int width;
            int height;
            Dimension topLeftSize = preferredNodeSize(leftTop);
            Dimension rightBottomSize = preferredNodeSize(rightBottom);


            if (split.isHorizontal()) {
                width = Math.max(topLeftSize.getWidth(), rightBottomSize.getWidth());
                height = topLeftSize.getHeight() + rightBottomSize.getHeight() + ((Split) root).getDivider().getPreferredSize().getHeight();
            } else {
                width = topLeftSize.getWidth() + rightBottomSize.getWidth() + ((Split) root).getDivider().getPreferredSize().getWidth();
                height = Math.max(topLeftSize.getHeight(), rightBottomSize.getHeight());
            }
            return new Dimension(width, height);
        }
    }

    public static abstract class Node {
        private MultiSplitLayout.Split parent = null;
        private Rectangle bounds = new Rectangle();

        public MultiSplitLayout.Split getParent() {
            return parent;
        }

        public void setParent(MultiSplitLayout.Split parent) {
            this.parent = parent;
        }

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

    public static class Split extends MultiSplitLayout.Node {
        private MultiSplitPane.Divider divider;
        private MultiSplitLayout.Leaf leftTop;
        private MultiSplitLayout.Leaf rightBottom;
        private boolean isHorizontal;
        private double weight = 0.5;


        public Split(boolean isHorizontal, MultiSplitLayout.Leaf leftTop, MultiSplitLayout.Leaf rightBottom) {
            this.isHorizontal = isHorizontal;
            this.leftTop = leftTop;
            this.rightBottom = rightBottom;

            divider = new MultiSplitPane.Divider(isHorizontal);
            divider.setParentNode(this);
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            if ((weight < 0.0) || (weight > 1.0)) {
                throw new IllegalArgumentException("invalid weight");
            }
            this.weight = weight;
        }

        public boolean isHorizontal() {
            return isHorizontal;
        }

        public void setLayout(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }

        public MultiSplitLayout.Node getLeftTopChild() {
            return leftTop;
        }

        public MultiSplitLayout.Node getRightBottomChild() {
            return rightBottom;
        }

        public MultiSplitPane.Divider getDivider() {
            return divider;
        }
    }

    public static class Leaf extends MultiSplitLayout.Node {
        private String name = "";

        public Leaf() {
        }

        public Leaf(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("name is null");
            }
            this.name = name;
        }
    }
}

