package la.crima.torsofan.config;

import la.crima.torsofan.annotations.SocketBody;
import la.crima.torsofan.annotations.SocketController;
import la.crima.torsofan.annotations.SocketMapping;
import la.crima.torsofan.socket.SocketClient;
import la.crima.torsofan.socket.SocketServer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

public class WebSocketConfigure {

    private SocketConfigure configure;
    private SocketServer server;

    public WebSocketConfigure(Integer port) {
        SocketConfigure configure = new SocketConfigure();
        configure.setPort(port);
        this.configure = configure;
    }

    public void start(Class<?> mainClass) throws Exception{
        if (server != null)
            throw new Exception("Socket already run on this server");

        server = new SocketServer(configure);
        server.startAsync();
        scanClasses(mainClass);
    }

    private void scanClasses(Class<?> clazz) throws Exception {
        Set<Method> set = new HashSet<>();
        for (Class<?> cls : getClasses(clazz.getPackage().getName())) {
            if (cls.isAnnotationPresent(SocketController.class)) {
                Arrays.stream(cls.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(SocketMapping.class))
                        .forEach(set::add);
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
            Arrays.stream(method.getParameters())
                    .filter(parameter -> parameter.getType().equals(SocketClient.class))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Missing parameter: SocketClient"));

            server.addEvent(eventName, requestBody, method);
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

}
