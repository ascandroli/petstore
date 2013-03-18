package org.amneris.petstore.pages;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Activiti
{

	@Inject
	private ProcessEngine processEngine;

	@Inject
	private RuntimeService runtimeService;

	@Inject
	private TaskService taskService;

	@Inject
	private RepositoryService repositoryService;

	@Property
	private Task taskIterator;

	@Property
	private ProcessDefinition processDefinitionIterator;

	@OnEvent(EventConstants.ACTIVATE)
	Object onActivate()
	{
		return null;
	}


	public List<ProcessDefinition> getProcessDefinitions()
	{
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
		return processDefinitions;
	}

	public List<ProcessInstance> getProcessInstances()
	{
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
		return processInstances;
	}

	public List<Task> getTasks()
	{
		List<Task> tasks = taskService.createTaskQuery().list();
		return tasks;
	}


	@OnEvent("start")
	void startProcessInstance(String key)
	{
		runtimeService.startProcessInstanceByKey(key);
	}

	@OnEvent("complete")
	void complete(String id)
	{
		taskService.complete(id);
	}

}
