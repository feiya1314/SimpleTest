# Qwythos-9B-Claude-Mythos 本地部署详细方案

- 模型：**empero-ai/Qwythos-9B-Claude-Mythos-5-1M**
- 基座：Qwen3.5-9B，全参数微调，1M 上下文，支持 Agent/Function Calling
- 环境：WSL2 Ubuntu + Windows（Clash 代理），**NVIDIA RTX 5060 Ti 16GB VRAM**

---

# 1. 环境准备

## 1.1 配置代理（WSL 通过 Clash 访问外网）

**用途**：Clash 运行在 Windows 上，WSL 直接 curl 不走代理会导致 HuggingFace/github 连接超时。需要配置 `HTTP_PROXY` 环境变量让 WSL 的网络请求经过 Windows 上的 Clash

```bash
# 检查 Clash 代理端口（Clash 默认通常为 7890/7897/1080 等）
# 测试代理是否可用
curl -s --connect-timeout 3 -x http://127.0.0.1:7897 https://www.google.com -o /dev/null -w "%{http_code}"

# 如果返回 302，说明代理可用。将代理写入 shell 配置，持久生效
cat >> ~/.zshrc << 'EOF'

# Clash 代理（WSL 通过 localhost 访问 Windows Clash）
export HTTP_PROXY=http://127.0.0.1:7897
export HTTPS_PROXY=http://127.0.0.1:7897
export NO_PROXY=localhost,127.0.0.1,.local,.hermes
EOF

source ~/.zshrc
```

**验证代理生效**：

```bash
curl -sL https://huggingface.co/api/models/empero-ai/Qwythos-9B-Claude-Mythos-5-1M-GGUF | head -5
```

- 能返回 JSON 数据 → 代理生效
- 返回空或超时 → 检查 Clash 端口或代理配置

**⚠️ 实际部署踩坑**：代理仅适合小请求（API 查询）。**下载大文件（GGUF 6.4GB）时，Clash 代理会导致 `SSL: UNEXPECTED_EOF_WHILE_READING` 错误**，原因是代理对 HuggingFace CDN（us.aws.cdn.hf.co）的 TLS 处理有问题。解决方案：不经过代理，直接用 `hf-mirror.com`（国内镜像，实测 52MB/s 远快于代理）

---

## 1.2 确认 GPU 可用

**用途**：确认 WSL 内能调用 NVIDIA GPU 进行 CUDA 加速推理。没有 GPU 加速的话 9B 模型跑在 CPU 上会非常慢

```bash
# 方法一：nvidia-smi
nvidia-smi
# 或
/usr/lib/wsl/lib/nvidia-smi

# 正常输出示例：
# NVIDIA GeForce RTX 5060 Ti
# 16311MiB VRAM
# CUDA Version: 13.1

# 方法二：检查 CUDA 库
ls /usr/lib/wsl/lib/libcuda*
# 应该包含 libcuda.so, libcuda.so.1 等

# 方法三：检查驱动版本
cat /proc/driver/nvidia/version
```

**如果 nvidia-smi 找不到**：WSL 需要从 Windows 安装 NVIDIA 驱动（Windows 上装，WSL 自动继承），然后在 WSL 中 `sudo apt install nvidia-utils-<version>` 或直接用 `/usr/lib/wsl/lib/nvidia-smi`

---

## 1.3 安装 CUDA 工具包（如需要）

**用途**：Ollama 和 llama.cpp 自带 CUDA 支持（从 NVIDIA GPU 驱动中获取），通常不需要额外安装 CUDA Toolkit。但如果后续需要编译/调试，可以安装

```bash
# 检查 CUDA 版本
nvidia-smi | grep "CUDA Version"

# 如果需要安装 CUDA Toolkit（通常不需要）
# wget https://developer.download.nvidia.com/compute/cuda/repos/wsl-ubuntu/x86_64/cuda-keyring_1.1-1_all.deb
# sudo dpkg -i cuda-keyring_1.1-1_all.deb
# sudo apt update && sudo apt install cuda-toolkit-12-8
```

> ⚠️ WSL2 上通常**不需要**单独安装 CUDA Toolkit，Ollama 和 llama.cpp 直接使用 `/usr/lib/wsl/lib/` 下的驱动即可

---

# 2. 部署方式一：Ollama（推荐，最简单）

## 2.1 安装 Ollama

**用途**：Ollama 是目前最流行的大模型本地运行工具，封装了 llama.cpp，提供命令行和 REST API，自动处理 GPU 加速、量化加载、模型管理等

### 方法 A：自动安装脚本（推荐，需要 sudo）

```bash
# 安装
curl -fsSL https://ollama.com/install.sh | sh
```

### 方法 B：手动安装（WSL 无 sudo 时）

**实际部署**：在 WSL 上 `curl | sh` 需要 sudo 密码且无法交互输入。改用 GitHub Releases 手动部署：

```bash
# 1. 获取最新版本号（或直接指定）
export RELEASE_TAG=v0.31.1
curl -L -o /tmp/ollama.tar.zst \
  "https://github.com/ollama/ollama/releases/download/$RELEASE_TAG/ollama-linux-amd64.tar.zst"
# 文件约 1.4GB（含所有 CUDA/CPU 依赖库）

# 2. 安装 Python zstandard 后解压（tar.zst 格式）
pip install zstandard
python3 -c "
import tarfile, zstandard
dctx = zstandard.ZstdDecompressor()
with open('/tmp/ollama.tar.zst', 'rb') as f:
    with dctx.stream_reader(f) as reader:
        with tarfile.open(fileobj=reader, mode='r|') as tar:
            tar.extractall(path='/tmp/ollama_extract')
"

# 3. 复制到 ~/.local（注意：bin + lib/ollama 两个目录都要复制！）
mkdir -p ~/.local/bin ~/.local/lib/ollama
cp /tmp/ollama_extract/bin/ollama ~/.local/bin/
cp -r /tmp/ollama_extract/lib/ollama/* ~/.local/lib/ollama/
# lib/ollama/ 包含 llama-quantize、llama-server、CUDA v12/v13 库、CPU 优化库
# 只复制 bin/ollama 会导致 ollama create 失败（找不到 llama-quantize）

# 4. 启动服务
export PATH="$HOME/.local/bin:$PATH"
ollama serve &
# 检测服务是否启动：curl http://localhost:11434/api/tags
# 应返回 {"models":[]}

---

## 2.2 下载 GGUF 量化模型文件

**用途**：该模型不在 Ollama 官方模型库中，需要从 HuggingFace 下载 GGUF 文件后通过 Modelfile 导入

**量化版本选择**（以 RTX 5060 Ti 16GB 为例）：

| 量化 | 文件大小 | VRAM占用 | 质量 | 推荐理由 |
|:----|:-------:|:--------:|:----|:---------|
| **Q4_K_M** | ~5.5GB | ~9GB | 🟡 较好 | VRAM 最充裕，可同时开大上下文 |
| **Q5_K_M** | ~6.5GB | ~10GB | 🟢 推荐 | 质量速度最佳平衡 |
| **Q6_K** | ~7.5GB | ~11GB | 🟢 很好 | 质量优先，仍有余量 |
| **Q8_0** | ~9.5GB | ~13GB | 🔵 极好 | 最高质量，VRAM 较紧张 |

> 💡 **推荐 Q5_K_M**：质量损失极小（<3%），VRAM 占用 10GB，余量 6GB 可支撑 128K 上下文。另有 **MTP 版**（Multi-Token Prediction）推理速度更快，优先选 MTP 版本

```bash
# 创建模型存放目录
mkdir -p ~/models

# 下载 GGUF 文件（选一个你想要的量化级别）
# 推荐 Q5_K_M 版本（含 MTP 加速，优先选 MTP 版）
cd ~/models

# ⚠️ 实际部署经验：不要通过 Clash 代理下载大文件！
# HuggingFace CDN 经代理会 SSL 错误。正确的做法：
# 1. 取消代理环境变量
unset HTTP_PROXY HTTPS_PROXY ALL_PROXY
# 2. 改用国内镜像 hf-mirror.com（实测 ~52MB/s，2分钟下完6.4GB）
curl -L -o qwythos-9b-Q5_K_M.gguf \
  https://hf-mirror.com/empero-ai/Qwythos-9B-Claude-Mythos-5-1M-GGUF/resolve/main/Qwythos-9B-Claude-Mythos-5-1M-MTP-Q5_K_M.gguf
# 
# 如果用原站需要走代理：
# export HTTPS_PROXY=http://127.0.0.1:7897
# curl -L -o qwythos-9b-Q5_K_M.gguf \
#   https://huggingface.co/empero-ai/Qwythos-9B-Claude-Mythos-5-1M-GGUF/resolve/main/Qwythos-9B-Claude-Mythos-5-1M-MTP-Q5_K_M.gguf

# 可用的 GGUF 文件列表（从 HuggingFace API 获取）：
# Qwythos-9B-Claude-Mythos-5-1M-BF16.gguf          (原始精度，~18GB，显存放不下)
# Qwythos-9B-Claude-Mythos-5-1M-MTP-BF16.gguf       (原始精度+MTP)
# Qwythos-9B-Claude-Mythos-5-1M-MTP-Q4_K_M.gguf     (MTP+4bit)
# Qwythos-9B-Claude-Mythos-5-1M-MTP-Q5_K_M.gguf     ⬅ 推荐
# Qwythos-9B-Claude-Mythos-5-1M-MTP-Q6_K.gguf       (可选)
# Qwythos-9B-Claude-Mythos-5-1M-MTP-Q8_0.gguf       (可选,VRAM吃紧)
# Qwythos-9B-Claude-Mythos-5-1M-Q4_K_M.gguf         (无MTP)
# Qwythos-9B-Claude-Mythos-5-1M-Q5_K_M.gguf         (无MTP)
# Qwythos-9B-Claude-Mythos-5-1M-Q6_K.gguf           (无MTP)
# Qwythos-9B-Claude-Mythos-5-1M-Q8_0.gguf           (无MTP)

# 验证文件完整性
ls -lh qwythos-*.gguf
# 应显示约 5.5-7.5GB 大小

# 计算文件哈希（可选，验证下载完整性）
sha256sum qwythos-9b-Q5_K_M.gguf
```

**下载慢怎么办？**
- 确保已配置代理
- 用 `aria2c` 替代 curl 可多线程下载：`aria2c -x 4 -s 4 [URL]`
- 或在 Windows 上直接下载后复制到 WSL：`cp /mnt/c/Users/feiya/Downloads/\*.gguf ~/models/`

---

## 2.3 创建 Ollama Modelfile 并导入

**用途**：Modelfile 告诉 Ollama 如何加载这个 GGUF 文件，以及配置模型的默认参数（上下文长度、温度等）

```bash
# 创建 Modelfile
cat > ~/models/Modelfile << 'EOF'
# 模型来源 GGUF 文件路径
FROM ./qwythos-9b-Q5_K_M.gguf

# 系统提示词（定义模型角色）
SYSTEM """You are Qwythos, a helpful AI assistant based on Qwen3.5 fine-tuned with Claude data.
You support long context, function calling, and agentic tasks."""

# 默认参数设置
PARAMETER num_ctx 32768          # 默认上下文窗口（模型支持 1M，默认先用 32K）
PARAMETER temperature 0.7        # 生成温度（0.0-2.0，越高越随机）
PARAMETER top_p 0.9              # Top-P 采样
PARAMETER top_k 40               # Top-K 采样
PARAMETER stop "</s>"            # 停止词
PARAMETER stop "<|im_end|>"      # 停止词（Qwen 格式）
EOF

# 导入到 Ollama
ollama create qwythos-9b -f ~/models/Modelfile

# 导入成功后 Ollama 会做以下工作：
# 1. 读取 GGUF 文件头部信息（模型结构、词汇表等）
# 2. 将 Modelfile 中的参数写入 Ollama 的模型数据库
# 3. 注册模型名为 qwythos-9b（可自定义）
# 4. GGUF 文件本身不会被复制，Ollama 直接引用原路径

# 验证模型列表
ollama list
# 输出示例：
# NAME              ID              SIZE    MODIFIED
# qwythos-9b        abc123def456    6.5 GB  2 minutes ago

# 查看模型详情
ollama show qwythos-9b
```

---

## 2.4 测试运行

**用途**：验证模型是否能正常加载、推理，确认 GPU 加速生效

```bash
# 命令行运行
ollama run qwythos-9b

# Ollama 执行流程：
# 1. 将 GGUF 模型加载到 GPU 显存（通过 llama.cpp CUDA backend）
# 2. GGUF 加载时自动将各层分配到 GPU（-ngl 99 = 全部分配到 GPU）
# 3. 等待用户输入
# 4. 用户输入后 → 分词（tokenize）→ GPU 前向传播 → 解码 → 输出

# 进入交互界面后，输入测试：
# >>> 你好，请介绍一下你自己
# >>> 9.11 和 9.9 哪个大？
# >>> 用 Python 写一个快排

# 也可以一次性运行（非交互式）
echo "你好，请介绍一下你自己" | ollama run qwythos-9b

# 测试 GPU 是否真正在工作（另一个终端运行）
watch -n 1 nvidia-smi
# 观察 GPU-Util 和 Memory-Usage 在推理时是否变化
```

**运行效果预期**：
- 首次加载约 5-15 秒（GGUF 加载+KV Cache 初始化）
- 推理速度约 **20-40 tokens/秒**（Q5_K_M + RTX 5060 Ti）
- 后续对话即时响应（KV Cache 缓存已就绪）

---

## 2.5 通过 API 调用（可选）

**用途**：Ollama 启动后默认在 `http://localhost:11434` 提供 OpenAI 兼容的 REST API，可让其他工具（如 Open WebUI、Cursor、VS Code 插件）调用

```bash
# 确认 API 服务在运行
curl http://localhost:11434/api/generate -d '{
  "model": "qwythos-9b",
  "prompt": "你好",
  "stream": true
}'

# API 端点说明：
# POST /api/generate    — 文本生成（支持流式）
# POST /api/chat        — 对话模式
# GET  /api/tags        — 列出模型
# POST /api/embeddings  — 获取文本嵌入向量

# 用 Python 调用 API 的示例
python3 << 'PYEOF'
import requests, json

response = requests.post("http://localhost:11434/api/generate", json={
    "model": "qwythos-9b",
    "prompt": "用 Python 写一个冒泡排序",
    "stream": False
})
print(json.loads(response.text)["response"])
PYEOF
```

---

# 3. 部署方式二：llama.cpp 直接运行（灵活性最高）

## 3.1 编译 llama.cpp

**用途**：llama.cpp 是 Ollama 的底层推理引擎，直接使用可获得最高灵活性和最新特性，但需要手动编译

```bash
# 克隆仓库
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp

# 编译（CUDA 加速版）
# -j 表示并行编译，数字为 CPU 线程数
make -j$(nproc) LLAMA_CUDA=1

# 编译产出的关键二进制文件：
# ./llama-cli      — 命令行推理工具
# ./llama-server   — HTTP API 服务器
# ./llama-quantize — 量化工具
# ./llama-perplex  — 评估工具

# 验证编译成功
./llama-cli --version
# 输出：version: 4020 (或类似数字)

# 如果 make 失败，可能需要安装依赖：
# sudo apt install build-essential cmake
```

---

## 3.2 运行推理（命令行）

**用途**：直接用 llama-cli 加载 GGUF 进行推理，手动控制所有参数

```bash
# 基本用法
./llama-cli \
  -m ~/models/qwythos-9b-Q5_K_M.gguf \   # 模型文件路径
  -ngl 99 \                                # GPU 卸载层数（99=全部卸载到GPU）
  -c 32768 \                               # 上下文长度（Context Size）
  -n 512 \                                 # 生成的最大 token 数
  -t $(nproc) \                            # CPU 线程数（用于无法卸载到 GPU 的部分）
  --temp 0.7 \                             # 温度
  --repeat-penalty 1.1 \                   # 重复惩罚
  --prompt "你好，请介绍一下你自己"          # 输入提示词

# 参数详细说明：
# -m     模型文件路径（.gguf）
# -ngl   GPU 卸载层数关键：
#         - 0 = 全部在 CPU 跑（极慢。9B模型~2-3 token/s）
#         - 99 = 全部在 GPU 跑（推荐。RTX 5060 Ti ~30-40 token/s）
#         - 一半层数 = 部分 GPU 部分 CPU（显存不够时的折中）
# -c     上下文长度（越长越吃显存。每增加 1024 tokens 约多占 ~50MB VRAM）
# -n     生成的 tokens 数上限
# -t     CPU 线程数（CPU 处理 tokenization/采样等非推理任务）

# 交互式会话（类似 Ollama run）
./llama-cli \
  -m ~/models/qwythos-9b-Q5_K_M.gguf \
  -ngl 99 \
  -c 32768 \
  --interactive \
  --color

# 交互模式中：
# - 输入内容按回车发送
# - /exit 退出
# - /reset 重置对话历史
# - Ctrl+C 终止当前生成
```

---

## 3.3 启动 API 服务（llama-server）

**用途**：启动 HTTP 服务，提供与 Ollama 兼容的 API，供外部程序调用

```bash
# 启动服务器
./llama-server \
  -m ~/models/qwythos-9b-Q5_K_M.gguf \
  -ngl 99 \
  -c 32768 \
  --host 0.0.0.0 \        # 监听所有网络接口
  --port 8080 \            # 端口号
  --n-gpu-layers 99        # 等效于 -ngl

# 后台启动（不占用终端）
nohup ./llama-server \
  -m ~/models/qwythos-9b-Q5_K_M.gguf \
  -ngl 99 \
  -c 32768 \
  --host 0.0.0.0 \
  --port 8080 > ~/llama-server.log 2>&1 &

# 测试 API
curl http://localhost:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwythos-9b",
    "messages": [{"role": "user", "content": "你好"}],
    "stream": false
  }'

# API 兼容 OpenAI 格式，可直接对接：
# - Open WebUI
# - VS Code Continue 插件
# - Cursor
# - 自定义 Python 脚本
```

---

# 4. 部署方式三：LM Studio（Windows 原生）

**用途**：LM Studio 提供图形界面，在 Windows 上直接操作，无需 WSL 命令行。适合不熟悉命令行的用户

```bash
# 操作步骤（在 Windows 上执行，非 WSL）：
# 
# 1. 下载安装 LM Studio
#    https://lmstudio.ai/
#    下载 .exe 安装包，双击安装即可
#
# 2. 搜索模型或本地导入
#    方式 A：在 LM Studio 搜索栏搜 "qwythos"
#    方式 B：手动下载 GGUF 文件后，在 LM Studio 中点击
#            "Local Models" → "Add Model" → 选择 .gguf 文件
#
# 3. 选择量化版本
#    在模型页面下拉选择 Q5_K_M.gguf
#
# 4. 配置并加载
#    GPU Offload: 拉到最大（Full offload）
#    Context Length: 8192（先设小点测试，再逐步增加）
#    Load 模式: "Default" 或 "GPU + CPU"
#    点击 "Load Model" 按钮
#
# 5. 使用
#    Chat 界面：直接对话
#    Local Server：启动后提供 http://localhost:1234/v1 的 OpenAI 兼容 API
```

---

# 5. 量化选择详解

## 不同量化等级的原理

**用途**：量化（Quantization）是将模型权重从高精度（FP16/BF16）降低到低精度（INT4/INT5/INT8）的压缩技术，用微小质量损失换取大幅降低显存占用和加速推理

| 量化等级 | 位宽 | 参数量/权重 | 文件大小(9B) | 质量损失 |
|:--------|:---:|:----------:|:-----------:|:--------:|
| BF16（原始） | 16bit | 2字节 | ~18GB | 无（基线） |
| Q8_0 | 8bit | 1字节 | ~9.5GB | ~1-2% |
| Q6_K | 6bit | 0.75字节 | ~7.5GB | ~2-3% |
| Q5_K_M | 5bit | 0.625字节 | ~6.5GB | ~3-5% |
| Q4_K_M | 4bit | 0.5字节 | ~5.5GB | ~5-8% |
| Q3_K_M | 3bit | 0.375字节 | ~4.5GB | ~10-15%（不推荐） |

**量化等级命名规则**（llama.cpp 的 K-quant 体系）：

- **Q** = Quantization（量化）
- **数字** = 位宽（4/5/6/8）
- **K** = K-quant 方法（根据每层重要性动态分配位宽）
- **_M** = Medium（中等大小，相比 _S 更均衡）
- **_S** = Small（更小，质量稍差）
- **_L** = Large（更大，质量稍好）

**K-quant 的智能之处**：不是所有层都同等重要。K-quant 会用更多的 bit 来编码重要的层（如注意力层的 Q/K/V 投影），用更少的 bit 编码不重要的层（如 FFN 的某些维度），从而在同等文件大小下获得更好的质量

---

## 推理显存计算

**用途**：选择量化前要确认 VRAM 是否够用，公式如下

```
总 VRAM 占用 = 模型权重 + KV Cache + 其他开销

模型权重 = 文件大小 × 1.05（加载开销）
KV Cache ≈ 2 × 层数 × 隐藏维度 × 上下文长度 × 2字节/参数
         ≈ 每 token 约 1-2 MB（9B 模型）

示例（Q5_K_M，32K 上下文）：
  模型权重：6.5 GB × 1.05 ≈ 6.8 GB
  KV Cache：32K × 1.5 MB ≈ 48 MB
  其他开销：~0.3 GB
  总计：~7.2 GB  ✅（16GB VRAM 余量充足）

示例（BF16，32K 上下文）：
  模型权重：18 GB × 1.05 ≈ 18.9 GB
  → 超过 16GB VRAM ❌
  → 只能部分 GPU 卸载（如卸载 60% 层，约 11GB 占用，剩余在 CPU）
  → 但跨 PCIe 传输会大幅降低速度
```

---

# 6. 常用参数调优

## 关键推理参数说明

| 参数 | 默认值 | 作用 | 调优建议 |
|:----|:-----:|:-----|:---------|
| **temperature** | 0.7 | 生成的随机性。0=确定性输出，2=非常随机 | 创意写作 0.8-1.0，代码/数学 0.1-0.3，通用 0.6-0.8 |
| **top_p** | 0.9 | 核心采样（Nucleus Sampling），只从累积概率 p 的 token 中采样 | 通常保持 0.9，不需要频繁改动 |
| **top_k** | 40 | 只从概率最高的 K 个 token 中采样 | 通常保持 40，兼顾多样性和质量 |
| **repeat_penalty** | 1.1 | 重复惩罚，>1 降低重复生成的概率 | 1.0=无惩罚，1.1=轻度，1.2=严格（防止重复但可能破坏连贯性）|
| **num_ctx** | 2048-32768 | 上下文窗口长度 | 越大模型"记忆"越多，但越吃显存。32K 起步，如需分析长文档可逐步增加到 128K-1M |
| **num_predict** | 无限制 | 每次生成的最大 token 数 | 短回答 256-512，长文章 2048-4096 |

## 不同任务推荐参数

```yaml
代码生成：
  temperature: 0.2
  top_p: 0.95
  repeat_penalty: 1.0    # 代码需要精确，不需惩罚重复

创意写作：
  temperature: 0.9
  top_p: 0.95
  top_k: 60
  repeat_penalty: 1.1    # 适度防止重复

分析/推理：
  temperature: 0.3
  top_p: 0.9
  top_k: 40
  repeat_penalty: 1.0

翻译：
  temperature: 0.1
  top_p: 0.9
  repeat_penalty: 1.0
```

---

# 7. 性能优化

## 7.1 GPU 加速确认

```bash
# 推理时观察 GPU 使用率
watch -n 1 nvidia-smi

# 期望看到：
# GPU-Util: 90-100%（说明 GPU 正在全速推理）
# Memory: ~10GB used（Q5_K_M，32K context）

# 如果 GPU-Util < 50%（说明瓶颈在 CPU/数据传输）
# 检查 -ngl 参数是否设为 99
```

## 7.2 上下文长度与显存的关系

```
注意：显存占用与上下文长度成线性关系

RTX 5060 Ti 16GB，Q5_K_M 量化（权重~7GB）：

上下文长度     KV Cache     总显存占用    是否可行
━━━━━━━━━     ━━━━━━━━━    ━━━━━━━━━    ━━━━━━━━
4K            ~0.05GB      ~7.5 GB     ✅ 绰绰有余
32K           ~0.4GB       ~8 GB       ✅ 推荐
128K          ~1.6GB       ~9 GB       ✅ 可行
512K          ~6.4GB       ~13.5 GB    ⚠️ 很紧
1M            ~12.8GB      ~20 GB      ❌ 超过16GB

如果想跑 1M 上下文：
- 使用更小的量化 Q4_K_M（权重~5.5GB）
- 使用 Flash Attention（减少 KV Cache 显存）
- 或减半上下文到 512K
```

## 7.3 KV Cache 量化

**用途**：KV Cache 是自注意力机制中的缓存，推理时存储已计算的 Key/Value 值。量化 KV Cache 可降低显存占用，但略微降低质量

```bash
# Ollama 中启用 KV Cache 量化
# 在 Modelfile 中添加：
PARAMETER num-ctx 32768
PARAMETER kqv 8             # KV Cache 量化为 8bit

# llama.cpp 中
./llama-cli \
  -m model.gguf \
  -ngl 99 \
  --cache-type-k q8_0 \     # K Cache 用 8bit
  --cache-type-v q8_0 \     # V Cache 用 8bit
  -c 131072                 # 可以在同等显存下扩大4倍上下文
```

---

# 8. 故障排查

## 常见问题及解决方案

| 问题 | 原因 | 解决方法 |
|:----|:-----|:---------|
| **CUDA error: out of memory** | 显存不足 | 换更小的量化（Q4_K_M），或减小上下文长度（-c 8192），或减小 GPU 卸载层数（-ngl 60） |
| **GGUF 加载后崩溃** | GGUF 文件损坏或不兼容 | 重新下载，检查 sha256，更新 llama.cpp/Ollama 版本 |
| **推理速度极慢（<5 token/s）** | 未使用 GPU 加速 | 检查 nvidia-smi，正确定义 -ngl 99，确认驱动正常 |
| **curl 下载超时** | 代理未配置或端口不对 | export HTTPS_PROXY=http://127.0.0.1:7897，再试 |
| **Ollama install 超时** | 代理未覆盖安装脚本 | 手动下载安装包，或用 `sudo systemctl` 方式安装 |
| **ollama create 失败：file not found** | GGUF 路径不对 | 检查 Modelfile 中的路径是绝对路径还是相对于 Modelfile 的相对路径 |
| **模型输出乱码** | tokenizer 不匹配 | GGUF 文件已内嵌 tokenizer，通常不会出现。检查模型是否下错（如选了不兼容的变体） |
| **API 连接拒绝（Connection refused）** | 服务没启动 | `ps aux | grep ollama` 确认进程在运行，`ollama serve` 启动服务 |
| **中文输出质量差** | 模型主要在英文上微调 | 用英文 prompt 效果更好。中文场景建议换 Qwen3.5-9B 原版 |
| **WSL 关机后 Ollama 没了** | WSL 重启后进程不持久 | 每次重启 WSL 后执行 `ollama serve &`，或配置 WSL 的 /etc/wsl.conf 开启 systemd |

---

# 附录：完整工作流速查

## 快速部署（从零到运行）

```bash
# 1. 配置代理
export HTTPS_PROXY=http://127.0.0.1:7897

# 2. 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 3. 下载模型（Q5_K_M 量化）
mkdir -p ~/models && cd ~/models
curl -L -o qwythos-9b-Q5_K_M.gguf \
  https://huggingface.co/empero-ai/Qwythos-9B-Claude-Mythos-5-1M-GGUF/resolve/main/Qwythos-9B-Claude-Mythos-5-1M-MTP-Q5_K_M.gguf

# 4. 创建 Modelfile 并导入
cat > Modelfile << 'EOF'
FROM ./qwythos-9b-Q5_K_M.gguf
PARAMETER num_ctx 32768
EOF
ollama create qwythos-9b -f Modelfile

# 5. 运行
ollama run qwythos-9b
```

## 文件结构

```bash
~/models/
├── qwythos-9b-Q5_K_M.gguf    # 模型文件（手动下载）
└── Modelfile                  # Ollama 配置（手动创建）

# Ollama 存储路径（自动管理）：
~/.ollama/
├── models/
│   └── blobs/                 # GGUF 文件索引
└── history                    # 对话历史
```

查看当前状态
bash

# 查看 ollama 进程是否在运行
ps aux | grep ollama
# 或检查 API 是否响应
curl -s http://localhost:11434/api/tags
关闭
bash

# 方式一：杀掉进程
pkill ollama

# 方式二：找到进程ID后杀掉
ps aux | grep ollama
kill <进程ID>
启动
bash

# 前台启动（占用终端，Ctrl+C 停止）
ollama serve

# 后台启动（不占终端）
nohup ollama serve > /tmp/ollama.log 2>&1 &

# 验证启动成功
curl http://localhost:11434/api/tags
# 应返回 {"models":[...]} 或 {"models":[]}
如果你用的是 ~/.local/bin/ollama（手动部署版）
bash

# 启动
~/.local/bin/ollama serve &

# 关闭
pkill -f ollama