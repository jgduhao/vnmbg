package consts;

public interface Mongo {

    int ASC = 1;
    int DESC = -1;

    String ID = "_id";
    String GREATER_THAN = "$gt";
    String LITTER_THAN = "$lt";
    String SET = "$set";
    String INCREASE = "$inc";

    String SEQ_COLLECTION = "seq";
    String BOARD_COLLECTION = "board";
    String POSTER_COLLECTION = "poster";
    String POST_COLLECTION = "post";
    String POST_SEQ_KEY = "post_seq";

}
