package entity;

import io.vertx.core.json.JsonObject;

public class Poster {

    private String posterSign;
    private Boolean available;
    private String createTime;
    private String expiredTime;

    public Poster(){
    }

    public Poster(JsonObject obj){
        this.posterSign = obj.getString(Fields.posterSign);
        this.available = obj.getBoolean(Fields.available);
        this.createTime = obj.getString(Fields.createTime);
        this.expiredTime = obj.getString(Fields.expiredTime);
    }

    public JsonObject toJsonObject(){
        JsonObject object = new JsonObject();
        if(this.posterSign != null){
            object.put(Fields.posterSign,this.posterSign);
        }
        if(this.available != null){
            object.put(Fields.available,this.available);
        }
        if(this.createTime != null){
            object.put(Fields.createTime,this.createTime);
        }
        if(this.expiredTime != null){
            object.put(Fields.expiredTime,this.expiredTime);
        }
        return object;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getPosterSign() {
        return posterSign;
    }

    public void setPosterSign(String posterSign) {
        this.posterSign = posterSign;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Poster{" +
                "posterSign='" + posterSign + '\'' +
                ", available=" + available +
                ", createTime='" + createTime + '\'' +
                '}';
    }

    public static class Fields{
        public static final String posterSign = "posterSign";
        public static final String available = "available";
        public static final String createTime = "createTime";
        public static final String expiredTime = "expiredTime";
    }
}
