package entity;

import io.vertx.core.json.JsonObject;

public class Board {

    private String name;
    private String boardSign;
    private String order;
    private Boolean available;
    private Boolean readonly;
    private String createTime;
    private String updateTime;
    private String operator;

    public Board() {
    }

    public Board(JsonObject obj){
        this.name = obj.getString(Fields.name);
        this.boardSign = obj.getString(Fields.boardSign);
        this.order = obj.getString(Fields.order);
        this.available = obj.getBoolean(Fields.available);
        this.readonly = obj.getBoolean(Fields.readonly);
        this.createTime = obj.getString(Fields.createTime);
        this.updateTime = obj.getString(Fields.updateTime);
        this.operator = obj.getString(Fields.operator);
    }

    public JsonObject toJsonObject(){
        JsonObject obj = new JsonObject();
        if(this.name != null){
            obj.put(Fields.name, this.name);
        }
        if(this.boardSign != null){
            obj.put(Fields.boardSign, this.boardSign);
        }
        if(this.order != null){
            obj.put(Fields.order, this.order);
        }
        if(this.available != null){
            obj.put(Fields.available, this.available);
        }
        if(this.readonly != null){
            obj.put(Fields.readonly, this.readonly);
        }
        if(this.createTime != null){
            obj.put(Fields.createTime, this.createTime);
        }
        if(this.updateTime != null){
            obj.put(Fields.updateTime, this.updateTime);
        }
        if(this.operator != null){
            obj.put(Fields.operator, this.operator);
        }
        return obj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardSign() {
        return boardSign;
    }

    public void setBoardSign(String boardSign) {
        this.boardSign = boardSign;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "Board{" +
                "name='" + name + '\'' +
                ", boardSign='" + boardSign + '\'' +
                ", order='" + order + '\'' +
                ", available=" + available +
                ", readonly=" + readonly +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }

    public static class Fields{
        public static final String name = "name";
        public static final String boardSign = "boardSign";
        public static final String order = "order";
        public static final String available = "available";
        public static final String readonly = "readonly";
        public static final String createTime = "createTime";
        public static final String updateTime = "updateTime";
        public static final String operator = "operator";
    }
}
