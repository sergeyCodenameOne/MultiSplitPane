//package com.codename1.components;
//
//import com.codename1.ui.Component;
//import com.codename1.ui.Container;
//import com.codename1.ui.geom.Dimension;
//import com.codename1.ui.geom.Rectangle;
//import com.codename1.ui.layouts.Layout;
//import com.codename1.ui.plaf.Style;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class MultiSplitLayout extends Layout {
//    private final Map<String, Component> childMap = new HashMap<>();
//    private Node model;
//
//    public MultiSplitLayout() {
//        this(new Leaf("default"));
//    }
//
//    public MultiSplitLayout(Node model) {
//        this.model = model;
//    }
//
//    public MultiSplitLayout.Node getModel() {
//        return model;
//    }
//
//    public void setModel(Node root) {
//        if (root instanceof DividerNode){
//            throw new IllegalArgumentException("divider cannot be root");
//        }
//        this.model = root;
//    }
//
//    public void addLayoutComponent(String name, Component child) {
//        if (name == null || child == null) {
//            throw new IllegalArgumentException("name not specified");
//        }
//        childMap.put(name, child);
//    }
//
//    public void removeLayoutComponent(Component child) {
//        if (child != null) {
//            String name = child.getName();
//            if (name != null) {
//                childMap.remove(name);
//            }
//        }
//    }
//
//    private Component childForNode(Node node) {
//        if (node instanceof Leaf) {
//            String name = ( (Leaf)node ).getName();
//            return (name != null) ? childMap.get(name) : null;
//        }else if(node instanceof DividerNode){
//            String name = ( (DividerNode)node ).getName();
//            return (name != null) ? childMap.get(name) : null;
//        }
//        return null;
//    }
//
//    private Dimension preferredComponentSize(Node node) {
//        Component child = childForNode(node);
//        return (child != null) ? child.getPreferredSize() : new Dimension(0, 0);
//    }
//
//    @Override
//    public Dimension getPreferredSize(Container parent) {
//        Style parentStyle = parent.getStyle();
//        Dimension size = preferredNodeSize(getModel());
//        size.setWidth(size.getWidth() + parentStyle.getHorizontalPadding());
//        size.setHeight(size.getHeight() + parentStyle.getVerticalPadding());
//        return size;
//    }
//
//    @Override
//    public void layoutContainer(Container parent) {
//        checkLayout(getModel(), parent);
//        Dimension size = new Dimension(parent.getWidth(), parent.getHeight());
//        Style parentStyle = parent.getStyle();
//        int paddingTop = parentStyle.getPaddingTop();
//        int paddingBottom = parentStyle.getPaddingBottom();
//        int paddingRight = parentStyle.getPaddingRight(parent.isRTL());
//        int paddingLeft = parentStyle.getPaddingLeft(parent.isRTL());
//
//        int width = size.getWidth() - paddingRight - paddingLeft;
//        int height = size.getHeight() - paddingBottom - paddingTop;
//
//        Rectangle bounds = new Rectangle(paddingLeft, paddingTop, width, height);
//        layout(getModel(), bounds);
//    }
//
//    private void checkLayout(Node root, Container parent){
//        if (root instanceof Leaf){
//            if (childForNode(root) == null){
//                throwInvalidLayout("Node: \"" + ((Leaf) root).getName() + "\" doesn't match any component in the layout");
//            }
//            if (!parent.contains(childForNode(root))){
//                throwInvalidLayout("Node: \"" + ((Leaf) root).getName() + "\" doesn't match any component in the container");
//            }
//        }else if(root instanceof DividerNode){
//            if (childForNode(root) == null){
//                throwInvalidLayout("Node: \"" + ((DividerNode) root).getName() + "\" doesn't match any component in the layout");
//            }
//            if (!parent.contains(childForNode(root))){
//                throwInvalidLayout("Node: \"" + ((DividerNode) root).getName() + "\" doesn't match any component in the container");
//            }
//        }else{
//            checkLayout(((Split)root).getLeftTopChild(), parent);
//            checkLayout(((Split)root).getDividerNode(), parent);
//            checkLayout(((Split)root).getRightBottomChild(), parent);
//        }
//    }
//
//    private void layout(Node root, Rectangle bounds) {
//        if (root instanceof Leaf || root instanceof DividerNode) {
//            root.setBounds(bounds);
//            Component cmp = childForNode(root);
//            cmp.setX(bounds.getX());
//            cmp.setY(bounds.getY());
//            cmp.setSize(bounds.getSize());
//        } else {
//            Split split = (Split) root;
//            split.setBounds(bounds);
//            if (split.isHorizontal()) {
//                Node topChild = split.getLeftTopChild();
//                Node divider = split.getDividerNode();
//                Node bottomChild = split.getRightBottomChild();
//                int y = bounds.getY();
//                int x= bounds.getX();
//                Dimension totalChildrenSize = new Dimension(bounds.getWidth(), bounds.getHeight() - preferredNodeSize(divider).getHeight());
//
//                Dimension topChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * split.getWeight()));
//                topChild.setBounds(new Rectangle(x, y, topChildDim));
//                //TODO see how can be written better.
//                if(((MultiSplitPane.Divider)childForNode(divider)).getParentNode() != root){
//                    ((MultiSplitPane.Divider)childForNode(divider)).setParentNode((Split)root);
//                }
//                y += topChildDim.getHeight();
//
//                divider.setBounds(new Rectangle(x, y, new Dimension(bounds.getWidth(), preferredNodeSize(divider).getHeight())));
//                y += preferredNodeSize(divider).getHeight();
//
//                Dimension bottomChildDim = new Dimension(totalChildrenSize.getWidth(), (int)(totalChildrenSize.getHeight() * (1 - split.getWeight())));
//                bottomChild.setBounds(new Rectangle(bounds.getX(), y, bottomChildDim));
//
//                layout(topChild, topChild.getBounds());
//                layout(bottomChild, bottomChild.getBounds());
//                layout(divider, divider.getBounds());
//            } else {
//                Node leftChild = split.getLeftTopChild();
//                Node divider = split.getDividerNode();
//                Node rightChild = split.getRightBottomChild();
//                int x = bounds.getX();
//                int y = bounds.getY();
//                Dimension totalChildrenSize = new Dimension(bounds.getWidth() - preferredNodeSize(divider).getWidth(), bounds.getHeight());
//
//                Dimension leftChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * split.getWeight())), totalChildrenSize.getHeight());
//                leftChild.setBounds(new Rectangle(x, y, leftChildDim));
//                x += leftChildDim.getWidth();
//
//                divider.setBounds(new Rectangle(x, y, new Dimension(preferredNodeSize(divider).getWidth(), bounds.getHeight())));
//                //TODO see how can be written better.
//                if(((MultiSplitPane.Divider)childForNode(divider)).getParentNode() != root){
//                    ((MultiSplitPane.Divider)childForNode(divider)).setParentNode((Split)root);
//                }
//                x += divider.getBounds().getWidth();
//
//                Dimension bottomChildDim = new Dimension(((int)(totalChildrenSize.getWidth() * (1 - split.getWeight()))), totalChildrenSize.getHeight());
//                rightChild.setBounds(new Rectangle(x, y, bottomChildDim));
//
//                layout(leftChild, leftChild.getBounds());
//                layout(rightChild, rightChild.getBounds());
//                layout(divider, divider.getBounds());
//            }
//        }
//    }
//
//    private Dimension preferredNodeSize(Node root) {
//        if (root instanceof Leaf) {
//            return preferredComponentSize(root);
//        }else if(root instanceof DividerNode){
//            return preferredComponentSize(root);
//        } else {
//            Split split = (Split) root;
//            Node leftTop = ((Split) root).getLeftTopChild();
//            Node rightBottom = ((Split) root).getRightBottomChild();
//            int width;
//            int height;
//            Dimension topLeftSize = preferredNodeSize(leftTop);
//            Dimension rightBottomSize = preferredNodeSize(rightBottom);
//
//            if (split.isHorizontal()) {
//                width = Math.max(topLeftSize.getWidth(), rightBottomSize.getWidth());
//                height = topLeftSize.getHeight() + rightBottomSize.getHeight() + preferredNodeSize(((Split) root).getDividerNode()).getHeight();
//            } else {
//                width = topLeftSize.getWidth() + rightBottomSize.getWidth() + preferredNodeSize(((Split) root).getDividerNode()).getWidth();
//                height = Math.max(topLeftSize.getHeight(), rightBottomSize.getHeight());
//            }
//            return new Dimension(width, height);
//        }
//    }
//
//    public static abstract class Node {
//        private Rectangle bounds = new Rectangle();
//
//        public Rectangle getBounds() {
//            return new Rectangle(this.bounds);
//        }
//
//        public void setBounds(Rectangle bounds) {
//            if (bounds == null) {
//                throw new IllegalArgumentException("null bounds");
//            }
//            this.bounds = new Rectangle(bounds);
//        }
//    }
//
//    public static class Split extends Node {
//        private final DividerNode dividerNode;
//        private final Node leftTop;
//        private final Node rightBottom;
//        private boolean isHorizontal;
//        private double weight = 0.5;
//
//
//        public Split(boolean isHorizontal, Node leftTop, Node rightBottom, DividerNode dividerNode) {
//            this.isHorizontal = isHorizontal;
//
//            this.leftTop = leftTop;
//            this.rightBottom = rightBottom;
//            this.dividerNode = dividerNode;
//        }
//
//        public double getWeight() {
//            return weight;
//        }
//
//        public void setWeight(double weight) {
//            if ((weight < 0.0) || (weight > 1.0)) {
//                throw new IllegalArgumentException("invalid weight");
//            }
//            this.weight = weight;
//        }
//
//        public boolean isHorizontal() {
//            return isHorizontal;
//        }
//
//        public void setOrientation(boolean isHorizontal) {
//            this.isHorizontal = isHorizontal;
//        }
//
//        public Node getLeftTopChild() {
//            return leftTop;
//        }
//
//        public Node getRightBottomChild() {
//            return rightBottom;
//        }
//
//        public DividerNode getDividerNode() {
//            return dividerNode;
//        }
//    }
//
//    public static class Leaf extends Node {
//        private String name;
//
//        public Leaf(String name) {
//            if (name == null) {
//                throw new IllegalArgumentException("name is null");
//            }
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            if (name == null) {
//                throw new IllegalArgumentException("name is null");
//            }
//            this.name = name;
//        }
//    }
//
//    public static class DividerNode extends Node {
//        private String name;
//
//        public DividerNode(String name) {
//            if (name == null) {
//                throw new IllegalArgumentException("name is null");
//            }
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            if (name == null) {
//                throw new IllegalArgumentException("name is null");
//            }
//            this.name = name;
//        }
//    }
//
//    /**
//     * The specified Node is either the wrong type or was configured
//     * incorrectly.
//     */
//    public static class InvalidLayoutException extends RuntimeException {
//        public InvalidLayoutException (String msg) {
//            super(msg);
//        }
//    }
//
//    private void throwInvalidLayout(String msg) {
//        throw new InvalidLayoutException(msg);
//    }
//}
