package ordersystem.ordersystem.member.domain;

import jakarta.persistence.*;
import lombok.*;
import ordersystem.ordersystem.common.domain.BaseTimeEntity;
import ordersystem.ordersystem.member.dto.MemberResDto;
import ordersystem.ordersystem.ordering.domain.Ordering;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString

public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true,nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @OneToMany(mappedBy = "member")
    private List<Ordering> orderingList;

    public MemberResDto fromEntity(){
        return MemberResDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .orderCount(this.orderingList.size())
                .build();
    }
}
