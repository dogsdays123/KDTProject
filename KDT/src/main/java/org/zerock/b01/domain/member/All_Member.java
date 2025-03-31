package org.zerock.b01.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roleSet")
public class All_Member extends BaseEntity {

    @Id
    private String allId;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String aPsw;

    private String aPhone;

    @Column(length = 20, nullable = false)
    private String memberType;

    private boolean del;
    private boolean aSocial;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<MemberRole> roleSet = new HashSet<>();

    public void changeName(String name) {this.name = name;}
    public void changeEmail(String email) {this.email = email;}

    public void changeAPsw(String aPsw) {this.aPsw = aPsw;}
    public void changeAPhone(String aPhone) {this.aPhone = aPhone;}
    public void changeMemberType(String memberType) {this.memberType = memberType;}

    public void modifyMember(String aPsw, String aPhone, String memberType) {
        this.aPsw = aPsw;
        this.aPhone = aPhone;
        this.memberType = memberType;
        //test
    }

    public void addRole(MemberRole memberRole){
        this.roleSet.add(memberRole);
    }
    public void clearRoles(){
        this.roleSet.clear();
    }
}
