package org.tool.csvtodb.edit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tool.csvtodb.common.dto.MemberInfoDto;

import jakarta.inject.Inject;

@Component // (1)
@Scope("step") // (2)
public class CsvtofileTasklet implements Tasklet{

    private static final String TARGET_STATUS = "1"; // (3)

    private static final String INITIAL_STATUS = "0"; // (4)

    private static final String GOLD_MEMBER = "G"; // (5)

    private static final String NORMAL_MEMBER = "N"; // (6)

    private static final int MAX_POINT = 1000000; // (7)

    private static final int CHUNK_SIZE = 10; //定数として、まとめて処理する単位(一定件数):10を定義する

    @Inject // (9)
    ItemStreamReader<MemberInfoDto> reader; //サブインタフェース
   
    @Inject // (9)
    ItemStreamWriter<MemberInfoDto> writer; // サブインタフェース

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { // (12)
        MemberInfoDto item = null;

        List<MemberInfoDto> items = new ArrayList<>(CHUNK_SIZE); // 一定件数分のitemを格納するためのリストを定義
        try {

            reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext()); // 入出力リソースをオープンす
            writer.open(chunkContext.getStepContext().getStepExecution().getExecutionContext()); // 入出力リソースをオープンす

            while ((item = reader.read()) != null) { // 入力リソース全件を逐次ループ処理する

                if (TARGET_STATUS.equals(item.getStatus())) {
                    if (GOLD_MEMBER.equals(item.getType())) {
                        item.setPoint(item.getPoint() + 100);
                    } else if (NORMAL_MEMBER.equals(item.getType())) {
                        item.setPoint(item.getPoint() + 10);
                    }

                    if (item.getPoint() > MAX_POINT) {
                        item.setPoint(MAX_POINT);
                    }

                    item.setStatus(INITIAL_STATUS);
                }

                items.add(item);

                if (items.size() == CHUNK_SIZE) { // リストに追加したitemの数が一定件数に達したかどうかを判定す
                    writer.write(new Chunk(items)); //処理したデータをファイルへ出力する。
                    items.clear();
                }
            }

            writer.write(new Chunk(items)); // 全体の処理件数/一定件数の余り分をファイルへ出力
        } finally {
            try {
                reader.close(); // (19)
            } catch (ItemStreamException e) {
                // do nothing.
            }
            try {
                writer.close(); // (19)
            } catch (ItemStreamException e) {
                // do nothing.
            }
        }

        return RepeatStatus.FINISHED; // (20)
    }
	
}
