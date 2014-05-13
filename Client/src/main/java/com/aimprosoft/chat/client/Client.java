package com.aimprosoft.chat.client;

import com.aimprosoft.library.ByteUtil;
import com.aimprosoft.library.MessageSimple;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;


public class Client {
    final Socket s;  // это будет сокет для сервера


    final BufferedReader userInput; // буферизированный читатель пользовательского ввода с консоли
    final DataOutputStream dos;
    final ObjectOutputStream oos;
    final DataInputStream dis;
    final String userName;
    private final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();


    /**
     * Конструктор объекта клиента
     * @param host - IP адрес или localhost или доменное имя
     * @param port - порт, на котором висит сервер
     * @throws java.io.IOException - если не смогли приконнектиться, кидается исключение, чтобы
     * предотвратить создание объекта
     */
    public Client(String host, int port, String name) throws IOException {
        s = new Socket(host, port); // создаем сокет
        // создаем читателя и писателя в сокет с дефолной кодировкой UTF-8
        userName = name;
        oos = new ObjectOutputStream(s.getOutputStream());
        dos = new DataOutputStream(s.getOutputStream());
        dis = new DataInputStream(s.getInputStream());
        // создаем читателя с консоли (от пользователя)
        userInput = new BufferedReader(new InputStreamReader(System.in));
        new Thread(new Receiver()).start();// создаем и запускаем нить асинхронного чтения из сокета
    }

    /**
     * метод, где происходит главный цикл чтения сообщений с консоли и отправки на сервер
     */
    public void run() {
//        System.out.println("Type phrase kind nick:password :");
//        while (true) {
//            String userString = null;
//            try {
//                userString = userInput.readLine(); // читаем строку от пользователя
//            } catch (IOException ignored) {} // с консоли эксепшена не может быть в принципе, игнорируем
//            //если что-то не так или пользователь просто нажал Enter...
//            if (userString == null || userString.length() == 0 || s.isClosed()) {
//                close(); // ...закрываем коннект.
//                break; // до этого break мы не дойдем, но стоит он, чтобы компилятор не ругался
//            } else { //...иначе...

//                Message message = new Message(userName, userString, new Date());
                try {

//                    byte[] bytes = ByteUtil.toByteArray(message);
//                    dos.writeInt(bytes.length);
//                    oos.writeObject(userString);
//                    oos.flush();
//                    oos.writeBytes(userString);
//                    oos.flush();
//                    oos.writeChars(userString);
//                    oos.write(new byte[]{1,2,3});
                    oos.writeObject("Hello world");
                    oos.flush();

                } catch (IOException e) {
                    close();
                }
//                try {
//                    byte[] bytes = userString.getBytes("UTF-8");
//                    dos.writeInt(bytes.length);
//                    dos.write(bytes, 0, 1);
//                    dos.flush();
//                    // рвем сообщение на две части, моделируя проблемы сетки
////                    try {
////                        Thread.sleep(1000);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    dos.write(bytes, 1, bytes.length - 1);
//                    dos.write(bytes);
//
//                    dos.flush();
//                } catch (IOException e) {
//                    close(); // в любой ошибке - закрываем.
//                }
            }
//        }
//    }

    /**
     * метод закрывает коннект и выходит из
     * программы (это единственный  выход прервать работу BufferedReader.readLine(), на ожидании пользователя)
     */
    public synchronized void close() {//метод синхронизирован, чтобы исключить двойное закрытие.
        if (!s.isClosed()) { // проверяем, что сокет не закрыт...
            try {
                s.close(); // закрываем...
                System.exit(0); // выходим!
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public static void main(String[] args)  { // входная точка программы
        try {
            new Client("localhost", 8000, "Makaka").run(); // Пробуем приконнетиться...
        } catch (IOException e) { // если объект не создан...
            System.out.println("Unable to connect. Server not running?"); // сообщаем...
        }
    }

    /**
     * Вложенный приватный класс асинхронного чтения
     */
    private class Receiver implements Runnable{
        /**
         * run() вызовется после запуска нити из конструктора клиента чата.
         */
        public void run() {
            while (!s.isClosed()) { //сходу проверяем коннект.
                int read = 0;
                int length = 0;
                byte[] bytes = null;
                try {
//                    length = dis.readInt(); // пробуем прочесть
//                    if (length > 0) {
                        bytes = new byte[100];
                        read = dis.read(bytes);

//                    }
                } catch (IOException e) { // если в момент чтения ошибка, то...
                    // проверим, что это не банальное штатное закрытие сокета сервером
                    if ("Socket closed".equals(e.getMessage())) {
                        break;
                    }
                    System.out.println("Connection lost"); // а сюда мы попадем в случае ошибок сети.
                    close(); // ну и закрываем сокет (кстати, вызвается метод класса ChatClient, есть доступ)
                }
                if (read == 0) {  // сервер прикрыл коннект по своей инициативе, сеть работает
                    System.out.println("Server has closed connection");
                    close(); // ...закрываемся
                } else { // иначе печатаем то, что прислал сервер.
                    try {
                        System.out.println(new String(bytes, 0, read, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
