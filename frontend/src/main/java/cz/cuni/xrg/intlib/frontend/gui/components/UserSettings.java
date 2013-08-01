package cz.cuni.xrg.intlib.frontend.gui.components;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Dialog for the User settings creation which opens from the main menu. 
 * Allows to set e-mail for get notifications and set scheduler notifications.
 * 
 * @author Maria Kukhar
 *
 */


public class UserSettings extends Window {
	
	private HorizontalLayout mainLayout;
	private Panel mainPanel;
	private HorizontalSplitPanel hsplit;
	private VerticalLayout tabsLayout;
	private VerticalLayout settingsLayout;
	private VerticalLayout schedulerLayout;
	private GridLayout gridLayoutEmail;
	private TextField textFieldEmail;
	private Button shownTab = null;
	private ComponentContainer componentContainer;
	private Button schedulerButton;
	private Button myAccountButton;

	private Button buttonEmailhRem;

	private Button buttonEmailAdd;

	private InvalidValueException ex;
	
	public UserSettings(){
		
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("User Settings");
		
		mainLayout = new HorizontalLayout();
		mainLayout.setMargin(true);

        
        tabsLayout = new VerticalLayout();
        tabsLayout.setWidth("105px");
        tabsLayout.setImmediate(true);

        
        settingsLayout = new VerticalLayout();
        settingsLayout.setMargin(true);
        settingsLayout.setSpacing(true);
        settingsLayout.setWidth("370px");
        settingsLayout.setImmediate(true);
        settingsLayout.setStyleName("settings");
       
        schedulerLayout = new VerticalLayout();
        schedulerLayout.setMargin(true);
        schedulerLayout.setSpacing(true);
        schedulerLayout.setWidth("370px");
        schedulerLayout.setHeight("300px");
        schedulerLayout.setImmediate(true);
        schedulerLayout.setStyleName("settings");


        myAccountButton = new NativeButton("My account");
        myAccountButton.setHeight("40px");
        myAccountButton.setWidth("90px");
        myAccountButton.setStyleName("selectedtab");
        myAccountButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=myAccountButton;
				shownTab.setStyleName("selectedtab");
				schedulerButton.setStyleName("multiline");
				mainLayout.removeComponent(settingsLayout);
				mainLayout.removeComponent(schedulerLayout);
				mainLayout.addComponent(settingsLayout);

				
			}
		});
        tabsLayout.addComponent(myAccountButton);
        tabsLayout.setComponentAlignment(myAccountButton, Alignment.TOP_RIGHT);
        
    
        schedulerButton = new NativeButton("Scheduler\r\n notifications");
        schedulerButton.setHeight("40px");
        schedulerButton.setWidth("90px");
        schedulerButton.setStyleName("multiline");
        schedulerButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=schedulerButton;
				shownTab.setStyleName("selectedtab");
				myAccountButton.setStyleName("multiline");
				
				mainLayout.removeComponent(settingsLayout);
				mainLayout.removeComponent(schedulerLayout);
				mainLayout.addComponent(schedulerLayout);

				
			}
		});
        tabsLayout.addComponent(schedulerButton);
        tabsLayout.setComponentAlignment(schedulerButton, Alignment.TOP_RIGHT);
        
     

        
		//Layout with buttons Save and Cancel
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		buttonBar.addComponent(saveButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {

			/**
			 * Closes DPU Template creation window
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
			close();

			}
		});
		buttonBar.addComponent(cancelButton);
		
		
        settingsLayout.addComponent(new Label("E-mail notifications to: "));
        initializeEmailList();
		settingsLayout.addComponent(gridLayoutEmail);
		settingsLayout.addComponent(buttonBar);
		settingsLayout.setComponentAlignment(buttonBar, Alignment.BOTTOM_RIGHT);
		
		mainLayout.addComponent(tabsLayout);
		mainLayout.addComponent(settingsLayout);

		this.setContent(mainLayout);
		setSizeUndefined();
		
	}
	
  

		  
	
	/**
	 * List<String> that contains e-mails.
	 */
	private List<String> griddata = initializeEmailData();

	/**
	 * Initializes data of the E-mail notification component
	 */
	private static List<String> initializeEmailData() {
		List<String> result = new LinkedList<>();
		result.add("");

		return result;

	}

	/**
	 * Add new data to E-mail notification component.
	 *
	 * @param newData. String that will be added
	 */
	private void addDataToEmailData(String newData) {
		griddata.add(newData.trim());
	}

	/**
	 * Remove data from E-mail notification component. Only if component contain more
	 * then 1 row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeDataEmailData(Integer row) {
		int index = row;
		if (griddata.size() > 1) {
			griddata.remove(index);
		}
	}

	private List<TextField> listedEditText = null;

	/**
	 * Save edited texts in the E-mail notification component
	 */
	private void saveEditedTexts() {
		griddata = new LinkedList<>();
		for (TextField editText : listedEditText) {
			griddata.add(editText.getValue().trim());
		}
	}


	/**
	 * Builds E-mail notification component which consists of textfields for e-mail
	 * and buttons for add and remove this textfields. Used in
//	 * {@link #initializeEmailList} and also in adding and removing fields
	 * for component refresh
	 */
	private void refreshEmailData() {
		gridLayoutEmail.removeAllComponents();
		int row = 0;
		listedEditText = new ArrayList<>();
		if (griddata.size() < 1) {
			griddata.add("");
		}
		gridLayoutEmail.setRows(griddata.size() + 1);
		for (String item : griddata) {
			textFieldEmail = new TextField();
			listedEditText.add(textFieldEmail);

			//text field for the graph
			textFieldEmail.setWidth("100%");
			textFieldEmail.setData(row);
			textFieldEmail.setValue(item.trim());
			textFieldEmail.setInputPrompt("franta@test.cz");
			textFieldEmail.addValidator(new Validator() {
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (value != null) {

						String email = value.toString().toLowerCase()
								.trim();

						if (email.isEmpty()) {
							return;
						}

						if (email.contains(" ")) {
							ex = new InvalidValueException(
									"E-mail(s) must contain no white spaces");
							throw ex;
						} 

					}

				}
			});

			//remove button
			buttonEmailhRem = new Button();
			buttonEmailhRem.setWidth("55px");
			buttonEmailhRem.setCaption("-");
			buttonEmailhRem.setData(row);
			buttonEmailhRem.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveEditedTexts();
					Button senderButton = event.getButton();
					Integer row = (Integer) senderButton.getData();
					removeDataEmailData(row);
					refreshEmailData();
				}
			});
			gridLayoutEmail.addComponent(textFieldEmail, 0, row);
			gridLayoutEmail.addComponent(buttonEmailhRem, 1, row);
			gridLayoutEmail.setComponentAlignment(buttonEmailhRem,
					Alignment.TOP_RIGHT);
			row++;
		}
		//add button
		buttonEmailAdd = new Button();
		buttonEmailAdd.setCaption("+");
		buttonEmailAdd.setImmediate(true);
		buttonEmailAdd.setWidth("55px");
		buttonEmailAdd.setHeight("-1px");
		buttonEmailAdd.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveEditedTexts();
				addDataToEmailData(" ");
				refreshEmailData();
			}
		});
		gridLayoutEmail.addComponent(buttonEmailAdd, 0, row);

	}

	/**
	 * Initializes E-mail notification component.
	 */
	private void initializeEmailList() {

		gridLayoutEmail = new GridLayout();
		gridLayoutEmail.setImmediate(false);
		gridLayoutEmail.setWidth("100%");
		gridLayoutEmail.setHeight("100%");
		gridLayoutEmail.setMargin(false);
		gridLayoutEmail.setColumns(2);
		gridLayoutEmail.setColumnExpandRatio(0, 0.95f);
		gridLayoutEmail.setColumnExpandRatio(1, 0.05f);

		refreshEmailData();

	}

	

}
