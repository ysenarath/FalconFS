package lk.uomcse.fs.model.com;

import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.IMessage;
import org.apache.log4j.Logger;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RestSender extends Sender {
    private final static Logger LOGGER = Logger.getLogger(RestSender.class.getName());

    private Node self;
    private final ThreadPoolExecutor threadPoolExecutor;


    public RestSender(Node self) {
        this.self = self;

        this.threadPoolExecutor = new ThreadPoolExecutor(3, 5, 100,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void send(String ip, int port, IMessage request) {
        request.setSender(self);
        threadPoolExecutor.execute(new RestClientLoader(ip, port, request));
    }

    public void stopWebClient() {
        threadPoolExecutor.shutdown();
    }

}
