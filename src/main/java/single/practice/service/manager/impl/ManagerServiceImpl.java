package single.practice.service.manager.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import single.practice.service.manager.ManagerService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ManagerServiceImpl")
public class ManagerServiceImpl implements ManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerServiceImpl.class);

    private final int MAX_RETRY_NUM = Integer.parseInt(ConfigUtil.getProperty("RETRY_MAX_NUM"));

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassRoomService classRoomService;


    @Override
    public Response addTask(TaskInfo taskInfo, HttpServletRequest request) {
        Response response = new Response();
        Map<String,String> taskMap = new HashMap<>();
        taskMap.put(Constant.TASK_ID,taskInfo.getTaskId());
        taskMap.put(Constant.CALLBACKURL,taskInfo.getCallbackUrl());
        taskMap.put(Constant.JUDGE_FILE,taskInfo.getJudgeFile());
        taskMap.put(Constant.TARGET_FILE,taskInfo.getTargetFile());
        taskMap.put(Constant.USER,request.getHeader(Constant.USER));
        taskMap.put(Constant.USER_ID,request.getHeader(Constant.USER_ID));
        taskMap.put(Constant.DOMAIN,request.getHeader(Constant.DOMAIN));
        taskMap.put(Constant.DOMAIN_ID,request.getHeader(Constant.DOMAIN_ID));
        if(taskInfo.getType()==null||taskInfo.getType().isEmpty()){
            taskMap.put(Constant.TYPE,"java");
        }else {
            taskMap.put(Constant.TYPE, taskInfo.getType());
        }
        String type = taskInfo.getType();
        if(Constant.TYPE_C.equals(type)||Constant.TYPE_CPLUS.equals(type)||Constant.TYPE_JAVA_WEB.equals(type)||Constant.TYPE_PTYHON.equals(type)) {
            String testCaseList = "";
            if(taskInfo.getTestCaseList() !=null){
                testCaseList = JSONHelper.toJsonStr(taskInfo.getTestCaseList());
            }
            taskMap.put(Constant.TEST_CASE, testCaseList);
        }

        //输入信息不为null
        if(checkMapEmpty(taskMap)){
            logger.error("task info empty");
            return ErrorUtil.getErrorResponse(ErrorCodeConstants.TASK_INFO_EMPTY);
        }
        //添加任务信息
        boolean flagAdd = taskService.addTaskInfo(taskMap);
        //将任务加入队列
        boolean flagPush = taskService.addTask(taskInfo.getTaskId());

        if(flagAdd&&flagPush) {
            response.setResult(taskInfo.getTaskId());
            return response;
        }else{
            return ErrorUtil.getErrorResponse(ErrorCodeConstants.ADD_TASK_ERROR);
        }
    }

    @Override
    public Response callBackClassRoom(String taskId, TaskExecuteInfo result, String type) {
        String methodName = "[callBackClassRoom]";
        logger.info(methodName+Constant.METHOD_BEGIN);

        Response response ;
        Response responseToClass = new Response();
        Map<String,Object> resultMap = new HashMap<>();

        logger.info(taskId);
        String judgeFileName = taskService.getTaskInfo(taskId, Constant.JUDGE_FILE);
        String targetFileName = taskService.getTaskInfo(taskId, Constant.TARGET_FILE);
        logger.info(result.toString());

        if(Constant.SUCCESS.equals(result.getStatus())){
            if(Constant.TYPE_JAVA.equalsIgnoreCase(type)
                    ||Constant.TYPE_CPLUS.equals(type)||Constant.TYPE_C.equals(type)
                    ||Constant.TYPE_JAVA_WEB.equals(type)||Constant.TYPE_PTYHON.equals(type)) {
                try {
                    resultMap.put("score", JSONHelper.toList(result.getResult(), new TypeReference<List<JsonNode>>() {
                    }));
                } catch (IOException e) {
                    resultMap.put("reason", result.getResult());
                    responseToClass.setStatus(Constant.ERROR);
                }
            }
            if(Constant.TYPE_HADOOP.equalsIgnoreCase(type)){
                String url = taskId;
//                List<String> urlList = new ArrayList<>();
//                urlList.add(url);
                resultMap.put("downloadUrl",url);

            }
            resultMap.put(Constant.JUDGE_FILE, judgeFileName);
            resultMap.put(Constant.TARGET_FILE, targetFileName);
            responseToClass.setResult(resultMap);
        }else {
            resultMap.put("reason",result.getResult());
            resultMap.put(Constant.JUDGE_FILE, judgeFileName);
            resultMap.put(Constant.TARGET_FILE, targetFileName);
            responseToClass.setResult(resultMap);
            responseToClass.setStatus(Constant.ERROR);
        }
        logger.info(JSONHelper.toJsonStr(responseToClass));
        response = classRoomService.returnScore(taskId, responseToClass);

        return response;
    }

    @Override
    public boolean heartCheck(String ipPort, String ContainerId) {
        String heartUrl = "https://" + ipPort + "/TaskWorkspace/heartBeat";
        int retryNum = 0;
        boolean response = false;
        while (retryNum< MAX_RETRY_NUM) {
            HttpRequestResult httpRequestResult;
            try {
//                Thread.sleep(100);
                Object obj = new Object();
                synchronized (obj) {
                    obj.wait(100);
                }
                httpRequestResult = HttpUtils.doGet(heartUrl);
            } catch (Exception e) {
                logger.error("dockerMachine " + ContainerId + " is not ready");
                retryNum++;
                continue;
            }
            Gson gson = new Gson();
            Map<String, String> map = gson.fromJson(httpRequestResult.getResponseResult(), Map.class);
            if (map == null) {
                logger.error("response is null");
                retryNum++;
                continue;
            }
            if (null != map.get("status") && Constant.SUCCESS.equalsIgnoreCase(map.get("status"))) {
                logger.info("dockerMachine " + ContainerId + " is alive");
                response = true;
                break;
            } else {
                logger.error("dockerMachine status is error");
                retryNum++;
            }
            if (retryNum >= MAX_RETRY_NUM) {
                logger.info("dockerMachine " + ContainerId + " is dead");
                break;
            }
        }
            return response;
    }

    private boolean checkMapEmpty(Map<String,String> map){
        for(String value :map.values()){
            if(value==null){
                return true;
            }
        }
        return false;
    }
}
