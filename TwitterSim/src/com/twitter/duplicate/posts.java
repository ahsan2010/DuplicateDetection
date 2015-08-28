package com.twitter.duplicate;

public class posts {

	
	String dupId ;
	String postId ;
	String accAnswerId;
	String creationDate ;
	String score ;
	String viewCount ;
	String body ;
	String title ;
	String tags ;
	String commentCount ;
	String favoriteCount ;
	String closedDate ;
	String ownerUserId ;
	String detecDuplicate ;
	
	
	
	public String getDupId() {
		return dupId;
	}
	public void setDupId(String dupId) {
		this.dupId = dupId;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getAccAnswerId() {
		return accAnswerId;
	}
	public void setAccAnswerId(String accAnswerId) {
		this.accAnswerId = accAnswerId;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getViewCount() {
		return viewCount;
	}
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	public String getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(String favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public String getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(String closedDate) {
		this.closedDate = closedDate;
	}
	public String getOwnerUserId() {
		return ownerUserId;
	}
	public void setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
	}
	public String getDetecDuplicate() {
		return detecDuplicate;
	}
	public void setDetecDuplicate(String detecDuplicate) {
		this.detecDuplicate = detecDuplicate;
	}
	
	
	
	public void showInfo(){
		
		System.out.println("dupId : " + dupId);
		System.out.println("PostId : " + postId);
		System.out.println("Body : " + body);
		System.out.println("duplicatDate: " + detecDuplicate);
		System.out.println("Tags: " + tags);
		
		
	}
	
	
}
