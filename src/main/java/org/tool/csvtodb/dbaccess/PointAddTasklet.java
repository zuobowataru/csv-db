package org.tool.csvtodb.dbaccess;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.tool.csvtodb.common.dto.MemberInfoDto;

import jakarta.inject.Inject;

@Component // (1)
public class PointAddTasklet implements Tasklet{
    private static final String TARGET_STATUS = "1"; // (2)

    private static final String INITIAL_STATUS = "0"; // (3)

    private static final String GOLD_MEMBER = "G"; // (4)

    private static final String NORMAL_MEMBER = "N"; // (5)

    private static final int MAX_POINT = 1000000; // (6)

    private static final int CHUNK_SIZE = 10; // (7)

    @Inject // (8)
    ItemStreamReader<MemberInfoDto> reader; // (9)

    @Inject // (8)
    ItemWriter<MemberInfoDto> writer; // (10)

    @Inject // (1)
    Validator<MemberInfoDto> validator; // (2)
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { // ビジネスロジックを実装
        MemberInfoDto item = null;

        List<MemberInfoDto> items = new ArrayList<>(CHUNK_SIZE); // リストを定義

        try {
            reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext()); // 入力リソースをオープンする。 このタイミングでSQL発行。

            while ((item = reader.read()) != null) { // 入力リソース全件を逐次ループ
            	validator.validate(item); // ItemReaderを通じて取得するDTOを引数としてValidator#validate()を実行する
                if (TARGET_STATUS.equals(item.getStatus())) {
                    if (GOLD_MEMBER.equals(item.getType())) {
                        item.setPoint(item.getPoint() + 100);
                    } else if (NORMAL_MEMBER.equals(item.getType())) {
                        item.setPoint(item.getPoint() + 10);
                    }

                    if (item.getPoint() > MAX_POINT) {
                        item.setPoint(MAX_POINT);
                    }

                   // item.setStatus(INITIAL_STATUS);
                }

                items.add(item);

                if (items.size() == CHUNK_SIZE) { // リストに追加したitemの数が一定件数に達したかどうかを判定
                    writer.write(new Chunk(items)); // データベースへ出力する。コミットはここではない
                    items.clear();
                }
            }

            writer.write(new Chunk(items)); // (17)
        } finally {
            reader.close(); // リソースをクローズする。ここで例外が発生した場合、タスクレット全体のトランザクションがロールバックされる。
        }

        return RepeatStatus.FINISHED; // (19)
    }

}
