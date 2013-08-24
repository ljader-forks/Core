package cz.cuni.xrg.intlib.frontend.gui;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import cz.cuni.xrg.intlib.commons.app.auth.AuthenticationContextService;
import cz.cuni.xrg.intlib.commons.app.user.User;

import cz.cuni.xrg.intlib.frontend.auxiliaries.App;


/**
 * Class represent main application component.
 * The component contains menu bar and a place where to place
 * application view.
 *
 * @author Petyr
 *
 */
public class MenuLayout extends CustomComponent {

	private static final long serialVersionUID = 1L;
	/**
	 * Used layout.
	 */
	private VerticalLayout mainLayout;
	/**
	 * Menu bar.
	 */
	private MenuBar menuBar;
	/**
	 * Layout for application views.
	 */
	private Panel viewLayout;

	Label userName;

	Button logOutButton;
	
	/**
	 * Authentication context used to render menu with respect to currently
	 * logged in user.
	 */
	private AuthenticationContextService authCtx;

	/**
	 * Class use as command to change sub-pages.
	 * @author Petyr
	 */
	private class NavigateToCommand implements Command {

		private static final long serialVersionUID = 1L;
		/**
		 * Url to navigate on.
		 */
		private String url;

		public NavigateToCommand (String url) {
			this.url = url;
		}

                @Override
		public void menuSelected(MenuItem selectedItem) {
			App.getApp().getNavigator().navigateTo(this.url);
		}

	}

	/**
	 * Return layout for application views.
	 * @return
	 */
	public Panel getViewLayout() {
		return this.viewLayout;
	}

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public MenuLayout() {
		authCtx = App.getApp().getAuthCtx();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// init menuBar
		menuBar.addItem("Pipelines", new NavigateToCommand(ViewNames.PipelineList.getUrl()) );
		menuBar.addItem("DPU Templates", new NavigateToCommand(ViewNames.DPU.getUrl()) );
		menuBar.addItem("Execution Monitor", new NavigateToCommand(ViewNames.ExecutionMonitor.getUrl()) );
		menuBar.addItem("Browse Data", new NavigateToCommand(ViewNames.DataBrowser.getUrl()) );
		menuBar.addItem("Scheduler", new NavigateToCommand(ViewNames.Scheduler.getUrl()) );
		menuBar.addItem("Settings", new NavigateToCommand(ViewNames.Administrator.getUrl()) );
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		this.mainLayout = new VerticalLayout();
		this.mainLayout.setImmediate(false);
		this.mainLayout.setMargin(false);

		// top-level component properties
		this.setWidth("100.0%");
		//this.setSizeUndefined();

		// menuBar
		this.menuBar = new MenuBar();
		this.menuBar.setImmediate(false);
		this.menuBar.setWidth("100.0%");
		this.menuBar.setHeight("20px");
		//this.mainLayout.addComponent(menuBar);
                
                userName = new Label(authCtx.getUsername());
                userName.setWidth(100, Unit.PIXELS);
                logOutButton = new Button();
                logOutButton.setWidth(16, Unit.PIXELS);
                logOutButton.setVisible(authCtx.isAuthenticated());
                logOutButton.setStyleName(BaseTheme.BUTTON_LINK);
                logOutButton.setIcon(new ThemeResource("icons/logout.png"), "Log out");
                logOutButton.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        App.getApp().getAuthCtx().clear();
                        refreshUserBar();
                        App.getApp().getNavigator().navigateTo(ViewNames.Login.getUrl());
                    }
                });
                HorizontalLayout menuLine = new HorizontalLayout(menuBar, userName, logOutButton);
                menuLine.setWidth(100, Unit.PERCENTAGE);
                menuLine.setExpandRatio(menuBar, 1.0f);
                this.mainLayout.addComponent(menuLine);
                
		// viewLayout
		this.viewLayout = new Panel();
		//this.viewLayout.setWidth("100.0%");
		//this.viewLayout.setHeight("100.0%");
		//this.viewLayout.setMargin(false);
		this.viewLayout.setStyleName("viewLayout");
		this.mainLayout.addComponent(viewLayout);

		return this.mainLayout;
	}
        
        public void refreshUserBar() {
            userName.setValue(authCtx.getUsername());
            logOutButton.setVisible(authCtx.isAuthenticated());
        }

}
