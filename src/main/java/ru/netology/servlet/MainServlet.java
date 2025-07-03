package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.сonfig.JavaConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private final static String GET = "GET";
    private final static String POST = "POST";
    private final static String DELETE = "DELETE";
    private final static String API_POSTS = "/api/posts";
    private final static String API_POSTS_D = "/api/posts/\\d+";

    @Override
    public void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            handleRequest(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        final var path = req.getRequestURI();
        final var method = req.getMethod();

        if (GET.equals(method)) {
            if (path.equals(API_POSTS)) {
                controller.all(resp);
                return;
            } else if (path.matches(API_POSTS_D)) {
                final var id = parseID(path);
                if (id != null) {
                    controller.getById(id, resp);
                    return;
                }
            }
        } else if (POST.equals(method) && path.equals(API_POSTS)) {
            controller.save(req.getReader(), resp);
            return;
        } else if (DELETE.equals(method) && path.matches(API_POSTS_D)) {
            final var id = parseID(path);
            if (id != null) {
                controller.removeById(id, resp);
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private Long parseID(String path) throws RuntimeException {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}