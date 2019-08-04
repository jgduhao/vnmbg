package entity;

import io.vertx.core.json.JsonObject;

public class Post {

    private String postId;
    private String postNo;
    private String boardSign;
    private String posterSign;
    private String replyPostNo;
    private String content;
    private Boolean available;
    private Boolean sage;
    private String createTime;
    private String updateTime;

    public Post(){}

    public Post(JsonObject object){
        this.postId = object.getString(Fields.postId);
        this.postNo = object.getString(Fields.postNo);
        this.boardSign = object.getString(Fields.boardSign);
        this.posterSign = object.getString(Fields.posterSign);
        this.replyPostNo = object.getString(Fields.replyPostNo);
        this.content = object.getString(Fields.content);
        this.available = object.getBoolean(Fields.available);
        this.sage = object.getBoolean(Fields.sage);
        this.createTime = object.getString(Fields.createTime);
        this.updateTime = object.getString(Fields.updateTime);
    }

    public JsonObject toJsonObject(){
        JsonObject object = new JsonObject();
        if(this.postId != null){
            object.put(Fields.postId,this.postId);
        }
        if(this.postNo != null){
            object.put(Fields.postNo,this.postNo);
        }
        if(this.boardSign != null){
            object.put(Fields.boardSign,this.boardSign);
        }
        if(this.posterSign != null){
            object.put(Fields.posterSign,this.posterSign);
        }
        if(this.replyPostNo != null){
            object.put(Fields.replyPostNo,this.replyPostNo);
        }
        if(this.content != null){
            object.put(Fields.content,this.content);
        }
        if(this.available != null){
            object.put(Fields.available,this.available);
        }
        if(this.sage != null){
            object.put(Fields.sage,this.sage);
        }
        if(this.createTime != null){
            object.put(Fields.createTime,this.createTime);
        }
        if(this.updateTime != null){
            object.put(Fields.updateTime,this.updateTime);
        }
        return object;
    }

    public String getPostNo() {
        return postNo;
    }

    public void setPostNo(String postNo) {
        this.postNo = postNo;
    }

    public String getBoardSign() {
        return boardSign;
    }

    public void setBoardSign(String boardSign) {
        this.boardSign = boardSign;
    }

    public String getPosterSign() {
        return posterSign;
    }

    public void setPosterSign(String posterSign) {
        this.posterSign = posterSign;
    }

    public String getReplyPostNo() {
        return replyPostNo;
    }

    public void setReplyPostNo(String replyPostNo) {
        this.replyPostNo = replyPostNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getSage() {
        return sage;
    }

    public void setSage(Boolean sage) {
        this.sage = sage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", postNo='" + postNo + '\'' +
                ", boardSign='" + boardSign + '\'' +
                ", posterSign='" + posterSign + '\'' +
                ", replyPostNo='" + replyPostNo + '\'' +
                ", content='" + content + '\'' +
                ", available=" + available +
                ", sage=" + sage +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

    public static class Fields{
        public static final String postId = "postId";
        public static final String postNo = "postNo";
        public static final String boardSign = "boardSign";
        public static final String posterSign = "posterSign";
        public static final String replyPostNo = "replyPostNo";
        public static final String content = "content";
        public static final String available = "available";
        public static final String sage = "sage";
        public static final String createTime = "createTime";
        public static final String updateTime = "updateTime";
    }

}
