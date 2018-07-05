//package single.practice.service.redis.impl;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import single.practice.dao.redis.RedisApplicationsDao;
//import single.practice.service.redis.RedisService;
//
//import java.util.Map;
//
//@Service("RedisServiceImpl")
//public class RedisServiceImpl implements RedisService {
//    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);
//    @Autowired
//    RedisApplicationsDao redisApplicationsDao;
//
//    @Override
//    public boolean addTaskInfo(String key,Map<String, String> task) {
//        return redisApplicationsDao.mapSet(key,task);
//    }
//
//    @Override
//    public String queryTaskField(String taskId, String field) {
//        return redisApplicationsDao.mapGet(taskId,field);
//    }
//
//    @Override
//    public boolean updateTaskInfo(String taskId, Map<String, String> task) {
//        return redisApplicationsDao.mapSet(TASK_HASH_TABLE,task);
//    }
//
//    @Override
//    public boolean deleteTaskInfo(String taskId) {
//
//        return redisApplicationsDao.del(taskId);
//    }
//
//    @Override
//    public boolean pushTaskList(String key,String taskId) {
//        logger.info("push task "+taskId);
//        return redisApplicationsDao.pushList(key,taskId);
//    }
//
//    @Override
//    public String popTaskList(String key) {
//        String taskId = redisApplicationsDao.popList(key);
//        if(taskId!=null){
//            logger.info("pop task "+ taskId);
//        }
//        return taskId;
//    }
//
//    @Override
//    public boolean setExpire(String key, int time) {
//        return redisApplicationsDao.expire(key,time);
//    }
//}
