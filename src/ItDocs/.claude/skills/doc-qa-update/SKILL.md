---
name: doc-qa-update
description: Only use when the user explicitly types /doc-qa-update. Updates this project's interview Q&A Markdown docs.
disable-model-invocation: true
---

你正在当前项目的 IT 技术面试问答笔记库中更新文档。

执行流程：
1. 先不要管当前的文档，我会和你讨论问题，然后我会告诉何时更新
2. 根据当前的所有讨论，你需要提取问题和回答，更新文档
3. 更新时需要遵守项目 `CLAUDE.md` 文档格式

注意事项:
1. 如果用户提供了参考信息：
   - 必须分析参考信息是否准确、完整；
   - 保留关键内容、结论
2. 不删除已有文件，除非用户明确要求。
