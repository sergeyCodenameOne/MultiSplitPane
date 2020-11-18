package com.codename1.components;

import com.codename1.ui.*;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

import java.util.List;

public class MultiSplitPane extends Container{

    public MultiSplitPane() {
        super(new MultiSplitLayout());
    }

    public final MultiSplitLayout getMultiSplitLayout() {
        return (MultiSplitLayout)getLayout();
    }

    public final void setModel(MultiSplitLayout.Node model) {
        ((MultiSplitLayout)getLayout()).setModel(model);

        //TODO rewrite function dividers problem
        List<Component> dividerList = ((MultiSplitLayout)getLayout()).getDividerList();
        for(Component currDivider : dividerList){
            if (!this.contains(currDivider)){
                add(currDivider);
            }
        }
    }

    public static class Divider extends Container {
        private int lastX, lastY;
        private boolean inDrag;

        //TODO see if can change to "public static final int"
        private boolean isHorizontal;

        private Button btnCollapse;
        private Button btnExpand;
        private Button btnDrag;
        private MultiSplitLayout.Split parentNode = null;

        public Divider(boolean isHorizontal) {
            super(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
            this.isHorizontal = isHorizontal;

            //TODO rewrite the button style
            this.btnCollapse = new Button("", "Button");
            btnCollapse.getAllStyles().setPadding(0,0,0,0);
            btnCollapse.getAllStyles().setMargin(0,0,0,0);
            this.btnCollapse.setIcon(getDefaultCollapseIcon(isHorizontal));

            btnCollapse.addActionListener(e ->{
                if(parentNode.getWeight() <= 0.5){
                    parentNode.setWeight(0);
                }else{
                    parentNode.setWeight(0.5);
                }
                //TODO make good animation
                this.revalidate();
            });

            //TODO rewrite the button style
            this.btnExpand = new Button("", "Button");
            btnExpand.getAllStyles().setPadding(0,0,0,0);
            btnExpand.getAllStyles().setMargin(0,0,0,0);
            this.btnExpand.setIcon(getDefaultExpandIcon(isHorizontal));
            btnExpand.addActionListener(e ->{
                if(parentNode.getWeight() < 0.5){
                    parentNode.setWeight(0.5);
                }else{
                    parentNode.setWeight(1.0);
                }
                //TODO make good animation
                this.revalidate();
            });

            //TODO rewrite the button style
            this.btnDrag = new Button("", "Button");
            btnDrag.getAllStyles().setPadding(0,0,0,0);
            btnDrag.getAllStyles().setMargin(0,0,0,0);
            this.btnDrag.setIcon(getDefaultDragIcon(isHorizontal));

            add(BorderLayout.CENTER, btnDrag);
            if (isHorizontal){
                add(BorderLayout.WEST, BoxLayout.encloseX(btnCollapse, btnExpand));
            }else{
                add(BorderLayout.NORTH, BoxLayout.encloseY(btnCollapse, btnExpand));
            }
        }

        //TODO see how to make not public
        public void setParentNode(MultiSplitLayout.Split parentNode){
            this.parentNode = parentNode;
        }

        public void setCollapseIcon(Image collapseImage){
            btnCollapse.setIcon(collapseImage);
        }

        public void setExpandIcon(Image collapseImage){
            btnExpand.setIcon(collapseImage);
        }

        public void setDragIcon(Image collapseImage) {
            btnDrag.setIcon(collapseImage);
        }

        private void updateParentWeight(final int x, final int y){
            //TODO write a function to see if the drag is in valid parent bounds.
//            if (!isInParentBounds){
//                return;
//            }
            Rectangle parentLimits = parentNode.getBounds();
            //TODO make animation better.
            if(isHorizontal){
                int parentHeight = parentLimits.getHeight();
                double draggedWeight = ((double)y - (double)lastY) / parentHeight;
                double newWeight = parentNode.getWeight() + draggedWeight;
                if (newWeight > 1.0){
                    parentNode.setWeight(1.0);
                }else if (newWeight < 0){
                    parentNode.setWeight(0);
                }else{
                    parentNode.setWeight(newWeight);
                }
            }else{
                int parentWidth = parentLimits.getWidth();
                double draggedWeight = ((double)x - (double)lastX) / parentWidth;
                double newWeight = parentNode.getWeight() + draggedWeight;
                if (newWeight > 1.0){
                    parentNode.setWeight(1.0);
                }else if (newWeight < 0){
                    parentNode.setWeight(0);
                }else{
                    parentNode.setWeight(newWeight);
                }
            }
            lastX = x;
            lastY = y;
        }

        private Image getDefaultCollapseIcon(boolean isHorizontal) {
            //TODO change the style of the icon and size.
            Style s = UIManager.getInstance().getComponentStyle("Button");
            s.setMargin(0,0,0,0);
            s.setPadding(0,0,0,0);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_UP, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_LEFT, s, 3);
            }
        }

        private Image getDefaultExpandIcon(boolean isHorizontal) {
            //TODO change the style of the icon and size.
            Style s = UIManager.getInstance().getComponentStyle("Button");
            s.setMargin(0,0,0,0);
            s.setPadding(0,0,0,0);
            if (isHorizontal) {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN, s, 3);
            } else {
                return FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, s, 3);
            }
        }

        private Image getDefaultDragIcon(boolean isHorizontal) {
            //TODO change the style of the icon and size.
            Style s = UIManager.getInstance().getComponentStyle("Button");
            s.setMargin(0,0,0,0);
            s.setPadding(0,0,0,0);
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
            pointerDragged(x, y);
        }

        @Override
        public void pointerDragged(int x, int y) {
            super.pointerDragged(x, y);
            if (!inDrag) {
                return;
            }
            updateParentWeight(x, y);
            this.revalidate();
        }

        @Override
        public void pointerReleased(int x, int y) {
            super.pointerReleased(x, y);
            inDrag = false;
        }

        @Override
        protected void dragFinished(int x, int y) {
            super.dragFinished(x, y);
            inDrag = false;
        }
    }
}
