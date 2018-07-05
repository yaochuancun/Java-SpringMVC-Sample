package single.practice.constant;

public interface ErrorCodeConstants {
    //50000 内部系统级别 环境 网络
    //51000 外部 服务
    String INVALID_TOKEN = "DEV-187-50001";
    String INTERNAL_ERROR = "DEV-187-50002";
    String JSON_ERROR = "DEV-187-50003";


    //40000 请求 校验
    String EMPTY_BODY = "DEV-187-40001";
    String BAD_REQUEST = "DEV-187-40002";
    String TASK_INFO_EMPTY = "DEV-187-40003";

    //70000  业务相关错误
    String CLASSROOM_CALLBACK_ERROR = "DEV-187-70001";
    String ADD_TASK_ERROR = "DEV-187-70002";
    String CREATE_WORKSPACE_ERROR = "DEV-187-70003";

}
