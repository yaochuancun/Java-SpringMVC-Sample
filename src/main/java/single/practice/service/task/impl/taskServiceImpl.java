package single.practice.service.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import single.practice.service.redis.RedisService;
import single.practice.service.task.TaskService;
import java.util.Map;

@Service("taskServiceImpl")
public class taskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(taskServiceImpl.class);

    @Autowired
    RedisService redisService;

    @Override
    public boolean addTaskInfo(Map<String, String> task) {
        return false;
    }

    @Override
    public String getTask() {
        return null;
    }

    @Override
    public String getTaskInfo(String taskId, String field) {
        return null;
    }
}
