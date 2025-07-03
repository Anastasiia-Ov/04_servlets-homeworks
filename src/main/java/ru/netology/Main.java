package ru.netology;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.service.PostService;

public class Main {
    public static void main(String[] args) {
        final var context = new AnnotationConfigApplicationContext("ru.netology");

        final var controller = context.getBean("postController");

        final var service = context.getBean(PostService.class);

        final var isSame = service == context.getBean("postService");
    }
}
