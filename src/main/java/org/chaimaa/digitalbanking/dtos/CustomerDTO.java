package org.chaimaa.digitalbanking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @NoArgsConstructor  @AllArgsConstructor
@Builder

public class CustomerDTO {
    private Long id;
    private String name ;
    private String email;

}
