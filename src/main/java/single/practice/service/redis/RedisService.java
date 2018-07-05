package single.practice.service.redis;

import java.util.Map;
import java.util.Set;

public interface RedisService {
    //hash
    boolean addTaskInfo(String key,Map<String, String> task);
    //hash
    String queryTaskField(String taskId,String field);
    //hash
    boolean updateTaskInfo(String taskId, Map<String,String> task);
    //hash
    boolean deleteTaskInfo(String taskId);
    //list
    boolean pushTaskList(String key,String taskId) ;
    //list
    String popTaskList(String key);
    //expire
    boolean setExpire(String key,int time);
}
