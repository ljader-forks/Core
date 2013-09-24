package cz.cuni.xrg.intlib.frontend.gui.components;


import java.util.List;
import org.vaadin.dialogs.ConfirmDialog;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import cz.cuni.xrg.intlib.commons.app.rdf.namespace.NamespacePrefix;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * GUI for Namespace Prefixes which opens from the Administrator menu. Contains table with
 * namespace prefixes and button for prefixes creation.
 *
 *
 * @author Maria Kukhar
 */

public class NamespacePrefixes {
	
	 private IntlibPagedTable prefixesTable;
	 private VerticalLayout prefixesListLayout;
	 private static String[] visibleCols = new String[]{"name", "uri", "actions"};
	 private static String[] headers = new String[]{"Prefix name", "Prefix URI", "Actions"};
	 private IndexedContainer tableData;
	 private Long prefixId;
	 private NamespacePrefix prefixDel;


	public VerticalLayout buildNamespacePrefixesLayout(){
		
		
		prefixesListLayout = new VerticalLayout();
		prefixesListLayout.setMargin(true);
		prefixesListLayout.setSpacing(true);
		prefixesListLayout.setWidth("100%");

		prefixesListLayout.setImmediate(true);
		
		
		//Layout for buttons Create new prefix and Clear Filters on the top.
		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);

		Button addUserButton = new Button();
		addUserButton.setCaption("Create new prefix");
		addUserButton.setWidth("120px");
		addUserButton
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				boolean newPrefix = true;
				// open usercreation dialog
				PrefixCreate prefix = new PrefixCreate(newPrefix);
				App.getApp().addWindow(prefix);
				prefix.addCloseListener(new CloseListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						refreshData();
					}
				});
			}
		});
		topLine.addComponent(addUserButton);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("120px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				prefixesTable.resetFilters();
				prefixesTable.setFilterFieldVisible("actions", false);
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		prefixesListLayout.addComponent(topLine);
		
        tableData = getTableData(App.getApp().getNamespacePrefixes().getAllPrefixes());

        //table with pipeline execution records
        prefixesTable = new IntlibPagedTable();
        prefixesTable.setSelectable(true);
        prefixesTable.setContainerDataSource(tableData);
        prefixesTable.setWidth("100%");
        prefixesTable.setHeight("100%");
        prefixesTable.setImmediate(true);
        prefixesTable.setVisibleColumns((Object[])visibleCols); // Set visible columns
        prefixesTable.setColumnHeaders(headers);

        //Actions column. Contains actions buttons: Debug data, Show log, Stop.
        prefixesTable.addGeneratedColumn("actions",
                new actionColumnGenerator());

        prefixesListLayout.addComponent(prefixesTable);
        prefixesListLayout.addComponent(prefixesTable.createControls());
        prefixesTable.setPageLength(10);
        prefixesTable.setFilterDecorator(new filterDecorator());
        prefixesTable.setFilterBarVisible(true);
        prefixesTable.setFilterFieldVisible("actions", false);
        prefixesTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {

					private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {

				if (!prefixesTable.isSelected(event.getItemId())) {
					prefixId = (Long) event.getItem().getItemProperty("id").getValue();
					showPrefixSettings(prefixId);
				}
			}
		});

		

		
		return prefixesListLayout;
	}
	
	/**
	 * Calls for refresh table {@link #schedulerTable}.
	 */
	private void refreshData() {
		int page = prefixesTable.getCurrentPage();
		tableData = getTableData(App.getApp().getNamespacePrefixes().getAllPrefixes());
		prefixesTable.setContainerDataSource(tableData);
		prefixesTable.setCurrentPage(page);
		prefixesTable.setVisibleColumns((Object[])visibleCols);
		prefixesTable.setFilterFieldVisible("actions", false);

	}
	
    /**
     * Container with data for {@link #prefixesTable}
     *
     * @param data. List of users
     * @return result. IndexedContainer with data for users table
     */
	@SuppressWarnings("unchecked")
	public static IndexedContainer getTableData(List<NamespacePrefix> data) {

		IndexedContainer result = new IndexedContainer();
		
		for (String p : visibleCols) {
			// setting type of columns
			result.addContainerProperty(p, String.class, "");
			break;
		}
		
		result.addContainerProperty("id", Long.class, "");


		for (NamespacePrefix item : data)  {
			Object num = result.addItem();
			
			result.getContainerProperty(num, "id").setValue(item.getId());
			result.getContainerProperty(num, "name").setValue(item.getName());
			result.getContainerProperty(num, "uri").setValue(item.getPrefixURI());
		}

		return result;

	}
	

    
	/**
	 * Generate column "actions" in the table {@link #prefixesTable}.
	 *
	 * @author Maria Kukhar
	 *
	 */
	class actionColumnGenerator implements CustomTable.ColumnGenerator {

		private static final long serialVersionUID = 1L;


		@Override
		public Object generateCell(final CustomTable source, final Object itemId,
				Object columnId) {

			HorizontalLayout layout = new HorizontalLayout();

			//Edit button. Open dialog for edit user's details.
			Button changeButton = new Button();
			changeButton.setCaption("Edit");
			changeButton.setWidth("80px");
			changeButton.addClickListener(new ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					
					prefixId = (Long) tableData.getContainerProperty(itemId, "id")
							.getValue();
					showPrefixSettings(prefixId);
					
				}
			});
			
			layout.addComponent(changeButton);
		
			//Delete button. Delete user's record from Database.
			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
			deleteButton.setWidth("80px");
			deleteButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					prefixId = (Long) tableData.getContainerProperty(itemId, "id")
							.getValue();
					prefixDel = App.getApp().getNamespacePrefixes().getPrefix(prefixId);
					//open confirmation dialog
					ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting user",
							"Delete the  " + prefixDel.getName() + " user?","Delete", "Cancel",
							new ConfirmDialog.Listener() {

								private static final long serialVersionUID = 1L;

						@Override
						public void onClose(ConfirmDialog cd) {
							if (cd.isConfirmed()) {

								App.getApp().getNamespacePrefixes().delete(prefixDel);
								refreshData();
								
							}
						}
					});
					
					

				}
			});
			layout.addComponent(deleteButton);
			
			return layout;
		}
	}
	
	/**
	 * Shows dialog with Namespace Prefix settings for given prefix.
	 *
	 * @param id Id of user to show.
	 */
	private void showPrefixSettings(Long id) {
		
		boolean newPrefix = false;
		// open usercreation dialog
		PrefixCreate prefixEdit = new PrefixCreate(newPrefix);
		NamespacePrefix prefix =  App.getApp().getNamespacePrefixes().getPrefix(id);
		prefixEdit.setSelectedPrefix(prefix);
		
		App.getApp().addWindow(prefixEdit);
	
		
		prefixEdit.addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				refreshData();
			}
		});
		
		


	}
	
	private class filterDecorator extends IntlibFilterDecorator {

		private static final long serialVersionUID = 1L;

		@Override
        public String getEnumFilterDisplayName(Object propertyId, Object value) {

            return super.getEnumFilterDisplayName(propertyId, value);
        }
	};
}