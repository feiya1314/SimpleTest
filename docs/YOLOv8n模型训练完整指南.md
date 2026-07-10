# YOLOv8n 模型训练完整指南

---

## 目录

1. [YOLOv8 基座功能](#1-yolov8-基座功能)
2. [为什么需要训练？](#2-为什么需要训练)
3. [YOLOv8n 模型简介](#3-yolov8n-模型简介)
4. [训练详细流程（分步说明）](#4-训练详细流程分步说明)
5. [训练参数详解](#5-训练参数详解)
6. [训练输出文件详解](#6-训练输出文件详解)
7. [常见问题与解决方案](#7-常见问题与解决方案)

---

## 1. YOLOv8 基座功能

YOLOv8（2023年1月发布）是 Ultralytics 推出的目标检测框架，其核心模型 **YOLOv8n（nano版）** 是最轻量级的变体。YOLOv8 支持以下 **5 大任务**：

| 任务 | 模式标识 | 功能描述 |
|------|----------|----------|
| **检测 (Detect)** | `detect` | 目标检测 — 识别并定位图像中的物体，输出边界框 + 类别 |
| **分割 (Segment)** | `segment` | 实例分割 — 像素级分割每个目标实例 |
| **分类 (Classify)** | `classify` | 图像分类 — 预测整张图像的类别标签 |
| **姿态估计 (Pose)** | `pose` | 关键点检测 — 识别人体/物体姿态关键点 |
| **OBB (定向框)** | `obb` | 旋转边界框 — 适用于卫星/医学图像中带角度目标的检测 |

**支持的 6 种运行模式：** `train`（训练）、`val`（验证）、`predict`（预测）、`export`（导出）、`track`（追踪）、`benchmark`（基准测试）

### YOLOv8n 的技术特点

- **Anchor-Free 架构** — 不再使用预定义的锚框，直接预测目标的中心点和尺寸
- **C2f 模块** — 比 YOLOv5 的 C3 模块更高效的特征提取
- **Decoupled Head** — 分类头和回归头分离，提升精度
- **Mosaic 数据增强** — 训练时将 4 张图拼成 1 张，提升鲁棒性
- **自动混合精度 (AMP)** — 减少显存占用，加速训练

### YOLOv8 模型系列对比

| 模型 | 参数量 | 尺寸 (MB) | mAPval 50-95 | CPU ONNX 速度 |
|------|--------|-----------|-------------|--------------|
| **YOLOv8n** | **3.2M** | **6.2 MB** | **37.3%** | **80.4 ms** |
| YOLOv8s | 11.2M | 21.5 MB | 44.9% | 128.4 ms |
| YOLOv8m | 25.9M | 49.7 MB | 50.2% | 234.7 ms |
| YOLOv8l | 43.7M | 83.7 MB | 52.9% | 375.2 ms |
| YOLOv8x | 68.2M | 130.5 MB | 53.9% | 479.1 ms |

> YOLOv8n 的 "n" 代表 **nano**，是最小、最快的版本，适合边缘设备、移动端和实时场景。

---

## 2. 为什么需要训练？

### 预训练模型 vs 自定义训练

| 场景 | 直接用预训练模型 | 需要自定义训练 |
|------|-----------------|--------------|
| 检测通用物体（人、车、猫、狗等 COCO 类别） | ✅ 直接可用 | ❌ 不需要 |
| 检测特定物体（缺陷、品牌logo、医学影像） | ❌ 无法识别 | ✅ **必须训练** |
| 改进特定场景精度（仓库、工地、水下） | ❌ 精度不足 | ✅ **需要微调** |
| 需要更高 NP 的推理速度 | 模型已固定 | ✅ **可蒸馏/剪枝** |

### 训练的三种场景

1. **从零训练（Scratch）** — 使用 `pretrained=False`，适用于数据量极大（>10万张）且与预训练分布差异很大的场景
2. **迁移学习 / 微调（Transfer Learning / Fine-tuning）** — 使用预训练权重，冻结部分层，在自定义数据集上继续训练。这是 **最推荐的方式**，数据量少也能取得好效果
3. **知识蒸馏（Knowledge Distillation）** — 使用大模型（如 YOLOv8x）作为 teacher，指导小的 student 模型（如 YOLOv8n）训练

---

## 3. YOLOv8n 模型简介

```yaml
# yolov8n.yaml 核心结构
# 参数量: ~3.2M (约 YOLOv8s 的 1/3)
# Backbone: 轻量级 CSPDarknet
# Neck: PAN-FPN
# Head: Decoupled Detection Head (Anchor-Free)
```

**适用场景：**
- 边缘设备（树莓派、Jetson Nano）
- 移动端 APP
- 实时视频流处理（30 FPS+）
- 轻量级嵌入式系统

---

## 4. 训练详细流程（分步说明）

### 第零步：环境准备

```bash
# 安装 Ultralytics
pip install ultralytics

# 验证安装
yolo checks
```

### 第一步：准备数据集

数据集需要组织为 YOLO 格式：

```
dataset/
├── images/
│   ├── train/        # 训练图片
│   │   ├── img001.jpg
│   │   ├── img002.jpg
│   │   └── ...
│   └── val/          # 验证图片
│       ├── img101.jpg
│       └── ...
├── labels/
│   ├── train/        # 训练标注（每张图一个 .txt 文件）
│   │   ├── img001.txt   # 格式: class_id x_center y_center width height (归一化到 0~1)
│   │   └── ...
│   └── val/          # 验证标注
│       └── ...
└── dataset.yaml      # 数据集配置文件
```

**`dataset.yaml` 格式示例：**

```yaml
# 数据集配置文件
path: ./dataset          # 数据集根目录
train: images/train      # 训练图片目录（相对于 path，或填写绝对路径）
val: images/val          # 验证图片目录
test: images/test        # 测试图片目录（可选）

# 类别信息
nc: 3                    # 类别数量 (number of classes)
names: ['person', 'car', 'bicycle']  # 类别名称列表
```

> **标注工具推荐：** LabelImg、Label Studio、Roboflow、CVAT

### 第二步：编写训练脚本

**方式 A — CLI 命令行（推荐新手）：**

```bash
yolo detect train data=dataset.yaml model=yolov8n.pt epochs=100 imgsz=640 batch=16 device=0
```

**方式 B — Python API（推荐调参/自动化）：**

```python
from ultralytics import YOLO

# 加载预训练模型
model = YOLO('yolov8n.pt')  # 自动下载 COCO 预训练权重

# 开始训练
results = model.train(
    data='dataset.yaml',
    epochs=100,
    imgsz=640,
    batch=16,
    device=0,          # GPU 0
    workers=8,
    lr0=0.01,
    patience=50,
    save=True,
    project='my_yolo_project',
    name='exp1',
    exist_ok=True,
    amp=True,
    augment=True,
)
```

### 第三步：执行训练 — 训练过程中的内部步骤

以下是每个 epoch 内部执行的具体步骤：

| 步骤编号 | 步骤名称 | 具体内容 |
|---------|---------|---------|
| **3.1** | **数据加载与增强** | 读取 batch 数据，执行 Mosaic、MixUp、HSV 抖动、随机翻转、缩放等数据增强 |
| **3.2** | **前向传播** | 图像通过 Backbone -> Neck -> Head 网络，输出预测结果 |
| **3.3** | **计算损失** | 计算 3 个损失分量：`box_loss`（边界框回归）、`cls_loss`（分类）、`dfl_loss`（分布聚焦损失） |
| **3.4** | **反向传播** | 计算梯度，backward |
| **3.5** | **优化器更新** | 根据优化器（SGD/AdamW）更新权重。包含：warmup -> cosine LR schedule -> weight decay |
| **3.6** | **验证评估** | 每个 epoch 结束后（或按 `val` 频率），在验证集上计算 mAP50、mAP50-95、Precision、Recall |
| **3.7** | **早停检查** | 检查 `patience` 轮内验证指标是否提升，若无提升则停止训练 |
| **3.8** | **日志与保存** | 记录指标到 TensorBoard/CSV，按 `save_period` 保存 checkpoint |

### 第四步：评估训练结果

```python
# 加载训练好的模型
model = YOLO('runs/detect/exp/weights/best.pt')

# 在验证集上评估
metrics = model.val(data='dataset.yaml')
print(f"mAP50: {metrics.box.map50:.4f}")
print(f"mAP50-95: {metrics.box.map:.4f}")
```

### 第五步：推理测试

```python
# 用训练好的模型做预测
model = YOLO('runs/detect/exp/weights/best.pt')
results = model.predict(
    source='test_images/',   # 图片/视频/目录/摄像头
    conf=0.25,               # 置信度阈值
    iou=0.7,                 # NMS IoU 阈值
    save=True,               # 保存结果图片
    save_txt=True,           # 保存标签
)
```

### 第六步：模型导出（部署）

```python
# 导出为不同格式
model.export(format='onnx')        # ONNX (通用)
model.export(format='tensorrt')    # TensorRT (NVIDIA GPU 加速)
model.export(format='openvino')    # OpenVINO (Intel)
model.export(format='tflite')      # TFLite (移动端/边缘)
model.export(format='coreml')      # CoreML (Apple)
```

---

## 5. 训练参数详解

### 核心参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `model` | str | None | 模型文件路径。`.pt`（预训练权重）或 `.yaml`（模型结构配置）|
| `data` | str | None | 数据集配置文件路径（.yaml） |
| `epochs` | int | 100 | 训练轮数。每个 epoch 遍历一次完整的训练集 |
| `time` | float | None | 最大训练时间（小时）。设置了则覆盖 epochs |
| `patience` | int | 100 | 早停 — 验证指标连续 N 轮不提升则停止训练。设为 0 禁用 |
| `batch` | int/float | 16 | 批大小。`batch=16` 固定值；`batch=-1` 自动适配 60% 显存；`batch=0.70` 按显存比例 |

### 图像与数据

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `imgsz` | int | 640 | 训练图像尺寸（正方形缩放到的边长）。越大越慢但细节越好 |
| `rect` | bool | False | 矩形训练 — 最小填充策略，省显存但精度略有影响 |
| `multi_scale` | float | 0.0 | 多尺度训练。如 0.25 则每批次从 0.75x ~ 1.25x 随机缩放 |
| `fraction` | float | 1.0 | 使用数据集的多少比例。1.0=全部，0.5=一半 |
| `cache` | bool | False | 缓存图像到内存 (`True`/`ram`) 或磁盘 (`disk`)，加速数据加载 |

### 优化器与学习率

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `optimizer` | str | 'auto' | 优化器：SGD / Adam / AdamW / Adamax / NAdam / RAdam / RMSProp |
| `lr0` | float | 0.01 | 初始学习率。SGD 推荐 1e-2，Adam 推荐 1e-3 |
| `lrf` | float | 0.01 | 最终学习率 = lr0 × lrf |
| `momentum` | float | 0.937 | SGD 动量 / Adam 的 beta1 |
| `weight_decay` | float | 0.0005 | L2 正则化权重衰减 |
| `cos_lr` | bool | False | 使用余弦衰减学习率调度器 |
| `warmup_epochs` | float | 3.0 | 学习率预热轮数 |
| `warmup_momentum` | float | 0.8 | 预热阶段的动量 |
| `warmup_bias_lr` | float | 0.1 | 预热阶段偏置参数的学习率 |

### 损失权重

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `box` | float | 7.5 | 边界框回归损失权重 |
| `cls` | float | 0.5 | 分类损失权重 |
| `dfl` | float | 1.5 | 分布聚焦损失（DFL）权重，影响边界框边缘回归精度 |
| `cls_pw` | float | 0.0 | 类别加权幂指数 — 处理类别不平衡。0.0=禁用，1.0=完全使用逆频率加权 |

### 硬件与性能

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `device` | int/str/list | None | 计算设备：`device=0`（单卡）、`device=[0,1]`（多卡）、`device='cpu'`、`device=-1`（自动选最空闲卡）|
| `workers` | int | 8 | 数据加载线程数（多 GPU 时为每个 RANK 的线程数） |
| `amp` | bool | True | 自动混合精度 — 半精度训练，省显存、加速 |
| `nbs` | int | 64 | 名义批大小（用于损失归一化）|

### 保存与恢复

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `save` | bool | True | 保存训练 checkpoint 和最终权重 |
| `save_period` | int | -1 | 每 N 轮自动保存一次 checkpoint。-1=禁用 |
| `project` | str | None | 项目目录名 |
| `name` | str | None | 实验名，自动创建子目录 |
| `exist_ok` | bool | False | 允许覆盖已有的 project/name 目录 |
| `resume` | bool | False | 从最后的 checkpoint 恢复训练（自动加载 optimizer 状态和 epoch 计数） |

### 数据增强

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `close_mosaic` | int | 10 | 在最后 N 轮关闭 Mosaic 增强，稳定训练 |
| `single_cls` | bool | False | 将所有类别当作单类训练（适合二分类） |
| `dropout` | float | 0.0 | 随机失活率（分类任务正则化） |

### 高级功能

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `freeze` | int/list | None | 冻结前 N 层或指定层（微调时用） |
| `compile` | bool/str | False | PyTorch 2.x 图编译加速。`True` 或 `'default'`/`'reduce-overhead'`/`'max-autotune-no-cudagraphs'` |
| `distill_model` | str | None | 知识蒸馏 teacher 模型路径 |
| `deterministic` | bool | True | 强制确定性算法（影响性能但可复现） |
| `seed` | int | 0 | 随机种子 |
| `max_det` | int | 300 | 验证阶段保留的最大检测框数 |

---

## 6. 训练输出文件详解

训练完成后，输出目录结构如下（默认 `runs/detect/train/`）：

```
runs/detect/train/               # 或 project/name 目录
├── weights/
│   ├── best.pt                  # 🔥 验证集 mAP 最高的模型权重（最常用！）
│   └── last.pt                  # 最后一个 epoch 的模型权重（用于恢复训练）
├── args.yaml                    # 本次训练所有参数配置快照
├── hyp.yaml                     # 超参数配置（与 args.yaml 基本一致）
├── results.csv                  # 📊 所有 epoch 的详细指标表格
├── results.png                  # 📈 训练过程可视化图（loss 曲线 + 指标曲线）
├── confusion_matrix.png         # 混淆矩阵（验证集）
├── confusion_matrix_normalized.png  # 归一化混淆矩阵
├── F1_curve.png                 # F1 Score vs 置信度曲线
├── P_curve.png                  # Precision vs 置信度曲线
├── R_curve.png                  # Recall vs 置信度曲线
├── PR_curve.png                 # 📈 Precision-Recall 曲线（核心评估图）
├── labels.jpg                   # 训练标注可视化
├── labels_correlogram.jpg       # 标注相关性图
├── train_batch*.jpg             # 训练批次样本可视化（增强后的效果）
├── val_batch*.jpg               # 验证批次预测结果可视化
├── events.out.tfevents.*        # TensorBoard 事件文件
└── ...其他日志文件
```

### 关键文件详解

#### `weights/best.pt`
- **最重要**的产出文件
- 包含：模型权重、optimizer 状态、epoch 计数等
- 大小：约 6~7 MB（YOLOv8n）
- 推理/部署时使用此文件

#### `weights/last.pt`
- 最后一个 epoch 的完全状态
- 用于突然中断后恢复训练：`model = YOLO('last.pt'); model.resume()`

#### `results.csv`

列包含（按顺序）：
| 列 | 含义 |
|----|------|
| epoch | 轮次 |
| train/box_loss | 训练集边界框损失 |
| train/cls_loss | 训练集分类损失 |
| train/dfl_loss | 训练集 DFL 损失 |
| metrics/precision | 验证集精确率 |
| metrics/recall | 验证集召回率 |
| metrics/mAP50 | 验证集 mAP@0.5 |
| metrics/mAP50-95 | 验证集 mAP@0.5:0.95（**核心指标**）|
| val/box_loss | 验证集边界框损失 |
| val/cls_loss | 验证集分类损失 |
| val/dfl_loss | 验证集 DFL 损失 |
| lr/pg0 | 学习率变化 |

#### `results.png`
一张组合图，包含 4 个子图：
- **左上**：训练 loss（box + cls + dfl）曲线 — 应持续下降
- **右上**：验证 loss 曲线 — 应与训练 loss 接近，若差距很大则过拟合
- **左下**：mAP50 和 mAP50-95 — 应持续上升后趋于平稳
- **右下**：Precision、Recall — 观察能否平衡

#### `PR_curve.png`
- X 轴：Recall，Y 轴：Precision
- AP = 曲线下面积。曲线越靠右上越好
- 每条曲线代表一个类别 + 所有类别的平均

---

## 7. 常见问题与解决方案

### 7.1 显存不足（CUDA Out of Memory）

| 解决方式 | 说明 |
|---------|------|
| 减小 `batch` | 从 16 -> 8 -> 4 -> 2 -> 1 逐步降低 |
| 减小 `imgsz` | 从 640 -> 416 -> 320 |
| 开启 `amp=True` | 混合精度训练，省约 40% 显存 |
| 使用 `device='cpu'` | 用 CPU 训练（极慢，仅测试用） |
| 使用 `cache=False` | 减少显存占用 |

### 7.2 模型过拟合

**现象：** 训练 loss 持续下降，但验证 loss 上升/mAP 停止提升

| 解决方式 | 说明 |
|---------|------|
| 增加数据量 + 数据增强 | 收集更多数据或使用更强的增强 |
| 降低 `epochs` 或提高 `patience` | 早停 |
| 增加 `weight_decay` | 更强的 L2 正则化 |
| 使用 `dropout` | 添加随机失活 |
| 使用更小的模型 | YOLOv8n -> YOLOv8n（已最小），或减少输入尺寸 |

### 7.3 模型欠拟合（训练 loss 不降）

| 解决方式 | 说明 |
|---------|------|
| 增加 `epochs` | 训练不足 |
| 提高 `lr0` | 学习率太小 |
| 使用更强的优化器 | 如 AdamW 代替 SGD |
| 确保数据标注正确 | 检查 labels.jpg 可视化 |
| 增加模型复杂度 | 换 YOLOv8s/m |

### 7.4 训练速度慢

| 解决方式 | 说明 |
|---------|------|
| 增加 `workers` | 提高数据加载并行度（常见 8~16）|
| 开启 `amp=True` | 混合精度 |
| 使用 `cache=True` | 缓存图像到内存 |
| 使用 GPU | `device=0` 或多卡 `device=[0,1,2,3]`|
| 减小 `imgsz` | 降低分辨率 |

### 7.5 mAP 不理想

| 问题 | 可能原因 | 解决方向 |
|------|---------|---------|
| mAP50 低但 mAP50-95 尚可 | 定位不准 | 增加 `box` 损失权重，检查标注框精度 |
| mAP50-95 明显低于 mAP50 | 回归精度差 | 调整 `dfl` 权重，调整 IoU 阈值 |
| 小目标检测差 | 特征图中信息不足 | 增加 `imgsz`，使用大模型，在数据集层面切割高分辨率图像 |
| 某个类别特别差 | 类别不平衡 | 增加该类数据，使用 `cls_pw` 类别加权 |
| Precision 低 | 误报多 | 提高 `conf` 阈值，检查背景噪声 |
| Recall 低 | 漏检多 | 降低 `conf` 阈值，增加数据多样性 |

### 7.6 训练不收敛

```bash
# 检查学习率是否合适 — 常见经验值
# SGD:  lr0=0.01  (batch=16 时)
# Adam: lr0=0.001 (batch=16 时)
# batch 翻倍，lr0 可相应增大 √batch 倍
```

### 7.7 训练中断后恢复

```python
# 方法一：自动恢复（推荐）
model = YOLO('runs/detect/train/weights/last.pt')
results = model.train(resume=True)

# 方法二：手动指定恢复目录
results = model.train(resume=True, project='runs/detect', name='train')
```

### 7.8 常见报错

| 错误 | 原因 | 解决方法 |
|------|------|---------|
| `No labels found in ...` | 标注路径不对 | 检查 `dataset.yaml` 中 `train`/`val` 路径是否正确 |
| `CUDA error: out of memory` | 显存不足 | 参考 7.1 |
| `AssertionError: Class number ...` | 数据集中 nc 与模型不匹配 | 检查数据集的类别数是否与模型一致 |
| `FileNotFoundError: ...yaml` | 数据集配置不存在 | 检查路径 |
| `RuntimeError: No such operator` | ONNX 导出兼容问题 | 升级 `onnxruntime` 或使用 `opset=12` |

---

## 快速上手实战命令

```bash
# 1. 安装
pip install ultralytics

# 2. 用 COCO 预训练权重在自定义数据集上微调
yolo detect train \
  data=custom_dataset.yaml \
  model=yolov8n.pt \
  epochs=100 \
  imgsz=640 \
  batch=16 \
  device=0 \
  project=my_project \
  name=custom_training \
  amp=True

# 3. 查看训练过程
tensorboard --logdir my_project/custom_training

# 4. 评估
yolo detect val data=custom_dataset.yaml model=my_project/custom_training/weights/best.pt

# 5. 推理
yolo predict model=my_project/custom_training/weights/best.pt source=test.jpg save=True

# 6. 导出
yolo export model=my_project/custom_training/weights/best.pt format=onnx
```
