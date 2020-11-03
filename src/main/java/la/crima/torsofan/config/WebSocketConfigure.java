package la.crima.torsofan.config;

import la.crima.torsofan.annotations.*;
import la.crima.torsofan.socket.SocketClient;
import la.crima.torsofan.socket.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.beans.beancontext.BeanContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

public class WebSocketConfigure {

    private final ApplicationContext context;

    private final SocketConfigure configure;
    private SocketServer server;
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public WebSocketConfigure(ApplicationContext context, Integer port) {
        this.context = context;
        SocketConfigure configure = new SocketConfigure();
        configure.setPort(port);
        this.configure = configure;
    }

    public SocketServer start(Class<?> mainClass) throws Exception{
        if (server != null)
            throw new Exception("Socket already run on this server");

        server = new SocketServer(configure);
        server.startAsync();
        scanClasses(mainClass);

        return server;
    }

    private void scanClasses(Class<?> clazz) throws Exception {
        Set<Method> set = new HashSet<>();
        for (Class<?> cls : getClasses(clazz.getPackage().getName())) {
            if (cls.isAnnotationPresent(SocketIOController.class)) {
                instances.put(cls, context.getBean(cls));
                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(SocketConnectEvent.class)) {
                        if (method.getParameterCount() != 1)
                            throw new IllegalArgumentException();
                        if (hasSocketClientParam(method))
                            throw new IllegalArgumentException("Missing parameter: SocketClient");
                        server.addConnectListener(client -> {
                            try {
                                method.invoke(instances.get(cls), new SocketClient(server, client));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (method.isAnnotationPresent(SocketDisconnectEvent.class)) {
                        if (method.getParameterCount() != 1)
                            throw new IllegalArgumentException();
                        if (hasSocketClientParam(method))
                            throw new IllegalArgumentException("Missing parameter: SocketClient");
                        server.addDisconnectListener(client -> {
                            try {
                                method.invoke(instances.get(cls), new SocketClient(server, client));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (method.isAnnotationPresent(SocketMapping.class)) {
                        set.add(method);
                    }
                }
            }
        }
        registerMethod(set);
    }

    private void registerMethod(Set<Method> set) {
        for (Method method : set) {
            String eventName = method.getAnnotation(SocketMapping.class).value();
            Class<?> requestBody = Arrays.stream(method.getParameters())
                    .filter(parameter -> parameter.isAnnotationPresent(SocketBody.class))
                    .findFirst()
                    .map(Parameter::getType)
                    .orElse(null);
            if (hasSocketClientParam(method))
                throw new IllegalArgumentException("Missing parameter: SocketClient");

            server.addEvent(eventName, requestBody, method, instances.get(method.getDeclaringClass()));
        }
    }

    private Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private boolean hasSocketClientParam(Method method) {
        return Arrays.stream(method.getParameters())
                .noneMatch(parameter -> parameter.getType().equals(SocketClient.class));
    }

}
