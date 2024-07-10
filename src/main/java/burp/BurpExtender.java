package burp;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BurpExtender implements IBurpExtender, ITab, IHttpListener, IContextMenuFactory {
    // Burp API imports
    private static IBurpExtenderCallbacks callbacks;
    private static IExtensionHelpers helpers;
    private static JPanel rootPanel;
    private static UARotate UARotate;

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {
        //Callback Objects
        BurpExtender.callbacks = callbacks;
        BurpExtender.helpers = callbacks.getHelpers();
        //Extension Name
        callbacks.setExtensionName("User-Agent Rotate");
        UARotate = new UARotate();
        rootPanel = UARotate.getUI();
        //Adding the UI to Burp
        callbacks.customizeUiComponent(rootPanel);
        //Adding a new tab with our extension
        callbacks.addSuiteTab(BurpExtender.this);
        //Calling the HTTP listener
        callbacks.registerHttpListener(BurpExtender.this);
        //Adding our extension to the right click menu
        callbacks.registerContextMenuFactory(BurpExtender.this);
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        return null;
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if(messageIsRequest) {
            UARotate.modifyRequest(messageInfo);
        }
    }
    public static IBurpExtenderCallbacks getCallbacks() {
        return callbacks;
    }
    @Override
    public String getTabCaption() {
        return "User-Agent Rotate";
    }

    @Override
    public Component getUiComponent() {
        return rootPanel;
    }
}