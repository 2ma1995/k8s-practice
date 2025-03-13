package ordersystem.ordersystem.member.service;

import jakarta.persistence.EntityNotFoundException;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.member.dto.LoginDto;
import ordersystem.ordersystem.member.dto.MemberResDto;
import ordersystem.ordersystem.member.dto.MemberSaveReqDto;
import ordersystem.ordersystem.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }
//  선생님 코드 예외처리 확인 기존 create랑 같은 코드
//    public Long save(MemberSaveReqDto memberSaveReqDto) {
//        Optional<Member> optionalMember =  memberRepository.findByEmail(memberSaveReqDto.getEmail());
//        if(optionalMember.isPresent()){
//            throw new IllegalArgumentException("기존에 존재하는 회원입니다.");
//        }
//        String password = passwordEncoder.encode(memberSaveReqDto.getPassword());
//        Member member = memberRepository.save(memberSaveReqDto.toEntity(password));
//        return  member.getId();
//    }

    public Long create(MemberSaveReqDto dto) throws IllegalArgumentException {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("중복 이메일입니다.");
        }
        Member member = memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        return member.getId();
    }

    public List<MemberResDto> findAll() {
        List<Member> members  = memberRepository.findAll();
        return members.stream().map(a -> a.fromEntity()).collect(Collectors.toUnmodifiableList());
    }

    public MemberResDto myInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
        return member.fromEntity();
    }


    public Member login(LoginDto dto){
//        email존재여부
//        boolean check = true;
//        Optional<Member> optionalMember = memberRepository.findByEmail(dto.getEmail());
//        if(!optionalMember.isPresent()){
//            check = false;
//        }
////        password일치 여부
//        if(!passwordEncoder.matches(dto.getPassword(), optionalMember.get().getPassword())){
//            check =false;
//        }
//        if(!check){
//            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다.");
//        }
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 이메일입니다."));
        if(!passwordEncoder.matches(dto.getPassword(),member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

}
