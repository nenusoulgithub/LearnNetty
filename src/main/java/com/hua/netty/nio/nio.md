# 创建NIO的过程

## 第一步：打开ServerSocketChannel，监听客户端连接。

```java
ServerSocketChannel ssc = ServerSocketChannel.open();
```

## 第二步：绑定监听端口，设置为非阻塞模式。

```java
ssc.socket.bind(new InetSocketAddress(InetAddress.getByName("IP"), port));
ssc.configBlocking(false);
```

## 第三步：创建多路复用器，并启动线程。

```java
Selector selector = Selector.open();
new Thread(new ReactorTask()).start();
```

## 第四步：将ServerSocketChannel绑定到Reactor线程的多虑复用器Selector上，监听ACCEPT事件。

```java
SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT, ioHandler);
```

## 第五步：在多路复用器内部轮询转呗就绪的Key。

```java
int num = selector.select();
Set selectedKeys = selector.selectedKeys();
Iterator it = selectedKeys.iterator();
while(it.hasNext()) {
    SelectionKey key = (SelectionKey) it.next();
    //...do something with SelectionKey
}
```

## 第六步：多路复用器监听到有客户端连接，处理接入请求，完成TCP三次握手，建立物理链路。

```java
SocketChannel sc = ssc.accept();
```

## 第七步：设置客户端为非阻塞模式

```java
sc.configBlocking(false);
sc.socket().setReuseAddress(true);
```

## 第八步：将新注册的客户端接入到Reactor上，监听读操作，读取客户端发送的信息。

```java
SelectionKey key = sc.register(selector, SelectionKey.OP_READ, ioHandler);
```

## 第九步：异步读取客户端请求的消息到缓冲区。

```java
int readNumber = channel.read(receiveedBuffer);
```

## 第十步：对ByteBuffer进行解码，如果有半包指针，读取后续报文，将解码成功的消息封装成Task，投递到业务线程池中进行处理。

```java

```

## 第十一步：将返回的对应encode成ByteBuffer，调用SocketChannel的异步接口，将消息异步发送给客户端。

```java
socketChannel.write(buffer);
```
