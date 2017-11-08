package lk.uomcse.fs.com;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.servlet.Filter;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;


public class RestReceiver extends Receiver {

    private final static Logger LOGGER = Logger.getLogger(RestReceiver.class.getName());

    private Tomcat tomcat;

    private Node self;


    public RestReceiver(Node self) {
        this.tomcat = new Tomcat();
        this.self = self;
    }

    public void startWebServices(ConcurrentMap<String, BlockingQueue<IMessage>> handle) throws LifecycleException {

//        TODO check if ip setting required
        //set host ip and port
        tomcat.setHostname(this.self.getIp());
        tomcat.setPort(this.self.getPort());
        LOGGER.info(String.format("Tomcat servers started using address %s:%d", tomcat.getHost().getName(), tomcat.getConnector().getPort()));

        File base = new File(".");
        Context context = tomcat.addContext("", base.getAbsolutePath());

        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMapping("/*", "default");

        final FilterDef def = new FilterDef();
        final FilterMap map = new FilterMap();

        def.setFilterName("jerseyFilter");
        def.addInitParameter("jersey.config.servlet.filter.contextPath", "/");
        def.setFilter(getJerseyFilter(handle));
        context.addFilterDef(def);

        map.setFilterName("jerseyFilter");
        map.addURLPattern("/*");
        context.addFilterMap(map);

        tomcat.start();
    }

    public void stopWebService() throws LifecycleException {
        tomcat.stop();
    }

    private static Filter getJerseyFilter(ConcurrentMap<String, BlockingQueue<IMessage>> handle) {
        final ResourceConfig config = new ResourceConfig()
                .register(new RestService(handle))
                .property(ServletProperties.FILTER_FORWARD_ON_404, true);

        return new ServletContainer(config);
    }

}
