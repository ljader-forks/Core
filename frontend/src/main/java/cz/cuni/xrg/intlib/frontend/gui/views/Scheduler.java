package cz.cuni.xrg.intlib.frontend.gui.views;

import com.sun.swing.internal.plaf.basic.resources.basic;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibFilterDecorator;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.frontend.gui.components.SchedulePipeline;

/**
 * GUI for Scheduler page which opens from the main menu. Contains table with
 * scheduler rules and button for scheduler rule creation.
 *
 *
 * @author Maria Kukhar
 */
class Scheduler extends ViewComponent {

	private VerticalLayout mainLayout;
	private Label label;
	private IntlibPagedTable schedulerTable;
	private IndexedContainer tableData;
	static String[] visibleCols = new String[]{"pipeline", "rule", "user",
		"last", "next", "status", "commands"};
	static String[] headers = new String[]{"pipeline", "Rule", "User",
		"Last", "Next", "Status", "Commands"};
	private DateFormat localDateFormat = null;
	int style = DateFormat.MEDIUM;
	private Long schId;
	static String filter;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public Scheduler() {
	}

	/**
	 * Builds main layout
	 *
	 */
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		//Layout for buttons Add new scheduling rule and Clear Filters on the top.
		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);
		topLine.setWidth(100, Unit.PERCENTAGE);

		Button addRuleButton = new Button();
		addRuleButton.setCaption("Add new scheduling rule");
		addRuleButton
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// open scheduler dialog
				SchedulePipeline sch = new SchedulePipeline();
				App.getApp().addWindow(sch);
				sch.addCloseListener(new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						refreshData();
					}
				});
			}
		});
		topLine.addComponent(addRuleButton);
		topLine.setComponentAlignment(addRuleButton, Alignment.MIDDLE_RIGHT);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("110px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				schedulerTable.resetFilters();
			}
		});
		topLine.addComponent(buttonDeleteFilters);
		topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);

		Label topLineFiller = new Label();
		topLine.addComponentAsFirst(topLineFiller);
		topLine.setExpandRatio(topLineFiller, 1.0f);
		mainLayout.addComponent(topLine);

		tableData = getTableData(App.getApp().getSchedules().getAllSchedules());

		//table with schedule rules records
		schedulerTable = new IntlibPagedTable();
		schedulerTable.setSelectable(true);
		schedulerTable.setContainerDataSource(tableData);
		schedulerTable.setWidth("100%");
		schedulerTable.setHeight("100%");
		schedulerTable.setImmediate(true);
		schedulerTable.setVisibleColumns(visibleCols); // Set visible columns
		schedulerTable.setColumnHeaders(headers);
		//Commands column. Contains commands buttons: Enable/Disable, Edit, Delete
		schedulerTable.addGeneratedColumn("commands",
				new actionColumnGenerator());
		//Debug column. Contains debug icons.
		schedulerTable.addGeneratedColumn("status", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId,
					Object columnId) {
				boolean isEnabled = (boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (isEnabled) {
					return "Enabled";
				} else {
					return "Disabled";
				}
			}
		});
		schedulerTable.setFilterDecorator(new filterDecorator());
		schedulerTable.setFilterFieldVisible("commands", false);
		mainLayout.addComponent(schedulerTable);
		mainLayout.addComponent(schedulerTable.createControls());
		schedulerTable.setPageLength(20);
		schedulerTable.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {

				if (!schedulerTable.isSelected(event.getItemId())) {
					schId = (Long) event.getItem().getItemProperty("schid").getValue();
					showSchedulePipeline(schId);
				}
			}
		});

		return mainLayout;
	}

	/**
	 * Refresh scheduler table data.
	 */
	private void refreshData() {
		int page = schedulerTable.getCurrentPage();
		tableData = getTableData(App.getApp().getSchedules().getAllSchedules());
		schedulerTable.setContainerDataSource(tableData);
		schedulerTable.setCurrentPage(page);
		schedulerTable.setVisibleColumns(visibleCols);
		schedulerTable.setFilterFieldVisible("commands", false);

	}

	/**
	 * Container with data for scheduler table.
	 *
	 * @param data. List of Schedule.
	 * @return result. IndexedContainer with data for scheduler table.
	 */
	public static IndexedContainer getTableData(List<Schedule> data) {

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			// setting type of columns
			switch (p) {
				case "last":
				case "next":
					result.addContainerProperty(p, Date.class, null);
					break;
				case "status":
					result.addContainerProperty(p, Boolean.class, false);
					break;
				default:
					result.addContainerProperty(p, String.class, "");
					break;
			}

		}
		result.addContainerProperty("schid", Long.class, "");

		for (Schedule item : data) {

			Object num = result.addItem();

			if (item.getFirstExecution() == null) {
				result.getContainerProperty(num, "next").setValue(null);
			} else {
				result.getContainerProperty(num, "next").setValue(
						item.getNextExecutionTimeInfo());
			}

			if (item.getLastExecution() == null) {
				result.getContainerProperty(num, "last").setValue(null);
			} else {

				result.getContainerProperty(num, "last").setValue(
						item.getLastExecution());
			}

			
			result.getContainerProperty(num, "status").setValue(item.isEnabled());
			

			if (item.getType().equals(ScheduleType.PERIODICALLY)) {
				if (item.isJustOnce()) {
					result.getContainerProperty(num, "rule").setValue(
							"Run on " + item.getFirstExecution().toLocaleString());
				} else {
					if (item.getPeriod().equals((long) 1)) {
						result.getContainerProperty(num, "rule").setValue(
								"Run on "
								+ item.getFirstExecution().toLocaleString()
								+ " and then repeat every "
								+ item.getPeriodUnit().toString()
								.toLowerCase());
					} else {
						result.getContainerProperty(num, "rule").setValue(
								"Run on "
								+ item.getFirstExecution().toLocaleString()
								+ " and then repeat every "
								+ item.getPeriod().toString()
								+ " "
								+ item.getPeriodUnit().toString()
								.toLowerCase() + "s");
					}
				}
			} else {

				Set<Pipeline> after = item.getAfterPipelines();
				String afterPipelines = "";
				after.size();
				int i = 0;
				for (Pipeline afteritem : after) {
					i++;
					if (i < after.size()) {
						afterPipelines = afterPipelines + afteritem.getName() + ", ";
					} else {
						afterPipelines = afterPipelines + afteritem.getName() + ". ";
					}
				}
				if (after.size() > 1) {
					result.getContainerProperty(num,
							"rule").setValue("Run after pipelines: " + afterPipelines);
				} else {
					result.getContainerProperty(num,
							"rule").setValue("Run after pipeline: " + afterPipelines);
				}
			}

			result.getContainerProperty(num, "schid").setValue(item.getId());
			result.getContainerProperty(num, "user").setValue(" ");
			result.getContainerProperty(num, "pipeline").setValue(
					item.getPipeline().getName());


		}

		return result;

	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Generate column "commands" in the scheduler table.
	 *
	 * @author Maria Kukhar
	 *
	 */
	class actionColumnGenerator implements CustomTable.ColumnGenerator {

		private ClickListener clickListener = null;

		@Override
		public Object generateCell(final CustomTable source, final Object itemId,
				Object columnId) {
			Property propStatus = source.getItem(itemId).getItemProperty(
					"status");
			boolean testStatus;

			HorizontalLayout layout = new HorizontalLayout();

			if (propStatus.getType().equals(Boolean.class)) {

				testStatus = (Boolean) propStatus.getValue();
				//If item in the scheduler table has Disabled status, then for that item will be shown
				//Enable button
				if (!testStatus) {
					Button enableButton = new Button("Enable");
					enableButton.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {

							schId = (Long) tableData.getContainerProperty(itemId, "schid")
									.getValue();
							List<Schedule> schedulers = App.getApp().getSchedules().getAllSchedules();
							for (Schedule item : schedulers) {
								if (item.getId().equals(schId)) {
									item.setEnabled(true);
									App.getApp().getSchedules().save(item);
								}
							}
							refreshData();

						}
					});
					layout.addComponent(enableButton);

				} //If item in the scheduler table has Enabled status, then for that item will be shown
				//Disable button
				else {
					Button disableButton = new Button();
					disableButton.setCaption("Disable");
					disableButton.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {

							schId = (Long) tableData.getContainerProperty(itemId, "schid")
									.getValue();
							List<Schedule> schedulers = App.getApp().getSchedules().getAllSchedules();
							for (Schedule item : schedulers) {
								if (item.getId().equals(schId)) {
									item.setEnabled(false);
									App.getApp().getSchedules().save(item);
								}
							}
							refreshData();

						}
					});
					layout.addComponent(disableButton);
				}

			}
			//Edit button. Opens the window for editing given scheduling rule.
			Button editButton = new Button();
			editButton.setCaption("Edit");
			editButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					schId = (Long) tableData.getContainerProperty(itemId, "schid")
							.getValue();
					showSchedulePipeline(schId);
				}
			});
			layout.addComponent(editButton);

			//Edit button. Delete scheduling rule from the table.
			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
			deleteButton.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {

					schId = (Long) tableData.getContainerProperty(itemId, "schid")
							.getValue();
					Schedule schedule = App.getApp().getSchedules().getSchedule(schId);
					App.getApp().getSchedules().delete(schedule);
					refreshData();
				}
			});
			layout.addComponent(deleteButton);

			return layout;
		}
	}

	/**
	 * Shows dialog for scheduling pipeline with given scheduling rule.
	 *
	 * @param id Id of schedule to show.
	 */
	private void showSchedulePipeline(Long id) {

		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();

		//openScheduler(schedule);
		Schedule schedule = App.getApp().getSchedules().getSchedule(id);
		sch.setSelectedSchedule(schedule);

		App.getApp().addWindow(sch);
		sch.addCloseListener(new CloseListener() {
			@Override
			public void windowClose(CloseEvent e) {
				refreshData();
			}
		});
	}

	private class filterDecorator extends IntlibFilterDecorator {

		@Override
		public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
			if (value) {
				return "Enabled";
			} else {
				return "Disabled";
			}
		}
	};
}
