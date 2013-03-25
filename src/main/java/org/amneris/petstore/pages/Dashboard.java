package org.amneris.petstore.pages;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Dashboard
{

	@Inject
	private ProcessEngine processEngine;

	@Inject
	private RuntimeService runtimeService;

	@Inject
	private TaskService taskService;

	@InjectComponent
	private Zone taskDetails;

	@Property
	private Task taskIterator;

	@Property
	private Task task;

	public Integer getPDef()
	{
		java.util.List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()
				.list();

		return processDefinitions.size();
	}

	public Long getRunningProcessInstances()
	{
		return runtimeService.createProcessInstanceQuery().count();
	}

	@OnEvent("start")
	void start() {
		runtimeService.startProcessInstanceByKey("SimpleProcess");
	}

	@OnEvent("complete")
	void complete(String taskId) {
		taskService.complete(taskId);
	}

	public Long getTasks()
	{
		return taskService.createTaskQuery().count();
	}

	public java.util.List<Task> getTasksSource()
	{
		return taskService.createTaskQuery().list();
	}

	@OnEvent("show")
	Object show(String taskId)
	{
		task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return taskDetails.getBody();
	}


}