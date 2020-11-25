package com.codename1.components;


import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;

import static com.codename1.ui.CN.*;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class MultiSplitPaneTest {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
//        Form demoForm = twoVerticalSplitDemo();
//        Form demoForm = twoHorizontalSplitDemo();
//        Form demoForm = threeHorizontalSplitDemo();
//        Form demoForm = threeVerticalSplitDemo();
//        Form demoForm = threeVerticalAndHorizontalSplitDemo();
        Form demoForm = playGroundDemo();

        demoForm.show();
    }


    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }

//    public Form twoVerticalSplitDemo(){
//        Form hi = new Form("Hi World", new BorderLayout());
//
//        Label l1 = new Label("demo label 1");
//        Label l2 = new Label("demo label 2");
//        Label l3 = new Label("demo label 3");
//        Label l4 = new Label("demo label 4");
//        Label l5 = new Label("demo label 5");
//        Label l6 = new Label("demo label 6");
//
//        Container c1 = BoxLayout.encloseY(l1, l2, l3);
//        Container c2 = BoxLayout.encloseY(l4, l5, l6);
//
//        MultiSplitPane.Divider div1 = new MultiSplitPane.Divider(false);
//
//        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf("1");
//        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf("2");
//        MultiSplitPane.DividerNode divNode1 = new MultiSplitPane.DividerNode("div1");
//
//
//        MultiSplitPane multi = new MultiSplitPane();
//        multi.addAll(c1, c2, div1);
//        multi.add("1", c1);
//
//
//        MultiSplitPane.Split split1 = new MultiSplitPane.Split(false, leaf1, leaf2, divNode1);
//
//
//        multi.setModel(split1);
//        hi.add(BorderLayout.CENTER, multi);
//        return hi;
//    }
//    public Form twoHorizontalSplitDemo(){
//        Form hi = new Form("Hi World", new BorderLayout());
//
//        Label l1 = new Label("demo label 1");
//        Label l2 = new Label("demo label 2");
//        Label l3 = new Label("demo label 3");
//        Label l4 = new Label("demo label 4");
//        Label l5 = new Label("demo label 5");
//        Label l6 = new Label("demo label 6");
//
//        Container c1 = BoxLayout.encloseY(l1, l2, l3);
//        Container c2 = BoxLayout.encloseY(l4, l5, l6);
//
//        MultiSplitPane.Divider div1 = new MultiSplitPane.Divider(true);
//
//        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf("1");
//        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf("2");
//        MultiSplitPane.DividerNode divNode1 = new MultiSplitPane.DividerNode("div1");
//        MultiSplitPane.Split split1 = new MultiSplitPane.Split(true, leaf1, leaf2, divNode1);
//
//        MultiSplitPane multi = new MultiSplitPane();
//
//
//        multi.addAll(c1, c2, div1);
//        multi.getMultiSplitLayout().addLayoutComponent("1", c1);
//        multi.getMultiSplitLayout().addLayoutComponent("2", c2);
//        multi.getMultiSplitLayout().addLayoutComponent("div1", div1);
//
//
//        multi.setModel(split1);
//        hi.add(BorderLayout.CENTER, multi);
//        return hi;
//    }
//
//    public Form threeHorizontalSplitDemo(){
//        Form hi = new Form("Hi World",  new BorderLayout());
//
//        Label l1 = new Label("demo label 1");
//        Label l2 = new Label("demo label 2");
//        Label l3 = new Label("demo label 3");
//        Label l4 = new Label("demo label 4");
//        Label l5 = new Label("demo label 5");
//        Label l6 = new Label("demo label 6");
//        Label l7 = new Label("demo label 7");
//        Label l8 = new Label("demo label 8");
//        Label l9 = new Label("demo label 9");
//
//        Container c1 = BoxLayout.encloseY(l1, l2, l3);
//        Container c2 = BoxLayout.encloseY(l4, l5, l6);
//        Container c3 = BoxLayout.encloseY(l7, l8, l9);
//
//        MultiSplitPane.Divider div1 = new MultiSplitPane.Divider(true);
//        MultiSplitPane.Divider div2 = new MultiSplitPane.Divider(true);
//
//        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf("1");
//        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf("2");
//        MultiSplitPane.Leaf leaf3 = new MultiSplitPane.Leaf("3");
//        MultiSplitPane.DividerNode divNode1 = new MultiSplitPane.DividerNode("div1");
//        MultiSplitPane.DividerNode divNode2 = new MultiSplitPane.DividerNode("div2");
//
//        MultiSplitPane multi = new MultiSplitPane();
//        multi.addAll(c1, c2, c3, div1, div2);
//        multi.getMultiSplitLayout().addLayoutComponent("1", c1);
//        multi.getMultiSplitLayout().addLayoutComponent("2", c2);
//        multi.getMultiSplitLayout().addLayoutComponent("3", c3);
//        multi.getMultiSplitLayout().addLayoutComponent("div1", div1);
//        multi.getMultiSplitLayout().addLayoutComponent("div2", div2);
//
//        MultiSplitPane.Split split1 = new MultiSplitPane.Split(true, leaf1, leaf2, divNode1);
//        MultiSplitPane.Split split2 = new MultiSplitPane.Split(true, split1, leaf3, divNode2);
//
//        multi.setModel(split2);
//        hi.add(BorderLayout.CENTER, multi);
//        return hi;
//    }
//    public Form threeVerticalSplitDemo(){
//        Form hi = new Form("Hi World", new BorderLayout());
//
//        Label l1 = new Label("demo label 1");
//        Label l2 = new Label("demo label 2");
//        Label l3 = new Label("demo label 3");
//        Label l4 = new Label("demo label 4");
//        Label l5 = new Label("demo label 5");
//        Label l6 = new Label("demo label 6");
//        Label l7 = new Label("demo label 7");
//        Label l8 = new Label("demo label 8");
//        Label l9 = new Label("demo label 9");
//
//        Container c1 = BoxLayout.encloseY(l1, l2, l3);
//        Container c2 = BoxLayout.encloseY(l4, l5, l6);
//        Container c3 = BoxLayout.encloseY(l7, l8, l9);
//
//        MultiSplitPane.Divider div1 = new MultiSplitPane.Divider(false);
//        MultiSplitPane.Divider div2 = new MultiSplitPane.Divider(false);
//
//        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf("1");
//        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf("2");
//        MultiSplitPane.Leaf leaf3 = new MultiSplitPane.Leaf("3");
//        MultiSplitPane.DividerNode divNode1 = new MultiSplitPane.DividerNode("div1");
//        MultiSplitPane.DividerNode divNode2 = new MultiSplitPane.DividerNode("div2");
//
//        MultiSplitPane multi = new MultiSplitPane();
//        multi.addAll(c1, c2, c3, div1, div2);
//        multi.getMultiSplitLayout().addLayoutComponent("1", c1);
//        multi.getMultiSplitLayout().addLayoutComponent("2", c2);
//        multi.getMultiSplitLayout().addLayoutComponent("3", c3);
//        multi.getMultiSplitLayout().addLayoutComponent("div1", div1);
//        multi.getMultiSplitLayout().addLayoutComponent("div2", div2);
//
//        MultiSplitPane.Split split1 = new MultiSplitPane.Split(false, leaf1, leaf2, divNode1);
//        MultiSplitPane.Split split2 = new MultiSplitPane.Split(false, split1, leaf3, divNode2);
//
//        multi.setModel(split2);
//        hi.add(BorderLayout.CENTER, multi);
//        return hi;
//    }
//    public Form threeVerticalAndHorizontalSplitDemo(){
//        Form hi = new Form("Hi World", new BorderLayout());
//
//        Label l1 = new Label("demo label 1");
//        Label l2 = new Label("demo label 2");
//        Label l3 = new Label("demo label 3");
//        Label l4 = new Label("demo label 4");
//        Label l5 = new Label("demo label 5");
//        Label l6 = new Label("demo label 6");
//        Label l7 = new Label("demo label 7");
//        Label l8 = new Label("demo label 8");
//        Label l9 = new Label("demo label 9");
//
//        Container c1 = BoxLayout.encloseY(l1, l2, l3);
//        Container c2 = BoxLayout.encloseY(l4, l5, l6);
//        Container c3 = BoxLayout.encloseY(l7, l8, l9);
//
//        // Can be removed
//        MultiSplitPane.Divider div1 = new MultiSplitPane.Divider(true);
//        MultiSplitPane.Divider div2 = new MultiSplitPane.Divider(false);
//
//        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf("1");
//        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf("2");
//        MultiSplitPane.Leaf leaf3 = new MultiSplitPane.Leaf("3");
//
//
//        // Can be removed
//        MultiSplitPane.DividerNode divNode1 = new MultiSplitPane.DividerNode("div1");
//        MultiSplitPane.DividerNode divNode2 = new MultiSplitPane.DividerNode("div2");
//
//        MultiSplitPane multi = new MultiSplitPane();
//        multi.addAll(c1, c2, c3, div1, div2);
//        multi.getMultiSplitLayout().addLayoutComponent("1", c1);
//        multi.getMultiSplitLayout().addLayoutComponent("2", c2);
//        multi.getMultiSplitLayout().addLayoutComponent("3", c3);
//        multi.getMultiSplitLayout().addLayoutComponent("div1", div1);
//        multi.getMultiSplitLayout().addLayoutComponent("div2", div2);
//
//        MultiSplitPane.Split split1 = new MultiSplitPane.Split(true, leaf1, leaf2, divNode1);
//        MultiSplitPane.Split split2 = new MultiSplitPane.Split(false, split1, leaf3, divNode2);
//        split1.setMaxRatio(0.7);
//        split1.setMinRatio(0.3);
//        div2.setDragIcon(FontImage.createMaterial(FontImage.MATERIAL_AIRPLANEMODE_ON, UIManager.getInstance().getComponentStyle("DemoDragIcon")));
//        div2.setDragLabelUIID("DemoDragIcon");
//        div2.setUIID("DemoDiv");
//
//
//
//        multi.setModel(split2);
//        hi.add(BorderLayout.CENTER, multi);
//        return hi;
//    }

    public Form playGroundDemo(){
        Form hi = new Form("Hi World", new BorderLayout());

        Label l1 = new Label("demo label 1");
        Label l2 = new Label("demo label 2");
        Label l3 = new Label("demo label 3");
        Label l4 = new Label("demo label 4");
        Label l5 = new Label("demo label 5");
        Label l6 = new Label("demo label 6");
        Label l7 = new Label("demo label 7");
        Label l8 = new Label("demo label 8");
        Button l9 = new Button("demo button show");


        Container c1 = BoxLayout.encloseY(l1, l2, l3);
        Container c2 = BoxLayout.encloseY(l4, l5, l6);
        Container c3 = BoxLayout.encloseY(l7, l8, l9);

        MultiSplitPane.Leaf leaf1 = new MultiSplitPane.Leaf(c1);
        MultiSplitPane.Leaf leaf2 = new MultiSplitPane.Leaf(c2);
        MultiSplitPane.Leaf leaf3 = new MultiSplitPane.Leaf(c3);

        MultiSplitPane.Split split1 = new MultiSplitPane.Split(true, leaf1, leaf2);
        MultiSplitPane.Split split2 = new MultiSplitPane.Split(false, split1, leaf3);

        MultiSplitPane multi = new MultiSplitPane(split2);

        multi.setUIID("DemoCnt");
        hi.add(BorderLayout.CENTER, multi);
        return hi;
    }
}
