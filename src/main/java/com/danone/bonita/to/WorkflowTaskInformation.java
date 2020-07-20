package com.danone.bonita.to;

/**
 * Transfer object use to pass all infromation of a task to a document from Bonita
 *
 * @author agarandel
 *
 */
public class WorkflowTaskInformation {

	/**
	 * Id workflow Bonita
	 */
	private Long idWorkflow;

	/**
	 * Id workflow Bonita
	 */
	private Long idWorkflowTask;

	/**
	 * Id of working document on Dandoc
	 */
	private String workingDocument;

	/**
	 * Step on workflow
	 */
	private BpmsStep step;

	/**
	 * State of the workflow
	 */
	private BpmsState state;

	/**
	 * Username of actor assign to the task
	 */
	private String actor;

	/**
	 * Comment of the task
	 */
	private String comment;
	
	/**
	 * Access of the task
	 */
	private String access;

	public Long getIdWorkflow() {
		return idWorkflow;
	}

	public void setIdWorkflow(Long idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public Long getIdWorkflowTask() {
		return idWorkflowTask;
	}

	public void setIdWorkflowTask(Long idWorkflowTask) {
		this.idWorkflowTask = idWorkflowTask;
	}

	public String getWorkingDocument() {
		return workingDocument;
	}

	public void setWorkingDocument(String workingDocument) {
		this.workingDocument = workingDocument;
	}

	public BpmsStep getStep() {
		return step;
	}

	public void setStep(BpmsStep step) {
		this.step = step;
	}

	public BpmsState getState() {
		return state;
	}

	public void setState(BpmsState state) {
		this.state = state;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

}
