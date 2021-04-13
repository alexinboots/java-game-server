package net.rpc.common;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * rpc服务
 * 
 * @Author ckf
 */
public class AbstractRpcService {

	protected final ManagedChannel channel;

	public AbstractRpcService(String ip, int port) {
		this.channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build();
	}

	public ManagedChannel getChannel() {
		return channel;
	}
}
