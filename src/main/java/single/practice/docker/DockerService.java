package single.practice.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;

public interface DockerService {

    DockerClient buildDockerClient(String host);

    boolean startContainer(String host, String containerId);

    void removeContainer(String host, String containerId);

    void pullImage(String imageName, String host);

    InspectContainerResponse inspectContainer(String host, String containerId);

    void deleteFailedContainer(String host);
}
