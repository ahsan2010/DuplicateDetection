

import java.io.Serializable;

public class Posts implements Serializable{

	public int Id;
	public int PostTypeId;
	public int ParentId;
	public int AcceptedAnswerId;
	public String CreationDate;
	public String Body;
	public String Tags;
	public String Title;
	public int AnswerCount;
	public int CommentCount;
	public int ViewCount;
	public int score;
	public int FavouriteCount;
	public String closedDate;
	public int OwnerUserId;
	
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getFavouriteCount() {
		return FavouriteCount;
	}

	public void setFavouriteCount(int favouriteCount) {
		FavouriteCount = favouriteCount;
	}

	public String getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(String closedDate) {
		this.closedDate = closedDate;
	}

	public int getOwnerUserId() {
		return OwnerUserId;
	}

	public void setOwnerUserId(int ownerUserId) {
		OwnerUserId = ownerUserId;
	}

	public int getViewCount() {
		return ViewCount;
	}

	public void setViewCount(int viewCount) {
		ViewCount = viewCount;
	}

	public Posts(){
		
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getPostTypeId() {
		return PostTypeId;
	}
	public void setPostTypeId(int postTypeId) {
		PostTypeId = postTypeId;
	}
	public int getParentId() {
		return ParentId;
	}
	public void setParentId(int parentId) {
		ParentId = parentId;
	}
	public int getAcceptedAnswerId() {
		return AcceptedAnswerId;
	}
	public void setAcceptedAnswerId(int acceptedAnswerId) {
		AcceptedAnswerId = acceptedAnswerId;
	}
	public String getCreationDate() {
		return CreationDate;
	}
	public void setCreationDate(String creationDate) {
		CreationDate = creationDate;
	}
	public String getBody() {
		return Body;
	}
	public void setBody(String body) {
		Body = body;
	}
	public String getTags() {
		return Tags;
	}
	public void setTags(String tags) {
		Tags = tags;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public int getAnswerCount() {
		return AnswerCount;
	}
	public void setAnswerCount(int answerCount) {
		AnswerCount = answerCount;
	}
	public int getCommentCount() {
		return CommentCount;
	}
	public void setCommentCount(int commentCount) {
		CommentCount = commentCount;
	}
	
	
	
}
