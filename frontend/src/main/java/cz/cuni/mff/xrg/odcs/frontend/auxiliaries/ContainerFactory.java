package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.container.IntlibLazyQueryContainer;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Level;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class support creating Vaadin container from List<?>.
 *
 * @author Petyr
 *
 */
@Transactional(readOnly = true)
public class ContainerFactory {
	
	@PersistenceContext
	private EntityManager em;

	/**
	 * Create container for Pipelines and fill it with given data.
	 *
	 * @param data data for container
	 * @return
	 */
	public Container createPipelines() {
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer(em, Pipeline.class, 10, "id", true, true, true);
		container.getQueryView().getQueryDefinition().setDefaultSortState(
				new Object[]{"id"}, new boolean[]{true});
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("name", String.class, "", true, true);
		container.addContainerProperty("description", String.class, "", true, true);

		return container;
	}
	
	/**
	 * Factory for table container with pipeline execution data.
	 * 
	 * @return container with pipeline executions
	 */
	public Container createExecutions() {
		
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, PipelineExecution.class, 20, "id", true, true, true);
		container.getQueryView().getQueryDefinition().setDefaultSortState(
				new Object[]{"start"}, new boolean[]{true}
		);

		container.addContainerProperty("id", Long.class, "");
		// Type used for date needs to be java.sql.Timestamp, because there
		// seems to be a bug in com.vaadin.data.util.filter.Compare#compareValue
		// that causes subclasses of java.util.Date to be uncomparable with
		// its superclass java.util.Date.
		// For details see github issue #135.
		container.addContainerProperty("start", Timestamp.class, null);
		container.addContainerProperty("pipeline.name", String.class, "");
		container.addContainerProperty("duration", String.class, "");
		container.addContainerProperty("status", PipelineExecutionStatus.class, null);
		container.addContainerProperty("isDebugging", Boolean.class, false);

		return container;
	}

	/**
	 * Create container for DPUTemplateRecord and fill it with given data.
	 *
	 * @param data data for container
	 * @return
	 */
	public Container createDPUTemplates(List<DPUTemplateRecord> data) {
		BeanContainer<Long, DPUTemplateRecord> container = new BeanContainer<>(DPUTemplateRecord.class);

		// set container id
		container.setBeanIdProperty("id");

		for (DPUTemplateRecord item : data) {
			container.addBean(item);
		}
		return container;
	}

	public Container createExecutionMessages() {
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, MessageRecord.class, 20, "id", true, true, true);
		container.getQueryView().getQueryDefinition().setDefaultSortState(
				new Object[]{"time"}, new boolean[]{true});
		container.getQueryView().getQueryDefinition().setMaxNestedPropertyDepth(1);
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("time", Date.class, null, true, true);
		container.addContainerProperty("type", MessageRecordType.class, MessageRecordType.DPU_INFO, true, true);
		container.addContainerProperty("dpuInstance.name", String.class, "", true, true);
		container.addContainerProperty("shortMessage", String.class, "", true, true);

		return container;
	}

	@Transactional
	public Container createRDFData(List<RDFTriple> data) {
		BeanContainer<Long, RDFTriple> container = new BeanContainer<>(RDFTriple.class);

		container.setBeanIdProperty("id");
		container.addAll(data);

		return container;
	}

	public IntlibLazyQueryContainer createLogMessages() {
		IntlibLazyQueryContainer container = new IntlibLazyQueryContainer<>(em, LogMessage.class, 28, "id", true, true, true);
		container.addContainerProperty("id", Long.class, 0, true, true);
		container.addContainerProperty("thread", String.class, "", true, true);
		container.addContainerProperty("level", Level.class, Level.ALL, true, true);
		container.addContainerProperty("source", String.class, "", true, true);
		container.addContainerProperty("message", String.class, "", true, true);
		container.addContainerProperty("date", Date.class, null, true, true);

		//container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, Integer.class, 0, true, true);
		//container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, Integer.class, 0, true, true);
		//container.addContainerProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME, Long.class, 0, true, false);

		return container;
	}
}