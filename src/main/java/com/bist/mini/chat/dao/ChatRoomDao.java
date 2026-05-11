package com.bist.mini.chat.dao;

import com.bist.mini.chat.entity.ChatRoom;
import com.bist.mini.chat.entity.ChatRoomMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 채팅방 관리 데이터 접근 객체
 */
@Mapper
public interface ChatRoomDao {
    // 채팅방 생성
    void insertRoom(ChatRoom chatRoom);

    // 채팅방 멤버 추가
    void insertRoomMember(ChatRoomMember chatRoomMember);

    // 채팅방 상세 조회
    ChatRoom findRoomById(Long roomId);

    // 회원이 참여 중인 채팅방 목록 조회
    List<ChatRoom> findRoomsByMemberId(Long memberId);

    // 채팅방 멤버 목록 조회
    List<ChatRoomMember> findMembersByRoomId(Long roomId);

    // 1:1 채팅방 존재 여부 확인
    Long findPersonalRoom(@Param("memberId1") Long memberId1, @Param("memberId2") Long memberId2);

    // 마지막 읽은 시간 업데이트
    int updateLastReadAt(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
