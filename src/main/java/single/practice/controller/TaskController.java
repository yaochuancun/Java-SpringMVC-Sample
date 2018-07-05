package single.practice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import single.practice.beans.Task;
import single.pro.controller.BaseController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Task Manager
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
@RestController
@RequestMapping("/v1")
public class TaskController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private static final boolean INIT_WORKSPACE = Constant.KEY_TRUE.equalsIgnoreCase(ConfigUtil.getProperty("IF_INIT_WORKSPACE"));

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private MyFilesService myFilesService;

    @Autowired
    private RunCodeService runCodeService;

    /**
     * springmvc加载完成后执行初始化方法
     */
    @PostConstruct()
    public void init(){
        logger.info("after spring mvc start,first exec.");
    }

    /**
     * 添加任务
     * @param task
     * @return
     */
    @RequestMapping(value = "/task", method = RequestMethod.POST, produces = "application/json")
    public String addTask(@RequestBody Task task, HttpServletRequest request){
        logger.info(request.getHeader("token"));
        //add to redis

        //add to mysql

        //add to rabbitmq
        return "success";
    }

    /**
     * 修改任务
     */
    @RequestMapping(value = "/task", method = RequestMethod.POST, produces = "application/json")
    public String modifyTaskById(@RequestBody Task task, HttpServletRequest request){
        //add to redis

        //add to mysql

        //add to rabbitmq

        return "success";
    }

    /**
     * 删除任务
     */
    @RequestMapping(value = "/task", method = RequestMethod.DELETE, produces = "application/json")
    public String deteleTaskById(@RequestBody Task task, HttpServletRequest request){
        //delete to redis

        //delete to mysql

        //delete to rabbitmq
        return "success";
    }

    /**
     * 查询任务
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.GET, produces = "application/json")
    public String getTaskById( HttpServletRequest request){
        //get task from redis

        //get task from mysql

        //get task from rabbitmq
        return "success";
    }
}
