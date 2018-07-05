package single.practice.service.task;

import java.util.Map;

public interface TaskService {
    boolean addTaskInfo(Map<String, String> task);

    //从队列里获取一个task
    String getTask();

    //获取任务信息的字段
    String getTaskInfo(String taskId, String field);
}
