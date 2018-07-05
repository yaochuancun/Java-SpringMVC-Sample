package single.practice.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskSchedule {

    private static final Logger logger = LoggerFactory.getLogger(TaskSchedule.class);

    /**
     *  定时调用任务,10S轮询一次
     */
    @Scheduled(fixedRate = 10*1000)
    public void taskRun() {
        logger.info("taskRun start");
    }
}



