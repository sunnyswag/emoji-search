# 使用 Embedding 实现 Emoji Search 的 Android APP

输入一段你对于相关 Emoji 的描述，来找到对应的 Emoji 表情

详细的构建过程和技术文档可以查看 [掘金](https://juejin.cn/spost/7264073604496638011)

[English README.md](./README.EN.md)

## 🎥 功能预览

![emoji-search-app-demo](./assets/emoji-search-app-demo.gif)

## 🕐 项目的时序简介

![emoji-search app 时序图](./assets/emoji-search-app.drawio.png)

​	项目的构建分为两个部分，准备数据和构建 Android APP，具体的步骤如下：

- 准备数据
  - 用 Python 来解析并获取到 Emoji 的数据
  - 向 OpenAI 发送 POST 请求，获取 Emoji 的 Embedding 数据
  - 将 Emoji 数据保存为 Json 格式的文件
  - 将数据复制到 Android APP，并将数据转换为 Protocol Buffers 的格式
- 构建 Android APP
  - 加载 Emoji 数据到内存
  - 处理用户输入，拿到用户输入的 Embeddings
  - 将 Embeddings 和 Emoji 的 Embedding 做点积运算，得到前 20 个最相关的 Emoji

## 📱 APP 体验

1. 从 [release](https://github.com/sunnyswag/emoji-search/releases/tag/v1.1.0) 界面下载体验 APP
2. 安装到手机，开启科学上网，确保可以访问到 ChatGPT 即可

## 🚀 快速开始

1. 将仓库 clone 到本地

   ```shell
   git clone https://github.com/sunnyswag/emoji-search.git
   ```

2. 准备数据(使用 [release](https://github.com/sunnyswag/emoji-search/releases/tag/v1.0.1-beta) 的数据，可跳过该步骤)

   * 将 OpenAI Key Export 到环境变量

     ```shell
     # Windows
     $Env:OPENAI_KEY = "your_openai_key"
     # Mac or Linux
     export OPENAI_KEY=your_openai_key
     ```

   * 安装好 [requirements.txt](https://github.com/sunnyswag/emoji-search/blob/main/Python/requirements.txt) 中的相关 Python 依赖

     ```shell
     pip install -r requirements.txt
     ```

   * 设置命令行代理

     ```shell
     set http_proxy=http://127.0.0.1:7890
     set https_proxy=http://127.0.0.1:7890
     ```

   * 运行 [build_emoji_data.py](https://github.com/sunnyswag/emoji-search/blob/main/Python/build_emoji_data.py) 得到处理好的 json.gz 文件

     ```shell
     python ./build_emoji_data.py
     ```

   * json.gz 数据转换为 Protocol Buffer 格式

     将 json.gz 数据 cp 到 Android Project

     Android 中 AppInitializer.kt 修改成如下：

     ```Kotlin
     private suspend fun readEmojiEmbeddings(context: Context) {
         ProcessorFactory.doProcess(
             context,
             ProcessorType.JSON_TO_PROTOBUF_PROCESSOR,
             listOf(R.raw.emoji_embeddings_json)
         )
     }
     ```

3. 运行 Android  APP

   * 将 Emoji Embedding 数据放到 res/raw 文件夹下，并在 AppInitializer.kt 指定对应的数据类型：

     ```kotlin
     private suspend fun readEmojiEmbeddings(context: Context) {
         ProcessorFactory.doProcess(
             context,
             // 使用 protobuf 进行加载
             ProcessorType.PROTOBUF_PROCESSOR,
             listOf(R.raw.emoji_embeddings_proto)
         )
     }
     ```

## 📝 TODO

- [ ] 训练 Mobile-Bert 代替 OpenAI Embedding API
- [ ] 添加 Emoji 搜索的历史记录
- [ ]  开启 R8 编译减少包体积
- [ ] 上传到 Google Play

## 📜 License

```
MIT License

Copyright (c) 2023 sunnyswag

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📧 联系方式

如果你在使用过程中遇到问题，或者有任何建议和反馈，欢迎通过以下方式联系我：

电子邮件：[hhq126152@gmail.com](mailto:hhq126152@gmail.com)

## 🤝 贡献指南

如果你希望参与此项目，以下是一些贡献的方式：

1. **提交问题**：通过项目的 [Issues 页面](https://github.com/sunnyswag/emoji-search/issues) 提交问题或者建议。

2. **提交更新**：提交 Pull Request 来修复问题或者增加新的功能。对于大的更改（如新特性或大型重构），建议先开启一个 issue 讨论。

   提交 Pull Request 的步骤：

   - Fork 仓库到你的 GitHub 账户下

   - 创建一个新的分支用于进行修改

   - 提交你的更改并 Push 到 Github

   - 在 GitHub 上向原始仓库提交一个 Pull Request

3. **改善文档**：如果你发现相关文档有错误，或者可以进行改进，也欢迎你提交 Pull Request。

在提交 Pull Request 时，请确保你的代码风格与项目现有的代码保持一致，并且所有的测试都能通过。

提前感谢你的贡献！

## 🙏 ACKNOWLEDGE

[Emoji Search](https://www.emojisearch.app/)：本项目的灵感来源

[https://github.com/lilianweng/emoji-semantic-search](https://github.com/lilianweng/emoji-semantic-search)：参考了 Emoji 数据处理相关代码

[UTS #51: Unicode Emoji](https://www.unicode.org/reports/tr51/tr51-21.html)：提供了 Emoji 相关的数据

[https://github.com/carpedm20/emoji](https://github.com/carpedm20/emoji)：提供了 Emoji 相关的数据

[https://github.com/zenled/Emoji_Browser](https://github.com/zenled/Emoji_Browser)：提供了 Emoji APP 的 Icon

[https://fonts.google.com/icons](https://fonts.google.com/icons)：提供了 Material 相关的 Icon

## 🔗 REFERENCE

[Android 当你需要读一个 47M 的 json.gz 文件 - 掘金](https://juejin.cn/post/7253744712409071673)

[https://github.com/aallam/openai-kotlin](https://github.com/aallam/openai-kotlin)

[https://github.com/Kotlin/multik](https://github.com/Kotlin/multik)
