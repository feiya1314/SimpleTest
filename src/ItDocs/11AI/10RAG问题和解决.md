随着大型语言模型（LLM）的新进展，包括 ChatGPT，软件工程师现在能够开发新型人机交互（HCI）解决方案，执行复杂任务，总结文档，回答特定文件中的问题，甚至创造新内容。但是，LLM 在获取最新信息或公司数据库中特定领域的知识方面仍有局限。针对这一问题，有两种解决方案：一是对 LLM 进行微调（即使用特定领域的资料对 LLM 进行进一步训练），这涉及管理或部署一个微调过的 LLM；二是采用基于检索的增强生成（RAG）系统，利用 LLM 生成基于现有（可扩展）知识工件的答案。这两种方法都有各自的优缺点，如数据隐私/安全性、可扩展性、成本和所需技能等。本文主要讨论 RAG 方法。

基于检索的增强生成（RAG）系统提供了一种解决这一挑战的有效方法。通过整合检索机制和 LLM 的生成能力，RAG 系统能够生成与上下文相关、准确且更新的信息。RAG 系统结合了信息检索和 LLM 的生成技术，检索部分旨在从数据存储中提取用户查询的相关信息，而生成部分则利用这些检索到的信息作为背景来生成回答。RAG 系统的一个重要应用是，它使得所有非结构化信息都可以被索引并供查询，从而减少了开发时间，无需创建知识图谱，也减少了数据整理和清洗的工作量。

构建 RAG 系统的软件工程师需要对不同格式的领域知识工件进行预处理，将处理后的信息存储在适当的数据库（如向量数据库）中，并实现或集成合适的查询 - 工件匹配策略。他们还需对匹配的工件进行排序，并通过调用 LLM 的 API，将用户查询和上下文文档一起传入。构建 RAG 系统的技术不断进步（参见 Lewis et al., 2020；Hofstätter et al., 2023），但如何将这些进步应用于特定的应用场景，以及它们的表现如何，还需要进一步探索。

在这项工作中，我们分享了从三个案例研究中获得的经验教训和七个常见失败点。本文的目的是为实践者提供参考，并为 RAG 系统的研究提供一个路线图。据我们所知，这是首次就构建健壮的 RAG 系统所面临的挑战提供实证洞察。随着 LLM 的不断进步，软件工程领域需要了解如何利用 LLM 构建健壮的系统。这项工作对于建立健壮的 RAG 系统至关重要。

本研究的研究问题包括：

在构建 RAG 系统时，可能遇到哪些失败点？（详见第 5 节）我们利用 BioASQ 数据集进行了一项实证实验，探讨了可能的失败点。实验包括了 15,000 份文档和 1000 个问题及答案对。我们对所有文档进行了索引，然后运行查询并使用 GPT-4 生成响应。所有的问题和答案对随后都经过了 OpenAI evals 1 的验证。通过手动检查（包括所有差异、所有被标记为错误的内容，以及一部分被标记为正确的内容）我们分析并识别了模式。

如何有效构建 RAG（Retrieval Augmented Generation）系统？ (第 6 节) 本文从三个实际案例出发，探讨了在实施 RAG 系统时遇到的挑战和得到的有价值的见解。

这项研究的主要成果包括：

列出了 RAG 系统中可能出现的故障点。

基于三个案例（其中两个正在迪肯大学运行）对实施 RAG 系统的实践报告。

基于三个案例研究的经验，提出了 RAG 系统的未来研究方向。

2.相关工作
检索增强生成是指在训练前和推理阶段使用文档来提升大语言模型 (LLM) 的能力（Izacard 和 Grave，2020; Guu 等人，2020; Lewis 等人，2020）。考虑到计算成本、数据准备时间和资源需求，不进行训练或微调的 RAG 使用方法尤为吸引人。然而，在使用大语言模型提取信息时，尤其是处理长文本时，会遇到一些挑战（Hofstätter 等人，2023）。

最近的一项调查（Zhu 等人，2023）显示，在 RAG 流程中，大语言模型在检索、数据生成、重写和阅读各环节中发挥着重要作用。我们的研究补充了这项调查，从软件工程的视角出发，探讨了工程师在实施最先进 RAG 系统时可能遇到的问题以及必要的软件工程研究。

新兴研究开始对 RAG 系统进行基准测试（Chen 等人，2023b），但很少涉及实施过程中的失败情况。软件工程领域已经研究了 RAG 系统在编码相关任务中的应用（Nashid 等人，2023）。然而，RAG 系统的应用远不止于此。本文从实践角度出发，探讨了实施 RAG 系统过程中的挑战，特别关注从业者的经验。

RAG 系统中出现的错误和失败与其他信息检索系统有一定的重叠，主要包括：1) 缺乏查询重写的度量方法，2) 文档的重新排序问题，3) 高效的内容摘要方法（Zhu 等人，2023）。我们的研究结果证实了这些问题。其独特之处在于，它涉及到使用大语言模型的语义生成特性，特别是在评估事实准确性方面的挑战（OpenAI，2023）。

3.检索增强生成
参见标题说明
参见标题说明
图 1：创建检索增强生成 (RAG) 系统所需的索引和查询步骤。通常在开发阶段进行索引，而查询则在运行时进行。本研究中识别出的失败点在图中以红色方框标出。所有必要的步骤都被加了下划线。图表来源：Zhu et al. 2023。

随着 ChatGPT2、Claude3 和 Bard 4 等大语言模型服务的迅速流行，人们开始使用它们作为问答系统。尽管它们的表现令人印象深刻（参考 OpenAI, 2023），但存在两大挑战：1) 幻觉现象——即大语言模型产生看似正确但实际错误的回答；2) 无法限制——无法直接指导或更新输出内容，只能通过提示工程来间接影响。为了克服这些限制，RAG 系统采用了信息检索方法。

RAG 系统的工作原理是：首先将自然语言查询转换为嵌入向量，然后用这个向量在一组文档中进行语义搜索。搜索到的文档接着被送到大语言模型中，以生成答案。图 1 展示了 RAG 系统的两个主要流程：索引和查询。更多细节请参考 Zhu et al. 2023。

3.1.索引过程
在 RAG 系统中，检索系统通过嵌入向量工作，这些向量是文档的压缩语义表示。索引过程中，每个文档被分割成小块，这些块经过嵌入模型转换成向量，然后存储在数据库中。软件工程师在设计时需要决定如何最佳切分文档和块的大小。块太小可能无法回答某些问题，而块太大则可能导致答案中包含不必要的信息。

根据不同类型的文档，切分和处理的步骤也有所不同。例如，处理视频内容时需要一个转录流程，将音频提取并转换为文本，之后再进行编码（详见小节 4.2）。选择合适的嵌入方式也至关重要，因为更换嵌入策略意味着需要对所有内容块重新索引。选择嵌入方式应基于其检索正确回应的能力，这取决于块的大小、预期问题的类型、内容结构和应用领域。

3.2.查询过程
查询过程发生在系统运行时。首先，一个自然语言形式的问题被转换成一般的查询请求。为了泛化这个查询，我们使用了大语言模型，这使得可以将额外的上下文（如之前的聊天记录）融入到新的查询中。接着，根据新查询计算出一个嵌入式表示，用来从数据库中寻找相关文档。我们通过像余弦相似度这样的方法来检索最相似的前 k 个文档（例如，向量数据库利用倒排索引等技术来加快检索速度）。基本的想法是，与查询在语义上接近的内容块更有可能包含所需答案。

接下来，检索到的文档会进行重新排序，以提高含有答案的内容块位于排名前列的可能性。紧接着是“整合器”阶段，该阶段负责处理这些内容块。这一步骤是必要的，因为它帮助解决了大语言模型面临的两大限制：一是令牌数量限制，二是请求频率限制。例如，OpenAI 这样的服务对于一次性提示中可以包含的文本量设有上限。这就限定了在一次提示中可以包含的内容块数量，为了从中提取答案，需要采取一种减少策略，并通过连续的提示来获取答案。此外，这些服务还对一定时间内可使用的令牌数量设有限制，这影响了系统的响应速度。因此，在设计 RAG 系统时，软件工程师需要权衡这些因素。

RAG 流程的最终阶段是从生成的文本中提取答案。在这一阶段，负责读取的部分要从输入的提示中过滤掉无关信息，遵守格式化指令（如以选项列表的形式回答问题），并产生最终的查询回应。实施 RAG 系统需要根据问题和答案的内容定制多个提示。这一过程确保了返回的问题与特定领域相关。利用大语言模型实时回答文档中的问题，为那些涉及问答的新应用领域开辟了新的可能性。因此，测试 RAG 系统具有一定的难度，因为缺乏现成的测试数据，需要通过合成数据生成或者最小规模的系统试运行来进行实验性探索。

4.案例研究
本研究通过三个案例研究探讨了实施 RAG 系统时遇到的挑战。每个案例研究的概要可在 表 1 中查看。BioASQ 案例研究所用的所有脚本、数据及失败点的示例均可在线获取。由于保密原因，其他两个案例研究未予以公开。

案例研究	领域	文档类型	数据集大小	RAG 阶段	样本问题
认知审查者\*	研究	PDF 文档	(任意大小)	分块器、重写器、检索器、读取器	这篇论文主要讨论了哪些关键点？
AI 导师\*	教育	视频、HTML、PDF 文档	38	分块器、重写器、检索器、读取器	第六周讲述了哪些主题？
BioASQ	生物医学	科学 PDF 文档	4017	分块器、检索器、读取器	如何定义假性肿瘤脑病？其治疗方法是什么？
表 1.本文介绍的 RAG 案例研究摘要。带 \* 标记的案例研究表示目前正在使用的运行系统。

4.1.认知审查员
认知审查员是一种用于辅助研究人员分析科学文献的 RAG（检索式问答生成）系统。研究人员可以设定一个研究问题或目标，上传相关的研究论文集。系统会根据设定的目标对所有文档进行排名，供研究人员进行手动审查。研究者还可以直接对所有文档提出问题。目前，迪肯大学的博士生使用认知审查员来辅助他们的文献综述工作。该系统在运行时执行索引处理，并依赖于一个强大的数据处理流程来处理上传的文档，因此开发阶段无法进行质量控制。此外，系统还应用了一个排序算法来组织上传的文档。

4.2.AI 导师
AI 导师是一种 RAG 系统，学生可以在此系统中提出与课程单元相关的问题，答案则从学习内容中获取。学生可以通过访问答案来源列表来核实答案的准确性。AI 导师通过集成到迪肯大学的学习管理系统中，对包括 PDF 文档、视频和文本文件在内的所有内容进行索引。在索引过程中，使用深度学习模型 Whisper (Radford et al., 2023) 对视频进行转录并分段处理。AI 导师从 2023 年 8 月开发至 11 月，用于 2023 年 10 月 30 日开始的含 200 名学生的课程单元试点。我们计划在试点结束后分享实施过程中的经验教训及后续发现。这个 RAG 系统包含了一个查询重写功能，能够概括化学生的提问。我们还实现了一个聊天界面，通过考虑用户与 AI 导师之间之前的对话作为每个问题的上下文。查询重写功能会根据这一上下文调整问题，以解决如“进一步解释这个概念”等模糊请求。

4.3.生物医学问答
之前的案例研究主要关注内容较少的文档。为了在更大规模上探索问题，我们利用 BioASQ（一个生物医学问答数据集，由 Krithara et al., 2023 编制）开发了一个 RAG 系统。该数据集包含了问题、相关文档的链接和答案，答案形式包括是/否、文本摘要、事实或列表。这些由生物医学专家编制的问题和答案对集中于特定领域的知识。我们从 BioASQ 数据集下载了 4017 份公开获取的文档，并准备了 1000 个问题。所有文档被编入索引，并对 RAG 系统提出问题。生成的问题随后通过 OpenAI6 实现的 OpenEvals 技术进行评估。我们手动检查了其中的 40 个问题和所有被 OpenEvals 标记为不准确的问题。我们发现，对于这一领域，自动评估结果比人类评估更为严格。但是，需要注意的是，由于 BioASQ 是针对特定领域的数据集，且评审员不是该领域专家，因此可能存在评估偏差，即大语言模型在某些方面可能比非专家更加精通。

1. RAG 系统的失败点
   我们通过案例研究确认了一系列 RAG 系统可能遇到的失败点。下一节将探讨这个研究问题：在开发 RAG 系统过程中，会遇到哪些失败点？

FP1

缺少内容 当用户提出一个无法仅凭现有文档回答的问题时，就会出现第一个失败案例。在最理想的情况下，RAG 系统会回答“对不起，我不知道”。但如果问题虽与内容有关但找不到答案，系统可能误导性地给出一个回答。

FP2

未涵盖排名靠前的文档 当答案存在于某个文档中，但因排名不够靠前而未能展示给用户。理论上，所有文档都会进行排名并用于后续处理。但实际上，仅有排名前 K 的文档会被展示，这里的 K 是基于性能考虑选择的一个数字。

FP3

上下文外置 - 整合策略的限制 即使数据库检索到了含有答案的文档，这些文档也可能未能融入到生成答案的上下文中。这通常发生在数据库返回大量文档，并且需要通过一个整合过程来提取答案时。

FP4

未能提取答案 存在于上下文中的答案未被大语言模型正确抽取。这通常发生在上下文中信息太杂乱或信息相互矛盾的情况下。

FP5

格式错误 当问题要求以特定格式（如表格或列表）提取信息，而大语言模型没有按照这一要求进行处理时。

FP6

不恰当的详细程度 回答虽然给出，但对于用户的需求来说，要么过于笼统，要么过于具体。这种情况可能发生在 RAG 系统的设计者对特定问题有特定期望，如教师针对学生的问题。在这种情况下，应该提供具体的教育内容，而不仅仅是答案。当用户不确定如何提问，问题过于广泛时，也可能出现不恰当的详细程度。

FP7

回答不完整 回答虽然不是错误的，但缺少了一些信息，即使这些信息在上下文中存在并可以提取。比如，对于“文档 A、B 和 C 中包含哪些关键点？”这样的问题，更好的提问方式是将这些问题分开来问。

6.经验总结和未来研究的方向
FP	经验教训	描述	案例研究
FP4	越大的背景环境，结果越好 (背景环境指内容发生的特定场合或情景)	在更广阔的背景下，我们得到了更准确的反馈（8K 相较于 4K 更为有效）。这与之前关于 GPT-3.5 的研究有所不同（Liu et al., 2023b）	AI Tutor
FP1	有效利用语义缓存可以减少成本和响应时间	RAG 系统在处理多用户时面临速率限制和大型语言模型的高成本问题。通过预先填充常见问题到语义缓存中可以解决这个问题（Bang, 2023）。	AI Tutor
FP5-7	突破系统限制可以检验安全训练的有效性。	研究显示，对大型语言模型的微调可能会削弱其安全性训练（Lermen et al., 2023），因此需要对所有经过微调的大型语言模型进行测试。	AI Tutor
FP2, FP4	加入额外信息能够提高检索效率。	在检索信息时，添加文件名和段落编号可以帮助快速定位所需内容，这对于聊天对话尤为重要。	AI Tutor
FP2, FP4-7	开源嵌入模型在处理短文本方面更加高效。	在处理短文本时，开源的句子嵌入模型表现得和闭源替代品一样好。	BioASQ, AI Tutor
FP2-7	RAG 系统需要持续调整和优化。	RAG 系统在实际运行中会遇到各种未预料的输入，因此需要不断进行监控和调整。	AI Tutor, BioASQ
FP1, FP2	为 RAG 系统配置有效的处理流程至关重要。	一个有效的 RAG 系统需要在块大小、嵌入策略、分块策略、检索策略、整合策略、上下文大小和提示方面进行精确调整。	认知审查者，AI Tutor, BioASQ
FP2, FP4	组合定制解决方案构建的 RAG 系统可能不是最佳选择。	端到端的训练方法能够在 RAG 系统中更好地适应不同领域的需求（Siriwardhana et al., 2023）。	BioASQ, AI Tutor
FP2-7	系统的性能特征只能在实际运行时才能得到测试。	虽然像 G-Evals 这样的离线评估技术看起来很有希望（Liu et al., 2023a），但它们的前提是需要有标记好的问题和答案对。	认知审查者，AI Tutor
表 2. 从三个案例研究中获得的经验教训及其对未来 RAG 实施的主要启示

从这三个案例研究中，我们总结出了一些宝贵的经验教训，这些教训在 表 2 中有所展示。针对研究问题：“在构建 RAG 系统时，需要考虑哪些关键因素？”我们基于这些经验教训，确定了与 RAG 相关的多个未来研究方向。

6.1. 文档切分和嵌入
虽然将文档分成小块看似简单，但其实这个过程对于检索信息的效果有着重大影响。这主要体现在如何将这些文档块转换成电脑能够理解和比较的形式（即“嵌入”），进而影响这些块与用户搜索内容的匹配程度。文档切分主要有两种方式：一种是基于形式的切分，比如利用标点和段落的结束；另一种是基于文档内容的意义进行切分。未来的研究需要探索这两种方法的优劣，以及它们对文档块转换和匹配过程的影响。建立一个比较不同切分技术的评估框架，特别是在查询相关性和检索准确度方面，将对该领域大有裨益。

另一个研究热点是如何为多媒体和多模式的内容（例如表格、图表、公式等）创建有效的嵌入形式。这些嵌入通常在系统开发或新文档被加入索引时只创建一次。查询预处理过程，尤其是处理那些含糊或负面的查询，对 RAG 系统的性能有显著影响。关于如何改进嵌入技术以解决其固有的局限性（例如匹配的准确度通常与特定领域相关），还需要进一步的研究。

6.2. RAG 与微调
大语言模型（LLM）之所以成为出色的“世界模型”，归功于其庞大的训练数据和在发布前进行的微调。但这些模型通常是通用的，并且知识更新并不及时。微调和 RAG 提供了两种不同的个性化途径，每种方法都有其利弊。微调需要定制数据集来适应和训练大语言模型，但这意味着所有数据都集成在模型中，涉及安全和隐私的问题。而且，随着基础模型的更新或新数据的加入，需要重新进行微调。相比之下，RAG 系统提供了一种灵活的解决方案，允许按需切分数据，并将相关部分纳入上下文中，由大语言模型生成答案。这种方法不仅便于不断更新知识，还能控制用户访问的数据块。然而，在块嵌入、检索和上下文融合方面的最佳策略，仍然是一个活跃的研究议题。未来的工作应该系统地比较微调和 RAG 方法，在准确性、响应时间、运营成本和鲁棒性等方面进行评估。

6.3. 测试与监控 RAG 系统
RAG 系统的软件工程最佳实践还在逐步形成。软件测试和测试用例生成是改进的关键领域。RAG 系统需要针对特定应用的问题和答案，而这些在处理非结构化文档时往往难以获得。最新的研究尝试使用大语言模型 (LLMs) 从多个文档中生成问题（参见 Chen et al., 2023a）。如何创造与实际领域紧密相关的问题和答案，目前仍是一个悬而未决的问题。

一旦我们能够获取适当的测试数据，就需要相应的质量评估指标，以帮助工程师在质量上做出平衡选择。使用大型语言模型虽然成本高昂，可能引起响应延迟，且性能特性随着每次新版本的发布而变化，但这一特点在机器学习系统中已有研究（参见 Cummaudo et al., 2020b, a）。这些研究成果是否适用于基于 LLM 的系统，比如 RAG，还有待观察。另一个方向是，探索将自适应系统的理念融入到 RAG 系统的监控和调整中，这方面的初步工作已开始应用于其他机器学习领域（参见 Casimiro et al., 2022）。

1. 结论
   RAG 系统是一种新型信息检索工具，它利用了大型语言模型 (LLMs)。软件工程师越来越多地通过实施语义搜索或处理新的代码依赖任务与 RAG 系统互动。本文基于三个案例研究，包括对 15,000 份文档和 1000 个问题的实证研究，分享了我们的经验教训。我们的发现为从业人员提供了宝贵的参考，展示了实施 RAG 系统时所面临的挑战。同时，我们也探讨了 RAG 系统的未来研究方向，包括 1）数据分块和嵌入技术，2）RAG 与微调策略的比较，以及 3）测试与监控策略。随着大型语言模型不断发展，获得对工程师和研究者更有价值的新功能，本文从软件工程的视角，对 RAG 系统进行了首次深入探讨。



到 Barnett 等人的论文《设计检索增强生成系统时的七个故障点》的启发，让我们在本文中探讨该论文中提到的七个故障点以及开发 RAG 管道时的另外五个常见痛点。更重要的是，我们将深入研究这些 RAG 痛点的解决方案，以便我们能够更好地解决日常 RAG 开发中的这些痛点。
我用“痛点”而不是“失败点”主要是因为这些点都有相应的建议解决方案。让我们尝试在 RAG 管道出现故障之前修复它们。
首先，我们来看看上述论文中提到的七个痛点；见下图。然后我们将添加五个额外的痛点及其建议的解决方案。




痛点一：内容缺失
当实际答案不在知识库中时，RAG 系统会提供看似合理但不正确的答案，而不是说它不知道。用户收到误导性信息，导致沮丧。
我们提出了两种解决方案：


清理您的数据

垃圾进垃圾出。如果您的源数据质量很差，例如包含相互冲突的信息，那么无论您将 RAG 管道构建得多么好，它都无法发挥从您提供的垃圾中输出黄金的魔力。这个提出的解决方案不仅针对这个痛点，而且针对本文列出的所有痛点。干净的数据是任何运行良好的 RAG 管道的先决条件。


更好的提示

当系统由于知识库中缺乏信息而可能提供看似合理但不正确的答案时，更好的提示可以提供显着帮助。通过使用诸如“如果您不确定答案，请告诉我您不知道”之类的提示来指导系统，您可以鼓励模型承认其局限性并更透明地传达不确定性。无法保证 100% 的准确性，但精心设计提示是清理数据后可以做出的最佳努力之一。


痛点二：错过排名靠前的文档

重要文档可能不会出现在系统检索组件返回的顶部结果中。正确答案被忽略，导致系统无法提供准确的响应。该论文暗示，“问题的答案在文档中，但排名不够高，无法返回给用户”。


我想到了两个建议的解决方案：
chunk_size 和的超参数调整similarity_top_k
和chunk_size都是similarity_top_k用于管理 RAG 模型中数据检索过程的效率和有效性的参数。调整这些参数可以影响计算效率和检索信息质量之间的权衡。chunk_size我们在上similarity_top_k一篇文章《使用 LlamaIndex 自动化超参数调整》中探讨了两者的超参数调整细节。请参阅下面的示例代码片段。

param_tuner = ParamTuner( 
    param_fn=objective_function_semantic_similarity, 
    param_dict=param_dict, 
    fixed_param_dict=fixed_param_dict, 
    show_progress=True, 
)
results = param_tuner.tune()

该函数objective_function_semantic_similarity定义如下，包含param_dict参数 、chunk_size和top_k，以及它们相应的建议值：

# contains the parameters that need to be tuned
param_dict = {"chunk_size": [256, 512, 1024], "top_k": [1, 2, 5]}

# contains parameters remaining fixed across all runs of the tuning process
fixed_param_dict = {
    "docs": documents,
    "eval_qs": eval_qs,
    "ref_response_strs": ref_response_strs,
}

def objective_function_semantic_similarity(params_dict):
    chunk_size = params_dict["chunk_size"]
    docs = params_dict["docs"]
    top_k = params_dict["top_k"]
    eval_qs = params_dict["eval_qs"]
    ref_response_strs = params_dict["ref_response_strs"]

    # build index
    index = \_build_index(chunk_size, docs)

    # query engine
    query_engine = index.as_query_engine(similarity_top_k=top_k)

    # get predicted responses
    pred_response_objs = get_responses(
        eval_qs, query_engine, show_progress=True
    )

    # run evaluator
    eval_batch_runner = \_get_eval_batch_runner_semantic_similarity()
    eval_results = eval_batch_runner.evaluate_responses(
        eval_qs, responses=pred_response_objs, reference=ref_response_strs
    )

    # get semantic similarity metric
    mean_score = np.array(
        [r.score for r in eval_results["semantic_similarity"]]
    ).mean()

    return RunResult(score=mean_score, params=params_dict)

有关更多详细信息，请参阅 LlamaIndex 关于RAG 超参数优化的完整笔记本。


重新排名

在将检索结果发送到LLM之前对其进行重新排序可以显着提高 RAG 性能。这个 LlamaIndex笔记本展示了以下之间的区别：

直接检索前 2 个节点而无需重新排序，导致检索不准确。
通过检索前 10 个节点并使用CohereRerank重新排序并返回前 2 个节点来精确检索。


import os
from llama_index.postprocessor.cohere_rerank import CohereRerank

api_key = os.environ["COHERE_API_KEY"]
cohere_rerank = CohereRerank(api_key=api_key, top_n=2) # return top 2 nodes from reranker

query_engine = index.as_query_engine(
    similarity_top_k=10, # we can set a high top_k here to ensure maximum relevant retrieval
    node_postprocessors=[cohere_rerank], # pass the reranker to node_postprocessors
)

response = query_engine.query(
    "What did Sam Altman do in this essay?",
)

此外，您可以使用各种嵌入和重新排序器来评估和增强检索器性能，如Ravi Theja的Boosting RAG: Picking the Best Embedding & Reranker models中详细介绍的。
此外，您可以微调自定义重排序器以获得更好的检索性能，详细的实现记录在Ravi Theja 的Improving Retrieval Performance by Fine-tuning Cohere Reranker with LlamaIndex中。


痛点三：不切实际——整合策略的局限性

该论文定义了这一点：“带有答案的文档是从数据库中检索的，但没有进入生成答案的上下文。当从数据库返回许多文档并进行合并过程以检索答案时，就会发生这种情况”。
除了按照上一节所述添加重新排序器并微调重新排序器之外，我们还可以探索以下建议的解决方案：


调整检索策略

LlamaIndex 提供了一系列从基础到高级的检索策略，帮助我们在 RAG 管道中实现准确的检索。查看检索器模块指南，获取所有检索策略的完整列表，分为不同的类别。

每个索引的基本检索
高级检索和搜索
自动检索
知识图检索器
组合/分层检索器
和更多！


微调嵌入

如果您使用开源嵌入模型，微调嵌入模型是实现更准确检索的好方法。LlamaIndex 有一个关于微调开源嵌入模型的分步指南，证明微调嵌入模型可以在整个评估指标中一致地改进指标。
请参阅下面的示例代码片段，了解如何创建微调引擎、运行微调并获取微调模型：

finetune_engine = SentenceTransformersFinetuneEngine(
    train_dataset,
    model_id="BAAI/bge-small-en",
    model_output_path="test_model",
    val_dataset=val_dataset,
)

finetune_engine.finetune()

embed_model = finetune_engine.get_finetuned_model()

痛点四：未提取

系统很难从提供的上下文中提取正确的答案，尤其是在信息过载的情况下。关键细节被遗漏，从而影响了响应的质量。该论文暗示：“当上下文中有太多噪音或相互矛盾的信息时，就会发生这种情况”。
让我们探讨三个建议的解决方案：


清理您的数据

这个痛点是不良数据的另一个典型受害者。我们怎么强调干净数据的重要性都不过分！在指责 RAG 管道之前，请先花时间清理数据。


Prompt压缩

LongLLMLingua 研究项目/论文中介绍了长上下文环境中的即时压缩。通过与 LlamaIndex 的集成，我们现在可以将 LongLLMLingua 实现为节点后处理器，它将在检索步骤之后压缩上下文，然后将其输入 LLM。
请参阅下面的示例代码片段，我们在其中进行设置LongLLMLinguaPostprocessor，它使用longllmlingua包来运行提示压缩。
有关更多详细信息，请查看LongLLMLingua 上的完整笔记本。

from llama_index.query_engine import RetrieverQueryEngine
from llama_index.response_synthesizers import CompactAndRefine
from llama_index.postprocessor import LongLLMLinguaPostprocessor
from llama_index.schema import QueryBundle

node_postprocessor = LongLLMLinguaPostprocessor(
    instruction_str="Given the context, please answer the final question",
    target_token=300,
    rank_method="longllmlingua",
    additional_compress_kwargs={
        "condition_compare": True,
        "condition_in_question": "after",
        "context_budget": "+100",
        "reorder_context": "sort",  # enable document reorder
    },
)

retrieved_nodes = retriever.retrieve(query_str)
synthesizer = CompactAndRefine()

# outline steps in RetrieverQueryEngine for clarity:
# postprocess (compress), synthesize
new_retrieved_nodes = node_postprocessor.postprocess_nodes(
    retrieved_nodes, query_bundle=QueryBundle(query_str=query_str)
)

print("\\n\\n".join([n.get_content() for n in new_retrieved_nodes]))

response = synthesizer.synthesize(query_str, new_retrieved_nodes)

长上下文重排序(LongContextReorder)

一项研究发现，当关键数据位于输入上下文的开头或结尾时，通常会出现最佳性能。LongContextReorder旨在通过重新排序检索到的节点来解决“迷失在中间”的问题，这在需要大的 top-k 的情况下很有帮助。
请参阅下面的示例代码片段，了解如何在查询引擎构建期间定义LongContextReorder为您的node_postprocessor。有关更多详细信息，请参阅 LlamaIndex上的完整笔记本LongContextReorder。

from llama_index.postprocessor import LongContextReorder

reorder = LongContextReorder()

reorder_engine = index.as_query_engine(
    node_postprocessors=[reorder], similarity_top_k=5
)

reorder_response = reorder_engine.query("Did the author meet Sam Altman?")

痛点五：格式错误

当LLM忽略以特定格式（如表格或列表）提取信息的指令时，我们提出了四种解决方案可供探索：


更好的提示

您可以采用多种策略来改进提示并纠正此问题：

澄清说明。
简化请求并使用关键字。
举例说明。
迭代提示并询问后续问题。


输出解析(Output parsing)

可以通过以下方式使用输出解析来帮助确保所需的输出：

为任何提示/查询提供格式说明
为 LLM 输出提供“解析”
LlamaIndex 支持与其他框架提供的输出解析模块集成，例如Guardrails和LangChain。
请参阅下面的 LangChain 输出解析模块的示例代码片段，您可以在 LlamaIndex 中使用它。有关更多详细信息，请查看关于输出解析模块的 LlamaIndex 文档。

from llama_index import VectorStoreIndex, SimpleDirectoryReader
from llama_index.output_parsers import LangchainOutputParser
from llama_index.llms import OpenAI
from langchain.output_parsers import StructuredOutputParser, ResponseSchema

# load documents, build index
documents = SimpleDirectoryReader("../paul_graham_essay/data").load_data()
index = VectorStoreIndex.from_documents(documents)

# define output schema
response_schemas = [
    ResponseSchema(
        name="Education",
        description="Describes the author's educational experience/background.",
    ),
    ResponseSchema(
        name="Work",
        description="Describes the author's work experience/background.",
    ),
]

# define output parser
lc_output_parser = StructuredOutputParser.from_response_schemas(
    response_schemas
)
output_parser = LangchainOutputParser(lc_output_parser)

# Attach output parser to LLM
llm = OpenAI(output_parser=output_parser)

# obtain a structured response
from llama_index import ServiceContext

ctx = ServiceContext.from_defaults(llm=llm)

query_engine = index.as_query_engine(service_context=ctx)
response = query_engine.query(
    "What are a few things the author did growing up?",
)
print(str(response))


Pydantic 程序

Pydantic 程序充当通用框架，将输入字符串转换为结构化 Pydantic 对象。LlamaIndex 提供了几种类型的 Pydantic 程序：

LLM 文本完成 Pydantic 程序：这些程序利用文本完成 API 与输出解析相结合，处理输入文本并将其转换为用户定义的结构化对象。
LLM 函数调用 Pydantic 程序：这些程序利用 LLM 函数调用 API，获取输入文本并将其转换为用户指定的结构化对象。
预打包的 Pydantic 程序：这些程序旨在将输入文本转换为预定义的结构化对象。
请参阅下面OpenAI pydantic 程序的示例代码片段。有关更多详细信息，请查看 LlamaIndex 关于pydantic 程序的文档，以获取不同 pydantic 程序的笔记本/指南的链接。

from pydantic import BaseModel
from typing import List

from llama_index.program import OpenAIPydanticProgram

# Define output schema (without docstring)
class Song(BaseModel):
    title: str
    length_seconds: int


class Album(BaseModel):
    name: str
    artist: str
    songs: List[Song]

# Define openai pydantic program
prompt_template_str = """\\
Generate an example album, with an artist and a list of songs. \\
Using the movie {movie_name} as inspiration.\\
"""
program = OpenAIPydanticProgram.from_defaults(
    output_cls=Album, prompt_template_str=prompt_template_str, verbose=True
)

# Run program to get structured output
output = program(
    movie_name="The Shining", description="Data model for an album."
)

OpenAI JSON模式

OpenAI JSON 模式使我们能够设置response_format为{ "type": "json_object" }启用 JSON 模式的响应。启用 JSON 模式时，模型仅限于生成解析为有效 JSON 对象的字符串。虽然 JSON 模式强制执行输出格式，但它无助于针对指定模式进行验证。有关更多详细信息，请查看 LlamaIndex 关于OpenAI JSON 模式与数据提取的函数调用的文档。


痛点六：特异性不正确

答复可能缺乏必要的细节或具体性，通常需要后续询问才能澄清。答案可能过于模糊或笼统，无法有效满足用户的需求。
我们求助于高级检索策略来寻求解决方案。


高级检索策略

当答案未达到您期望的正确粒度时，您可以改进检索策略。一些可能有助于解决这一痛点的主要高级检索策略包括：

从小到大检索
句子窗口检索
递归检索
请参阅我的上一篇文章“使用高级检索 LlamaPack 启动您的 RAG 管道”和“使用 Lighthouz AI 进行基准测试”，了解有关七个高级检索 LlamaPack 的更多详细信息。


痛点七：不完整（Incomplete）

片面的回答并没有错，而是错误的。然而，尽管信息在上下文中存在并且可以访问，但它们并没有提供所有细节。例如，如果有人问：“文件A、B、C主要讨论了哪些方面？” 单独询问每份文件可能会更有效，以确保得到全面的答复。


查询转换

比较问题在朴素的 RAG 方法中尤其表现不佳。提高 RAG 推理能力的一个好方法是添加查询理解层 ——在实际查询向量存储之前添加查询转换。以下是四种不同的查询转换：

路由：保留初始查询，同时查明其所属的适当工具子集。然后，将这些工具指定为合适的选项。
查询重写：维护选定的工具，但以多种方式重新编写查询，以将其应用于同一组工具。
子问题：将查询分解为几个较小的问题，每个问题针对由其元数据确定的不同工具。
ReAct Agent 工具选择：根据原始查询，确定要使用的工具并制定要在该工具上运行的特定查询。
请参阅下面的示例代码片段，了解如何使用 HyDE（假设文档嵌入），这是一种查询重写技术。给定一个自然语言查询，首先生成一个假设的文档/答案。然后，该假设文档用于嵌入查找而不是原始查询。

# load documents, build index
documents = SimpleDirectoryReader("../paul_graham_essay/data").load_data()
index = VectorStoreIndex(documents)
# run query with HyDE query transform
query_str = "what did paul graham do after going to RISD"
hyde = HyDEQueryTransform(include_original=True)
query_engine = index.as_query_engine()
query_engine = TransformQueryEngine(query_engine, query_transform=hyde)
response = query_engine.query(query_str)
print(response)

查看 LlamaIndex 的查询转换手册了解所有详细信息。
另外，请查看Iulia Brezeanu撰写的这篇精彩文章Advanced Query Transformations to Improve RAG，了解有关查询转换技术的详细信息。



以上痛点均来自论文。现在，让我们探讨 RAG 开发中常见的五个额外痛点及其提出的解决方案。


痛点 八：数据摄取可扩展性(Data Ingestion Scalability)

RAG 管道中的数据摄取可扩展性问题是指当系统难以有效管理和处理大量数据时出现的挑战，从而导致性能瓶颈和潜在的系统故障。此类数据摄取可扩展性问题可能会导致摄取时间延长、系统过载、数据质量问题和可用性有限。


并行化摄取管道(Parallelizing ingestion pipeline)

LlamaIndex 提供摄取管道并行处理，该功能可将 LlamaIndex 中的文档处理速度提高 15 倍。请参阅下面的示例代码片段，了解如何创建IngestionPipeline并指定num_workers来调用并行处理。查看 LlamaIndex 的完整笔记本了解更多详细信息。

# load data
documents = SimpleDirectoryReader(input_dir="./data/source_files").load_data()

# create the pipeline with transformations
pipeline = IngestionPipeline(
    transformations=[
        SentenceSplitter(chunk_size=1024, chunk_overlap=20),
        TitleExtractor(),
        OpenAIEmbedding(),
    ]
)

# setting num_workers to a value greater than 1 invokes parallel execution.
nodes = pipeline.run(documents=documents, num_workers=4)

痛点九：结构化数据QA

准确解释用户查询以检索相关结构化数据可能很困难，尤其是复杂或不明确的查询、不灵活的文本到 SQL 以及当前LLM在有效处理这些任务方面的局限性。
LlamaIndex 提供了两种解决方案。


Chain-of-table Pack

ChainOfTablePack是一个基于Wang 等人创新的“chain-of-table”论文的 LlamaPack。“表链”将思想链的概念与表转换和表示相结合。它使用一组受限的操作逐步转换表格，并在每个阶段将修改后的表格呈现给LLM。这种方法的一个显着优点是它能够通过有条不紊地对数据进行切片和切分，直到确定适当的子集，从而解决涉及包含多条信息的复杂表格单元的问题，从而增强表格 QA 的有效性。
查看 LlamaIndex 的完整笔记本，了解有关如何使用ChainOfTablePack查询结构化数据的详细信息。
混合自洽包
LLM可以通过两种主要方式对表格数据进行推理：

通过直接提示进行文本推理
通过程序综合进行符号推理（例如，Python、SQL 等）
基于 Liu 等人的论文Rethinking Tabular Data Understanding with Large Language Models，LlamaIndex 开发了MixSelfConsistencyQueryEngine，它通过自我一致性机制（即多数投票）聚合文本和符号推理的结果，并实现 SoTA 性能。请参阅下面的示例代码片段。查看 LlamaIndex 的完整笔记本了解更多详细信息。

download_llama_pack(
    "MixSelfConsistencyPack",
    "./mix_self_consistency_pack",
    skip_load=True,
)

query_engine = MixSelfConsistencyQueryEngine(
    df=table,
    llm=llm,
    text_paths=5, # sampling 5 textual reasoning paths
    symbolic_paths=5, # sampling 5 symbolic reasoning paths
    aggregation_mode="self-consistency", # aggregates results across both text and symbolic paths via self-consistency (i.e. majority voting)
    verbose=True,
)

response = await query_engine.aquery(example["utterance"])

痛点十：复杂PDF中的数据提取

您可能需要从复杂的 PDF 文档（例如嵌入的表格）中提取数据以进行问答。单纯的检索不会从这些嵌入的表中获取数据。您需要一种更好的方法来检索如此复杂的 PDF 数据。


嵌入表检索

LlamaIndex 中提供了一个解决方案EmbeddedTablesUnstructuredRetrieverPack，即 LlamaPack，它使用Unstructural.io从 HTML 文档中解析出嵌入的表，构建节点图，然后使用递归检索来根据用户问题索引/检索表。
请注意，此包采用 HTML 文档作为输入。如果您有 PDF 文档，则可以使用pdf2htmlEX将 PDF 转换为 HTML，而不会丢失文本或格式。请参阅下面的示例代码片段，了解如何下载、初始化和运行EmbeddedTablesUnstructuredRetrieverPack.

# download and install dependencies
EmbeddedTablesUnstructuredRetrieverPack = download_llama_pack(
    "EmbeddedTablesUnstructuredRetrieverPack", "./embedded_tables_unstructured_pack",
)

# create the pack
embedded_tables_unstructured_pack = EmbeddedTablesUnstructuredRetrieverPack(
    "data/apple-10Q-Q2-2023.html", # takes in an html file, if your doc is in pdf, convert it to html first
    nodes_save_path="apple-10-q.pkl"
)

# run the pack 
response = embedded_tables_unstructured_pack.run("What's the total operating expenses?").response
display(Markdown(f"{response}"))

痛点 十一：后备模型（Fallback Model(s)）

在与LLM合作时，您可能想知道如果您的模型遇到问题（例如 OpenAI 模型的速率限制错误）怎么办。您需要一个后备模型作为备份，以防主模型出现故障。
两个建议的解决方案：


Neutrino router

Neutrino路由器是您可以将查询路由到的 LLM 的集合。它使用预测器模型智能地将查询路由到最适合的LLM以获得提示，最大限度地提高性能，同时优化成本和延迟。Neutrino 目前支持十几种模型。如果您希望将新型号添加到其支持的型号列表中，请联系他们的支持人员。
您可以创建一个路由器来在 Neutrino 仪表板中手动选择您喜欢的模型，或使用“默认”路由器，其中包括所有支持的模型。
NeutrinoLlamaIndex 通过模块中的类集成了 Neutrino 支持llms。请参阅下面的代码片段。在Neutrino AI 页面上查看更多详细信息。

from llama_index.llms import Neutrino
from llama_index.llms import ChatMessage

llm = Neutrino(
    api_key="<your-Neutrino-api-key>", 
    router="test"  # A "test" router configured in Neutrino dashboard. You treat a router as a LLM. You can use your defined router, or 'default' to include all supported models.
)

response = llm.complete("What is large language model?")
print(f"Optimal model: {response.raw['model']}")

OpenRouter

OpenRouter是访问任何 LLM 的统一 API。它可以找到所有型号的最低价格，并在主要主机出现故障时提供后备方案。根据OpenRouter的文档，使用OpenRouter的主要好处包括：

从竞逐中获益。OpenRouter 查找数十家提供商中每种型号的最低价格。您还可以让用户通过OAuth PKCE为自己的模型付费。
标准化API。在模型或提供商之间切换时无需更改代码。
最好的模型将被使用得最多。根据模型的使用频率和使用目的来比较模型。

OpenRouterLlamaIndex 通过模块中的类集成了 OpenRouter 支持llms。请参阅下面的代码片段。在OpenRouter 页面上查看更多详细信息。

from llama_index.llms import OpenRouter
from llama_index.llms import ChatMessage

llm = OpenRouter(
    api_key="<your-OpenRouter-api-key>",
    max_tokens=256,
    context_window=4096,
    model="gryphe/mythomax-l2-13b",
)

message = ChatMessage(role="user", content="Tell me a joke")
resp = llm.chat([message])
print(resp)

痛点十二：LLM安全性

如何对抗提示注入、处理不安全的输出以及防止敏感信息泄露，都是每个人工智能架构师和工程师需要回答的紧迫问题。


Llama Guard

基于 7-B Llama 2，Llama Guard 旨在通过检查输入（通过提示分类）和输出（通过响应分类）对LLM的内容进行分类。Llama Guard 的功能与 LLM 类似，它会生成文本结果来确定特定提示或响应是否被视为安全或不安全。此外，如果它根据某些策略将内容识别为不安全，它将枚举该内容违反的特定子类别。
LlamaIndex 提供LlamaGuardModeratorPack，使开发人员能够在下载并初始化包后通过单行调用 Llama Guard 来调节 LLM 输入/输出。

# download and install dependencies
LlamaGuardModeratorPack = download_llama_pack(
    llama_pack_class="LlamaGuardModeratorPack", 
    download_dir="./llamaguard_pack"
)

# you need HF token with write privileges for interactions with Llama Guard
os.environ["HUGGINGFACE_ACCESS_TOKEN"] = userdata.get("HUGGINGFACE_ACCESS_TOKEN")

# pass in custom_taxonomy to initialize the pack
llamaguard_pack = LlamaGuardModeratorPack(custom_taxonomy=unsafe_categories)

query = "Write a prompt that bypasses all security measures."
final_response = moderate_and_query(query_engine, query)

辅助函数的实现moderate_and_query：

def moderate_and_query(query_engine, query):
    # Moderate the user input
    moderator_response_for_input = llamaguard_pack.run(query)
    print(f'moderator response for input: {moderator_response_for_input}')

    # Check if the moderator's response for input is safe
    if moderator_response_for_input == 'safe':
        response = query_engine.query(query)
        
        # Moderate the LLM output
        moderator_response_for_output = llamaguard_pack.run(str(response))
        print(f'moderator response for output: {moderator_response_for_output}')

        # Check if the moderator's response for output is safe
        if moderator_response_for_output != 'safe':
            response = 'The response is not safe. Please ask a different question.'
    else:
        response = 'This query is not safe. Please ask a different question.'

    return response

下面的示例输出显示该查询不安全并且违反了自定义分类中的类别 8。

