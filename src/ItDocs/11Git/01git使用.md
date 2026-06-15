# 1. merge是什么，怎么使用，适用场景

**merge 是把两个分支的修改合并到一起，会产生一个新的合并提交（merge commit）**，保留两条分支的完整历史。

使用方式：

- `git checkout master`：切到目标分支
- `git merge feature-login`：把 feature-login 分支合并进来

举个例子，假设 master 上有 A、B 两个提交，你从 B 切了 feature 分支，开发了 C、D 两个提交，期间 master 上别人又合了 E。这时执行 `git merge feature`，会产生一个新的 M 提交，它有两个父节点（E 和 D），历史变成一个菱形结构：

```
A---B---E---M  (master)
     \     /
      C---D    (feature)
```

适用场景：

- **公共分支合并**：如 feature 合到 develop、develop 合到 master，必须用 merge 保留完整历史
- **多人协作分支**：保留每个人的提交记录便于追溯
- **需要回滚整个特性**：merge commit 可以通过 `git revert -m` 一键回滚整个特性

# 2. rebase是什么，怎么使用，适用场景

**rebase 叫"变基"，把当前分支的提交在另一条分支的最新位置上重新逐个"播放"一遍**，效果是历史变成一条直线，没有合并提交。

使用方式：

- `git checkout feature-login`：切到要变基的分支
- `git rebase master`：把当前分支变基到 master 最新位置

接着上面的例子，feature 分支上执行 `git rebase master`，Git 会把 C、D 两个提交先暂存，把分支基底从 B 挪到 E，再把 C、D 重新打成 C'、D' 提交在 E 后面：

```
A---B---E---C'---D'  (feature)
```

注意 C' 和 D' 是**全新的提交**，commit hash 跟原来的 C、D 不一样。

适用场景：

- **本地分支同步主干最新代码**：保持线性历史，看 log 一目了然
- **合并前整理提交**：用 `git rebase -i` 交互式合并、改写、删除提交
- **合 PR 前清理临时提交**：把 "fix typo"、"再改一下" 这类碎提交合掉

# 3. rebase把commit合并

**使用** `git rebase -i` 交互式 rebase，这是 rebase 最常用的功能之一。

操作方式：`git rebase -i HEAD~3` 表示对最近 3 个提交进行交互式变基，会弹出编辑器：

```
pick abc123 实现登录接口
pick def456 修复登录bug
pick ghi789 补充登录单测

# Commands:
# p, pick = 使用该commit
# s, squash = 将该commit合并到前一个commit，保留提交信息
# f, fixup = 类似squash，但丢弃该commit的提交信息
# r, reword = 使用该commit，但修改提交信息
# d, drop = 删除该commit
```

把后两行的 `pick` 改成 `squash` 或 `s`，保存退出后三个提交就合并成一个。

# 4. 什么情况下合并commit

合并 commit 主要是为了让历史清晰、便于代码审查和回滚。常见场景：

- **PR 提交前清理**：开发过程中可能有 "WIP"、"fix"、"再调一下" 这种碎提交，合成一个有完整意义的提交再提 PR
- **修复同一个 bug 的多次尝试**：第一次没修对，又改了几次，最终合成一个 "修复 xxx 问题" 的提交
- **拆分功能时的辅助提交**：比如先重构一个工具类，再用它实现功能，重构和实现可以根据是否独立有意义决定是否合并
- **review 后的修改**：根据 review 意见做的多次小修改，合并到原提交里，保持每个 commit 是一个完整逻辑单元

原则是 **每个 commit 应该是一个原子的、可独立理解和回滚的修改单元**。

# 5. commit应该是一个原子的、可独立理解和回滚的修改单元怎么理解

拆解三个关键词：

- **原子的**：一个 commit 只做一件事，不把多个不相关的修改混在一起
- **可独立理解**：不依赖前后 commit，看 commit message 和 diff 就能明白这个提交在做什么、为什么做
- **可独立回滚**：单独 `git revert` 这个 commit 不会让代码处于损坏状态

反例（坏 commit）：

```
commit message: "改了一些东西"
diff:
  - 修复了登录接口的 NPE
    - 顺手重构了 StringUtils 工具类
    - 把数据库连接池从 10 调到 20
    - 删了一段注释掉的旧代码
```

这个 commit 里 4 件事杂糅，想单独回滚"连接池调整"做不到，bisect 定位问题也很困难。

正例（好 commit）：

```
commit 1: fix(login): 修复用户名为空时的 NPE
commit 2: refactor(common): 简化 StringUtils.isBlank 实现
commit 3: chore(db): 数据库连接池从 10 调整到 20
commit 4: style: 删除已废弃的旧版登录代码
```

实际价值：

- `git bisect` 二分定位 bug：每个 commit 是独立单元，能精确定位到引入问题的那个提交
- `git revert` 回滚干净：线上出问题，可以只回滚有问题的那个 commit
- `git cherry-pick` 灵活挑选：紧急修复可以单独挑出来合到 release 分支
- **Code Review 高效**：reviewer 一次只看一个逻辑点，不会被无关修改干扰

# 6. 一个合并到master的commit最好是什么，有什么标准

**最好是一个有明确业务含义、可独立部署的最小变更单元**，不一定要是完整的端到端功能。

判断标准（六条硬性要求）：

- **可编译可测试**：合入后 master 能正常构建，CI 全绿
- **不破坏现有功能**：现有用例继续通过，不引入回归
- **逻辑自洽**：不会留下半截代码（比如调用了一个还没实现的方法）
- **可独立回滚**：单独 revert 不会引入新问题
- **提交信息清晰**：遵循 Conventional Commits 规范，说明做了什么、为什么做
- **大小适中**：一般几十到几百行，超过 500 行就考虑拆分

举例对比：

| 类型 | 例子 | 是否合格 |
| --- | --- | --- |
| 完整功能 | feat: 实现手机验证码登录 | 合格 |
| 子任务 | feat: 增加 sms_code 表和 DAO 层 | 合格（不影响线上） |
| 修复 | fix: 解决登录接口高并发下的 NPE | 合格 |
| 半截代码 | feat: 增加登录接口（service 层还没实现） | 不合格 |
| 杂烩 | "改了一些东西"（混合 5 个无关修改） | 不合格 |
| 巨型 PR | 一次性 3000 行重构整个用户模块 | 不合格（应拆分） |

实际工作中的经验：**一个 PR 对应一个 commit（用 squash merge）、一个 PR 对应一个需求卡片或 bug 单**，这是大多数大厂的标准做法。

# 7. 为什么不建议在master上rebase

核心原因是 **rebase 会重写提交历史，产生新的 commit hash，破坏所有人本地仓库的一致性**。

具体危害举例：你的 master 上有 A、B、C 三个提交，团队 10 个人本地都拉过这个 master。这时你 rebase 重写了 B、C 变成 B'、C'，强推到远端。

- 其他 9 个人下次拉代码时，本地的 B、C 跟远端的 B'、C' 冲突
- 他们如果直接 pull，会产生重复提交（B、C、B'、C' 都在）
- 如果用 force pull 或 reset，会丢掉自己基于 B、C 做的本地工作
- **CI/CD 流水线**可能因为 commit hash 变了而异常

所以铁律是：**已经推到公共分支、其他人可能基于它工作的提交，绝不能 rebase 重写**。

# 8. rebase适合在什么分支使用

**只在自己独占的、没推给别人用的分支上使用 rebase**。

适合 rebase 的场景：

- **本地未推送的提交**：还没 push，怎么改都行
- **个人 feature 分支**：只有自己开发，没人基于它拉分支
- **PR 分支同步主干**：在合并前 rebase master，保持线性
- **私有的临时分支**：试验性分支、本地实验

不适合 rebase 的场景：

- **master、develop 等公共主干分支**
- **多人协作的特性分支**
- **已经被别人 checkout 过、基于它开发的分支**
- **已发布的 release 分支**

判断标准很简单：**如果别人可能基于这条分支工作，就不要 rebase**。

# 9. 本地dev分支需要rebase吗

**取决于 dev 分支是个人的还是团队共享的**。

如果是 **个人本地 feature 分支**，建议定期 rebase master：

- 及时同步主干最新代码，避免最后合并时有大量冲突
- 冲突分散解决，每次 rebase 只处理一小段时间的差异
- 提 PR 时历史是线性的，review 更直观

举例：你开发一个功能花了 3 天，master 上别人合了 5 个 PR。如果不 rebase，最后合并时一次性面对所有冲突；定期 rebase 就是每天处理一点。

如果是 **团队共享的 dev 分支**（多个开发者都往上提交），就不要 rebase，只能用 merge，原因同第 7 题。

# 10. 远端master一般只merge吗

**是的，远端 master/main 这类主干分支，原则上只接受 merge，禁止 rebase 和 force push**。

原因：

- master 是所有人的基线，提交一旦推上去，就是不可变的历史
- CI/CD、Tag、发布版本都基于 master 的 commit hash，变了会导致追溯混乱
- 多人协作的安全底线，任何人都不能改写公共历史

具体策略上，业界主流有三种 merge 方式：

- **merge commit**（默认）：保留完整分支结构，看得到 feature 的开发过程
- **squash merge**：把 feature 上所有提交压成一个提交合到 master，master 历史最干净（GitHub Flow 常用）
- **rebase merge**：把 feature 的提交逐个 rebase 到 master 末尾，没有 merge commit 但保留每个提交

实际中用 squash merge 的最多，因为 master 历史最清晰，每个提交对应一个 PR/特性。

# 11. master上merge必须是一个完整功能吗

**不一定要是完整的端到端功能，但必须是"可独立合入"的修改单元**。

可独立合入的标准：

- **不破坏 master**：合入后编译通过、测试通过、可部署
- **逻辑自洽**：不会留下半截代码影响其他开发
- **可独立回滚**：单独 revert 不会引入新问题

举例说明：

- 一个完整的"用户登录功能"可以合入 master
- 但"登录功能的数据库表结构 + DAO 层"也可以单独合入，因为它不影响线上功能
- 大特性可以拆成多个 PR：先建表、再加接口、再接 UI，每个都能独立合

这就是 **"小步快跑"** 的实践，配合 **特性开关**（feature flag），可以把未完成的功能合到 master 但对用户隐藏，避免长期分支带来的合并地狱。

# 12. merge到主分支的内容策略有哪些

按"合并什么内容"来分，业界主要有三种策略：**完整功能合并、分阶段增量合并、特性开关下的持续合并**。

**策略一：完整功能合并（Feature Branch）**：

- 做法：feature 分支上把整个功能开发完、测试通过，再一次性 merge 到 master
- 例子：开发"手机验证码登录"，建表 → DAO → Service → Controller → 单测 → 联调全部完成，最后一个 PR 合到 master
- 优点：master 上每次合并都是一个端到端可用的功能
- 缺点：
  - 分支生命周期长（几天到几周），跟 master 容易有大量冲突
  - 大 PR 难以 review，质量难保证
  - 多个长期分支并行时合并地狱
- 适用：小特性（1\~3 天能完成）、有明确边界的独立功能

**策略二：分阶段增量合并（Incremental Merge）**：

- 做法：把大特性拆成多个小阶段，每个阶段独立可合并、不破坏 master
- 例子：开发"商品秒杀"系统拆成 4 个 PR
  - PR1：增加 seckill 表结构（DDL）
  - PR2：增加 SeckillService 骨架和单测（不暴露接口）
  - PR3：实现库存扣减逻辑（仍未暴露给前端）
  - PR4：增加 Controller 和前端入口（功能上线）
- 优点：
  - 每个 PR 小（几十到几百行），review 高效
  - 风险分散，前几个阶段出问题影响小
  - 团队成员可以并行接力开发
- 缺点：需要前期设计好拆分方案，避免后续阶段推翻前面
- 适用：中大型特性、跨模块改动、重构类工作

**策略三：特性开关下的持续合并（Trunk-Based + Feature Flag）**：

- 做法：代码每天甚至每小时合到 master，未完成的功能用特性开关包起来，对用户隐藏
- 例子：新版搜索功能开发周期 2 周
  - 第 1 天：合入开关框架，`isEnabled("new-search")` 默认 false
  - 第 2\~13 天：每天合入新代码，都包在 `if (isEnabled)` 分支里，老用户走旧逻辑
  - 第 14 天：内部测试 → 灰度 1% → 10% → 100%
  - 第 20 天：删除开关和旧代码
- 优点：
  - 永远不存在长期分支，不会有合并地狱
  - 半成品代码也能合，主干持续集成
  - 支持灰度发布、A/B 测试、紧急降级
- 缺点：依赖完善的特性开关基础设施和测试覆盖
- 适用：互联网产品、持续部署、Trunk-Based Development

三种策略对比：

| 策略 | 分支生命周期 | PR 大小 | 上线节奏 | 适用规模 |
| --- | --- | --- | --- | --- |
| 完整功能合并 | 几天\~几周 | 大 | 合并即上线 | 小特性 |
| 分阶段增量 | 单阶段 1\~2 天 | 中 | 最后一阶段上线 | 中大型特性 |
| 特性开关持续合并 | 几小时\~1 天 | 小 | 合并与上线解耦 | 任意规模 |

实际选择建议：

- **业务紧、人少、特性独立**：完整功能合并最简单
- **特性大、跨模块、有依赖**：分阶段增量
- **互联网产品、追求快速迭代**：特性开关 + 持续合并是大厂主流

补充说明：这里讲的是"合什么内容"的策略；Git 命令层面具体用 `merge commit`、`squash merge` 还是 `rebase merge`，是另一个维度的选择，详见第 10 题。

# 13. master分支和release分支有什么区别

两者职责不同，**master 是开发主干，release 是版本快照**。

核心区别：

| 维度 | master | release |
| --- | --- | --- |
| 用途 | 开发主线，最新稳定代码 | 准备发布的版本快照 |
| 生命周期 | 长期存在 | 随版本生命周期，发布完可归档 |
| 接受的修改 | 新功能、bug 修复、重构 | 只接受 bug 修复，不接新功能 |
| 部署环境 | 测试/预发布 | 生产环境 |
| 命名 | master / main | release/v1.2.0、release-20260615 |
| 是否打 tag | 一般不打 | 发布前打 tag（v1.2.0） |

典型工作流举例：

1. master 持续合并新功能，到了版本计划点（如 v2.0）
2. 从 master 切出 `release/v2.0` 分支，进入测试阶段
3. master 继续接受 v2.1 的新功能开发
4. 测试中发现 bug：在 `release/v2.0` 上修复，然后 cherry-pick 回 master（避免 master 同样的 bug）
5. release 测试通过，打 tag `v2.0.0`，部署生产
6. 上线后又发现紧急 bug：从 tag 拉 hotfix 分支修复，再 cherry-pick 回 master 和 release

为什么需要 release 分支：

- **隔离稳定代码**：release 进入"冻结期"，只修 bug 不加功能，保证发布质量
- **支持并行开发**：测试 release 期间，master 上可以继续开发下一版本
- **便于版本追溯**：release 分支对应一个明确的版本，出问题能快速定位

注意：**主干开发模式（Trunk-Based）下通常没有 release 分支**，直接从 master 打 tag 发布。是否需要 release 分支取决于团队的发布节奏和环境复杂度。

# 14. hotfix 修复后为什么用 cherry-pick 回 master 和 release，而不是 merge

先澄清一个常见误解：**merge 不会把老代码拉回来，最终代码内容和 cherry-pick 是一样的，冲突可能性也基本相同**。

详细分析这个场景：假设线上跑着 v2.0（对应 tag `v2.0.0`），master 上已经合了几十个 PR 在开发 v2.1。这时 v2.0 出紧急 bug，从 tag 拉 hotfix 分支：

```
A---B---C---D---E---F---G  (master, v2.1开发中)
        |
        v2.0.0 tag
        |
        H---I  (hotfix/v2.0.1, 修了2个commit)
```

**merge 的真实行为**：

- 三方合并算法找共同祖先 C，比较 master（G 相对 C 的 diff）和 hotfix（I 相对 C 的 diff），合并这两组 diff
- 结果代码内容 = G 的内容 + (H、I 相对 C 的 diff)
- **本质上和 cherry-pick H、I 到 G 上得到的代码是一样的**，不会引入老代码
- 冲突可能性也相同：H、I 改的代码行如果 D~G 也改过，两种方式都会冲突
  - merge：一次性出冲突，整体解决
  - cherry-pick：H 先冲突解决，再 I 冲突解决（逐个 commit 应用）

**那两者的真正区别在哪**：

- **历史形态不同**：
  - merge 会留下 merge commit + hotfix 完整分支结构在 master 历史里
  - cherry-pick 在 master 上是线性的 H'、I'，hotfix 分支与 master 历史不连接

```
merge 后的 master 历史：
A---B---C---D---E---F---G---M  (master)
        |                  /
        H------------------I    (hotfix 分支结构永远留在历史中)

cherry-pick 后的 master 历史：
A---B---C---D---E---F---G---H'---I'  (master，线性干净)
        |
        H---I  (hotfix 分支独立，可删)
```

- **后续分支行为不同**：merge 过后 Git 知道 H、I 已合并；cherry-pick 因为生成新 hash，后续如果再有分支整体合并，可能触发第 16 题的"重复 commit"识别问题
- **commit 归属语义不同**：cherry-pick 加 `-x` 参数会自动添加 `(cherry picked from commit xxx)` 注释，明确标记是搬运过来的；merge 是把整条分支语义并进来
- **分支管理习惯**：hotfix 是基于老 tag 的临时分支，用完即删，没必要在 master 历史里永久留一个指向已废弃分支的合并节点

**对比总结**：

| 维度 | merge hotfix → master | cherry-pick hotfix → master |
| --- | --- | --- |
| 最终代码 | 相同 | 相同 |
| 冲突可能性 | 相同 | 相同 |
| 历史形态 | 有 merge commit，分支结构永久保留 | 线性，干净 |
| 语义清晰度 | "合并整个分支" | "搬运这两个修复" |
| hotfix 分支可丢弃性 | 不能完全丢弃（merge 引用） | 可以删除 |

**为什么 master 推荐 cherry-pick**：

- master 是长期主干，历史要尽量干净，不希望有指向"已废弃临时分支"的合并节点
- 跨版本搬运的语义"我只是把这个修复挪过来"用 cherry-pick 表达更清晰
- 避免后续分支整体合并时 Git 历史图混乱

**为什么 release 推荐 merge**：

- hotfix 通常就是从 release（或 release 上的 tag）拉出来的
- release 和 hotfix 基底相同、历史连续，merge 后产生 fast-forward 或简单的 merge commit，历史合理
- release 分支寿命短，多一个 merge commit 也不影响

**判断准则**：

- **目标分支与源分支历史连续、共享基底（如 hotfix 回 release）**：用 merge
- **目标分支与源分支基底差异大、属于跨版本搬运（如 hotfix 到 master）**：用 cherry-pick

这也是为什么 cherry-pick 被称为"跨分支的精准搬运工具"。

# 15. cherry-pick怎么使用，适用场景

**cherry-pick 是从其他分支挑选一个或多个特定的 commit，复制到当前分支**。

使用方式：

- `git cherry-pick abc123`：把 abc123 这个提交复制到当前分支
- `git cherry-pick abc123 def456`：复制多个提交
- `git cherry-pick abc123..ghi789`：复制一段范围（不含 abc123）
- `git cherry-pick abc123^..ghi789`：复制一段范围（含 abc123）

举个典型场景：master 上发现一个紧急 bug，开发同学已经在 develop 分支修了（commit hash 是 abc123）。这时不能把整个 develop 合过来（develop 还有其他没测完的代码），就：

```
git checkout master
git cherry-pick abc123
git push origin master
```

适用场景：

- **hotfix 反向同步**：dev 分支的修复要紧急同步到 master 或 release
- **release 分支选择性合并**：master 上的修复只挑必要的合到 release
- **跨版本回合**：v1.0 上的 bug 修复回合到 v0.9 分支
- **挑选有用提交**：废弃的分支里有几个有用的提交想保留

# 16. cherry-pick某个commit，后续完整某个分支merge时，同一个commit出现两次吗

**会出现"同一份修改"两次，但严格来说是两个不同的 commit（hash 不同），Git 通常能智能处理避免冲突，但不绝对**。

详细分析：

- cherry-pick 会**生成一个新的 commit**，hash 跟原 commit 不同，但内容一致
- 后续 merge 时，Git 通过 **三方合并算法** 比对内容，发现这部分修改已经存在，通常会自动跳过
- **但如果中间有冲突或修改**，比如 cherry-pick 后又改了一下，merge 时就会冲突

举例说明：master 上 cherry-pick 了 dev 的提交 abc123（变成 abc123' 在 master 上），后来 dev 整体合并到 master：

- 顺利情况：Git 识别出 abc123 的修改已经在 master，merge 时自动跳过
- 冲突情况：cherry-pick 之后，abc123' 在 master 上被人微调过，merge 时会报冲突，需要手动解决

实践建议：

- **能避免就避免**：cherry-pick 后续如果计划整体合并，就不要 cherry-pick，等合并即可
- **必须 cherry-pick 时打标记**：在 commit message 里注明 `(cherry picked from commit abc123)`，merge 时方便判断
- **使用** `-x` 参数：`git cherry-pick -x abc123` 会自动添加上面这行注释

# 17. 大厂一般git工作流程是什么

大厂常见的是 **trunk-based development（主干开发）** 或 **改良的 GitHub Flow**，核心特点是分支短期、频繁合并、自动化保障。

典型流程：

- **从 master 拉短期 feature 分支**：分支生命周期一般 1\~3 天
- **本地开发 + 频繁 rebase master**：保持代码与主干同步
- **提 PR/MR**：触发 CI（编译、单测、静态扫描、安全扫描）
- **Code Review**：至少 1\~2 人 approve，自动化检查必须全绿
- **Squash merge 到 master**：保持 master 提交历史干净，每个提交对应一个 PR
- **自动部署到测试环境**：master 自动触发 CI/CD
- **特性开关控制发布节奏**：合到 master 不等于上线，通过 feature flag 控制可见性
- **Tag 发布生产**：定期从 master 打 tag，发布到生产

举例（阿里、字节、腾讯类似）：

- 一个需求拆成多个小 PR，每个 PR 半天到一天完成
- 不存在长期的 develop 分支
- 紧急修复直接从 master 拉 hotfix 分支，修完合回 master 并 cherry-pick 到当前 release

跟传统 Git Flow 的区别：**没有长期 develop 分支、没有复杂的分支模型、强依赖 CI/CD 和特性开关**。

# 18. 常用的git工作流程是什么

业界常见的有四种，按复杂度排序：

- **Trunk-Based Development（主干开发）**：所有人在 master 上做小步提交，配合特性开关。Google、Facebook 用得多。
- **GitHub Flow**：master + 短期 feature 分支，PR 合并。最简单灵活，开源项目和互联网公司常用。
- **GitLab Flow**：在 GitHub Flow 基础上加环境分支（pre-production、production），适合需要多环境部署的场景。
- **Git Flow**：master + develop + feature + release + hotfix，分支多、流程重。适合有明确版本发布周期的传统软件（如桌面软件）。

四种流程对比：

| 流程 | 分支数 | 适用场景 | 主要痛点 |
| --- | --- | --- | --- |
| Trunk-Based | 1（master）+ 短分支 | 互联网产品、持续部署 | 强依赖 CI/特性开关 |
| GitHub Flow | master + feature | SaaS、开源 | 多环境部署支持弱 |
| GitLab Flow | \+ 环境分支 | 多环境部署 | 中等复杂度 |
| Git Flow | 5 类分支 | 版本化发布 | 太重、分支管理复杂 |

举例：一个互联网公司新业务团队选 GitHub Flow，老牌金融软件公司选 Git Flow，谷歌这种超大规模代码库选 Trunk-Based。

# 19. conventional commits理念是什么

**Conventional Commits 是一种结构化的提交信息规范**，让提交信息既人可读、又机器可解析，用于自动生成 changelog 和语义化版本。

格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

常用 type：

- **feat**：新功能（对应 minor 版本号 +1）
- **fix**：bug 修复（对应 patch 版本号 +1）
- **docs**：文档变更
- **style**：代码格式（不影响功能）
- **refactor**：重构
- **perf**：性能优化
- **test**：测试相关
- **chore**：构建、配置、依赖等杂项
- **revert**：回滚提交

举例：

```
feat(login): 增加手机验证码登录

新增 /api/v1/login/sms 接口，调用阿里云短信服务发送验证码，
验证码 5 分钟有效期，存储在 Redis。

Closes #1234
BREAKING CHANGE: 登录接口的响应字段从 token 改为 access_token
```

带来的好处：

- **自动生成 changelog**：工具按 type 分类生成发布日志
- **自动语义化版本**：`feat` 自动 +minor，`fix` 自动 +patch，`BREAKING CHANGE` 自动 +major
- **提交历史可读**：一眼看出每个提交的性质和影响范围
- **配合工具链**：commitizen、commitlint、semantic-release 一整套生态

# 20. 特性开关模式是什么

**特性开关（Feature Flag / Feature Toggle）是用配置开关控制功能是否对用户可见的模式**，让"代码合并"和"功能上线"解耦。

核心思想：

- 代码可以合到 master 并部署到生产，但功能默认关闭
- 通过配置中心动态打开开关，控制哪些用户、什么时候能看到这个功能
- 验证没问题后逐步放量，最后清理开关代码

举个例子：开发新版搜索功能，写法如下：

```java
if (featureFlagService.isEnabled("new-search", userId)) {
    return newSearch(query);  // 新版逻辑
} else {
    return oldSearch(query);  // 旧版逻辑
}
```

发布过程：

1. 代码合 master，开关默认关闭，所有用户看到老版搜索
2. 上线后，给内部员工打开开关验证
3. 灰度 1% 用户验证稳定性
4. 逐步放量到 10%、50%、100%
5. 全量稳定后，删除老代码和开关，只保留新版

特性开关的几种类型：

- **Release Toggle**：发布开关，控制特性上线，临时使用
- **Experiment Toggle**：A/B 测试开关，对比新旧版本效果
- **Ops Toggle**：运维开关，紧急情况下关闭某个功能（降级）
- **Permission Toggle**：权限开关，VIP 用户可见某些功能

带来的价值：

- **支持主干开发**：避免长期 feature 分支
- **降低发布风险**：出问题秒级关闭，不需要回滚代码
- **灰度发布能力**：分用户、分比例放量
- **A/B 测试能力**：同时跑两套逻辑对比效果

注意点：开关代码会增加圈复杂度，**用完必须及时清理**，否则会变成"开关坟场"，代码维护成本激增。

