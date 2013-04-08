package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;

public class DPU extends CustomComponent implements View {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Label label;

	private Label lblUri;
	
	private TextField txtUri;
	
	private Button btnOpenDialog;
	
	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public DPU() {

	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("600px");
		mainLayout.setHeight("800px");
		
		// top-level component properties
		setWidth("600px");
		setHeight("800px");
		
		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<h1>DPUs</h>");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label, "top:30.0px;left:80.0px;");
		
		lblUri = new Label();
		lblUri.setValue("uri:");
		mainLayout.addComponent(lblUri, "top:100.0px;left:80.0px;");
		
		txtUri = new TextField();
		txtUri.setWidth("250px");
		txtUri.setHeight("25px");
		mainLayout.addComponent(txtUri, "top:120.0px;left:80.0px;");
		
		btnOpenDialog = new Button();
		btnOpenDialog.setCaption("show dialog");
		btnOpenDialog.setWidth("100px");
		btnOpenDialog.setHeight("25px");
		btnOpenDialog.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				App.getApp().getNavigator().navigateTo( 
						"expDialog/" + txtUri.getValue() );			
			}
		});
		mainLayout.addComponent(btnOpenDialog, "top:170.0px;left:200.0px;");
		
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
