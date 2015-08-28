package com.twitter.duplicate;

public class postFeature {

	String dupId;
	String relId;
	
	
	double lavDistance;
	double overLapTerm;
	double overLapTags;
	double viewDifference;
	double voteDifference;
	long dateDifference;
	double lengthDifference;
	double answerDifference;
	double commentDifference;
	double scoreDifference;
	String duplicate;
	
	public double overlapEntity;
	public double overlapEntityType;
	
	public String getDuplicate() {
		return duplicate;
	}
	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}
	public double getOverlapEntity() {
		return overlapEntity;
	}
	public void setOverlapEntity(double overlapEntity) {
		this.overlapEntity = overlapEntity;
	}
	public double getOverlapEntityType() {
		return overlapEntityType;
	}
	public void setOverlapEntityType(double overlapEntityType) {
		this.overlapEntityType = overlapEntityType;
	}
	public double getAnswerDifference() {
		return answerDifference;
	}
	public void setAnswerDifference(double answerDifference) {
		this.answerDifference = answerDifference;
	}
	public double getCommentDifference() {
		return commentDifference;
	}
	public void setCommentDifference(double commentDifference) {
		this.commentDifference = commentDifference;
	}
	public double getScoreDifference() {
		return scoreDifference;
	}
	public void setScoreDifference(double scoreDifference) {
		this.scoreDifference = scoreDifference;
	}
	public boolean isAcceptedAnswer() {
		return acceptedAnswer;
	}
	public void setAcceptedAnswer(boolean acceptedAnswer) {
		this.acceptedAnswer = acceptedAnswer;
	}
	boolean codeDiffence;
	boolean acceptedAnswer;
	
	
	
	
	public String getDupId() {
		return dupId;
	}
	public void setDupId(String dupId) {
		this.dupId = dupId;
	}
	public String getRelId() {
		return relId;
	}
	public void setRelId(String relId) {
		this.relId = relId;
	}
	public double getLavDistance() {
		return lavDistance;
	}
	public void setLavDistance(double lavDistance) {
		this.lavDistance = lavDistance;
	}
	public double getOverLapTerm() {
		return overLapTerm;
	}
	public void setOverLapTerm(double overLapTerm) {
		this.overLapTerm = overLapTerm;
	}
	public double getOverLapTags() {
		return overLapTags;
	}
	public void setOverLapTags(double overLapTags) {
		this.overLapTags = overLapTags;
	}
	public double getViewDifference() {
		return viewDifference;
	}
	public void setViewDifference(double viewDifference) {
		this.viewDifference = viewDifference;
	}
	public double getVoteDifference() {
		return voteDifference;
	}
	public void setVoteDifference(double voteDifference) {
		this.voteDifference = voteDifference;
	}
	public long getDateDifference() {
		return (dateDifference);
	}
	public void setDateDifference(long dateDifference) {
		this.dateDifference = dateDifference;
		System.out.println(this.dateDifference);
	}
	public double getLengthDifference() {
		return lengthDifference;
	}
	public void setLengthDifference(double lengthDifference) {
		this.lengthDifference = lengthDifference;
	}
	public boolean isCodeDiffence() {
		return codeDiffence;
	}
	public void setCodeDiffence(boolean codeDiffence) {
		this.codeDiffence = codeDiffence;
	}
	
	
	
	
	
}
