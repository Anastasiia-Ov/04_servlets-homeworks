package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;
    private final Gson gson = new Gson();
    private static final String MSG_NOT_FOUND = "Post not found";
    private static final String MSG_SERVER_ERROR = "Internal server error";

    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var data = service.all();
        response.getWriter().print(gson.toJson(data));
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        try {
            Post post = service.getById(id);
            if (post != null) {
                writeJsonResponse(response, post);
            } else {
                handleError(response, MSG_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleError(response, MSG_SERVER_ERROR, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var post = gson.fromJson(body, Post.class);
        final var data = service.save(post);
        response.getWriter().print(gson.toJson(data));
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        try {
            Post post = service.getById(id);
            if (post != null) {
                service.removeById(id);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                handleError(response, MSG_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleError(response, MSG_SERVER_ERROR, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType(APPLICATION_JSON);
        response.getWriter().print(gson.toJson(data));
    }

    private void handleError(HttpServletResponse response, String message, int error) throws IOException {
        response.setStatus(error);
        writeJsonResponse(response, Map.of("message", message));
    }
}
