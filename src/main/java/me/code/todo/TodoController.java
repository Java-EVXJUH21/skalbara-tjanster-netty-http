package me.code.todo;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

public class TodoController {

    private final TodoService service;
    private final Gson gson;

    public TodoController(TodoService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public HttpResponse getAllTodos(HttpRequest request) {
        var all = service.getAll();
        return withBody(all);
    }

    public HttpResponse createTodo(FullHttpRequest request) {
        var body = request.content();
        var json = gson.fromJson(
                body.toString(CharsetUtil.UTF_8),
                TodoCreation.class);

        var todo = service.create(json.name, json.description);

        return withBody(todo);
    }

    public HttpResponse deleteTodo(FullHttpRequest request) {
        var split = request.uri().split("/");
        var name = split[split.length - 1];

        var todo = service.remove(name);

        return withBody(todo);
    }

    private <T> HttpResponse withBody(T content) {
        var json = gson.toJson(content);
        var data = Unpooled.buffer();
        data.writeBytes(json.getBytes());

        var headers = new DefaultHttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Content-Length", json.length());

        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                data,
                headers,
                new DefaultHttpHeaders()
        );
    }

    @Getter
    @Setter
    private static class TodoCreation {
        private String name, description;
    }

//    @DeleteMapping("/remove/{name}")
//    public ResponseEntity<Todo> removeTodo(
//            @PathVariable String name
//    ) {
//        var todo = service.remove(name);
//        if (todo == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(todo);
//    }

}
