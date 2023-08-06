# Android APP Implementing Emoji Search with Embedding

Enter a description of the relevant emoji to find the corresponding emoji

The detailed construction process and technical documents can be viewed on [JueJin](https://juejin.cn/post/7264073604496638011)

## 🎥 Feature Preview

![emoji-search-app-demo](./assets/emoji-search-app-demo.gif)

## 🕐 Project  Timeline Introduction

![emoji-search-app-sequence-diagram](./assets/emoji-search-app.drawio.png)

The construction of the project is divided into two parts, preparing data and building the Android APP. The specific steps are as follows:

- Data preparation
  - Use Python to parse and get Emoji data
  - Send POST request to OpenAI to get Emoji Embedding data
  - Save Emoji data as a Json formatted file
  - Copy the data to the Android APP, and convert the data into the format of Protocol Buffers
- Build Android APP
  - Load Emoji data into memory
  - Process user input and get user input Embeddings
  - Perform dot product operation with Embeddings and Emoji Embeddings, and get the top 20 most relevant Emojis

## 📱 APP Experience

1. Download the experience APP from the [release](https://github.com/sunnyswag/emoji-search/releases/tag/v1.1.0) page
2. Install it on your phone, turn on scientific internet access, and make sure you can access ChatGPT

## 🚀 Quick Start

1. Clone the repository to local

   ```shell
   git clone https://github.com/sunnyswag/emoji-search.git
   ```

2. Prepare data (use data from this [release](https://github.com/sunnyswag/emoji-search/releases/tag/v1.0.1-beta), you can skip this step)

   * Export OpenAI Key to the environment variable

     ```shell
     # Windows
     $Env:OPENAI_KEY = "your_openai_key"
     # Mac or Linux
     export OPENAI_KEY=your_openai_key
     ```

   * Install relevant Python dependencies in [requirements.txt](https://github.com/sunnyswag/emoji-search/blob/main/Python/requirements.txt)

     ```shell
     pip install -r requirements.txt
     ```

   * Set command line proxy

     ```shell
     set http_proxy=http://127.0.0.1:7890
     set https_proxy=http://127.0.0.1:7890
     ```

   * Run [build_emoji_data.py](https://github.com/sunnyswag/emoji-search/blob/main/Python/build_emoji_data.py) to get the processed json.gz file

     ```shell
     python ./build_emoji_data.py
     ```

   * Convert json.gz data to Protocol Buffer format

     Copy json.gz data to Android Project

     Modify AppInitializer.kt in Android as follows:

     ```Kotlin
     private suspend fun readEmojiEmbeddings(context: Context) {
         ProcessorFactory.doProcess(
             context,
             ProcessorType.JSON_TO_PROTOBUF_PROCESSOR,
             listOf(R.raw.emoji_embeddings_json)
         )
     }
     ```

3. Run Android APP

   * Put Emoji Embedding data into the res/raw folder and specify the corresponding data type in AppInitializer.kt:

     ```kotlin
     private suspend fun readEmojiEmbeddings(context: Context) {
         ProcessorFactory.doProcess(
             context,
             // Use protobuf for loading
             ProcessorType.PROTOBUF_PROCESSOR,
             listOf(R.raw.emoji_embeddings_proto)
         )
     }
     ```

## 📝 TODO

- [ ] Train Mobile-Bert to replace OpenAI Embedding API
- [ ] Add Emoji search history
- [ ] Enable R8 compilation to reduce package size
- [ ] Upload to Google Play

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

## 📧 Contact

If you encounter any problems during use, or have any suggestions or feedback, please contact me through the following ways:

Email: [hhq126152@gmail.com](mailto:hhq126152@gmail.com)

## 🤝 Contribution Guidelines

If you want to participate in this project, here are some ways to contribute:

1. **Submit Issues**: Submit issues or suggestions through the project's [Issues page](https://github.com/sunnyswag/emoji-search/issues).

2. **Submit Updates**: Submit a Pull Request to fix issues or add new features. For large changes (such as new features or large refactoring), it is recommended to start an issue for discussion first.

   Steps to submit a Pull Request:

   - Fork the repository to your GitHub account

   - Create a new branch for modifications

   - Commit your changes and push to Github

   - Submit a Pull Request to the original repository on GitHub

3. **Improve Documentation**: If you find errors in the related documentation or can make improvements, you are also welcome to submit a Pull Request.

When submitting a Pull Request, please ensure that your code style is consistent with the existing code in the project, and that all tests can pass.

Thank you in advance for your contribution!

## 🙏 ACKNOWLEDGE

[Emoji Search](https://www.emojisearch.app/): The inspiration for this project

[https://github.com/lilianweng/emoji-semantic-search](https://github.com/lilianweng/emoji-semantic-search): Referenced Emoji data processing related code

[UTS #51: Unicode Emoji](https://www.unicode.org/reports/tr51/tr51-21.html): Provides Emoji related data

[https://github.com/carpedm20/emoji](https://github.com/carpedm20/emoji): Provides Emoji related data

https://github.com/zenled/Emoji_Browser：Provide the Icon of Emoji APP

https://fonts.google.com/icons：Provide the material Icons 

## 🔗 REFERENCE

[When Android needs to read a 47M json.gz file - JueJin](https://juejin.cn/post/7253744712409071673)

[https://github.com/aallam/openai-kotlin](https://github.com/aallam/openai-kotlin)

[https://github.com/Kotlin/multik](https://github.com/Kotlin/multik)