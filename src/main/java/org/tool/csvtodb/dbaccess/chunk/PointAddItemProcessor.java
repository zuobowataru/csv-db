package org.tool.csvtodb.dbaccess.chunk;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.tool.csvtodb.common.dto.MemberInfoDto;

@Component // (1)
public class PointAddItemProcessor implements ItemProcessor<MemberInfoDto, MemberInfoDto> { // (2)

    private static final String TARGET_STATUS = "1"; // (3)

    private static final String INITIAL_STATUS = "0"; // (4)

    private static final String GOLD_MEMBER = "G"; // (5)

    private static final String NORMAL_MEMBER = "N"; // (6)

    private static final int MAX_POINT = 1000000; // (7)

    @Override
    public MemberInfoDto process(MemberInfoDto item) throws Exception { // (8) (9) (10)
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

        return item;
    }
}
