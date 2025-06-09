package com.spring.demo.api.request;

import com.spring.demo.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskChangeStatusRequest {
        private TaskStatus status;
}
