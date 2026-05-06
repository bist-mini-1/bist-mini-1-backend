package com.bist.mini.post.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachments {

    private Long attachment_id;
    private Long post_id;
    private String original_name;
    private Long file_size;
    private String file_type;
    private String upload_type;
    private String file_data;
    private Long download_count;
    private String is_deleted;
    private LocalDateTime created_at;
    private LocalDateTime deleted_at;

}
