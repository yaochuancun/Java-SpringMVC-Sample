//package single.practice.docker.impl;
//
//import com.github.dockerjava.api.DockerClient;
//import com.github.dockerjava.api.command.InspectContainerResponse;
//import com.github.dockerjava.api.exception.NotFoundException;
//import com.github.dockerjava.api.model.Container;
//import com.github.dockerjava.core.DockerClientBuilder;
//import com.github.dockerjava.core.command.PullImageResultCallback;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import single.practice.constant.Constant;
//import single.practice.docker.DockerService;
//
//import java.util.List;
//import java.util.concurrent.*;
//
//@Service
//public class DockerServiceImpl implements DockerService {
//
//    public static final String TCP = "tcp://";
//    private static final Logger logger = LoggerFactory.getLogger(DockerServiceImpl.class);
//
//    @Override
//    public DockerClient buildDockerClient(String host) {
//        String tcpDockerDaemon = TCP + host;
//        return DockerClientBuilder.getInstance(tcpDockerDaemon)
//                .withDockerCmdExecFactory(Constant.DOCKER_CMD_EXEC_FACTORY)
//                .build();
//    }
//
//    @Override
//    public boolean startContainer(String host, String containerId) {
//        try {
//
//            DockerClient dockerClient = buildDockerClient(host);
//            dockerClient.startContainerCmd(containerId).exec();
//            logger.info("start to start container, ID =" + containerId);
//            return waitForDockerTaskCompletion(3, 1000,
//                    () -> {
//                        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
//                        return containerInfo.getState().getRunning();
//                    });
//        }catch (NotFoundException e){
//            return false;
//        }
//    }
//
//    @Override
//    public void removeContainer(String host, String containerId) {
//        logger.info("start to remove Container,containerID=" + containerId);
//        try {
//            DockerClient dockerClient = buildDockerClient(host);
//            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
//        }catch (NotFoundException e){
//            logger.info("remove Container failed ,containerID=" + containerId);
//            return;
//        }
//
//        logger.info("remove Container successfully ,containerID=" + containerId);
//    }
//
//    @Override
//    public void pullImage(String imageName, String host) {
//        DockerClient dockerClient = buildDockerClient(host);
//        dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitSuccess();
//    }
//
//    @Override
//    public InspectContainerResponse inspectContainer(String host, String containerId) {
//        DockerClient dockerClient = buildDockerClient(host);
//        return dockerClient.inspectContainerCmd(containerId).exec();
//    }
//
//    private  boolean waitForDockerTaskCompletion(int tryNumber, int delayBetweenCall, TaskFunction<Boolean> function) {
//
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        Callable<Boolean> callable = () -> function.apply();
//        // executes the callable immediately
//        Future<Boolean> future = executor.schedule(callable, 0L, TimeUnit.MILLISECONDS);
//
//
//        boolean taskOk = false;
//        try {
//            taskOk = future.get();
//        }
//        catch (InterruptedException|ExecutionException e) {
//            logger.error("Error in waitForTaskCompletion method. exception: ",e);
//        }
//        if (taskOk) {
//            executor.shutdown();
//            return true;
//        }
//
//        int tryIndex = 0;
//        do {
//            // the callable will be executed after delayBetweenCall has passed
//            future = executor.schedule(callable, delayBetweenCall, TimeUnit.MILLISECONDS);
//            try {
//                taskOk = future.get();// wait for the callable to complete
//            }
//            catch (InterruptedException|ExecutionException e) {
//                logger.error("Error in waitForTaskCompletion method. exception: ",e);
//            }
//            tryIndex++;
//        }
//        while (!taskOk && tryIndex < tryNumber);
//        executor.shutdown();
//        return taskOk;
//    }
//
//    @Override
//    public void deleteFailedContainer(String host) {
//        logger.info("Try to delete created container");
//        String containerAddress = host+":2375";
//        DockerClient dockerClient = buildDockerClient(containerAddress);
//        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
//        for(Container container : containers){
//            if("Created".equalsIgnoreCase(container.getStatus())){
//                removeContainer(containerAddress,container.getId());
//                logger.info("Remove created container "+container.getId());
//            }
//
//        }
//    }
//}
