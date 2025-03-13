package ordersystem.ordersystem.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.member.domain.Role;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberSaveReqDto {

    private String name;
    private String email;
    private String password;
    private Role role = Role.USER;

    public Member toEntity(String encodedPassword){
        return Member.builder().name(this.name).email(this.email).password(encodedPassword).build();
    }
}
