package com.aimprosoft.library;

import com.aimprosoft.library.TodoProvider.Todo;
import com.aimprosoft.library.TodoProvider.Todos;
import com.aimprosoft.library.TodoProvider.Todo.Priority;
import com.google.protobuf.InvalidProtocolBufferException;

//import com.google.protobuf.*;
public class ProtobufBilder {

    Todo todo1 = Todo.newBuilder()
            .setTitle("Do the laundry")
            .setPriority(Priority.MEDIUM).build();
    Todo todo2 = Todo.newBuilder()
            .setTitle("Write the tutorial")
            .setPriority(Priority.HIGH).build();
    Todos todos = Todos.newBuilder()
            .addTodos(todo1)
            .addTodos(todo2)
            .build();



    byte[] bytes = new byte[]{1,2,3};
    Todos newTodos;

    public ProtobufBilder() throws InvalidProtocolBufferException {
        newTodos = Todos.parseFrom(bytes);
    }

}
