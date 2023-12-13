package com.yupi.yuojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

import java.util.List;


/**
 * @author cyr
 * @version 1.0
 * @description TODO
 * @date 2023/12/11 9:23
 */
public class DockerDemo {
	public static void main(String[] args) throws InterruptedException {

		// 获取默认的docker client
		DockerClient dockerClient = DockerClientBuilder.getInstance().build();
		String image = "nginx:latest";

		// 拉取镜像
//		PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//		PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
//			@Override
//			public void onNext(PullResponseItem item) {
//				super.onNext(item);
//			}
//		};
//		pullImageCmd
//				.exec(pullImageResultCallback)
//				.awaitCompletion();
//		System.out.println("下载成功");

		// 创建容器
		CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
		CreateContainerResponse createContainerResponse = containerCmd
				.withCmd("echo", "Hello world")
				.exec();
		System.out.println(createContainerResponse);
		String containerId = createContainerResponse.getId();

		// 查看容器状态
		ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
		List<Container> containerList = listContainersCmd.withShowAll(true).exec();
		for (Container container : containerList) {
			System.out.println(container);
		}

		// 启动容器
		dockerClient.startContainerCmd(containerId).exec();

		// 查看日志
		LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
			@Override
			public void onNext(Frame item) {
				System.out.println(item.getStreamType());
				System.out.println("日志:" + new String(item.getPayload()));
				super.onNext(item);
			}
		};
		dockerClient.logContainerCmd(containerId)
				.withStdErr(true)
				.withStdOut(true)
				.exec(logContainerResultCallback)
				.awaitCompletion();

		// 删除容器
		dockerClient.removeContainerCmd(containerId).withForce(true).exec();

		// 删除镜像
		dockerClient.removeImageCmd(image).exec();
	}
}
