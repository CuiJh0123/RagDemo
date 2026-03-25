## 一、需求

### 1、背景

为了进一步理解大模型应用开发的流程，特别是对于RAG的理解，构建一个简单的网页问答系统，使用户能够基于预设文档进行回答。

### 2、核心目标

实现一个最小可运行的RAG Demo：

- **用户能够在网页输入问题**
- **系统基于本地文档进行检索**
- **把检索的结果作为上下文输入大模型**
- **返回更为准确的回答**

### 3、使用场景

**用户希望查询项目相关信息，例如：**

- **拼团规则是什么？**
- **订单状态如何流转？**
- **系统有哪些业务模块？**

### 4、需求分析

**功能一：问题输入**

用户可以在前端页面中输入问题

**功能二：文档检索**

系统从本地知识库中检索相关内容

**功能三：RAG增强回答**

将检索结果拼接进提示词，调用大模型生成回答

**功能四：结果展示**

把大模型回答展示给用户

## 二、技术方案设计

### 1、整体架构

> [!NOTE]
>
> **基于SpringBoot+LangChain4j实现一个最小的RAG问答系统。**

### 2、核心流程

```
用户提问
	↓
后端接收请求
	↓
问题向量化（embedding）
	↓
向量库相似度检索（topK）
	↓
获取相关文档片段
	↓
拼接 prompt（问题 + 上下文）
	↓
调用大模型
	↓
返回答案
```

### 3、RAG设计

**文档来源：本地**

**文档处理流程：文档加载->embedding->存入向量库**

**检索流程：用户问题->embedding->向量余弦相似度检索->topK文档**

**提示词设计：分为用户提示词以及系统提示词，系统提示词也写入文档中**

### 4、技术选型

|  模块  |           技术            |
| :----: | :-----------------------: |
|  后端  |        Spring Boot        |
| AI框架 |        LangChain4j        |
|  模型  | Qwen问答以及Embedding模型 |
| 向量库 |        内存向量库         |
|  前端  |   HTML+JS（用AI辅助写）   |

## 三、系统模块

**考虑到系统是一个简单的问答系统，因此可以用MVC架构快速实现，不再使用DDD进行领域建模。**

- **Controller层：接收请求**
- **Service层：提供Ai Service能力**
- **Config层：用于配置RAG等**
- **DAO层：如果需要在MySQL数据库记录用户记录的话**

## 四、设计实现效果

### 1、未加RAG前的最小闭环Demo

![image-20260325170441655](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325170441655.png)

**这里引入了系统提示词**：

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325170522254.png" alt="image-20260325170522254" style="zoom:50%;" />

**用GPT辅助做了一个前端界面**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325170609872.png" alt="image-20260325170609872" style="zoom:50%;" />

### 2、引入RAG

**Step1：先引入一个编码器，用到的是千问的一个Embedding**

![image-20260325172748236](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325172748236.png)

**Step2：引入RAG需要的知识文档**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325172817749.png" alt="image-20260325172817749" style="zoom:50%;" />

**Step3：定义配置类**

![image-20260325180922120](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325180922120.png)

![image-20260325180942866](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325180942866.png)

**Step4：由于引入了递归分割器，所以需要在yml中配置下max-batch-size为10**

![image-20260325181027928](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325181027928.png)

**这是一个bug点。**

### 3、结果展示：有无RAG对比

**只需要在Controller层新增一个请求接口，并写一个新的不带RAG检索增强的服务，调用即可。**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325181849602.png" alt="image-20260325181849602" style="zoom:50%;" />

![image-20260325181929766](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325181929766.png)

**进一步用AI修改下前端页面：支持RAG增强版本以及不带RAG的版本**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325182408391.png" alt="image-20260325182408391" style="zoom:50%;" />

**我验证的消息如下：**

- 拼团必须几人成团？
- 退款可以发生在哪些状态？
- domain层负责什么？

**问题一：**

| 普通回答                                                     | RAG检索增强回答                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20260325182931220](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325182931220.png) | <img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325182953629.png" alt="image-20260325182953629" style="zoom:100%;" /> |

**问题二：**

| 普通回答                                                     | RAG检索增强回答                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20260325183141967](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325183141967.png) | ![image-20260325183222290](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325183222290.png) |

**问题三：**

| 普通回答                                                     | RAG检索增强回答                                              |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![image-20260325183340104](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325183340104.png) | ![image-20260325183411988](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325183411988.png) |

**现象：发现两种回答差不多，分析原因：经过日志输出信息来判断，就算前端选择不走RAG检索增强的问答，后端依然有检索的信息**

![image-20260325185350242](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325185350242.png)

**可能原因：一旦配置类生效了，会全局注入Spring容器，对所有的AiService都会生效**

**排查过程：在Controller入口处新增日志输出**

![image-20260325190620522](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325190620522.png)

**虽然日志输出进入了普通接口，但还会走RAG检索增强**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325190648367.png" alt="image-20260325190648367" style="zoom:50%;" />

**所以可能是对所有的AiService都生效了。所以我先不把RAG这个分支合并到主分支，主分支依旧是只有系统提示词的问答，基于此来分析结果。**

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325191536466.png" alt="image-20260325191536466" style="zoom:100%;" />

**以上是没有RAG检索的。**

**接下来是有RAG检索的：**

![image-20260325191700613](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20260325191700613.png)

