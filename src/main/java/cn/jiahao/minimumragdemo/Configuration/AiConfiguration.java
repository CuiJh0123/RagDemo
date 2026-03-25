package cn.jiahao.minimumragdemo.Configuration;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Configuration
public class AiConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AiConfiguration.class);
    @Resource
    private OpenAiChatModel model;
    @Resource
    private EmbeddingModel embeddingModel;
    @Bean
    public EmbeddingStore<TextSegment> store(){
        //从resource/rag下读取所有的md文件
        log.info("开始加载classpath/rag目录下文档：");
        List<Document> documentList = ClassPathDocumentLoader.loadDocuments("rag");
        log.info("加载到文档数量: {}",documentList.size());
        //构建一个内存向量数据库的操作对象
        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
        //把这些md文件切割成片段再写入内存向量数据库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(DocumentSplitters.recursive(300,50))//引入recusive切割器
                .build();
        //分割并写入内存向量数据库中
        log.info("构建EmbeddingStoreIngestor对象完成");
        ingestor.ingest(documentList);
        log.info("写入内存向量数据库完成");
        return store;
    }

    @Bean("ragContentRetriever")
    public ContentRetriever ragContentRetriever(EmbeddingStore<TextSegment> store){
        ContentRetriever delegate = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .minScore(0.5)
                .maxResults(5)
                .build();

        return query -> {
            List<Content> contents = delegate.retrieve(query);
            log.info("============ RAG 检索开始 ============");
            log.info("用户问题:{}",query.text());
            log.info("命中片段数量:{}",contents.size());
            for (int i = 0; i < contents.size(); i++) {
                Content content = contents.get(i);
                log.info("Top{} 命中片段: {}", i + 1, content.textSegment().text());
            }
            log.info("============ RAG 检索结束 ============");
            return contents;
        };
    }
}
