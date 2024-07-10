package burp;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Random;




public class UARotate {
	
    public static final int textHeight = new JTextField().getPreferredSize().height;
    private JPanel mainPanel, gridPanel, gridPanel2;
    private JToggleButton activatedButton;
	private JButton fileSelectButton;
	private JButton defaultSelectButton;
    private GridBagConstraints c;
	private JFileChooser UAFileChooser;
    private IExtensionHelpers helpers;
    private IBurpExtenderCallbacks callbacks;
	private DefaultListModel<String> listModel;
	private JList<String> userAgentList;
	private JScrollPane scrollPane;
	private String defaultPath;
	private Random rand = new Random();



	

    public UARotate(){
        this.callbacks = BurpExtender.getCallbacks();
        helpers = callbacks.getHelpers();
		
		// Initialize the DefaultListModel for the JList
		listModel = new DefaultListModel<>();
		
		// Populate list Model with Defaults
		// TODO - FIX THIS - I am hardcoding this for now as java paths are annoying AF! 
		String defaultPath = "c:\\temp\\useragents.txt";

		listModel = readFileUserAgents(defaultPath);

        createUI();
    }



	public void selectCustomFile(){
		
		UAFileChooser = new JFileChooser();
		//TODO - Set Current Directory wherever default file is
		UAFileChooser.setCurrentDirectory(new File("."));
		int result = UAFileChooser.showOpenDialog(null);
						
		if(result == JFileChooser.APPROVE_OPTION){
			File selectedFile = new File(UAFileChooser.getSelectedFile().getAbsolutePath());
			readFileUserAgents(selectedFile.getAbsolutePath());
			callbacks.issueAlert("Loaded new User Agents file" + selectedFile.getAbsolutePath());	
		}
		else if(result == JFileChooser.CANCEL_OPTION){
			callbacks.issueAlert("Cancelled");
		}
		
	}
	

	public DefaultListModel<String> readFileUserAgents(String path){
		

		listModel.clear();
		
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			callbacks.issueAlert("bruv3");
            String line;
            while ((line = br.readLine()) != null) {
				callbacks.issueAlert(line);
				listModel.addElement(line);
            }
        } catch (IOException e) {
            callbacks.issueAlert("Error Loading File");
        }
		
		
		return listModel;
		
	}


    private void createUI(){
        mainPanel = new JPanel();
        gridPanel = new JPanel();

		// Create the Jlist
		userAgentList = new JList<>(listModel);
		// Set a fixed size for the JList
        userAgentList.setFixedCellWidth(590);
        userAgentList.setFixedCellHeight(20);

        // Wrap the JList in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(userAgentList);
        scrollPane.setPreferredSize(new Dimension(530, 150));
		
		

        gridPanel.setLayout(new GridBagLayout());
        gridPanel.setPreferredSize(new Dimension(600, textHeight*10));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gridPanel.setBorder(BorderFactory.createTitledBorder("User-Agent Selection"));
		
        c = new GridBagConstraints();
		


        activatedButton = new JToggleButton("Activate");
        activatedButton.addChangeListener(e -> {
            if (activatedButton.isSelected()) {
                activatedButton.setText("Deactivate");
            } else {
                activatedButton.setText("Activate");
            }
        });
		
		
		
		fileSelectButton = new JButton("Custom User Agents File");
        fileSelectButton.addActionListener(e -> {
			
				selectCustomFile();
						
            });
		
		defaultSelectButton = new JButton("Reset to Defaults");
        defaultSelectButton.addActionListener(e -> {
				readFileUserAgents("c:\\temp\\useragents.txt");
						
            });
		


		// Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(fileSelectButton);
		buttonPanel.add(defaultSelectButton);
        buttonPanel.add(activatedButton);
		
	
        //c.anchor = GridBagConstraints.EAST;
        //c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        gridPanel.add(scrollPane, c);
		c.gridy = 1;
        gridPanel.add(buttonPanel, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
    

        Dimension buttonDimension = new Dimension(200, new JTextField().getPreferredSize().height);
        activatedButton.setPreferredSize(buttonDimension);
        activatedButton.setMaximumSize(buttonDimension);
        activatedButton.setMinimumSize(buttonDimension);
		
		

        mainPanel.add(gridPanel);

    }
	
	
	
	
	
	
	

    public JPanel getUI() {
        return mainPanel;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public void modifyRequest(IHttpRequestResponse messageInfo) {
		
	
		//Check if the extension is enabled
        if(activatedButton.isSelected()){
       
			IRequestInfo requestInfo = helpers.analyzeRequest(messageInfo);
		
			// Get the headers
			List<String> headers = requestInfo.getHeaders();
			
			// Get the body offset and contents
			int bodyOffset = requestInfo.getBodyOffset();
			String body = new String(messageInfo.getRequest()).substring(bodyOffset);
			
			// URL parts
			//String url1 = requestInfo.getUrl().getProtocol().toString();
			//String url2 = requestInfo.getUrl().getHost().toString();
			//String url3 = requestInfo.getUrl().getPath().toString();
			//String url = url1+ "://" + url2+url3;
			
			int index = (int)(Math.random() * listModel.size());
			String UAValue = listModel.get(index);
			
			// Prepare UA Header Value	
			String UAHeader = "User-Agent: " + 	UAValue;

			// Strip Old header from existing
			headers.removeIf(header -> header.startsWith("User-Agent:"));
			
			// Add New User-Agent header
			requestInfo.getHeaders().add(UAHeader);
			
			// Wooooooooosh
			byte[] newRequest = helpers.buildHttpMessage(requestInfo.getHeaders(), body.getBytes());
			messageInfo.setRequest(newRequest);
			
	   
        }
    }
}
