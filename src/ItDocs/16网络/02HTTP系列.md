## 1. HTTP状态码有哪些分类？各代表什么含义？

**原理分析**

| 分类 | 含义 | 常见状态码 |
| --- | --- | --- |
| 1xx | 信息提示，表示临时响应 | **100** Continue继续；**101** 切换协议 |
| 2xx | 请求成功 | **200** 请求正在被服务端正确处理 |
| 3xx | 重定向，浏览器自动跳转到新URL（从Location获取） | **301** 永久重定向；**302** 临时重定向；**303** 资源存在另一个URL应使用GET方法请求；**304** 资源被浏览器缓存，不需要重新请求 |
| 4xx | 客户端错误 | **400** 请求报文语法错误；**401** Unauthorized（认证相关，如token验证失败）；**403** Forbidden（无权限，如不在白名单）；**404** 请求资源不存在 |
| 5xx | 服务器错误 | **501** 服务器不支持请求所需功能（如不支持的PUT请求）；**502** 网关错误（如Nginx获取响应超时）；**503** 服务不可用（维护或负载过重） |

HTTP默认端口80，HTTPS标准端口443。HTTP工作于应用层，HTTPS的安全传输机制工作在传输层。

## 2. URI和URL有什么区别？

**原理分析**

- **URI（Uniform Resource Identifier）**：统一资源标识符，重点在于**标识资源**。例如`http://www.abc.com/aaa.png`中的`aaa.png`就是URI
- **URL（Uniform Resource Locator）**：统一资源定位符，是URI的子集，重点在于**定位资源**，包含协议、域名、文件名等。例如`http://www.abc.com/aaa.png`整个就是URL

![URI与URL关系](../assets/16%E7%BD%91%E7%BB%9C/92ee6d8ee79a44048fb4b59a3d76a5f5.png)

## 3. HTTP请求报文和响应报文包含哪些部分？

**原理分析**

**请求报文包含四部分：**

- **请求行**：包含**请求方法、URI、HTTP版本信息**
- **请求首部字段**：如Accept、Content-Type等
- **空行**
- **请求内容实体**：如POST请求时的JSON数据

**响应报文包含四部分：**

- **状态行**：包含**HTTP版本、状态码、状态码的原因短语**
- **响应首部字段**
- **空行**
- **响应内容实体**

## 4. 一次完整的HTTP请求所经历的7个步骤是什么？

**原理分析**

1. **建立TCP连接**：HTTP是比TCP更高层次的应用层协议，必须先建立低层连接（TCP，默认端口80）
2. **Web浏览器向Web服务器发送请求行**：如`GET /sample/hello.jsp HTTP/1.1`
3. **Web浏览器发送请求头**：发送头信息后，浏览器发送一个**空白行通知服务器**头信息发送结束
4. **Web服务器应答**：如`HTTP/1.1 200 OK`，应答第一部分是协议版本号和状态码
5. **Web服务器发送应答头**
6. **Web服务器向浏览器发送数据**：发送头信息后发送空白行表示结束，然后以Content-Type描述的格式发送实际数据
7. **Web服务器关闭TCP连接**：如果浏览器或服务器在头信息中加入`Connection: keep-alive`，TCP连接保持打开状态，可以继续通过相同连接发送请求，节省时间和带宽

**流程总结**：建立TCP连接 → 发送请求行 → 发送请求头 → 服务器发送状态行 → 发送响应头 → 发送响应数据 → 断开TCP连接

## 5. GET和POST有什么区别？

**原理分析**

- **GET请求是获取一个资源，浏览器可以对其进行缓存**。POST请求是修改资源，重复提交表单时浏览器会提示确认
- 浏览器场景下，**GET请求没有body，只有URL（数据放在querystring中）**；**POST请求的数据在body中**
- 在代码接口中（Ajax、Java HttpClient、Postman等），没有浏览器那么多限制，**只要符合HTTP格式就行**。并没有限制说POST不能在URL上添加querystring，GET不能添加body，可以自行定制
- 为了降低开发成本，有了REST规范：GET/POST/DELETE/PUT分别作为**获取、创建、删除、更新**资源，请求体使用JSON
- **安全性GET和POST没有本质区别**，HTTP在网络上是明文传输的。GET信息展示在URL上可分享给第三方，也不安全。一些中间节点（网关、代理、日志）通常会记录URL，**body里的数据也可能被记录**。保证安全还是需要HTTPS

**URL编码**：GET的URL中如果有特殊字符需要**percent encoding**，只管把字符串转换成URL可用字符，不管字符集编码（目前基本上都是UTF-8）。POST的body有Content-Type明确格式（如`application/x-www-form-urlencoded`）和字符编码UTF-8。

**表单提交类型**：

- 浏览器submit表单提交：`application/x-www-form-urlencoded`（简单key value）或`multipart/form-data`（提交文件）
- Ajax或HttpClient发出POST：body格式比较自由（JSON、XML、CSV等），前后端约定好即可

## 6. POST需要发送两次请求吗？

**原理分析**

其实**不一定**。服务器端在解析时，总是会先完全解析全部的请求头部，了解控制信息后才能决定请求怎么处理（拒绝还是根据Content-Type调用相应解析器处理）。客户端可以利用HTTP的**Continued协议**（100-continue）这样做：

- 客户端先发送所有请求头给服务器校验
- 如果通过，服务器回复 **"100 - Continue"**，客户端再把剩下的数据发给服务器
- 如果请求被拒，服务器回复400之类的错误，交互终止

**到底发一次还是发N次，客户端可以灵活决定**，因为不管怎么发都符合HTTP协议。这是实现细节，不是GET和POST本身的区别。

## 7. URL的长度限制是多少？

**原理分析**

**HTTP协议本身对URL长度没有做任何规定**。实际限制由客户端/浏览器和服务端决定：

- **常说的2048个字符限制，其实是IE8的限制**。原始文档说的是"URL最大长度2083字符，path部分最长2048字符"
- **Chrome的URL限制是2MB**
- 对开发者来说，使用超长URL需要考虑前后端及中间代理每一个环节的配置。**超长URL会影响搜索引擎爬虫**（有些爬虫无法处理超过2000字节的URL）
- **经验建议**：只要API的URL长度**可能达到2000bytes以上，就必须用body传数据**
- 注意：1个汉字字符经过UTF-8编码+percent encoding后会变成**9个字节**

**一切由双方约定**：Elastic Search的`_search`接口使用GET却用body表达查询，因为查询复杂用JSON编码比percent encoding更舒服。

## 8. HTTP为什么是无状态协议？如何解决？

**原理分析**

**无状态协议**就是指**浏览器对于事务的处理没有记忆能力**。每个请求都是独立的，服务端不保留之前请求的信息。

解决方案：使用**Cookie和Session**机制来维持状态。

## 9. Content-Length与实际长度不一致会怎样？

**原理分析**

- **Content-Length > 实际长度**：服务端/客户端读取到消息结尾后，会等待下一个字节，**无响应直到超时**
- **Content-Length < 实际长度**：首次请求消息会被截取。在**Connection:keep-alive**场景下，连续两次请求会产生解析混乱（如将上一次剩余消息拼接到后续请求中，导致"Request method 'ruiqingPOST' not supported"）。如果使用Connection:close，则每次请求都被截断但不会产生解析混乱

**不确定Content-Length时**：应使用**Transfer-Encoding: chunked**（分块传输编码）。返回消息被分为多个数据块，每个数据块由**长度+数据**组成（以CRLF结尾）。终止块是**长度为0**的chunk。主要应用于：

- 要传输大量数据
- 请求在处理完成前无法获知响应长度（如从数据库查询生成大HTML表格、传输大量图片）

## 10. HTTP请求头和响应头有哪些常见字段？

**原理分析**

**常见请求头（Request Header）：**

| Header | 解释 | 示例 |
| --- | --- | --- |
| Accept | 客户端能够接收的内容类型 | Accept: text/plain, text/html |
| Accept-Charset | 浏览器可接受的字符编码集 | Accept-Charset: iso-8859-5 |
| Accept-Encoding | 浏览器支持的压缩编码类型 | Accept-Encoding: compress, gzip |
| Accept-Language | 浏览器可接受的语言 | Accept-Language: en,zh |
| Authorization | HTTP授权的授权证书 | Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ== |
| Cache-Control | 指定请求和响应遵循的缓存机制 | Cache-Control: no-cache |
| Connection | 是否需要持久连接（HTTP/1.1默认keep-alive） | Connection: close |
| Cookie | 保存在该请求域名下的所有cookie值 | Cookie: $Version=1; Skin=new; |
| Content-Length | 请求的内容长度 | Content-Length: 348 |
| Content-Type | 请求的与实体对应的MIME信息 | Content-Type: application/x-www-form-urlencoded |
| Host | 指定请求的服务器的域名和端口号 | Host: www.zcmhi.com |
| If-Modified-Since | 在指定时间后修改则请求成功，未修改返回304 | If-Modified-Since: Sat, 29 Oct 2010 19:43:31 GMT |
| If-None-Match | 内容未改变返回304，参数为服务器先前发送的Etag | If-None-Match: "737060cd8c284d8af7ad3082f209582d" |
| Range | 只请求实体的一部分，指定范围 | Range: bytes=500-999 |
| Referer | 先前网页的地址（来路） | Referer: http://www.zcmhi.com/archives/71.html |
| User-Agent | 发出请求的用户信息 | User-Agent: Mozilla/5.0 (Linux; X11) |

**常见响应头（Response Header）：**

| Header | 解释 | 示例 |
| --- | --- | --- |
| Age | 从原始服务器到代理缓存形成的估算时间（秒） | Age: 12 |
| Allow | 对某网络资源的有效请求行为，不允许返回405 | Allow: GET, HEAD |
| Cache-Control | 告诉所有缓存机制是否可以缓存及类型 | Cache-Control: no-cache |
| Content-Encoding | web服务器支持的返回内容压缩编码类型 | Content-Encoding: gzip |
| Content-Length | 响应体的长度 | Content-Length: 348 |
| Content-Type | 返回内容的MIME类型 | Content-Type: text/html; charset=utf-8 |
| ETag | 请求变量的实体标签的当前值 | ETag: "737060cd8c284d8af7ad3082f209582d" |
| Expires | 响应过期的日期和时间 | Expires: Thu, 01 Dec 2010 16:00:00 GMT |
| Last-Modified | 请求资源的最后修改时间 | Last-Modified: Tue, 15 Nov 2010 12:45:26 GMT |
| Location | 重定向到非请求URL的位置 | Location: http://www.zcmhi.com/archives/94.html |
| Refresh | 重定向（5秒后跳转） | Refresh: 5; url=http://www.zcmhi.com/ |
| Server | web服务器软件名称 | Server: Apache/1.3.27 (Unix) |
| Set-Cookie | 设置Http Cookie | Set-Cookie: UserID=JohnDoe; Max-Age=3600; Version=1 |
| Transfer-Encoding | 文件传输编码 | Transfer-Encoding: chunked |
| WWW-Authenticate | 表明客户端请求实体应使用的授权方案 | WWW-Authenticate: Basic |

## 11. HTTP Keep-Alive是什么？有哪些参数？

**原理分析**

**Keep-Alive**是一个通用消息头，允许消息发送者暗示连接的状态，还可设置超时时长和最大请求数。需要将Connection首部设置为`keep-alive`才有意义。

- 在HTTP/1.0中，默认使用短连接，每进行一次HTTP操作就建立一次连接，任务结束就中断
- 从**HTTP/1.1起，默认使用长连接**，连接在发送后将保持打开状态
- 长连接中关闭连接通过`Connection: closed`头部字段
- **keep-alive必须客户端和服务端同时支持才有效**
- 在HTTP/2中，Connection和Keep-Alive是被忽略的，采用其他机制进行连接管理

**参数**：

- **timeout**：指定空闲连接需要保持打开的最小时长（秒）。大于TCP层面的超时设置会被忽略
- **max**：在连接关闭前可以发送的请求最大值。在非管道连接中除0以外被忽略；HTTP管道连接可用它限制管道使用

示例：`Keep-Alive: timeout=5, max=1000`

**Content-Length在长连接中的作用**：Content-Length可以帮助确定每个请求体的长度，从而区分每个请求，避免数据混乱。

## 12. HTTP/1.0、HTTP/1.1、HTTP/2.0有什么区别？

**原理分析**

**HTTP/1.0（1996年引入）：**

- 提供了最基本的认证，用户名和密码未加密，容易受到窥探
- 使用**短连接**，每次发送数据都要经过TCP三次握手和四次挥手，效率低
- 只使用`If-Modified-Since`和`Expires`作为缓存失效标准
- **不支持断点续传**，每次传送全部页面和数据
- 认为每台计算机只能绑定一个IP，请求消息中URL不传递主机名（hostname）

**HTTP/1.1（1999年引入）：**

- 使用**摘要算法**进行身份验证
- **默认使用长连接**，只需一次建立就可传输多次数据，连接时长通过`keep-alive`设置
- 新增`E-tag`、`If-Unmodified-Since`、`If-Match`、`If-None-Match`等缓存控制标头
- **支持断点续传**，通过请求头中的`Range`实现
- 使用**虚拟网络**，一台物理服务器可存在多个虚拟主机（Multi-homed Web Servers），共享一个IP
- **管道化（Pipelining）**：基于长连接，可以不等第一个请求响应继续发送后面的请求，但**响应顺序还是按照请求顺序返回**

![HTTP各版本对比](../assets/16%E7%BD%91%E7%BB%9C/3e534688a4be493ab62ca465ffd90dda.png)

**HTTP/2.0（2015年标准）：**

- **头部压缩**：HTTP/1.1中User-Agent、Cookie、Accept、Server、Range等字段可能占用几百甚至几千字节，而Body却经常只有几十字节。HTTP/2.0使用**HPACK算法**进行压缩
- **二进制格式**：HTTP/1.x解析基于文本（ASCII码），HTTP/2.0使用更靠近TCP/IP的二进制格式，提升解析效率
- **强化安全**：HTTP/2.0一般都跑在HTTPS上
- **多路复用（Multiplexing）**：每个请求对应一个id，一个连接上可以有多个请求，共享连接
- **服务器推送（Server Push）**：服务端向客户端发送比客户端请求更多的数据，允许服务器直接提供浏览器渲染页面所需资源，无须浏览器在收到解析页面后再发起一轮请求，节约加载时间

## 13. HTTP/1.1的管道化（Pipelining）是什么？

**原理分析**

基于长连接的基础上：

**没有管道化的请求响应**（同一个TCP通道）：

```
请求1 → 响应1 → 请求2 → 响应2 → 请求3 → 响应3
```

**管道化的请求响应**：

```
请求1 → 请求2 → 请求3 → 响应1 → 响应2 → 响应3
```

管道化可以**不等第一个请求响应继续发送后面的请求**，但即使服务器先准备好响应2，也是按照请求顺序先返回响应1（**队头阻塞**）。

## 14. HTTP/2.0的多路复用和服务器推送是什么？

**原理分析**

**多路复用**：Web浏览器最多允许对同一个Host建立六个TCP连接。如果图片都是HTTPS连接并且在同一域名下，浏览器在SSL握手后会和服务端商量能不能用HTTP/2，如果能就使用**Multiplexing**功能在这个连接上进行多路传输。如果不能用HTTP/2，浏览器就在一个HOST上建立多个TCP连接（最多取决于浏览器设置），所有连接都在发送请求时其他请求只能等待。

**关于Chrome的多路复用**：如果发现用不了HTTP/2（现实中的HTTP/2都是在HTTPS上实现的，所以只能使用HTTP/1.1），浏览器会在一个HOST上建立多个TCP连接，连接数量最大限制取决于浏览器设置（Chrome最多允许对同一个Host建立六个TCP连接）。

## 15. HTTPS请求流程是什么？证书是如何工作的？

**原理分析**

**数字证书的目的**：确保服务器的公钥安全，防止中间人攻击。

**CA签发证书流程**：CA把证书（包含服务器的公钥、证书版本、序列号、签名算法、有效期等）内容做hash，得到一个固定长度的字符串，再用**CA自己的私钥加密**得到数字签名，附在证书末尾，得到完整的数字证书。

**HTTPS请求流程（TLS握手）：**

1. **Client发起HTTPS请求**（默认443端口），发送：
   - 支持的协议版本（如TLS 1.0）
   - 客户端生成的**随机数random1**（稍后用于生成对话密钥）
   - 支持的加密方法（如RSA公钥加密）
   - 支持的压缩方法
2. **Server返回**：
   - 确认使用的加密通信协议版本（不一致则关闭）
   - 服务器生成的**随机数random2**（稍后用于生成对话密钥）
   - 确认使用的加密方法
   - **服务器公钥证书**
3. **Client验证公钥证书**：验证有效期、用途是否匹配、是否在CRL吊销列表、**上一级证书是否有效**（递归过程直到根证书）。如果验证通过继续，不通过则显示警告信息
4. Client使用**伪随机数生成器**生成**pre-master secret**，用证书的公钥加密发给Server。**random1、random2、pre-master secret**通过一定算法得出**session Key和MAC算法密钥**（对称密钥）
5. **Server使用自己的私钥解密**，加上random1、random2得到会话密钥。至此客户端和服务端**双方持有相同的会话密钥**
6. 此后通信使用会话密钥进行对称加密解密

![HTTPS流程1](../assets/16%E7%BD%91%E7%BB%9C/192b8d1a013a47be9c0ff27586ff1fbf.png)

![HTTPS流程2](../assets/16%E7%BD%91%E7%BB%9C/7b830090d6be4f8387025b77e071e0dc.png)

**注意**：如果浏览器验证证书无效，会弹出提示说该证书不可信，但**用户可以忽略**，继续使用该证书进行协商加密通道。

## 16. 什么是根证书？证书链如何验证？

**原理分析**

**根证书**是整个证书体系安全的根本。证书体系构成一个树形关系（倒立的树），处于最顶上的树根位置的那个证书就是根证书。根证书**自己证明自己是可靠的**（不需要被证明）。如果根证书出了问题（不再可信），所有被根证书信任的其他证书也就不再可信了。

**验证过程**：

- 操作系统内置（或浏览器内置）的根证书是安全的
- 验证时使用上一级证书的公钥解密签名，拿到证书的摘要
- 对证书信息重新hash，如果和解密后的摘要一致，则证书没有被篡改
- 这是一个**递归的过程，直到验证到根证书**

**最重要的是操作系统预置的根证书的安全**，不要随便往系统安装证书。

![证书链](../assets/16%E7%BD%91%E7%BB%9C/658615ea81b645a186d4f83c4aaaa5f3.png) ![证书验证](../assets/16%E7%BD%91%E7%BB%9C/ca0baaa925394cfebf1908cc13b11082.png)

## 17. HTTPS到底是怎么加密的？

**原理分析**

HTTPS加密分为两部分：

1. **身份验证（非对称加密）**：客户端验证服务器证书，确认服务器是真实的而不是被伪造的，使用RSA验证服务器证书
2. **密钥交换**：使用**DHE（Diffie-Hellman Ephemeral）算法**交换密钥。简单来说，知道`p=n*m`，通过只交换p，服务端解出n，客户端解出m，然后各自使用m和n算出对称加密密钥。这部分没有用到加密，但用了**密钥交换算法**
3. **数据传输（对称加密）**：根据前面的会话密钥各自进行加解密。**服务端给客户端的数据加密密钥和客户端给服务端的数据加密密钥是不相同的**

所以两种说法本质上都是：

- 客户端用服务端公钥加密对称密钥发往服务端（RSA密钥交换方式）
- 或者客户端和服务端各有3个相同的随机数，用共同算法各自计算对称密钥（DHE密钥交换方式）

两种都对，取决于使用的密钥交换算法。

## 18. WebSocket和HTTP的区别和联系是什么？

**原理分析**

**HTTP协议**：

- 应用层协议，基于TCP
- 建立连接必须经过三次握手
- 分为短连接和长连接
- 短连接：每个request对应一个response
- 长连接：在一定期限内保持连接，但必须由客户端发起请求，服务器被动响应。客户端主动，服务器被动

**WebSocket**：

- 为解决客户端发起多个HTTP请求到服务器需要长时间轮询的问题而诞生
- 实现了**多路复用**，是**全双工通信**
- 建立了WebSocket后，服务器不必等浏览器发送请求后再发送信息，**服务器有主动权**
- 信息中不必带有head的部分信息，相比HTTP长连接不仅降低服务器压力，也减少了多余信息

**二者的联系**：

- WebSocket也是基于TCP的协议
- WebSocket的握手阶段使用HTTP协议（通过HTTP的Upgrade头部字段从HTTP切换协议到WebSocket）

## 19. HTTP长连接与WebSocket持久连接有什么区别？

**原理分析**

**HTTP长连接（HTTP/1.1 Keep-Alive）**：

- 在一定期限内保持连接（TCP不断开）
- 客户端短时间内向服务端请求大量资源时复用连接
- 本质上仍是**Request/Response消息对**，客户端主动，服务器被动
- 在一个TCP连接上可以传输多个Request/Response消息对，但仍然会造成资源浪费、实时性不强

**长轮询（Long Polling）**：客户端发送一个超时时间很长的Request，服务器hold住这个连接，在有新数据到达时返回Response。这是HTTP模拟实时通信的一种变通方案。

**WebSocket持久连接**：

- 只需建立**一次Request/Response消息对**，之后都是TCP连接
- 避免了多次Request/Response消息对产生的冗余头部信息
- 全双工通信，服务器可主动推送数据

## 20. 一次完整的HTTP请求从输入URL到页面加载经历了什么？

**原理分析**

**第1步：DNS域名解析**
浏览器查找域名对应的IP地址，查找顺序：

1. **浏览器缓存**
2. **本机缓存**
3. **hosts文件**
4. **路由器缓存**
5. **ISP DNS缓存**
6. **DNS递归查询**（可能存在负载均衡导致每次IP不一样）

**DNS递归/迭代查询详细过程**：

1. 如果本地hosts文件没有找到，浏览器发出DNS请求到本地DNS服务器（网络接入服务商提供，如中国电信、中国移动）
2. 本地DNS服务器先查缓存，有则直接返回（递归）
3. 没有则向**DNS根服务器**查询（迭代）
4. 根DNS服务器没有具体记录，告诉本地DNS服务器到**域服务器**（如.com域）查询
5. .com域服务器告诉本地DNS服务器到**域名解析服务器**查询
6. 本地DNS服务器向域名的解析服务器发出请求，收到域名和IP对应关系，并缓存

**第2步：浏览器向web服务器发送HTTP请求**
拿到IP地址后，浏览器以随机端口（1024<端口<65535）向服务器的WEB程序（httpd、nginx等）80端口发起TCP连接请求，然后发送HTTP请求

**第3步：服务器对HTTP请求处理**
中间可能经过**反向代理服务器、负载均衡服务器**：

1. 数据进入网卡，进入内核TCP/IP协议栈（识别连接请求、解封包）
2. 请求到达HTTP服务器，根据端口找到对应的HTTP服务（如Tomcat、JBoss）
3. 解析HTTP请求URL，根据url找到对应的Servlet，将HTTP请求封装成HttpRequest和HttpResponse
4. 调用Servlet的service方法处理，结果放到response，将结果返回

**第4步：浏览器解析渲染**
浏览器解析返回的HTML、CSS、JavaScript并渲染页面

![HTTP请求流程](../assets/16%E7%BD%91%E7%BB%9C/b43f8d7df790433982bd6dbb4d475a89.png)

## 21. ARP协议在HTTP请求中起什么作用？

**原理分析**

**ARP（Address Resolution Protocol）**：是根据IP地址获取物理地址（MAC地址）的TCP/IP协议。主机发送信息时包含目标IP地址的ARP请求广播到局域网上的所有主机，并接收返回消息，确定目标的物理地址。收到返回消息后将IP地址和物理地址存入本机ARP缓存并保留一定时间，下次请求时直接查询ARP缓存以节约资源。

## 22. HTTP客户端（如OkHttp）如何优化连接池？

**原理分析**

**maxIdleConnections**（最大空闲连接数）：

- 高并发时增加该值，确保连接池中有足够空闲连接处理请求
- 目标服务器连接数限制小时减少该值，避免占用资源
- 客户端资源受限时减少该值
- 空闲连接很少被使用时可减少；经常达到连接池最大容量时可增加

**keepAliveDuration**（连接最大空闲时间）：

- 超过该时间的空闲连接将被关闭并从连接池移除
- 默认5分钟
- 确保不超过服务器的连接超时时间，避免不必要的连接重建
- 高请求频率可适当缩短；高网络延迟可适当增加

**maxRequests / maxRequestsPerHost**：

- **maxRequests**：整个OkHttp客户端实例的最大请求数量（默认64）
- **maxRequestsPerHost**：每个主机的最大请求数量（默认64）
- 控制并发请求数量，避免对服务器造成过大负荷

**生产环境TIME_WAIT过高**：说明大量TCP连接主动关闭。需要调整连接池参数，减少频繁创建和销毁连接。

## 23. Spring MVC中的Content-Type是如何处理的？

**原理分析**

Spring中`@RequestMapping`的produces和consumes属性：

- **produces**：指定返回值类型，可设置返回值类型和字符编码
- **consumes**：指定处理请求的提交内容类型（Content-Type），例如application/json、text/html

服务器端处理请求时，根据Content-Type找到对应的解析器处理数据。如果Controller路径一致但Content-Type不一样，不能进入Controller。

## 24. 浏览器中URL编码有哪几种情况？

**原理分析**

**情况1：网址路径中包含汉字**（如`http://zh.wikipedia.org/wiki/春节`）

- **结论：URL路径的编码用UTF-8编码**。IE自动将"春节"编码成`%E6%98%A5%E8%8A%82`

**情况2：查询字符串包含汉字**（如`http://www.baidu.com/s?wd=春节`）

- **结论：查询字符串编码用操作系统的默认编码**。IE以GB2312编码发送，Firefox同样用GB2312编码但在每个字节前加`%`

**情况3：Get方法生成的URL包含汉字**

- 编码由网页编码决定（HTML源码中charset设置）
- 如果charset是UTF-8，URL就以UTF-8编码；是GB2312就以GB2312编码

**情况4：Ajax调用的URL包含汉字**

- **IE总是采用GB2312编码**（操作系统默认编码）
- **Firefox总是采用UTF-8编码**
- 解决办法：在提交请求前用JS编码

## 25. TCP连接建立和HTTP请求的关系是怎样的？

**原理分析**

1. **TCP通过三次握手建立双方连接**
2. **客户端通过发送请求报文及请求数据给服务端**
3. **服务端返回响应报文及响应数据给客户端**
4. **TCP通过四次挥手进行断开连接**

**关于服务器端主动FIN的处理**：

- 服务端发FIN代表服务端没有数据要发了，请求关闭TCP连接
- 客户端内核立即回复ACK，进入**CLOSE_WAIT**状态
- 连接处于**半关闭**：服务端→客户端通道关闭（客户端读不到数据，read返回0）；客户端→服务端通道还能正常发数据
- 若应用层一直不关闭，**CLOSE_WAIT会长期挂着**

**连接池复用半关闭连接的后果**：

- 客户端发送HTTP请求（write调用会成功，因为内核允许发送到缓冲区）
- 服务端收到数据后直接回复**RST复位报文**
- 客户端收到RST后内核销毁连接
- 实际业务中会出现：**Connection reset by peer（ECONNRESET）**、**Broken pipe（EPIPE）**

**解决方案**：

- 空闲连接设置最大空闲时间，低于服务端超时时间
- 从连接池获取连接时做可读检测/心跳
- 捕获ECONNRESET/EPIPE异常，直接销毁连接，新建重试

## 26. HTTP请求中遇到RST（Connection reset by peer）和ETIMEDOUT的区别是什么？

**原理分析**

**ELB等负载均衡丢连接的场景：**

**一、主动发RST强关（立即报错）**

1. **空闲超时（Idle Timeout）**：TCP/NLB默认350s，HTTP/ALB默认60s。ELB直接发RST，客户端next read/write立即返回ECONNRESET
2. **后端不健康/下线**：四层NLB直接发RST；七层ALB发FIN或RST
3. **连接数/带宽超限、限流、黑白名单**：直接发RST
4. **七层ALB特殊情况**（请求畸形/超长）：直接关闭连接（RST）

**二、ELB静默丢包（等超时）**

1. **网络闪断、ELB实例崩溃/重启**：不发FIN/RST，直接丢包。客户端连接看似正常，发请求后一直阻塞直到超时（如60s），报**ETIMEDOUT**
2. **后端无响应、ELB转发超时**：先等ELB超时（默认60s），若ELB发RST→立即ECONNRESET，若静默丢→再等自己超时→ETIMEDOUT

**一句话区分**：有RST→立即ECONNRESET/EPIPE（主动强关）；无RST、无数据→等到ETIMEDOUT（静默丢包）。

## 27. ECONNRESET和EPIPE有什么区别？

**原理分析**

**ECONNRESET（Connection reset by peer）**：

- 触发时机：调用read/recv/write时，**内核刚好收到对方发来的RST报文**
- 本质：TCP对端强制重置连接

**EPIPE（Broken pipe）**：

- 触发时机：连接已经收到RST、内核标记连接已断开，**你又再次调用write往里面写数据**
- 本质：连接早已断开还硬写数据

**Linux下关键行为**：

- 对已经收到RST的socket，**第一次写**：返回失败，errno = **ECONNRESET**
- **第二次及以后再写**：errno = **EPIPE**，同时会给进程发**SIGPIPE信号**，默认情况下程序会直接崩溃退出

