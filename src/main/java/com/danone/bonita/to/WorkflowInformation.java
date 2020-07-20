package com.danone.bonita.to;

/**
 * Transfer object use to pass all infromation of a task to a document from Bonita
 *
 * @author agarandel
 *
 */
public class WorkflowInformation {
	/**
	 * Id workflow Bonita
	 */
	private Long idWorkflow;
	/**
	 * Workflow type
	 * Values : UPDATE,CREATE,DELETE
	 */
	private String typeWorkflow;

	/**
	 * Id of working document on Dandoc
	 */
	private String workingDocument;

	/**
	 * Id of original document on Dandoc
	 */
	private String originalDocument;

	/**
	 * Username of document
	 */
	private String documentOwner;

	/**
	 * state of the workflow
	 */
	private BpmsState state;

	/**
	 * Comment of the workflow
	 */
	private String comment;
	
	/**
	 * Date of the end of the workflow
	 */
	private String issueDate;

	public Long getIdWorkflow() {
		return idWorkflow;
	}

	public void setIdWorkflow(Long idWorkflow) {
		this.idWorkflow = idWorkflow;
	}

	public String getTypeWorkflow() {
		return typeWorkflow;
	}

	public void setTypeWorkflow(String typeWorkflow) {
		this.typeWorkflow = typeWorkflow;
	}

	public String getWorkingDocument() {
		return workingDocument;
	}

	public void setWorkingDocument(String workingDocument) {
		this.workingDocument = workingDocument;
	}

	public String getOriginalDocument() {
		return originalDocument;
	}

	public void setOriginalDocument(String originalDocument) {
		this.originalDocument = originalDocument;
	}

	public String getDocumentOwner() {
		return documentOwner;
	}

	public void setDocumentOwner(String documentOwner) {
		this.documentOwner = documentOwner;
	}

	public BpmsState getState() {
		return state;
	}

	public void setState(BpmsState state) {
		this.state = state;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

}
