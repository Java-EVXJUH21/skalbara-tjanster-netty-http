package me.code;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import me.code.todo.TodoController;
import me.code.todo.TodoRepository;
import me.code.todo.TodoService;

public class RequestReader extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final TodoController todoController;

    public RequestReader() {
        var todoRepository = new TodoRepository();
        var todoService = new TodoService(todoRepository);
        this.todoController = new TodoController(todoService);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        var uri = request.uri();
        var method = request.method();

        HttpResponse response = null;
        if (uri.equals("/all") && method == HttpMethod.GET) {
            response = todoController.getAllTodos(request);
        } else if (uri.equals("/create") && method == HttpMethod.POST) {
            response = todoController.createTodo(request);
        } else if (uri.startsWith("/delete/") && method == HttpMethod.DELETE) {
            response = todoController.deleteTodo(request);
        } else {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND,
                    Unpooled.EMPTY_BUFFER
            );
        }

        ctx.channel().writeAndFlush(response);
    }
}
