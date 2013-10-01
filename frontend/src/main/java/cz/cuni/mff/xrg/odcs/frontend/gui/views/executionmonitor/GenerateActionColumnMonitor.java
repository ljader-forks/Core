package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor;

import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.HorizontalLayout;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * Generate the column "actions" in the table with pipeline execution records in
 * {@link ExecutionMonitor} with buttons: Cancel, Show log, Debug data.
 *
 * @author Maria Kukhar
 *
 */
public class GenerateActionColumnMonitor implements CustomTable.ColumnGenerator {

	private ClickListener clickListener = null;

	/**
	 * Set listener
	 *
	 * @param outclickListerner.
	 */
	public GenerateActionColumnMonitor(ClickListener outclickListerner) {
		super();
		this.clickListener = outclickListerner;
	}

	/**
	 * Generate cell on the "actions" column for each table item.
	 */
	@Override
	public Object generateCell(CustomTable source, Object itemId, Object columnId) {
		Property propStatus = source.getItem(itemId).getItemProperty("status");
		Property prop2 = source.getItem(itemId).getItemProperty("isDebugging");
		PipelineExecutionStatus testStatus;
		boolean testDebug;

		HorizontalLayout box = new HorizontalLayout();
		box.setSpacing(true);

		if ((propStatus.getType().equals(PipelineExecutionStatus.class))
				|| (prop2.getType().equals(Boolean.class))) {
			testStatus = (PipelineExecutionStatus) propStatus.getValue();
			testDebug = (boolean) prop2.getValue();

			//If item execution status is SCHEDULED then for that item will be shown Cancel button
			if (testStatus == PipelineExecutionStatus.SCHEDULED
					|| testStatus == PipelineExecutionStatus.RUNNING) {
				Button stopButton = new Button("Cancel");
				stopButton.setData(new ActionButtonData("cancel", itemId));
				stopButton.setWidth("90px");
				box.addComponent(stopButton);
				if (this.clickListener != null) {
					stopButton.addClickListener(this.clickListener);
				}

			}
			//If item execution status is FAILED or FINISHED_SUCCESS then for that item will be shown Show log button
			if (((testStatus == PipelineExecutionStatus.FAILED)
					|| (testStatus == PipelineExecutionStatus.FINISHED_SUCCESS)
					|| (testStatus == PipelineExecutionStatus.FINISHED_WARNING)
					|| (testStatus == PipelineExecutionStatus.RUNNING)
					|| (testStatus == PipelineExecutionStatus.CANCELLED)
					|| (testStatus == PipelineExecutionStatus.CANCELLING))
					&& !testDebug) {
				Button logButton = new Button("Show log");
				logButton.setData(new ActionButtonData("showlog", itemId));

				logButton.setWidth("90px");
				if (this.clickListener != null) {
					logButton.addClickListener(this.clickListener);
				}

				box.addComponent(logButton);

			}

			//If item debug is true then for that item will be shown Debug data button
			if (testDebug && !testStatus.equals(PipelineExecutionStatus.SCHEDULED)) {
				Button debugButton = new Button("Debug data");

				debugButton.setData(new ActionButtonData("debug", itemId));
				debugButton.setWidth("90px");
				if (this.clickListener != null) {
					debugButton.addClickListener(this.clickListener);
				}

				box.addComponent(debugButton);

			}

			if (testStatus != PipelineExecutionStatus.RUNNING
					&& testStatus != PipelineExecutionStatus.CANCELLING) {
				//Re-run button
				Button rerunButton = new Button();
				rerunButton.setDescription("Run pipeline");
				rerunButton.setIcon(new ThemeResource("icons/running.png"), "Run pipeline");
				rerunButton.setData(new ActionButtonData("rerun", itemId));
				if (this.clickListener != null) {
					rerunButton.addClickListener(this.clickListener);
				}
				box.addComponent(rerunButton);

				//Re-debug button
				Button redebugButton = new Button();
				redebugButton.setDescription("Debug pipeline");
				redebugButton.setIcon(new ThemeResource("icons/debug.png"), "Debug pipeline");
				redebugButton.setData(new ActionButtonData("redebug", itemId));
				if (this.clickListener != null) {
					redebugButton.addClickListener(this.clickListener);
				}
				box.addComponent(redebugButton);
			}

		}

		return box;
	}
}
