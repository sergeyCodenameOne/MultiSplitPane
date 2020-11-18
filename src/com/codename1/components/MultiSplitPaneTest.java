package com.codename1.components;


import com.codename1.io.Log;
import com.codename1.ui.*;
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
        Form hi = new Form("Hi World", BoxLayout.y());

        Label l1 = new Label("demo label 1");
        Label l2 = new Label("demo label 2");
        Label l3 = new Label("demo label 3");
        Label l4 = new Label("demo label 4");
        Label l5 = new Label("demo label 5");
        Label l6 = new Label("demo label 6");

        Container c1 = BoxLayout.encloseY(l1, l2, l3);
        Container c2 = BoxLayout.encloseY(l4, l5, l6);

        MultiSplitLayout.Leaf leaf1 = new MultiSplitLayout.Leaf("1");
        MultiSplitLayout.Leaf leaf2 = new MultiSplitLayout.Leaf("2");
        MultiSplitPane.Divider div = new MultiSplitPane.Divider(true);

        MultiSplitPane multi = new MultiSplitPane();
        multi.addAll(c1, c2);
        multi.getMultiSplitLayout().addLayoutComponent("1", c1);
        multi.getMultiSplitLayout().addLayoutComponent("2", c2);

        MultiSplitLayout.Split split = new MultiSplitLayout.Split(true, leaf1, leaf2, div);


        multi.setModel(split);
        hi.add(multi);
        hi.show();
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

}
