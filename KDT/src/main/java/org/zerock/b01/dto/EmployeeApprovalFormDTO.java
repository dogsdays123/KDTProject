package org.zerock.b01.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeApprovalFormDTO {
    private List<UserByDTO> users;
}
