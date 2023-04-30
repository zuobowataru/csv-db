package org.tool.csvtodb.common.repository;

import org.apache.ibatis.cursor.Cursor;
import org.tool.csvtodb.common.dto.MemberInfoDto;

public interface MemberInfoRepository {
	  Cursor<MemberInfoDto> cursor(); // member_infoテーブルからすべてのレコードを取得するためのメソッドを定義

	  int updatePointAndStatus(MemberInfoDto memberInfo); // (2)

	  int insertall(MemberInfoDto memberInfo); // original

	  int inserttest(MemberInfoDto memberInfo); // original2

}
