package single.practice.constant;

import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;

public interface Constant {
    String SUCCESS = "success";
    String ERROR = "error";

    // Http Code
    /** 请求资源不存在状态码 **/
    String NOT_FOUND_CODE = "404";
    /** 请求成功状态码 **/
    String SUCCESS_CODE = "200";
    /** 请求内部错误状态码 **/
    String SERVER_ERROR_CODE = "500";
    /** 请求拒绝(请求权限不正确)状态码 **/
    String FORBIDDEN_CODE = "403";
    /** 请求资源冲突状态码 **/
    String CONFLICT_CODE = "409";
    /** 请求参数非法 **/
    String BAD_REQUEST = "400";

    //docker
    DockerCmdExecFactory DOCKER_CMD_EXEC_FACTORY = new JerseyDockerCmdExecFactory()
            .withReadTimeout(Constant.DOCKER_TIMEOUT_IN_MS)
            .withConnectTimeout(5000)
            .withMaxTotalConnections(100)
            .withMaxPerRouteConnections(10);
    int DOCKER_TIMEOUT_IN_MS = 50000;
}
