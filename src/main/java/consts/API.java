package consts;

public interface API {

    String API_QUERY_BOARDS = "/nmb/boards";
    String API_ADD_BOARDS = "/nmb/boards";
    String API_UPDATE_BOARDS = "/nmb/boards/:boardSign";
    String API_DELETE_BOARDS = "/nmb/boards/:boardSign";
    String API_QUERY_POSTS_IN_BOARD = "/nmb/posts";
    String API_QUERY_REPLY_IN_POST = "/nmb/replys";
    String API_ADD_POSTS = "/nmb/posts";
    String API_REPLY_POSTS = "/nmb/replys";

    String FIELD_PAGE_SIZE = "pageSize";
    String FIELD_SORT = "sort";
    String FIELD_LAST_POST_NO = "lastPostNo";
    String FIELD_LAST_POST_UPD_TIME = "lastPostUpdTime";
    String FIELD_LAST_POST_ID = "lastPostId";

    String POSTER_COOKIE_NAME = "vnmb_psign";


}
