package org.tool.csvtodb.edit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tool.csvtodb.common.dto.MemberInfoDto;

import jakarta.inject.Inject;

@Component // (1)
@Scope("step") // (2)
public class CsvtofileTasklet implements Tasklet{

    private static final int CHUNK_SIZE = 10; //定数として、まとめて処理する単位(一定件数):10を定義する

    @Inject // (9)
    ItemStreamReader<MemberInfoDto> reader; //CSVから読み込み
   
    @Inject // (9)
    ItemWriter<MemberInfoDto> writer; // DB用

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception { // (12)
        MemberInfoDto item = null;

        List<MemberInfoDto> items = new ArrayList<>(CHUNK_SIZE); // 一定件数分のitemを格納するためのリストを定義
        try {

        	// CSVのデータを開く
            reader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext()); // 入出力リソースをオープンす

            while ((item = reader.read()) != null) { // 入力リソース全件を逐次ループ処理する

                //　CSVの入力値をセット
                items.add(item);

                if (items.size() == CHUNK_SIZE) { // リストに追加したitemの数が一定件数に達したかどうかを判定す
                    writer.write(new Chunk(items)); //処理したデータをファイルへ出力する。
                    items.clear();
                }
            }

            writer.write(new Chunk(items)); // 全体の処理件数/一定件数の余り分をファイルへ出力
        } finally {
            reader.close(); // リソースをクローズする。ここで例外が発生した場合、タスクレット全体のトランザクションがロールバックされる。
        }

        return RepeatStatus.FINISHED; // (20)
    }
	
}
